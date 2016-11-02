/***********************************************************************
 * $RCSfile: complib.cpp,v $		$Revision: 1.24 $
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
 * This module contains the behavioural definitions of all the actual
 * components in the predefined component library.  These components
 * are instantiated and simulated by the simulator engine.
 *
 ***********************************************************************/

#include	"complib.h"

/*
 * Component library. All user defined components go here.
 * Only components at the lowest level of abstract need to
 * redefine the virtual process() function.
 */

#define CONNECT(Port, Wire, name)	Port(*this, Wire, name)

And2::And2(Connector &ci1, Connector &ci2, Connector &co1,
	   ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "And2 I1"),
	CONNECT(I2, ci2, "And2 I2"),
	CONNECT(O1, co1, "And2 O1")	
{ }

void
And2::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();
	Sig_Val		sigval2 = I2.get_signal(t).get_value();

	if (sigval1 == SIG_HIGH && sigval2 == SIG_HIGH)
		O1.send_signal(Signal(t + delay, SIG_HIGH));
	else if (sigval1 == SIG_LOW || sigval2 == SIG_LOW)
		O1.send_signal(Signal(t + delay, SIG_LOW));
	else
		O1.send_signal(Signal(t + delay, SIG_X));
}

Nand2::Nand2(Connector &ci1, Connector &ci2, Connector &co1,
	     ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "Nand2 I1"),
	CONNECT(I2, ci2, "Nand2 I2"),
	CONNECT(O1, co1, "Nand2 O1")
{ }

void
Nand2::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();
	Sig_Val		sigval2 = I2.get_signal(t).get_value();

	if (sigval1 == SIG_LOW || sigval2 == SIG_LOW)
		O1.send_signal(Signal(t + delay, SIG_HIGH));
	else if (sigval1 == SIG_HIGH && sigval2 == SIG_HIGH)
		O1.send_signal(Signal(t + delay, SIG_LOW));
	else
		O1.send_signal(Signal(t + delay, SIG_X));
}

Or2::Or2(Connector &ci1, Connector &ci2, Connector &co1,
	   ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "Or2 I1"),
	CONNECT(I2, ci2, "Or2 I2"),
	CONNECT(O1, co1, "Or2 O1")	
{ }

void
Or2::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();
	Sig_Val		sigval2 = I2.get_signal(t).get_value();

	if (sigval1 == SIG_HIGH || sigval2 == SIG_HIGH)
		O1.send_signal(Signal(t + delay, SIG_HIGH));
	else if (sigval1 == SIG_LOW && sigval2 == SIG_LOW)
		O1.send_signal(Signal(t + delay, SIG_LOW));
	else
		O1.send_signal(Signal(t + delay, SIG_X));
}

Nor2::Nor2(Connector &ci1, Connector &ci2, Connector &co1,
	     ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "Nor2 I1"),
	CONNECT(I2, ci2, "Nor2 I2"),
	CONNECT(O1, co1, "Nor2 O1")
{ }

void
Nor2::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();
	Sig_Val		sigval2 = I2.get_signal(t).get_value();

	if (sigval1 == SIG_LOW && sigval2 == SIG_LOW)
		O1.send_signal(Signal(t + delay, SIG_HIGH));
	else if (sigval1 == SIG_HIGH || sigval2 == SIG_HIGH)
		O1.send_signal(Signal(t + delay, SIG_LOW));
	else
		O1.send_signal(Signal(t + delay, SIG_X));
}

Xor2::Xor2(Connector &ci1, Connector &ci2, Connector &co1,
	   ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "Xor2 I1"),
	CONNECT(I2, ci2, "Xor2 I2"),
	CONNECT(O1, co1, "Xor2 O1")
{ }

void
Xor2::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();
	Sig_Val		sigval2 = I2.get_signal(t).get_value();

	if ((sigval1 == SIG_LOW  && sigval2 == SIG_HIGH) ||
	    (sigval1 == SIG_HIGH && sigval2 == SIG_LOW))
		O1.send_signal(Signal(t + delay, SIG_HIGH));
	else if ((sigval1 == SIG_LOW  && sigval2 == SIG_LOW) ||
	         (sigval1 == SIG_HIGH && sigval2 == SIG_HIGH))
		O1.send_signal(Signal(t + delay, SIG_LOW));
	else
		O1.send_signal(Signal(t + delay, SIG_X));
}

Xnor2::Xnor2(Connector &ci1, Connector &ci2, Connector &co1,
	   ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "Xnor2 I1"),
	CONNECT(I2, ci2, "Xnor2 I2"),
	CONNECT(O1, co1, "Xnor2 O1")
{ }

void
Xnor2::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();
	Sig_Val		sigval2 = I2.get_signal(t).get_value();

	if ((sigval1 == SIG_HIGH && sigval2 == SIG_HIGH) ||
	    (sigval1 == SIG_LOW  && sigval2 == SIG_LOW))
		O1.send_signal(Signal(t + delay, SIG_HIGH));
	else if ((sigval1 == SIG_HIGH && sigval2 == SIG_LOW) ||
	         (sigval1 == SIG_LOW  && sigval2 == SIG_HIGH))
		O1.send_signal(Signal(t + delay, SIG_LOW));
	else
		O1.send_signal(Signal(t + delay, SIG_X));
}

Not::Not(Connector &ci1, Connector &co1,
	     ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "Not I1"),
	CONNECT(O1, co1, "Not O1")
{ }

void
Not::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();

	if (sigval1 == SIG_LOW)
		O1.send_signal(Signal(t + delay, SIG_HIGH));
	else if (sigval1 == SIG_HIGH)
		O1.send_signal(Signal(t + delay, SIG_LOW));
	else
		O1.send_signal(Signal(t + delay, SIG_X));
}

Buffer::Buffer(Connector &ci1, Connector &co1,
	     ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "Buffer I1"),
	CONNECT(O1, co1, "Buffer O1")
{ }

void
Buffer::process(ckt_time t)
{
	Sig_Val		sigval1 = I1.get_signal(t).get_value();

	O1.send_signal(Signal(t + delay, sigval1));
}

And3::And3(Connector &ci1, Connector &ci2,
	   Connector &ci3, Connector &co1, 
	   ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(I1, ci1, "And3 I1"),
	CONNECT(I2, ci2, "And3 I2"),
	CONNECT(I3, ci3, "And3 I3"),
	CONNECT(O1, co1, "And3 O1"),
	w("And3 wire"),
	and2a(I1, I2, w, 1L, "And2a"),
	and2b(w, I3, O1, 1L, "And2b")
{ }

RS_Latch::RS_Latch(Connector &ci1, Connector &ci2,
		   Connector &co1, Connector &co2, 
		   ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(R, ci1, "R"),
	CONNECT(S, ci2, "S"),
	CONNECT(Q, co1, "Q"),
	CONNECT(Qb, co2, "Qb"),
	nand2a(R, Qb, Q, 1L, "Nand2a"),
	nand2b(Q, S, Qb, 1L, "Nand2b")
{ }

Func_Block::Func_Block(Connector &ci1, Connector &ci2,
		       Connector &co1, Connector &co2, 
		       ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(A, ci1, "A"),
	CONNECT(B, ci2, "B"),
	CONNECT(Y, co1, "Y"),
	CONNECT(Z, co2, "Z"),
	w("Func wire"),
	nand2(A, B, w, 1L, "nand2"),
	and2a(A, w, Y, 1L, "and2a"),
	and2b(B, w, Z, 1L, "and2b")
{ }

Func_Block2::Func_Block2(Connector &ci1, Connector &ci2,
			 Connector &co1, Connector &co2,
			 Connector &co3,
			 ckt_time dly, char *n) :
	Component(dly, n),
	CONNECT(A, ci1, "A"),
	CONNECT(B, ci2, "B"),
	CONNECT(X, co1, "X"),
	CONNECT(Y, co2, "Y"),
	CONNECT(Z, co3, "Z"),
	and2a(A, Y, X, 1L, "nand2"),
	and2b(A, B, Y, 1L, "and2a"),
	and2c(B, Y, Z, 1L, "and2b")
{ }
