/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	BreakpointArray.m - Container for a sorted array of breakpoints
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


#import "BreakpointArray.h"
#import "Breakpoint.h"
#import "NSMutableArray+BinarySearch.h"


@implementation BreakpointArray


- (void) awakeFromNib
{
	breakpoints = [[NSMutableArray alloc] init];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil]; 
}


- (void) loadFromPrefs:(NSString *)key
{
	prefsKey = [key retain];
	NSData *data = [[NSUserDefaults standardUserDefaults] dataForKey:key];
	if (data) {
		[breakpoints release];
		breakpoints = [[NSMutableArray alloc]
			initWithArray:[NSKeyedUnarchiver unarchiveObjectWithData:data]];
	}
	[[NSNotificationCenter defaultCenter]
		postNotificationName:BREAKPOINTS_CHANGED_NOTIFICATION object:self];
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	// NSLog (@"BreakpointArray notifyApplicationWillTerminate");
	[[NSUserDefaults standardUserDefaults]
		setObject:[NSKeyedArchiver archivedDataWithRootObject:breakpoints] forKey:prefsKey];
}


- (BOOL) hasBreakpointWithValueNotEqualTo:(unsigned)value
{
	Breakpoint *breakpoint;
	
	NSEnumerator *enumerator = [breakpoints objectEnumerator];
	while ((breakpoint = [enumerator nextObject])) {
		if ([breakpoint value] != value)
			return TRUE;
	}
	return FALSE;
}


- (unsigned) valueForIdentifier:(unsigned)ident
{
	int n = [breakpoints indexOfObject:[Breakpoint breakpointWithIdentifier:ident value:0]
		inArraySortedBy:@selector(compareAddress:)];
	return (n == NSNotFound) ? 0 : [(Breakpoint *)[breakpoints objectAtIndex:n] value];
}


- (unsigned) setBreakpointWithIdentifier:(unsigned)ident value:(unsigned)val
{
	unsigned index = [breakpoints indexOfObject:[Breakpoint breakpointWithIdentifier:ident value:0]
		inArraySortedBy:@selector(compareAddress:)];
	if (index == NSNotFound)
		index = [breakpoints addObject:
			[Breakpoint breakpointWithIdentifier:ident value:val]
			toArraySortedBy:@selector(compareAddress:) replaceExistingObject:YES];
	else
		[[breakpoints objectAtIndex:index] setValue:val];
	[[NSNotificationCenter defaultCenter]
		postNotificationName:BREAKPOINTS_CHANGED_NOTIFICATION object:self];
	return index;
}


- (NSIndexSet *) setBreakpointsWithIdentifierArray:(NSArray *)idents value:(unsigned)val
// mass insert to avoid performance problems with the BREAKPOINTS_CHANGED_NOTIFICATION
// idents must be sorted ascending to get a correct index set
{
	NSNumber *number;
	
	NSMutableIndexSet *index = [[[NSMutableIndexSet alloc] init] autorelease];
	NSEnumerator *enumerator = [idents objectEnumerator];
	while ((number = [enumerator nextObject]))
		[index addIndex:[breakpoints addObject:
			[Breakpoint breakpointWithIdentifier:[number intValue] value:val]
			toArraySortedBy:@selector(compareAddress:) replaceExistingObject:YES]];
	[[NSNotificationCenter defaultCenter]
		postNotificationName:BREAKPOINTS_CHANGED_NOTIFICATION object:self];
	return index;
}


- (unsigned) identifierAtIndex:(unsigned)index
{
	NSAssert (index < [breakpoints count], @"Index out of range");
	return [(Breakpoint *)[breakpoints objectAtIndex:index] identifier];
}


- (unsigned) valueAtIndex:(unsigned)index
{
	NSAssert (index < [breakpoints count], @"Index out of range");
	return [(Breakpoint *)[breakpoints objectAtIndex:index] value];
}


- (void) setBreakpointAtIndex:(unsigned)index value:(unsigned)val
{
	NSAssert (index < [breakpoints count], @"Index out of range");
	[[breakpoints objectAtIndex:index] setValue:val];
}


- (void) deleteBreakpointAtIndex:(unsigned)index
{
	NSAssert (index < [breakpoints count], @"Index out of range");
	[breakpoints removeObjectAtIndex:index];
	[[NSNotificationCenter defaultCenter]
		postNotificationName:BREAKPOINTS_CHANGED_NOTIFICATION object:self];
}


- (void) deleteBreakpointsAtIndexes:(NSIndexSet *)indexes
{
	[breakpoints removeObjectsAtIndexes:indexes];
	[[NSNotificationCenter defaultCenter]
		postNotificationName:BREAKPOINTS_CHANGED_NOTIFICATION object:self];
}


- (void) setAllValues:(unsigned)val
{
	Breakpoint *breakpoint;
	
	NSEnumerator *enumerator = [breakpoints objectEnumerator];
	while ((breakpoint = [enumerator nextObject]))
		[breakpoint setValue:val];
	[[NSNotificationCenter defaultCenter]
		postNotificationName:BREAKPOINTS_CHANGED_NOTIFICATION object:self];
}


- (int) numberOfBreakpoints
{
	return [breakpoints count];
}


- (NSEnumerator *) enumerator
{
	return [breakpoints objectEnumerator];
}


@end
