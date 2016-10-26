/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryContentView.m - Memory Content View for the PDP-8/E Simulator
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


#import "MemoryContentView.h"


@implementation MemoryContentView


- (void) setMemory:(unsigned short *)m
{
	mem = m;
	NSCopyMemoryPages (mem, oldmem, sizeof(oldmem));
}


- (void) update
{
	if (! [self lockFocusIfCanDraw])
		return;
	int i;
	for (i = 0; i < PDP8_MEMSIZE; i++) {
		unsigned w = mem[i];
		if (w != oldmem[i]) {
			[[NSColor colorWithCalibratedRed:((w >> 8) & 017) / 15.0
				green:((w >> 4) & 017) / 15.0
				blue:(w & 017) / 15.0 alpha:1.0] set];
			NSRect r = NSMakeRect(4 * ((i / 128) + 1 + (i / 4096)), 512 - 4 * (i % 128), 4, 4);
				NSRectFill (r);
			oldmem[i] = w;
		}
	}
	[self unlockFocus];
}


- (void) drawRect:(NSRect)rect
{
	int i;
	for (i = 0; i < 0100000; i++) {
		unsigned w = mem[i];
		[[NSColor colorWithCalibratedRed:((w >> 8) & 017) / 15.0 green:((w >> 4) & 017) / 15.0
			blue:(w & 017) / 15.0 alpha:1.0] set];
		NSRect r = NSMakeRect(4 * ((i / 128) + 1 + (i / 4096)), 512 - 4 * (i % 128), 4, 4);
		NSRectFill (r);
		oldmem[i] = w;
	}
}


@end
