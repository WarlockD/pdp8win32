/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	ConsoleSwitchCell.h - NSButtonCell subclass for the console switches
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


#import "ConsoleSwitchCell.h"


@implementation ConsoleSwitchCell


- (void) awakeFromNib
{
	if ([self tag] == -1) {
		upWithoutShadow = [[NSImage alloc] initByReferencingFile:
			[[NSBundle bundleForClass:[self class]]
				pathForResource:[self alternateTitle] ofType:@"png"]];
		upWithShadow = [[self image] retain];
	} else {
		upWithoutShadow = [[self alternateImage] retain];
		upWithShadow = [[NSImage alloc] initByReferencingFile:
			[[NSBundle bundleForClass:[self class]]
				pathForResource:[self alternateTitle] ofType:@"png"]];
	}
	[(NSButton *)[self controlView] setAlternateTitle:nil];
}


- (void) highlight:(BOOL)flag withFrame:(NSRect)frame inView:(NSView *)view
{
	// we need highlight only for momentary changing switches, detected via tag value -1
	if ([self tag] == -1) {
		[super highlight:flag withFrame:frame inView:view];
		[leftNeighbour updateShadow:! flag];		
	}
	else
		[view setNeedsDisplay:YES];
}


- (void) performClick:(id)sender
{
	// called by the key equivalents; the default method causes the switch to highlight
	// we need highlight only for momentary changing switches, detected via tag value -1
	if ([self tag] == -1)
		[super performClick:sender];
	else {
		[self setState:! [self state]];
		[[self target] performSelector:[self action] withObject:sender];
	}
	[[self controlView] setNeedsDisplay:YES];	// to show keyboad operation via space key with Tiger
}


- (void) setState:(int)state
{
	[super setState:state];
	[leftNeighbour updateShadow:state];
}


- (void) updateShadow:(BOOL)showShadow
{
	if ([self tag] == -1)
		[self setImage:showShadow ? upWithShadow : upWithoutShadow];
	else
		[self setAlternateImage:showShadow ? upWithShadow : upWithoutShadow];
}


@end
