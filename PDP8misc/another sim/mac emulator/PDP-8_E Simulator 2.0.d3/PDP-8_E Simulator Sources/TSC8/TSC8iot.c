/*
 *	PDP-8/E Simulator Source Code
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	TSC8iot.h - IOTs for the TSC8-75 plugin
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
 

#define USE_RK8E_REGISTERS_DIRECTLY 1
#define USE_PDP8_REGISTERS_DIRECTLY 1

#include "TSC8iot.h"
#include "TSC8.h"
#include "PDP8.h"


void i6360 (void)				/* ETDS 6360 */
{
	pdp8->IMASK &= ~pdp8->_tsc8.flag;
	pdp8->IOFLAGS &= ~pdp8->_tsc8.flag;
	EXECUTION_TIME (12);
}


void i6361 (void)				/* ESKP 6361 */
{
	if (pdp8->IOFLAGS & pdp8->_tsc8.flag)
		pdp8->PC++;
	EXECUTION_TIME (12);
}


unsigned s6361 (void)				/* ESKP 6361 skip test */
{
	return pdp8->IOFLAGS & pdp8->_tsc8.flag;
}


void i6362 (void)				/* ECTF 6362 */
{
	pdp8->IOFLAGS &= ~pdp8->_tsc8.flag;
	EXECUTION_TIME (12);
}


void i6363 (void)				/* ECDF 6363 */
{
	pdp8->AC |= (pdp8->_tsc8.eriot >> 3) & 00007;
	if (pdp8->_tsc8.ecdf)
		pdp8->PC++;
	pdp8->_tsc8.ecdf = 0;
	EXECUTION_TIME (12);
}


unsigned s6363 (void)				/* ECDF 6363 skip test */
{
	return pdp8->_tsc8.ecdf;
}


void i6364 (void)				/* ERTB 6364 */
{
	pdp8->AC = (pdp8->AC & 010000) | pdp8->_tsc8.ertb;
	EXECUTION_TIME (12);
}


void i6365 (void)				/* ESME */
{
	if (pdp8->_tsc8.esmeEnabled) {
		if (pdp8->_tsc8.ecdf && ((pdp8->_tsc8.eriot & 070) >> 3) == (pdp8->SF & 07)) {
			pdp8->PC++;
			pdp8->_tsc8.ecdf = 0;
		}
	}
	EXECUTION_TIME (12);
}


unsigned s6365 (void)				/* ESME (skip test) */
{
	unsigned ret = 0;
	if (pdp8->_tsc8.esmeEnabled) {
		if (pdp8->_tsc8.ecdf && ((pdp8->_tsc8.eriot & 070) >> 3) == (pdp8->SF & 07))
			ret = 1;
	}
	return (ret);
}


void i6366 (void)				/* ERIOT 6366 */
{
	pdp8->AC = (pdp8->AC & 010000) | pdp8->_tsc8.eriot;
	EXECUTION_TIME (12);
}


void i6367 (void)				/* ETEN 6367 */
{
	pdp8->IMASK |= pdp8->_tsc8.flag;
	EXECUTION_TIME (12);
}
