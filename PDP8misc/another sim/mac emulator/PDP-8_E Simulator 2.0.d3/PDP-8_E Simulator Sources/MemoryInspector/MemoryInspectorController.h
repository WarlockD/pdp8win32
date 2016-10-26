/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorController.h - Controller for the Memory Inspectors Drawer
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


@class TableCornerView, PDP8;
@protocol MemoryInspector;


@interface MemoryInspectorScrollView : NSScrollView
{
}
@end


@interface MemoryInspectorController : NSObject {
@private
	IBOutlet NSDrawer		*memoryInspectorDrawer;
	IBOutlet NSPopUpButton		*popupButton;
	IBOutlet NSTableView		*memoryView;
	IBOutlet TableCornerView	*cornerView;
	IBOutlet PDP8			*pdp8;
	NSArray				*memoryInspectors;
	NSFormatter <MemoryInspector>	*currentInspector;
	unsigned			alignment;
}

- (IBAction) selectMemoryInspector:(id)sender;
- (IBAction) alignMemory:(id)sender;

@end
