/***********************************************************************
 * $RCSfile: signal.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:30:15 $		$Locker:  $
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
 * Class declaration for the signal class.
 *
 ***********************************************************************/

#if !defined(SIGNALS_H_)		/* protect from multiple inclusion */
#  define SIGNALS_H_

#include	<iostream>
#include <ostream>
#include	"sim.h"		/* For ckt_time	typedef			*/

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
friend std::ostream &operator <<(std::ostream &, const Signal &);
public:
	/*
	* Signal constructor. Assign the supplied signal value and signal time to
	* the private members of the signal class. This is the only place where
	* such an assignment can be made.
	*/
	Signal(ckt_time ct = CKT_TIME_INIT, Sig_Val sv= SIG_X) : value(sv), t(ct) {}
	Signal(const Signal &signal) : value(signal.value), t(signal.t) {}
	Signal &operator=(const Signal &sig) { if (&sig != this) { *this = Signal(sig); } return *this; }

	Sig_Val		get_value() const { return value; }	// get the signal's value.
	ckt_time	get_time() const { return t; }	// return the time of signal.

	void		set_value(Sig_Val v) { value = v; }
	bool operator==(const Signal& sig) const { return sig.t == t && sig.value == value; }
	bool operator!=(const Signal& sig) const { return !(sig == *this); }
	bool operator<(const Signal& sig) const { return t < sig.t; }
private:
	Sig_Val		value;		// Value of the signal
	ckt_time	t;		// Time that the signal occurred.
};

struct SignalInterface {
	virtual Signal	get_signal(ckt_time) const = 0;	// Retrieve a signal.
	virtual ~SignalInterface() {}
};
#endif	/* !SIGNALS_H_ */
