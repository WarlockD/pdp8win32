/***********************************************************************
 * $RCSfile: connect.cpp,v $		$Revision: 1.25 $
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
 * This module contains the method definitions for the abstract
 * Connector class.
 *
 ***********************************************************************/

#include	<list>
#include	<iostream>

#include	"sim.h"			/* For generate_tabs() decl	*/
#include	"comp.h"		/* For propagate() & display()	*/
#include	"connect.h"

using std::list;
using std::endl;

/*
 * Method definitions for the Connector class.  Connectors are responsible
 * for connecting components with one another (Wires) and the outside world
 * (Ports). It is through connectors that the transmission of signals
 * occurs.
 */

/*
 * Trivial Connector constructor. Assign name passed
 * in to the name of the Connector.
 */
Connector::Connector(const char *nm)
{
	name = new char[strlen(nm) + 1];
	(void) strcpy(name, nm);
}

/*
 * Deallocate the storage used to store the name of the connector.
 */
Connector::~Connector()
{
	delete [] name;
}

/*
 * Return a constant pointer to the name.
 */
const char *
Connector::get_name() const
{
	return name;
}

/*
 * When a Connector object receives a connect message, it adds the
 * component pointer passed in to the fan-out of the connector.  This
 * method is used exclusively by the Port constructor which is passed a
 * pointer to the component via the CONNECT macro. This component is added
 * to the fan-out of the connector passed into the Port constructor.
 */
void Connector::connect(Component &cmp)
{
	// Add the component pointer to the fan-out list.
	fan_out.push_back(&cmp);
}

/*
 * Inform each of the component's in the connector's fan-out list
 * to simulate, thereby propagating signals into the circuit.
 */
void
Connector::propagate() const
{
	list<Component *>::const_iterator	c;

	// Scan all the elements in the component pointer list.
	for (c = fan_out.begin(); c != fan_out.end(); c++)
	{
		// Send simulate() messages to each component in the list.
		(*c)->simulate();
	}
}

/*
 * Display the connector name and its fan-out.
 */
void
Connector::display(ostream &os, int tabs) const
{
	char	*indent = generate_tabs(tabs);

	os << indent << "Connector Name: " << name << endl;

	os << indent << "Connector Fanout List:\n";
	list<Component *>::const_iterator	  cmp;
	for (cmp = fan_out.begin(); cmp != fan_out.end(); cmp++)
		(*cmp)->display(os, tabs + 1);

	os << indent << "--- end fanout list ---\n";

	delete [] indent;
}
