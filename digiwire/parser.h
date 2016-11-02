/***********************************************************************
 * $RCSfile: parser.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:26:29 $		$Locker:  $
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
 * Class declaration for the Parser class which is used to aid in the
 * parsing of the circuit description obtained from standard input and
 * to build the circuit representation.
 *
 ***********************************************************************/

#if !defined(PARSE_H_)		/* protect from multiple inclusion */
#  define PARSE_H_

#include	"sim.h"		/* For bool typedef			*/

class		Runtime_Component;
class		Wire;

class Parser
{
public:
	enum wire_type	{ wire_input, wire_output, wire_internal };
	Parser();
	int		 ckt(Runtime_Component &);
	bool		 read_netlist(wire_type, Runtime_Component &);
	bool		 read_component(Runtime_Component &);
	bool		 read_signals(Wire *);
	bool		 read_component_attribute(bool &, std::string&);

private:
	int		 get_line();
	void		 unget_line();
	void		 error(const std::string&) const;
	std::string  get_word(const char* delim = " ");
	int		 line_num;
	std::string buffer;
	stringtok m_tokenizer;
	int		 cached;
	int		 read_one;
};

#endif	/* !PARSE_H_ */
