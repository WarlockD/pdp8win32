/***********************************************************************
 * $RCSfile: sim.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:30:29 $		$Locker:  $
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
 * General purpose header file containing typedefs and constants
 * which are useful to the simulator engine as a whole.
 *
 ***********************************************************************/

#if !defined(SIM_H_)		/* protect from multiple inclusion */
#  define SIM_H_

/*
 * Define our typedefs, signal values and a few useful consts. This header
 * file should be included by most source files in the implementation.
 */
typedef int	boolean;
#if !defined(TRUE)
	const	boolean TRUE = 1;
	const   boolean FALSE = 0;
#endif	/* !TRUE */

typedef long	ckt_time;

const	ckt_time CKT_TIME_INIT = -1;
const	ckt_time CKT_TIME_NULL = -2;

char	*generate_tabs(int);	// Defined in main.cpp

#endif	/* !SIM_H_ */
