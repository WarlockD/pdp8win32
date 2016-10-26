/* pdp8_pt.c: PDP-8 paper tape reader/punch simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   ptr		paper tape reader
   ptp		paper tape punch
*/

#include "pdp8_defs.h"
extern int int_req, dev_done, dev_enable, stop_inst;
int ptr_stopioe = 0, ptp_stopioe = 0;
int ptr_svc (UNIT *uptr);
int ptp_svc (UNIT *uptr);
int ptr_reset (DEVICE *dptr);
int ptp_reset (DEVICE *dptr);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);

/* PTR data structures

   ptr_dev	PTR device descriptor
   ptr_unit	PTR unit descriptor
   ptr_reg	PTR register list
*/

UNIT ptr_unit = {
	UDATA (&ptr_svc, UNIT_SEQ+UNIT_ATTABLE, 0), SERIAL_IN_WAIT };

REG ptr_reg[] = {
	{ ORDATA (BUF, ptr_unit.buf, 8) },
	{ FLDATA (INT, int_req, INT_V_PTR) },
	{ FLDATA (DONE, dev_done, INT_V_PTR) },
	{ FLDATA (ENABLE, dev_enable, INT_V_PTR) },
	{ DRDATA (POS, ptr_unit.pos, 32), PV_LEFT },
	{ DRDATA (TIME, ptr_unit.wait, 24), PV_LEFT },
	{ FLDATA (STOP_IOE, ptr_stopioe, 0) },
	{ NULL }  };

DEVICE ptr_dev = {
	"PTR", &ptr_unit, ptr_reg, NULL,
	1, 10, 32, 1, 8, 8,
	NULL, NULL, &ptr_reset,
	NULL, NULL, NULL };

/* PTP data structures

   ptp_dev	PTP device descriptor
   ptp_unit	PTP unit descriptor
   ptp_reg	PTP register list
*/

UNIT ptp_unit = {
	UDATA (&ptp_svc, UNIT_SEQ+UNIT_ATTABLE, 0), SERIAL_OUT_WAIT };

REG ptp_reg[] = {
	{ ORDATA (BUF, ptp_unit.buf, 8) },
	{ FLDATA (INT, int_req, INT_V_PTP) },
	{ FLDATA (DONE, dev_done, INT_V_PTP) },
	{ FLDATA (ENABLE, dev_enable, INT_V_PTP) },
	{ DRDATA (POS, ptp_unit.pos, 32), PV_LEFT },
	{ DRDATA (TIME, ptp_unit.wait, 24), PV_LEFT },
	{ FLDATA (STOP_IOE, ptp_stopioe, 0) },
	{ NULL }  };

DEVICE ptp_dev = {
	"PTP", &ptp_unit, ptp_reg, NULL,
	1, 10, 32, 1, 8, 8,
	NULL, NULL, &ptp_reset,
	NULL, NULL, NULL };

/* Paper tape reader routines

   ptr		process IOT
   ptr_svc	process event
   ptr_reset	process reset
*/

int ptr (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 0: 						/* RPE */
	dev_enable = dev_enable | (INT_PTR+INT_PTP);	/* set enable */
	int_req = INT_UPDATE;				/* update interrupts */
	return AC;
case 1:							/* RSF */
	return (dev_done & INT_PTR)? IOT_SKP + AC: AC;	
case 6:							/* RFC!RRB */
	sim_activate (&ptr_unit, ptr_unit.wait);
case 2:							/* RRB */
	dev_done = dev_done & ~INT_PTR;			/* clear flag */
	int_req = int_req & ~INT_PTR;			/* clear int req */
	return (AC | ptr_unit.buf);			/* or data to AC */
case 4:							/* RFC */
	sim_activate (&ptr_unit, ptr_unit.wait);
	dev_done = dev_done & ~INT_PTR;			/* clear flag */
	int_req = int_req & ~INT_PTR;			/* clear int req */
	return AC;
default:
	return (stop_inst << IOT_V_REASON) + AC;  }	/* end switch */
}							/* end ptr */

int ptr_svc (UNIT *uptr)
{
int temp;

dev_done = dev_done | INT_PTR;				/* set done */
int_req = INT_UPDATE;					/* update interrupts */
if ((ptr_unit.flags & UNIT_ATT) == 0)			/* attached? */
	return IORETURN (ptr_stopioe, SCPE_UNATT);
if ((temp = getc (ptr_unit.fileref)) == EOF) {
	if (feof (ptr_unit.fileref)) {
		if (ptr_stopioe) printf ("PTR end of file\n");
		else return SCPE_OK;  }
	else perror ("PTR I/O error");
	clearerr (ptr_unit.fileref);
	return SCPE_IOERR;  }
ptr_unit.buf = temp & 0377;
ptr_unit.pos = ptr_unit.pos + 1;
return SCPE_OK;
}							/* end ptr_svc */

int ptr_reset (DEVICE *dptr)
{
ptr_unit.buf = 0;
dev_done = dev_done & ~INT_PTR;				/* clear done, int */
int_req = int_req & ~INT_PTR;
dev_enable = dev_enable | INT_PTR;			/* set enable */
sim_cancel (&ptr_unit);					/* deactivate unit */
return SCPE_OK;
}							/* end ptr_reset */

/* Paper tape punch routines

   ptp		process IOT
   ptp_svc	process event
   ptp_reset	process reset
*/

int ptp (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 0: 						/* PCE */
	dev_enable = dev_enable & ~(INT_PTR+INT_PTP);	/* clear enables */
	int_req = INT_UPDATE;				/* update interrupts */
	return AC;
case 1:							/* PSF */
	return (dev_done & INT_PTP)? IOT_SKP + AC: AC;
case 2:							/* PCF */
	dev_done = dev_done & ~INT_PTP;			/* clear flag */
	int_req = int_req & ~INT_PTP;			/* clear int req */
	return AC;
case 6:							/* PLS */
	dev_done = dev_done & ~INT_PTP;			/* clear flag */
	int_req = int_req & ~INT_PTP;			/* clear int req */
case 4:							/* PPC */
	ptp_unit.buf = AC & 0377;			/* load punch buf */
	sim_activate (&ptp_unit, ptp_unit.wait);	/* activate unit */
	return AC;
default:
	return (stop_inst << IOT_V_REASON) + AC;  }	/* end switch */
}							/* end ptp */

int ptp_svc (UNIT *uptr)
{
dev_done = dev_done | INT_PTP;				/* set done */
int_req = INT_UPDATE;					/* update interrupts */
if ((ptp_unit.flags & UNIT_ATT) == 0)			/* attached? */
	return IORETURN (ptp_stopioe, SCPE_UNATT);
if (putc (ptp_unit.buf, ptp_unit.fileref) == EOF) {
	perror ("PTP I/O error");
	clearerr (ptp_unit.fileref);
	return SCPE_IOERR;  }
ptp_unit.pos = ptp_unit.pos + 1;
return SCPE_OK;
}							/* end ptp_svc */

int ptp_reset (DEVICE *dptr)
{
ptp_unit.buf = 0;
dev_done = dev_done & ~INT_PTP;				/* clear done, int */
int_req = int_req & ~INT_PTP;
dev_enable = dev_enable | INT_PTP;			/* set enable */
sim_cancel (&ptp_unit);					/* deactivate unit */
return SCPE_OK;
}							/* end ptp_reset */
