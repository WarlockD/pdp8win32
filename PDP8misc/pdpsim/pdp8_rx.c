/* pdp8_rx.c: RX8E/RX01 floppy disk simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   rx		RX8E disk controller

   An RX01 diskette consists of 77 tracks, each with 26 sectors of 128B.
   Tracks are numbered 0-76, sectors 1-26.  The RX8E can store data in
   8b mode or 12b mode.  In 8b mode, the controller reads or writes
   128 bytes per sector.  In 12b mode, the reads or writes 64 12b words
   per sector.  The 12b words are bit packed into the first 96 bytes
   of the sector; the last 32 bytes are zeroed on writes.
*/

#include "pdp8_defs.h"

#define RX_NUMTR	77				/* tracks/disk */
#define RX_M_TRACK	0377
#define RX_NUMSC	26				/* sectors/track */
#define RX_M_SECTOR	0377
#define RX_NUMBY	128				/* bytes/sector */
#define RX_NUMWD	(RX_NUMBY / 2)			/* words/sector */
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
#define RXCS_DRV	0020				/* drive */
#define RXCS_MODE	0100				/* mode */
#define RXCS_MAINT	0200				/* maintenance */

#define RXES_CRC	0001				/* CRC error */
#define RXES_PAR	0002				/* parity error */
#define RXES_ID		0004				/* init done */
#define RXES_WLK	0010				/* write protect */
#define RXES_DD		0100				/* deleted data */
#define RXES_DRDY	0200				/* drive ready */

#define TRACK u3					/* current track */
#define READ_RXDBR ((rx_csr & RXCS_MODE)? AC | (rx_dbr & 0377): rx_dbr)
#define CALC_DA(t,s) (((t) * RX_NUMSC) + ((s) - 1)) * RX_NUMBY

extern int int_req, dev_done, dev_enable;
int rx_tr = 0, rx_err = 0;
int rx_dbr = 0, rx_esr = 0, rx_ecode = 0;
int rx_csr = 0, rx_state = IDLE;
int rx_track = 0, rx_sector = 0;
int rx_cwait = 100;					/* command time */
int rx_swait = 10;					/* seek, per track */
int rx_xwait = 1;					/* tr set time */
int rx_stopioe = 1;
unsigned char buf[RX_NUMBY] = { 0 };
int bufptr = 0;
int rx_svc (UNIT *uptr);
int rx_reset (DEVICE *dptr);
int rx_boot (int unitno);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);

/* RX8E data structures

   rx_dev	RX device descriptor
   rx_unit	RX unit list
   rx_reg	RX register list
   rx_mod	RX modifier list
*/

UNIT rx_unit[] = {
	{ UDATA (&rx_svc,
	  UNIT_FIX+UNIT_ATTABLE+UNIT_BUFABLE+UNIT_MUSTBUF, RX_SIZE) },
	{ UDATA (&rx_svc,
	  UNIT_FIX+UNIT_ATTABLE+UNIT_BUFABLE+UNIT_MUSTBUF, RX_SIZE) }  };

REG rx_reg[] = {
	{ ORDATA (RXCS, rx_csr, 12) },
	{ ORDATA (RXDB, rx_dbr, 12) },
	{ ORDATA (RXES, rx_esr, 8) },
	{ ORDATA (RXERR, rx_ecode, 8) },
	{ ORDATA (RXTA, rx_track, 8) },
	{ ORDATA (RXSA, rx_sector, 8) },
	{ ORDATA (STAPTR, rx_state, 3), REG_RO },
	{ ORDATA (BUFPTR, bufptr, 7)  },
	{ FLDATA (INT, int_req, INT_V_RX) },
	{ FLDATA (DONE, dev_done, INT_V_RX) },
	{ FLDATA (ENABLE, dev_enable, INT_V_RX) },
	{ FLDATA (TR, rx_tr, 0) },
	{ FLDATA (ERR, rx_err, 0) },
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

/* Floppy disk routines

   rx		process IOT
   rx_svc	process event
   rx_reset	process reset
*/

int rx (int pulse, int AC)
{
int drv, i, rval;
UNIT *uptr;

switch (pulse) {					/* decode IR<9:11> */
case 0:							/* unused */
	return AC;
case 1:							/* LCD */
	if (rx_state != IDLE) return AC;		/* ignore if busy */
	rx_dbr = rx_csr = AC;				/* save new command */
	dev_done = dev_done & ~INT_RX;			/* clear done, int */
	int_req = int_req & ~INT_RX;
	rx_tr = rx_err = 0;				/* clear flags */
	bufptr = 0;					/* clear buf pointer */
	switch ((AC >> RXCS_V_FUNC) & RXCS_M_FUNC) {	/* decode command */
	case RXCS_FILL:
		rx_state = FILL;			/* state = fill */
		rx_tr = 1;				/* xfer is ready */
		break;
	case RXCS_EMPTY:
		rx_state = EMPTY;			/* state = empty */
		sim_activate (&rx_unit[0], rx_xwait);	/* sched xfer */
		break;
	case RXCS_READ: case RXCS_WRITE: case RXCS_WRDEL:
		rx_state = RWDS;			/* state = get sector */
		rx_tr = 1;				/* xfer is ready */
		rx_esr = rx_esr & RXES_ID;		/* clear errors */
		break;
	default:
		rx_state = CMD_COMPLETE;		/* state = cmd compl */
		drv = (rx_csr & RXCS_DRV) > 0;		/* get drive number */
		sim_activate (&rx_unit[drv], rx_cwait);	/* sched done */
		break;  }				/* end switch func */
	return AC;
case 2:							/* XDR */
	switch (rx_state & 07) {			/* case on state */
	default:					/* default */
		return READ_RXDBR;			/* return data reg */
	case EMPTY:					/* emptying buffer */
		sim_activate (&rx_unit[0], rx_xwait);	/* sched xfer */
		return READ_RXDBR;			/* return data reg */
	case RWDS:					/* sector */
		rx_sector = AC & RX_M_SECTOR;		/* save sector */
	case FILL:					/* fill */
		rx_dbr = AC;				/* save data */
		sim_activate (&rx_unit[0], rx_xwait);	/* sched xfer */
		break;
	case RWDT:					/* track */
		rx_track = AC & RX_M_TRACK;		/* save track */
		rx_dbr = AC;				/* save data */
		drv = (rx_csr & RXCS_DRV) > 0;		/* get drive number */
		sim_activate (&rx_unit[drv],		/* sched done */
			rx_swait * abs (rx_track - rx_unit[drv].TRACK));
		break;  }				/* end switch state */
	return AC;
case 3:							/* STR */
	if (rx_tr != 0) {
		rx_tr = 0;
		return IOT_SKP + AC;  }
	return AC;
case 4:							/* SER */
	if (rx_err != 0) {
		rx_err = 0;
		return IOT_SKP + AC;  }
	return AC;
case 5:							/* SDN */
	if ((dev_done & INT_RX) != 0) {
		dev_done = dev_done & ~INT_RX;
		int_req = int_req & ~INT_RX;
		return IOT_SKP + AC;  }
	return AC;
case 6:							/* INTR */
	if (AC & 1) dev_enable = dev_enable | INT_RX;
	else dev_enable = dev_enable & ~INT_RX;
	int_req = INT_UPDATE;
	return AC;
case 7:							/* INIT */
	rx_reset (&rx_dev);				/* reset device */
	return AC;  }					/* end case pulse */
}							/* end rx */

/* Unit service; the action to be taken depends on the transfer state:

   IDLE		Should never get here, treat as unknown command
   RWDS		Just transferred sector, wait for track, set tr
   RWDT		Just transferred track, do read or write, finish command
   FILL		copy dbr to buf[bufptr], advance ptr
   		if bufptr > max, finish command, else set tr
   EMPTY	if bufptr > max, finish command, else
		copy buf[bufptr] to dbr, advance ptr, set tr
   CMD_COMPLETE	copy requested data to dbr, finish command
   INIT_COMPLETE read drive 0, track 1, sector 1 to buffer, finish command

   For RWDT and CMD_COMPLETE, the input argument is the selected drive;
   otherwise, it is drive 0.
*/

int rx_svc (UNIT *uptr)
{
int i, rval, err, func, byptr, da;
void rx_done (int new_dbr, int new_ecode);
#define PTR12(x) (((x) + (x) + (x)) >> 1)

rval = SCPE_OK;						/* assume ok */
func = (rx_csr >> RXCS_V_FUNC) & RXCS_M_FUNC;		/* get function */
switch (rx_state) {					/* case on state */
case IDLE:						/* idle */
	rx_done (rx_esr, 0);				/* done */
	break;
case EMPTY:						/* empty buffer */
	if (rx_csr & RXCS_MODE) {			/* 8b xfer? */
		if (bufptr >= RX_NUMBY) {		/* done? */
			rx_done (rx_esr, 0);		/* set done */
			break;  }			/* and exit */
		rx_dbr = buf[bufptr];  }		/* else get data */
	else {	byptr = PTR12 (bufptr);			/* 12b xfer */
		if (bufptr >= RX_NUMWD) {		/* done? */
			rx_done (rx_esr, 0);		/* set done */
			break;  }			/* and exit */
		rx_dbr = (bufptr & 1)?			/* get data */
			((buf[byptr] & 017) << 8) | buf[byptr + 1]:
			(buf[byptr] << 4) | ((buf[byptr + 1] >> 4) & 017);  }
	bufptr = bufptr + 1;
	rx_tr = 1;
	break;
case FILL:						/* fill buffer */
	if (rx_csr & RXCS_MODE) {			/* 8b xfer? */
		buf[bufptr] = rx_dbr;			/* fill buffer */
		bufptr = bufptr + 1;
		if (bufptr < RX_NUMBY) rx_tr = 1;	/* if more, set xfer */
		else rx_done (rx_esr, 0);  }		/* else done */
	else { 	byptr = PTR12 (bufptr);			/* 12b xfer */
		if (bufptr & 1) {			/* odd or even? */
		  buf[byptr] = (buf[byptr] & 0360) | ((rx_dbr >> 8) & 017);
		  buf[byptr + 1] = rx_dbr & 0377;  }
		else {
		  buf[byptr] = (rx_dbr >> 4) & 0377;
		  buf[byptr + 1] = (rx_dbr & 017) << 4;  }
		bufptr = bufptr + 1;
		if (bufptr < RX_NUMWD) rx_tr = 1;	/* if more, set xfer */
		else {	for (i = PTR12 (RX_NUMWD); i < RX_NUMBY; i++)
				buf[i] = 0;		/* else fill sector */
			rx_done (rx_esr, 0);  }  }	/* set done */
	break;
case RWDS:						/* wait for sector */
	rx_tr = 1;					/* set xfer ready */
	rx_state = RWDT;				/* advance state */
	break;
case RWDT:						/* wait for track */
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
}

/* Command complete.  Set done and put final value in interface register,
   return to IDLE state.
*/

void rx_done (int new_dbr, int new_ecode)
{
dev_done = dev_done | INT_RX;				/* set done */
int_req = INT_UPDATE;					/* update ints */
rx_dbr = new_dbr;					/* update buffer */
if (new_ecode != 0) {					/* test for error */
	rx_ecode = new_ecode;
	rx_err = 1;  }
rx_state = IDLE;					/* now idle */
return;
}

/* Device initialization.  The RX is one of the few devices that schedules
   an I/O transfer as part of its initialization.
*/

int rx_reset (DEVICE *dptr)
{
int i;

rx_esr = rx_ecode = 0;					/* clear error */
rx_tr = rx_err = 0;					/* clear flags */
dev_done = dev_done & ~INT_RX;				/* clear done, int */
int_req = int_req & ~INT_RX;
rx_dbr = rx_csr = 0;					/* 12b mode, drive 0 */
rx_state = INIT_COMPLETE;				/* set state */
sim_cancel (&rx_unit[1]);				/* cancel drive 1 */
sim_activate (&rx_unit[0],				/* start drive 0 */
	rx_swait * abs (1 - rx_unit[0].TRACK));
return SCPE_OK;
}

/* Device bootstrap */

#define BOOT_START 022
#define BOOT_INST 060
#define BOOT_LEN (sizeof (boot_rom) / sizeof (int))

static int boot_rom[] = {
	06705,			/* 22, SDN */
	05020,			/* 23, JMP .-1 */
	07126,			/* 24, CLL CML RTL	; read command + */
	01060,			/* 25, TAD UNIT		; unit no */
	06701,			/* 26, LCD		; load read+unit */
	07201,			/* 27, CLL IAC 		; AC = 1 */
	04053,			/* 30, JMS 053		; load sector */
	04053,			/* 31, JMS 053		; load track */
	07104,			/* 32, CLL RAL		; AC = 2 */
	06705,			/* 33, SDN */
	05054,			/* 34, JMP 54 */
	06704,			/* 35, SER */
	07450,			/* 36, SNA		; more to do? */
	07610,			/* 37, CLA SKP		; error */
	05046,			/* 40, JMP 46		; go empty */
	07402, 07402,		/* 41-45, HALT		; error */
	07402, 07402, 07402,
	06701,			/* 46, LCD		; load empty */
	04053,			/* 47, JMS 53		; get data */
	03002,			/* 50, DCA 2		; store */
	02050,			/* 51, ISZ 50		; incr store */
	05047,			/* 52, JMP 47		; loop until done */
	00000,			/* 53, 0 */
	06703,			/* 54, STR */
	05033,			/* 55, JMP 33 */
	06702,			/* 56, XDR */
	05453,			/* 57, JMP I 53 */
	07024,			/* UNIT, CML RAL	; for unit 1 */
	06030			/* 61, KCC */
};

int rx_boot (int unitno)
{
int i;
extern int saved_PC;
extern unsigned short M[];

for (i = 0; i < BOOT_LEN; i++) M[BOOT_START + i] = boot_rom[i];
M[BOOT_INST] = unitno? 07024: 07004;
saved_PC = BOOT_START;
return SCPE_OK;
}
