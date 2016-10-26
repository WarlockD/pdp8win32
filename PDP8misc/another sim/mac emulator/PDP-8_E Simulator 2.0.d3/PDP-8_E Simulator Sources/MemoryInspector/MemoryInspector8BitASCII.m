/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspector8BitASCII.m - 8-Bit ASCII Memory Inspector
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
#import "Unicode.h"


#define ORDER_IN_MENU	20
#define WORDS_PER_ROW	4
#define CONTENT_WIDTH	4
#define NEEDS_ALIGNMENT	NO
#define MENU_TITLE	@"8-Bit ASCII"


@interface MemoryInspector8BitASCII : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspector8BitASCII


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
	NSString *string = NSLocalizedString(@"This column displays the memory content as 8-bit ASCII. "
		"When editing a cell, use %C (option-shift-9) to keep the "
		"current value and %C (option-8) to force zero.", @"");
	return [NSString stringWithFormat:string, UNICODE_BULLET_OPERATOR, UNICODE_BULLET];
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	NSMutableString *string = [NSMutableString stringWithCapacity:4];
	int i;
	for (i = 0; i < WORDS_PER_ROW; i++) {
		unsigned short c = [[value objectAtIndex:i] intValue];
		c &= ~0200;	// ignore parity bit
		if (c < 040 || 0177 <= c)
			c = UNICODE_MIDDLE_DOT;
		[string appendFormat:@"%C", c];
	}
	return string;
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	int i, c, m;

	if ([string length] != CONTENT_WIDTH) {
		if (error)
			*error = NSLocalizedString(@"Exactly four characters required.", @"");
		return FALSE;
	}
	NSMutableArray *val = [NSMutableArray arrayWithCapacity:WORDS_PER_ROW];
	for (i = 0; i < CONTENT_WIDTH; i++) {
		c = [string characterAtIndex:i];
		m = 077770000;
		if (c == UNICODE_MIDDLE_DOT)		// don't care, save old value
			c = m = 0;
		else if (c == UNICODE_BULLET)		// force zero
			c = 0;
		else if (c & ~0177) {
			if (error)
				*error = [NSString stringWithFormat:NSLocalizedString(
					@"Invalid 8-bit ASCII character %C%C%C.", @""),
					UNICODE_LEFT_DOUBLEQUOTE, c, UNICODE_RIGHT_DOUBLEQUOTE];
			return FALSE;
		}
		[val addObject:[NSNumber numberWithInt:m | c]];
	}
	*value = val;
	return TRUE;
}


@end
