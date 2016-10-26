/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	KeepInMenuWindow.m - Windows that are kept in the window menu when hidden
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


#import "KeepInMenuWindow.h"
#import "Unicode.h"


@implementation KeepInMenuWindow


#define WINDOW_VISIBILITY_DEFAULTS_KEY_SUFFIX	@" Visible"	// with leading space


- (void) saveVisibilityInDefaults
{
	NSString *frameAutosaveName = [self frameAutosaveName];
	if (frameAutosaveName)
		[[NSUserDefaults standardUserDefaults] setBool:[self isVisible] forKey:
			[frameAutosaveName stringByAppendingString:WINDOW_VISIBILITY_DEFAULTS_KEY_SUFFIX]];
}


- (BOOL) getVisibilityFromDefaults
{
	NSNumber *visible = nil;
	NSString *frameAutosaveName = [self frameAutosaveName];
	if (frameAutosaveName)
		visible = [[NSUserDefaults standardUserDefaults] objectForKey:
			[frameAutosaveName stringByAppendingString:WINDOW_VISIBILITY_DEFAULTS_KEY_SUFFIX]];
	else
		NSLog (@"Missing autosave name for window %C%@%C",
			UNICODE_LEFT_DOUBLEQUOTE, [self title], UNICODE_RIGHT_DOUBLEQUOTE);
	// check existance of the key to make all windows visible at the very first launch of the simulator
	return frameAutosaveName == nil || visible == nil || [visible boolValue];
}


- (BOOL) windowShouldClose:(NSWindow *)window
// When the delegate allows the window to close, close it and then re-add it to the window menu
{
	id delegate = [window delegate];
	if (delegate == nil || ! [delegate respondsToSelector:@selector(windowShouldClose:)] ||
		[delegate windowShouldClose:self]) {
		[window close];
		[self saveVisibilityInDefaults];
		[NSApp addWindowsItem:window title:[window title] filename:NO];
	}
	return NO;
}


- (void) makeKeyAndOrderFront:(id)sender
{
	[super makeKeyAndOrderFront:sender];
	[self saveVisibilityInDefaults];
}


- (void) orderFrontFromDefaults:(id)sender
{
	if ([self getVisibilityFromDefaults])
		[self orderFront:sender];
	else
		[NSApp addWindowsItem:self title:[self title] filename:NO];
	[self saveVisibilityInDefaults];
}


- (void) orderBackFromDefaults:(id)sender
{
	if ([self getVisibilityFromDefaults])
		[self orderBack:sender];
	else
		[NSApp addWindowsItem:self title:[self title] filename:NO];
	[self saveVisibilityInDefaults];
}


@end
