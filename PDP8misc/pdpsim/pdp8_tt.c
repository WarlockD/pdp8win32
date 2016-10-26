/* pdp8_tt.c: PDP-8 console terminal simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   tti		terminal input
   tto		terminal output
*/

#include "pdp8_defs.h"
extern int int_req, dev_done, dev_enable, stop_inst;
int tti_svc (UNIT *uptr);
int tto_svc (UNIT *uptr);
int tti_reset (DEVICE *dptr);
int tto_reset (DEVICE *dptr);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);
extern int sim_poll_kbd (void);
extern int sim_type_tty (char out);

/* TTI data structures

   tti_dev	TTI device descriptor
   tti_unit	TTI unit descriptor
   tti_reg	TTI register list
*/

UNIT tti_unit = { UDATA (&tti_svc, 0, 0), KBD_POLL_WAIT };

REG tti_reg[] = {
	{ ORDATA (BUF, tti_unit.buf, 8) },
	{ FLDATA (INT, int_req, INT_V_TTI) },
	{ FLDATA (DONE, dev_done, INT_V_TTI) },
	{ FLDATA (ENABLE, dev_enable, INT_V_TTI) },
	{ DRDATA (POS, tti_unit.pos, 32), PV_LEFT },
	{ DRDATA (TIME, tti_unit.wait, 24), PV_LEFT },
	{ NULL }  };

DEVICE tti_dev = {
	"TTI", &tti_unit, tti_reg, NULL,
	1, 10, 32, 1, 8, 8,
	NULL, NULL, &tti_reset,
	NULL, NULL, NULL };

/* TTO data structures

   tto_dev	TTO device descriptor
   tto_unit	TTO unit descriptor
   tto_reg	TTO register list
*/

UNIT tto_unit = { UDATA (&tto_svc, 0, 0), SERIAL_OUT_WAIT };

REG tto_reg[] = {
	{ ORDATA (BUF, tto_unit.buf, 8) },
	{ FLDATA (INT, int_req, INT_V_TTO) },
	{ FLDATA (DONE, dev_done, INT_V_TTO) },
	{ FLDATA (ENABLE, dev_enable, INT_V_TTO) },
	{ DRDATA (POS, tto_unit.pos, 32), PV_LEFT },
	{ DRDATA (TIME, tto_unit.wait, 24), PV_LEFT },
	{ NULL }  };

DEVICE tto_dev = {
	"TTO", &tto_unit, tto_reg, NULL,
	1, 10, 32, 1, 8, 8,
	NULL, NULL, &tto_reset, 
	NULL, NULL, NULL };

/* Terminal input routines

   tti		process IOT
   tti_svc	process event
   tti_reset	process reset
*/

int tti (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 0: 						/* KCF */
	dev_done = dev_done & ~INT_TTI;			/* clear flag */
	int_req = int_req & ~INT_TTI;
	return AC;
case 1:							/* KSF */
	return (dev_done & INT_TTI)? IOT_SKP + AC: AC;
case 2:							/* KCC */
	dev_done = dev_done & ~INT_TTI;			/* clear flag */
	int_req = int_req & ~INT_TTI;
	return 0;					/* clear AC */
case 4:							/* KRS */
	return (AC | tti_unit.buf);			/* return buffer */
case 5:							/* KIE */
	if (AC & 1) dev_enable = dev_enable | (INT_TTI+INT_TTO);
	else dev_enable = dev_enable & ~(INT_TTI+INT_TTO);
	int_req = INT_UPDATE;				/* update interrupts */
	return AC;
case 6:							/* KRB */
	dev_done = dev_done & ~INT_TTI;			/* clear flag */
	int_req = int_req & ~INT_TTI;
	return (tti_unit.buf);				/* return buffer */
default:
	return (stop_inst << IOT_V_REASON) + AC;  }	/* end switch */
}							/* end tti */

int tti_svc (UNIT *uptr)
{
int temp;

sim_activate (&tti_unit, tti_unit.wait);		/* continue poll */
if ((temp = sim_poll_kbd ()) < SCPE_KFLAG) return temp;	/* no char or error? */
tti_unit.buf = (temp & 0377) | 0200;			/* got char */
dev_done = dev_done | INT_TTI;				/* set done */
int_req = INT_UPDATE;					/* update interrupts */
tti_unit.pos = tti_unit.pos + 1;
return SCPE_OK;
}							/* end tti_svc */

int tti_reset (DEVICE *dptr)
{
tti_unit.buf = 0;
dev_done = dev_done & ~INT_TTI;				/* clear done, int */
int_req = int_req & ~INT_TTI;
dev_enable = dev_enable | INT_TTI;			/* set enable */
sim_activate (&tti_unit, tti_unit.wait);		/* activate unit */
return SCPE_OK;
}							/* end tti_reset */

/* Terminal output routines

   tto		process IOT
   tto_svc	process event
   tto_reset	process reset
*/

int tto (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 0: 						/* TLF */
	dev_done = dev_done | INT_TTO;			/* set flag */
	int_req = INT_UPDATE;				/* update interrupts */
	return AC;
case 1:							/* TSF */
	return (dev_done & INT_TTO)? IOT_SKP + AC: AC;
case 2:							/* TCF */
	dev_done = dev_done & ~INT_TTO;			/* clear flag */
	int_req = int_req & ~INT_TTO;			/* clear int req */
	return AC;
case 5:							/* SPI */
	return (int_req & (INT_TTI+INT_TTO))? IOT_SKP + AC: AC;
case 6:							/* TLS */
	dev_done = dev_done & ~INT_TTO;			/* clear flag */
	int_req = int_req & ~INT_TTO;			/* clear int req */
case 4:							/* TPC */
	sim_activate (&tto_unit, tto_unit.wait);	/* activate unit */
	tto_unit.buf = AC;				/* load buffer */
	return AC;
default:
	return (stop_inst << IOT_V_REASON) + AC;  }	/* end switch */
}							/* end tto */

int tto_svc (UNIT *uptr)
{
int temp;

dev_done = dev_done | INT_TTO;				/* set done */
int_req = INT_UPDATE;					/* update interrupts */
if ((temp = sim_type_tty (tto_unit.buf & 0177)) != SCPE_OK) return temp;
tto_unit.pos = tto_unit.pos + 1;
return SCPE_OK;
}							/* end tto_svc */

int tto_reset (DEVICE *dptr)
{
tto_unit.buf = 0;
dev_done = dev_done & ~INT_TTO;				/* clear done, int */
int_req = int_req & ~INT_TTO;
dev_enable = dev_enable | INT_TTO;			/* set enable */
sim_cancel (&tto_unit);					/* deactivate unit */
return SCPE_OK;
}							/* end tto_reset */
