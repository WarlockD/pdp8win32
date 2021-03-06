/***********************************************************************
 * $RCSfile: signal.cpp,v $		$Revision: 1.24 $
 *
 * $Date: 2004/07/15 16:19:10 $		$Locker:  $
 *
 * --------------------------------------------------------------------
 * DigiTcl 0.3.2 - An Elementary Digital Simulator 
 * (C) 1995-2004 Donald C. Craig (donald@cs.mun.ca)
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
 * Signal constructor. Assign the supplied signal value and signal time to
 * the private members of the signal class. This is the only place where
 * such an assignment can be made.
 */
Signal::Signal(ckt_time ct, Sig_Val sv) :
	value(sv), t(ct)	
{ }

Signal::Signal(const Signal &sig)
{
	value = sig.value;
	t = sig.t;
}

Signal &
Signal::operator=(const Signal &sig)
{
	if (&sig == this)
		return *this;

	value = sig.value;
	t = sig.t;
	return *this;
}

/*
 * User defined conversion. Used to automatically convert a signal object
 * into its signal value.  This method is very useful in the process()
 * methods of components.
 */
Sig_Val
Signal::get_value() const
{
	// Simply return the signal value.
	return value;
}

/*
 * Access method for getting the time of the signal.  Could not be
 * implemented as a user defined conversion because its return value (long)
 * clashes with the return value of operator Sig_Val() (int).
 */
ckt_time
Signal::get_time() const
{
	// Simply return the signal time.
	return t;
}

/*
 * Modify the signal value.  This method is required as zero-delay
 * components with feed back stabilize their outputs.
 */
void
Signal::set_value(Sig_Val sv)
{
	value = sv;
}

/*
 * Overloaded << operator. This enables signals to be output just like any
 * other built in type in C++. Somewhat long but very straightforward.
 */
ostream &
operator <<(ostream &os, const Signal &s)
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
