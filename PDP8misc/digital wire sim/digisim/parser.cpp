/***********************************************************************
 * $RCSfile: parser.cpp,v $		$Revision: 1.25 $
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
 * Elementary methods for reading lines from standard input and parsing
 * the words within each line.  Also provides primitive support for
 * ungetting lines, error handling and user-defined type conversion.
 *
 * As the circuit description is parsed, the internal circuit representation
 * is constructed.
 *
 ***********************************************************************/

#include	<iostream>
#include	<list>
#include	<sstream>

#include	"wire.h"
#include	"rtcomp.h"
#include	"parser.h"
#include	"signal.h"

using std::cin;
using std::cerr;
using std::endl;
using std::list;
using std::istringstream;

Parser::Parser(int bufsize) :
	line_size(bufsize), line_num(0), tmp_buf(0), cached(0), read_one(0)
{
	buffer = new char[line_size + 1];
}

Parser::~Parser()
{
	delete [] buffer;
	delete [] tmp_buf;
}

/*
 * Read a signal line from standard input or restore the cached line
 * (if one is available).  Return TRUE if a line was successfully
 * obtained; FALSE otherwise.
 */
int
Parser::get_line()
{
	if (cached || cin.getline(buffer, line_size))
	{
		if (cached)
			cached = FALSE;
		else
			++ line_num;
		read_one = TRUE;
		delete [] tmp_buf;
		tmp_buf = 0;
		return TRUE;
	}
	return FALSE;
}

/*
 * Let the user put the currently read line 'back into the buffer'.
 * Note that this can only be done if at least one line has already
 * been read from the input.
 */
void
Parser::unget_line()
{
	if (read_one && ! cached)
		cached = TRUE;
}

/*
 * Dump a parser error on standard error indicating which line in 
 * standard input the error occurred.
 */
void
Parser::error(char *errstring) const
{
	cerr << errstring << "\n\tline " << line_num << ": " << buffer << endl;
}

/*
 * Extract the next word from the current line.  The calling function
 * has the option of supplying the word delimiters -- by default, " "
 * is used as the delimiter.  Return a pointer to the character string
 * representing the next word, or 0 if no next word exists.
 *
 * Note that the word returned should be copied by the client
 * code and not manipulated directly.
 */
char *
Parser::get_word(char *delim)
{
	static char	*tp;

	if (! read_one)
		return 0;

	if (tmp_buf == 0)
	{
		tmp_buf = new char[strlen(buffer) + 1];
		(void) strcpy(tmp_buf, buffer);
		tp = tmp_buf;
	}
	char *ret = strtok(tp, delim);
	tp = 0;
	return ret;
}

/*
 * Read an input/output/internal netlist stanza from standard input.  The
 * format of a netlist is as indicated by the following example:
 *
 *	 input:
 *		id: 2
 *		name: A
 *		values: {0 1} {4 X} {5 0}
 *
 * In this example, the netlist 2 is a primary input, with a name 'A'
 * and with signal values 1 X and 0 at times 0 4 and 5 respectively.
 *
 * Returns TRUE if the netlist has been read correctly, FALSE otherwise.
 */
boolean
Parser::read_netlist(wire_type type, Runtime_Component &cmp)
{
	Wire	*wire = 0;

	/*
	 * Parse the attributes of the netlist.
	 */
	while (get_line())
	{
		char		*word;

		if ((word = get_word()) == 0)
		{
			error("Cannot access netlist attributes.");
			continue;
		}
		if (strcmp(word, "\tid:") == 0)
		{
			if ((word = get_word()) == 0)
			{
				error("Netlist has no id.");
				continue;
			}
			/*
			 * Create a new wire off the heap and then
			 * create an appropriate port for the component which
			 * will act as a gateway to this wire.
			 */
			wire = new Wire(word);
			if (type == wire_input) {
				cmp.create_input(wire, word);
			}
			else if (type == wire_output) {
				cmp.create_output(wire, word);
			}
			else {
				cmp.create_internal(wire);
			}
		}
		else if (strcmp(word, "\tvalues:") == 0)
		{
			if (wire == 0)
			{
				error("Cannot read values without a wire.");
				continue;
			}
			if (type != wire_input)
			{
				error("Only inputs can have specify signals.");
				continue;
			}
			read_signals(wire);
		}
		else
		{
			// Assume we have come to the end of the external
			// definition.  Unget the line for the next read.
			unget_line();
			return TRUE;
		}

	}

	return TRUE;
}

/*
 * Read signals from the standard input.  The signal list will take the
 * following form:
 *
 *	{time1 value1} {time2 value2} ... {timeN valueN}
 *
 *	e.g.:
 *	{0 1} {1 0} {3 1} {4 0} {7 X} {8 0} {10 1} {11 0} {13 1} {16 0} {31 X}
 */
boolean Parser::read_signals(Wire *wire)
{
	char		*word;
	Sig_Val		 sigval;
	int		 time;

	while ((word = get_word(" {}")) != 0)
	{
		/*
		 * Convert the time from ASCII to integer.
		 */
		istringstream	 ist(word);
		ist >> time;

		if ((word = get_word(" {}")) == 0)
		{
			error("Cannot find corresponding signal value.");
			return FALSE;
		}

		/*
		 * Convert the ASCII signal value to type Sig_Val.
		 */
		if (strcmp(word, "1") == 0) {
			sigval = SIG_HIGH;
		} else if (strcmp(word, "0") == 0) {
			sigval = SIG_LOW;
		} else  {
			sigval = SIG_X;
		}
		wire->add_signal(Signal(time, sigval));
	} 
	return TRUE;
}

/*
 * Read the component line into memory.
 *
 * The format of a component is:
 *
 *	component:
 *		type: nand
 *		id: 0
 *		ports: 0 1 2
 *
 *
 * Return TRUE if successfully FALSE otherwise.
 */
boolean
Parser::read_component(Runtime_Component &cmp)
{
	char		*word, *cmp_type, *cmp_num;
	list<char *>	 ports;
	boolean		 read_type = FALSE,
			 read_num = FALSE,
			 read_ports = FALSE;

	while (get_line())
	{
		if ((word = get_word()) == 0)
		{
			error("Cannot access component attributes.");
			continue;
		}
		if (strcmp(word, "\ttype:") == 0)
		{
			if (! read_component_attribute(read_type, cmp_type))
				return FALSE;
		}
		else if (strcmp(word, "\tid:") == 0)
		{
			if (! read_component_attribute(read_num, cmp_num))
				return FALSE;
		}
		else if (strcmp(word, "\tports:") == 0)
		{
			if (read_ports)
				continue;
			while ((word = get_word()) != 0)
			{
				char	*net_name = new char[strlen(word) + 1];
				(void) strcpy(net_name, word);
				ports.push_back(net_name);
			}
			read_ports = TRUE;
		}
		else
		{
			/*
			 * Assume we have come to the end of this component
			 * specification.  Put this line back so that
			 * we can pick it up again later and get out of loop.
			 */
			unget_line();
			break;
		}
	}

	boolean ret = TRUE;
	if (cmp.create_subcmp(cmp_type, cmp_num, ports) != 0)
	{
		error("Cannot create subcomponent.");
		/*
		 * We don't do a return here so that we can fall
		 * through and do memory deallocation.
		 */
		ret = FALSE;
	}

	/*
	 * Deallocate the memory used for the component
	 * name, number and netlist connections.
	 */
	delete [] cmp_type;
	delete [] cmp_num;
	list<char *>::const_iterator	  names;
	for (names = ports.begin(); names != ports.end(); names++)
		delete [] *names;

	return ret;
}

/*
 * Reads a component type or id number attribute.  'check' is a boolean
 * which is set to TRUE when that particular attribute has already
 * been read.  If we attempt to re-read the same attribute for the same
 * component, this procedure will return immediately.
 */
boolean
Parser::read_component_attribute(boolean &check, char *&val)
{
	char	*word;

	if (check)
		return TRUE;
	if ((word = get_word()) == 0)
	{
		error("Cannot get component attribute.");
		return FALSE;
	}
	val = new char[strlen(word) + 1];
	(void) strcpy(val, word);
	check = TRUE;
	return TRUE;
}

/*
 * Main driver method responsible for doing all the necessary method
 * invocations to build a runtime component from scratch as supplied
 * via standard input.
 */
int
Parser::ckt(Runtime_Component &component)
{
	char		*word;
	int		 successful = 0;

	while (get_line())
	{
		if ((word = get_word()) == 0)
		{
			error("Unable to read stanza header.");
			return -1;
		}
		if (strcmp(word, "end") == 0)
		{
			successful = 1;
			break;
		}
		else if (strcmp(word, "input:") == 0)
		{
			if (! read_netlist(wire_input, component))
				return -1;
		}
		else if (strcmp(word, "output:") == 0)
		{
			if (! read_netlist(wire_output, component))
				return -1;
		}
		else if (strcmp(word, "internal:") == 0)
		{
			if (! read_netlist(wire_internal, component))
				return -1;
		}
		else if (strcmp(word, "component:") == 0)
		{
			if (! read_component(component))
				return -1;
		}
		else
		{
			cerr << "Invalid stanza header: " << buffer << endl;
			return -1;
		}
	}

	if (! successful)
	{
		cerr << "Unterminated circuit description?\n";
		return -1;
	}

	return 0;
}
