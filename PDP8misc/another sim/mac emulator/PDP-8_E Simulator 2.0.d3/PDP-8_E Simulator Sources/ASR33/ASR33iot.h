/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	ASR33iot.h - ASR 33 Teletype IOTs
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


extern void	i6030 (void);			/* KCF		6030	*/
extern void	i6031 (void);			/* KSF		6031	*/
extern unsigned s6031 (void);			/* KSF (skip test)	*/
extern void	i6032 (void);			/* KCC		6032	*/
extern void	i6034 (void);			/* KRS		6034	*/
extern void	i6035 (void);			/* KIE		6035	*/
extern void	i6036 (void);			/* KRB		6036	*/
extern void	i6040 (void);			/* TFL		6040	*/
extern void	i6041 (void);			/* TSF		6041	*/
extern unsigned s6041 (void);			/* TSF (skip test)	*/
extern void	i6042 (void);			/* TCF		6042	*/
extern void	i6044 (void);			/* TPC		6044	*/
extern void	i6045 (void);			/* TSK		6045	*/
extern unsigned s6045 (void);			/* TSK (skip test)	*/
extern void	i6046 (void);			/* TLS		6046	*/
