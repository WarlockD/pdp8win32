/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	NSMutableArray+BinarySearch.m - Category for binary search
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


@implementation NSMutableArray (BinarySearch)


- (unsigned) addObject:(id)object toArraySortedBy:(SEL)compare replaceExistingObject:(BOOL)replace
{
	int n = [self count];	
	if (n == 0) {
		[self addObject:object];
		return 0;
	}
	NSRange range = NSMakeRange(0, n);
	while (range.length > 0) {
		unsigned m = range.location + range.length / 2;
		switch ((NSComparisonResult)
			[[self objectAtIndex:m] performSelector:compare withObject:object]) {
		case NSOrderedAscending:
			n = range.location + range.length;
			range.location = m + 1;
			range.length = n - range.location;
			break;
		case NSOrderedDescending:
			range.length = m - range.location;
			break;
		case NSOrderedSame:
			if (replace)
				[self replaceObjectAtIndex:m withObject:object];
			return m;
		default:
			NSAssert (FALSE, @"Invalid comparison result");
			break;
		}
	}
	[self insertObject:object atIndex:range.location];
	return range.location;
}


- (unsigned) indexOfObject:(id)object inArraySortedBy:(SEL)compare;
{
	int n = [self count];
	if (n == 0)
		return NSNotFound;
	NSRange range = NSMakeRange(0, n);
	while (range.length > 0) {
		unsigned m = range.location + range.length / 2;
		switch ((NSComparisonResult)
			[[self objectAtIndex:m] performSelector:compare withObject:object]) {
		case NSOrderedAscending:
			n = range.location + range.length;
			range.location = m + 1;
			range.length = n - range.location;
			break;
		case NSOrderedDescending:
			range.length = m - range.location;
			break;
		case NSOrderedSame:
			return m;
		default:
			NSAssert (FALSE, @"Invalid comparison result");
			break;
		}
	}
	return NSNotFound;
}


@end
