/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	CPUMemoryViewController.m - Controller for the CPU window memory view
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


#import <Carbon/Carbon.h>		// for Help Manger functions

#import "CPUMemoryViewController.h"
#import "PDP8.h"
#import "Opcode.h"
#import "Breakpoint.h"
#import "BreakpointArray.h"
#import "NSTableView+Scrolling.h"
#import "NonWrappingTableView.h"
#import "OpcodeFormatter.h"
#import "Disassembler.h"
#import "Unicode.h"
#import "Utilities.h"


#define PC_ARROW_BLUE_IMAGE			@"pcArrowBlue"
#define PC_ARROW_GRAPHITE_IMAGE			@"pcArrowGraphite"
#define PC_ARROW_DRAG_TYPE			@"pcArrowDragType"
#define UPDATE_MEMORY_NOTIFICATION		@"UpdateMemoryNotification"

#define PC_DEFAULT_ROW_PREFS_KEY		@"DefaultPCRow"

#define PC_DEFAULT_ROW	10

#define PC_COLUMN	0
#define BP_COLUMN	1
#define ADDR_COLUMN	2
#define WORD_COLUMN	3
#define OPCODE_COLUMN	4

#define CONTEXTMENU_SET_BREAKPOINT		0
#define CONTEXTMENU_SET_BREAKOPCODE		1
#define CONTEXTMENU_SET_SYSTEM_BREAKOPCODE	2
#define CONTEXTMENU_SET_USER_BREAKOPCODE	3
#define CONTEXTMENU_SET_PC			4
#define CONTEXTMENU_GO_AND_STOP_HERE		5
#define CONTEXTMENU_TRACE_AND_STOP_HERE		6
#define CONTEXTMENU_SET_DEFAULT_PC_ROW		7


@interface CPUMemoryTableView : NonWrappingTableView <OpcodeFormatterAddressGetter>
{
}
@end


@implementation CPUMemoryTableView


- (NSImage *) dragImageForRowsWithIndexes:(NSIndexSet *)dragRows tableColumns:(NSArray *)tableColumns
	event:(NSEvent*)dragEvent offset:(NSPointPointer)dragImageOffset
{
	return [NSImage imageNamed:[NSColor currentControlTint] == NSGraphiteControlTint ?
		PC_ARROW_GRAPHITE_IMAGE : PC_ARROW_BLUE_IMAGE];
}


- (NSMenu *) menuForEvent:(NSEvent *)event
{
	int row = [self rowAtPoint:[self convertPoint:[event locationInWindow] fromView:nil]];
	[self selectRowIndexes:[NSIndexSet indexSetWithIndex:row] byExtendingSelection:NO];
	return [self menu];
}


- (void) mouseDown:(NSEvent *)event
{
	CFStringRef tipContent;
	
	NSPoint point = [self convertPoint:[event locationInWindow] fromView:nil];
	int row = [self rowAtPoint:point];
	[self selectRowIndexes:[NSIndexSet indexSetWithIndex:row] byExtendingSelection:NO];
	// why does [super mouseDown:event] not select the clicked row immediately?
	if (([event modifierFlags] & NSShiftKeyMask) && [self columnAtPoint:point] == OPCODE_COLUMN &&
		(tipContent = (CFStringRef) [[self delegate] operandInfoAtAddress:row])) {
		HMHelpContentRec tip;
		tip.version = kMacHelpVersion;
		NSRect rect = [self frameOfCellAtColumn:OPCODE_COLUMN row:row];
		rect.origin = [[self window] convertBaseToScreen:
			[self convertPoint:rect.origin toView:nil]];
		float scale = [[self window] userSpaceScaleFactor];
		tip.absHotRect.left = rect.origin.x / scale + 33;
		tip.absHotRect.right = (rect.origin.x + rect.size.width) / scale;
		tip.absHotRect.top =
			([[[NSScreen screens] objectAtIndex:0] frame].size.height - rect.origin.y) / scale;
		tip.absHotRect.bottom = tip.absHotRect.top + rect.size.height / scale;
		tip.tagSide = kHMOutsideBottomLeftAligned;
		tip.content[0].contentType = tip.content[1].contentType = kHMCFStringContent;
		tip.content[0].u.tagCFString = tip.content[1].u.tagCFString = tipContent;
		HMDisplayTag (&tip);
	}
	[super mouseDown:event];
	HMHideTag ();
}


- (void) selectRowIndexes:(NSIndexSet *)indexes byExtendingSelection:(BOOL)extend
{
	if ((int) [indexes firstIndex] != [self selectedRow])
		HMHideTag ();
	[super selectRowIndexes:indexes byExtendingSelection:extend];
}


- (int) getCurrentAddress	// OpcodeFormatterAddressGetter protocol
{
	return [self selectedRow];
}


@end


@implementation CPUMemoryViewController


- (void) awakeFromNib
{
	NSSize size;
	
	NSFont *font = [NSFont userFixedPitchFontOfSize:11];
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	
	size.width = 1;
	size.height = [memoryView rowHeight] + 2;
	[[memoryView window] setResizeIncrements:size];
	[[[[memoryView tableColumns] objectAtIndex:ADDR_COLUMN] dataCell] setFont:font];
	[[[[memoryView tableColumns] objectAtIndex:WORD_COLUMN] dataCell] setFont:font];
	[[[[memoryView tableColumns] objectAtIndex:OPCODE_COLUMN] dataCell] setFont:font];
	[[[[memoryView tableColumns] objectAtIndex:OPCODE_COLUMN] dataCell] setFormatter:
		[OpcodeFormatter formatterWithPDP8:pdp8 addressGetter:memoryView]];
	[memoryView setTarget:self];
	[memoryView setDoubleAction:@selector(memoryViewDoubleClick:)];
	[memoryView registerForDraggedTypes:[NSArray arrayWithObject:PC_ARROW_DRAG_TYPE]];
	pcDefaultRow = [[NSUserDefaults standardUserDefaults] integerForKey:PC_DEFAULT_ROW_PREFS_KEY];
	if (pcDefaultRow == 0)
		pcDefaultRow = PC_DEFAULT_ROW;
	[memoryView scrollRowToTop:0200 + 1 - pcDefaultRow];
	[[memoryView superview] setPostsFrameChangedNotifications:YES];
	/* frame changed notification seems not work for the tableview itself */
	[defaultCenter addObserver:self selector:@selector(notifyMemoryViewSizeChanged:)
		name:NSViewFrameDidChangeNotification object:[memoryView superview]]; 
	[defaultCenter addObserver:self selector:@selector(notifyMemoryChanged:)
		name:MEMORY_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyMemoryChanged:)
		name:EAE_MODE_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyMemoryChanged:)
		name:BREAKPOINTS_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyMemoryChanged:)
		name:PROGRAM_COUNTER_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyMemoryChanged:)
		name:DF_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyUpdateMemoryView:)
		name:UPDATE_MEMORY_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyStepPDP8:)
		name:PDP8_STEP_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyStopPDP8:)
		name:PDP8_STOP_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyPCChanged:)
		name:PROGRAM_COUNTER_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyKeyWindowChanged:)
		name:NSWindowDidBecomeKeyNotification object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyKeyWindowChanged:)
		name:NSWindowDidResignKeyNotification object:nil];
}


- (BOOL) tableView:(NSTableView *)tableView shouldEditTableColumn:(NSTableColumn *)column row:(int)row
{
	return 0 <= row && row < [pdp8 memorySize];
}


- (BOOL) tableView:(NSTableView *)tableView shouldSelectRow:(int)row
{
	return YES;
}


- (BOOL) tableView:(NSTableView *)tableView shouldSelectTableColumn:(NSTableColumn *)column
{
	if ([[column identifier] intValue] == PC_COLUMN)
		[memoryView scrollRowToTop:max(0, [pdp8 getProgramCounter] - pcDefaultRow + 1)];
	return NO;
}


- (BOOL) tableView:(NSTableView *)tableView writeRowsWithIndexes:(NSIndexSet *)rows
	toPasteboard:(NSPasteboard *)pboard
{
	if ([rows count] != 1 || [rows firstIndex] != [pdp8 getProgramCounter])
		return NO;
	[pboard declareTypes:[NSArray arrayWithObject:PC_ARROW_DRAG_TYPE] owner:nil];
	return YES;
}


- (NSDragOperation) tableView:(NSTableView *)tableView validateDrop:(id <NSDraggingInfo>)info
	proposedRow:(int)row proposedDropOperation:(NSTableViewDropOperation)operation
{
	return operation == NSTableViewDropOn ? NSDragOperationPrivate : NSDragOperationNone;
}


- (BOOL) tableView:(NSTableView *)tableView acceptDrop:(id <NSDraggingInfo>)info
	row:(int)row dropOperation:(NSTableViewDropOperation)operation
{
	if ((row > 07777) & ! [pdp8 hasKM8E])
		return NO;
	[pdp8 setProgramCounter:row];
	return YES;
}


- (int) numberOfRowsInTableView:(NSTableView *)tableView
{
	return PDP8_MEMSIZE;
}


- (id) tableView:(NSTableView *)tableView objectValueForTableColumn:(NSTableColumn *)column row:(int)row
{
	switch ([[column identifier] intValue]) {
	case PC_COLUMN :
		// save the visible range now to be able to scroll to the correct location after "go"
		visibleMemoryRange = [memoryView rowsInRect:[memoryView visibleRect]];
		if (row == [pdp8 getProgramCounter])
			return [NSImage imageNamed:([NSColor currentControlTint] == NSBlueControlTint)
					&& [[tableView window] isKeyWindow] ?
				PC_ARROW_BLUE_IMAGE : PC_ARROW_GRAPHITE_IMAGE];
		break;
	case BP_COLUMN :
		if ([breakpoints valueForIdentifier:row])
			return [NSImage imageNamed:@"breakpoint"];
		switch ([breakopcodes valueForIdentifier:[pdp8 memoryAt:row]]) {
		case BREAKOPCODE :
			return [NSImage imageNamed:@"breakOpcode"];
		case USERMODE_BREAKOPCODE :
			return [NSImage imageNamed:@"breakOpcodeU"];
		case SYSTEMMODE_BREAKOPCODE :
			return [NSImage imageNamed:@"breakOpcodeS"];
		}
		break;
	case ADDR_COLUMN :
		if ((row & 007770) == 000010) {		// autoincrement locations are underlined
			NSMutableParagraphStyle *style =
				[[[NSMutableParagraphStyle alloc] init] autorelease];
			[style setAlignment:NSCenterTextAlignment];
			return [[[NSAttributedString alloc]
				initWithString:[NSString stringWithFormat:@"%5.5o", row]
				attributes:[NSDictionary dictionaryWithObjectsAndKeys:
					[NSNumber numberWithInt:NSSingleUnderlineStyle],
						NSUnderlineStyleAttributeName,
					style, NSParagraphStyleAttributeName,
					nil]] autorelease];
		}
		return [NSString stringWithFormat:@"%5.5o", row];
	case WORD_COLUMN :
		return [NSString stringWithFormat:@"%4.4o", [pdp8 memoryAt:row]];
	case OPCODE_COLUMN :
		return [Opcode opcodeWithAddress:row value:[pdp8 memoryAt:row]];
	}
	return nil;
}


- (NSString *) tableView:(NSTableView *)tableView toolTipForCell:(NSCell *)cell
	rect:(NSRectPointer)rect tableColumn:(NSTableColumn *)column row:(int)row
	mouseLocation:(NSPoint)mouseLocation
{
	switch ([[column identifier] intValue]) {
	case PC_COLUMN :
		return NSLocalizedString(
			@"In this column, an arrow indicates the PDP-8/E program counter.\n\n"
			"Drag the arrow or double-click a location to modify the program counter.\n\n"
			"Click the column header to scroll to the current program counter location.", @"");
	case BP_COLUMN :
		return [NSString stringWithFormat:NSLocalizedString(
			@"This column shows breakpoints with a red dot. "
			"Break opcodes are indicated with a yellow dot. "
			"A small %Cs%C or %Cu%C in the dot indicate system or user mode break opcodes.\n\n"
			"Double-click sets or clears a breakpoint.\n\n"
			"Option-double-click and command-option-double-click toogles break opcodes.", @""),
			UNICODE_LEFT_DOUBLEQUOTE, UNICODE_RIGHT_DOUBLEQUOTE,
			UNICODE_LEFT_DOUBLEQUOTE, UNICODE_RIGHT_DOUBLEQUOTE];
	case ADDR_COLUMN :
		return NSLocalizedString(@"This column displays the memory adresses. "
			"Adresses of autoincrement memory locations are underlined.", @"");
	case WORD_COLUMN :
		return NSLocalizedString(@"This column displays the octal memory content.", @"");
	case OPCODE_COLUMN :
		return NSLocalizedString(@"This column displays the disassembled PDP-8 instruction.\n\n"
			"Shift-click to view the operands of MRIs.", @"");
	}
	return nil;
}


- (void) tableView:(NSTableView *)tableView setObjectValue:(Opcode *)opcode
	forTableColumn:(NSTableColumn *)column row:(int)row
{
	// why do we get this setObjectValue message for read-only cells?
	if (row < [pdp8 memorySize]) {
		ignoreUpdateMemoryNotification = YES;
		[pdp8 setMemoryAtAddress:row toValue:[opcode word0]];
		if ([opcode word1] >= 0)
			[pdp8 setMemoryAtNextAddress:row toValue:[opcode word1]];
	}
}


- (BOOL) control:(NSControl *)control didFailToFormatString:(NSString *)string
	errorDescription:(NSString *)error
{
	NSRange range;
	
	NSScanner *scanner = [NSScanner scannerWithString:error];
	NSAlert *alert = [[NSAlert alloc] init];
	[scanner scanInt:(signed *) &range.location];
	range.length = [string length] - range.location;
	[[control currentEditor] setSelectedRange:range];
	[alert setMessageText:[error substringFromIndex:[scanner scanLocation] + 1]];
	[alert beginSheetModalForWindow:[control window]
		modalDelegate:nil didEndSelector:nil contextInfo:nil];
	[alert release];
	return NO;
}


- (BOOL) control:(NSControl *)control textView:(NSTextView *)textView doCommandBySelector:(SEL)command
{
	if (command == @selector(cancelOperation:)) {
		// ESC aborts editing of the current cell
		[control abortEditing];
		return YES;
	}
	return NO;
}


- (void) memoryViewDoubleClick:(id)sender
{
	unsigned modifiers;
	
	int row = [sender clickedRow];
	if (row < 0 || [pdp8 isRunning])
		return;
	switch ([sender clickedColumn]) {
	case PC_COLUMN :
		if ([pdp8 hasKM8E] || row < 010000)
			[pdp8 setProgramCounter:row];
		break;
	case BP_COLUMN :
	case ADDR_COLUMN :
		modifiers = [[NSApp currentEvent] modifierFlags];
		if (modifiers & NSAlternateKeyMask) {
			unsigned opcode = [pdp8 memoryAt:row];
			unsigned value = [breakopcodes valueForIdentifier:opcode];
			if (modifiers & NSCommandKeyMask)
				value = (value + BREAKOPCODE) & BREAKOPCODE;
			else
				value = value ? 0 : BREAKOPCODE;
			[breakopcodes setBreakpointWithIdentifier:opcode value:value];
		} else
			[breakpoints setBreakpointWithIdentifier:row
				value:[breakpoints valueForIdentifier:row] ? 0 : BREAKPOINT];
		[sender reloadData];
		break;
	case WORD_COLUMN :
		break;
	case OPCODE_COLUMN :
		// with Leopard, this is required, don't know why: for editable cells, the
		// double action should not be called, but the editing should start automatically
		// (NSTableView documentation)
		if (row < [pdp8 memorySize])
			[sender editColumn:OPCODE_COLUMN row:row withEvent:nil select:YES];
		break;
	}
}


- (BOOL) validateMenuItem:(NSMenuItem *)menuItem
{
	if ([pdp8 isRunning])
		return FALSE;
	int row = [memoryView selectedRow];
	unsigned breakop = [breakopcodes valueForIdentifier:[pdp8 memoryAt:row]];
	switch ([menuItem tag]) {
	case CONTEXTMENU_SET_BREAKPOINT :
		[menuItem setTitle:[breakpoints valueForIdentifier:row] ?
			NSLocalizedString(@"Clear Breakpoint", @"") :
			NSLocalizedString(@"Set Breakpoint", @"")];
		return TRUE;
	case CONTEXTMENU_SET_BREAKOPCODE :
		[menuItem setTitle:breakop == BREAKOPCODE ?
			NSLocalizedString(@"Clear Break Opcode", @"") :
			NSLocalizedString(@"Set Break Opcode", @"")];
		return breakop == BREAKOPCODE || breakop == 0;
	case CONTEXTMENU_SET_SYSTEM_BREAKOPCODE :
		[menuItem setTitle:(breakop & SYSTEMMODE_BREAKOPCODE) ?
			NSLocalizedString(@"Clear System Mode Break Opcode", @"") :
			NSLocalizedString(@"Set System Mode Break Opcode", @"")];
		return TRUE;
	case CONTEXTMENU_SET_USER_BREAKOPCODE :
		[menuItem setTitle:(breakop & USERMODE_BREAKOPCODE) ?
			NSLocalizedString(@"Clear User Mode Break Opcode", @"") :
			NSLocalizedString(@"Set User Mode Break Opcode", @"")];
		return TRUE;
	case CONTEXTMENU_SET_PC :
	case CONTEXTMENU_GO_AND_STOP_HERE :
	case CONTEXTMENU_TRACE_AND_STOP_HERE :
		return row < 010000 || [pdp8 hasKM8E];
	case CONTEXTMENU_SET_DEFAULT_PC_ROW :
		return TRUE;
	}
	return FALSE;
}


- (IBAction) handleContextMenu:(id)sender
{
	int row = [memoryView selectedRow];
	unsigned opcode = [pdp8 memoryAt:row];
	unsigned breakop = [breakopcodes valueForIdentifier:opcode];
	switch ([sender tag]) {
	case CONTEXTMENU_SET_BREAKPOINT :
		[breakpoints setBreakpointWithIdentifier:row
			value:[breakpoints valueForIdentifier:row] ? 0 : BREAKPOINT];
		break;
	case CONTEXTMENU_SET_BREAKOPCODE :
		[breakopcodes setBreakpointWithIdentifier:opcode value:breakop ? 0 : BREAKOPCODE];
		break;
	case CONTEXTMENU_SET_SYSTEM_BREAKOPCODE :
		[breakopcodes setBreakpointWithIdentifier:opcode value:breakop ^ SYSTEMMODE_BREAKOPCODE];
		break;
	case CONTEXTMENU_SET_USER_BREAKOPCODE :
		[breakopcodes setBreakpointWithIdentifier:opcode value:breakop ^ USERMODE_BREAKOPCODE];
		break;
	case CONTEXTMENU_SET_PC :
		if ([pdp8 hasKM8E] || row < 010000)
			[pdp8 setProgramCounter:row];
		break;
	case CONTEXTMENU_GO_AND_STOP_HERE :
		[pdp8 go:row];
		break;
	case CONTEXTMENU_TRACE_AND_STOP_HERE :
		[pdp8 trace:row];
		break;
	case CONTEXTMENU_SET_DEFAULT_PC_ROW :
		pcDefaultRow = row - [memoryView rowsInRect:[memoryView visibleRect]].location + 1;
		[[NSUserDefaults standardUserDefaults] setInteger:pcDefaultRow
			forKey:PC_DEFAULT_ROW_PREFS_KEY];
		[memoryView scrollRowToTop:max(0, [pdp8 getProgramCounter] - pcDefaultRow + 1)];
		break;
	}
}


- (NSString *) operandInfoAtAddress:(int)addr
{
	return [[Disassembler sharedDisassembler] operandInfoForPDP8:pdp8 atAddress:addr];
}


#pragma mark Notifications


- (void) notifyKeyWindowChanged:(NSNotification *)notification
{
	if ([[notification object] isEqual:[memoryView window]])
		[memoryView setNeedsDisplayInRect:
			[memoryView frameOfCellAtColumn:PC_COLUMN row:[pdp8 getProgramCounter]]];
}


- (void) notifyMemoryViewSizeChanged:(NSNotification *)notification
{
	if ([memoryView rowsInRect:[memoryView visibleRect]].length < pcDefaultRow) {
		pcDefaultRow = PC_DEFAULT_ROW;
		[[NSUserDefaults standardUserDefaults] removeObjectForKey:PC_DEFAULT_ROW_PREFS_KEY];
	}
}


- (void) notifyMemoryChanged:(NSNotification *)notification
{
	/* coalesc the multiple MEMORY_CHANGED_NOTIFICATIONS (caused by the different register update
	   notifications) to one UPDATE_MEMORY_NOTIFICATION to avoid time consuming repeated memory
	   view updates */
	// NSLog (@"CPUMemoryViewController notifyMemoryChanged");
	[[NSNotificationQueue defaultQueue] enqueueNotification:
		[NSNotification notificationWithName:UPDATE_MEMORY_NOTIFICATION object:self]
		postingStyle:NSPostASAP coalesceMask:NSNotificationCoalescingOnName forModes:nil];
}


- (void) notifyUpdateMemoryView:(NSNotification *)notification
{
	// NSLog (@"CPUMemoryViewController notifyUpdateMemoryView ign=%d", ignoreUpdateMemoryNotification);
	if (ignoreUpdateMemoryNotification)
		ignoreUpdateMemoryNotification = NO;
	else
		[memoryView reloadData];
}


- (void) notifyStepPDP8:(NSNotification *)notification
{
	// NSLog (@"CPUMemoryViewController notifyStepPDP8");
	NSRange range = [memoryView rowsInRect:[memoryView visibleRect]];
	unsigned pc = [pdp8 getProgramCounter];
	if (range.location + range.length == pc)
		[memoryView scrollRowToVisible:pc];
	else if (pc < range.location || range.location + range.length <= pc)
		[memoryView scrollRowToTop:max(0, pc - pcDefaultRow + 1)];
	[memoryView reloadData];
	ignoreUpdateMemoryNotification = YES;
}


- (void) notifyStopPDP8:(NSNotification *)notification
{
	// NSLog (@"CPUMemoryViewController notifyStopPDP8");
	NSRange range = [memoryView rowsInRect:[memoryView visibleRect]];
	unsigned pc = [pdp8 getProgramCounter];
	if (pc < range.location || range.location + range.length <= pc)
		[memoryView scrollRowToTop:max(0, pc - pcDefaultRow + 1)];
}


- (void) notifyPCChanged:(NSNotification *)notification
{
	NSRange range = [memoryView rowsInRect:[memoryView visibleRect]];
	unsigned pc = [pdp8 getProgramCounter];
	if (range.location + range.length == pc) {
		[memoryView scrollRowToVisible:pc];
	} else if (pc < range.location || range.location + range.length <= pc) {
		[memoryView scrollRowToTop:max(0, pc - pcDefaultRow + 1)];
	}
}


@end
