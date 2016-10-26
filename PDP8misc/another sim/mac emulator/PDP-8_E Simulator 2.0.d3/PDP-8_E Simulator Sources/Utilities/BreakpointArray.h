/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	BreakpointArray.h - Container for a sorted array of breakpoints
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


@class Breakpoint;


#define BREAKPOINTS_CHANGED_NOTIFICATION	@"BreakpointsChangedNotification"


@interface BreakpointArray : NSObject
{
@private
	NSString		*prefsKey;
	NSMutableArray		*breakpoints;
}

- (void) loadFromPrefs:(NSString *)key;

- (BOOL) hasBreakpointWithValueNotEqualTo:(unsigned)value;

- (unsigned) valueForIdentifier:(unsigned)ident;
- (unsigned) setBreakpointWithIdentifier:(unsigned)ident value:(unsigned)val;
- (NSIndexSet *) setBreakpointsWithIdentifierArray:(NSArray *)idents value:(unsigned)val;

- (unsigned) identifierAtIndex:(unsigned)index;
- (unsigned) valueAtIndex:(unsigned)index;
- (void) setBreakpointAtIndex:(unsigned)index value:(unsigned)val;
- (void) deleteBreakpointAtIndex:(unsigned)index;
- (void) deleteBreakpointsAtIndexes:(NSIndexSet *)indexes;

- (void) setAllValues:(unsigned)val;

- (int) numberOfBreakpoints;
- (NSEnumerator *) enumerator;

@end

