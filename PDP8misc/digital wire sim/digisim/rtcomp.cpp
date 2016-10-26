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
Runtime_Component::create_input(Wire *wire, const char *name)
{
	(void) new Input(*this, *wire, name);
}

/*
 * Create an output port and connect it to the wire.  The output
 * port and wire should have the same name, since they are
 * electrically identical.
 */
void
Runtime_Component::create_output(Wire *wire, const char *name)
{
	(void) new Output(*this, *wire, name);
}

/*
 * Add the supplied netlist wire to the list of wires comprising
 * the internal netlists.
 */
void
Runtime_Component::create_internal(Wire *wire)
{
	internal_netlist.push_back(wire);
}

/*
 * Search for the connector within the confines of the component.
 * Must search the internal netlists as well as the input/output ports.
 */
Connector *
Runtime_Component::find_connector(const char *name)
{
	list<Wire *>::const_iterator	wire;

	for (	wire = internal_netlist.begin();
		wire != internal_netlist.end();
		wire++)
	{
		if (strcmp(name, (*wire)->get_name()) == 0)
			return *wire;
	}

	list<Port *>::const_iterator	  port;

	for (port = I_List.begin(); port != I_List.end(); port++)
		if (strcmp(name, (*port)->get_name()) == 0)
			return *port;

	for (port = O_List.begin(); port != O_List.end(); port++)
		if (strcmp(name, (*port)->get_name()) == 0)
			return *port;

	return 0;
}

/*
 * Instantiate a component of the type specified by the string 'type',
 * (e.g. "and", "nor", "buffer", etc.) and serial number 'num'
 * and with a list of Input/Output connector names given by 'io'.
 */
int
Runtime_Component::create_subcmp(const char *type,
				 const char *num,
				 list<char *>io)
{
	list<char *>::const_iterator	  con_name;
	Connector		**connect = new Connector*[io.size()];
	int			  io_num = 0;
	ckt_time		  delay = 1L;

	for (con_name = io.begin(); con_name != io.end(); con_name++)
		connect[io_num++] = find_connector(*con_name);

	char *name = new char [strlen(type) + strlen(num) + 2];
	(void) strcpy(name, type);
	(void) strcat(name, " ");
	(void) strcat(name, num);
	if (strcmp(type, "and") == 0)
	{
		if (io_num != 3)
			return -1;
		(void) new And2(*connect[0], *connect[1],
			        *connect[2], delay, name);
	}
	else if (strcmp(type, "nand") == 0)
	{
		if (io_num != 3)
			return -1;
		(void) new Nand2(*connect[0], *connect[1],
			         *connect[2], delay, name);
	}
	else if (strcmp(type, "or") == 0)
	{
		if (io_num != 3)
			return -1;
		(void) new Or2(*connect[0], *connect[1],
			        *connect[2], delay, name);
	}
	else if (strcmp(type, "nor") == 0)
	{
		if (io_num != 3)
			return -1;
		(void) new Nor2(*connect[0], *connect[1],
			         *connect[2], delay, name);
	}
	else if (strcmp(type, "xor") == 0)
	{
		if (io_num != 3)
			return -1;
		(void) new Xor2(*connect[0], *connect[1],
			        *connect[2], delay, name);
	}
	else if (strcmp(type, "xnor") == 0)
	{
		if (io_num != 3)
			return -1;
		(void) new Xnor2(*connect[0], *connect[1],
			         *connect[2], delay, name);
	}
	else if (strcmp(type, "not") == 0)
	{
		if (io_num != 2)
			return -1;
		(void) new Not(*connect[0], *connect[1], delay, name);
	}
	else if (strcmp(type, "buffer") == 0)
	{
		if (io_num != 2)
			return -1;
		(void) new Buffer(*connect[0], *connect[1], delay, name);
	} else {
		cerr << "Unknown component: \"" << type << "\".\n";
		return -1;
	}

	delete [] connect;

	return 0;
}

/*
 * Display the component and all its inter-connections.
 */
void
Runtime_Component::display(ostream &os, int tabs) const
{
	char	*indent = generate_tabs(tabs);

	os << indent << "Runtime_Component is a Component\n";
	Component::display(os, tabs + 1);

	int counter = 0;

	os << indent << "Runtime Component's Internal Net List:\n";
	list<Wire *>::const_iterator	  wire;
	for (	wire = internal_netlist.begin();
		wire != internal_netlist.end();
		wire++)
	{
		os << indent << "Netlist #" << ++counter << endl;
		(*wire)->display(os, tabs + 1);
	}
	os << indent << "--- end component's internal netlist ---\n";

	delete [] indent;
}
