/***********************************************************************
 * $RCSfile: parser.cpp,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:26:19 $		$Locker:  $
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
 * Elementary methods for reading lines from standard input and parsing
 * the words within each line.  Also provides primitive support for
 * ungetting lines, error handling and user-defined type conversion.
 *
 * As the circuit description is parsed, the internal circuit representation
 * is constructed.
 *
 ***********************************************************************/

#include	<iostream>
#include	<sstream>
#include	<string.h>
#include	"wire.h"
#include	"rtcomp.h"
#include	"parser.h"
#include	"signal.h"
#include	<list>

Parser::Parser() : line_num(0),  cached(0), read_one(0)
{
}



/*
 * Read a signal line from standard input or restore the cached line
 * (if one is available).  Return true if a line was successfully
 * obtained; false otherwise.
 */
int
Parser::get_line()
{
	char line[512];
	if (cached || std::cin.getline(line, 512))
	{
		if (cached)
			cached = false;
		else
			++ line_num;

		read_one = true;
		buffer = line;
		m_tokenizer = buffer;
		return true;
	}
	return false;
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
		cached = true;
}

/*
 * Dump a parser error on standard error indicating which line in 
 * standard input the error occurred.
 */
void
Parser::error(const std::string&errstring) const
{
	std::cerr << errstring << "\n\tline " << line_num << ": " << buffer << std::endl;
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
std::string
Parser::get_word(const char* delim)
{
	if (! read_one) return std::string();
	return m_tokenizer(delim);
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
 * Returns true if the netlist has been read correctly, false otherwise.
 */
bool
Parser::read_netlist(wire_type type, Runtime_Component &cmp)
{
	Wire	*wire = 0;

	/*
	 * Parse the attributes of the netlist.
	 */
	while (get_line())
	{
		std::string word = get_word();

		if (word.empty())
		{
			error("Cannot access netlist attributes.");
			continue;
		}
		if (word=="\tid:")
		{
			word = get_word();
			if (word.empty())
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
		else if (word=="\tvalues:")
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
			return true;
		}

	}

	return true;
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
bool Parser::read_signals(Wire *wire)
{
	std::string word = get_word(" {}");
	Sig_Val		 sigval;
	int		 time;

	while (!word.empty())
	{
		/*
		 * Convert the time from ASCII to integer.
		 */
		std::stringstream	 ist(word);
		ist >> time;
		word = get_word(" {}");
		if (word.empty())
		{
			error("Cannot find corresponding signal value.");
			return false;
		}

		/*
		 * Convert the ASCII signal value to type Sig_Val.
		 */
		if (word=="1") {
			sigval = SIG_HIGH;
		} else if (word=="0") {
			sigval = SIG_LOW;
		} else  {
			sigval = SIG_X;
		}
		wire->add_signal(Signal(time, sigval));
	} 
	return true;
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
 * Return true if successfully false otherwise.
 */
bool
Parser::read_component(Runtime_Component &cmp)
{
	std::string word, cmp_type, cmp_num;
	std::list<std::string>	 ports;
	bool		 read_type = false,
			 read_num = false,
			 read_ports = false;

	while (get_line())
	{
		word = get_word();
		if (word.empty())
		{
			error("Cannot access component attributes.");
			continue;
		}
		if (word=="\ttype:")
		{
			if (! read_component_attribute(read_type, cmp_type))
				return false;
		}
		else if (word=="\tid:")
		{
			if (! read_component_attribute(read_num, cmp_num))
				return false;
		}
		else if (word== "\tports:")
		{
			if (read_ports)
				continue;
			for (word = get_word(); !word.empty(); word = get_word()) 
			{
				ports.push_back(word);
			}
			read_ports = true;
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

	bool ret = true;
	if (cmp.create_subcmp(cmp_type, cmp_num,  ports ) != 0)
	{
		error("Cannot create subcomponent.");
		/*
		 * We don't do a return here so that we can fall
		 * through and do memory deallocation.
		 */
		ret = false;
	}

	/*
	 * Deallocate the memory used for the component
	 * name, number and netlist connections.
	 */



	return ret;
}

/*
 * Reads a component type or id number attribute.  'check' is a bool
 * which is set to true when that particular attribute has already
 * been read.  If we attempt to re-read the same attribute for the same
 * component, this procedure will return immediately.
 */
bool
Parser::read_component_attribute(bool &check, std::string& val)
{
	if (check) return true;

	std::string word = get_word();
	if (!word.empty())
	{
		error("Cannot get component attribute.");
		return false;
	}
	val = word;
	check = true;
	return true;
}

/*
 * Main driver method responsible for doing all the necessary method
 * invocations to build a runtime component from scratch as supplied
 * via standard input.
 */
int
Parser::ckt(Runtime_Component &component)
{
	std::string word;
	int		 successful = 0;

	while (get_line())
	{
		word = get_word();
		if (word.empty())
		{
			error("Unable to read stanza header.");
			return -1;
		}
		if (word=="end")
		{
			successful = 1;
			break;
		}
		else if (word=="input:")
		{
			if (! read_netlist(wire_input, component))
				return -1;
		}
		else if (word=="output:")
		{
			if (! read_netlist(wire_output, component))
				return -1;
		}
		else if(word == "internal:")
		{
			if (! read_netlist(wire_internal, component))
				return -1;
		}
		else if (word == "component:")
		{
			if (! read_component(component))
				return -1;
		}
		else
		{
			std::cerr << "Invalid stanza header: " << buffer << std::endl;
			return -1;
		}
	}

	if (! successful)
	{
		std::cerr << "Unterminated circuit description?\n";
		return -1;
	}

	return 0;
}
