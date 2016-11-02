/***********************************************************************
 * $RCSfile: main.cpp,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:25:41 $		$Locker:  $
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
 * Main driver program for intializing the primary inputs, constructing
 * and simulating the circuit and displaying the primary outputs. 
 *
 ***********************************************************************/

#include	<iostream>
#include	"sim.h"			/* For generate_tabs() decl	*/
#include	"rtcomp.h"
#include	"parser.h"

#include	"signal.h"
#include	"wire.h"
#include	"complib.h"

/*
 * Generate a string containing the specified number of tab characters
 * for indenting output purposes.
 */

std::ostream& operator<<(std::ostream& os, const generate_tabs& gtabs) {
	os << gtabs.tabs;
	return os;
}
#define DDJ
/*
 * Main driver function:
 * If DDJ is defined, then then main program will programmatically
 * create a 3-input AND gate with some test inputs and then simulate
 * and display the resultant output.
 *
 * If DDJ is not defined, then we parse the circuit description (from
 * standard input), simulate the circuit and send the output to the
 * standard output.
 */
int
main()
{
#ifdef DDJ
	Signal  Signal1[] =
	{
	 	// {0 1} {2 0} {3 1} {4 0} {31 0}
		Signal(0, SIG_HIGH),Signal(2, SIG_LOW),Signal(3, SIG_HIGH),Signal(4, SIG_LOW),
 	};

	Signal  Signal2[] =
	{
		// {0 1} {1 0} {2 1} {4 0} {31 0}
		Signal(0, SIG_HIGH),
		Signal(1, SIG_LOW),
		Signal(2, SIG_HIGH),
		Signal(4, SIG_LOW)
	};

	Signal  Signal3[] =
	{
		// {0 1} {2 0} {4 1} {5 0} {31 0}
		Signal(0, SIG_HIGH),
		Signal(2, SIG_LOW),
		Signal(4, SIG_HIGH),
		Signal(5, SIG_LOW)
	};

	Wire	w1({ Signal(0, SIG_HIGH),Signal(2, SIG_LOW),Signal(3, SIG_HIGH),Signal(4, SIG_LOW) }, "Main in_w1");
	Wire	w2({ Signal(0, SIG_HIGH),Signal(2, SIG_LOW),Signal(3, SIG_HIGH),Signal(4, SIG_LOW) }, "Main in_w2");
	Wire	w3({ Signal(0, SIG_HIGH),Signal(2, SIG_LOW),Signal(3, SIG_HIGH),Signal(4, SIG_LOW) }, "Main in_w3");
	Wire	w4("Main out_w4");
	And3	and3(w1, w2, w3, w4, CKT_TIME_NULL, "and3");
	auto test = w1 && w2 && w3;
	
	and3.simulate();

	and3.show_outputs();
	w4.show_signals();
#else
	Runtime_Component	component;
	Parser			parse;

	if (parse.ckt(component) != 0)
		return -1;

	// component.display(cout, 0);

	component.simulate();

	component.show_outputs();
#endif

	return 0;
}
