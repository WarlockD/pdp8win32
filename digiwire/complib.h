/***********************************************************************
 * $RCSfile: complib.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:24:04 $		$Locker:  $
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
 * Class definitions for the actual components to be simulated.
 * Each of these components must be derived from the 'Component' class.
 *
 ***********************************************************************/

#if !defined(COMPLIB_H_)		/* protect from multiple inclusion */
#  define COMPLIB_H_

#include	"sim.h"		/* For ckt_time typedef			*/
#include	"comp.h"	/* And2 etc. are derived from Component	*/
#include	"port.h"	/* For definition of Input/Output ports	*/
#include	"wire.h"	/* For definition of wire class		*/



class And2 : public Component
{
public:
	And2(Connector &, Connector &, Connector &,
	     ckt_time = 1L, const std::string& = "And2");
	void process(ckt_time);
private:
	Input	I1, I2;
	Output	O1;
};

class Nand2 : public Component
{
public:
	Nand2(Connector &, Connector &, Connector &,
	      ckt_time = 1L, const std::string& = "Nand2");
	void process(ckt_time);
private:
	Input	I1, I2;
	Output	O1;
};

class Or2 : public Component
{
public:
	Or2(Connector &, Connector &, Connector &,
	     ckt_time = 1L, const std::string& = "And2");
	void process(ckt_time);
private:
	Input	I1, I2;
	Output	O1;
};

class Nor2 : public Component
{
public:
	Nor2(Connector &, Connector &, Connector &,
	      ckt_time = 1L, const std::string& = "Nand2");
	void process(ckt_time);
private:
	Input	I1, I2;
	Output	O1;
};

class Xor2 : public Component
{
public:
	Xor2(Connector &, Connector &, Connector &,
	    ckt_time = 1L, const std::string& = "Xor2");
	void process(ckt_time);
private:
	Input	I1, I2;
	Output	O1;
};

class Xnor2 : public Component
{
public:
	Xnor2(Connector &, Connector &, Connector &,
	    ckt_time = 1L, const std::string& = "Xnor2");
	void process(ckt_time);
private:
	Input	I1, I2;
	Output	O1;
};

class Not : public Component
{
public:
	Not(Connector &, Connector &,
	      ckt_time = 1L, const std::string& = "Nand2");
	void process(ckt_time);
private:
	Input	I1;
	Output	O1;
};

class Buffer : public Component
{
public:
	Buffer(Connector &, Connector &,
	      ckt_time = 1L, const std::string& = "Nand2");
	void process(ckt_time);
private:
	Input	I1;
	Output	O1;
};

class And3 : public Component
{
public:
	And3(Connector &, Connector &, 
	     Connector &, Connector &, 
	     ckt_time = CKT_TIME_NULL, const std::string& = "And3");
private:
	Input	I1, I2, I3;
	Output	O1;
	Wire	w;
	And2	and2a, and2b;
};

class RS_Latch : public Component
{
public:
	RS_Latch(Connector &, Connector &, 
		 Connector &, Connector &, 
		 ckt_time = CKT_TIME_NULL, const std::string& = "RS_Latch");
private:
	Input	R, S;
	Output	Q, Qb;
	Nand2	nand2a, nand2b;
};

class Func_Block : public Component
{
public:
	Func_Block(Connector &, Connector &, 
		   Connector &, Connector &, 
		   ckt_time = CKT_TIME_NULL, const std::string& = "Func_Block");
private:
	Input	A, B;
	Output	Y, Z;
	Wire	w;
	Nand2	nand2;
	And2	and2a, and2b;
};

class Func_Block2: public Component
{
public:
	Func_Block2(Connector &, Connector &,
		    Connector &, Connector &,
		    Connector &,
		    ckt_time = CKT_TIME_NULL, const std::string& = "Func_Block2");
private:
	Input	A, B;
	Output	X, Y, Z;
	And2	and2a, and2b, and2c;
};

Component& operator&&(Component& l, Component& r) {
	Port* wire = new Port(l.get_name() + " && " + r.get_name());
	new And2(l, r, wire);
	return l;
	Component* ret = new Component()

	return And2(l)
}
#endif	/* !COMPLIB_H_ */
