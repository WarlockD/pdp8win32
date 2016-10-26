/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorUnsignedInteger.m - Unsigned Integer Memory Inspector
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


#define ORDER_IN_MENU	50
#define WORDS_PER_ROW	2
#define CONTENT_WIDTH	9
#define NEEDS_ALIGNMENT	NO
#define MENU_TITLE	@"Unsigned Integer"


@interface MemoryInspectorUnsignedInteger : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspectorUnsignedInteger


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
	return NSLocalizedString(@"This column displays the memory content as unsigned integer.", @"");
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	return [NSString stringWithFormat:@"%4d %4d",
		[[value objectAtIndex:0] intValue], [[value objectAtIndex:1] intValue]];
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	int i, n;
	
	NSMutableArray *val = [NSMutableArray arrayWithCapacity:WORDS_PER_ROW];
	NSScanner *scanner = [NSScanner scannerWithString:string];
	for (i = 0; i < WORDS_PER_ROW; i++) {
		if ([scanner scanInt:&n]) {
			if ((n & ~07777) != 0) {
				if (error)
					*error = NSLocalizedString(
						@"Unsigned integer out of valid range from 0 to 4095.", @"");
				return FALSE;
			}
			[val addObject:[NSNumber numberWithInt:077770000 | n]];
		} else {
			if (error)
				*error = [scanner isAtEnd] ?
					NSLocalizedString(@"Exactly two unsigned integers expected.", @"") :
					NSLocalizedString(@"Invalid unsigned integer.", @"");
			return FALSE;
		}
	}
	if (! [scanner isAtEnd]) {
		if (error)
			*error = NSLocalizedString(@"Exactly two unsigned integers expected.", @"");
		return FALSE;
	}
	*value = val;
	return TRUE;
}


@end
