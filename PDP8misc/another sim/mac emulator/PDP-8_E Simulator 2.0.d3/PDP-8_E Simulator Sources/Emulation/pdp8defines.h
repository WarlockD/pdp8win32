/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	pdp8defines.h - #defines making the PDP8 class from 
 *			PDP8.h compatible to Bill Haygoods pdp8 
 *			instruction execution routines
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


/* Types */
#define VOID	void
#define REG	register
#define UWORD	unsigned short
#define ULONG	unsigned long
#define LONG	long
#define INT	short
#define UINT	unsigned short
#define BYTE	char
#define UBYTE	unsigned char


/* Attributes of the PDP8 class */
#define base		(pdp8->mem)
#define	SR		(pdp8->SR)
#define AC		(pdp8->AC)
#define PC		(pdp8->PC)
#define SC		(pdp8->SC)
#define MQ		(pdp8->MQ)
#define GTF		(pdp8->GTF)
#define IF		(pdp8->IF)
#define IB		(pdp8->IB)
#define W_IB		(pdp8->W_IB)
#define DF		(pdp8->DF)
#define W_DF		(pdp8->W_DF)
#define UF		(pdp8->UF)
#define UB		(pdp8->UB)
#define SF		(pdp8->SF)
#define int_ena		(pdp8->IENABLE)
#define int_inh		(pdp8->IINHIBIT)
#define delay		(pdp8->IDELAY)
#define io_flags	(pdp8->IOFLAGS)
#define int_mask	(pdp8->IMASK)
#define EAE		(pdp8->eaeMode)
#define tsc8		(pdp8->_tsc8)
#define hw		(pdp8->_hw)
#define INST		(pdp8->_state.currInst)
#define run		(pdp8->_state.running)


/* The 12 bits of a PDP-8 word are numered 0-11 from left to right.
   The following defines reflect this numbering. */
#define	BIT0		04000
#define	BIT1		02000
#define	BIT2		01000
#define	BIT3		00400
#define	BIT4		00200
#define	BIT5		00100
#define	BIT6		00040
#define	BIT7		00020
#define	BIT8		00010
#define	BIT9		00004
#define	BIT10		00002
#define	BIT11		00001
