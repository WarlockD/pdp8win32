/* File: kk8e.c
   Author: Douglas Jones, Dept. of Comp. Sci., U. of Iowa, Iowa City, IA 52242.
   Date: Mar. 6, 1996
   Language: C (UNIX)
   Purpose: DEC PDP-8/e emulator

   Based on the PDP-8/E Small Computer Handbook,
   Digital Equipment Corporation, 1971, 1973,
   and the PDP-8/E/F/M Maintenance Manual, volumes 1 and 2,
   Digital Equipment Corporation, 1975.
*/

/* First, declare that this is a main program */
#define MAIN

#include <stdio.h>
#include "bus.h"
#include "realtime.h"


/*
#define BRKPNT 07443
*/
/************************************************************/
/* Declarations of machine components not included in bus.h */
/************************************************************/

/* Machine control bits */

int enab_del; /* secondary enable flipflop, used to delay enable operations */


/******************/
/* Initialization */
/******************/

/* Both the reset key on the console and the CAF instruction call this */
clearflags()
{
	/* device specific effects of the reset operation */
	kc8init(); /* front panel */
#ifdef DEBUG
	reset_debug();
#endif
#ifdef KE8E
	ke8einit(); /* eae */
#endif
#ifdef KM8E
	km8einit(); /* mmu */
#endif
#ifdef DK8E
	dk8einit(); /* real-time clock */
#endif
#ifdef KP8E
	kp8einit(); /* power fail */
#endif
#ifdef KL8E
	kl8einit(); /* console TTY */
#endif
#ifdef PC8E
	pc8einit(); /* paper tape reader punch */
#endif
#ifdef CR8F
	cr8finit(); /* card reader */
#endif
#ifdef VC8E
	vc8einit(); /* point plot display */
#endif
#ifdef RX8E
	rx8einit(); /* diskette drive */
#endif
#ifdef TD8E
	td8einit(); /* DECtape drive (by PxG) */
#endif
	link = 000000;
	ac = 00000;
	irq = 0;
	enab = 0;
	enab_del = 0;
}

static closecore(u)
int u;
{
	corename[0] = '\0';
}

static int opencore(u, f)
int u;
char * f;
{
	set_file_name( corename, f );
	return(1);
}

powerup(argc,argv)
/* called only once for power on */
int argc;
char** argv;
{
	/* first, see if there is a specified core image save file,
	   since PDP-8/E machines usually have core memory and tend
	   to remember what was in them as of the previous shutdown

	   A parameter of the form <filename> with no leading dash
	   is interpreted as the name of the core file.  If there is
	   no core file specified, core comes up uninitialized.
	*/

	run = 0; /* by default, the machine comes up halted */

	getargs( argc, argv );

	/* take over the TTY */
	ttyraw();

	/* initialize the real-time simulation mechanisms */
	init_timers();

	/* initialize all devices to their power-on state */

	/* core must be registered first because it's going to be
	   on the console device window at console-power up */
        register_device( opencore, closecore, 0,
                         "CORE", "- saved image of main memory   ",
                         corename );

	kc8power( argc, argv ); /* console */
#ifdef KE8E
	ke8epower(); /* eae */
#endif
#ifdef KE8E
	km8epower(); /* mmu */
#endif
#ifdef DK8E
        dk8epower(); /* real-time clock */
#endif
#ifdef KP8E
	kp8epower(); /* power fail */
#endif
#ifdef KL8E
        kl8epower(); /* console TTY */
#endif
#ifdef PC8E
        pc8epower(); /* paper tape reader punch */
#endif
#ifdef CR8F
        cr8fpower(); /* card reader */
#endif
#ifdef VC8E
        vc8epower(); /* point plot display */
#endif
#ifdef RX8E
	rx8epower(); /* diskette drive */
#endif
#ifdef TD8E
	td8epower(); /* DECtape drive (by PxG) */
#endif

	/* now, with all devices set up, mount devices, as needed */
	if (corename[0] != '\0') { /* there is a core file */
		readcore();
		/* if the machine was running when last stopped, this may
		   set the run flipflop */
	}
	clearflags();
}

powerdown()
/* called only once from the console to exit the emulator */
{
	ttyrestore();
	if (corename[0] != '\0') { /* there is a core file */
		dumpcore(corename);
	}
	close_devices();
	exit(0);
}


/************************/
/* Instruction decoding */
/************************/

/*
/			       _____ _____ _____ _____ 
/  instruction word format: = |_|_|_|_|_|_|_|_|_|_|_|_|
/ 			      | op  |i|z|     adr     |
/
/  Instructions will be decoded using a 5 bit opcode-mode combination,
/  so the following opcode definitions are shifted 2 places left
*/

#define opAND (0 << 2)
#define opTAD (1 << 2)
#define opISZ (2 << 2)
#define opDCA (3 << 2)
#define opJMS (4 << 2)
#define opJMP (5 << 2)
#define opIOT (6 << 2)
#define opOPR (7 << 2)

/* The following definitions give the addressing modes as a 2 bit field
*/

#define DIRECT 0
#define DEFER 2
#define ZERO 0
#define CURRENT 1

/* The following definitions give instruction times in terms of 200 ns ticks
*/
#define shortcycle 6
#define longcycle 7

/* The following definitions give widely used code for addressing
*/

#ifdef KM8E

#define PAGE_ZERO cpma = ((mb & 0177) | ifr)

#define CURRENT_PAGE cpma = ((mb & 0177) | (pc & 07600) | ifr)

#define DEFER_CYCLE {							\
	if ((cpma & 07770) == 00010) { /* indexed */			\
		cpma = ((memory[cpma] = ((memory[cpma] + 1) & 07777))) | dfr;\
		countdown -= longcycle;					\
	} else { /* normal */						\
		cpma = (memory[cpma]) | dfr;				\
		countdown -= shortcycle;				\
	}								\
}
#else

#define PAGE_ZERO cpma = (mb & 0177)

#define CURRENT_PAGE cpma = (mb & 0177) | (pc & 07600)

#define DEFER_CYCLE {							\
	if ((cpma & 07770) == 00010) { /* indexed */			\
		cpma = (memory[cpma] = ((memory[cpma] + 1) & 07777));	\
		countdown -= longcycle;					\
	} else { /* normal */						\
		cpma = memory[cpma];					\
		countdown -= shortcycle;				\
	}								\
}

#endif


/* Emulate the fetch/execute cycle */
main(argc,argv)
int argc;
char **argv;
{
	powerup(argc,argv);
	if (run == 0) {
		kc8halt();
	}

	for (;;) {
		/* setup to fetch from pc */
#ifdef KM8E
		cpma = pc | ifr;
#else
		cpma = pc;
#endif
		/* I/O and console activity happens with CPMA holding PC */
		while (countdown <= 0) { /* handle pending device activity */
			fire_timer();
		}

		/* If an interrupt was requested, PC will change! */
		if ((irq > 0) && (enab_del != 0) && (enab_rtf != 0)) {
			/* an interrupt occurs */
			memory[0] = pc;
			pc = 1;
#ifdef KM8E
			sf = (ifr >> 9) | (dfr >> 12) | (uf << 6);
			ifr = 0;
			ib = 0;
			dfr = 0;
			uf = 0;
			ub = 0;
			cpma = pc | ifr;
#else
			cpma = pc;
#endif
			countdown -= longcycle;
			enab = 0;
		}

		/* this line handles 1 instr delay of interrupt enable */
		enab_del = enab;

#ifdef BRKPNT
		if ( cpma == BRKPNT ) {
			mb = 07402;
		}
		else {
		/* the actual instruction fetch is here */
			mb = memory[cpma];
		}
#else
		/* the actual instruction fetch is here */
		mb = memory[cpma];
#endif
#ifdef DEBUG
		accumulate_debug(cpma,mb);
#endif
		countdown -= shortcycle;

		switch (mb >> 7) { /* note that we decode i and z here */


		case opAND | DIRECT | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			ac = ac & memory[cpma];
			countdown -= longcycle;
			break;

		case opAND | DIRECT | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			ac = ac & memory[cpma];
			countdown -= longcycle;
			break;

		case opAND | DEFER | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			ac = ac & memory[cpma];
			countdown -= longcycle;
			break;

		case opAND | DEFER | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			ac = ac & memory[cpma];
			countdown -= longcycle;
			break;


		case opTAD | DIRECT | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			ac = (ac | link) + memory[cpma];
			link = ac & 010000;
			ac = ac & 007777;
			countdown -= longcycle;
			break;

		case opTAD | DIRECT | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			ac = (ac | link) + memory[cpma];
			link = ac & 010000;
			ac = ac & 007777;
			countdown -= longcycle;
			break;

		case opTAD | DEFER | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			ac = (ac | link) + memory[cpma];
			link = ac & 010000;
			ac = ac & 007777;
			countdown -= longcycle;
			break;

		case opTAD | DEFER | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			ac = (ac | link) + memory[cpma];
			link = ac & 010000;
			ac = ac & 007777;
			countdown -= longcycle;
			break;


		case opISZ | DIRECT | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			mb = memory[cpma] = ((memory[cpma] + 1) & 07777);
			if (mb == 0) {
				pc = (pc + 1) & 07777;
			}
			countdown -= longcycle;
			break;

		case opISZ | DIRECT | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			mb = memory[cpma] = ((memory[cpma] + 1) & 07777);
			if (mb == 0) {
				pc = (pc + 1) & 07777;
			}
			countdown -= longcycle;
			break;

		case opISZ | DEFER | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			mb = memory[cpma] = ((memory[cpma] + 1) & 07777);
			if (mb == 0) {
				pc = (pc + 1) & 07777;
			}
			countdown -= longcycle;
			break;

		case opISZ | DEFER | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			mb = memory[cpma] = ((memory[cpma] + 1) & 07777);
			if (mb == 0) {
				pc = (pc + 1) & 07777;
			}
			countdown -= longcycle;
			break;


		case opDCA | DIRECT | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			memory[cpma] = ac;
			ac = 00000;
			countdown -= longcycle;
			break;

		case opDCA | DIRECT | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			memory[cpma] = ac;
			ac = 00000;
			countdown -= longcycle;
			break;

		case opDCA | DEFER | ZERO:
			PAGE_ZERO;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			memory[cpma] = ac;
			ac = 00000;
			countdown -= longcycle;
			break;

		case opDCA | DEFER | CURRENT:
			CURRENT_PAGE;
			pc = (pc + 1) & 07777;
			DEFER_CYCLE;
			memory[cpma] = ac;
			ac = 00000;
			countdown -= longcycle;
			break;

/* force indirect branching to use the instruction field */
#define dfr ifr

		case opJMS | DIRECT | ZERO:
			PAGE_ZERO;
#ifdef KM8E
			ifr = ib;
			uf = ub;
#endif
			enab_rtf = 1;
			memory[cpma] = (pc + 1) & 07777;
			countdown -= longcycle;
			pc = (cpma + 1) & 07777;
			break;

		case opJMS | DIRECT | CURRENT:
			CURRENT_PAGE;
#ifdef KM8E
			ifr = ib;
			uf = ub;
			cpma = (cpma & 07777) | ifr;
#endif
			enab_rtf = 1;
			memory[cpma] = (pc + 1) & 07777;
			countdown -= longcycle;
			pc = (cpma + 1) & 07777;
			break;

		case opJMS | DEFER | ZERO:
			PAGE_ZERO;
			DEFER_CYCLE;
#ifdef KM8E
			ifr = ib;
			uf = ub;
			cpma = (cpma & 07777) | ifr;
#endif
			enab_rtf = 1;
			memory[cpma] = (pc + 1) & 07777;
			countdown -= longcycle;
			pc = (cpma + 1) & 07777;
			break;

		case opJMS | DEFER | CURRENT:
			CURRENT_PAGE;
			DEFER_CYCLE;
#ifdef KM8E
			ifr = ib;
			uf = ub;
			cpma = (cpma & 07777) | ifr;
#endif
			enab_rtf = 1;
			memory[cpma] = (pc + 1) & 07777;
			countdown -= longcycle;
			pc = (cpma + 1) & 07777;
			break;


		case opJMP | DIRECT | ZERO:
			PAGE_ZERO;
#ifdef KM8E
			ifr = ib;
			uf = ub;
			cpma = (cpma & 07777) | ifr;
#endif
			enab_rtf = 1;
			pc = cpma & 07777;
			break;

		case opJMP | DIRECT | CURRENT:
			CURRENT_PAGE;
#ifdef KM8E
			ifr = ib;
			uf = ub;
			cpma = (cpma & 07777) | ifr;
#endif
			enab_rtf = 1;
			pc = cpma & 07777;
			break;

		case opJMP | DEFER | ZERO:
			PAGE_ZERO;
			DEFER_CYCLE;
#ifdef KM8E
			ifr = ib;
			uf = ub;
			cpma = (cpma & 07777) | ifr;
#endif
			enab_rtf = 1;
			pc = cpma & 07777;
			break;

		case opJMP | DEFER | CURRENT:
			CURRENT_PAGE;
			DEFER_CYCLE;
#ifdef KM8E
			ifr = ib;
			uf = ub;
			cpma = (cpma & 07777) | ifr;
#endif
			enab_rtf = 1;
			pc = cpma & 07777;
			break;

/* undo kluge to force branches to use instruction field */
#undef dfr

		case opIOT | DIRECT | ZERO:
		case opIOT | DIRECT | CURRENT:
		case opIOT | DEFER | ZERO:
		case opIOT | DEFER | CURRENT:

			pc = (pc + 1) & 07777;
#ifdef KM8E
			if (uf == 1) { /* illegal in user mode */
				irq = irq + 1;
				km8e_uif = 1;
				break; /* abort instruction */
			}
#endif
			switch ((mb >> 3) & 077) { /* decode device address */

			case 000:
				switch (mb & 07) { /* decode CPU IOTs */
				case 00: /* SKON */
					if (enab != 0) {
						pc = (pc + 1) & 07777;
					}
					enab = 0;
					enab_del = 0;
					break;
				case 01: /* ION */
					enab = 1;
					break;
				case 02: /* IOF */
					enab = 0;
					enab_del = 0;
					break;
				case 03: /* SRQ */
					if (irq > 0) {
						pc = (pc + 1) & 07777;
					}
					break;
				case 04: /* GTF */
					ac = (link >> 1)       /* bit 0 */
#ifdef KE8E
					   | (gt?)	       /* bit 1 */
#endif
					   | ((irq > 0) << 9)  /* bit 2 */
#ifdef KM8E
					   | (0)	/*?*/  /* bit 3 */
#endif
					   | (enab << 7)       /* bit 4 */
#ifdef KM8E
					   | sf 	       /* bit 5-11 */
#endif
					;
					break;
				case 05: /* RTF */
					link = (ac<<1)& 010000; /* bit 0 */
#ifdef KE8E
					gt = ?			/* bit 1 */
#endif
					/* nothing */		/* bit 2 */
					/* nothing */		/* bit 3 */
					enab = 1;		/* bit 4 */
#ifdef KM8E
					ub = (ac & 00100) >> 6; /* bit 5 */
					ib = (ac & 00070) << 9; /* bit 6-8 */
					dfr = (ac & 00007) << 12;/* bit 9-11 */
#endif
					/* disable interrupts until branch */
					enab_rtf = 0;
					break;
				case 06: /* SGT */
#ifdef KE8E
					if (?) {
						pc = (pc + 1) & 07777;
					}
#endif
					break;
				case 07: /* CAF */
					clearflags();
					break;
				}
				break;

#ifdef PC8E
			case 001:
				pc8edev1(mb & 07);
				break;
			case 002:
				pc8edev2(mb & 07);
				break;
#endif

#ifdef KL8E
			case 003:
				kl8edev3(mb & 07);
				break;
			case 004:
				kl8edev4(mb & 07);
				break;
#endif

#ifdef VC8E
			case 005:
				vc8edev5(mb & 07);
				break;
#endif

#ifdef DK8E
			case 013:
				dk8edev(mb & 07);
				break;
#endif

#ifdef KM8E
			case 020:
			case 021:
			case 022:
			case 023:
			case 024:
			case 025:
			case 026:
			case 027:
				km8edev(mb & 077);
				break;
#endif

#ifdef CR8F
			case 063:
				cr8fdev3(mb & 07);
				break;
			case 067:
				cr8fdev7(mb & 07);
				break;
#endif

#ifdef RX8E
			case 075:
				rx8edev(mb & 07);
				break;
#endif

#ifdef TC08
			case 076:
				tc08dev6(mb & 07);
				break;
			case 077:
				tc08dev7(mb & 07);
				break;
#endif

#ifdef TD8E				/* DECtape (by PxG) */
			case 077:
				td8edev(mb & 07);
				break;
#endif

			default: /* non existant device */
				break;
			}
			break;


		case opOPR | DIRECT | ZERO:	/* group 1, CLA = 0 */
		case opOPR | DIRECT | CURRENT:	/* group 1, CLA = 1 */

			pc = (pc + 1) & 07777;
			switch ((mb >> 4) & 017) { /* decode CLA ... CML here */

			case 000:	/* NOP */
				break;

			case 001:	/*             CML */
				link = link ^ 010000; 
				break;

			case 002:	/*         CMA     */
				ac = ac ^ 007777;
				break;

			case 003:	/*         CMA CML */
				ac = ac ^ 007777;
				link = link ^ 010000; 
				break;

			case 004:	/*     CLL         */
				link = 000000; 
				break;

			case 005:	/*     CLL     CML */
				link = 010000; 
				break;

			case 006:	/*     CLL CMA     */
				ac = ac ^ 007777;
				link = 000000; 
				break;

			case 007:	/*     CLL CMA CML */
				ac = ac ^ 007777;
				link = 010000; 
				break;

			case 010:	/* CLA             */
				ac = 00000;
				break;

			case 011:	/* CLA         CML */
				ac = 00000;
				link = link ^ 010000; 
				break;

			case 012:	/* CLA     CMA     */
				ac = 07777;
				break;

			case 013:	/* CLA     CMA CML */
				ac = 07777;
				link = link ^ 010000; 
				break;

			case 014:	/* CLA CLL         */
				ac = 00000;
				link = 000000;
				break;

			case 015:	/* CLA CLL     CML */
				ac = 00000;
				link = 010000;
				break;

			case 016:	/* CLA CLL CMA     */
				ac = 07777;
				link = 000000;
				break;

			case 017:	/* CLA CLL CMA CML */
				ac = 07777;
				link = 010000;
				break;
			}

			if (mb & 00001) { /* IAC */
				ac = (ac | link) + 1;
				link = ac & 010000;
				ac = ac & 007777;
			}

			switch ((mb >> 1) & 07) { /* decode RAR,RAL,TWO */

			case 00:	/* NOP */
				break;

			case 01:	/*         TWO -- BSW */
				ac = ((ac & 07700) >> 6)
				   | ((ac & 00077) << 6);
				break;

			case 02:	/*     RAL     */
				ac = (ac << 1) | (link >> 12);
				link = ac & 010000;
				ac = ac & 007777;
				break;

			case 03:	/*     RAL TWO */
				ac = (ac << 2) | ((ac | link) >> 11);
				link = ac & 010000;
				ac = ac & 007777;
				break;

			case 04:	/* RAR         */
				ac = ((ac | link) >> 1) | (ac << 12);
				link = ac & 010000;
				ac = ac & 007777;
				break;

			case 05:	/* RAR     TWO */
				ac = ((ac | link) >> 2) | (ac << 11);
				link = ac & 010000;
				ac = ac & 007777;
				break;

			case 06:	/* RAR RAL     */
				/* this uses a data path meant for AND */
				ac = ac & mb;
				break;

			case 07:	/* RAR RAL TWO */
				/* this uses an addressing data path */
				ac = ((pc - 1) & 07600) | (mb & 00177);
				break;

			}

			break;

		case opOPR | DEFER | ZERO:	/* group 2,3 CLA = 0 */
		case opOPR | DEFER | CURRENT:   /* group 2,3 CLA = 1 */

			if ((mb & 00001) == 0) { /* GROUP 2 */

				pc = (pc + 1) & 07777;

				switch ((mb & 00170) >> 3) { /* SMA ... REV */

				case 000: /* NOP */
					break;

				case 001: /*             REV */
					pc = (pc + 1) & 07777;
					break;

				case 002: /*         SNL     */
					if (link) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 003: /*         SNL REV */
					if (link == 0) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 004: /*     SZA         */
					if (ac == 0) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 005: /*     SZA     REV */
					if (ac) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 006: /*     SZA SNL     */
					if ((ac == 0) || link) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 007: /*     SZA SNL REV */
					if (ac && (link == 0)) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 010: /* SMA             */	
					if (ac & 04000) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 011: /* SMA         REV */
					if ((ac & 04000) == 0) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 012: /* SMA     SNL     */
					if ((ac | link) & 014000) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 013: /* SMA     SNL REV */	
					if (((ac | link) & 014000) == 0) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 014: /* SMA SZA         */
					if ((ac & 04000) || (ac == 0)) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 015: /* SMA SZA     REV */
					if (((ac & 004000) == 0) && ac) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 016: /* SMA SZA SNL     */
					if (((ac | link) & 014000)
					 || (ac == 0)) {
						pc = (pc + 1) & 07777;
					}
					break;

				case 017: /* SMA SZA SNL REV */	
					if ((((ac | link) & 014000) == 0)
					 && (ac)) {
						pc = (pc + 1) & 07777;
					}
					break;
				}

				if (mb & 00200) { /* CLA */
					ac = 00000;
				}
#ifdef KM8E
				if ((uf != 0) && ((mb & 00006) != 0)) {
					/* illegal in user mode */
					irq = irq + 1;
					km8e_uif = 1;
					break; /* abort instruction */
				}
#endif
				if (mb & 00004) { /* OSR */
					ac = ac | sr;
				}

				if (mb & 00002) { /* HLT */
					kc8halt();
					countdown = 0;
					run = 0;
				}

			} else { /* GROUP 3 */
				pc = (pc + 1) & 07777;

				if (mb & 00200) { /* CLA */
					ac = 00000;
				}

				if ((mb & 00120) == 00100) { /* MQA */
					ac = mq | ac;
				} else if ((mb & 00120) == 00020) { /* MQL */
					mq = ac;
					ac = 00000;
				} else if ((mb & 00120) == 00120) { /*MQA,MQL*/
					int temp;
					temp = mq;
					mq = ac;
					ac = temp;
				}
#ifdef KE8E
				if (EAEmode == 0) { /* mode A */
					if (mb & 00040) { /* mode A SCA */
						ac |= sc;
					}
					switch ((mb & 00016) >> 1) {
					case 00: /* NOP */
						break;
					case 01: /* SCL */
#ifdef KM8E
						cpma = pc | ifr;
#else
						cpma = pc;
#endif
						mb = memory[cpma];
						pc = (pc + 1) & 07777;
						sc = (~mb) & 00037;
						break;
					case 02: /* MUY */
#ifdef KM8E
						cpma = pc | ifr;
#else
						cpma = pc;
#endif
						mb = memory[cpma];
						pc = (pc + 1) & 07777;
						{
							long int prod = mp * mb;
							mq = prod & 07777;
							ac = (prod>>12) & 07777;
							link = 000000;
						}
						sc = 013;
						break;
					case 03: /* DVI */
#ifdef KM8E
						cpma = pc | ifr;
#else
						cpma = pc;
#endif
						mb = memory[cpma];
						pc = (pc + 1) & 07777;
						if (ac < mb) { /* no overflow */
							long int idend;
							idend = (ac << 12) | mq;
							mq = idend / mb;
							ac = idend % mb;
							link = 000000;
						} else { /* overflow */
							/* --- mq = ?? --- */
							ac = ac - mb;
							/* shift ac-mq-link
							link = 010000;
							/* --- shift ?? --- */
						}
						sc = 014;
						break;
					case 04: /* NMI or SWAB */
						if ((mb & 00060) == 020) {
						    /* SWAB */
						    EAEmode = 1
						} else {
						    /* NMI */
						    long int shift, news;
						    shift = (link | ac)<<12;
						    shift |= mq;
						    sc = 0;
						    do (;;) {
							news = shift << 1;
						        if(!(news & 027777777))
							    break;
							if ( (news^shift)
							   & 040000000 ) break;
							shift = news;
							sc ++;
						    }
						    mq = shift & 07777;
						    shift >>= 12;
						    ac = shift & 07777;
						    link = shift & 010000;
						}
						break;
					case 05: /* SHL */
#ifdef KM8E
						cpma = pc | ifr;
#else
						cpma = pc;
#endif
						mb = memory[cpma];
						pc = (pc + 1) & 07777;
						sc = (~mb) & 00037;
						}
						    long int shift;
						    shift = (link | ac)<<12;
						    shift |= mq;
						    do (;;) {
							shift <<= 1;
							sc ++;sc &= 037;
							if (sc == 0) break;
						    }
						    mq = shift & 07777;
						    shift >>= 12;
						    ac = shift & 07777;
						    link = shift & 010000;
						}
						break;
					case 06: /* ASR */
#ifdef KM8E
						cpma = pc | ifr;
#else
						cpma = pc;
#endif
						mb = memory[cpma];
						pc = (pc + 1) & 07777;
						sc = (~mb) & 00037;
						}
						    long int shift;
						    shift = (ac<<12)|mq;
						    link = (ac<<1)&010000;
						    do (;;) {
							shift=(link|shift)>>1;
							sc ++;sc &= 037;
							if (sc == 0) break;
						    }
						    mq = shift & 07777;
						    shift >>= 12;
						    ac = shift & 07777;
						}
						break;
					case 07: /* LSR */
#ifdef KM8E
						cpma = pc | ifr;
#else
						cpma = pc;
#endif
						mb = memory[cpma];
						pc = (pc + 1) & 07777;
						sc = (~mb) & 00037;
						}
						    long int shift;
						    shift = (ac<<12)|mq;
						    do (;;) {
							shift >>= 1;
							sc ++;sc &= 037;
							if (sc == 0) break;
						    }
						    mq = shift & 07777;
						    shift >>= 12;
						    ac = shift & 07777;
						    link = 0;
						}
						break;
					}
				} else { /* mode B */
				    if ((mb & 00040) == 0) { /* CLASS 1 */
					switch ((mb & 00016) >> 1) {
					case 00: /* NOP */
						break;
					case 01: /* ACS */
						sc = ac & 037;
						break;
					case 02: /* */
						break;
					case 03: /* */
						break;
					case 04: /* */
						break;
					case 05: /* SAM */
						ac = mq - ac;
						break;
					case 06: /* */
						break;
					case 07: /* */
						break;
					}
				    } else { /* CLASS 2 */
					switch ((mb & 00016) >> 1) {
					case 00: /* SCA */
						ac = ac | sc;
						break;
					case 01: /* */
						break;
					case 02: /* */
						break;
					case 03: /* */
						break;
					case 04: /* */
						break;
					case 05: /* */
						break;
					case 06: /* */
						break;
					case 07: /* */
						break;
					}
				    }
				}
#endif
			}
		}
	}
}
