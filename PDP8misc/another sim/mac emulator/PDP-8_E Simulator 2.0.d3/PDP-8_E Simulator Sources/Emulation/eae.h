/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	eae.h - Interface for the EAE instructions code for the PDP-8/E
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
 *	Include file for Operate microinstruction (OPR) module
 *	to handle EAE instructions.
 */
/* -------------------------------------------------------------------- */
extern	void	i7401 (void) ;			/* NOP	Grp III		*/
extern	void	i7403 (void) ;
extern	void	i7405 (void) ;			/* MUY			*/
extern	void	i7407 (void) ;			/* DVI			*/
extern	void	i7411 (void) ;			/* NMI			*/
extern	void	i7413 (void) ;			/* SHL			*/
extern	void	i7415 (void) ;			/* ASR			*/
extern	void	i7417 (void) ;			/* LSR			*/
extern	void	i7421 (void) ;			/* MQL			*/
extern	void	i7423 (void) ;			/* MQL SCL		*/
extern	void	i7425 (void) ;			/* MQL MUY		*/
extern	void	i7427 (void) ;			/* MQL DVI		*/
extern	void	i7431 (void) ;			/* SWAB			*/
extern	void	i7433 (void) ;			/* MQL SHL		*/
extern	void	i7435 (void) ;			/* MQL ASR		*/
extern	void	i7437 (void) ;			/* MQL LSR		*/
extern	void	i7441 (void) ;			/* SCA			*/
extern	void	i7443 (void) ;
extern	void	i7445 (void) ;
extern	void	i7447 (void) ;			/* SWBA			*/
extern	void	i7451 (void) ;
extern	unsigned s7451 (void) ;			/* 	(skip test)	*/
extern	void	i7453 (void) ;
extern	void	i7455 (void) ;
extern	void	i7457 (void) ;
extern	void	i7461 (void) ;			/* MQL SCA		*/
extern	void	i7463 (void) ;			/* A: MQL SCA SCL	*/
extern	void	i7465 (void) ;			/* A: MQL SCA MUY	*/
extern	void	i7467 (void) ;			/* MQL SWBA		*/
extern	void	i7471 (void) ;			/* A: MQL SCA NMI	*/
extern	void	i7473 (void) ;			/* A: MQL SCA SHL	*/
extern	void	i7475 (void) ;			/* A: MQL SCA ASR	*/
extern	void	i7477 (void) ;			/* A: MQL SCA LSR	*/
extern	void	i7501 (void) ;			/* MQA			*/
extern	void	i7503 (void) ;			/* A: MQA SCL		*/
extern	void	i7505 (void) ;			/* MQA MUY		*/
extern	void	i7507 (void) ;			/* MQA DVI		*/
extern	void	i7511 (void) ;			/* MQA NMI		*/
extern	void	i7513 (void) ;			/* MQA SHL		*/
extern	void	i7515 (void) ;			/* MQA ASR		*/
extern	void	i7517 (void) ;			/* MQA LSR		*/
extern	void	i7521 (void) ;			/* SWP			*/
extern	void	i7523 (void) ;			/* A: SWP SCL		*/
extern	void	i7525 (void) ;			/* SWP MUY		*/
extern	void	i7527 (void) ;			/* SWP DVI		*/
extern	void	i7531 (void) ;			/* SWP NMI		*/
extern	void	i7533 (void) ;			/* SWP SHL		*/
extern	void	i7535 (void) ;			/* SWP ASR		*/
extern	void	i7537 (void) ;			/* SWP LSR		*/
extern	void	i7541 (void) ;			/* MQA SCA		*/
extern	void	i7543 (void) ;			/* A: MQA SCA SCL	*/
extern	void	i7545 (void) ;			/* A: MQA SCA MUY	*/
extern	void	i7547 (void) ;			/* A: MQA SWBA		*/
extern	void	i7551 (void) ;			/* A: MQA SCA NMI	*/
extern	void	i7553 (void) ;			/* A: MQA SCA SHL	*/
extern	void	i7555 (void) ;			/* A: MQA SCA ASR	*/
extern	void	i7557 (void) ;			/* A: MQA SCA LSR	*/
extern	void	i7561 (void) ;			/* SWP SCA		*/
extern	void	i7563 (void) ;			/* A: SWP SCA SCL	*/
extern	void	i7565 (void) ;			/* A: SWP SCA MUY	*/
extern	void	i7567 (void) ;			/* SWP SWBA		*/
extern	void	i7571 (void) ;			/* A: SWP SCA NMI	*/
extern	void	i7573 (void) ;			/* A: SWP SCA SHL	*/
extern	void	i7575 (void) ;
extern	void	i7577 (void) ;			/* A: SWP SCA LSR	*/
extern	void	i7601 (void) ;			/* CLA Grp III		*/
extern	void	i7603 (void) ;			/* CLA SCA		*/
extern	void	i7605 (void) ;			/* CLA MUY		*/
extern	void	i7607 (void) ;			/* CLA DVI		*/
extern	void	i7611 (void) ;			/* CLA NMI		*/
extern	void	i7613 (void) ;			/* CLA SHL		*/
extern	void	i7615 (void) ;			/* CLA ASR		*/
extern	void	i7617 (void) ;			/* CLA LSR		*/
extern	void	i7621 (void) ;			/* CLA MQL		*/
extern	void	i7623 (void) ;			/* CLA MQL SCL		*/
extern	void	i7625 (void) ;			/* CLA MQL MUY		*/
extern	void	i7627 (void) ;			/* CLA MQL DVI		*/
extern	void	i7631 (void) ;			/* CLA MQL NMI		*/
extern	void	i7633 (void) ;			/* CLA MQL SHL		*/
extern	void	i7635 (void) ;			/* CLA MQL ASR		*/
extern	void	i7637 (void) ;			/* CLA MQL LSR		*/
extern	void	i7641 (void) ;			/* CLA SCA		*/
extern	void	i7643 (void) ;			/* A: CLA SCA SCL	*/
extern	void	i7645 (void) ;			/* A: CLA SCA MUY	*/
extern	void	i7647 (void) ;			/* CLA SWBA		*/
extern	void	i7651 (void) ;			/* A: CLA SCA NMI	*/
extern	void	i7653 (void) ;			/* A: CLA SCA SHL	*/
extern	void	i7655 (void) ;			/* A: CLA SCA ASR	*/
extern	void	i7657 (void) ;			/* A: CLA SCA LSR	*/
extern	void	i7661 (void) ;			/* CLA MQL SCA		*/
extern	void	i7663 (void) ;			/* A: CLA MQL SCA SCL	*/
extern	void	i7665 (void) ;			/* A: CLA MQL SCA MUY	*/
extern	void	i7667 (void) ;			/* CLA MQL SWBA		*/
extern	void	i7671 (void) ;			/* A: CLA MQL SCA NMI	*/
extern	void	i7673 (void) ;			/* A: CLA MQL SCA SHL	*/
extern	void	i7675 (void) ;			/* A: CLA MQL SCA ASR	*/
extern	void	i7677 (void) ;			/* A: CLA MQL SCA LSR	*/
extern	void	i7701 (void) ;			/* CLA MQA		*/
extern	void	i7703 (void) ;			/* CLA MQA SCL		*/
extern	void	i7705 (void) ;			/* CLA MQA MUY		*/
extern	void	i7707 (void) ;			/* CLA MQA DVI		*/
extern	void	i7711 (void) ;			/* CLA MQA NMI		*/
extern	void	i7713 (void) ;			/* CLA MQA SHL		*/
extern	void	i7715 (void) ;			/* CLA MQA ASR		*/
extern	void	i7717 (void) ;			/* CLA MQA LSR		*/
extern	void	i7721 (void) ;			/* CLA SWP		*/
extern	void	i7723 (void) ;			/* CLA SWP SCL		*/
extern	void	i7725 (void) ;			/* CLA SWP MUY		*/
extern	void	i7727 (void) ;			/* CLA SWP DVI		*/
extern	void	i7731 (void) ;			/* CLA SWP NMI		*/
extern	void	i7733 (void) ;			/* CLA SWP SHL		*/
extern	void	i7735 (void) ;			/* CLA SWP ASR		*/
extern	void	i7737 (void) ;			/* CLA SWP LSR		*/
extern	void	i7741 (void) ;			/* CLA MQA SCA		*/
extern	void	i7743 (void) ;			/* A: CLA MQA SCA SCL	*/
extern	void	i7745 (void) ;			/* A: CLA MQA SCA MUY	*/
extern	void	i7747 (void) ;			/* A: CLA MQA SWBA	*/
extern	void	i7751 (void) ;			/* A: CLA MQA SCA NMI	*/
extern	void	i7753 (void) ;			/* A: CLA MQA SCA SHL	*/
extern	void	i7755 (void) ;			/* A: CLA MQA SCA ASR	*/
extern	void	i7757 (void) ;			/* A: CLA MQA SCA LSR	*/
extern	void	i7761 (void) ;			/* CLA SWP SCA		*/
extern	void	i7763 (void) ;			/* A: CLA SWP SCA SCL	*/
extern	void	i7765 (void) ;			/* A: CLA SWP SCA MUY	*/
extern	void	i7767 (void) ;			/* A: CLA SWP SCA DVI	*/
extern	void	i7771 (void) ;			/* CLA SWP DPSZ		*/
extern	void	i7773 (void) ;			/* A: CLA SWP SCA SHL	*/
extern	void	i7775 (void) ;
extern	void	i7777 (void) ;			/* A: CLA SWP SCA LSR	*/
/* -------------------------------------------------------------------- */
