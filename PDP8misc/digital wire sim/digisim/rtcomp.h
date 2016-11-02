/***********************************************************************
 * $RCSfile: rtcomp.h,v $		$Revision: 1.25 $
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
 * Declarations for a component which is constructed during runtime.
 *
 ***********************************************************************/

#if !defined(RTCOMP_H_)		/* protect from multiple inclusion */
#  define RTCOMP_H_

#include	<iostream>	/* For ostream declaration		*/
#include	"sim.h"		/* For ckt_time	typedef			*/
#include	<list>		/* For the internal netlist		*/
#include	"comp.h"	/* Runtime_Component is a derived class	*/

class		Connector;	/* Connector pointer required by class	*/
class		Wire;		/* Wire pointed required by class	*/

/*
 * A runtime component is build during runtime based upon the circuit
 * description received from the GUI.
 */
class Runtime_Component : public Component
{
public:
	Runtime_Component();
	void		 create_input(Wire *, const char *);
	void		 create_output(Wire *, const char *);
	void		 create_internal(Wire *);

	Connector	*find_connector(const char *);
	int		 create_subcmp(const char *,
				       const char *,
				       std::list<char *>);
	virtual void	 display(std::ostream &, int) const;
private:
	std::list<Wire *>	 internal_netlist;	// All the internal
							// netlists for the cmp.
};

#endif	/* !RTCOMP_H_ */
