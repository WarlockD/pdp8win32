/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	IOFlagController.h - Controller for the I/O flags table view in the CPU window
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


@class PDP8;


@interface IOFlagController : NSObject
{
@private
	IBOutlet NSTableView	*ioFlagsView;
	IBOutlet PDP8		*pdp8;
	NSMutableArray		*deviceNames;
	unsigned long		enabledFlags;
}

- (unsigned) numberOfAvailableFlags;
- (unsigned long) addIODevice:(NSString *)name;	// returns the I/O mask for the new device or 0 on error
- (void) disableIODevice:(unsigned long)ioflag;
- (void) enableIODevice:(unsigned long)ioflag;

@end
