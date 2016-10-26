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

#include	"sim.h"		/* For boolean typedef			*/

class		Runtime_Component;
class		Wire;

class Parser
{
public:
	enum wire_type	{ wire_input, wire_output, wire_internal };

	Parser(int = 512);
	~Parser();
	int		 ckt(Runtime_Component &);
	boolean		 read_netlist(wire_type, Runtime_Component &);
	boolean		 read_component(Runtime_Component &);
	boolean		 read_signals(Wire *);
	boolean		 read_component_attribute(boolean &, char *&);

private:
	int		 get_line();
	void		 unget_line();
	void		 error(char *) const;
	char		*get_word(char *delim = " ");

	const int	 line_size;
	int		 line_num;
	char		*buffer;
	char		*tmp_buf;
	int		 cached;
	int		 read_one;
};

#endif	/* !PARSE_H_ */
