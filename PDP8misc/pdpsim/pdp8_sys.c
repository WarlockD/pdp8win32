/* pdp8_sys.c: PDP-8 simulator interface

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited
*/

#include "pdp8_defs.h"

extern DEVICE cpu_dev;
extern DEVICE ptr_dev, ptp_dev;
extern DEVICE tti_dev, tto_dev;
extern DEVICE clk_dev, lpt_dev;
extern DEVICE rk_dev, rx_dev;
extern DEVICE rf_dev;
extern REG cpu_reg[];
extern unsigned short M[];

/* SCP data structures and interface routines

   sim_name		simulator name string
   sim_PC		pointer to saved PC register descriptor
   sim_devices		array of pointers to simulated devices
   sim_stop_messages	array of pointers to stop messages
   sim_load		binary loader
*/

char sim_name[] = "PDP-8";

REG *sim_PC = &cpu_reg[0];

DEVICE *sim_devices[] = { &cpu_dev,
	&ptr_dev, &ptp_dev, &tti_dev, &tto_dev,
	&clk_dev, &lpt_dev,
	&rk_dev, &rx_dev, &rf_dev, NULL };

char *sim_stop_messages[] = {
	"Unknown error",
	"Unimplemented instruction",
	"HALT instruction",
	"Breakpoint"  };

/* Binary loader

   Loader format consists of a string of 12-bit words (made up from 7 bit
   characters) between leader and trailer (200).  The last word on tape
   is the checksum.  A word with the "link" bit set is a new origin;
   a character > 0200 indicates a change of field.
*/

int sim_load (FILE *fileref)
{
int rubout, word, low, high, origin, csum, field, state, i;

rubout = state = field = origin = csum = 0;
while ((i = getc (fileref)) != EOF) {
	if (rubout) {
		rubout = 0;
		continue;  }
	if (i == 0377) {
		rubout = 1;
		continue;  }
	if (i > 0200) {
		field = (i & 070) << 9;
		continue;  }
	switch (state) {
	case 0:						/* leader */
		if ((i != 0) && (i != 0200)) state = 1;
		high = i;				/* save as high */
		break;
	case 1:						/* low byte */
		low = i;
		state = 2;
		break;
	case 2:						/* high with test */
		word = (high << 6) | low;
		if (i == 0200) {			/* end of tape? */
			if ((csum - word) & 07777) return SCPE_CSUM;
			return SCPE_OK;  }
		csum = csum + low + high;
		if (word >= 010000) origin = word & 07777;
		else {	if ((field | origin) >= MEMSIZE) return SCPE_NXM;
			M[field | origin] = word & 07777;
			origin = (origin + 1) & 07777;  }
		high = i;
		state = 1;
		break;  }				/* end switch */
	}						/* end while */
return SCPE_FMT;					/* eof? error */
}							/* end sim_loader */
