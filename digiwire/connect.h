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

#include "sim.h"
#include	"signal.h"	/* Signal size req'd by Connector	*/

//class 		Component;	/* Forward declaration for 'fan_out'	*/

/*
 * Connectors are responsible for connecting components with one another
 * and the outside world. It is through connectors that the transmission
 * of signals occurs.
 */
class Connector : public SignalInterface, std::enable_shared_from_this<Connector>
{
	friend class Component;
public:
	std::shared_ptr<Connector> ptr() { return shared_from_this(); }
	virtual	void	 send_signal(Signal) = 0;	// Generate signal
	virtual	void	 show_signals() const = 0;	// Display signals.

	virtual void	 display(std::ostream &, int) const;	// Display info.

    // Add the component pointer to the fan-out list.
	void connect(Component & cmp) { m_fan_out.push_back(&cmp); }
		
	const std::string& get_name() const { return m_name; }		// Return the name
	void propagate() const;		// Simulate fanout cmps


	virtual	~Connector() { m_all_connectors.erase(get_name()); }
	bool operator==(const Connector& other) const { return get_name() == other.get_name(); }
	bool operator!=(const Connector& other) const { return !(*this == other); }
	bool operator<(const Connector& other) const { return get_name() < other.get_name(); }
protected:
	Connector(const std::string& nm) : m_name(nm) { 
		assert(m_all_connectors.find(nm) == m_all_connectors.end());
		m_all_connectors.emplace(std::make_pair(nm,this)); 
	}

private:
	std::unordered_map<std::string, Connector*> m_all_connectors;
	std::vector<Component*> m_fan_out;	// Components in fan-out
	std::string m_name; // Name of connector.
};

namespace std {
	template<>
	struct hash<Connector> {
		const static  std::hash<std::string> shash;
		size_t operator()(const Connector& c) const { return shash(c.get_name()); }
	};
};

#endif	/* !CONNECT_H_ */
