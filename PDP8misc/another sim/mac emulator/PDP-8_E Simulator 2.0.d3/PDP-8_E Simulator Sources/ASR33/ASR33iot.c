/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	ASR33iot.c - ASR 33 Teletype IOTs
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


#define USE_ASR33_REGISTERS_DIRECTLY 1
#define USE_PDP8_REGISTERS_DIRECTLY 1

#include "PluginFramework/PDP8.h"

#include "ASR33.h"
#include "ASR33iot.h"


void i6030 (void)				/* KCF		6030	*/
{
	pdp8->IOFLAGS &= ~PLUGIN_POINTER(ASR33)->inflag;
	EXECUTION_TIME (12);
}	


void i6031 (void)				/* KSF		6031	*/
{
	if (pdp8->IOFLAGS & PLUGIN_POINTER(ASR33)->inflag)
		pdp8->PC++;
	EXECUTION_TIME (12);
}


unsigned s6031 (void)				/* KSF		6031	stiptest */
{
	return pdp8->IOFLAGS & PLUGIN_POINTER(ASR33)->inflag;
}


void i6032 (void)				/* KCC		6032	*/
{
	pdp8->IOFLAGS &= ~PLUGIN_POINTER(ASR33)->inflag;
	pdp8->AC &= 010000;
	[PLUGIN_POINTER(ASR33) canContinueInput];
	EXECUTION_TIME (12);
}


void i6034 (void)				/* KRS		6034	*/
{
	pdp8->AC |= PLUGIN_POINTER(ASR33)->KBB;
	EXECUTION_TIME (12);
}


void i6035 (void)				/* KIE		6035	*/
{
	if (pdp8->AC & 1)
		pdp8->IMASK |= PLUGIN_POINTER(ASR33)->inflag | PLUGIN_POINTER(ASR33)->outflag;
	else
		pdp8->IMASK &= ~(PLUGIN_POINTER(ASR33)->inflag | PLUGIN_POINTER(ASR33)->outflag);
	EXECUTION_TIME (12);
}


void i6036 (void)				/* KRB		6036	*/
{
	pdp8->AC = (pdp8->AC & 010000) | PLUGIN_POINTER(ASR33)->KBB;
	pdp8->IOFLAGS &= ~PLUGIN_POINTER(ASR33)->inflag;
	[PLUGIN_POINTER(ASR33) canContinueInput];
	EXECUTION_TIME (12);
}


void i6040 (void)				/* TFL		6040	*/
{
	pdp8->IOFLAGS |= PLUGIN_POINTER(ASR33)->outflag;
	EXECUTION_TIME (12);
}


void i6041 (void)				/* TSF		6041	*/
{
	if (pdp8->IOFLAGS & PLUGIN_POINTER(ASR33)->outflag)
		pdp8->PC++;
	EXECUTION_TIME (12);
}


unsigned s6041 (void)				/* TSF		6041	skiptest */
{
	return pdp8->IOFLAGS & PLUGIN_POINTER(ASR33)->outflag;
}


void i6042 (void)				/* TCF		6042	*/
{
	pdp8->IOFLAGS &= ~PLUGIN_POINTER(ASR33)->outflag;
	EXECUTION_TIME (12);
}


void i6044 (void)				/* TPC		6044	*/
{
	PLUGIN_POINTER(ASR33)->output = (pdp8->AC & 0377);
	[PLUGIN_POINTER(ASR33) canContinueOutput];
	EXECUTION_TIME (12);
}


void i6045 (void)				/* TSK		6045	*/
{

	if (pdp8->IOFLAGS & pdp8->IMASK & (PLUGIN_POINTER(ASR33)->inflag | PLUGIN_POINTER(ASR33)->outflag))
		pdp8->PC++;
	EXECUTION_TIME (12);
}


unsigned s6045 (void)				/* TSK		6045	skiptest */
{
	return pdp8->IOFLAGS & pdp8->IMASK & (PLUGIN_POINTER(ASR33)->inflag | PLUGIN_POINTER(ASR33)->outflag);
}


void i6046 (void)				/* TLS		6046	*/
{
	PLUGIN_POINTER(ASR33)->output = (pdp8->AC & 0377);
	pdp8->IOFLAGS &= ~PLUGIN_POINTER(ASR33)->outflag;
	[PLUGIN_POINTER(ASR33) canContinueOutput];
	EXECUTION_TIME (12);
}
