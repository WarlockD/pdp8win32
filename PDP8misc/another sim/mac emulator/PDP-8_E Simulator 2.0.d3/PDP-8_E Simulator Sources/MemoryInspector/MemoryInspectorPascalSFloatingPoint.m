/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorPascalSFloatingPoint.m - Pascal-S Floating Point Memory Inspector
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


#import "MemoryInspectorProtocol.h"
#import "FloatingPointNumber.h"


#define ORDER_IN_MENU	110
#define WORDS_PER_ROW	4
#define CONTENT_WIDTH	17
#define NEEDS_ALIGNMENT	YES
#define MENU_TITLE	@"Pascal-S Floating Point (4 words)"


@interface MemoryInspectorPascalSFloatingPoint : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspectorPascalSFloatingPoint


+ (void) load
{
	/* dummy to load the class when zero-linking while development */
}


- (NSNumber *) orderInMemoryInspectorMenu
{
	return [NSNumber numberWithInt:ORDER_IN_MENU];
}


- (NSString *) menuTitle
{
	return NSLocalizedString(MENU_TITLE, @"");
}


- (unsigned) wordsPerRow
{
	return WORDS_PER_ROW;
}


- (unsigned) contentWidthInCharacters
{
	return CONTENT_WIDTH;
}


- (BOOL) needsMemoryAlignment
{
	return NEEDS_ALIGNMENT;
}


- (NSString *) toolTipForContentColumn
{
	return NSLocalizedString(
		@"This column displays the memory content as Pascal-S floating point numbers.", @"");
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	return [[FloatingPointNumber
		floatingPointNumberWithExponent:[[value objectAtIndex:0] intValue]
		bias:0
		negative:([[value objectAtIndex:1] intValue] & 04000) ? YES : NO
		twoComplementMantissa:NO
		mantissa:([[value objectAtIndex:1] longLongValue] << 53) |
				([[value objectAtIndex:2] longLongValue] << 41) |
				([[value objectAtIndex:3] longLongValue] << 29)]
			stringValueWithPrecision:10];
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	FloatingPointNumber *f = [FloatingPointNumber floatingPointNumberFromString:string
		withBias:0 withTwoComplementMantissa:NO error:error];
	if (! f)
		return FALSE;
	*value = [NSArray arrayWithObjects:
		[NSNumber numberWithInt:077770000 | [f exponent]],
		[NSNumber numberWithInt:
			077770000 | ([f negative] ? 04000 : 0) | (unsigned long) ([f mantissa] >> 53)],
		[NSNumber numberWithInt:077770000 | (unsigned long) (([f mantissa] >> 41) & 07777)],
		[NSNumber numberWithInt:077770000 | (unsigned long) (([f mantissa] >> 29) & 07777)],
		nil];
	return TRUE;
}


@end
