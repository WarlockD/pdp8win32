/* pdp8_rk.c: RK8E cartridge disk simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited
*/

#include "pdp8_defs.h"

/* Constants */

#define RK_NUMSC	16				/* sectors/surface */
#define RK_NUMSF	2				/* surfaces/cylinder */
#define RK_NUMCY	203				/* cylinders/drive */
#define RK_NUMWD	256				/* words/sector */
#define RK_SIZE	(RK_NUMCY * RK_NUMSF * RK_NUMSC * RK_NUMWD) /* words/drive */
#define RK_NUMDR	4				/* drives/controller */
#define RK_M_NUMDR	03

/* Flags in the unit flags word */

#define UNIT_V_ONLINE	(UNIT_V_UF)			/* present */
#define UNIT_V_HWLK	(UNIT_V_UF + 1)			/* hwre write lock */
#define UNIT_V_SWLK	(UNIT_V_UF + 2)			/* swre write lock */
#define UNIT_W_UF	3				/* user flags width */
#define UNIT_ONLINE	(1 << UNIT_V_ONLINE)
#define UNIT_HWLK	(1 << UNIT_V_HWLK)
#define UNIT_SWLK	(1 << UNIT_V_SWLK)

/* Parameters in the unit descriptor */

#define CYL		u3				/* current cylinder */
#define FUNC		u4				/* function */

/* Status register */

#define RKS_DONE	04000				/* transfer done */
#define RKS_HMOV	02000				/* heads moving */
#define RKS_SKFL	00400				/* drive seek fail */
#define RKS_NRDY	00200				/* drive not ready */
#define RKS_BUSY	00100				/* control busy error */
#define RKS_TMO		00040				/* timeout error */
#define RKS_WLK		00020				/* write lock error */
#define RKS_CRC		00010				/* CRC error */
#define RKS_DLT		00004				/* data late error */
#define RKS_STAT	00002				/* drive status error */
#define RKS_CYL		00001				/* cyl address error */
#define RKS_ERR	(RKS_BUSY+RKS_TMO+RKS_WLK+RKS_CRC+RKS_DLT+RKS_STAT+RKS_CYL)

/* Command register */

#define RKC_M_FUNC	07				/* function */
#define  RKC_READ	0
#define	 RKC_RALL	1
#define  RKC_WLK	2
#define  RKC_SEEK	3
#define  RKC_WRITE	4
#define  RKC_WALL	5
#define RKC_V_FUNC	9
#define RKC_IE		00400				/* interrupt enable */
#define RKC_SKDN	00200				/* int on seek done */
#define RKC_HALF	00100				/* 128W sector */
#define RKC_MEX		00070				/* memory extension */
#define RKC_V_MEX	3
#define RKC_M_DRV	03				/* drive select */
#define RKC_V_DRV	1
#define RKC_CYHI	00001				/* high cylinder addr */

#define GET_FUNC(x)	(((x) >> RKC_V_FUNC) & RKC_M_FUNC)
#define GET_DRIVE(x)	(((x) >> RKC_V_DRV) & RKC_M_DRV)
#define GET_MEX(x)	(((x) & RKC_MEX) << (12 - RKC_V_MEX))

/* Disk address */

#define RKD_V_SECT	0				/* sector */
#define RKD_M_SECT	017
#define RKD_V_SUR	4				/* surface */
#define RKD_M_SUR	01
#define RKD_V_CYL	5				/* cylinder */
#define RKD_M_CYL	0177
#define GET_CYL(x,y)	((((x) & RKC_CYHI) << (12-RKD_V_CYL)) | \
			 (((y) >> RKD_V_CYL) & RKD_M_CYL))
#define GET_DA(x,y)	((((x) & RKC_CYHI) << 12) | y)

/* Reset commands */

#define RKX_CLS		0				/* clear status */
#define RKX_CLC		1				/* clear control */
#define RKX_CLD		2				/* clear drive */
#define RKX_CLSA	3				/* clear status alt */

#define RK_INT_UPDATE \
	if (((rk_stat & (RKS_DONE + RKS_ERR)) != 0) && \
	    ((rk_cmd & RKC_IE) != 0)) int_req = int_req | INT_RK; \
	else int_req = int_req & ~INT_RK
#define RK_MIN 10
#define MAX(x,y) (((x) > (y))? (x): (y))

extern int int_req;
extern unsigned short M[];
int rk_busy = 0, rk_stat = 0, rk_cmd = 0;
int rk_dar = 0, rk_car = 0;
int rk_swait = 10, rk_rwait = 10;
int rk_stopioe = 1;
int rk_svc (UNIT *uptr);
int rk_reset (DEVICE *dptr);
int rk_boot (int unitno);
void rk_go (int function, int cylinder);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);
extern int sim_is_active (UNIT *uptr);

/* RK-8E data structures

   rk_dev	RK device descriptor
   rk_unit	RK unit list
   rk_reg	RK register list
   rk_mod	RK modifiers list
*/

UNIT rk_unit[] = {
	{ UDATA (&rk_svc, UNIT_FIX+UNIT_ATTABLE+UNIT_ONLINE, RK_SIZE) },
	{ UDATA (&rk_svc, UNIT_FIX+UNIT_ATTABLE+UNIT_ONLINE, RK_SIZE) },
	{ UDATA (&rk_svc, UNIT_FIX+UNIT_ATTABLE+UNIT_ONLINE, RK_SIZE) },
	{ UDATA (&rk_svc, UNIT_FIX+UNIT_ATTABLE+UNIT_ONLINE, RK_SIZE) } };

REG rk_reg[] = {
	{ ORDATA (STAT, rk_stat, 12) },
	{ ORDATA (CAR, rk_car, 12) },
	{ ORDATA (DAR, rk_dar, 12) },
	{ ORDATA (CMD, rk_cmd, 12) },
	{ FLDATA (BUSY, rk_busy, 0) },
	{ FLDATA (INT, int_req, INT_V_RK) },
	{ DRDATA (STIME, rk_swait, 24), PV_LEFT },
	{ DRDATA (RTIME, rk_rwait, 24), PV_LEFT },
	{ GRDATA (FLG0, rk_unit[0].flags, 8, UNIT_W_UF, UNIT_V_UF), REG_HRO },
	{ GRDATA (FLG1, rk_unit[1].flags, 8, UNIT_W_UF, UNIT_V_UF), REG_HRO },
	{ GRDATA (FLG2, rk_unit[2].flags, 8, UNIT_W_UF, UNIT_V_UF), REG_HRO },
	{ GRDATA (FLG3, rk_unit[3].flags, 8, UNIT_W_UF, UNIT_V_UF), REG_HRO },
	{ FLDATA (STOP_IOE, rk_stopioe, 0) },
	{ NULL }  };

MTAB rk_mod[] = {
	{ UNIT_ONLINE, 0, "offline", "OFFLINE", NULL },
	{ UNIT_ONLINE, UNIT_ONLINE, "online", "ONLINE", NULL },
	{ (UNIT_HWLK+UNIT_SWLK), 0, "write enabled", "ENABLED", NULL },
	{ (UNIT_HWLK+UNIT_SWLK), UNIT_HWLK, "write locked", "LOCKED", NULL },
	{ (UNIT_HWLK+UNIT_SWLK), UNIT_SWLK, "write locked", NULL, NULL },
	{ (UNIT_HWLK+UNIT_SWLK), (UNIT_HWLK+UNIT_SWLK), "write locked",
		NULL, NULL }, 
	{ 0 }  };

DEVICE rk_dev = {
	"RK", rk_unit, rk_reg, rk_mod,
	RK_NUMDR, 8, 24, 1, 8, 12,
	NULL, NULL, &rk_reset,
	&rk_boot, NULL, NULL };

/* Cartridge disk routines

   rk		process IOT
   rk_svc	process event
   rk_reset	process reset
*/

int rk (int pulse, int AC)
{
int i;
UNIT *uptr;

switch (pulse) {					/* decode IR<9:11> */
case 0:							/* unused */
	return AC;
case 1:							/* DSKP */
	return (rk_stat & (RKS_DONE + RKS_ERR))?	/* skip on done, err */
		IOT_SKP + AC: AC;
case 2:							/* DCLR */
	rk_stat = 0;					/* clear status */
	switch (AC & 03) {				/* decode AC<10:11> */
	case RKX_CLS:					/* clear status */
		if (rk_busy != 0) rk_stat = rk_stat | RKS_BUSY;
	case RKX_CLSA:					/* clear status alt */
		break;
	case RKX_CLC:					/* clear control */
		rk_cmd = rk_busy = 0;			/* clear registers */
		rk_car = rk_dar = 0;
		for (i = 0; i < RK_NUMDR; i++) {	/* cancel everything */
			uptr = rk_dev.units + i;
			sim_cancel (uptr);  }
		break;
	case RKX_CLD:					/* reset drive */
		if (rk_busy != 0) rk_stat = rk_stat | RKS_BUSY;
		else rk_go (RKC_SEEK, 0);		/* seek to 0 */
		break;  }				/* end switch AC */
	break;
case 3:							/* DLAG */
	if (rk_busy != 0) rk_stat = rk_stat | RKS_BUSY;
	else {	rk_dar = AC;				/* load disk addr */
		rk_go (GET_FUNC (rk_cmd), GET_CYL (rk_cmd, rk_dar));  }
	break;
case 4:							/* DLCA */
	if (rk_busy != 0) rk_stat = rk_stat | RKS_BUSY;
	else rk_car = AC;				/* load curr addr */
	break;
case 5:							/* DRST */
	uptr = rk_dev.units + GET_DRIVE (rk_cmd);	/* selected unit */
	rk_stat = rk_stat & ~(RKS_HMOV + RKS_NRDY);	/* clear dynamic */
	if ((uptr -> flags & UNIT_ATT) == 0) rk_stat = rk_stat | RKS_NRDY;
	if (sim_is_active (uptr)) rk_stat = rk_stat | RKS_HMOV;
	return rk_stat;
case 6:							/* DLDC */
	if (rk_busy != 0) rk_stat = rk_stat | RKS_BUSY;
	else {	rk_cmd = AC;				/* load command */
		rk_stat = 0;  }				/* clear status */
	break;
case 7:							/* DMAN */
	break;  }					/* end case pulse */
RK_INT_UPDATE;						/* update int req */
return 0;						/* clear AC */
}							/* end rk */

/* Initiate new function

   Called with function, cylinder, to allow recalibrate as well as
   load and go to be processed by this routine.

   Assumes that the controller is idle, and that updating of interrupt
   request will be done by the caller.
*/

void rk_go (int func, int cyl)
{
int t;
UNIT *uptr;

if (func == RKC_RALL) func = RKC_READ;			/* all? use standard */
if (func == RKC_WALL) func = RKC_WRITE;
uptr = rk_dev.units + GET_DRIVE (rk_cmd);		/* selected unit */
if (((uptr -> flags & UNIT_ONLINE) == 0) ||		/* not present or */
    ((uptr -> flags & UNIT_ATT) == 0)) {		/* not attached? */
	rk_stat = rk_stat | RKS_DONE | RKS_NRDY | RKS_STAT;
	return;  }
if (sim_is_active (uptr) || (cyl >= RK_NUMCY)) {	/* busy or bad cyl? */
	rk_stat = rk_stat | RKS_DONE | RKS_STAT;
	return;  }
if ((func == RKC_WRITE) && (uptr -> flags & (UNIT_HWLK + UNIT_SWLK))) {
	rk_stat = rk_stat | RKS_DONE | RKS_WLK;		/* write and locked? */
	return;  }
if (func == RKC_WLK) {					/* write lock? */
	uptr -> flags = uptr -> flags | UNIT_SWLK;
	rk_stat = rk_stat | RKS_DONE;
	return;  }
t = abs (cyl - uptr -> CYL) * rk_swait;			/* seek time */
if (func == RKC_SEEK) {					/* seek? */
	sim_activate (uptr, MAX (RK_MIN, t));		/* schedule */
	rk_stat = rk_stat | RKS_DONE;  }		/* set done */
else {	sim_activate (uptr, t + rk_rwait);		/* schedule */
	rk_busy = 1;  }					/* set busy */
uptr -> FUNC = func;					/* save func */
uptr -> CYL = cyl;					/* put on cylinder */
return;
}

/* Unit service

   If seek, complete seek command
   Else complete data transfer command

   Note that for reads and writes, memory addresses wrap around in the
   current field.

   The unit control block contains the function and cylinder address for
   the current command.
*/

static unsigned short fill[RK_NUMWD/2] = { 0 };
int rk_svc (UNIT *uptr)
{
int err, wc, wc1, awc, swc, pa, da;
UNIT *seluptr;

if (uptr -> FUNC == RKC_SEEK) {				/* seek? */
	seluptr = rk_dev.units + GET_DRIVE (rk_cmd);	/* see if selected */
	if ((uptr == seluptr) && ((rk_cmd & RKC_SKDN) != 0)) {
		rk_stat = rk_stat | RKS_DONE;
		RK_INT_UPDATE;  }
	return SCPE_OK;  }

pa = GET_MEX (rk_cmd) | rk_car;				/* phys address */
da = GET_DA (rk_cmd, rk_dar) * RK_NUMWD * sizeof (short);/* disk address */
swc = wc = (rk_cmd & RKC_HALF)? RK_NUMWD / 2: RK_NUMWD;	/* get transfer size */
if ((wc1 = ((rk_car + wc) - 010000)) > 0)		/* field wraparound? */
	wc = wc - wc1;					/* limit 1st xfer */
if ((uptr -> flags & UNIT_ATT) == 0) {			/* not att? abort */
	rk_stat = rk_stat | RKS_DONE | RKS_NRDY | RKS_STAT;
	rk_busy = 0;
	RK_INT_UPDATE;
	return IORETURN (rk_stopioe, SCPE_NOATT);  }

err = fseek (uptr -> fileref, da, SEEK_SET);		/* locate sector */

if ((uptr -> FUNC == RKC_READ) && (err == 0) && MEM_ADDR_OK (pa)) { /* read? */
	awc = fread (&M[pa], sizeof (short), wc, uptr -> fileref);
	for ( ; awc < wc; awc++) M[pa + awc] = 0; 	/* fill if eof */
	err = ferror (uptr -> fileref);
	if ((wc1 > 0) && (err == 0))  {			/* field wraparound? */
		pa = pa & 070000;			/* wrap phys addr */
		awc = fread (&M[pa], sizeof (short), wc1, uptr -> fileref);
		for ( ; awc < wc1; awc++) M[pa + awc] = 0; /* fill if eof */
		err = ferror (uptr -> fileref);  }  }

if ((uptr -> FUNC == RKC_WRITE) && (err == 0)) {	/* write? */
	fwrite (&M[pa], sizeof (short), wc, uptr -> fileref);
	err = ferror (uptr -> fileref);
	if ((wc1 > 0) && (err == 0)) {			/* field wraparound? */
		pa = pa & 070000;			/* wrap phys addr */
		fwrite (&M[pa], sizeof (short), wc1, uptr -> fileref);
		err = ferror (uptr -> fileref);  }
	if ((rk_cmd & RKC_HALF) && (err == 0)) {	/* fill half sector */
		fwrite (fill, sizeof (short), RK_NUMWD/2, uptr -> fileref);
		err = ferror (uptr -> fileref);  }  }

rk_car = (rk_car + swc) & 07777;			/* incr mem addr reg */
rk_stat = rk_stat | RKS_DONE;				/* set done */
rk_busy = 0;
RK_INT_UPDATE;

if (err != 0) {
	perror ("RK I/O error");
	clearerr (uptr -> fileref);
	return SCPE_IOERR;  }
return SCPE_OK;
}

/* Device reset */

int rk_reset (DEVICE *dptr)
{
int i;
UNIT *uptr;

rk_cmd = rk_car = rk_dar = rk_stat = rk_busy = 0;
int_req = int_req & ~INT_RK;				/* clear interrupt */
for (i = 0; i < RK_NUMDR; i++) {			/* stop all units */
	uptr = rk_dev.units + i;
	sim_cancel (uptr);
	uptr -> flags = uptr -> flags & ~UNIT_SWLK;
	uptr -> CYL = uptr -> FUNC = 0;  }
return SCPE_OK;
}

/* Device bootstrap */

#define BOOT_START 023
#define BOOT_UNIT 032
#define BOOT_LEN (sizeof (boot_rom) / sizeof (int))

static int boot_rom[] = {
	06007,			/* 23, CAF */
	06744,			/* 24, DLCA		; addr = 0 */
	01032,			/* 25, TAD UNIT		; unit no */
	06746,			/* 26, DLDC		; command, unit */
	06743,			/* 27, DLAG		; disk addr, go */
	01032,			/* 30, TAD UNIT		; unit no, for OS */
	05031,			/* 31, JMP . */
	00000			/* UNIT, 0 		; in bits <9:10> */
};

int rk_boot (int unitno)
{
int i;
extern int saved_PC;

for (i = 0; i < BOOT_LEN; i++) M[BOOT_START + i] = boot_rom[i];
M[BOOT_UNIT] = (unitno & RK_M_NUMDR) << 1;
saved_PC = BOOT_START;
return SCPE_OK;
}
