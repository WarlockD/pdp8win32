/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	iot.c - Interface for the IOT instructions code for the PDP-8/E
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
 *	Include file for Input / Output Transfer (IOT) module.
 */
/* -------------------------------------------------------------------- */
/*
 *	IOT Instructions
 */
/* -------------------------------------------------------------------- */
extern	void	iuser (void) ;
extern	void	i6000 (void) ;			/* SKON		6000	*/
extern	unsigned s6000 (void) ;			/* SKON (skip test)	*/
extern	void	i6001 (void) ;			/* ION		6001	*/
extern	void	i6002 (void) ;			/* IOF		6002	*/
extern	void	i6003 (void) ;			/* SRQ		6003	*/
extern	unsigned s6003 (void) ;			/* SRQ (skip test)	*/
extern	void	i6004 (void) ;			/* GTF		6004	*/
extern	void	i6005 (void) ;			/* RTF		6005	*/
extern	void	i6006 (void) ;			/* SGT		6006	*/
extern	unsigned s6006 (void) ;			/* SGT (skip test)	*/
extern	void	i6007 (void) ;			/* CAF		6007	*/
extern	void	i6201 (void) ;			/* CDF 0	6201	*/
extern	void	i6202 (void) ;			/* CIF 0	6202	*/
extern	void	i6203 (void) ;			/* CIF CDF 0	6203	*/
extern	void	i6204 (void) ;			/* CINT		6204	*/
extern	void	i6211 (void) ;			/* CDF 1	6211	*/
extern	void	i6212 (void) ;			/* CIF 1	6212	*/
extern	void	i6213 (void) ;			/* CIF CDF 1	6213	*/
extern	void	i6214 (void) ;			/* RDF		6214	*/
extern	void	i6221 (void) ;			/* CDF 2	6221	*/
extern	void	i6222 (void) ;			/* CIF 2	6222	*/
extern	void	i6223 (void) ;			/* CIF CDF 2	6223	*/
extern	void	i6224 (void) ;			/* RIF		6224	*/
extern	void	i6231 (void) ;			/* CDF 3	6231	*/
extern	void	i6232 (void) ;			/* CIF 3	6232	*/
extern	void	i6233 (void) ;			/* CIF CDF 3	6233	*/
extern	void	i6234 (void) ;			/* RIB		6234	*/
extern	void	i6241 (void) ;			/* CDF 4	6241	*/
extern	void	i6242 (void) ;			/* CIF 4	6242	*/
extern	void	i6243 (void) ;			/* CIF CDF 4	6243	*/
extern	void	i6244 (void) ;			/* RMF		6244	*/
extern	void	i6251 (void) ;			/* CDF 5	6251	*/
extern	void	i6252 (void) ;			/* CIF 5	6252	*/
extern	void	i6253 (void) ;			/* CIF CDF 5	6253	*/
extern	void	i6254 (void) ;			/* SINT		6254	*/
extern	unsigned s6254 (void) ;			/* SINT (skip test)	*/
extern	void	i6261 (void) ;			/* CDF 6	6261	*/
extern	void	i6262 (void) ;			/* CIF 6	6262	*/
extern	void	i6263 (void) ;			/* CIF CDF 6	6263	*/
extern	void	i6264 (void) ;			/* CUF		6264	*/
extern	void	i6271 (void) ;			/* CDF 7	6271	*/
extern	void	i6272 (void) ;			/* CIF 7	6272	*/
extern	void	i6273 (void) ;			/* CIF CDF 7	6273	*/
extern	void	i6274 (void) ;			/* SUF		6274	*/
extern	void	i6551 (void) ;			/* FPINT	6551	*/
extern	unsigned s6551 (void) ;			/* FPINT (skip test)	*/
extern	void	i6552 (void) ;			/* FPICL	6552	*/
extern	void	i6553 (void) ;			/* FPCOM	6553	*/
extern	void	i6554 (void) ;			/* FPHLT	6554	*/
extern	void	i6555 (void) ;			/* FPST		6555	*/
extern	unsigned s6555 (void) ;			/* FPST (skip test)	*/
extern	void	i6556 (void) ;			/* FPRST	6556	*/
extern	void	i6557 (void) ;			/* FPIST	6557	*/
extern	unsigned s6557 (void) ;			/* FPIST (skip test)	*/
extern	void	i6567 (void) ;			/* FPEP		6567	*/
extern	void	rxsel (void) ;			/* SEL		6750	*/
extern	void	rxlcd (void) ;			/* LCD		6751	*/
extern	void	rxxdr (void) ;			/* XDR		6752	*/
extern	void	rxstr (void) ;			/* STR		6753	*/
extern	void	rxser (void) ;			/* SER		6754	*/
extern	void	rxsdn (void) ;			/* SDN		6755	*/
extern	void	rxintr (void) ;			/* INTR		6756	*/
extern	void	rxinit (void) ;			/* INIT		6757	*/
/* -------------------------------------------------------------------- */
