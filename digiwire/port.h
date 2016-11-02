/***********************************************************************
 * $RCSfile: port.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:27:33 $		$Locker:  $
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
 * Class declaration for port class and its input and output port
 * subclasses.
 *
 ***********************************************************************/

#if !defined(PORT_H_)		/* protect from multiple inclusion */
#  define PORT_H_

#include	<iostream>	/* For ostream declaration		*/
#include	"sim.h"		/* For ckt_time typedef			*/
#include	"connect.h"	/* Port is a derived class of Connector	*/

/*
 * Ports connect components with the outside world.
 */
class Port : public Connector
{
public:
	virtual Signal get_signal(ckt_time) const;	// Get signal from ext.
	virtual void send_signal(Signal);		// Send sig. to ext.
	virtual void show_signals() const;		// Display ext. sigs.
	void		 display(std::ostream &, int) const; // Diplay port info.
protected:
	Port(Connector &, const std::string& = "Port"); 	// Only derived ports
							// can be created.

	Connector	*external;	// External connector (port or wire) to which the
					// port is attachted.
};

/*
 * Input ports can get signals from the external world but are unable to
 * interpret a send_signal() message.
 */
class Input : public Port
{
public:
	Input(Component &, Connector &, const std::string& = "Input");

	void	send_signal(Signal);	// Input ports can't send signals.
};

/*
 * Output ports can receive signals and send them, so inherit both
 * send_ and get_signal methods from Port.
 */
class Output : public Port
{
public:
	Output(Component &, Connector &, const std::string& = "Output");
};

#endif	/* !PORT_H_ */
