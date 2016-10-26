/* pdp11_sys.c: PDP-11 simulator interface

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited
*/

#include "pdp11_defs.h"

extern DEVICE cpu_dev;
extern DEVICE ptr_dev, ptp_dev;
extern DEVICE tti_dev, tto_dev;
extern DEVICE lpt_dev, clk_dev;
extern DEVICE rk_dev, rx_dev;
extern DEVICE rl_dev;
extern REG cpu_reg[];
extern unsigned short M[];
extern int saved_PC;

/* SCP data structures and interface routines

   sim_name		simulator name string
   sim_PC		pointer to saved PC register descriptor
   sim_devices		array of pointers to simulated devices
   sim_stop_messages	array of pointers to stop messages
   sim_load		binary loader
*/

char sim_name[] = "PDP-11";

REG *sim_PC = &cpu_reg[0];

DEVICE *sim_devices[] = { &cpu_dev,
	&ptr_dev, &ptp_dev, &tti_dev, &tto_dev,
	&lpt_dev, &clk_dev, &rk_dev,
	&rl_dev, &rx_dev, NULL };

char *sim_stop_messages[] = {
	"Unknown error",
	"Red stack trap",
	"Odd address trap",
	"Memory management trap",
	"Non-existent memory trap",
	"Parity error trap",
	"Privilege trap",
	"Illegal instruction trap",
	"BPT trap",
	"IOT trap",
	"EMT trap",
	"TRAP trap",
	"Trace trap",
	"Yellow stack trap",
	"Powerfail trap",
	"Floating point exception",
	"HALT instruction",
	"Breakpoint",
	"Wait state",
	"Trap vector fetch abort",
	"Trap stack push abort"  };

/* Binary loader.

   Loader format consists of blocks, optionally preceded, separated, and
   followed by zeroes.  Each block consists of:

	001		---
	xxx		 |
	lo_count	 |
	hi_count	 |
	lo_origin	 > count bytes
	hi_origin	 |
	data byte	 |
	:		 |
	data byte	---
	checksum

   If the byte count is exactly six, the block is the last on the tape, and
   there is no checksum.  If the origin is not 000001, then the origin is
   the PC at which to start the program.
*/

int sim_load (FILE *fileref)
{
int origin, csum, count, state, i;

state = csum = 0;
while ((i = getc (fileref)) != EOF) {
	csum = csum + i;				/* add into chksum */
	switch (state) {
	case 0:						/* leader */
		if (i == 1) state = 1;
		else csum = 0;
		break;
	case 1:						/* ignore after 001 */
		state = 2;
		break;
	case 2:						/* low count */
		count = i;
		state = 3;
		break;
	case 3:						/* high count */
		count = (i << 8) | count;
		state = 4;
		break;
	case 4:						/* low origin */
		origin = i;
		state = 5;
		break;
	case 5:						/* high origin */
		origin = (i << 8) | origin;
		if (count == 6) {
			if (origin != 1) saved_PC = origin & 0177776;
			return SCPE_OK;  }
		count = count - 6;
		state = 6;
		break;
	case 6:						/* data */
		if (origin >= MEMSIZE) return SCPE_NXM;
		M[origin >> 1] = (origin & 1)?
			(M[origin >> 1] & 0377) | (i << 8):
			(M[origin >> 1] & 0177400) | i;
		count = count - 1;
		state = state + (count == 0);
		break;
	case 7:						/* checksum */
		if (csum & 0377) return SCPE_CSUM;
		csum = state = 0;
		break;  }				/* end switch */
	}						/* end while */
return SCPE_FMT;					/* unexpected eof */
}							/* end sim_loader */
