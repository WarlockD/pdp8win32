/***********************************************************************
 * $RCSfile: signal.cpp,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:29:21 $		$Locker:  $
 *
 * --------------------------------------------------------------------
 * DigiTcl 0.3.0 - An Elementary Digital Simulator 
 * (C) 1995-1998 Donald C. Craig (donald@cs.mun.ca)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * --------------------------------------------------------------------
 *
 * This module contains all the methods for signal manipulation.
 *
 ***********************************************************************/

#include	"signal.h"

/*
 * Method definitions for Signal class.
 */



/*
 * Overloaded << operator. This enables signals to be output just like any
 * other built in type in C++. Somewhat long but very straightforward.
 */
std::ostream &
operator <<(std::ostream &os, const Signal &s)
{
	os << "{";

	// Output special times as strings instead of numbers.
	switch (s.t)
	{
		case CKT_TIME_INIT:
			os << "_";
			break;
		case CKT_TIME_NULL:
			os << "?";
			break;
		default:
			// Get output times to line up nicely.
			os << s.t;
			break;
	}

	os << " ";

	// Output Sig_Val enumerated type.
	switch (s.value)
	{
		case SIG_LOW:
			os << "0";
			break;
		case SIG_HIGH:
			os << "1";
			break;
		case SIG_X:
			os << "X";
			break;
		case SIG_NULL:
			os << "?";
			break;
		default:
			os << "BAD SIGNAL!";
			break;
	}

	// Return ostream in case <<'s are used in succession.
	return os << "} ";
}
