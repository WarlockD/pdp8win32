/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	BreakpointController.m - Class for maintaining breakpoints
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


#import "BreakpointController.h"
#import "BreakpointArray.h"
#import "OctalFormatter.h"


@implementation BreakpointController


/* ADDRESS_COLUMN is the identifier of the address column. The other checkbox
   columns must be identified with integers representing the bit mask for the
   enabled bit in the breakpoints enable bit mask, i. e. 1, 2, 4, 8,...
   Then ALL_BREAKPOINTS is the mask for "all columns enabled". */
   
#define ADDRESS_COLUMN			0
#define ALL_BREAKPOINTS			((1 << ([tableView numberOfColumns] - 1)) - 1)


/* #defines for the cases where the instance must know if it is the
   breakpoints or break opcodes controller */
   
#define IS_BREAKPOINT_CONTROLLER	([tableView numberOfColumns] == 2)
#define IS_BREAKOPCODE_CONTROLLER	([tableView numberOfColumns] == 3)

#define MAX_ADDRESS			(IS_BREAKPOINT_CONTROLLER ? 077777 : 07777)

#define BREAKPOINT_COLUMN		1
#define USER_BREAKOPCODE_COLUMN		1
#define SYSTEM_BREAKOPCODE_COLUMN	2


- (void) notifyUpdateBreakpointView:(NSNotification *)notification
{
	// NSLog (@"BreakpointController notifyUpdateBreakpointView");
	[enableAllButton setEnabled:[breakpoints hasBreakpointWithValueNotEqualTo:ALL_BREAKPOINTS]];
	[disableAllButton setEnabled:[breakpoints hasBreakpointWithValueNotEqualTo:0]];
	[tableView reloadData];
}


- (IBAction) addBreakpoint:(id)sender
{
	[[sender window] makeKeyWindow];
	[breakpoints setBreakpointWithIdentifier:0 value:ALL_BREAKPOINTS];
	[tableView selectRowIndexes:[NSIndexSet indexSetWithIndex:0] byExtendingSelection:NO];
	[tableView editColumn:[tableView columnWithIdentifier:
		[NSString stringWithFormat:@"%d", ADDRESS_COLUMN]]
		row:0 withEvent:nil select:YES];
}


- (IBAction) deleteBreakpoint:(id)sender
{
	int selectedRow = [tableView selectedRow];
	[tableView reloadData];		// when editing, this saves the edited cell that will be deleted
	[breakpoints deleteBreakpointsAtIndexes:[tableView selectedRowIndexes]];
	// reselect old row, otherwise selection jumps to the position of the edited but deleted breakpoint
	[tableView selectRowIndexes:[NSIndexSet indexSetWithIndex:selectedRow] byExtendingSelection:NO];
}


- (IBAction) enableAllBreakpoints:(id)sender
{
	[breakpoints setAllValues:ALL_BREAKPOINTS];
}


- (IBAction) disableAllBreakpoints:(id)sender
{
	[breakpoints setAllValues:0];
}


- (void) tableViewSelectionDidChange:(NSNotification *)notification
{
	[deleteButton setEnabled:[tableView numberOfSelectedRows] != 0];
}


- (int) numberOfRowsInTableView:(NSTableView *)tableView
{
	return [breakpoints numberOfBreakpoints];
}


- (id) tableView:(NSTableView *)tableView
	objectValueForTableColumn:(NSTableColumn *)column row:(int)row
{
	int columnID = [[column identifier] intValue];
	return columnID == ADDRESS_COLUMN ?
		[NSNumber numberWithInt:[breakpoints identifierAtIndex:row]] :
		[NSNumber numberWithBool:[breakpoints valueAtIndex:row] & columnID];
}


- (void) tableView:(NSTableView *)tabView setObjectValue:(id)object
	forTableColumn:(NSTableColumn *)column row:(int)row
{
	int columnID = [[column identifier] intValue];
	if (columnID == ADDRESS_COLUMN) {
		[breakpoints deleteBreakpointAtIndex:row];
		if ([object isKindOfClass:[NSNumber class]])
			[tableView selectRowIndexes:[NSIndexSet indexSetWithIndex:
				[breakpoints setBreakpointWithIdentifier:[object intValue]
					value:ALL_BREAKPOINTS]]
				byExtendingSelection:NO];
		else {
			NSIndexSet *index = [breakpoints setBreakpointsWithIdentifierArray:object
				value:ALL_BREAKPOINTS];
			[tableView selectRowIndexes:index byExtendingSelection:NO];
			row = [index firstIndex];
		}
		[tableView editColumn:0 row:0 withEvent:nil select:NO];
		// [tableView abortEditing] does not work, so edit an invalid cell
		[tableView scrollRowToVisible:row];
		[tableView reloadData];
		[enableAllButton setEnabled:[breakpoints hasBreakpointWithValueNotEqualTo:ALL_BREAKPOINTS]];
		[disableAllButton setEnabled:[breakpoints hasBreakpointWithValueNotEqualTo:0]];
	} else {
		unsigned value = [breakpoints valueAtIndex:row];
		if ([[NSApp currentEvent] modifierFlags] & NSAlternateKeyMask)
			value = value ? 0 : ALL_BREAKPOINTS;
		else
			value = (value & columnID) ? (value & ~columnID) : (value | columnID);
		[breakpoints setBreakpointWithIdentifier:[breakpoints identifierAtIndex:row] value:value];
	}
}


- (NSString *) tableView:(NSTableView *)tabView toolTipForCell:(NSCell *)cell
	rect:(NSRectPointer)rect tableColumn:(NSTableColumn *)column row:(int)row
	mouseLocation:(NSPoint)mouseLocation
{
	switch ([[column identifier] intValue]) {
	case ADDRESS_COLUMN :
		return IS_BREAKPOINT_CONTROLLER ?
			NSLocalizedString(
			@"Address where the PDP-8 halts when the breakpoint is enabled", @"") :
			NSLocalizedString(
			@"Opcode that causes the PDP-8 to halt when enabled for the current mode", @"");
	case BREAKPOINT_COLUMN | USER_BREAKOPCODE_COLUMN :
		return IS_BREAKPOINT_CONTROLLER ?
			NSLocalizedString(@"Enables or disables this breakpoint", @"") :
			NSLocalizedString(@"Enables or disables this break opcode for PDP-8 user mode "
				@"(option-click to set user and system mode simultaneously)", @"");
	case SYSTEM_BREAKOPCODE_COLUMN :
		return NSLocalizedString(@"Enables or disables this break opcode for PDP-8 system mode "
				@"(option-click to set user and system mode simultaneously)", @"");

	default :
		NSAssert (FALSE, @"Unknown column in BreakpointControllers table view");
		break;
	}
	return nil;
}


- (BOOL) control:(NSControl *)control didFailToFormatString:(NSString *)string
	errorDescription:(NSString *)error
{
	[control abortEditing];
	NSAlert *alert = [[NSAlert alloc] init];
	[alert setMessageText:error];
	[alert beginSheetModalForWindow:[control window] modalDelegate:self
		didEndSelector:nil contextInfo:nil];
	[alert release];
	return NO;
}


- (void) awakeFromNib
{
	NSCell *dataCell = [[tableView tableColumnWithIdentifier:
				[NSString stringWithFormat:@"%d", ADDRESS_COLUMN]] dataCell];
	[dataCell setFormatter:[OctalFormatter formatterWithBitMask:MAX_ADDRESS wildcardAllowed:YES]];
	[dataCell setFont:[NSFont userFixedPitchFontOfSize:11]];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyUpdateBreakpointView:)
		name:BREAKPOINTS_CHANGED_NOTIFICATION object:breakpoints];
	/* set max width = min width of the panel: IB only allows max width one pixel more
	   than min width, so the user can resize the width for one pixel - bug in IB? */
	NSSize size = [[tableView window] minSize];
	size.height = [[tableView window] maxSize].height;
	[[tableView window] setMaxSize:size];
	[addButton setEnabled:YES];	// to avoid Analyzer warning "addButton is never used"
}


@end


