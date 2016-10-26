/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	TableCornerView.m - Status indicator and button corner view for a NSTableControl
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


#import "TableCornerView.h"


@interface TableCornerCell : NSTableHeaderCell
{
}
@end


@implementation TableCornerCell


- (void) setState:(int)state
{
	// ignore state setting caused by mouse klick, otherwise the cell switchs from white to gray
	// send the action here because NSTableHeaderControl does not send any action
	[NSApp sendAction:[self action] to:[self target] from:self];
}


- (void) highlight:(BOOL)flag withFrame:(NSRect)frame inView:(NSView *)view
{
	// [super hightlight:NO ...] does not cause an unhighlight of the cell - Cocoa bug?
	if (flag)
		[super highlight:YES withFrame:frame inView:view];
	else
		[super drawWithFrame:frame inView:view];
}


@end


@implementation TableCornerView


- (TableCornerView *) initWithFrame:(NSRect)frame
{
	if ((self = [super initWithFrame:frame])) {
		[self setCell:[[TableCornerCell alloc] initImageCell:nil]];
		[self setClickable:NO];
	}
	return self;
}


- (void) setImageNamed:(NSString *)name toolTip:(NSString *)toolTip
{
	[[self cell] setImage:[NSImage imageNamed:name]];
	[self setToolTip:toolTip];
}


- (void) mouseDown:(NSEvent *)event
{
	if (clickable)
		[super mouseDown:event];
}


- (BOOL) isFlipped
// don't know why this is required - see http://zzot.net/2004/11/20/nstableheadercell/
{
	return YES;
}


- (BOOL) isClickable
{
	return clickable;
}


- (void) setClickable:(BOOL)flag
{
	clickable = flag;
}


@end
