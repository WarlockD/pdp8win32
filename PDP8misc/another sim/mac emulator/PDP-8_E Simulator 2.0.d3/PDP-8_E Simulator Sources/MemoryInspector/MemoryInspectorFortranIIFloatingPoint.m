/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorFortranIIFloatingPoint.m - Fortran II Floating Point Memory Inspector
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


#define ORDER_IN_MENU	100
#define WORDS_PER_ROW	3
#define CONTENT_WIDTH	16
#define NEEDS_ALIGNMENT	YES
#define MENU_TITLE	@"FORTRAN II Floating Point (3 words)"


@interface MemoryInspectorFortranIIFloatingPoint : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspectorFortranIIFloatingPoint


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
		@"This column displays the memory content as FORTRAN II floating point numbers.", @"");
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	return [[FloatingPointNumber
		floatingPointNumberWithExponent:([[value objectAtIndex:0] intValue] >> 3) & 0377
		bias:0200
		negative:([[value objectAtIndex:0] intValue] & 04000) ? YES : NO
		twoComplementMantissa:NO
		mantissa:([[value objectAtIndex:0] longLongValue] << 61) |
				([[value objectAtIndex:1] longLongValue] << 49) |
				([[value objectAtIndex:2] longLongValue] << 37)]
			stringValueWithPrecision:8];
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	FloatingPointNumber *f = [FloatingPointNumber floatingPointNumberFromString:string
		withBias:0200 withTwoComplementMantissa:NO error:error];
	if (! f)
		return FALSE;
	unsigned long long mantissa = [f mantissa];
	*value = [NSArray arrayWithObjects:
		[NSNumber numberWithInt:077770000 |
			([f negative] ? 04000 : 0) | ([f exponent] << 3) | (unsigned long) (mantissa >> 61)],
		[NSNumber numberWithInt:077770000 | (unsigned long) ((mantissa >> 49) & 07777)],
		[NSNumber numberWithInt:077770000 | (unsigned long) ((mantissa >> 37) & 07777)],
		nil];
	return TRUE;
}


@end
