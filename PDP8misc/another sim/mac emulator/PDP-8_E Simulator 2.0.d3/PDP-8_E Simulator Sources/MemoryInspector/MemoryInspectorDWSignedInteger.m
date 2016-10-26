/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorDWSignedInteger.m - Double Word Signed Integer Memory Inspector
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


#define ORDER_IN_MENU	60
#define WORDS_PER_ROW	2
#define CONTENT_WIDTH	8
#define NEEDS_ALIGNMENT	YES
#define MENU_TITLE	@"Double Word Signed Integer"


@interface MemoryInspectorDWSignedInteger : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspectorDWSignedInteger


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
		@"This column displays the memory content as double word signed integer.", @"");
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	int n = [[value objectAtIndex:0] intValue] | ([[value objectAtIndex:1] intValue] << 12);
	if (n & 040000000)
		n |= ~077777777;
	return [NSString stringWithFormat:@"%8d", n];
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	int n;
	
	NSMutableArray *val = [NSMutableArray arrayWithCapacity:WORDS_PER_ROW];
	NSScanner *scanner = [NSScanner scannerWithString:string];
	if ([scanner scanInt:&n]) {
		if ((n & ~037777777) != 0 && (n & ~037777777) != ~037777777) {
			if (error)
				*error = NSLocalizedString(@"Double word signed integer out of valid "
					"range from -8388608 to 8388607.", @"");
			return FALSE;
		}
		[val addObject:[NSNumber numberWithInt:077770000 | (n & 07777)]];
		[val addObject:[NSNumber numberWithInt:077770000 | ((n >> 12) & 07777)]];
	} else {
		if (error)
			*error = NSLocalizedString(@"Invalid double word signed integer.", @"");
		return FALSE;
	}
	if (! [scanner isAtEnd]) {
		if (error)
			*error = NSLocalizedString(
				@"Exactly one double word signed integer expected.", @"");
		return FALSE;
	}
	*value = val;
	return TRUE;
}


@end
