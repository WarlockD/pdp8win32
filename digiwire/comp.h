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

#include	"sim.h"		/* For ckt_time/bool	typedef		*/


class		Port;		/* Forward declaration for I/O_list	*/

/*
 * Component objects are the functional units of the circuit that process
 * inputs and produce outputs.
 */

class Component : std::enable_shared_from_this<Component>
{
public:
	std::shared_ptr<Component> ptr() { return shared_from_this(); }
	void add_input_port(Port& port) { Inputs.emplace_back( &port); }
	void add_output_port(Port& port) { Outputs.emplace_back( &port); }
	virtual		~Component() {}
	void		 simulate();		// Simulate the component.
	void		 show_outputs() const;	// Display the output signals.
	virtual void	 display(std::ostream &, int) const;	// Display cmp.
	virtual void 	 process(ckt_time);	// Method responsible for
										// processing component inputs.
										// In general, this is
										// different for all cmps.
	const std::string& get_name() const { return m_name; }
protected:
	std::vector<Port*>	 Inputs;		// List of input ports.
	std::vector<Port*>	 Outputs;		// List of output ports.
protected:
	Component(ckt_time t = 1L, const std::string& nm= "Component") 
		: m_name(nm),delay(t), local_time(CKT_TIME_INIT) { }
	ckt_time	 delay;		// Transport delay of component.
private:
	void		 display_ports(std::ostream &, const std::vector<Port*>&, int) const;
			 			// Used twice by display().

	bool		 inputs_are_ready() const;
						// Is cmp ready for simulation?
						// Used by simulate().

	std::string m_name;		// Name of the component.
	ckt_time	 local_time;	// Local time of component.
};
class ComponentGate : public Component , public SignalInterface {
public:
	WireExpression(SignalInterface* l, int op, SignalInterface* r);

	~WireExpression();
	virtual void process(ckt_time) override;	// Method responsible for
										// processing component inputs.
										// In general, this is
										// different for all cmps.
protected:
	WireExpression(Wire* l);
	int m_op;
	SignalInterface* m_l;
	SignalInterface* m_r;
};
inline WireExpression operator&&(SignalInterface& l, SignalInterface& r) {
	return WireExpression(&l, '&', &r);
}
inline WireExpression operator||(SignalInterface& l, SignalInterface& r) {
	return WireExpression(&l, '|', &r);
}
inline WireExpression operator!(SignalInterface& r) {
	return WireExpression(nullptr, '!', &r);
}
#endif	/* !COMP_H_ */
