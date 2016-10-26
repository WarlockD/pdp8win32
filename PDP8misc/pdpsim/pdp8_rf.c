/* pdp8_rf.c: RF08 fixed head disk simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   The RF08 is a head-per-track disk.  It uses the three cycle data break
   facility.  To minimize overhead, the entire RF08 is buffered in memory.

   Two timing parameters are provided:

   rf_time	Interword timing.  If 0, treated as 1.
   rf_burst	Burst mode.  If 0, DMA occurs cycle by cycle; otherwise,
		DMA occurs in a burst.
*/

#include "pdp8_defs.h"
#include <math.h>

/* Constants */

#define RF_NUMWD	2048				/* words/track */
#define RF_NUMTR	128				/* tracks/disk */
#define RF_NUMDK	4				/* disks/controller */
#define RF_SIZE		(RF_NUMDK * RF_NUMTR * RF_NUMWD) /* words/drive */
#define RF_MWC		07750				/* word count */
#define RF_MMA		07751				/* mem address */
#define RF_WMASK	(RF_NUMWD - 1)			/* word mask */

/* Parameters in the unit descriptor */

#define FUNC		u4				/* function */
#define RF_READ		0				/* read */
#define RF_WRITE	1				/* write */

/* Status register */

#define RFS_PCA		04000				/* photocell status */
#define RFS_DRE		02000				/* data req enable */
#define RFS_WLS		01000				/* write lock status */
#define RFS_EIE		00400				/* error int enable */
#define RFS_PIE		00200				/* photocell int enb */
#define RFS_CIE		00100				/* done int enable */
#define RFS_MEX		00070				/* memory extension */
#define RFS_DRL		00004				/* data late error */
#define RFS_NXD		00002				/* non-existent disk */
#define RFS_PER		00001				/* parity error */
#define RFS_ERR		(RFS_WLS + RFS_DRL + RFS_NXD + RFS_PER)
#define RFS_V_MEX	3

#define MIN_TIME(x)	((x > 0)? (x): 1)
#define GET_MEX(x)	(((x) & RFS_MEX) << (12 - RFS_V_MEX))
#define GET_POS(x)	((int) fmod (sim_gtime() / ((double) (MIN_TIME(x))), \
			((double) RF_NUMWD)))

extern int int_req;
extern unsigned short M[];
int rf_stat = 0, rf_dar = 0;
int rf_done = 0, rf_wlk = 0;
int rf_time = 10, rf_burst = 1;
int rf_stopioe = 1;
int rf_svc (UNIT *uptr);
int pcell_svc (UNIT *uptr);
int rf_reset (DEVICE *dptr);
int rf_boot (int unitno);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);
extern int sim_is_active (UNIT *uptr);
extern double sim_gtime(void);

/* RF08 data structures

   rf_dev	RF device descriptor
   rf_unit	RF unit descriptor
   pcell_unit	photocell timing unit (orphan)
   rf_reg	RF register list
*/

UNIT rf_unit =
	{ UDATA (&rf_svc, UNIT_FIX+UNIT_ATTABLE+UNIT_BUFABLE+UNIT_MUSTBUF,
	RF_SIZE) };

UNIT pcell_unit = { UDATA (&pcell_svc, 0, 0) };

REG rf_reg[] = {
	{ ORDATA (STAT, rf_stat, 12) },
	{ ORDATA (DAR, rf_dar, 20) },
	{ ORDATA (WLK, rf_wlk, 32) },
	{ FLDATA (DONE, rf_done, 0) },
	{ FLDATA (INT, int_req, INT_V_RF) },
	{ ORDATA (WLK, rf_wlk, 32) },
	{ DRDATA (TIME, rf_time, 24), PV_LEFT },
	{ FLDATA (BURST, rf_burst, 0) },
	{ FLDATA (STOP_IOE, rf_stopioe, 0) },
	{ NULL }  };

DEVICE rf_dev = {
	"RF", &rf_unit, rf_reg, NULL,
	1, 8, 20, 1, 8, 12,
	NULL, NULL, &rf_reset,
	&rf_boot, NULL, NULL };

/* Fixed head disk routines

   rf6x		process IOTs
   rf_svc	process event
   rf_reset	process reset
*/

int rf60 (int pulse, int AC)
{
int t;

switch (pulse) {					/* decode IR<9:11> */
case 1:							/* DCMA */
	rf_dar = rf_dar & 03770000;			/* clear DAR<11:0> */
	return AC;
case 3:case 5:						/* DMAR, DMAW */
	rf_dar = (rf_dar & 03770000) | AC;		/* DAR<11:0> <- AC */
	rf_done = 0;					/* clear done */
	rf_stat = rf_stat & ~RFS_ERR;			/* clear errors */
	int_req = int_req & ~INT_RF;			/* clear int req */
	rf_unit.FUNC = (pulse >> 2) & 1;		/* save function */
	t = (rf_dar & RF_WMASK) - GET_POS (rf_time);	/* delta to new loc */
	if (t < 0) t = t + RF_NUMWD;			/* wrap around? */
	sim_activate (&rf_unit, t * rf_time);		/* schedule op */
	return 0;  }					/* clear AC */
return AC;						/* unimplemented */
}							/* end rf60 */

int rf61 (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 1:							/* DCIM */
	rf_stat = rf_stat & 07007;			/* clear STA<8:3> */
	sim_cancel (&pcell_unit);			/* cancel photocell */
	return AC;
case 2:							/* DSAC */
	return ((rf_dar & RF_WMASK) == GET_POS (rf_time))? IOT_SKP + AC: AC;
case 5:							/* DIML */
	rf_stat = (rf_stat & 07007) | (AC & 0770);	/* STA<8:3> <- AC */
	if (rf_stat & RFS_PIE)				/* photocell int? */
		sim_activate (&pcell_unit, (RF_NUMWD - GET_POS (rf_time)) *
			MIN_TIME (rf_time));
	else sim_cancel (&pcell_unit);
	return 0;					/* clear AC */
case 6:							/* DIMA */
	rf_stat = rf_stat & ~RFS_PCA;			/* clear photocell */
	if (GET_POS (rf_time) < 6) rf_stat = rf_stat | RFS_PCA;
	return rf_stat;	 }				/* AC <- STA<11:0> */
return AC;						/* unimplemented */
}							/* end rf61 */

int rf62 (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 1:							/* DFSE */
	return (rf_stat & RFS_ERR)? IOT_SKP + AC: AC;
case 2:							/* DFSC */
	return (rf_done)? IOT_SKP + AC: AC;
case 3:							/* DISK */
	return (rf_done || (rf_stat & RFS_ERR))? IOT_SKP + AC: AC;
case 6:							/* DMAC */
	return rf_dar & 07777;  }			/* AC <- DAR<11:0> */
return AC;						/* unimplemented */
}							/* end rf62 */

int rf64 (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 1:							/* DCXA */
	rf_dar = rf_dar & 07777;			/* clear DAR<19:12> */
	return AC;
case 3:							/* DXAL */
	rf_dar = (rf_dar & 07777) | ((AC & 0377) << 12); /* DAR<19:12> <- AC */
	return 0;					/* clear AC */
case 5:							/* DXAC */
	return ((rf_dar >> 12) & 0377);  }		/* AC <- DAR<19:12> */
return AC;						/* unimplemented */
}							/* end RF60 */

/* Disk unit service

   Note that for reads and writes, memory addresses wrap around in the
   current field.  This code assumes the entire disk is buffered.
*/

int rf_svc (UNIT *uptr)
{
int t, mex, pa;

if ((uptr -> flags & UNIT_BUF) == 0) {			/* not buf? abort */
	rf_stat = rf_stat | RFS_NXD;
	rf_done = 1;
	if (rf_stat & (RFS_EIE + RFS_CIE)) int_req = int_req | INT_RF;
	return IORETURN (rf_stopioe, SCPE_NOATT);  }

mex = GET_MEX (rf_stat);
do { 	M[RF_MMA] = (M[RF_MMA] + 1) & 07777;		/* incr mem addr */
	pa = mex | M[RF_MMA]; 				/* add extension */
	if (uptr -> FUNC == RF_READ) {
		if (MEM_ADDR_OK (pa))			/* read, check nxm */
			M[pa] = *(((short *) uptr -> filebuf) + rf_dar);  }
	else {	t = ((rf_dar >> 15) & 030) | ((rf_dar >> 14) & 07);
		if ((rf_wlk >> t) & 1) rf_stat = rf_stat | RFS_WLS;
		else {	*(((short *) uptr -> filebuf) + rf_dar) = M[pa];
			if (rf_dar >= uptr -> hwmark)
				uptr -> hwmark = rf_dar + 1;  }  }
	rf_dar = (rf_dar + 1) & 03777777;		/* incr disk addr */
	M[RF_MWC] = (M[RF_MWC] + 1) & 07777;  }		/* incr word count */
while ((M[RF_MWC] != 0) && (rf_burst != 0));		/* brk if wc, no brst */

if (M[RF_MWC] != 0)					/* more to do? */
	sim_activate (&rf_unit, MIN_TIME (rf_time));	/* sched next */
else {	rf_done = 1;					/* done */
	if (rf_stat & RFS_CIE) int_req = int_req | INT_RF;  }
return SCPE_OK;
}

/* Photocell unit service */

int pcell_svc (UNIT *uptr)
{
if (rf_stat & RFS_PIE) {				/* int enable? */
	sim_activate (&pcell_unit, RF_NUMWD * MIN_TIME (rf_time));
	int_req = int_req | INT_RF;  }
return SCPE_OK;
}

/* Device reset */

int rf_reset (DEVICE *dptr)
{
rf_stat = rf_dar = 0;
rf_done = 1;
int_req = int_req & ~INT_RF;				/* clear interrupt */
sim_cancel (&rf_unit);
sim_cancel (&pcell_unit);
return SCPE_OK;
}

/* Device bootstrap */

#define BOOT_START 7750
#define BOOT_LEN (sizeof (boot_rom) / sizeof (int))

static int boot_rom[] = {
	07600,			/* 7750, CLA CLL	; also word count */
	06603,			/* 7751, DMAR		; also address */
	06622,			/* 7752, DFSC		; done? */
	05352,			/* 7753, JMP .-1	; no */
	05752			/* 7754, JMP @.-2	; enter boot */
};

int rf_boot (int unitno)
{
int i;
extern int saved_PC;

for (i = 0; i < BOOT_LEN; i++) M[BOOT_START + i] = boot_rom[i];
saved_PC = BOOT_START;
return SCPE_OK;
}
