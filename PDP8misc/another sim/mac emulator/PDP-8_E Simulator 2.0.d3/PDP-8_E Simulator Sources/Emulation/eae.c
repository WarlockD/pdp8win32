/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	eae.c - EAE instructions code for the PDP-8/E
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
 *	Operate microinstruction (OPR) module to handle EAE instructions.
 *
 *	Questions:
 */


#define USE_PDP8_REGISTERS_DIRECTLY	1

#include "PDP8.h"
#include "eae.h"
#include "pdp8defines.h"


/*
 *	Operate microinstructions
 *
 *		|           |           |           |
 *		|---|---|---|---|---|---|---|---|---|
 *		| 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10| 11|
 *		| 1 |CLA|MQA| 0 |MQL| 0 | 0 | 0 | 1 |	Group III
 *		|---|---|---|---|---|---|---|---|---|
 *	Sequence:     1   2       2
 *
 *		|           |           |           |
 *		|---|---|---|---|---|---|---|---|---|
 *		| 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10| 11|
 *		| 1 |CLA|MQA|SCA|MQL|   |   |   | 1 |	Group III Mode A
 *		|---|---|---|---|---|---|---|---|---|
 *	Sequence:     1   2       2  \____3____/
 *					  V
 *				0 = NOP       4 = NMI
 *				1 = SCL       5 = SHL
 *				2 = MUY       6 = ASR
 *				3 = DVI       7 = LSR
 *
 *		|           |           |           |
 *		|---|---|---|---|---|---|---|---|---|
 *		| 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10| 11|
 *		| 1 |CLA|MQA|   |MQL|   |   |   | 1 |	Group III Mode B
 *		|---|---|---|---|---|---|---|---|---|
 *	Sequence:     1   2  \    2            /
 *			      \_______3_______/
 *				      V
 *				0 = NOP       10 = SCA
 *				1 = ACS       11 = DAD
 *				2 = MUY       12 = DST
 *				3 = DVI       13 = SWBA
 *				4 = NMI       14 = DPSZ
 *				5 = SHL       15 = DPIC (MQL & MQA set)
 *				6 = ASR       16 = DCM  (MQL & MQA set)
 *				7 = LSR       17 = SAM
 *
 *	In the code below, the author refers to DPIC and DCM
 *	instructions without both MQL & MQA bits set as DPIC-
 *	and DCM- respectively (the author wonders whether he
 *	has implemented these odd-balls correctly).
 */
/* -------------------------------------------------------------------- */
VOID i7401 (VOID)				/* NOP	Grp III		*/
{
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7403 (VOID)
{
    if (EAE == 'A')				/* SCL			*/
    {
	if (++PC & 010000)
	    PC &= 07777 ;
	SC = (~(*(base + (IF | PC)))) & 037 ;
	EXECUTION_TIME (26);
    }
    else					/* ACS			*/
    {
	SC = AC & 037 ;
	AC &= 010000 ;
	EXECUTION_TIME (12);
    }
}
/* -------------------------------------------------------------------- */
VOID i7405 (VOID)				/* MUY			*/
{
    REG UWORD *p ;
    REG ULONG temp ;
    if (++PC & 010000)
	PC &= 07777 ;
    p = base + (IF | PC) ;
    if (EAE == 'B')
    {
	if ((PC & 07770) == 010 && (++*p & 010000))
	    *p = 0 ;
	p = base + (DF | *p) ;
        EXECUTION_TIME (86);
    } else
    	EXECUTION_TIME (74);
    temp = ((ULONG) *p * MQ) + (AC & 07777) ;
    AC = temp >> 12 ;	/* no overflow into L can occure */
    MQ = temp & 07777 ;
    SC = 014 ;
}
/* -------------------------------------------------------------------- */
VOID i7407 (VOID)				/* DVI			*/
{
    REG UWORD *p ;

    if (++PC & 010000)
	PC &= 07777 ;
    p = base + (IF | PC) ;
    if (EAE == 'B')
    {
	if ((PC & 07770) == 010 && (++*p & 010000))
	    *p = 0 ;
	p = base + (DF | *p) ;
    }
/*
 *	Note from Bob Supnik at DEC: supnik@human.enet.dec.com
 *	DVI.  If overflow occurs, the MQ is not changed, and the AC
 *	contains the original AC minus the divisor (result of first
 *	subtraction).
 *	Another note from Bob Supnik corrected the above:
 *	On a divide overflow, the action taken by the EAE is as follows:
 *	L = 1
 *	AC = unchanged
 *	MQ = ((MQ << 1) + 1) & 07777
 *	Also, the SC is 01 (normally, at end of divide, SC is 015).
 *	This from the EAE diagnostic.
 */
    AC &= 07777 ;
    if (AC < *p)			/* Normal divide	*/
    {
	REG ULONG temp = ((ULONG) AC << 12) | MQ ;
	MQ = temp / *p ;
	AC = temp - ((ULONG) *p * MQ) ;
	SC = 015 ;
	EXECUTION_TIME (EAE == 'A' ? 74 : 86);
    }
    else				/* Divide overflow	*/
    {
	AC |= 010000 ;
	MQ = ((MQ << 1) + 1) & 07777 ;
	SC = 01 ;
	EXECUTION_TIME (EAE == 'A' ? 26 : 86);
    }
}
/* -------------------------------------------------------------------- */
VOID i7411 (VOID)				/* NMI			*/
{
/*
 *	Note from Bob Supnik at DEC: supnik@human.enet.dec.com
 *	NMI.  The hardware executes a "for" loop with these conditions:
 *	for (sc = 0; (AC<0> == AC<1>) && ((AC<2:11>|MQ) != 0); sc++) {
 *		shift link'ac'mq left 1;  }
 *	If, at the end of normalization, the AC'MQ = 4000'0000
 *	and the mode is B, AC is cleared.
 */
    REG ULONG temp = ((ULONG) AC << 12) | MQ ;

    for (SC = 0 ; (temp & 040000000) == ((temp & 020000000) << 1)
					&& (temp & 017777777) ; SC++)
	temp <<= 1 ;
    if (EAE == 'B' && (temp & 077777777) == 040000000)
	temp &= 0100000000 ;
    AC = (temp >> 12) & 017777 ;
    MQ = temp & 07777 ;
    EXECUTION_TIME (15 + 3 * SC);
}
/* -------------------------------------------------------------------- */
VOID i7413 (VOID)				/* SHL			*/
{
    REG INT count ;
    REG ULONG temp = ((ULONG) AC << 12) | MQ ;
    if (++PC & 010000)
	PC &= 07777 ;
    count = *(base + (IF | PC)) & 037 ;
    EXECUTION_TIME (29 + 3 * count);	/* A: 2.6 + 0.3 * count; B: 2.9 + 0.3 * count */
    if (EAE == 'A')
	count++ ;
    if (count)
	temp <<= count ;
    AC = (temp >> 12) & 017777 ;
    MQ = temp & 07777 ;
    SC = (EAE == 'B') ? 037 : 0 ;

}
/* -------------------------------------------------------------------- */
VOID i7415 (VOID)				/* ASR			*/
{
    REG INT count ;
    REG LONG gtf ;

    AC = ((AC & 04000) << 1) | (AC & 07777) ;
    if (++PC & 010000)
	PC &= 07777 ;
    count = *(base + (IF | PC)) & 037 ;
    EXECUTION_TIME (29 + 3 * count);	/* A: 2.6 + 0.3 * count; B: 2.9 + 0.3 * count */
    if (EAE == 'A')
	count++ ;
    if (count)
    {
	REG LONG temp = ((ULONG) AC << 12) | MQ ;
	if (AC & 04000)
	    temp |= 0xff000000 ;
	gtf = (count < 24) ? (temp & (1l << (count - 1))) : (AC & 04000) ;
	if (AC & 04000)
	{
	    while (count > 8)
	    {
		temp = (temp >> 8) | 0xffff0000 ;
		count -= 8 ;
	    }
	}
	temp >>= count ;
	if (temp & 040000000)
	    temp |= 0100000000 ;
/*
 *	Note from Bob Supnik at DEC: supnik@human.enet.dec.com
 *	ASR, LSR.  If B and count == 0, the GTF is not changed,
 *	rather than cleared.
 */
	if (EAE == 'B')
	    GTF = (gtf) ? true : false ;
	AC = (temp >> 12) & 017777 ;
	MQ = temp & 07777 ;
    }
    SC = (EAE == 'B') ? 037 : 0 ;
}
/* -------------------------------------------------------------------- */
VOID i7417 (VOID)				/* LSR			*/
{
    REG INT count ;
    REG ULONG temp ;

    if (++PC & 010000)
	PC &= 07777 ;
    count = *(base + (IF | PC)) & 037 ;
    EXECUTION_TIME (29 + 3 * count);	/* A: 2.6 + 0.3 * count; B: 2.9 + 0.3 * count */
    if (EAE == 'A')
	count++ ;
    AC &= 07777 ;
    temp = ((ULONG) AC << 12) | MQ ;
    if (EAE == 'B' && count)
	GTF = (temp >> (count - 1)) & 1;
    temp >>= count ;
    AC = (temp >> 12) & 07777 ;
    MQ = temp & 07777 ;
    SC = (EAE == 'B') ? 037 : 0 ;
}
/* -------------------------------------------------------------------- */
VOID i7421 (VOID)				/* MQL			*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7423 (VOID)				/* MQL SCL		*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7403 () ;
}
/* -------------------------------------------------------------------- */
VOID i7425 (VOID)				/* MQL MUY		*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7405 () ;
}
/* -------------------------------------------------------------------- */
VOID i7427 (VOID)				/* MQL DVI		*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7407 () ;
}
/* -------------------------------------------------------------------- */
VOID i7431 (VOID)				/* SWAB			*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    EAE = 'B' ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7433 (VOID)				/* MQL SHL		*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7413 () ;
}
/* -------------------------------------------------------------------- */
VOID i7435 (VOID)				/* MQL ASR		*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7415 () ;
}
/* -------------------------------------------------------------------- */
VOID i7437 (VOID)				/* MQL LSR		*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7417 () ;
}
/* -------------------------------------------------------------------- */
VOID i7441 (VOID)				/* SCA			*/
{
    AC |= SC ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7443 (VOID)
{
    REG UWORD *p ;
    if (++PC & 010000)
	PC &= 07777 ;
    p = base + (IF | PC) ;
    if (EAE == 'A')				/* A: SCA SCL		*/
    {
	AC |= SC ;
	SC = (~*p) & 037 ;
	EXECUTION_TIME (26);
    }
    else					/* B: DAD		*/
    {
	REG ULONG temp ;
	REG UINT addr;
	if ((PC & 07770) == 010 && (++*p & 010000))
	    *p = 0 ;
	addr = *p;
	temp = MQ + *(base + (DF | addr)) ;
	temp += ((ULONG) ((AC & 07777)
			+ *(base + (DF | ((addr+1) & 07777))))) << 12 ;
	AC = temp >> 12 ;
	MQ = temp & 07777 ;
	EXECUTION_TIME (52);
    }
}
/* -------------------------------------------------------------------- */
VOID i7445 (VOID)
{
    if (EAE == 'A')				/* A: SCA MUY		*/
    {
	AC |= SC ;
	i7405 () ;
    }
    else					/* B: DST		*/
    {
	REG UWORD *p ;	
	REG UINT addr;
	if (++PC & 010000)
	    PC &= 07777 ;
	p = base + (IF | PC) ;
	if ((PC & 07770) == 010 && (++*p & 010000))
	    *p = 0 ;
	addr = *p;
	*(base + (W_DF | addr)) = MQ ;
	*(base + (W_DF | ((addr+1) & 07777))) = AC & 07777 ;
 	EXECUTION_TIME (52);
   }
}
/* -------------------------------------------------------------------- */
VOID i7447 (VOID)				/* SWBA			*/
{
    EAE = 'A' ;
    GTF = false ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7451 (VOID)
{
    if (EAE == 'A')				/* A: SCA NMI		*/
    {
	AC |= SC ;
	i7411 () ;
    }
    else					/* B: DPSZ		*/
    {
	if (!((AC & 07777) | MQ))
	    ++PC ;
	EXECUTION_TIME (12);
    }
}
unsigned s7451 (VOID)
{
    if (EAE == 'A')				/* A: SCA NMI		*/
	return (false);
    else					/* B: DPSZ		*/
	return (!((AC & 07777) | MQ));
}
/* -------------------------------------------------------------------- */
VOID i7453 (VOID)
{
    if (EAE == 'A')				/* A: SCA SHL		*/
    {
	AC |= SC ;
	i7413 () ;
    }
    else					/* B: DPIC-		*/
    {
/*
 *	Note from Bob Supnik at DEC: supnik@human.enet.dec.com
 *	DPIC, DCOM without SWP.  The instructions use the swap to
 *	bring the MQ into the AC, perform step 1, swap back,
 *	perform step 2:
 *
 *	<swap AC and MQ>
 *	increment/complement AC, carry out to link
 *	<swap AC and MQ>
 *	add link/complement and add link, carry out to link
 */
	REG ULONG temp = (((ULONG) MQ << 12) | (AC & 07777)) + 1 ;
	AC = temp >> 12 ;
	MQ = temp & 07777 ;
	EXECUTION_TIME (18);	/* time of DPIC */
    }
}
/* -------------------------------------------------------------------- */
VOID i7455 (VOID)
{
    if (EAE == 'A')				/* A: SCA ASR		*/
    {
	AC |= SC ;
	i7415 () ;
    }
    else					/* B: DCM-		*/
    {
/*
 *	Note from Bob Supnik at DEC: supnik@human.enet.dec.com
 *	DPIC, DCOM without SWP.  The instructions use the swap to
 *	bring the MQ into the AC, perform step 1, swap back,
 *	perform step 2:
 *
 *	<swap AC and MQ>
 *	increment/complement AC, carry out to link
 *	<swap AC and MQ>
 *	add link/complement and add link, carry out to link
 */
	REG ULONG temp = - (((ULONG) MQ << 12) | (AC & 07777)) ;
	AC = (temp >> 12) & 017777 ;
	MQ = temp & 07777 ;
	EXECUTION_TIME (18);	/* time of DCM */
    }
}
/* -------------------------------------------------------------------- */
VOID i7457 (VOID)
{
    if (EAE == 'A')				/* A: SCA LSR		*/
    {
	AC |= SC ;
	i7417 () ;
    }
    else					/* B: SAM		*/
    {
/*
 *	SAM code supplied by Bob Supnik at supnik@human.enet.dec.com
 */
	REG INT temp = AC & 07777 ;
	AC = MQ + (temp ^ 07777) + 1 ;
	GTF = (temp <= MQ) ^ ((temp ^ MQ) >> 11) ;
	EXECUTION_TIME (12);
    }
}
/* -------------------------------------------------------------------- */
VOID i7461 (VOID)				/* MQL SCA		*/
{
    MQ = AC & 07777 ;
    AC &= 010000 ;
    AC |= SC ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7463 (VOID)				/* A: MQL SCA SCL	*/
{						/* B: MQL DAD		*/
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7443 () ;
}
/* -------------------------------------------------------------------- */
VOID i7465 (VOID)				/* A: MQL SCA MUY	*/
{						/* B: MQL DST		*/
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7445 () ;
}
/* -------------------------------------------------------------------- */
VOID i7467 (VOID)				/* A: MQL SCA DVI	*/
{						/* B: MQL SWBA (SWBA ignored) */
    MQ = AC & 07777 ;
    AC &= 010000 ;
    if (EAE == 'A') {
    	AC |= SC;
    	i7407 ();
    } else
    	EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7471 (VOID)				/* A: MQL SCA NMI	*/
{						/* B: MQL DPSZ		*/
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7451 () ;
}
/* -------------------------------------------------------------------- */
VOID i7473 (VOID)				/* A: MQL SCA SHL	*/
{						/* B: MQL DPIC- (7453)	*/
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7453 () ;
}
/* -------------------------------------------------------------------- */
VOID i7475 (VOID)				/* A: MQL SCA ASR	*/
{						/* B: MQL DCM- (7455)	*/
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7455 () ;
}
/* -------------------------------------------------------------------- */
VOID i7477 (VOID)				/* A: MQL SCA LSR	*/
{						/* B: MQL SAM		*/
    MQ = AC & 07777 ;
    AC &= 010000 ;
    i7457 () ;
}
/* -------------------------------------------------------------------- */
VOID i7501 (VOID)				/* MQA			*/
{
    AC |= MQ ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7503 (VOID)				/* A: MQA SCL		*/
{						/* B: MQA ACS		*/
    AC |= MQ ;
    i7403 () ;
}
/* -------------------------------------------------------------------- */
VOID i7505 (VOID)				/* MQA MUY		*/
{
    AC |= MQ ;
    i7405 () ;
}
/* -------------------------------------------------------------------- */
VOID i7507 (VOID)				/* MQA DVI		*/
{
    AC |= MQ ;
    i7407 () ;
}
/* -------------------------------------------------------------------- */
VOID i7511 (VOID)				/* MQA NMI		*/
{
    AC |= MQ ;
    i7411 () ;
}
/* -------------------------------------------------------------------- */
VOID i7513 (VOID)				/* MQA SHL		*/
{
    AC |= MQ ;
    i7413 () ;
}
/* -------------------------------------------------------------------- */
VOID i7515 (VOID)				/* MQA ASR		*/
{
    AC |= MQ ;
    i7415 () ;
}
/* -------------------------------------------------------------------- */
VOID i7517 (VOID)				/* MQA LSR		*/
{
    AC |= MQ ;
    i7417 () ;
}
/* -------------------------------------------------------------------- */
VOID i7521 (VOID)				/* SWP			*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7523 (VOID)				/* A: SWP SCL		*/
{						/* B: SWP ACS		*/
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7403 () ;
}
/* -------------------------------------------------------------------- */
VOID i7525 (VOID)				/* SWP MUY		*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7405 () ;
}
/* -------------------------------------------------------------------- */
VOID i7527 (VOID)				/* SWP DVI		*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7407 () ;
}
/* -------------------------------------------------------------------- */
VOID i7531 (VOID)				/* SWP NMI		*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7411 () ;
}
/* -------------------------------------------------------------------- */
VOID i7533 (VOID)				/* SWP SHL		*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7413 () ;
}
/* -------------------------------------------------------------------- */
VOID i7535 (VOID)				/* SWP ASR		*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7415 () ;
}
/* -------------------------------------------------------------------- */
VOID i7537 (VOID)				/* SWP LSR		*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7417 () ;
}
/* -------------------------------------------------------------------- */
VOID i7541 (VOID)				/* MQA SCA		*/
{
    AC |= MQ ;
    AC |= SC ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7543 (VOID)				/* A: MQA SCA SCL	*/
{						/* B: MQA DAD		*/
    AC |= MQ ;
    i7443 () ;
}
/* -------------------------------------------------------------------- */
VOID i7545 (VOID)				/* A: MQA SCA MUY	*/
{						/* B: MQA DST		*/
    AC |= MQ ;
    i7445 () ;
}
/* -------------------------------------------------------------------- */
VOID i7547 (VOID)				/* A: MQA SCA DVI	*/
{						/* B: MQA SWBA (SWBA ignored) */
    AC |= MQ ;
    if (EAE == 'A') {
    	AC |= SC;
    	i7407 ();
    } else
    	EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7551 (VOID)				/* A: MQA SCA NMI	*/
{						/* B: MQA DPSZ		*/
    AC |= MQ ;
    i7451 () ;
}
/* -------------------------------------------------------------------- */
VOID i7553 (VOID)				/* A: MQA SCA SHL	*/
{						/* B: MQA DPIC- (7453)	*/
    AC |= MQ ;
    i7453 () ;
}
/* -------------------------------------------------------------------- */
VOID i7555 (VOID)				/* A: MQA SCA ASR	*/
{						/* B: MQA DCM- (7455)	*/
    AC |= MQ ;
    i7455 () ;
}
/* -------------------------------------------------------------------- */
VOID i7557 (VOID)				/* A: MQA SCA LSR	*/
{						/* B: MQA SAM		*/
    AC |= MQ ;
    i7457 () ;
}
/* -------------------------------------------------------------------- */
VOID i7561 (VOID)				/* SWP SCA		*/
{
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp | SC ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7563 (VOID)				/* A: SWP SCA SCL	*/
{						/* B: SWP DAD		*/
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7443 () ;
}
/* -------------------------------------------------------------------- */
VOID i7565 (VOID)				/* A: SWP SCA MUY	*/
{						/* B: SWP DST		*/
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7445 () ;
}
/* -------------------------------------------------------------------- */
VOID i7567 (VOID)				/* A: SWP SCA DVI	*/
{						/* B: SWP SWBA (SWBA ignored) */
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    if (EAE == 'A') {
    	AC |= SC;
    	i7407 ();
    } else
    	EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7571 (VOID)				/* A: SWP SCA NMI	*/
{						/* B: SWP DPSZ		*/
    REG UINT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7451 () ;
}
/* -------------------------------------------------------------------- */
VOID i7573 (VOID)				/* A: SWP SCA SHL	*/
{						/* B: DPIC		*/
    if (EAE == 'A')
    {
	REG UINT temp = MQ ;
	MQ = AC & 07777 ;
	AC = (AC & 010000) | temp ;
	i7453 () ;
    }
    else
    {
	REG ULONG temp = (((ULONG) (AC & 07777) << 12) | MQ) + 1 ;
	AC = temp >> 12 ;
	MQ = temp & 07777 ;
	EXECUTION_TIME (18);
    }
}
/* -------------------------------------------------------------------- */
VOID i7575 (VOID)				/* A: SWP SCA ASR	*/
{						/* B: DCM		*/
    if (EAE == 'A')
    {
	REG UINT temp = MQ ;
	MQ = AC & 07777 ;
	AC = (AC & 010000) | temp ;
	i7455 () ;
    }
    else
    {
	REG ULONG temp = (((((ULONG) (AC & 07777)) << 12) | MQ)
							^ 077777777) + 1 ;
	AC = (temp >> 12) & 017777 ;
	MQ = temp & 07777 ;
 	EXECUTION_TIME (18);
   }
}
/* -------------------------------------------------------------------- */
VOID i7577 (VOID)				/* A: SWP SCA LSR	*/
{						/* B: SWP SAM		*/
    REG INT temp = MQ ;
    MQ = AC & 07777 ;
    AC = (AC & 010000) | temp ;
    i7457 () ;
}
/* -------------------------------------------------------------------- */
VOID i7601 (VOID)				/* CLA Grp III		*/
{
    AC &= 010000 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7603 (VOID)				/* CLA SCA		*/
{
    AC &= 010000 ;
    i7403 () ;
}
/* -------------------------------------------------------------------- */
VOID i7605 (VOID)				/* CLA MUY		*/
{
    AC &= 010000 ;
    i7405 () ;
}
/* -------------------------------------------------------------------- */
VOID i7607 (VOID)				/* CLA DVI		*/
{
    AC &= 010000 ;
    i7407 () ;
}
/* -------------------------------------------------------------------- */
VOID i7611 (VOID)				/* CLA NMI		*/
{
    AC &= 010000 ;
    i7411 () ;
}
/* -------------------------------------------------------------------- */
VOID i7613 (VOID)				/* CLA SHL		*/
{
    AC &= 010000 ;
    i7413 () ;
}
/* -------------------------------------------------------------------- */
VOID i7615 (VOID)				/* CLA ASR		*/
{
    AC &= 010000 ;
    i7415 () ;
}
/* -------------------------------------------------------------------- */
VOID i7617 (VOID)				/* CLA LSR		*/
{
    AC &= 010000 ;
    i7417 () ;
}
/* -------------------------------------------------------------------- */
VOID i7621 (VOID)				/* CLA MQL		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7623 (VOID)				/* CLA MQL SCL		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    i7403 () ;
}
/* -------------------------------------------------------------------- */
VOID i7625 (VOID)				/* CLA MQL MUY		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    i7405 () ;
}
/* -------------------------------------------------------------------- */
VOID i7627 (VOID)				/* CLA MQL DVI		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    i7407 () ;
}
/* -------------------------------------------------------------------- */
VOID i7631 (VOID)				/* CLA MQL NMI		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    i7411 () ;
}
/* -------------------------------------------------------------------- */
VOID i7633 (VOID)				/* CLA MQL SHL		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    i7413 () ;
}
/* -------------------------------------------------------------------- */
VOID i7635 (VOID)				/* CLA MQL ASR		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    i7415 () ;
}
/* -------------------------------------------------------------------- */
VOID i7637 (VOID)				/* CLA MQL LSR		*/
{
    AC &= 010000 ;
    MQ = 0 ;
    i7417 () ;
}
/* -------------------------------------------------------------------- */
VOID i7641 (VOID)				/* CLA SCA		*/
{
    AC = (AC & 010000) | SC ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7643 (VOID)				/* A: CLA SCA SCL	*/
{						/* B: CLA DAD		*/
    AC &= 010000 ;
    i7443 () ;
}
/* -------------------------------------------------------------------- */
VOID i7645 (VOID)				/* A: CLA SCA MUY	*/
{						/* B: CLA DST		*/
    AC &= 010000 ;
    i7445 () ;
}
/* -------------------------------------------------------------------- */
VOID i7647 (VOID)				/* A: CLA SCA DVI	*/
{						/* B: CLA SWBA (SWBA ignored) */
    AC &= 010000 ;
    if (EAE == 'B') {
    	AC |= SC;
    	i7407 ();
    } else
    	EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7651 (VOID)				/* A: CLA SCA NMI	*/
{						/* B: CLA DPSZ		*/
    AC &= 010000 ;
    i7451 () ;
}
/* -------------------------------------------------------------------- */
VOID i7653 (VOID)				/* A: CLA SCA SHL	*/
{						/* B: CLA DPIC- (7453)	*/
    AC &= 010000 ;
    i7453 () ;
}
/* -------------------------------------------------------------------- */
VOID i7655 (VOID)				/* A: CLA SCA ASR	*/
{						/* B: CLA DCM- (7455)	*/
    AC &= 010000 ;
    i7455 () ;
}
/* -------------------------------------------------------------------- */
VOID i7657 (VOID)				/* A: CLA SCA LSR	*/
{						/* B: CLA SAM		*/
    AC &= 010000 ;
    i7457 () ;
}
/* -------------------------------------------------------------------- */
VOID i7661 (VOID)				/* CLA MQL SCA		*/
{
    AC = (AC & 010000) | SC ;
    MQ = 0 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7663 (VOID)				/* A: CLA MQL SCA SCL	*/
{						/* B: CLA MQL DAD (DLD)	*/
    AC &= 010000 ;
    MQ = 0 ;
    i7443 () ;
}
/* -------------------------------------------------------------------- */
VOID i7665 (VOID)				/* A: CLA MQL SCA MUY	*/
{						/* B: CLA MQL DST (DDZ)	*/
    AC &= 010000 ;
    MQ = 0 ;
    i7445 () ;
}
/* -------------------------------------------------------------------- */
VOID i7667 (VOID)				/* A: CLA MQL SCA DVI	*/
{						/* B: CLA MQL SWBA (SWBA ignored) */
    AC &= 010000 ;
    MQ = 0 ;
    if (EAE == 'A') {
    	AC |= SC;
    	i7407 ();
    } else
    	EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7671 (VOID)				/* A: CLA MQL SCA NMI	*/
{						/* B: CLA MQL DPSZ	*/
    AC &= 010000 ;
    MQ = 0 ;
    i7451 () ;
}
/* -------------------------------------------------------------------- */
VOID i7673 (VOID)				/* A: CLA MQL SCA SHL	*/
{						/* B: CLA MQL DPIC- (7453) */
    AC &= 010000 ;
    MQ = 0 ;
    i7453 () ;
}
/* -------------------------------------------------------------------- */
VOID i7675 (VOID)				/* A: CLA MQL SCA ASR	*/
{						/* B: CLA MQL DCM- (7455) */
	AC &= 010000;
	MQ = 0;
	i7455 ();
}
/* -------------------------------------------------------------------- */
VOID i7677 (VOID)				/* A: CLA MQL SCA LSR	*/
{						/* B: CLA MQL SAM	*/
    AC &= 010000 ;
    MQ = 0 ;
    i7457 () ;
}
/* -------------------------------------------------------------------- */
VOID i7701 (VOID)				/* CLA MQA		*/
{
    AC = (AC & 010000) | MQ ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7703 (VOID)				/* CLA MQA SCL		*/
{
    AC = (AC & 010000) | MQ ;
    i7403 () ;
}
/* -------------------------------------------------------------------- */
VOID i7705 (VOID)				/* CLA MQA MUY		*/
{
    AC = (AC & 010000) | MQ ;
    i7405 () ;
}
/* -------------------------------------------------------------------- */
VOID i7707 (VOID)				/* CLA MQA DVI		*/
{
    AC = (AC & 010000) | MQ ;
    i7407 () ;
}
/* -------------------------------------------------------------------- */
VOID i7711 (VOID)				/* CLA MQA NMI		*/
{
    AC = (AC & 010000) | MQ ;
    i7411 () ;
}
/* -------------------------------------------------------------------- */
VOID i7713 (VOID)				/* CLA MQA SHL		*/
{
    AC = (AC & 010000) | MQ ;
    i7413 () ;
}
/* -------------------------------------------------------------------- */
VOID i7715 (VOID)				/* CLA MQA ASR		*/
{
    AC = (AC & 010000) | MQ ;
    i7415 () ;
}
/* -------------------------------------------------------------------- */
VOID i7717 (VOID)				/* CLA MQA LSR		*/
{
    AC = (AC & 010000) | MQ ;
    i7417 () ;
}
/* -------------------------------------------------------------------- */
VOID i7721 (VOID)				/* CLA SWP		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7723 (VOID)				/* CLA SWP SCL		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7403 () ;
}
/* -------------------------------------------------------------------- */
VOID i7725 (VOID)				/* CLA SWP MUY		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7405 () ;
}
/* -------------------------------------------------------------------- */
VOID i7727 (VOID)				/* CLA SWP DVI		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7407 () ;
}
/* -------------------------------------------------------------------- */
VOID i7731 (VOID)				/* CLA SWP NMI		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7411 () ;
}
/* -------------------------------------------------------------------- */
VOID i7733 (VOID)				/* CLA SWP SHL		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7413 () ;
}
/* -------------------------------------------------------------------- */
VOID i7735 (VOID)				/* CLA SWP ASR		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7415 () ;
}
/* -------------------------------------------------------------------- */
VOID i7737 (VOID)				/* CLA SWP LSR		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7417 () ;
}
/* -------------------------------------------------------------------- */
VOID i7741 (VOID)				/* CLA MQA SCA		*/
{
    AC = (AC & 010000) | MQ | SC ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7743 (VOID)				/* A: CLA MQA SCA SCL	*/
{						/* B: CLA MQA DAD	*/
    AC = (AC & 010000) | MQ ;
    i7443 () ;
}
/* -------------------------------------------------------------------- */
VOID i7745 (VOID)				/* A: CLA MQA SCA MUY	*/
{						/* B: CLA MQA DST	*/
    AC = (AC & 010000) | MQ ;
    i7445 () ;
}
/* -------------------------------------------------------------------- */
VOID i7747 (VOID)				/* A: CLA MQA SCA DVI	*/
{						/* B: CLA MQA SWBA (SWBA ignored) */
    AC = (AC & 010000) | MQ ;
    if (EAE == 'A') {
    	AC |= SC;
    	i7407 ();
    } else
        EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7751 (VOID)				/* A: CLA MQA SCA NMI	*/
{						/* B: CLA MQA DPSZ	*/
    AC = (AC & 010000) | MQ ;
    i7451 () ;
}
/* -------------------------------------------------------------------- */
VOID i7753 (VOID)				/* A: CLA MQA SCA SHL	*/
{						/* B: CLA MQA DPIC- (7453) */
    AC = (AC & 010000) | MQ ;
    i7453 () ;
}
/* -------------------------------------------------------------------- */
VOID i7755 (VOID)				/* A: CLA MQA SCA ASR	*/
{						/* B: CLA MQA DCM- (7455) */
    AC = (AC & 010000) | MQ ;
    i7455 () ;
}
/* -------------------------------------------------------------------- */
VOID i7757 (VOID)				/* A: CLA MQA SCA LSR	*/
{						/* B: CLA MQA SAM	*/
    AC = (AC & 010000) | MQ ;
    i7457 () ;
}
/* -------------------------------------------------------------------- */
VOID i7761 (VOID)				/* CLA SWP SCA		*/
{
    AC = (AC & 010000) | MQ | SC ;
    MQ = 0 ;
    EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7763 (VOID)				/* A: CLA SWP SCA SCL	*/
{						/* B: CLA DAD		*/
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7443 () ;
}
/* -------------------------------------------------------------------- */
VOID i7765 (VOID)				/* A: CLA SWP SCA MUY	*/
{						/* B: CLA DST		*/
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7445 () ;
}
/* -------------------------------------------------------------------- */
VOID i7767 (VOID)				/* A: CLA SWP SCA DVI	*/
{						/* B: CLA SWP SWBA (SWBA ignored) */
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    if (EAE == 'A') {
    	AC |= SC;
    	i7407 ();
    } else
        EXECUTION_TIME (12);
}
/* -------------------------------------------------------------------- */
VOID i7771 (VOID)				/* CLA SWP DPSZ		*/
{
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7451 () ;
}
/* -------------------------------------------------------------------- */
VOID i7773 (VOID)				/* A: CLA SWP SCA SHL	*/
{						/* B: CLA DPIC		*/
    AC &= 010000 ;
    if (EAE == 'A')
    {
	AC |= MQ ;
	MQ = 0 ;
	i7453 () ;
    }
    else
	i7573 () ;
}
/* -------------------------------------------------------------------- */
VOID i7775 (VOID)				/* A: CLA SWP SCA ASR	*/
{						/* B: CLA DCM		*/
    AC &= 010000 ;
    i7575 () ;
}
/* -------------------------------------------------------------------- */
VOID i7777 (VOID)				/* A: CLA SWP SCA LSR	*/
{						/* B: CLA SWP SAM	*/
    AC = (AC & 010000) | MQ ;
    MQ = 0 ;
    i7457 () ;
}
/* -------------------------------------------------------------------- */
