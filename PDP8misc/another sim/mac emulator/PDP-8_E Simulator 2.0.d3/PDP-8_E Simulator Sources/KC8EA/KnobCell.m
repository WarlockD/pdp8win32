/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	KnobCell.m - NSButtonCell subclass for the console turning switches
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


#import "KnobCell.h"


@implementation KnobCell


- (void) awakeFromNib
{
	int i;

	NSString *imageNamePrefix = [self alternateTitle];
	[(NSButton *)[self controlView] setAlternateTitle:nil];
	int numberOfImages = [self tag];
	if (numberOfImages < 0)
		numberOfImages = -numberOfImages;
	images = [[NSMutableArray alloc] initWithCapacity:numberOfImages];
	for (i = 0; i < numberOfImages; i++) {
		[images insertObject:[[[NSImage alloc] initByReferencingFile:
			[[NSBundle bundleForClass:[self class]]
				pathForResource:[imageNamePrefix stringByAppendingFormat:@"%d", i]
				ofType:@"png"]] autorelease]
			atIndex:i];
	}
}


- (void) setTag:(int)tag
// correctly, we would have to overwrite [NSButton setTag], but to avoid subclassing NSButton,
// we use the cell method to set the buttons tag and image
{
	NSButton *button = (NSButton *)[self controlView];
	[button setTag:tag];
	[button setImage:[images objectAtIndex:tag]];
	[button setAlternateImage:[images objectAtIndex:tag]];
}


- (void) performClick:(id)sender
{
	// called by the key equivalents
	int newsegment = ([sender tag] + 1) % [images count];
	[sender setImage:[images objectAtIndex:newsegment]];
	[sender setAlternateImage:[images objectAtIndex:newsegment]];
	[sender setTag:newsegment];
	[[self target] performSelector:[self action] withObject:sender];
	[[self controlView] setNeedsDisplay:YES];	// to show keyboad operation via space key with Tiger
}



- (BOOL) startTrackingAt:(NSPoint)startPoint inView:(NSView *)controlView
{
	return YES;
}


- (void) trackAt:(NSPoint)currentPoint inView:(NSButton *)control sendAction:(BOOL)sendAction
{
	int segments = [self tag];
	NSSize size = [control bounds].size;
	float length = size.height;
	float pos = currentPoint.y;
	if (segments < 0) {
		segments = -segments;
		length = size.width;
		pos = currentPoint.x;
	}
	int newsegment = pos / length * segments;
	if (newsegment != [control tag]) {
		[control setImage:[images objectAtIndex:newsegment]];
		[control setAlternateImage:[images objectAtIndex:newsegment]];
		[control setTag:newsegment];
		if (sendAction)
			[[self target] performSelector:[self action] withObject:control];
	}
}


- (BOOL) continueTracking:(NSPoint)lastPoint at:(NSPoint)currentPoint inView:(NSButton *)control
{
	[self trackAt:currentPoint inView:control sendAction:YES];
	return YES;
}


- (void) stopTracking:(NSPoint)lastPoint at:(NSPoint)stopPoint inView:(NSButton *)control mouseIsUp:(BOOL)up
{
	if (up)
		[self trackAt:stopPoint inView:control sendAction:NO];
}


@end
