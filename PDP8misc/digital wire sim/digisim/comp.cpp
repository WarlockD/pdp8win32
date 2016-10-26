/***********************************************************************
 * $RCSfile: comp.cpp,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:19:31 $		$Locker:  $
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
 * This module contains all the definitions necesary for the manipulation
 * of abstract components
 *
 ***********************************************************************/

#include	<string.h>
#include	"sim.h"			/* For generate_tabs() decl	*/
#include	"port.h"		/* Req'd by inputs_are_ready()	*/
#include	"signal.h"		/* Req'd by inputs_are_ready()	*/
#include	"comp.h"

/*
 * Method definitions of the abstract base class for Component.
 */

/*
 * Create a component object, initialize the delay time, name, and
 * local time of the object. This constructor is almost always
 * called from the member initialization list of a component
 * derived from Component.
 */
Component::Component(ckt_time t, const char *nm) :
	delay(t), local_time(CKT_TIME_INIT)
{
	name = new char[strlen(nm) + 1];
	(void) strcpy(name, nm);
}

/*
 * Deallocate the storage used to store the name of the component.
 */
Component::~Component()
{
	delete [] name;
}

/*
 * The main method responsible for simulating a component. It's inherited
 * by all components. Basic approach is to continue executing this method
 * while the component has inputs available.  When the component has all
 * of its inputs ready at its given local time, invoke its process() message
 * passing it the time the inputs were discovered to exist (i.e. at time
 * local_time - 1). 
 */
void
Component::simulate()
{

	// Are ALL the inputs ready at the local time of the Component?
	while (inputs_are_ready() == TRUE)
	{
		// If so, then increment local time here.
		// Otherwise, circuits with feedback go on forever.
		local_time++;

		// Send a process method to the component.
		// This may trigger further simulate()
		// messages depending upon the connectivity
		// of the component.
		process(local_time - 1);
	}
}

/*
 * This method determines if the signals which are directed to the input
 * ports of the component are available at the given time.  If they are all
 * ready, return TRUE otherwise return FALSE.
 */
boolean
Component::inputs_are_ready() const
{
	list<Port *>::const_iterator	  p;

	// Scan the linked list of port pointers.
	for (p = I_List.begin(); p != I_List.end(); p++)
	{
		// Get the signal directed to the port at localtime
		Signal s = (*p)->get_signal(local_time);

		// If the signal does not exist at that time then 
		// return FALSE.
		if (s.get_time() == CKT_TIME_NULL && s.get_value() == SIG_NULL)
			return FALSE;
	}

	// Otherwise, all signals are ready.
	return TRUE;
}

/*
 * All nonleaf components should inherit this process() function.  If a
 * component is composed of sub-components, then descend the three
 * dimensional hierarchy, sending simulate() messages to all the
 * component's subcomponents.  The simulate() is sent by the propagate()
 * message of the Connector class.  This method triggers the subcomponents
 * of the enclosing component.  It esentially descends the
 * three-dimensional hierarchy of components.
 */
void
Component::process(ckt_time)
{
	list<Port *>::const_iterator	  p;

	// Scan all the port pointers in the port list
	// and activate all the subcomponents.
	for (p = I_List.begin(); p != I_List.end(); p++)
		(*p)->propagate();
}

/*
 * Display all the output signals.
 */
void
Component::show_outputs() const
{
	list<Port *>::const_iterator	  p;

	for (p = O_List.begin(); p != O_List.end(); p++)
		(*p)->show_signals();
}

/*
 * Display the component's name. 
 */
void
Component::display(ostream &os, int tabs) const
{
	char	*indent = generate_tabs(tabs);

	os << indent << "Component Name: " << name << endl;

	os << indent << "Component's Input Port List:\n";
	display_ports(os, I_List, tabs + 1);
	os << indent << "--- end component's input ports ---\n";

	os << indent << "Component's Output Port List:\n";
	display_ports(os, O_List, tabs + 1);
	os << indent << "--- end component's output ports ---\n";

	delete [] indent;
}

/*
 * Display the ports in the list.
 */
void
Component::display_ports(ostream &os, list<Port *> ports, int tabs) const
{
	list<Port *>::const_iterator	  p;

	char		 *indent = generate_tabs(tabs);
	int		  counter = 0;

	for (p = ports.begin(); p != ports.end(); p++)
	{
		os << indent << "Port #" << ++counter << endl;
		(*p)->display(os, tabs + 1);
	}

	delete [] indent;
}
