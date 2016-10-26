/***********************************************************************
 * $RCSfile: wire.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:33:25 $		$Locker:  $
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
 * Class declaration for wire class.
 *
 ***********************************************************************/

#if !defined(WIRE_H_)		/* protect from multiple inclusion */
#  define WIRE_H_

#include	<iostream.h>	/* For ostream declaration		*/
#include	"sim.h"		/* For ckt_time	typedef			*/
#include	"connect.h"	/* Wire is a derived class of Connector	*/
#include	"signal.h"	/* Size of Signal required by Wire	*/
#include	<list>		/* 'signals' is a linked list		*/

/*
 * Wires connect components with each other. They store a history of
 * signals <value, time> in addition to a linked list of components
 * inherited from Connector.
 */
class Wire : public Connector
{
public:
	Wire(const char* = "Wire");
	Wire::Wire(Signal s[], int num, char *nm);

	Signal	get_signal(ckt_time) const;		// Retrieve a signal.
	void	send_signal(Signal);			// Put & propagate sig.
	void	show_signals() const;			// Show wire signals.
	void	display(ostream &, int) const;		// Show wire info.
	void	add_signal(Signal);			// Place initial 
							// signals on a wire.
private:
	void		display_signals() const;	// Dump signals on wire.
	void		replace(Signal);		// Replace a signal.
	list<Signal>	signals;	// History of all the signals which 
					// have travelled along the wire.
};


#endif	/* !WIRE_H_ */
