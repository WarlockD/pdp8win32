/***********************************************************************
 * $RCSfile: rtcomp.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:28:35 $		$Locker:  $
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
 * Declarations for a component which is constructed during runtime.
 *
 ***********************************************************************/

#if !defined(RTCOMP_H_)		/* protect from multiple inclusion */
#  define RTCOMP_H_

#include	"sim.h"		/* For ckt_time	typedef			*/
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
	using Component::simulate;
	using Component::show_outputs;
	Runtime_Component();
	void		 create_input(Wire *, const std::string&);
	void		 create_output(Wire *, const std::string&);
	void		 create_internal(Wire *);

	Connector	*find_connector(const std::string&);
	template<typename R>
	Component* create_subcmp(const std::string& type, const std::string& num, const R& io) {
		std::vector<Connector*> connect;
		//connect.reserve(io.size());
		for (auto a : io) connect.push_back(find_connector(a));
		return create_subcmp(type, num, connect);
	}
	
	
	virtual void	 display(std::ostream &, int) const;
private:
	Component* create_subcmp(const std::string&, const std::string&, const std::vector<Connector*>& connect);
	size_t internal_wires=0;
	std::unordered_map<std::string, std::unique_ptr<Port>> internal_portlist;
	std::unordered_map<std::string, std::unique_ptr<Wire>> internal_netlist;
	std::vector<std::unique_ptr<Component>> m_subcomponents;
};

#endif	/* !RTCOMP_H_ */
