/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RegisterFormCell.m - NSFormCell subclass for PDP-8/E registers
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


#import "RegisterFormCell.h"
#import "OctalFormatter.h"


@implementation RegisterFormCell


- (BOOL) control:(NSControl *)control textView:(NSTextView *)textView doCommandBySelector:(SEL)command
{
	if (command == @selector(cancelOperation:)) {
		// ESC aborts editing of the cell
		[control abortEditing];
		return YES;
	}
	return NO;
}


- (BOOL) control:(NSControl *)control didFailToFormatString:(NSString *)string
	errorDescription:(NSString *)error
{
	NSRange range;
	
	NSAlert *alert = [[NSAlert alloc] init];
		
	range.location = 0;
	range.length = -1;
	[[control currentEditor] setSelectedRange:range];
	[alert setMessageText:error];
	[alert beginSheetModalForWindow:[control window]
		modalDelegate:nil didEndSelector:nil contextInfo:nil];
	[alert release];
	return NO;
}


- (void) controlTextDidEndEditing:(NSNotification *)notification
{
	[registerOwner performSelector:setRegisterValue withObject:(id)[self intValue]];
}


- (void) notifyRegisterHasChanged:(NSNotification *)notification
{
	[self setIntValue:(unsigned)[registerOwner performSelector:getRegisterValue]];
}


- (void) setupRegisterFor:(id)owner getRegisterValue:(SEL)getter setRegisterValue:(SEL)setter
	changedNotificationName:(NSString *)notification
	mask:(unsigned)mask base:(short)base
{
	NSAssert (base == 8, @"Currenty only base 8 is implemented for RegisterFormCell");
	[(NSForm *)[self controlView] setDelegate:self];
	[self setFormatter:[[[OctalFormatter alloc] initWithBitMask:mask wildcardAllowed:NO] autorelease]];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyRegisterHasChanged:)
		name:notification object:nil];
	registerOwner = owner;
	getRegisterValue = getter;
	setRegisterValue = setter;
}


@end
