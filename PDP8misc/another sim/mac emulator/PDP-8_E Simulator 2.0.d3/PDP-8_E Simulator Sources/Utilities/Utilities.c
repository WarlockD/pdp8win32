/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Utilities.c - Some general utilities and macros, esp. for Tiger compatibility
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


#include "Utilities.h"


static mach_timebase_info_data_t timebaseInfo;
	
	
uint64_t nanoseconds2absolute (uint64_t nanoseconds)	// see Technical Q&A QA1398
{
	if (timebaseInfo.denom == 0)
		mach_timebase_info (&timebaseInfo);
	return nanoseconds * timebaseInfo.denom / timebaseInfo.numer;
}


uint64_t absolute2nanoseconds (uint64_t absolute)	// see Technical Q&A QA1398
{
	if (timebaseInfo.denom == 0)
		mach_timebase_info (&timebaseInfo);
	return absolute * timebaseInfo.numer / timebaseInfo.denom;
}
