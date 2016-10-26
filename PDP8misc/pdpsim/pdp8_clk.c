/* pdp8_clk.c: PDP-8 real-time clock simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   clk		real-time clock

   Note: includes the IOT's for both the PDP-8/E and PDP-8/A clocks
*/

#include "pdp8_defs.h"
extern int int_req, dev_enable, dev_done, stop_inst;
int clk_svc (UNIT *uptr);
int clk_reset (DEVICE *dptr);
extern int sim_activate (UNIT *uptr, int interval);
extern int sim_cancel (UNIT *uptr);

/* CLK data structures

   clk_dev	CLK device descriptor
   clk_unit	CLK unit descriptor
   clk_reg	CLK register list
*/

UNIT clk_unit = { UDATA (&clk_svc, 0, 0), 5000 };

REG clk_reg[] = {
	{ FLDATA (INT, int_req, INT_V_CLK) },
	{ FLDATA (DONE, dev_done, INT_V_CLK) },
	{ FLDATA (ENABLE, dev_enable, INT_V_CLK) },
	{ DRDATA (TIME, clk_unit.wait, 24), PV_LEFT },
	{ NULL }  };

DEVICE clk_dev = {
	"CLK", &clk_unit, clk_reg, NULL,
	1, 0, 0, 0, 0, 0,
	NULL, NULL, &clk_reset,
	NULL, NULL, NULL };

/* Clock routines

   clk		process IOT
   clk_svc	process event
   clk_reset	process reset

   IOT's 6131-6133 are the PDP-8/E clock; 6135-6137 the PDP-8/A clock
*/

int clk (int pulse, int AC)
{
switch (pulse) {					/* decode IR<9:11> */
case 1:							/* CLEI */
	dev_enable = dev_enable | INT_CLK;		/* enable clk ints */
	int_req = INT_UPDATE;				/* update interrupts */
	return AC;
case 2:							/* CLDI */
	dev_enable = dev_enable & ~INT_CLK;		/* disable clk ints */
	int_req = int_req & ~INT_CLK;			/* update interrupts */
	return AC;
case 3:							/* CLSC */
	if (dev_done & INT_CLK) {			/* flag set? */
		dev_done = dev_done & ~INT_CLK;		/* clear flag */
		int_req = int_req & ~INT_CLK;		/* clear int req */
		return IOT_SKP + AC;  }
	return AC;
case 5:							/* CLLE */
	if (AC & 1) dev_enable = dev_enable | INT_CLK;	/* test AC<11> */
	else dev_enable = dev_enable & ~INT_CLK;
	int_req = INT_UPDATE;				/* update interrupts */
	return AC;
case 6:							/* CLCL */
	dev_done = dev_done & ~INT_CLK;			/* clear flag */
	int_req = int_req & ~INT_CLK;			/* clear int req */
	return AC;
case 7:							/* CLSK */
	return (dev_done & INT_CLK)? IOT_SKP + AC: AC;
default:
	return (stop_inst << IOT_V_REASON) + AC;  }	/* end switch */
}							/* end clk */

int clk_svc (UNIT *uptr)
{
dev_done = dev_done | INT_CLK;				/* set done */
int_req = INT_UPDATE;					/* update interrupts */
sim_activate (&clk_unit, clk_unit.wait);		/* reactivate unit */
return SCPE_OK;
}							/* end clk_svc */

int clk_reset (DEVICE *dptr)
{
dev_done = dev_done & ~INT_CLK;				/* clear done, int */
int_req = int_req & ~INT_CLK;
dev_enable = dev_enable & ~INT_CLK;			/* clear enable */
sim_activate (&clk_unit, clk_unit.wait);		/* activate unit */
return SCPE_OK;
}							/* end clk_reset */
