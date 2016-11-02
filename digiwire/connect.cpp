/***********************************************************************
 * $RCSfile: connect.cpp,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:24:39 $		$Locker:  $
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
 * This module contains the method definitions for the abstract
 * Connector class.
 *
 ***********************************************************************/

#include	<string.h>
#include	"sim.h"			/* For generate_tabs() decl	*/
#include	"comp.h"		/* For propagate() & display()	*/
#include	"connect.h"

namespace std {
	const std::hash<std::string> std::hash<Connector>::shash;
};


/*
 * Method definitions for the Connector class.  Connectors are responsible
 * for connecting components with one another (Wires) and the outside world
 * (Ports). It is through connectors that the transmission of signals
 * occurs.
 */


/*
 * When a Connector object receives a connect message, it adds the
 * component pointer passed in to the fan-out of the connector.  This
 * method is used exclusively by the Port constructor which is passed a
 * pointer to the component via the CONNECT macro. This component is added
 * to the fan-out of the connector passed into the Port constructor.
 */


/*
 * Inform each of the component's in the connector's fan-out list
 * to simulate, thereby propagating signals into the circuit.
 */
void
Connector::propagate() const
{
	// Send simulate() messages to each component in the list.
	for (auto& c : m_fan_out) c->simulate();
}

/*
 * Display the connector name and its fan-out.
 */
void
Connector::display(std::ostream &os, int tabs) const
{
	generate_tabs indent(tabs);

	os << indent << "Connector Name: " << m_name << std::endl;

	os << indent << "Connector Fanout List:\n";
	for(auto& cmp : m_fan_out) cmp->display(os, tabs + 1);

	os << indent << "--- end fanout list ---\n";

}
