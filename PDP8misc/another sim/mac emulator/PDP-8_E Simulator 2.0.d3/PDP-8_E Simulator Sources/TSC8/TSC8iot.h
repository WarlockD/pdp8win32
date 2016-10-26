/*
 *	PDP-8/E Simulator Source Code
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	TSC8iots.c - IOTs for the TSC8-75 plugin
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


extern void	i6360 (void);			/* ETDS */
extern void	i6361 (void);			/* ESKP */
extern unsigned	s6361 (void);			/* ESKP (skip test) */
extern void	i6362 (void);			/* ECTF */
extern void	i6363 (void);			/* ECDF */
extern unsigned	s6363 (void);			/* ECDF */
extern void	i6364 (void);			/* ERTB */
extern void	i6365 (void);			/* ESME */
extern unsigned	s6365 (void);			/* ESME (skip test) */
extern void	i6366 (void);			/* ERIOT */
extern void	i6367 (void);			/* ETEN */
