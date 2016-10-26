/***********************************************************************
 * $RCSfile: connect.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:24:52 $		$Locker:  $
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
 * Class declaration for the connector class.
 *
 ***********************************************************************/

#if !defined(CONNECT_H_)		/* protect from multiple inclusion */
#  define CONNECT_H_

#include	<iostream.h>	/* For ostream declaration		*/
#include	"signal.h"	/* Signal size req'd by Connector	*/
#include	<list>		/* 'fan_out' is a linked list		*/

class 		Component;	/* Forward declaration for 'fan_out'	*/

/*
 * Connectors are responsible for connecting components with one another
 * and the outside world. It is through connectors that the transmission
 * of signals occurs.
 */
class Connector
{
public:
	virtual		~Connector();
	virtual	Signal	 get_signal(ckt_time) const = 0;// Retrieve signal
	virtual	void	 send_signal(Signal) = 0;	// Generate signal
	virtual	void	 show_signals() const = 0;	// Display signals.

	virtual void	 display(ostream &, int) const;	// Display info.

	void		 connect(Component &);		// Add comp. to fan-out
	const char	*get_name() const;		// Return the name
	void		 propagate() const;		// Simulate fanout cmps

protected:
	Connector(const char* = "Connector");		// Only derived
							// connectors can be
							// created.
private:
	list<Component *> fan_out;	// Components in fan-out
	char 		 *name;		// Name of connector.
};

#endif	/* !CONNECT_H_ */
