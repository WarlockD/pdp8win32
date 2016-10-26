/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspector6BitASCII.m - 6-Bit ASCII Memory Inspector
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


#define ORDER_IN_MENU	10
#define WORDS_PER_ROW	4
#define CONTENT_WIDTH	8
#define NEEDS_ALIGNMENT	NO
#define MENU_TITLE	@"6-Bit ASCII"


@interface MemoryInspector6BitASCII : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspector6BitASCII


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
	NSString *string = NSLocalizedString(@"This column displays the memory content as 6-bit ASCII. "
		"When editing a cell, use %C (option-shift-9) to keep the "
		"current value and %C (option-8) to force zero.", @"");
	return [NSString stringWithFormat:string, UNICODE_BULLET_OPERATOR, UNICODE_BULLET];
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	NSMutableString *string = [NSMutableString stringWithCapacity:8];
	int i;
	for (i = 0; i < WORDS_PER_ROW; i++) {
		unsigned short c1 = [[value objectAtIndex:i] intValue];
		unsigned short c0 = c1 >> 6;
		if (c0 == 0)
			c0 = UNICODE_MIDDLE_DOT;
		else if (c0 < 040)
			c0 |= 0100;
		c1 &= 077;
		if (c1 == 0)
			c1 = UNICODE_MIDDLE_DOT;
		else if (c1 < 040)
			c1 |= 0100;
		[string appendFormat:@"%C%C", c0, c1];
	}
	return string;
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	int i, c0, c1, m0, m1;
	
	if ([string length] != CONTENT_WIDTH) {
		if (error)
			*error = NSLocalizedString(@"Exactly eight characters required.", @"");
		return FALSE;
	}
	m1 = c1 = 0;	// only to avoid "used uninitialized" compiler warning
	NSMutableArray *val = [NSMutableArray arrayWithCapacity:WORDS_PER_ROW];
	for (i = 0; i < CONTENT_WIDTH; i++) {
		c0 = [string characterAtIndex:i];
		m0 = 0770000;
		if (c0 == UNICODE_MIDDLE_DOT)		// don't care, save old value
			c0 = m0 = 0;
		else if (c0 == UNICODE_BULLET)		// force zero
			c0 = 0;
		else if (040 <= c0 && c0 < 0140)
			c0 &= 077;
		else {
			if (error)
				*error = [NSString stringWithFormat:NSLocalizedString(
					@"Invalid 6-bit ASCII character %C%C%C.", @""),
					UNICODE_LEFT_DOUBLEQUOTE, c0, UNICODE_RIGHT_DOUBLEQUOTE];
			return FALSE;
		}
		if (i & 1)
			[val addObject:[NSNumber numberWithInt:m1 | m0 | c1 | c0]];
		else {
			c1 = c0 << 6;
			m1 = m0 << 6;
		}
	}
	*value = val;
	return TRUE;
}


@end
