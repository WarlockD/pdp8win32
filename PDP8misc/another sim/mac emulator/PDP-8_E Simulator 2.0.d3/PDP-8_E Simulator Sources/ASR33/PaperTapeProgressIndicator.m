/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PaperTapeProgressIndicator.m - Progress indicator for the paper tape reader and punch
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


#import "PaperTapeProgressIndicator.h"


@interface NSProgressIndicator (HeartBeat)

- (void) heartBeat:(id)sender;		// Apple internal method for the animation

@end


@implementation PaperTapeProgressIndicator


- (void) animateForSeconds:(NSTimeInterval)seconds
{
	[self startAnimation:self];
	if (stopAnimationTimer)
		[stopAnimationTimer
			setFireDate:[NSDate dateWithTimeIntervalSinceNow:seconds]];
	else
		stopAnimationTimer = [NSTimer scheduledTimerWithTimeInterval:seconds
			target:self selector:@selector(stopAnimation:) userInfo:nil repeats:NO];
}


- (void) startAnimation:(id)sender
{
	isAnimating = YES;
	[super startAnimation:sender];
}

				
- (void) stopAnimation:(id)sender
{
	isAnimating = NO;
	[stopAnimationTimer invalidate];
	stopAnimationTimer = nil;
	[super stopAnimation:sender];
}


- (void) heartBeat:(id)sender
{
	if (isAnimating)
		[super heartBeat:sender];
}


@end
