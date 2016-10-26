/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	iot.c - IOT instructions code for the PDP-8/E
 *
 *	This file is part of PDP-8/E Simulator.
 *
 *	PDP-8/E Simulator is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/*
 *	EMUL-8: a pdp8e emulator.
 *
 *	Author:
 *		Bill Haygood
 *		41832 Ernest Road
 *		Loon Lake, WA 99148-9607
 *		Internet: billh@comtch.iea.com
 *		Voice/AnsMach/FAX \
 *			or	   509-233-2555
 *		  Cellular/Pager  /
 *
 *	Copyright 1992, 1993, 1994 by the author with all rights reserved.
 *
 *	Input / Output Transfer (IOT) module.
 */


#define USE_PDP8_REGISTERS_DIRECTLY	1

#include "PDP8.h"
#include "iot.h"
#include "pdp8defines.h"
#include "PluginAPI.h"		// for CAF for I/O devices


/* -------------------------------------------------------------------- */
VOID iuser (VOID)
{
	io_flags |= userFLAG;
	tsc8.eriot = INST;
	tsc8.ecdf = ((tsc8.eriot & 07707) == 06201);
	EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6000 (VOID)				/* SKON		6000	*/
{
    if (int_ena)
	++PC ;
    int_ena = delay = false ;
    EXECUTION_TIME (12);
}
unsigned s6000 (VOID)				/* SKON		6000	*/
{
	return (int_ena);
}
/* -------------------------------------------------------------------- */
VOID i6001 (VOID)				/* ION		6001	*/
{
    int_ena = delay = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6002 (VOID)				/* IOF		6002	*/
{
    int_ena = delay = false ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6003 (VOID)				/* SRQ		6003	*/
{
    if (io_flags & int_mask)
	++PC ;
    EXECUTION_TIME (12);
}
unsigned s6003 (VOID)				/* SRQ		6003	*/
{
	return (io_flags & int_mask);
}
/* -------------------------------------------------------------------- */
VOID i6004 (VOID)				/* GTF		6004	*/
{
    AC = (AC & 010000) | ((AC & 010000) >> 1)	/* Save the LINK	*/
	| ((EAE == 'B' && GTF) ? BIT1 : 0)	/* Save the GTF		*/
	| ((io_flags & int_mask) ? BIT2 : 0)	/* Save Interrupt Req	*/
	/* | (int_inh << 8) */			/* Save Interrupt Inh	*/
	/* AC(3) should become the inhibit flag, but the PDP-8/E
	   hardware does not implement this - detected by the
	   Extended Memory Control and Timeshare Test MAINDEC-8E-D1HA */
	| (int_ena << 7)			/* Save Interrupt Ena	*/
	| SF ;					/* Save UF, IF, DF	*/
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6005 (VOID)				/* RTF		6005	*/
{
    AC = (AC & 07777) | ((AC & BIT0) << 1) ;	/* Restore LINK	*/
    if (EAE == 'B')
	GTF = (AC & BIT1) >> 10 ;		/* Restore GTF	*/
    SF = AC & 0177 ;				/* Restore SF	*/
    UB = (AC & BIT5) << 6;			/* Restore UB	*/
    W_IB = IB = (AC & 070) << 9 ;		/* Restore IB	*/
    if (IB >= hw.memsize)
    	W_IB = 0100000;
    W_DF = DF = (AC & 07) << 12 ;		/* Restore DF	*/
    if (DF >= hw.memsize)
    	W_DF = 0100000;
    int_ena = delay = int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6006 (VOID)				/* SGT		6006	*/
{
    if (EAE == 'B' && GTF)
	++PC ;
    EXECUTION_TIME (12);
}
unsigned s6006 (VOID)				/* SGT		6006	*/
{
	return (EAE == 'B' && GTF);
}
/* -------------------------------------------------------------------- */
VOID i6007 (VOID)				/* CAF		6007	*/
{
    AC = 0 ;		/* Clear pdp8 registers	*/
    int_ena = int_inh = delay = false ;
    io_flags = false ;	/* no flags, but serial IE's = 1! */
    EAE = 'A' ;		/* Set EAE mode A	*/
    GTF = false ;
    int i;
    for (i = 0; i < PDP8_IOADDRS; i++) {
	if (pdp8->_state.pluginPointer[i])
		[(PDP8Plugin *) pdp8->_state.pluginPointer[i] CAF:i];
    }

#ifdef FPP
    if (FPP)
    {
	io_flags &= ~fppFLAG ;	/* Clear FPP Int flag	*/
	fpp_mode = LEAV ;	/* Interleaved mode	*/
	fpp_run = false ;	/* Stop the FPP		*/
	fpp_pause = false ;	/* ditto		*/
	fpp_data = FP ;		/* Enable FP mode	*/
	fpp_stat = 0 ;		/* Clear FPP status	*/
    }
    rx5command = 0 ;		/* Clear RX50 regs	*/
    rx5curaddr = 0 ;
    rx5block   = 0 ;
    rx5status  = 0 ;
    rx5unit = 0 ;
    rx5lock = false ;
#endif

    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
/*
 *	Field change IOTs.
 */
/* -------------------------------------------------------------------- */
VOID i6201 (VOID)				/* CDF 0	6201	*/
{
    W_DF = DF = 0 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6202 (VOID)				/* CIF 0	6202	*/
{
    W_IB = IB = 0 ;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6203 (VOID)				/* CIF CDF 0	6203	*/
{
    W_IB = IB = W_DF = DF = 0 ;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
/*
 *	Clear User Interrupt.
 */
/* -------------------------------------------------------------------- */
VOID i6204 (VOID)				/* CINT		6204	*/
{
    io_flags &= ~userFLAG ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6211 (VOID)				/* CDF 1	6211	*/
{
    if ((W_DF = DF = 010000) >= hw.memsize)
    	W_DF = 0100000;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6212 (VOID)				/* CIF 1	6212	*/
{
    if ((W_IB = IB = 010000) >= hw.memsize)
    	W_IB = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6213 (VOID)				/* CIF CDF 1	6213	*/
{
    if ((W_IB = IB = W_DF = DF = 010000) >= hw.memsize)
 	W_IB = W_DF = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6214 (VOID)				/* RDF		6214	*/
{
    AC |= DF >> 9 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6221 (VOID)				/* CDF 2	6221	*/
{
    if ((W_DF = DF = 020000) >= hw.memsize)
    	W_DF = 0100000;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6222 (VOID)				/* CIF 2	6222	*/
{
    if ((W_IB = IB = 020000) >= hw.memsize)
    	W_IB = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6223 (VOID)				/* CIF CDF 2	6223	*/
{
    if ((W_IB = IB = W_DF = DF = 020000) >= hw.memsize)
    	W_IB = W_DF = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6224 (VOID)				/* RIF		6224	*/
{
    AC |= IF >> 9 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6231 (VOID)				/* CDF 3	6231	*/
{
    if ((W_DF = DF = 030000) >= hw.memsize)
    	W_DF = 0100000;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6232 (VOID)				/* CIF 3	6232	*/
{
    if ((W_IB = IB = 030000) >= hw.memsize)
    	W_IB = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6233 (VOID)				/* CIF CDF 3	6233	*/
{
    if ((W_IB = IB = W_DF = DF = 030000) >= hw.memsize)
    	W_IB = W_DF = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6234 (VOID)				/* RIB		6234	*/
{
    AC |= SF ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6241 (VOID)				/* CDF 4	6241	*/
{
    if ((W_DF = DF = 040000) >= hw.memsize)
    	W_DF = 0100000;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6242 (VOID)				/* CIF 4	6242	*/
{
    if ((W_IB = IB = 040000) >= hw.memsize)
    	W_IB = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6243 (VOID)				/* CIF CDF 4	6243	*/
{
    if ((W_IB = IB = W_DF = DF = 040000) >= hw.memsize)
    	W_IB = W_DF = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6244 (VOID)				/* RMF		6244	*/
{
    UB = (SF & BIT5) << 6 ;
    if ((W_IB = IB = (SF & 070) << 9) >= hw.memsize)
    	W_IB = 0100000;
    if ((W_DF = DF = (SF & 07) << 12) >= hw.memsize)
    	W_DF = 0100000;
    int_inh = true;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6251 (VOID)				/* CDF 5	6251	*/
{
    if ((W_DF = DF = 050000) >= hw.memsize)
    	W_DF = 0100000;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6252 (VOID)				/* CIF 5	6252	*/
{
    if ((W_IB = IB = 050000) >= hw.memsize)
    	W_IB = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6253 (VOID)				/* CIF CDF 5	6253	*/
{
    if ((W_IB = IB = W_DF = DF = 050000) >= hw.memsize)
    	W_IB = W_DF = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
/*
 *	Skip on User Interrupt.
 */
/* -------------------------------------------------------------------- */
VOID i6254 (VOID)				/* SINT		6254	*/
{
    if (io_flags & userFLAG)
	++PC ;
    EXECUTION_TIME (12);
}
unsigned s6254 (VOID)				/* SINT		6254	*/
{
	return (io_flags & userFLAG);
}
/* -------------------------------------------------------------------- */
VOID i6261 (VOID)				/* CDF 6	6261	*/
{
    if ((W_DF = DF = 060000) >= hw.memsize)
    	W_DF = 0100000;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6262 (VOID)				/* CIF 6	6262	*/
{
    if ((W_IB = IB = 060000) >= hw.memsize)
    	W_IB = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6263 (VOID)				/* CIF CDF 6	6263	*/
{
    if ((W_IB = IB = W_DF = DF = 060000) >= hw.memsize)
    	W_IB = W_DF = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
/*
 *	Clear the User Flag.
 */
/* -------------------------------------------------------------------- */
VOID i6264 (VOID)				/* CUF		6264	*/
{
    UB = 0 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6271 (VOID)				/* CDF 7	6271	*/
{
    if ((W_DF = DF = 070000) >= hw.memsize)
    	W_DF = 0100000;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6272 (VOID)				/* CIF 7	6272	*/
{
    if ((W_IB = IB = 070000) >= hw.memsize)
    	W_IB = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i6273 (VOID)				/* CIF CDF 7	6273	*/
{
    if ((W_IB = IB = W_DF = DF = 070000) >= hw.memsize)
    	W_IB = W_DF = 0100000;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
/*
 *	Set the User Flag.
 */
/* -------------------------------------------------------------------- */
VOID i6274 (VOID)				/* SUF		6274	*/
{
    UB = 1 << 12 ;
    int_inh = true ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
/*
 *	FPP-8/A Floating Point Processor
 */
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6551 (VOID)				/* FPINT	6551	*/
{
    if (io_flags & fppFLAG)
	++PC ;
    EXECUTION_TIME (12);
}
#error "s6551 missing"
#endif
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6552 (VOID)				/* FPICL	6552	*/
{
    io_flags &= ~fppFLAG ;		/* Clear FPP Int flag	*/
    fpp_mode = LEAV ;			/* Interleaved mode	*/
    fpp_run = false ;			/* Stop the FPP		*/
    fpp_pause = false ;			/* ditto		*/
    fpp_data = FP ;			/* Enable FP mode	*/
    fpp_stat = 0 ;			/* Clear FPP status	*/
    EXECUTION_TIME (12);
}
#endif
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6553 (VOID)				/* FPCOM	6553	*/
{
    if (fpp_run == false && !(io_flags & fppFLAG))
    {
	fpp_command = (AC & 07777) | 0100000 ;  /* Insure non-zero	*/
						/* See FPEP below	*/
	fpp_stat &= ~(BIT0 | BIT8 | BIT9) ;	/* Set FPP mode FP	*/
	if (fpp_command & BIT0)
	{
	    fpp_stat |= BIT0 ;			/* Set DP in status	*/
	    fpp_data = DP ;			/* Set DP mode		*/
	}
	int_mask &= ~fppFLAG ;			/* Clear interrupts	*/
	if (fpp_command & BIT3)			/* Enable interrupts ?	*/
	    int_mask |= fppFLAG ;		/* Yes			*/
	fpp_mode = LEAV ;			/* Set interleave mode	*/
	if (fpp_command & BIT8)			/* Lockout mode ?	*/
	{
	    fpp_mode = LOCK ;			/* Yes			*/
	    fpp_stat |= BIT8 ;
	}
    }
    EXECUTION_TIME (12);
}
#endif
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6554 (VOID)				/* FPHLT	6554	*/
{
    if (fpp_run)
    {
	fpp_pause = true ;
	fpp_stat |= BIT2 ;
	if (fpp_stat & BIT10)			/* If FPP paused,	*/
	    fpp_pc = --fpp_pc & 077777 ;	/*	decrement FPC	*/
    }
    EXECUTION_TIME (12);
}
#endif
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6555 (VOID)				/* FPST		6555	*/
{
    if (fpp_run == false && !(io_flags & fppFLAG))
    {
	fpp_aptp = ((fpp_command & 07) << 12) | (AC & 07777) ;
	fpp_pc = ((*(base + fpp_aptp) & 07) << 12)
					+ (*(base + fpp_aptp + 1)) ;
	fpp_opadd = fpp_pc ;		/* OPADD (2-5)		*/
	if (!(fpp_command & BIT5))
			fpp_x0 = ((*(base + fpp_aptp) & 070) << 9)
					+ (*(base + fpp_aptp + 2)) ;
	if (!(fpp_command & BIT6))
			fpp_br = ((*(base + fpp_aptp) & 0700) << 6)
					+ (*(base + fpp_aptp + 3)) ;
	if (!(fpp_command & BIT7))
	{
	    fpp_ac [0] = *(base + fpp_aptp + 5) ;
	    fpp_ac [1] = *(base + fpp_aptp + 6) ;
	    fpp_ac [2] = *(base + fpp_aptp + 7) ;
	    if (fpp_data == EP)
	    {
		fpp_ac [3] = *(base + fpp_aptp + 8) ;
		fpp_ac [4] = *(base + fpp_aptp + 9) ;
		fpp_ac [5] = *(base + fpp_aptp + 10) ;
	    }
	}
	fpp_stat &= ~BIT10 ;			/* Clear FPAUSE in stat	*/
	fpp_run = true ;
	fpp_stat |= BIT11 ;			/* Set RUN in status	*/
	++PC ;
    }
    EXECUTION_TIME (12);
}
#error "s6555 missing"
#endif
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6556 (VOID)				/* FPRST	6556	*/
{
    AC = (AC & 010000) | fpp_stat ;
    EXECUTION_TIME (12);
}
#endif
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6557 (VOID)				/* FPIST	6557	*/
{
    if (io_flags & fppFLAG)
    {
	++PC ;
	AC = (AC & 010000) | fpp_stat ;
	fpp_stat = 0 ;
	io_flags &= ~fppFLAG ;
    }
    EXECUTION_TIME (12);
}
#error "s6557 missing"
#endif
/* -------------------------------------------------------------------- */
#ifdef FPP
VOID i6567 (VOID)				/* FPEP		6567	*/
{
    if ((AC & BIT0) && fpp_run == false && fpp_command)
    {
	fpp_stat &= ~BIT0 ;		/* Remove DP mode	*/
	if (fpp_command & 0100000)
	{
	    fpp_stat |= 4 ;		/* Set EP in status	*/
	    fpp_data = EP ;		/* Set EP mode		*/
	    fpp_command &= 0107777 ;
	}
    }
    AC &= 010000 ;
    EXECUTION_TIME (12);
}
#endif
