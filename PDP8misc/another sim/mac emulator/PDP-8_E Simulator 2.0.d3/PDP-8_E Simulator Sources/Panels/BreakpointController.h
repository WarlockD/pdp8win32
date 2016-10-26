/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	BreakpointController.h - Class for maintaining breakpoints
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


@class BreakpointArray;


@interface BreakpointController : NSObject {
@private
	IBOutlet NSTableView		*tableView;
	IBOutlet NSButton		*addButton;
	IBOutlet NSButton		*deleteButton;
	IBOutlet NSButton		*enableAllButton;
	IBOutlet NSButton		*disableAllButton;
	IBOutlet BreakpointArray	*breakpoints;
}

- (IBAction) addBreakpoint:(id)sender;
- (IBAction) deleteBreakpoint:(id)sender;
- (IBAction) disableAllBreakpoints:(id)sender;
- (IBAction) enableAllBreakpoints:(id)sender;

@end
