/***********************************************************************
 * $RCSfile: port.cpp,v $		$Revision: 1.25 $
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
 * This module contains the method definitions for ports and its
 * input and output subobjects.
 *
 ***********************************************************************/

#include	<iostream>

#include	"sim.h"			/* For generate_tabs() decl	*/
#include	"port.h"
#include	"comp.h"	/* Component's methods are called here	*/

using std::endl;
using std::cerr;

/*
 * Constructor for a port. Set the name of the port by passing the name to
 * the Connector base class constructor.  Assign the encapsulated external
 * Connector pointer member to the address of the connector passed to the
 * constructor.  External will point to the Connector that feeds the port
 * (if the port is an inport port) or to the Connector that the port feeds
 * (if the port is an output port).
 */
Port::Port(Connector &con, const char *nm) :
	Connector(nm), external(&con)
{ }

/*
 * Method used to get and return a signal from a port which occured at time
 * t. Like the preceding method, this method will recurse until the
 * get_signal() messages is sent to a wire, after which the recursion
 * unfolds.
 */
Signal
Port::get_signal(ckt_time t) const
{
	// Send message to the external feeder to get its signal.
	return external->get_signal(t);
}

/*
 * Method sends a supplied signal to the external world.  This method will
 * recurse until a send_signal() message is sent to a Wire. Since every
 * port connects eventually to a wire, this recursion will eventually
 * terminate. After sending the signal to the outside world, an attempt is
 * made to propagate the signal to any components which are at the same
 * level of hierarchy and are in the fanout list of the port.
 */
void
Port::send_signal(Signal s)
{
	// Send the signal to the external Connector.
	external->send_signal(s);

	// Propagate the signal at the current level of the hierarchy.
	propagate();
}

/*
 * Send a message to the external connector to display all the signals
 * that have travelled along it.
 */
void
Port::show_signals() const
{
	// Instruct the external connector to show its signal list.
	external->show_signals();
}

/*
 * Display the port information.
 */
void
Port::display(ostream &os, int tabs) const
{
	char	*indent = generate_tabs(tabs);

	os << indent << "Port's external connector: "
		<< external->get_name() << endl;
	Connector::display(os, tabs);

	delete [] indent;
}

/*
 * Input constructor. Invoke Port's constructor to do most of the work.
 * Add the component passed in to the fan-out list external connector.
 * Then add the Input port to the linked list of input ports already in the
 * component.
 */
Input::Input(Component &cmp, Connector &con, const char *n) :
	Port(con, n)
{
	// Send the connect message to the external feeding connector.
	// Note: The message fan_out.add() could not be sent directly
	// to the connector, since fan_out is an inaccessible member
	// of Connector con -- this port constructor can only access its
	// own fan-out, not the fan-out of another Connector.
	external->connect(cmp);

	// Add Input port to component's linked list of Input ports
	// pointers.  Note that 'add' takes a constant reference
	// parameter.  Therefore, we cannot pass 'this' explicity.
	cmp.I_List.push_back((Port *)this);
}

/*
 * Method to warn about send_signal() messages being sent to an Input port.
 */
void
Input::send_signal(Signal)
{
	// Output a warning message if an input signals tries to send a signal.
	cerr << "Warning: Cannot send a signal via Input port.\n";
}

/*
 * Output constructor. Invoke Port's constructor to do most of the work.
 * Add the Output port to the linked list of output ports already in the
 * component.
 */
Output::Output(Component &cmp, Connector &con, const char *n) :
	Port(con, n)
{
	// Add Output port to component's linked list of Output ports
	// pointers.  Note that add takes a constant reference
	// parameter.  Therefore, we cannot pass 'this' explicity.
	cmp.O_List.push_back((Port *)this);
}
