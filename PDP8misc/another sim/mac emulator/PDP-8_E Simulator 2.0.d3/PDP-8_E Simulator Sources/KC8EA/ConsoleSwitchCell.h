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


#import <Cocoa/Cocoa.h>


// for toggles with default position "up"
// - image is up image with shaddow
// - alternate image is down position
// - alternate title is the name of the up position image with shadow
// for switches
// - image is the down image
// - alternate image is the up position image without shadow
// - alternate title is the name of the up position image with shadow
// tag value == -1 for a momentary changing switch, otherwise it is a dual state switch


@interface ConsoleSwitchCell : NSButtonCell
{
@private
	IBOutlet ConsoleSwitchCell	*leftNeighbour;
	NSImage				*upWithShadow;
	NSImage				*upWithoutShadow;
}

- (void) updateShadow:(BOOL)showShadow;

@end
