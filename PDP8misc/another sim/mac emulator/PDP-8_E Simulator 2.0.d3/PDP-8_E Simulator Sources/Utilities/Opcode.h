/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Opcode.h - Class representing a PDP-8 opcode
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


@interface Opcode : NSObject <NSCopying>
{
@private
	int		address;	/* PDP-8 address used while assembly */
	int		word0;		/* first 12-bit word of the opcode, -1 == invalid */
	int		word1;		/* optional second word, -1 == not used */
}

+ (Opcode *) opcodeWithAddress:(int)addr;
+ (Opcode *) opcodeWithAddress:(int)addr value:(int)value;

- (Opcode *) initWithAddress:(int)addr;
- (Opcode *) initWithAddress:(int)addr value:(int)value;
- (int) address;
- (int) word0;
- (void) setWord0:(int)w1;
- (int) word1;
- (void) setWord1:(int)w2;

@end
