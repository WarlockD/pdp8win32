/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	mri.h - Interface for the MRI instructions code for the PDP-8/E
 *
 *	This file is part of PDP-8/E Simulator.
 *
 *	PDP-8/E Simulator is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/*
 *	EMUL-8: a pdp8e emulator.
 *
 *	Author:
 *		Bill Haygood
 *		41832 Ernest Road
 *		Loon Lake, WA 99148-9607
 *		Internet: billh@comtch.iea.com
 *		Voice/AnsMach/FAX \
 *			or	   509-233-2555
 *		  Cellular/Pager  /
 *
 *	Copyright 1992, 1993, 1994 by the author with all rights reserved.
 *
 *	Memory Reference Instruction (MRI) module.
 */
/* -------------------------------------------------------------------- */
extern	void	i0000 (void) ;
extern	void	i0200 (void) ;
extern	void	i0400 (void) ;
extern	void	i0410 (void) ;
extern	void	i0600 (void) ;
extern	void	i0610 (void) ;
extern	void	i1000 (void) ;
extern	void	i1200 (void) ;
extern	void	i1400 (void) ;
extern	void	i1410 (void) ;
extern	void	i1600 (void) ;
extern	void	i1610 (void) ;
extern	void	i2000 (void) ;
extern	unsigned s2000 (void) ;
extern	void	i2200 (void) ;
extern	unsigned s2200 (void) ;
extern	void	i2400 (void) ;
extern	unsigned s2400 (void) ;
extern	void	i2410 (void) ;
extern	unsigned s2410 (void) ;
extern	void	i2600 (void) ;
extern	unsigned s2600 (void) ;
extern	void	i2610 (void) ;
extern	unsigned s2610 (void) ;
extern	void	i3000 (void) ;
extern	void	i3200 (void) ;
extern	void	i3400 (void) ;
extern	void	i3410 (void) ;
extern	void	i3600 (void) ;
extern	void	i3610 (void) ;
extern	void	i4000 (void) ;
extern	void	u4000 (void) ;
extern	void	i4200 (void) ;
extern	void	u4200 (void) ;
extern	void	i4400 (void) ;
extern	void	u4400 (void) ;
extern	void	i4410 (void) ;
extern	void	u4410 (void) ;
extern	void	i4600 (void) ;
extern	void	u4600 (void) ;
extern	void	i4610 (void) ;
extern	void	u4610 (void) ;
extern	void	i5000 (void) ;
extern	void	u5000 (void) ;
extern	void	i5200 (void) ;
extern	void	u5200 (void) ;
extern	void	i5400 (void) ;
extern	void	u5400 (void) ;
extern	void	i5410 (void) ;
extern	void	u5410 (void) ;
extern	void	i5600 (void) ;
extern	void	u5600 (void) ;
extern	void	i5610 (void) ;
extern	void	u5610 (void) ;
/* -------------------------------------------------------------------- */
