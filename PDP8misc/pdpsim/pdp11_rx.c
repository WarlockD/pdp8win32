/* pdp11_rx.c: RX11/RX01 floppy disk simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   rx		RX11 disk controller

   An RX01 diskette consists of 77 tracks, each with 26 sectors of 128B.
   Tracks are numbered 0-76, sectors 1-26.
*/

#include "pdp11_defs.h"

#define RX_NUMTR	77				/* tracks/disk */
#define RX_M_TRACK	0377
#define RX_NUMSC	26				/* sectors/track */
#define RX_M_SECTOR	0377
#define RX_NUMBY	128				/* bytes/sector */
#define RX_SIZE		RX_NUMTR * RX_NUMSC * RX_NUMBY	/* bytes/disk */
#define RX_NUMDR	2				/* drives/controller */
#define RX_M_NUMDR	01
#define UNIT_V_WLK	(UNIT_V_UF)			/* write locked */
#define UNIT_WLK	(1 << UNIT_V_UF)

#define IDLE		0				/* idle state */
#define RWDS		1				/* rw, sect next */
#define RWDT		2				/* rw, track next */
#define FILL		3				/* fill buffer */
#define EMPTY		4				/* empty buffer */
#define CMD_COMPLETE	5				/* set done next */
#define INIT_COMPLETE	6				/* init compl next */

#define RXCS_V_FUNC	1				/* function */
#define RXCS_M_FUNC	7
#define  RXCS_FILL	0				/* fill buffer */
#define  RXCS_EMPTY	1				/* empty buffer */
#define  RXCS_WRITE	2				/* write sector */
#define  RXCS_READ	3				/* read sector */
#define  RXCS_RXES	5				/* read status */
#define  RXCS_WRDEL	6				/* write del data */
#define  RXCS_ECODE	7				/* read error code */
#define RXCS_V_DRV	4				/* drive select */
#define RXCS_V_DONE	5				/* done */
#define RXCS_V_TR	7				/* xfer request */
#define RXCS_V_INIT	14				/* init */
#define RXCS_FUNC	(RXCS_M_FUNC << RXCS_V_FUNC)
#define RXCS_DRV	(1 << RXCS_V_DRV)
#define RXCS_DONE	(1 << RXCS_V_DONE)
#define RXCS_TR		(1 << RXCS_V_TR)
#define RXCS_INIT	(1 << RXCS_V_INIT)
#define RXCS_ROUT	(CSR_ERR+RXCS_TR+CSR_IE+RXCS_DONE)
#define RXCS_IMP	(RXCS_ROUT+RXCS_DRV+RXCS_FUNC)
#define RXCS_RW		(CSR_IE)			/* read/write */

#define RXES_CRC	0001				/* CRC error */
#define RXES_PAR	0002				/* parity error */
#define RXES_ID		0004				/* init done */
#define RXES_WLK	0010				/* write protect */
#define RXES_DD		0100				/* deleted data */
#define RXES_DRDY	0200				/* drive ready */

#define TRACK u3					/* current track */
#define CALC_DA(t,s) (((t) * RX_NUMSC) + ((s) - 1)) * RX_NUMBY

extern int int_req;
int rx_csr = 0, rx_dbr = 0;
int rx_esr = 0, rx_ecode = 0;
int rx_track = 0, rx_sector = 0;
int rx_state = IDLE, rx_stopioe = 1;
int rx_cwait = 100;					/* command time */
int rx_swait = 10;					/* seek, per track */
int rx_xwait = 1;					/* tr set time */
unsigned char buf[RX_NUMBY] = { 0 };
int bptr = 0;
int rx_svc (UNIT *uptr);
int rx_reset (DEVICE *dptr);
int rx_boot (int unitno);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);

/* RX11 data structures

   rx_dev	RX device descriptor
   rx_unit	RX unit list
   rx_reg	RX register list
   rx_mod	RX modifier list
*/

UNIT rx_unit[] = {
	{ UDATA (&rx_svc,
	  UNIT_FIX+UNIT_ATTABLE+UNIT_BUFABLE+UNIT_MUSTBUF, RX_SIZE) },
	{ UDATA (&rx_svc,
	  UNIT_FIX+UNIT_ATTABLE+UNIT_BUFABLE+UNIT_MUSTBUF, RX_SIZE) } };

REG rx_reg[] = {
	{ ORDATA (RXCS, rx_csr, 16) },
	{ ORDATA (RXDB, rx_dbr, 8) },
	{ ORDATA (RXES, rx_esr, 8) },
	{ ORDATA (RXERR, rx_ecode, 8) },
	{ ORDATA (RXTA, rx_track, 8) },
	{ ORDATA (RXSA, rx_sector, 8) },
	{ ORDATA (STAPTR, rx_state, 3), REG_RO },
	{ ORDATA (BUFPTR, bptr, 7)  },
	{ FLDATA (INT, int_req, INT_V_RX) },
	{ FLDATA (ERR, rx_csr, CSR_V_ERR) },
	{ FLDATA (TR, rx_csr, RXCS_V_TR) },
	{ FLDATA (IE, rx_csr, CSR_V_IE) },
	{ FLDATA (DONE, rx_csr, RXCS_V_DONE) },
	{ DRDATA (CTIME, rx_cwait, 24), PV_LEFT },
	{ DRDATA (STIME, rx_swait, 24), PV_LEFT },
	{ DRDATA (XTIME, rx_xwait, 24), PV_LEFT },
	{ FLDATA (FLG0, rx_unit[0].flags, UNIT_V_WLK), REG_HRO },
	{ FLDATA (FLG1, rx_unit[1].flags, UNIT_V_WLK), REG_HRO },
	{ FLDATA (STOP_IOE, rx_stopioe, 0) },
	{ ORDATA (BUF0, buf[0], 8), REG_HIDDEN },
	{ ORDATA (BUF1, buf[1], 8), REG_HIDDEN },
	{ ORDATA (BUF2, buf[2], 8), REG_HIDDEN },
	{ ORDATA (BUF3, buf[3], 8), REG_HIDDEN },
	{ ORDATA (BUF4, buf[4], 8), REG_HIDDEN },
	{ ORDATA (BUF5, buf[5], 8), REG_HIDDEN },
	{ ORDATA (BUF6, buf[6], 8), REG_HIDDEN },
	{ ORDATA (BUF7, buf[7], 8), REG_HIDDEN },
	{ ORDATA (BUF8, buf[8], 8), REG_HIDDEN },
	{ ORDATA (BUF9, buf[9], 8), REG_HIDDEN },
	{ ORDATA (BUF10, buf[10], 8), REG_HIDDEN },
	{ ORDATA (BUF11, buf[11], 8), REG_HIDDEN },
	{ ORDATA (BUF12, buf[12], 8), REG_HIDDEN },
	{ ORDATA (BUF13, buf[13], 8), REG_HIDDEN },
	{ ORDATA (BUF14, buf[14], 8), REG_HIDDEN },
	{ ORDATA (BUF15, buf[15], 8), REG_HIDDEN },
	{ ORDATA (BUF16, buf[16], 8), REG_HIDDEN },
	{ ORDATA (BUF17, buf[17], 8), REG_HIDDEN },
	{ ORDATA (BUF18, buf[18], 8), REG_HIDDEN },
	{ ORDATA (BUF19, buf[19], 8), REG_HIDDEN },
	{ ORDATA (BUF20, buf[20], 8), REG_HIDDEN },
	{ ORDATA (BUF21, buf[21], 8), REG_HIDDEN },
	{ ORDATA (BUF22, buf[22], 8), REG_HIDDEN },
	{ ORDATA (BUF23, buf[23], 8), REG_HIDDEN },
	{ ORDATA (BUF24, buf[24], 8), REG_HIDDEN },
	{ ORDATA (BUF25, buf[25], 8), REG_HIDDEN },
	{ ORDATA (BUF26, buf[26], 8), REG_HIDDEN },
	{ ORDATA (BUF27, buf[27], 8), REG_HIDDEN },
	{ ORDATA (BUF28, buf[28], 8), REG_HIDDEN },
	{ ORDATA (BUF29, buf[29], 8), REG_HIDDEN },
	{ ORDATA (BUF30, buf[30], 8), REG_HIDDEN },
	{ ORDATA (BUF31, buf[31], 8), REG_HIDDEN },
	{ ORDATA (BUF32, buf[32], 8), REG_HIDDEN },
	{ ORDATA (BUF33, buf[33], 8), REG_HIDDEN },
	{ ORDATA (BUF34, buf[34], 8), REG_HIDDEN },
	{ ORDATA (BUF35, buf[35], 8), REG_HIDDEN },
	{ ORDATA (BUF36, buf[36], 8), REG_HIDDEN },
	{ ORDATA (BUF37, buf[37], 8), REG_HIDDEN },
	{ ORDATA (BUF38, buf[38], 8), REG_HIDDEN },
	{ ORDATA (BUF39, buf[39], 8), REG_HIDDEN },
	{ ORDATA (BUF40, buf[40], 8), REG_HIDDEN },
	{ ORDATA (BUF41, buf[41], 8), REG_HIDDEN },
	{ ORDATA (BUF42, buf[42], 8), REG_HIDDEN },
	{ ORDATA (BUF43, buf[43], 8), REG_HIDDEN },
	{ ORDATA (BUF44, buf[44], 8), REG_HIDDEN },
	{ ORDATA (BUF45, buf[45], 8), REG_HIDDEN },
	{ ORDATA (BUF46, buf[46], 8), REG_HIDDEN },
	{ ORDATA (BUF47, buf[47], 8), REG_HIDDEN },
	{ ORDATA (BUF48, buf[48], 8), REG_HIDDEN },
	{ ORDATA (BUF49, buf[49], 8), REG_HIDDEN },
	{ ORDATA (BUF50, buf[50], 8), REG_HIDDEN },
	{ ORDATA (BUF51, buf[51], 8), REG_HIDDEN },
	{ ORDATA (BUF52, buf[52], 8), REG_HIDDEN },
	{ ORDATA (BUF53, buf[53], 8), REG_HIDDEN },
	{ ORDATA (BUF54, buf[54], 8), REG_HIDDEN },
	{ ORDATA (BUF55, buf[55], 8), REG_HIDDEN },
	{ ORDATA (BUF56, buf[56], 8), REG_HIDDEN },
	{ ORDATA (BUF57, buf[57], 8), REG_HIDDEN },
	{ ORDATA (BUF58, buf[58], 8), REG_HIDDEN },
	{ ORDATA (BUF59, buf[59], 8), REG_HIDDEN },
	{ ORDATA (BUF60, buf[60], 8), REG_HIDDEN },
	{ ORDATA (BUF61, buf[61], 8), REG_HIDDEN },
	{ ORDATA (BUF62, buf[62], 8), REG_HIDDEN },
	{ ORDATA (BUF63, buf[63], 8), REG_HIDDEN },
	{ ORDATA (BUF64, buf[64], 8), REG_HIDDEN },
	{ ORDATA (BUF65, buf[65], 8), REG_HIDDEN },
	{ ORDATA (BUF66, buf[66], 8), REG_HIDDEN },
	{ ORDATA (BUF67, buf[67], 8), REG_HIDDEN },
	{ ORDATA (BUF68, buf[68], 8), REG_HIDDEN },
	{ ORDATA (BUF69, buf[69], 8), REG_HIDDEN },
	{ ORDATA (BUF70, buf[70], 8), REG_HIDDEN },
	{ ORDATA (BUF71, buf[71], 8), REG_HIDDEN },
	{ ORDATA (BUF72, buf[72], 8), REG_HIDDEN },
	{ ORDATA (BUF73, buf[73], 8), REG_HIDDEN },
	{ ORDATA (BUF74, buf[74], 8), REG_HIDDEN },
	{ ORDATA (BUF75, buf[75], 8), REG_HIDDEN },
	{ ORDATA (BUF76, buf[76], 8), REG_HIDDEN },
	{ ORDATA (BUF77, buf[77], 8), REG_HIDDEN },
	{ ORDATA (BUF78, buf[78], 8), REG_HIDDEN },
	{ ORDATA (BUF79, buf[79], 8), REG_HIDDEN },
	{ ORDATA (BUF80, buf[80], 8), REG_HIDDEN },
	{ ORDATA (BUF81, buf[81], 8), REG_HIDDEN },
	{ ORDATA (BUF82, buf[82], 8), REG_HIDDEN },
	{ ORDATA (BUF83, buf[83], 8), REG_HIDDEN },
	{ ORDATA (BUF84, buf[84], 8), REG_HIDDEN },
	{ ORDATA (BUF85, buf[85], 8), REG_HIDDEN },
	{ ORDATA (BUF86, buf[86], 8), REG_HIDDEN },
	{ ORDATA (BUF87, buf[87], 8), REG_HIDDEN },
	{ ORDATA (BUF88, buf[88], 8), REG_HIDDEN },
	{ ORDATA (BUF89, buf[89], 8), REG_HIDDEN },
	{ ORDATA (BUF90, buf[90], 8), REG_HIDDEN },
	{ ORDATA (BUF91, buf[91], 8), REG_HIDDEN },
	{ ORDATA (BUF92, buf[92], 8), REG_HIDDEN },
	{ ORDATA (BUF93, buf[93], 8), REG_HIDDEN },
	{ ORDATA (BUF94, buf[94], 8), REG_HIDDEN },
	{ ORDATA (BUF95, buf[95], 8), REG_HIDDEN },
	{ ORDATA (BUF96, buf[96], 8), REG_HIDDEN },
	{ ORDATA (BUF97, buf[97], 8), REG_HIDDEN },
	{ ORDATA (BUF98, buf[98], 8), REG_HIDDEN },
	{ ORDATA (BUF99, buf[99], 8), REG_HIDDEN },
	{ ORDATA (BUF100, buf[100], 8), REG_HIDDEN },
	{ ORDATA (BUF101, buf[101], 8), REG_HIDDEN },
	{ ORDATA (BUF102, buf[102], 8), REG_HIDDEN },
	{ ORDATA (BUF103, buf[103], 8), REG_HIDDEN },
	{ ORDATA (BUF104, buf[104], 8), REG_HIDDEN },
	{ ORDATA (BUF105, buf[105], 8), REG_HIDDEN },
	{ ORDATA (BUF106, buf[106], 8), REG_HIDDEN },
	{ ORDATA (BUF107, buf[107], 8), REG_HIDDEN },
	{ ORDATA (BUF108, buf[108], 8), REG_HIDDEN },
	{ ORDATA (BUF109, buf[109], 8), REG_HIDDEN },
	{ ORDATA (BUF110, buf[110], 8), REG_HIDDEN },
	{ ORDATA (BUF111, buf[111], 8), REG_HIDDEN },
	{ ORDATA (BUF112, buf[112], 8), REG_HIDDEN },
	{ ORDATA (BUF113, buf[113], 8), REG_HIDDEN },
	{ ORDATA (BUF114, buf[114], 8), REG_HIDDEN },
	{ ORDATA (BUF115, buf[115], 8), REG_HIDDEN },
	{ ORDATA (BUF116, buf[116], 8), REG_HIDDEN },
	{ ORDATA (BUF117, buf[117], 8), REG_HIDDEN },
	{ ORDATA (BUF118, buf[118], 8), REG_HIDDEN },
	{ ORDATA (BUF119, buf[119], 8), REG_HIDDEN },
	{ ORDATA (BUF120, buf[120], 8), REG_HIDDEN },
	{ ORDATA (BUF121, buf[121], 8), REG_HIDDEN },
	{ ORDATA (BUF122, buf[122], 8), REG_HIDDEN },
	{ ORDATA (BUF123, buf[123], 8), REG_HIDDEN },
	{ ORDATA (BUF124, buf[124], 8), REG_HIDDEN },
	{ ORDATA (BUF125, buf[125], 8), REG_HIDDEN },
	{ ORDATA (BUF126, buf[126], 8), REG_HIDDEN },
	{ ORDATA (BUF127, buf[127], 8), REG_HIDDEN },
	{ NULL }  };

MTAB rx_mod[] = {
	{ UNIT_WLK, 0, "write enabled", "ENABLED", NULL },
	{ UNIT_WLK, UNIT_WLK, "write locked", "LOCKED", NULL },
	{ 0 }  };

DEVICE rx_dev = {
	"RX", rx_unit, rx_reg, rx_mod,
	RX_NUMDR, 8, 20, 1, 8, 8,
	NULL, NULL, &rx_reset,
	&rx_boot, NULL, NULL };

/* I/O dispatch routine, I/O addresses 17777170 - 17777172

   17777170		floppy CSR
   17777172		floppy data register
*/

int rx_rd (int *data, int PA, int access)
{
switch ((PA >> 1) & 1) {				/* decode PA<1> */
case 0:							/* RXCS */
	rx_csr = rx_csr & RXCS_IMP;			/* clear junk */
	*data = rx_csr & RXCS_ROUT;
	return SCPE_OK;
case 1:							/* RXDB */
	if (rx_state == EMPTY) {			/* empty? */
		sim_activate (&rx_unit[0], rx_xwait);
		rx_csr = rx_csr & ~RXCS_TR;  }		/* clear xfer */
	*data = rx_dbr;					/* return data */
	return SCPE_OK;  }				/* end switch PA */
}							/* end rx_rd */

int rx_wr (int data, int PA, int access)
{
int drv;

switch ((PA >> 1) & 1) {				/* decode PA<1> */

/* Writing RXCS, three cases:
   1. Writing INIT, reset device
   2. Idle and writing new function
	- clear error, done, transfer ready, int req
	- save int enable, function, drive
	- start new function
   3. Otherwise, write IE and update interrupts
*/

case 0:							/* RXCS */
	rx_csr = rx_csr & RXCS_IMP;			/* clear junk */
	if (access == WRITEB) data = (PA & 1)?		/* write byte? */
		(rx_csr & 0377) | (data << 8): (rx_csr & ~0377) | data;
	if (data & RXCS_INIT) {				/* initialize? */
		rx_reset (&rx_dev);			/* reset device */
		return SCPE_OK;  }			/* end if init */
	if ((data & CSR_GO) && (rx_state == IDLE)) {	/* new function? */
		rx_csr = data & (CSR_IE + RXCS_DRV + RXCS_FUNC);
		bptr = 0;				/* clear buf pointer */
		switch ((data >> RXCS_V_FUNC) & RXCS_M_FUNC) {
		case RXCS_FILL:
			rx_state = FILL;		/* state = fill */
			rx_csr = rx_csr | RXCS_TR;	/* xfer is ready */
			break;
		case RXCS_EMPTY:
			rx_state = EMPTY;		/* state = empty */
			sim_activate (&rx_unit[0], rx_xwait);
			break;
		case RXCS_READ: case RXCS_WRITE: case RXCS_WRDEL:
			rx_state = RWDS;		/* state = get sector */
			rx_csr = rx_csr | RXCS_TR;	/* xfer is ready */
			rx_esr = rx_esr & RXES_ID;	/* clear errors */
			break;
		default:
			rx_state = CMD_COMPLETE;	/* state = cmd compl */
			drv = (data & RXCS_DRV) > 0;	/* get drive number */
			sim_activate (&rx_unit[drv], rx_cwait);
			break;  }			/* end switch func */
		return SCPE_OK;  }			/* end if GO */
	if ((data & CSR_IE) == 0) int_req = int_req & ~INT_RX;
	else if ((rx_csr & (RXCS_DONE + CSR_IE)) == RXCS_DONE)
		int_req = int_req | INT_RX;
	rx_csr = (rx_csr & ~RXCS_RW) | (data & RXCS_RW);
	return SCPE_OK;					/* end case RXCS */

/* Accessing RXDB, two cases:
   1. Write idle, write
   2. Write not idle and TR set, state dependent
*/

case 1:							/* RXDB */
	if ((PA & 1) || ((rx_state != IDLE) && ((rx_csr & RXCS_TR) == 0)))
		return SCPE_OK;				/* if ~IDLE, need tr */
	rx_dbr = data & 0377;				/* save data */
	if ((rx_state == FILL) || (rx_state == RWDS)) {	/* fill or sector? */
		sim_activate (&rx_unit[0], rx_xwait);	/* sched event */
		rx_csr = rx_csr & ~RXCS_TR;  }		/* clear xfer */
	if (rx_state == RWDT) {				/* track? */
		drv = (rx_csr & RXCS_DRV) > 0;		/* get drive number */
		sim_activate (&rx_unit[drv],		/* sched done */
			rx_swait * abs (rx_track - rx_unit[drv].TRACK));
		rx_csr = rx_csr & ~RXCS_TR;  }		/* clear xfer */
	return SCPE_OK;					/* end case RXDB */
	}						/* end switch PA */
}							/* end rx_wr */

/* Unit service; the action to be taken depends on the transfer state:

   IDLE		Should never get here, treat as unknown command
   RWDS		Just transferred sector, wait for track, set tr
   RWDT		Just transferred track, do read or write, finish command
   FILL		copy ir to buf[bptr], advance ptr
		if bptr > max, finish command, else set tr
   EMPTY	if bptr > max, finish command, else
		copy buf[bptr] to ir, advance ptr, set tr
   CMD_COMPLETE	copy requested data to ir, finish command
   INIT_COMPLETE read drive 0, track 1, sector 1 to buffer, finish command

   For RWDT and CMD_COMPLETE, the input argument is the selected drive;
   otherwise, it is drive 0.
*/

int rx_svc (UNIT *uptr)
{
int i, rval, err, func, da;
void rx_done (int new_dbr, int new_ecode);

rval = SCPE_OK;						/* assume ok */
func = (rx_csr >> RXCS_V_FUNC) & RXCS_M_FUNC;		/* get function */
switch (rx_state) {					/* case on state */
case IDLE:						/* idle */
	rx_done (rx_esr, 0);				/* done */
	break;
case EMPTY:						/* empty buffer */
	if (bptr >= RX_NUMBY) rx_done (rx_esr, 0);	/* done all? */
	else {	rx_dbr = buf[bptr];			/* get next */
		bptr = bptr + 1;
		rx_csr = rx_csr | RXCS_TR;  }		/* set xfer */
	break;
case FILL:						/* fill buffer */
	buf[bptr] = rx_dbr;				/* write next */
	bptr = bptr + 1;
	if (bptr < RX_NUMBY) rx_csr = rx_csr | RXCS_TR;	/* if more, set xfer */
	else rx_done (rx_esr, 0);			/* else done */
	break;
case RWDS:						/* wait for sector */
	rx_sector = rx_dbr & RX_M_SECTOR;		/* save sector */
	rx_csr = rx_csr | RXCS_TR;			/* set xfer */
	rx_state = RWDT;				/* advance state */
	break;
case RWDT:						/* wait for track */
	rx_track = rx_dbr & RX_M_TRACK;			/* save track */
	if (rx_track >= RX_NUMTR) {			/* bad track? */
		rx_done (rx_esr, 0040);			/* done, error */
		break;  }
	uptr -> TRACK = rx_track;			/* now on track */
	if ((rx_sector == 0) || (rx_sector > RX_NUMSC)) {	/* bad sect? */
		rx_done (rx_esr, 0070);			/* done, error */
		break;  }
	if ((uptr -> flags & UNIT_BUF) == 0) {		/* not buffered? */
		rx_done (rx_esr, 0070);			/* done, error */
		rval = SCPE_NOATT;			/* return error */
		break;  }
	da = CALC_DA (rx_track, rx_sector);		/* get disk address */
	if (func == RXCS_WRDEL) rx_esr = rx_esr | RXES_DD;	/* del data? */
	if (func == RXCS_READ) {			/* read? */
		for (i = 0; i < RX_NUMBY; i++)
			buf[i] = *(((char *) uptr -> filebuf) + da + i);  }
	else {	if (uptr -> flags & UNIT_WLK) {		/* write and locked? */
			rx_esr = rx_esr | RXES_WLK;	/* flag error */
			rx_done (rx_esr, 0100);		/* done, error */
			break;  }
		for (i = 0; i < RX_NUMBY; i++)		/* write */
			*(((char *) uptr -> filebuf) + da + i) = buf[i];
		da = da + RX_NUMBY;
		if (da > uptr -> hwmark) uptr -> hwmark = da;  }
	rx_done (rx_esr, 0);				/* done */
	break;
case CMD_COMPLETE:					/* command complete */
	if (func == RXCS_ECODE) rx_done (rx_ecode, 0);
	else if (uptr -> flags & UNIT_ATT) rx_done (rx_esr | RXES_DRDY, 0);
	else rx_done (rx_esr, 0);
	break;
case INIT_COMPLETE:					/* init complete */
	rx_unit[0].TRACK = 1;				/* drive 0 to trk 1 */
	rx_unit[1].TRACK = 0;				/* drive 1 to trk 0 */
	if ((rx_unit[0].flags & UNIT_BUF) == 0) {	/* not buffered? */
		rx_done (rx_esr | RXES_ID, 0010);	/* init done, error */
		break;	}
	da = CALC_DA (1, 1);				/* track 1, sector 1 */
	for (i = 0; i < RX_NUMBY; i++)			/* read sector */
		buf[i] = *(((char *) uptr -> filebuf) + da + i);
	if (rx_unit[1].flags & UNIT_ATT)		/* check drive 1 */
		rx_done (rx_esr | RXES_ID | RXES_DRDY, 0);
	else rx_done (rx_esr | RXES_ID | RXES_DRDY, 0020);
	break;  }					/* end case state */
return IORETURN (rx_stopioe, rval);
}							/* end rx_svc */

/* Command complete.  Set done and put final value in interface register,
   request interrupt if needed, return to IDLE state.
*/

void rx_done (int new_dbr, int new_ecode)
{
rx_csr = rx_csr | RXCS_DONE;				/* set done */
if (rx_csr & CSR_IE) int_req = int_req | INT_RX;	/* if ie, intr */
rx_dbr = new_dbr;					/* update RXDB */
if (new_ecode != 0) {					/* test for error */
	rx_ecode = new_ecode;
	rx_csr = rx_csr | CSR_ERR;  }
rx_state = IDLE;					/* now idle */
return;
}

/* Device initialization.  The RX is one of the few devices that schedules
   an I/O transfer as part of its initialization.
*/

int rx_reset (DEVICE *dptr)
{
int i;

rx_csr = rx_dbr = 0;					/* clear regs */
rx_esr = rx_ecode = 0;					/* clear error */
rx_state = INIT_COMPLETE;				/* set state */
int_req = int_req & ~INT_RX;				/* clear int req */
sim_cancel (&rx_unit[1]);				/* cancel drive 1 */
sim_activate (&rx_unit[0],				/* start drive 0 */
	rx_swait * abs (1 - rx_unit[0].TRACK));
return SCPE_OK;
}

/* Device bootstrap */

#define BOOT_START 02000
#define BOOT_UNIT 02006
#define BOOT_LEN (sizeof (boot_rom) / sizeof (int))

static int boot_rom[] = {
	0012706, 0002000,			/* MOV #2000, SP */
	0012700, 0000000,		/* MOV #unit, R0	; unit number */
	0010003,			/* MOV R0, R3 */
	0006303,			/* ASL R3 */
	0006303,			/* ASL R3 */
	0006303,			/* ASL R3 */
	0006303,			/* ASL R3 */
	0012701, 0177170,		/* MOV #RXCS, R1	; csr */
	0105711,			/* TSTB (R1)		; ready? */
	0001376,			/* BEQ .-2 */
	0052703, 0000007,		/* BIS #READ+GO, R3 */
	0010311,			/* MOV R3, (R1)		; read & go */
	0105711,			/* TSTB (R1)		; xfr ready? */
	0001376,			/* BEQ .-2 */
	0012761, 0000001, 0000002,	/* MOV #1, 2(R1)	; sector */
	0105711,			/* TSTB (R1)		; xfr ready? */
	0001376,			/* BEQ .-2 */
	0012761, 0000001, 0000002,	/* MOV #1, 2(R1)	; track */
	0005003,			/* CLR R3 */
	0012711, 0000003,		/* MOV #EMPTY+GO, (R1)	; empty & go */
	0105711,			/* TSTB (R1)		; xfr, done? */
	0001376,			/* BEQ .-2 */
	0100003,			/* BPL .+010 */
	0116123, 0000002,		/* MOVB 2(R1), (R3)+	; move byte */
	0000772,			/* BR .-012 */
	0005002,			/* CLR R2 */
	0005003,			/* CLR R3 */
	0005004,			/* CLR R4 */
	0012705, 0062170,		/* MOV #"DX, R5 */
	0005007				/* CLR R7 */
};

int rx_boot (int unitno)
{
int i;
extern int saved_PC;
extern unsigned short M[];

for (i = 0; i < BOOT_LEN; i++) M[(BOOT_START >> 1) + i] = boot_rom[i];
M[BOOT_UNIT >> 1] = unitno & RX_M_NUMDR;
saved_PC = BOOT_START;
return SCPE_OK;
}
