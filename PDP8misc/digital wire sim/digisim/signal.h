/***********************************************************************
 * $RCSfile: signal.h,v $		$Revision: 1.25 $
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
 * Class declaration for the signal class.
 *
 ***********************************************************************/

#if !defined(SIGNALS_H_)		/* protect from multiple inclusion */
#  define SIGNALS_H_

#include	<iostream>
#include	"sim.h"		/* For ckt_time	typedef			*/

using std::ostream;

enum Sig_Val {SIG_LOW, SIG_HIGH, SIG_X, SIG_NULL};

/*
 * Signal class declaration. Every signal in this implementation is
 * comprised of two entities: a signals value (e.g. SIG_HIGH, SIG_LOW,
 * SIG_X ...) and a time at which the signal occurred during the
 * simulation. The wire class is responsible for handling the signals. The
 * Signal class is essentially a write once/read many structure.  with the
 * only write being done once by the constructor.  Once a signal is set, it
 * cannot be changed, either accidentally or intentionally.
 */
class Signal
{
// Overload << operator for intuitive output of signals.
friend ostream &operator <<(ostream &, const Signal &);
public:
	Signal(ckt_time = CKT_TIME_INIT, Sig_Val = SIG_X);
	Signal(const Signal &signal);
	Signal &operator=(const Signal &sig);

	Sig_Val		get_value() const;	// get the signal's value.
	ckt_time	get_time() const;	// return the time of signal.

	void		set_value(Sig_Val);
private:
	Sig_Val		value;		// Value of the signal
	ckt_time	t;		// Time that the signal occurred.
};

#endif	/* !SIGNALS_H_ */
