/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	TypeaheadBuffer.m - Typeahead buffer for a keyboard input device
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


#import "PluginFramework/Utilities.h"

#import "TypeaheadBuffer.h"
#import "InputConsumerProtocol.h"


@implementation TypeaheadBuffer


#define MIN_LENGTH_TO_SHOW_FLUSH_TYPEAHEAD_BUTTON	5


- (IBAction) flush:(id)sender
{
	[typeaheadBuffer deleteCharactersInRange:NSMakeRange(0, [typeaheadBuffer length])];
	[flushTypeaheadBufferButton setHidden:YES];
}


- (BOOL) hasCharacters
{
	return [typeaheadBuffer length] > 0;
}


- (signed short) getNextChar
{
	signed short c = 0;
	int length = [typeaheadBuffer length];
	if (length < MIN_LENGTH_TO_SHOW_FLUSH_TYPEAHEAD_BUTTON)
		[flushTypeaheadBufferButton setHidden:YES];
	if (length > 0) {
		c = [typeaheadBuffer characterAtIndex:0];
		[typeaheadBuffer deleteCharactersInRange:NSMakeRange(0, 1)];
	}
	return c;
}


- (void) typeahead:(NSString *)string
{
	int length = [typeaheadBuffer length];
	[typeaheadBuffer appendString:string];
	if ([typeaheadBuffer length] >= MIN_LENGTH_TO_SHOW_FLUSH_TYPEAHEAD_BUTTON)
		[flushTypeaheadBufferButton setHidden:NO];
	if (length == 0)
		[inputConsumer canContinueInput];
}


- (void) awakeFromNib
{
	if (runningOnTiger()) {
		NSPoint p = [flushTypeaheadBufferButton frame].origin;
		p.x += 2; p.y += 1;
		[flushTypeaheadBufferButton setFrameOrigin:p];
		[flushTypeaheadBufferButton setBezelStyle:NSRoundRectBezelStyle];
		[flushTypeaheadBufferButton setShowsBorderOnlyWhileMouseInside:NO];
		[[flushTypeaheadBufferButton cell] setImagePosition:NSNoImage];
	}
	typeaheadBuffer = [[NSMutableString stringWithCapacity:128] retain];
}


@end
