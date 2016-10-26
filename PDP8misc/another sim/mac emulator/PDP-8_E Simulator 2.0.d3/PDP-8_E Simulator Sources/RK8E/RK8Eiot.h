/*
 *	PDP-8/E Simulator Source Code
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK8Eiots.c - IOTs for the RK8-E plugin
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


extern void	i6741 (void);			/* DSKP		6741	*/
extern unsigned s6741 (void);			/* DSKP (skip test)	*/
extern void	i6742 (void);			/* DCLR		6742	*/
extern void	i6743 (void);			/* DLAG		6743	*/
extern void	i6744 (void);			/* DLCA		6744	*/
extern void	i6745 (void);			/* DRST		6745	*/
extern void	i6746 (void);			/* DLDC		6746	*/
extern void	i6747 (void);			/* DMAN		6747	*/
