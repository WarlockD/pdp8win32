/***********************************************************************
 * $RCSfile: comp.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:19:56 $		$Locker:  $
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
 * Class declaration for component class.
 *
 ***********************************************************************/

#if !defined(COMP_H_)		/* protect from multiple inclusion */
#  define COMP_H_

#include	<iostream.h>	/* For ostream declaration		*/
#include	"sim.h"		/* For ckt_time/boolean	typedef		*/
#include	<list>		/* Input/Output are linked lists	*/

class		Port;		/* Forward declaration for I/O_list	*/

/*
 * Component objects are the functional units of the circuit that process
 * inputs and produce outputs.
 */
class Component
{
public:
	virtual		~Component();

	list<Port *>	 I_List;		// List of input ports.
	list<Port *>	 O_List;		// List of output ports.
	virtual void 	 process(ckt_time);	// Method responsible for
						// processing component inputs.
			 			// In general, this is
			 			// different for all cmps.
	void		 simulate();		// Simulate the component.
	void		 show_outputs() const;	// Display the output signals.

	virtual void	 display(ostream &, int) const;	// Display cmp.
							// information.

protected:
	Component(ckt_time = 1L, const char* = "Component");	
						// Only derived components
						// can be created.

	ckt_time	 delay;		// Transport delay of component.

private:
	void		 display_ports(ostream &, list<Port *>, int) const;
			 			// Used twice by display().

	boolean		 inputs_are_ready() const;
						// Is cmp ready for simulation?
						// Used by simulate().

	char		*name;		// Name of the component.
	ckt_time	 local_time;	// Local time of component.
};

#endif	/* !COMP_H_ */
