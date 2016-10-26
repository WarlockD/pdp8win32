/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	NonWrappingTableView.h - NSTableView without wrap-around editing
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
 

#import "NonWrappingTableView.h"
#import "Utilities.h"


@implementation NonWrappingTableView


- (void) textDidEndEditing:(NSNotification *)notification
// Avoid editing to wrap around from last available memory location to 0 and vice versa.
// Note the different behaviour of Tiger and Leopard:
// http://developer.apple.com/releasenotes/Cocoa/AppKit.html#NSTableView
{
	NSDictionary *userInfo = [notification userInfo];
	int textMovement = [[userInfo valueForKey:@"NSTextMovement"] intValue];
	int row = [self editedRow];
	int col = [self editedColumn];
	int newrow = (textMovement == NSBacktabTextMovement) ? row - 1 : row + 1;
	
	if (textMovement == NSTabTextMovement && runningOnLeopardOrNewer()) {
		// default behaviour on Leopard: tab to next view
		[super textDidEndEditing:notification];
		return;
	}
	if ((textMovement == NSReturnTextMovement ||
		textMovement == NSTabTextMovement || textMovement == NSBacktabTextMovement)
		&& [[self delegate] tableView:self shouldEditTableColumn:nil row:newrow])
		textMovement = (textMovement == NSBacktabTextMovement) ?
			NSUpTextMovement : NSDownTextMovement;
	else
		textMovement = NSIllegalTextMovement;
	NSMutableDictionary *newInfo = [NSMutableDictionary dictionaryWithDictionary:userInfo];
	[newInfo setObject:[NSNumber numberWithInt:textMovement] forKey:@"NSTextMovement"];
	[super textDidEndEditing:[NSNotification notificationWithName:[notification name]
		object: [notification object] userInfo:newInfo]];
	if (textMovement != NSIllegalTextMovement) {
		[self selectRowIndexes:[NSIndexSet indexSetWithIndex:newrow] byExtendingSelection:NO];
		[self editColumn:col row:newrow withEvent:nil select:YES];
	}
}


@end


