/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	KnobCell.h - NSButtonCell subclass for the console turning switches
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


// alternate title of the control is the prefix for the images of the knob
// the tag of the cell is the number of positions / images of the knob
// when this number is negative, the knob is operated horizontally, otherwise upright
// the control tag always contains the current knob position
// use [[control cell] setTag:x] to set the tag (i. e. position) of the knob and update the image


@interface KnobCell : NSButtonCell
{
@private
	NSMutableArray	*images;
}

@end
