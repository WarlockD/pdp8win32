/***********************************************************************
 * $RCSfile: rtcomp.cpp,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:28:23 $		$Locker:  $
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
 * Method definitions for a component which is constructed during
 * running as its circuit description is parsed by the Parser class.
 *
 ***********************************************************************/

#include	<string.h>
#include	"port.h"
#include	"sim.h"		/* For CKT_TIME_NULL typedef	*/
#include	"rtcomp.h"
#include	"complib.h"

/*
 * Create a default runtime component with a null transport delay time
 * and a suitable string for its name.
 */
Runtime_Component::Runtime_Component() :
	Component(CKT_TIME_NULL, "Runtime")
{ }

/*
 * Create an input port and connect it to the wire.  The input
 * port and wire should have the same name, since they are
 * electrically identical.
 */
void
Runtime_Component::create_input(Wire *wire, const std::string& name)
{
	Port* port = new Input(*this, *wire, name);
	internal_portlist.emplace(std::make_pair(name, std::unique_ptr<Port>(port)));
}

/*
 * Create an output port and connect it to the wire.  The output
 * port and wire should have the same name, since they are
 * electrically identical.
 */
void
Runtime_Component::create_output(Wire *wire, const std::string&name)
{
	Port* port = new Output(*this, *wire, name);
	internal_portlist.emplace(std::make_pair(name, std::unique_ptr<Port>(port)));
}

/*
 * Add the supplied netlist wire to the list of wires comprising
 * the internal netlists.
 */
void
Runtime_Component::create_internal(Wire *wire)
{
	internal_netlist.emplace(std::make_pair(wire->get_name(),wire));
}

/*
 * Search for the connector within the confines of the component.
 * Must search the internal netlists as well as the input/output ports.
 */
Connector * Runtime_Component::find_connector(const std::string& name)
{
	auto& wire = internal_netlist.at(name);
	if (wire) return wire.get();

	auto& port = internal_portlist.at(name);
	if (port) return wire.get();

	return nullptr;
}

/*
 * Instantiate a component of the type specified by the string 'type',
 * (e.g. "and", "nor", "buffer", etc.) and serial number 'num'
 * and with a list of Input/Output connector names given by 'io'.
 */
Component* Runtime_Component::create_subcmp(const std::string& type, const std::string& num, const std::vector<Connector*>& connect) {
	std::string name = type + " " + num;
	ckt_time		  delay = 1L;
	Component* ret = nullptr;
	if (connect.size() == 3) {
		if (type == "and") ret = new And2(*connect[0], *connect[1], *connect[2], delay, name);
		if (type == "nand") ret = new Nand2(*connect[0], *connect[1], *connect[2], delay, name);
		if (type == "or") ret = new Or2(*connect[0], *connect[1], *connect[2], delay, name);
		if (type == "nor") ret = new Nor2(*connect[0], *connect[1], *connect[2], delay, name);
		if (type == "xor") ret = new Xor2(*connect[0], *connect[1], *connect[2], delay, name);
		if (type == "xnor") ret = new Xnor2(*connect[0], *connect[1], *connect[2], delay, name);
	}
	else if (connect.size() == 2) {
		if (type == "not") ret = new Not(*connect[0], *connect[1], delay, name);
		if (type == "buffer") ret = new Buffer(*connect[0], *connect[1], delay, name);
	}
	else {
		std::cerr << "Unknown component: \"" << type << "\".\n";
	}
	if (ret != nullptr) m_subcomponents.emplace_back(ret);
	return ret;
}

/*
 * Display the component and all its inter-connections.
 */
void Runtime_Component::display(std::ostream &os, int tabs) const
{
	generate_tabs indent(tabs);
	int counter = 0;
	os << indent << "Runtime_Component is a Component\n";
	Component::display(os, tabs + 1);

	os << indent << "Runtime Component's Internal Net List:\n";
	for (auto& wire : internal_netlist) {
		os << indent << "Netlist #" << ++counter << std::endl;
		wire.second->display(os, tabs + 1);
	}
	os << indent << "--- end component's internal netlist ---\n";
}
