/***********************************************************************
 * $RCSfile: wire.cpp,v $		$Revision: 1.25 $
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
 * This module contains the method definitions for wire objects.
 *
 ***********************************************************************/

#include	<iostream>
#include	<list>

#include	"sim.h"			/* For generate_tabs() decl	*/
#include	"wire.h"

using std::cerr;
using std::cout;
using std::endl;
using std::list;

/*
 * Trivial wire constructor. Pass the name to the base Connector constructor
 * and initialize the Signal list by adding an unknown input at the initial
 * time to the list.
 */
Wire::Wire(const char *nm) :
	Connector(nm)
{ 
	// Add initial signal.
	add_signal(Signal(CKT_TIME_INIT, SIG_X));
}

/*
 * Wire constructor used to add a series of signals to a wire. Useful for
 * placing a set of initial signals on, say, an input wire for circuit
 * testing. A wire constructor should also be provided which reads these
 * signals from an input file.
 */
Wire::Wire(Signal s[], int num, char *nm) :
	Connector(nm)
{
	// Add initial signal.
	add_signal(Signal(CKT_TIME_INIT, SIG_X));

	// Add signals which are in the signal array to the wire.
	for (int i = 0; i < num; i++)
		add_signal(s[i]);
}

/*
 * Add the specified signal to the wire.  This function should be
 * called to put initial signals on an input wire -- the signals
 * will not actually be propagated until the simulation begins.
 */
void
Wire::add_signal(Signal sig)
{
	// If the signal is out of order with the last signal on the list,
	// display a warning.
	//
	if (! signals.empty())
	{
		const Signal	last = signals.back();

		if (last.get_time() > sig.get_time())
		{
			cerr << "Warning: Signal out of order: "
				<< last.get_time() << " > "
				<< sig.get_time() << endl;
			return;
		}
		else if (last.get_time() == sig.get_time())
		{
			cerr << "Signal clash at time:" << last.get_time()
				<< endl;
			replace(sig);
		}
	}

	// Otherwise, append the signal to the signal list.
	signals.push_back(sig);
}

/*
 * Method used to find a signal in the wire's encapsulated signal list.  If
 * the signal is not found, an undefined signal (undefined value, undefine
 * time) is returned.
 */
Signal
Wire::get_signal(ckt_time t) const
{
	// If the signal list is empty or if the time we are looking for
	// is greater than the time the last signal came into the wire,
	// then assume an undefined signal.  We should also inform the
	// requesting component to not update its local time until it can
	// be verfied that this assumption is correct.  (For now, we return
	// an undefined signal until we can find a way to inform the
	// component to not update its local time).
	if (signals.empty() || signals.back().get_time() < t)
		return Signal(CKT_TIME_NULL, SIG_NULL);

	list<Signal>::const_iterator	 s (signals.begin());
	Signal	found;

	// Otherwise, start the scan of the signal linked list.
	while (s != signals.end() && (*s).get_time() <= t)
		found = *s++;
	return found;
}

/*
 * Method used to send an add() message to the encapsulated signal list of
 * Wire. After adding the signal to the wire, a message is sent to the
 * fan-out of the wire (inherited from Connector) to propagate the signal
 * as far as possible.
 */
void
Wire::send_signal(Signal sig)
{
	add_signal(sig);
	propagate();
}

/*
 * Display all the signals in the wire.  The output of this method
 * is suitable for parsing by another application.
 */
void
Wire::show_signals() const
{
	cout << "output:" << endl << "\tid: " << get_name() << endl;
	cout << "\tvalues: ";

	// Dump the signals on the wire.
	display_signals();
	cout << endl;
}

/*
 * This method is used to show the user signals (value and time) on the
 * wire.  In addition, information pertaining to the wire's connector
 * information is also displayed.
 */
void
Wire::display(ostream &os, int tabs) const
{
	char	*indent = generate_tabs(tabs);

	// Output the name of the wire
	os << indent << "Wire values: (";

	// Output the signal values in the wire.
	display_signals();
	os << ")" << endl;

	// Dump the connector information.
	Connector::display(os, tabs + 1);

	delete [] indent;
}

/* 
 * Replace a signal in the wire's signal list.  The time of the signal to
 # replace and the (possibly) new value is given by sig.
 */
void
Wire::replace(Signal sig)
{
	list<Signal>::iterator	s;

	for (s = signals.begin(); s != signals.end(); s++)
		if ((*s).get_time() == sig.get_time())
			(*s).set_value(sig.get_value());
}

/*
 * Method used to loop through all the signals in the encapsulated signal
 * list, displaying them one by one.  Output of the signals is of the
 * form {0 1} {5 X} {6 1} {9 0} ... (i.e. {time1 value1} {time2 value2} ...).
 * Only the deltas are reported.
 */
void
Wire::display_signals() const
{
	list<Signal>::const_iterator	 sig;
	Sig_Val			 prev_val = SIG_X;
	int			 num = signals.size();
	boolean			 first = TRUE;

	// Scan all the signals in the list.
	// Dump the signals on the wire.
	for (sig = signals.begin(); sig != signals.end(); sig++)
	{
		num --;
		if (first || (*sig).get_value() != prev_val || num == 0)
		{
			cout << *sig;
			prev_val = (*sig).get_value();
		}
		first = FALSE;
	}
}
