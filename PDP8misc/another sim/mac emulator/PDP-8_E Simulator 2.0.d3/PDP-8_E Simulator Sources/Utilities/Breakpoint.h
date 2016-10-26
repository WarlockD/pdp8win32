/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Breakpoint.h - Class for a single breakpoint
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


// Enable bits for breakpoints and break opcodes; "value" of a breakpoint
#define BREAKPOINT		1
#define USERMODE_BREAKOPCODE	1
#define SYSTEMMODE_BREAKOPCODE	2
#define BREAKOPCODE		(USERMODE_BREAKOPCODE | SYSTEMMODE_BREAKOPCODE)


@interface Breakpoint : NSObject <NSCoding>
{
@private
	unsigned	identifier;	// address for normal breakpoint, opcode for break-opcode
	unsigned	value;		// see defines above
}

+ (Breakpoint *) breakpointWithIdentifier:(unsigned)ident value:(unsigned)val;

- (Breakpoint *) initWithIdentifier:(unsigned)ident value:(unsigned)val;
- (unsigned) identifier;
- (unsigned) value;
- (void) setValue:(unsigned)val;
- (NSComparisonResult) compareAddress:(Breakpoint *)breakpoint;

@end
