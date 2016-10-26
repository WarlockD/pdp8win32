/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	StateMachine.h - State Machine for the KC8-EA Programmer’s Console
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


#import <Cocoa/Cocoa.h>


@class PDP8;


@interface StateMachine : NSObject <NSCoding> {
@private
	PDP8			*pdp8;
	unsigned short		cycle;
	unsigned short		md;
	unsigned short		mdDir;
	unsigned short		ir;
	unsigned short		cpma;
	unsigned short		bus;
	BOOL			pause;
	unsigned short		autoindex;
	BOOL			interruptInProgress;
	unsigned short		oldpc;
	unsigned short		oldinst;
}

- (StateMachine *) initWithPDP8:(PDP8 *)p8;
- (StateMachine *) initWithCoder:(NSCoder *)coder pdp8:(PDP8 *)p8;

- (unsigned short) state:(BOOL)sw;
- (unsigned short) status;
- (unsigned short) md;
- (unsigned short) bus;
- (unsigned short) cpma;

- (BOOL) isInFetchCycle;
- (void) loadAddress;
- (void) loadExtendedAddress;
- (void) updateState:(BOOL)exitFromGo;
- (void) executeSingleCycle;
- (void) examine;
- (void) deposit;

@end
