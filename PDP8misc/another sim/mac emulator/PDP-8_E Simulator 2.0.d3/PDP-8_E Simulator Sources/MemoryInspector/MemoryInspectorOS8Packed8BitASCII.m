/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorOS8Packed8BitASCII.m - OS/8 Packed 8Bit ASCII Memory Inspector
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


#define ORDER_IN_MENU	30
#define WORDS_PER_ROW	4
#define CONTENT_WIDTH	6
#define NEEDS_ALIGNMENT	YES
#define MENU_TITLE	@"OS/8 Packed 8-Bit ASCII"


@interface MemoryInspectorOS8Packed8BitASCII : NSFormatter <MemoryInspector>
{
}
@end


@implementation MemoryInspectorOS8Packed8BitASCII


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
	NSString *string = NSLocalizedString(@"This column displays the memory content as "
		"OS/8 Packed 8-bit ASCII. When editing a cell, use %C (option-shift-9) to keep the "
		"current value and %C (option-8) to force zero.", @"");
	return [NSString stringWithFormat:string, UNICODE_BULLET_OPERATOR, UNICODE_BULLET];
}


- (NSString *) stringForObjectValue:(NSArray *)value
{
	unsigned short i, c, w0, w1;
	
	if ([[value class] isSubclassOfClass:[NSString class]])
		return (NSString *) value;
	NSAssert ([value count] == WORDS_PER_ROW, @"Invalid number of words to format");
	NSMutableString *string = [NSMutableString stringWithCapacity:CONTENT_WIDTH];
	for (i = 0; i < WORDS_PER_ROW; i += 2) {
		w0 = [[value objectAtIndex:i] intValue];
		c = w0 & 0177;
		if (c < 040 || c >= 0177)
			c = UNICODE_MIDDLE_DOT;
		[string appendFormat:@"%C", c];
		w1 = [[value objectAtIndex:i + 1] intValue];
		c = w1 & 0177;
		if (c < 040 || c >= 0177)
			c = UNICODE_MIDDLE_DOT;
		[string appendFormat:@"%C", c];
		c = ((w0 & 03400) >> 4) | ((w1 & 07400) >> 8);
		if (c < 040 || c >= 0177)
			c = UNICODE_MIDDLE_DOT;
		[string appendFormat:@"%C", c];
	}
	return string;
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
{
	int i, c, c0, c1, m, m0, m1;

	if ([string length] != CONTENT_WIDTH) {
		if (error)
			*error = NSLocalizedString(@"Exactly six characters required.", @"");
		return FALSE;
	}
	c0 = c1 = m0 = m1 = 0;	// only to avoid "used uninitialized" compiler warning
	NSMutableArray *val = [NSMutableArray arrayWithCapacity:4];
	for (i = 0; i < CONTENT_WIDTH; i++) {
		c = [string characterAtIndex:i];
		m = 0377;
		if (c == UNICODE_MIDDLE_DOT) {		// don't care, save old value
			c = 0;
			m &= ~0377;
		} else if (c == UNICODE_BULLET)		// force zero
			c = 0;
		else if (c & ~0177) {
			if (error)
				*error = [NSString stringWithFormat:NSLocalizedString(
					@"Invalid 8-bit ASCII character %C%C%C.", @""),
					UNICODE_LEFT_DOUBLEQUOTE, c, UNICODE_RIGHT_DOUBLEQUOTE];
			return FALSE;
		}
		switch (i % 3) {
		case 0 :
			c0 = c;
			m0 = m;
			break;
		case 1 :
			c1 = c;
			m1 = m;
			break;
		case 2 :
			c0 |= (c & 0360) << 4;
			m0 |= (m & 0360) << 4;
			c1 |= (c & 017) << 8;
			m1 |= (m & 017) << 8;
			[val addObject:[NSNumber numberWithInt:(m0 << 12) | c0]];
			[val addObject:[NSNumber numberWithInt:(m1 << 12) | c1]];
			break;
		}
	}
	*value = val;
	return TRUE;
}


@end
