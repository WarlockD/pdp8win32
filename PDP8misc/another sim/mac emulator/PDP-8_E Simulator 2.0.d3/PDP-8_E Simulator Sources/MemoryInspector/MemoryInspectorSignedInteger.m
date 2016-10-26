/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorSignedInteger.m - Signed Integer Memory Inspector
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


#define ORDER_IN_MENU	40
#define WORDS_PER_ROW	2
#define CONTENT_WIDTH	11
#define NEEDS_ALIGNMENT	NO
#define MENU_TITLE	@"Signed Integer"


@interface MemoryInspectorSignedInteger : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspectorSignedInteger


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
	return NSLocalizedString(@"This column displays the memory content as signed integer.", @"");
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	int w0 = [[value objectAtIndex:0] intValue];
	int w1 = [[value objectAtIndex:1] intValue];
	if (w0 & 04000)
		w0 |= ~07777;
	if (w1 & 04000)
		w1 |= ~07777;
	return [NSString stringWithFormat:@"%5d %5d", w0, w1];
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	int i, n;
	
	NSMutableArray *val = [NSMutableArray arrayWithCapacity:WORDS_PER_ROW];
	NSScanner *scanner = [NSScanner scannerWithString:string];
	for (i = 0; i < WORDS_PER_ROW; i++) {
		if ([scanner scanInt:&n]) {
			if ((n & ~03777) != 0 && (n & ~03777) != ~03777) {
				if (error)
					*error = NSLocalizedString(
						@"Signed integer out of valid range from -2048 to 2047.",
						@"");
				return FALSE;
			}
			[val addObject:[NSNumber numberWithInt:077770000 | (n & 07777)]];
		} else {
			if (error)
				*error = [scanner isAtEnd] ?
					NSLocalizedString(@"Exactly two signed integers expected.", @"") :
					NSLocalizedString(@"Invalid signed integer.", @"");
			return FALSE;
		}
	}
	if (! [scanner isAtEnd]) {
		if (error)
			*error = NSLocalizedString(@"Exactly two signed integers expected.", @"");
		return FALSE;
	}
	*value = val;
	return TRUE;
}


@end
