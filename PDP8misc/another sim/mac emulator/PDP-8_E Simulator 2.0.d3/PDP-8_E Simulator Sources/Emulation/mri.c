/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	mri.c - MRI instructions code for the PDP-8/E
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
 *	Memory Reference Instruction (MRI) module.
 */


#define USE_PDP8_REGISTERS_DIRECTLY	1

#include "PDP8.h"
#include "mri.h"
#include "pdp8defines.h"


/* -------------------------------------------------------------------- */
VOID i0000 (VOID)
{
    AC &= *(base + (IF + INST)) | 010000 ;
    EXECUTION_TIME (26);
}
/* -------------------------------------------------------------------- */
VOID i0200 (VOID)
{
    AC &= *(base + (IF | (PC & 07600) | (INST & 0177))) | 010000 ;
    EXECUTION_TIME (26);
}
/* -------------------------------------------------------------------- */
VOID i0400 (VOID)
{
    AC &= *(base + (DF | *(base + (IF | (INST & 0177))))) | 010000 ;
    EXECUTION_TIME (38);
}
/* -------------------------------------------------------------------- */
VOID i0410 (VOID)
{
    REG UWORD *p = base + (IF | (INST & 0177)) ;
    AC &= *(base + (DF | (*p = (*p + 1) & 07777))) | 010000 ;
    EXECUTION_TIME (40);
}
/* -------------------------------------------------------------------- */
VOID i0600 (VOID)
{
    AC &= *(base + (DF | *(base + (IF | (PC & 07600)
					| (INST & 0177))))) | 010000 ;
    EXECUTION_TIME (38);
}
/* -------------------------------------------------------------------- */
VOID i0610 (VOID)
{
    REG UINT page = PC & 07600 ;
    REG UWORD *p = base + (IF | page | (INST & 0177)) ;
    if (!page && (++*p & 010000))
	*p &= 07777 ;
    AC &= *(base + (DF | *p)) | 010000 ;
    EXECUTION_TIME (page ? 38 : 40);
}
/* -------------------------------------------------------------------- */
VOID i1000 (VOID)
{
    AC = (AC + *(base + (IF | (INST & 0177)))) & 017777 ;
    EXECUTION_TIME (26);
}
/* -------------------------------------------------------------------- */
VOID i1200 (VOID)
{
    AC = (AC + *(base + (IF | (PC & 07600) | (INST & 0177)))) & 017777 ;
    EXECUTION_TIME (26);
}
/* -------------------------------------------------------------------- */
VOID i1400 (VOID)
{
    AC = (AC + *(base + (DF | *(base + (IF | (INST & 0177)))))) & 017777 ;
    EXECUTION_TIME (38);
}
/* -------------------------------------------------------------------- */
VOID i1410 (VOID)
{
    REG UWORD *p = base + (IF | (INST & 0177)) ;
    AC = (AC + *(base + (DF | (*p = (*p + 1) & 07777)))) & 017777 ;
    EXECUTION_TIME (40);
}
/* -------------------------------------------------------------------- */
VOID i1600 (VOID)
{
    AC = (AC + *(base + (DF | *(base + (IF | (PC & 07600)
					| (INST & 0177)))))) & 017777 ;
    EXECUTION_TIME (38);
}
/* -------------------------------------------------------------------- */
VOID i1610 (VOID)
{
    REG UINT page = PC & 07600 ;
    REG UWORD *p = base + (IF | page | (INST & 0177)) ;
    if (!page && (++*p & 010000))
	*p &= 07777 ;
    AC = (AC + *(base + (DF | *p))) & 017777 ;
    EXECUTION_TIME (page ? 38 : 40);
}
/* -------------------------------------------------------------------- */
VOID i2000 (VOID)
{
    REG UWORD *p = base + (IF | (INST & 0177)) ;
    if (!(*p = (*p + 1) & 07777))
	++PC ;
    EXECUTION_TIME (26);
}
unsigned s2000 (VOID)
{
    REG UWORD *p = base + (IF | (base[IF | PC] & 0177));
    /* Don´t use INST but pdp8.mem[pdp8.IF | pdp8.PC] because
       state.currInst is not set up for skip tests */
    return (!((*p + 1) & 07777));
}
/* -------------------------------------------------------------------- */
VOID i2200 (VOID)
{
    REG UWORD *p = base + (IF | (PC & 07600) | (INST & 0177)) ;
    if (!(*p = (*p + 1) & 07777))
	++PC ;
    EXECUTION_TIME (26);
}
unsigned s2200 (VOID)
{
    REG UWORD *p = base + (IF | (PC & 07600) | (base[IF | PC] & 0177)) ;
    return (!((*p + 1) & 07777));
}
/* -------------------------------------------------------------------- */
VOID i2400 (VOID)
{
	REG UWORD addr = base[IF | (INST & 0177)];
	REG UWORD *p = base + (DF | addr);
	REG UWORD *wp = base + (W_DF | addr);

	*wp = (*p + 1) & 07777;
	if (! *wp)	/* MB is checked, no re-read from memory */
		PC++;   
    EXECUTION_TIME (38);
}
unsigned s2400 (VOID)
{
	REG UWORD addr = base[IF | (base[IF | PC] & 0177)];
	REG UWORD *p = base + (DF | addr);

	return (! ((*p + 1) & 07777));
}
/* -------------------------------------------------------------------- */
VOID i2410 (VOID)
{
    REG UWORD *p = base + (IF | (INST & 0177)) ;
    REG UWORD *wp;
    *p = (*p + 1) & 07777 ;
    wp = base + (W_DF | *p);
    p = base + (DF | *p) ;
    *wp = (*p + 1) & 07777;
    if (! *wp)		/* MB is checked, no re-read from memory */
	++PC ;
    EXECUTION_TIME (40);
}
unsigned s2410 (VOID)
{
    REG UWORD *p = base + (IF | (base[IF | PC] & 0177)) ;
    REG UWORD *pp;
    REG UWORD inc;
    
    pp = base + (DF | ((*p + 1) & 07777)) ;
    inc = (pp == p) ? 1 : 0;
    return (! ((*pp + 1 + inc) & 07777));
}
/* -------------------------------------------------------------------- */
VOID i2600 (VOID)
{
    REG UWORD addr = base[IF | (PC & 07600) | (INST & 0177)];
    REG UWORD *p = base + (DF | addr);
    REG UWORD *wp = base + (W_DF | addr);
 
    *wp = (*p + 1) & 07777;
    if (! *wp)		/* MB is checked, no re-read from memory */
	++PC ;
    EXECUTION_TIME (38);
}
unsigned s2600 (VOID)
{
    REG UWORD addr = base[IF | (PC & 07600) | (base[IF | PC] & 0177)];
    REG UWORD *p = base + (DF | addr);
 
    return (! ((*p + 1) & 07777));
}
/* -------------------------------------------------------------------- */
VOID i2610 (VOID)
{
    REG UWORD page = PC & 07600 ;
    REG UWORD *p = base + (IF | page | (INST & 0177)) ;
    REG UWORD *wp;
    if (!page)
	*p = (*p + 1) & 07777 ;
    wp = base + (W_DF | *p);
    p = base + (DF | *p) ;
    *wp = (*p + 1) & 07777;
    if (! *wp)		/* MB is checked, no re-read from memory */
    	++PC;
    EXECUTION_TIME (page ? 38 : 40);
}
unsigned s2610 (VOID)
{
    REG UWORD page = PC & 07600 ;
    REG UWORD *p = base + (IF | page | (base[IF | PC] & 0177)) ;
    REG UWORD *pp;
    REG UWORD inc;
    if (!page) {
	pp = base + (DF | ((*p + 1) & 07777)) ;
	inc = (pp == p) ? 1 : 0;
    } else {
    	pp = base + (DF | *p) ; 
    	inc = 0;
    }
    return (! ((*pp + 1 + inc) & 07777));
}
/* -------------------------------------------------------------------- */
VOID i3000 (VOID)
{
    *(base + (IF | (INST & 0177))) = AC & 07777 ;
    AC &= 010000 ;
    EXECUTION_TIME (26);
}
/* -------------------------------------------------------------------- */
VOID i3200 (VOID)
{
    *(base + (IF | (PC & 07600) | (INST & 0177))) = AC & 07777 ;
    AC &= 010000 ;
    EXECUTION_TIME (26);
}
/* -------------------------------------------------------------------- */
VOID i3400 (VOID)
{
    *(base + (W_DF | *(base + (IF | (INST & 0177))))) = AC & 07777 ;
    AC &= 010000 ;
    EXECUTION_TIME (38);
}
/* -------------------------------------------------------------------- */
VOID i3410 (VOID)
{
    REG UWORD *p = base + (IF | (INST & 0177)) ;
    *p = ++*p & 07777 ;
    *(base + (W_DF | *p)) = AC & 07777 ;
    AC &= 010000 ;
    EXECUTION_TIME (40);
}
/* -------------------------------------------------------------------- */
VOID i3600 (VOID)
{
    *(base + (W_DF | *(base + (IF | (PC & 07600) | (INST & 0177)))))
							= AC & 07777 ;
    AC &= 010000 ;
    EXECUTION_TIME (38);
}
/* -------------------------------------------------------------------- */
VOID i3610 (VOID)
{
    REG UINT page = PC & 07600 ;
    REG UWORD *p = base + (IF | page | (INST & 0177)) ;
    if (!page)
	*p = (*p + 1) & 07777 ;
    *(base + (W_DF | *p)) = AC & 07777 ;
    AC &= 010000 ;
    EXECUTION_TIME (page ? 38 : 40);
}
/* -------------------------------------------------------------------- */
VOID i4000 (VOID)
{
    REG UINT temp = INST & 0177 ;
    *(base + (W_IB | temp)) = (PC + 1) & 07777 ;
    PC = temp ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (26);
}
VOID u4000 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
	PC = INST & 0177;
	EXECUTION_TIME (26);
    } else
	i4000 ();
}
/* -------------------------------------------------------------------- */
VOID i4200 (VOID)
{
    REG UINT temp = (PC & 07600) | (INST & 0177) ;
    *(base + (W_IB | temp)) = (PC + 1) & 07777 ;
    PC = temp ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (26);
}
VOID u4200 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
	PC = (PC & 07600) | (INST & 0177);
	EXECUTION_TIME (26);
    } else
	i4200 ();
}
/* -------------------------------------------------------------------- */
VOID i4400 (VOID)
{
    REG UINT temp = *(base + (IF | (INST & 0177))) ;
    *(base + (W_IB | temp )) = (PC + 1) & 07777 ;
    PC = temp ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (38);
}
VOID u4400 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
	PC = *(base + (IF | (INST & 0177)));
	EXECUTION_TIME (38);
    } else
	i4400 ();
}
/* -------------------------------------------------------------------- */
VOID i4410 (VOID)
{
    REG UWORD *p = base + (IF | (INST & 0177)) ;
    *(base + (W_IB | (*p = (*p + 1) & 07777))) = (PC + 1) & 07777 ;
    PC = *p ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (40);
}
VOID u4410 (VOID)		/* user mode with TSC8-75 */
/* this opcode is not checked by the TSC8.SV hardware diagnostics */
{
    REG UWORD *p;
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
	p = base + (IF | (INST & 0177));
	PC = *p = (*p + 1) & 07777;
	EXECUTION_TIME (40);
    } else
	i4410 ();
}
/* -------------------------------------------------------------------- */
VOID i4600 (VOID)
{
    REG UINT temp = *(base + (IF | (PC & 07600) | (INST & 0177))) ;
    *(base + (W_IB | temp)) = (PC + 1) & 07777 ;
    PC = temp ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (38);
}
VOID u4600 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
	PC = *(base + (IF | (PC & 07600) | (INST & 0177)));
	EXECUTION_TIME (38);
    } else
	i4600 ();
}
/* -------------------------------------------------------------------- */
VOID i4610 (VOID)
{
    REG UINT page = PC & 07600 ;
    REG UWORD *p = base + (IF | page | (INST & 0177)) ;
    if (!page)
	*p = (*p + 1) & 07777 ;
    *(base + (W_IB | *p)) = (PC + 1) & 07777 ;
    PC = *p ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (page ? 38 : 40);
}
VOID u4610 (VOID)		/* user mode with TSC8-75 */
/* the TSC8.SV hardware diagnostics doesn´t check this opcode at the autoincrement memory locations */
{
    REG UINT page;
    REG UWORD *p;
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
	page = PC & 07600;
	p = base + (IF | page | (INST & 0177));
	if (! page)
	    *p = (*p + 1) & 07777;
	PC = *p;
	EXECUTION_TIME (page ? 38 : 40);
    } else
	i4610 ();
}
/* -------------------------------------------------------------------- */
VOID i5000 (VOID)
{
    PC = (INST & 0177) - 1 ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (12);
}
VOID u5000 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
    } 
    i5000 ();
}
/* -------------------------------------------------------------------- */
VOID i5200 (VOID)
{
    PC = ((PC & 07600) | (INST & 0177)) - 1 ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (12);
}
VOID u5200 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
    } 
    i5200 ();
}
/* -------------------------------------------------------------------- */
VOID i5400 (VOID)
{
    PC = *(base + (IF | (INST & 0177))) - 1 ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (24);
}
VOID u5400 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
    }
    i5400 ();
}
/* -------------------------------------------------------------------- */
VOID i5410 (VOID)
{
    REG UWORD *p = base + (IF | (INST & 0177)) ;
    PC = *p ;
    *p = (*p + 1) & 07777 ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (26);
}
VOID u5410 (VOID)		/* user mode with TSC8-75 */
/* this opcode is not checked by the TSC8.SV hardware diagnostics */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
    }
    i5410 ();
}
/* -------------------------------------------------------------------- */
VOID i5600 (VOID)
{
    PC = *(base + (IF | (PC & 07600) | (INST & 0177))) - 1 ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (24);
}
VOID u5600 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
    }
    i5600 ();
}
/* -------------------------------------------------------------------- */
VOID i5610 (VOID)
{
    REG UINT page = PC & 07600 ;
    REG UWORD *p = base + (IF | page | (INST & 0177)) ;
    if (!page)
	*p = (*p + 1) & 07777 ;
    PC = *p - 1 ;
    IF = IB ;
    if (hw.hasKM8Etimesharing)
	UF = UB ;
    int_inh = false ;
    EXECUTION_TIME (page ? 24 : 26);
}
VOID u5610 (VOID)		/* user mode with TSC8-75 */
{
    tsc8.eriot = INST;
    tsc8.ecdf = 0;
    if (int_mask & tsc8.flag) {
	tsc8.ertb = PC;
	io_flags |= tsc8.flag;
    }
    i5610 ();
}
/* -------------------------------------------------------------------- */
