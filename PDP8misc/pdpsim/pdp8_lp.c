/* pdp8_lp.c: PDP-8 line printer simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   lpt		line printer
*/

#include "pdp8_defs.h"
extern int int_req, dev_done, dev_enable, stop_inst;
int lpt_err = 0, lpt_stopioe = 0;
int lpt_svc (UNIT *uptr);
int lpt_reset (DEVICE *dptr);
int lpt_attach (UNIT *uptr, char *cptr);
int lpt_detach (UNIT *uptr);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);
extern int attach_unit (UNIT *uptr, char *cptr);
extern int detach_unit (UNIT *uptr);

/* LPT data structures

   lpt_dev	LPT device descriptor
   lpt_unit	LPT unit descriptor
   lpt_reg	LPT register list
*/

UNIT lpt_unit = {
	UDATA (&lpt_svc, UNIT_SEQ+UNIT_ATTABLE, 0), SERIAL_OUT_WAIT };

REG lpt_reg[] = {
	{ ORDATA (BUF, lpt_unit.buf, 8) },
	{ FLDATA (INT, int_req, INT_V_LPT) },
	{ FLDATA (DONE, dev_done, INT_V_LPT) },
	{ FLDATA (ENABLE, dev_enable, INT_V_LPT) },
	{ FLDATA (ERR, lpt_err, 0) },
	{ DRDATA (POS, lpt_unit.pos, 32), PV_LEFT },
	{ DRDATA (TIME, lpt_unit.wait, 24), PV_LEFT },
	{ FLDATA (STOP_IOE, lpt_stopioe, 0) },
	{ NULL }  };

DEVICE lpt_dev = {
	"LPT", &lpt_unit, lpt_reg, NULL,
	1, 10, 32, 1, 8, 8,
	NULL, NULL, &lpt_reset,
	NULL, &lpt_attach, &lpt_detach };

/* Line printer routines

   lpt		process IOT
   lpt_svc	process event
   lpt_reset	process reset
   lpt_attach	process attach
   lpt_detach	process detach
*/

int lpt (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 1:							/* PSKF */
	return (dev_done & INT_LPT)? IOT_SKP + AC: AC;
case 2:							/* PCLF */
	dev_done = dev_done & ~INT_LPT;			/* clear flag */
	int_req = int_req & ~INT_LPT;			/* clear int req */
	return AC;
case 3:							/* PSKE */
	return (lpt_err)? IOT_SKP + AC: AC;
case 6:							/* PCLF!PSTB */
	dev_done = dev_done & ~INT_LPT;			/* clear flag */
	int_req = int_req & ~INT_LPT;			/* clear int req */
case 4:							/* PSTB */
	lpt_unit.buf = AC & 0177;			/* load buffer */
	if ((lpt_unit.buf == 015) || (lpt_unit.buf == 014) ||
	    (lpt_unit.buf == 012)) {
		sim_activate (&lpt_unit, lpt_unit.wait);
		return AC;  }
	return (lpt_svc (&lpt_unit) << IOT_V_REASON) + AC;
case 5:							/* PSIE */
	dev_enable = dev_enable | INT_LPT;		/* set enable */
	int_req = INT_UPDATE;				/* update interrupts */
	return AC;
case 7:							/* PCIE */
	dev_enable = dev_enable & ~INT_LPT;		/* clear enable */
	int_req = int_req & ~INT_LPT;			/* clear int req */
	return AC;
default:
	return (stop_inst << IOT_V_REASON) + AC;  }	/* end switch */
}							/* end lpt */

int lpt_svc (UNIT *uptr)
{
dev_done = dev_done | INT_LPT;				/* set done */
int_req = INT_UPDATE;					/* update interrupts */
if ((lpt_unit.flags & UNIT_ATT) == 0) {
	lpt_err = 1;
	return IORETURN (lpt_stopioe, SCPE_UNATT);  }
if (putc (lpt_unit.buf, lpt_unit.fileref) == EOF) {
	perror ("LPT I/O error");
	clearerr (lpt_unit.fileref);
	return SCPE_IOERR;  }
lpt_unit.pos = lpt_unit.pos + 1;
return SCPE_OK;
}							/* end lpt_svc */

int lpt_reset (DEVICE *dptr)
{
lpt_unit.buf = 0;
dev_done = dev_done & ~INT_LPT;				/* clear done, int */
int_req = int_req & ~INT_LPT;
dev_enable = dev_enable | INT_LPT;			/* set enable */
lpt_err = (lpt_unit.flags & UNIT_ATT) == 0;
sim_cancel (&lpt_unit);					/* deactivate unit */
return SCPE_OK;
}							/* end lpt_reset */

int lpt_attach (UNIT *uptr, char *cptr)
{
int reason;

reason = attach_unit (uptr, cptr);
lpt_err = (lpt_unit.flags & UNIT_ATT) == 0;
return reason;
}

int lpt_detach (UNIT *uptr)
{
lpt_err = 1;
return detach_unit (uptr);
}
