/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	opr.h - Interface for the operate instructions code for the PDP-8/E
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
 *	for all OPRs except EAE.
 */
/* -------------------------------------------------------------------- */
extern	void	i7000 (void) ;		/* NOP				*/
extern	void	i7001 (void) ;		/* IAC				*/
extern	void	i7002 (void) ;		/* BSW				*/
extern	void	i7003 (void) ;		/* IAC BSW			*/
extern	void	i7004 (void) ;		/* RAL				*/
extern	void	i7005 (void) ;		/* IAC RAL			*/
extern	void	i7006 (void) ;		/* RTL				*/
extern	void	i7007 (void) ;		/* IAC RTL			*/
extern	void	i7010 (void) ;		/* RAR				*/
extern	void	i7011 (void) ;		/* IAC RAR			*/
extern	void	i7012 (void) ;		/* RTR				*/
extern	void	i7013 (void) ;		/* IAC RTR			*/
extern	void	i7014 (void) ;		/* RAR RAL (reserved)		*/
extern	void	i7015 (void) ;		/* IAC RAR RAL (reserved)	*/
extern	void	i7016 (void) ;		/* RTR RTL (reserved)		*/
extern	void	i7017 (void) ;		/* IAC RTR RTL (reserved)	*/
extern	void	i7020 (void) ;		/* CML				*/
extern	void	i7021 (void) ;		/* CML IAC			*/
extern	void	i7022 (void) ;		/* CML BSW			*/
extern	void	i7023 (void) ;		/* CML IAC BSW			*/
extern	void	i7024 (void) ;		/* CML RAL			*/
extern	void	i7025 (void) ;		/* CML IAC RAL			*/
extern	void	i7026 (void) ;		/* CML RTL			*/
extern	void	i7027 (void) ;		/* CML IAC RTL			*/
extern	void	i7030 (void) ;		/* CML RAR			*/
extern	void	i7031 (void) ;		/* CML IAC RAR			*/
extern	void	i7032 (void) ;		/* CML RTR			*/
extern	void	i7033 (void) ;		/* CML IAC RTR			*/
extern	void	i7034 (void) ;		/* CML RAR RAL (reserved)	*/
extern	void	i7035 (void) ;		/* CML IAC RAR RAL (reserved)	*/
extern	void	i7036 (void) ;		/* CML RTR RTL (reserved)	*/
extern	void	i7037 (void) ;		/* CML IAC RTR RTL (reserved)	*/
extern	void	i7040 (void) ;		/* CMA				*/
extern	void	i7041 (void) ;		/* CMA IAC			*/
extern	void	i7042 (void) ;		/* CMA BSW			*/
extern	void	i7043 (void) ;		/* CMA IAC BSW			*/
extern	void	i7044 (void) ;		/* CMA RAL			*/
extern	void	i7045 (void) ;		/* CMA IAC RAL			*/
extern	void	i7046 (void) ;		/* CMA RTL			*/
extern	void	i7047 (void) ;		/* CMA IAC RTL			*/
extern	void	i7050 (void) ;		/* CMA RAR			*/
extern	void	i7051 (void) ;		/* CMA IAC RAR			*/
extern	void	i7052 (void) ;		/* CMA RTR			*/
extern	void	i7053 (void) ;		/* CMA IAC RTR			*/
extern	void	i7054 (void) ;		/* CMA RAR RAL (reserved)	*/
extern	void	i7055 (void) ;		/* CMA IAC RAR RAL (reserved)	*/
extern	void	i7056 (void) ;		/* CMA RTR RTL (reserved)	*/
extern	void	i7057 (void) ;		/* CMA IAC RTR RTL (reserved)	*/
extern	void	i7060 (void) ;		/* CMA CML			*/
extern	void	i7061 (void) ;		/* CMA CML IAC			*/
extern	void	i7062 (void) ;		/* CMA CML BSW			*/
extern	void	i7063 (void) ;		/* CMA CML IAC BSW		*/
extern	void	i7064 (void) ;		/* CMA CML RAL			*/
extern	void	i7065 (void) ;		/* CMA CML IAC RAL		*/
extern	void	i7066 (void) ;		/* CMA CML RTL			*/
extern	void	i7067 (void) ;		/* CMA CML IAC RTL		*/
extern	void	i7070 (void) ;		/* CMA CML RAR			*/
extern	void	i7071 (void) ;		/* CMA CML IAC RAR		*/
extern	void	i7072 (void) ;		/* CMA CML RTR			*/
extern	void	i7073 (void) ;		/* CMA CML IAC RTR		*/
extern	void	i7074 (void) ;		/* CMA CML RAR RAL (reserved)	*/
extern	void	i7075 (void) ;		/* CMA CML IAC RAR RAL		*/
extern	void	i7076 (void) ;		/* CMA CML RTR RTL (reserved)	*/
extern	void	i7077 (void) ;		/* CMA CML IAC RTR RTL		*/
extern	void	i7100 (void) ;		/* CLL				*/
extern	void	i7101 (void) ;		/* CLL IAC			*/
extern	void	i7102 (void) ;		/* CLL BSW			*/
extern	void	i7103 (void) ;		/* CLL IAC BSW			*/
extern	void	i7104 (void) ;		/* CLL RAL			*/
extern	void	i7105 (void) ;		/* CLL IAC RAL			*/
extern	void	i7106 (void) ;		/* CLL RTL			*/
extern	void	i7107 (void) ;		/* CLL IAC RTL			*/
extern	void	i7110 (void) ;		/* CLL RAR			*/
extern	void	i7111 (void) ;		/* CLL IAC RAR			*/
extern	void	i7112 (void) ;		/* CLL RTR			*/
extern	void	i7113 (void) ;		/* CLL IAC RTR			*/
extern	void	i7114 (void) ;		/* CLL RAR RAL (reserved)	*/
extern	void	i7115 (void) ;		/* CLL IAC RAR RAL (reserved)	*/
extern	void	i7116 (void) ;		/* CLL RTR RTL (reserved)	*/
extern	void	i7117 (void) ;		/* CLL IAC RTR RTL (reserved)	*/
extern	void	i7120 (void) ;		/* CLL CML			*/
extern	void	i7121 (void) ;		/* CLL CML IAC			*/
extern	void	i7122 (void) ;		/* CLL CML BSW			*/
extern	void	i7123 (void) ;		/* CLL CML IAC BSW		*/
extern	void	i7124 (void) ;		/* CLL CML RAL			*/
extern	void	i7125 (void) ;		/* CLL CML IAC RAL		*/
extern	void	i7126 (void) ;		/* CLL CML RTL			*/
extern	void	i7127 (void) ;		/* CLL CML IAC RTL		*/
extern	void	i7130 (void) ;		/* CLL CML RAR			*/
extern	void	i7131 (void) ;		/* CLL CML IAC RAR		*/
extern	void	i7132 (void) ;		/* CLL CML RTR			*/
extern	void	i7133 (void) ;		/* CLL CML IAC RTR		*/
extern	void	i7134 (void) ;		/* CLL CML RAR RAL (reserved)	*/
extern	void	i7135 (void) ;		/* CLL CML IAC RAR RAL		*/
extern	void	i7136 (void) ;		/* CLL CML RTR RTL (reserved)	*/
extern	void	i7137 (void) ;		/* CLL CML IAC RTR RTL		*/
extern	void	i7140 (void) ;		/* CLL CMA			*/
extern	void	i7141 (void) ;		/* CLL CMA IAC			*/
extern	void	i7142 (void) ;		/* CLL CMA BSW			*/
extern	void	i7143 (void) ;		/* CLL CMA IAC BSW		*/
extern	void	i7144 (void) ;		/* CLL CMA RAL			*/
extern	void	i7145 (void) ;		/* CLL CMA IAC RAL		*/
extern	void	i7146 (void) ;		/* CLL CMA RTL			*/
extern	void	i7147 (void) ;		/* CLL CMA IAC RTL		*/
extern	void	i7150 (void) ;		/* CLL CMA RAR			*/
extern	void	i7151 (void) ;		/* CLL CMA IAC RAR		*/
extern	void	i7152 (void) ;		/* CLL CMA RTR			*/
extern	void	i7153 (void) ;		/* CLL CMA IAC RTR		*/
extern	void	i7154 (void) ;		/* CLL CMA RAR RAL (reserved)	*/
extern	void	i7155 (void) ;		/* CLL CMA IAC RAR RAL		*/
extern	void	i7156 (void) ;		/* CLL CMA RTR RTL (reserved)	*/
extern	void	i7157 (void) ;		/* CLL CMA IAC RTR RTL		*/
extern	void	i7160 (void) ;		/* CLL CMA CML			*/
extern	void	i7161 (void) ;		/* CLL CMA CML IAC		*/
extern	void	i7162 (void) ;		/* CLL CMA CML BSW		*/
extern	void	i7163 (void) ;		/* CLL CMA CML IAC BSW		*/
extern	void	i7164 (void) ;		/* CLL CMA CML RAL		*/
extern	void	i7165 (void) ;		/* CLL CMA CML IAC RAL		*/
extern	void	i7166 (void) ;		/* CLL CMA CML RTL		*/
extern	void	i7167 (void) ;		/* CLL CMA CML IAC RTL		*/
extern	void	i7170 (void) ;		/* CLL CMA CML RAR		*/
extern	void	i7171 (void) ;		/* CLL CMA CML IAC RAR		*/
extern	void	i7172 (void) ;		/* CLL CMA CML RTR		*/
extern	void	i7173 (void) ;		/* CLL CMA CML IAC RTR		*/
extern	void	i7174 (void) ;		/* CLL CMA CML RAR RAL		*/
extern	void	i7175 (void) ;		/* CLL CMA CML IAC RAR RAL	*/
extern	void	i7176 (void) ;		/* CLL CMA CML RTR RTL		*/
extern	void	i7177 (void) ;		/* CLL CMA CML IAC RTR RTL	*/
extern	void	i7200 (void) ;		/* CLA				*/
extern	void	i7201 (void) ;		/* CLA IAC			*/
extern	void	i7202 (void) ;		/* CLA BSW			*/
extern	void	i7203 (void) ;		/* CLA IAC BSW			*/
extern	void	i7204 (void) ;		/* CLA RAL			*/
extern	void	i7205 (void) ;		/* CLA IAC RAL			*/
extern	void	i7206 (void) ;		/* CLA RTL			*/
extern	void	i7207 (void) ;		/* CLA IAC RTL			*/
extern	void	i7210 (void) ;		/* CLA RAR			*/
extern	void	i7211 (void) ;		/* CLA IAC RAR			*/
extern	void	i7212 (void) ;		/* CLA RTR			*/
extern	void	i7213 (void) ;		/* CLA IAC RTR			*/
extern	void	i7214 (void) ;		/* CLA RAR RAL (reserved)	*/
extern	void	i7215 (void) ;		/* CLA IAC RAR RAL (reserved)	*/
extern	void	i7216 (void) ;		/* CLA RTR RTL (reserved)	*/
extern	void	i7217 (void) ;		/* CLA IAC RTR RTL (reserved)	*/
extern	void	i7220 (void) ;		/* CLA CML			*/
extern	void	i7221 (void) ;		/* CLA CML IAC			*/
extern	void	i7222 (void) ;		/* CLA CML BSW			*/
extern	void	i7223 (void) ;		/* CLA CML IAC BSW		*/
extern	void	i7224 (void) ;		/* CLA CML RAL			*/
extern	void	i7225 (void) ;		/* CLA CML IAC RAL		*/
extern	void	i7226 (void) ;		/* CLA CML RTL			*/
extern	void	i7227 (void) ;		/* CLA CML IAC RTL		*/
extern	void	i7230 (void) ;		/* CLA CML RAR			*/
extern	void	i7231 (void) ;		/* CLA CML IAC RAR		*/
extern	void	i7232 (void) ;		/* CLA CML RTR			*/
extern	void	i7233 (void) ;		/* CLA CML IAC RTR		*/
extern	void	i7234 (void) ;		/* CLA CML RAR RAL (reserved)	*/
extern	void	i7235 (void) ;		/* CLA CML IAC RAR RAL		*/
extern	void	i7236 (void) ;		/* CLA CML RTR RTL (reserved)	*/
extern	void	i7237 (void) ;		/* CLA CML IAC RTR RTL		*/
extern	void	i7240 (void) ;		/* CLA CMA			*/
extern	void	i7241 (void) ;		/* CLA CMA IAC			*/
extern	void	i7242 (void) ;		/* CLA CMA BSW			*/
extern	void	i7243 (void) ;		/* CLA CMA IAC BSW		*/
extern	void	i7244 (void) ;		/* CLA CMA RAL			*/
extern	void	i7245 (void) ;		/* CLA CMA IAC RAL		*/
extern	void	i7246 (void) ;		/* CLA CMA RTL			*/
extern	void	i7247 (void) ;		/* CLA CMA IAC RTL		*/
extern	void	i7250 (void) ;		/* CLA CMA RAR			*/
extern	void	i7251 (void) ;		/* CLA CMA IAC RAR		*/
extern	void	i7252 (void) ;		/* CLA CMA RTR			*/
extern	void	i7253 (void) ;		/* CLA CMA IAC RTR		*/
extern	void	i7254 (void) ;		/* CLA CMA RAR RAL (reserved)	*/
extern	void	i7255 (void) ;		/* CLA CMA IAC RAR RAL		*/
extern	void	i7256 (void) ;		/* CLA CMA RTR RTL (reserved)	*/
extern	void	i7257 (void) ;		/* CLA CMA IAC RTR RTL		*/
extern	void	i7260 (void) ;		/* CLA CMA CML			*/
extern	void	i7261 (void) ;		/* CLA CMA CML IAC		*/
extern	void	i7262 (void) ;		/* CLA CMA CML BSW		*/
extern	void	i7263 (void) ;		/* CLA CMA CML IAC BSW		*/
extern	void	i7264 (void) ;		/* CLA CMA CML RAL		*/
extern	void	i7265 (void) ;		/* CLA CMA CML IAC RAL		*/
extern	void	i7266 (void) ;		/* CLA CMA CML RTL		*/
extern	void	i7267 (void) ;		/* CLA CMA CML IAC RTL		*/
extern	void	i7270 (void) ;		/* CLA CMA CML RAR		*/
extern	void	i7271 (void) ;		/* CLA CMA CML IAC RAR		*/
extern	void	i7272 (void) ;		/* CLA CMA CML RTR		*/
extern	void	i7273 (void) ;		/* CLA CMA CML IAC RTR		*/
extern	void	i7274 (void) ;		/* CLA CMA CML RAR RAL		*/
extern	void	i7275 (void) ;		/* CLA CMA CML IAC RAR RAL	*/
extern	void	i7276 (void) ;		/* CLA CMA CML RTR RTL		*/
extern	void	i7277 (void) ;		/* CLA CMA CML IAC RTR RTL	*/
extern	void	i7300 (void) ;		/* CLA CLL			*/
extern	void	i7301 (void) ;		/* CLA CLL IAC			*/
extern	void	i7302 (void) ;		/* CLA CLL BSW			*/
extern	void	i7303 (void) ;		/* CLA CLL IAC BSW		*/
extern	void	i7304 (void) ;		/* CLA CLL RAL			*/
extern	void	i7305 (void) ;		/* CLA CLL IAC RAL		*/
extern	void	i7306 (void) ;		/* CLA CLL RTL			*/
extern	void	i7307 (void) ;		/* CLA CLL IAC RTL		*/
extern	void	i7310 (void) ;		/* CLA CLL RAR			*/
extern	void	i7311 (void) ;		/* CLA CLL IAC RAR		*/
extern	void	i7312 (void) ;		/* CLA CLL RTR			*/
extern	void	i7313 (void) ;		/* CLA CLL IAC RTR		*/
extern	void	i7314 (void) ;		/* CLA CLL RAR RAL (reserved)	*/
extern	void	i7315 (void) ;		/* CLA CLL IAC RAR RAL		*/
extern	void	i7316 (void) ;		/* CLA CLL RTR RTL (reserved)	*/
extern	void	i7317 (void) ;		/* CLA CLL IAC RTR RTL		*/
extern	void	i7320 (void) ;		/* CLA CLL CML			*/
extern	void	i7321 (void) ;		/* CLA CLL CML IAC		*/
extern	void	i7322 (void) ;		/* CLA CLL CML BSW		*/
extern	void	i7323 (void) ;		/* CLA CLL CML IAC BSW		*/
extern	void	i7324 (void) ;		/* CLA CLL CML RAL		*/
extern	void	i7325 (void) ;		/* CLA CLL CML IAC RAL		*/
extern	void	i7326 (void) ;		/* CLA CLL CML RTL		*/
extern	void	i7327 (void) ;		/* CLA CLL CML IAC RTL		*/
extern	void	i7330 (void) ;		/* CLA CLL CML RAR		*/
extern	void	i7331 (void) ;		/* CLA CLL CML IAC RAR		*/
extern	void	i7332 (void) ;		/* CLA CLL CML RTR		*/
extern	void	i7333 (void) ;		/* CLA CLL CML IAC RTR		*/
extern	void	i7334 (void) ;		/* CLA CLL CML RAR RAL		*/
extern	void	i7335 (void) ;		/* CLA CLL CML IAC RAR RAL	*/
extern	void	i7336 (void) ;		/* CLA CLL CML RTR RTL		*/
extern	void	i7337 (void) ;		/* CLA CLL CML IAC RTR RTL	*/
extern	void	i7340 (void) ;		/* CLA CLL CMA			*/
extern	void	i7341 (void) ;		/* CLA CLL CMA IAC		*/
extern	void	i7342 (void) ;		/* CLA CLL CMA BSW		*/
extern	void	i7343 (void) ;		/* CLA CLL CMA IAC BSW		*/
extern	void	i7344 (void) ;		/* CLA CLL CMA RAL		*/
extern	void	i7345 (void) ;		/* CLA CLL CMA IAC RAL		*/
extern	void	i7346 (void) ;		/* CLA CLL CMA RTL		*/
extern	void	i7347 (void) ;		/* CLA CLL CMA IAC RTL		*/
extern	void	i7350 (void) ;		/* CLA CLL CMA RAR		*/
extern	void	i7351 (void) ;		/* CLA CLL CMA IAC RAR		*/
extern	void	i7352 (void) ;		/* CLA CLL CMA RTR		*/
extern	void	i7353 (void) ;		/* CLA CLL CMA IAC RTR		*/
extern	void	i7354 (void) ;		/* CLA CLL CMA RAR RAL		*/
extern	void	i7355 (void) ;		/* CLA CLL CMA IAC RAR RAL	*/
extern	void	i7356 (void) ;		/* CLA CLL CMA RTR RTL		*/
extern	void	i7357 (void) ;		/* CLA CLL CMA IAC RTR RTL	*/
extern	void	i7360 (void) ;		/* CLA CLL CMA CML		*/
extern	void	i7361 (void) ;		/* CLA CLL CMA CML IAC		*/
extern	void	i7362 (void) ;		/* CLA CLL CMA CML BSW		*/
extern	void	i7363 (void) ;		/* CLA CLL CMA CML IAC BSW	*/
extern	void	i7364 (void) ;		/* CLA CLL CMA CML RAL		*/
extern	void	i7365 (void) ;		/* CLA CLL CMA CML IAC RAL	*/
extern	void	i7366 (void) ;		/* CLA CLL CMA CML RTL		*/
extern	void	i7367 (void) ;		/* CLA CLL CMA CML IAC RTL	*/
extern	void	i7370 (void) ;		/* CLA CLL CMA CML RAR		*/
extern	void	i7371 (void) ;		/* CLA CLL CMA CML IAC RAR	*/
extern	void	i7372 (void) ;		/* CLA CLL CMA CML RTR		*/
extern	void	i7373 (void) ;		/* CLA CLL CMA CML IAC RTR	*/
extern	void	i7374 (void) ;		/* CLA CLL CMA CML RAR RAL	*/
extern	void	i7375 (void) ;		/* CLA CLL CMA CML IAC RAR RAL	*/
extern	void	i7376 (void) ;		/* CLA CLL CMA CML RTR RTL	*/
extern	void	i7377 (void) ;		/* CLA CLL CMA CML IAC RTR RTL	*/
extern	void	i7400 (void) ;		/* NOP	Grp II			*/
extern	void	i7402 (void) ;		/* HLT				*/
extern	void	i7404 (void) ;		/* OSR				*/
extern	void	i7406 (void) ;		/* OSR HLT			*/
extern	void	i7410 (void) ;		/* SKP				*/
extern	unsigned s7410 (void) ;		/* SKP (skip test)		*/
extern	void	i7412 (void) ;		/* SKP HLT			*/
extern	void	u7412 (void) ;		/* SKP HLT (user mode)		*/
extern	void	i7414 (void) ;		/* SKP OSR			*/
extern	void	i7416 (void) ;		/* SKP OSR HLT			*/
extern	void	i7420 (void) ;		/* SNL				*/
extern	unsigned s7420 (void) ;		/* SNL (skip test)		*/
extern	void	i7422 (void) ;		/* SNL HLT			*/
extern	void	u7422 (void) ;		/* SNL HLT (user mode)		*/
extern	void	i7424 (void) ;		/* SNL OSR			*/
extern	void	i7426 (void) ;		/* SNL OSR HLT			*/
extern	void	i7430 (void) ;		/* SZL				*/
extern	unsigned s7430 (void) ;		/* SZL (skipt test)		*/
extern	void	i7432 (void) ;		/* SZL HLT			*/
extern	void	u7432 (void) ;		/* SZL HLT (user mode)		*/
extern	void	i7434 (void) ;		/* SZL OSR			*/
extern	void	i7436 (void) ;		/* SZL OSR HLT			*/
extern	void	i7440 (void) ;		/* SZA				*/
extern	unsigned s7440 (void) ;		/* SZA (skip test)		*/
extern	void	i7442 (void) ;		/* SZA HLT			*/
extern	void	u7442 (void) ;		/* SZA HLT (user mode)		*/
extern	void	i7444 (void) ;		/* SZA OSR			*/
extern	void	i7446 (void) ;		/* SZA OSR HLT			*/
extern	void	i7450 (void) ;		/* SNA				*/
extern	unsigned s7450 (void) ;		/* SNA (skip test)		*/
extern	void	i7452 (void) ;		/* SNA HLT			*/
extern	void	u7452 (void) ;		/* SNA HLT (user mode)		*/
extern	void	i7454 (void) ;		/* SNA OSR			*/
extern	void	i7456 (void) ;		/* SNA OSR HLT			*/
extern	void	i7460 (void) ;		/* SZA SNL			*/
extern	unsigned s7460 (void) ;		/* SZA SNL (skip test)		*/
extern	void	i7462 (void) ;		/* SZA SNL HLT			*/
extern	void	u7462 (void) ;		/* SZA SNL HLT (user mode)	*/
extern	void	i7464 (void) ;		/* SZA SNL OSR			*/
extern	void	i7466 (void) ;		/* SZA SNL OSR HLT		*/
extern	void	i7470 (void) ;		/* SNA SZL			*/
extern	unsigned s7470 (void) ;		/* SNA SZL (skip test)		*/
extern	void	i7472 (void) ;		/* SNA SZL HLT			*/
extern	void	u7472 (void) ;		/* SNA SZL HLT (user mode)	*/
extern	void	i7474 (void) ;		/* SNA SZL OSR			*/
extern	void	i7476 (void) ;		/* SNA SZL OSR HLT		*/
extern	void	i7500 (void) ;		/* SMA				*/
extern	unsigned s7500 (void) ;		/* SMA (skip test)		*/
extern	void	i7502 (void) ;		/* SMA HLT			*/
extern	void	u7502 (void) ;		/* SMA HLT (user mode)		*/
extern	void	i7504 (void) ;		/* SMA OSR			*/
extern	void	i7506 (void) ;		/* SMA OSR HLT			*/
extern	void	i7510 (void) ;		/* SPA				*/
extern	unsigned s7510 (void) ;		/* SPA (skip test)		*/
extern	void	i7512 (void) ;		/* SPA HLT			*/
extern	void	u7512 (void) ;		/* SPA HLT (user mode)		*/
extern	void	i7514 (void) ;		/* SPA OSR			*/
extern	void	i7516 (void) ;		/* SPA OSR HLT			*/
extern	void	i7520 (void) ;		/* SMA SNL			*/
extern	unsigned s7520 (void) ;		/* SMA SNL (skip test)		*/
extern	void	i7522 (void) ;		/* SMA SNL HLT			*/
extern	void	u7522 (void) ;		/* SMA SNL HLT (user mode)	*/
extern	void	i7524 (void) ;		/* SMA SNL OSR			*/
extern	void	i7526 (void) ;		/* SMA SNL OSR HLT		*/
extern	void	i7530 (void) ;		/* SPA SZL			*/
extern	unsigned s7530 (void) ;		/* SPA SZL (skip test)		*/
extern	void	i7532 (void) ;		/* SPA SZL HLT			*/
extern	void	u7532 (void) ;		/* SPA SZL HLT (user mode)	*/
extern	void	i7534 (void) ;		/* SPA SZL OSR			*/
extern	void	i7536 (void) ;		/* SPA SZL OSR HLT		*/
extern	void	i7540 (void) ;		/* SMA SZA			*/
extern	unsigned s7540 (void) ;		/* SMA SZA (skip test)		*/
extern	void	i7542 (void) ;		/* SMA SZA HLT			*/
extern	void	u7542 (void) ;		/* SMA SZA HLT (user mode)	*/
extern	void	i7544 (void) ;		/* SMA SZA OSR			*/
extern	void	i7546 (void) ;		/* SMA SZA OSR HLT		*/
extern	void	i7550 (void) ;		/* SPA SNA			*/
extern	unsigned s7550 (void) ;		/* SPA SNA (skip test)		*/
extern	void	i7552 (void) ;		/* SPA SNA HLT			*/
extern	void	u7552 (void) ;		/* SPA SNA HLT (user mode)	*/
extern	void	i7554 (void) ;		/* SPA SNA OSR			*/
extern	void	i7556 (void) ;		/* SPA SNA OSR HLT		*/
extern	void	i7560 (void) ;		/* SMA SZA SNL			*/
extern	unsigned s7560 (void) ;		/* SMA SZA SNL (skip test)	*/
extern	void	i7562 (void) ;		/* SMA SZA SNL HLT		*/
extern	void	u7562 (void) ;		/* SMA SZA SNL HLT (user mode)	*/
extern	void	i7564 (void) ;		/* SMA SZA SNL OSR		*/
extern	void	i7566 (void) ;		/* SMA SZA SNL OSR HLT		*/
extern	void	i7570 (void) ;		/* SPA SNA SZL			*/
extern	unsigned s7570 (void) ;		/* SPA SNA SZL (skip test)	*/
extern	void	i7572 (void) ;		/* SPA SNA SZL HLT		*/
extern	void	u7572 (void) ;		/* SPA SNA SZL HLT (user mode)	*/
extern	void	i7574 (void) ;		/* SPA SNA SZL OSR		*/
extern	void	i7576 (void) ;		/* SPA SNA SZL OSR HLT		*/
extern	void	i7600 (void) ;		/* CLA Grp II			*/
extern	void	i7602 (void) ;		/* CLA HLT			*/
extern	void	u7602 (void) ;		/* CLA HLT (user mode)		*/
extern	void	i7604 (void) ;		/* CLA OSR			*/
extern	void	i7606 (void) ;		/* CLA OSR HLT			*/
extern	void	i7610 (void) ;		/* SKP CLA			*/
extern	void	i7612 (void) ;		/* SKP CLA HLT			*/
extern	void	u7612 (void) ;		/* SKP CLA HLT (user mode)	*/
extern	void	i7614 (void) ;		/* SKP CLA OSR			*/
extern	void	i7616 (void) ;		/* SKP CLA OSR HLT		*/
extern	void	i7620 (void) ;		/* SNL CLA			*/
extern	void	i7622 (void) ;		/* SNL CLA HLT			*/
extern	void	u7622 (void) ;		/* SNL CLA HLT (user mode)	*/
extern	void	i7624 (void) ;		/* SNL CLA OSR			*/
extern	void	i7626 (void) ;		/* SNL CLA OSR HLT		*/
extern	void	i7630 (void) ;		/* SZL CLA			*/
extern	void	i7632 (void) ;		/* SZL CLA HLT			*/
extern	void	u7632 (void) ;		/* SZL CLA HLT (user mode)	*/
extern	void	i7634 (void) ;		/* SZL CLA OSR			*/
extern	void	i7636 (void) ;		/* SZL CLA OSR HLT		*/
extern	void	i7640 (void) ;		/* SZA CLA			*/
extern	void	i7642 (void) ;		/* SZA CLA HLT			*/
extern	void	u7642 (void) ;		/* SZA CLA HLT (user mode)	*/
extern	void	i7644 (void) ;		/* SZA CLA OSR			*/
extern	void	i7646 (void) ;		/* SZA CLA OSR HLT		*/
extern	void	i7650 (void) ;		/* SNA CLA			*/
extern	void	i7652 (void) ;		/* SNA CLA HLT			*/
extern	void	u7652 (void) ;		/* SNA CLA HLT (user mode)	*/
extern	void	i7654 (void) ;		/* SNA CLA OSR			*/
extern	void	i7656 (void) ;		/* SNA CLA OSR HLT		*/
extern	void	i7660 (void) ;		/* SZA SNL CLA			*/
extern	void	i7662 (void) ;		/* SZA SNL CLA HLT		*/
extern	void	u7662 (void) ;		/* SZA SNL CLA HLT (user mode)	*/
extern	void	i7664 (void) ;		/* SZA SNL CLA OSR		*/
extern	void	i7666 (void) ;		/* SZA SNL CLA OSR HLT		*/
extern	void	i7670 (void) ;		/* SNA SZL CLA			*/
extern	void	i7672 (void) ;		/* SNA SZL CLA HLT		*/
extern	void	u7672 (void) ;		/* SNA SZL CLA HLT (user mode)	*/
extern	void	i7674 (void) ;		/* SNA SZL CLA OSR		*/
extern	void	i7676 (void) ;		/* SNA SZL CLA OSR HLT		*/
extern	void	i7700 (void) ;		/* SMA CLA			*/
extern	void	i7702 (void) ;		/* SMA CLA HLT			*/
extern	void	u7702 (void) ;		/* SMA CLA HLT			*/
extern	void	i7704 (void) ;		/* SMA CLA OSR			*/
extern	void	i7706 (void) ;		/* SMA CLA OSR HLT		*/
extern	void	i7710 (void) ;		/* SPA CLA			*/
extern	void	i7712 (void) ;		/* SPA CLA HLT			*/
extern	void	u7712 (void) ;		/* SPA CLA HLT (user mode)	*/
extern	void	i7714 (void) ;		/* SPA CLA OSR			*/
extern	void	i7716 (void) ;		/* SPA CLA OSR HLT		*/
extern	void	i7720 (void) ;		/* SMA SNL CLA			*/
extern	void	i7722 (void) ;		/* SMA SNL CLA HLT		*/
extern	void	u7722 (void) ;		/* SMA SNL CLA HLT (user mode)	*/
extern	void	i7724 (void) ;		/* SMA SNL CLA OSR		*/
extern	void	i7726 (void) ;		/* SMA SNL CLA OSR HLT		*/
extern	void	i7730 (void) ;		/* SPA SZL CLA			*/
extern	void	i7732 (void) ;		/* SPA SZL CLA HLT		*/
extern	void	u7732 (void) ;		/* SPA SZL CLA HLT (user mode)	*/
extern	void	i7734 (void) ;		/* SPA SZL CLA OSR		*/
extern	void	i7736 (void) ;		/* SPA SZL CLA OSR HLT		*/
extern	void	i7740 (void) ;		/* SMA SZA CLA			*/
extern	void	i7742 (void) ;		/* SMA SZA CLA HLT		*/
extern	void	u7742 (void) ;		/* SMA SZA CLA HLT (user mode)	*/
extern	void	i7744 (void) ;		/* SMA SZA CLA OSR		*/
extern	void	i7746 (void) ;		/* SMA SZA CLA OSR HLT		*/
extern	void	i7750 (void) ;		/* SPA SNA CLA			*/
extern	void	i7752 (void) ;		/* SPA SNA CLA HLT		*/
extern	void	u7752 (void) ;		/* SPA SNA CLA HLT (user mode)	*/
extern	void	i7754 (void) ;		/* SPA SNA CLA OSR		*/
extern	void	i7756 (void) ;		/* SPA SNA CLA OSR HLT		*/
extern	void	i7760 (void) ;		/* SMA SZA SNL CLA		*/
extern	void	i7762 (void) ;		/* SMA SZA SNL CLA HLT		*/
extern	void	u7762 (void) ;		/* SMA SZA SNL CLA HLT		*/
extern	void	i7764 (void) ;		/* SMA SZA SNL CLA OSR		*/
extern	void	i7766 (void) ;		/* SMA SZA SNL CLA OSR HLT	*/
extern	void	i7770 (void) ;		/* SPA SNA SZL CLA		*/
extern	void	i7772 (void) ;		/* SPA SNA SZL CLA HLT		*/
extern	void	u7772 (void) ;		/* SPA SNA SZL CLA HLT		*/
extern	void	i7774 (void) ;		/* SPA SNA SZL CLA OSR		*/
extern	void	i7776 (void) ;		/* SPA SNA SZL CLA OSR HLT	*/
/* -------------------------------------------------------------------- */
