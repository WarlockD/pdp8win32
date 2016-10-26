/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	ASR33TextView.m - NSTextView for the ASR 33 Teletype
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
 

#import "ASR33TextView.h"
#import "TypeaheadBuffer.h"


@implementation ASR33TextView


- (BOOL) validateUserInterfaceItem:(id <NSValidatedUserInterfaceItem >)item
{
	SEL action = [item action];
	return (action == @selector(copy:) ||
		action == @selector(paste:) ||
		action == @selector(selectAll:) ||
		action == @selector(_learnSpellingFromMenu:) ||
		action == @selector(spotlight:) ||
		action == @selector(_searchWithGoogleFromMenu:) ||
		action == @selector(_lookUpIndefiniteRangeInDictionaryFromMenu:) ||
		action == @selector(startSpeaking:) ||
		action == @selector(stopSpeaking:)) ?
		[super validateUserInterfaceItem:item] : FALSE;
}


- (void) doCommandBySelector:(SEL)selector
{
	// NSLog (@"doCommandByselector %@", selectorName);
	NSString *selectorName = NSStringFromSelector(selector);
	if (selector == @selector(insertNewline:))
		[typeaheadBuffer typeahead:@"\r"];
	else if (selector == @selector(deleteBackward:))
		[typeaheadBuffer typeahead:@"\b"];
	else if (selector == @selector(deleteForward:))				// rubout
		[typeaheadBuffer typeahead:@"\177"];
	else if (selector == @selector(centerSelectionInVisibleArea:))
		[typeaheadBuffer typeahead:@"\f"];				// Ctrl-L
	else if (selector == @selector(pageDown:))
		[typeaheadBuffer typeahead:@"\026"];				// Ctrl-V
	else if (selector == @selector(noop:)) {
		NSEvent *currentEvent = [NSApp currentEvent];
		unsigned modifierFlags = [currentEvent modifierFlags];
		unsigned c = [[currentEvent charactersIgnoringModifiers] characterAtIndex:0];
		if (modifierFlags & NSControlKeyMask) {
			switch (c) {
			case 'c' :
			case 'C' :
				[typeaheadBuffer typeahead:@"\003"];		// Ctrl-C
				break;
			case 'z' :
			case 'Z' :
				[typeaheadBuffer typeahead:@"\032"];		// Ctrl-Z
				break;
			default :
				NSLog (@"character %c (%o) ignored", c, c);
				break; 
			}
		} else if (modifierFlags & NSFunctionKeyMask) {
			if (c == 0xf739)		// fn-6 = forward delete on notebook keyboards
				[typeaheadBuffer typeahead:@"\177"];		// rubout
		}
	} else if (selector != @selector(complete:) &&
		! [selectorName hasPrefix:@"delete"])		// ignore all delete* selectors
		[super doCommandBySelector:selector];
}


- (BOOL) readSelectionFromPasteboard:(NSPasteboard *)pasteboard type:(NSString *)type
{
	[pasteboard types];	// required before accessing the pasteboards data
	if ([type isEqualToString:NSStringPboardType]) {
		[typeaheadBuffer typeahead:[pasteboard stringForType:type]];
		return YES;
	} 
	return NO;
}


- (void) insertText:(NSString *)string
{
	[typeaheadBuffer typeahead:string];
}


- (void) putChar:(unichar)c
{
	NSTextStorage *storage = [self textStorage];
	int length = [storage length];
	NSRange endOfText = NSMakeRange(length, 0);
	[self scrollRangeToVisible:endOfText];
	[self setSelectedRange:endOfText];
	switch (c) {
	case '\a' :
		// NSBeep ();		// handled by the ASR33 method playSound:
		break;
	case '\b' :
		if (length && [[storage string] characterAtIndex:length - 1] != '\n')
			[super deleteBackward:self];
		break;
	default :
		[super insertText:[NSString stringWithCharacters:&c length:1]];
		break;
	}
}


- (void) awakeFromNib
{
	// set size of container - to make horizontal scroller appear
	NSTextContainer *container = [self textContainer];
	NSSize size = [container containerSize];
	size.width = UINT_MAX;
	[container setContainerSize:size];
	[container setWidthTracksTextView:NO];
	
	// set default font
	NSFont *font = [NSFont userFixedPitchFontOfSize:11];
	[self setFont:font];

	// set tabstop every 8 character
	NSMutableParagraphStyle *style = [[[NSParagraphStyle defaultParagraphStyle] mutableCopy] autorelease];
	[style setTabStops:[NSArray array]];
	NSDictionary *attrs = [NSDictionary dictionaryWithObjectsAndKeys:font, NSFontAttributeName, nil];
	NSAttributedString *str =
		[[[NSAttributedString alloc] initWithString:@" " attributes:attrs] autorelease];
	[style setDefaultTabInterval:8 * [str size].width /* == 7 */];
	[self setTypingAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
		style, NSParagraphStyleAttributeName, font, NSFontAttributeName, nil]];
	if ([self respondsToSelector:@selector(setAutomaticTextReplacementEnabled:)])
		[self performSelector:@selector(setAutomaticTextReplacementEnabled:)
			withObject:(NSObject *) NO];
}


@end
