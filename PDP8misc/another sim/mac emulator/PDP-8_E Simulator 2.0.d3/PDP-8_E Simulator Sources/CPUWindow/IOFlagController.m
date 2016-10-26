/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	IOFlagController.m - Controller for the I/O flags table view in the CPU window
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
 

#import "IOFlagController.h"
#import "PDP8.h"


#define NAME_COLUMN		0
#define IENABLE_COLUMN		1
#define IOFLAG_COLUMN		2

#define USER_MODE_ROW		0


@implementation IOFlagController


- (void) awakeFromNib
{
	deviceNames = [[NSMutableArray alloc] init];
	[self addIODevice:NSLocalizedString(@"User Mode", @"")];
	[[[[ioFlagsView tableColumns] objectAtIndex:NAME_COLUMN] dataCell]
		setFont:[NSFont userFontOfSize:11]];
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter addObserver:self selector:@selector(notifyIOFlagsChanged:)
		name:IOFLAGS_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyKM8EMount:)
		name:KM8E_MOUNT_NOTIFICATION object:nil];
	/* Under certain contitions, the checkboxes in the I/O column are not activated/deactivated
	   when the CPU window activates/deactivates. The notifyKeyWindowChanged notification is
	   a workaround. Maybe a Cocoa bug with 10.4 and 10.5 (it does not appear when the table
	   control has the 10.5 only hilight style "sourcelist"). */	
	[defaultCenter addObserver:self selector:@selector(notifyKeyWindowChanged:)
		name:NSWindowDidResignKeyNotification object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyKeyWindowChanged:)
		name:NSWindowDidBecomeKeyNotification object:nil];
}


- (void) notifyKeyWindowChanged:(NSNotification *)notification
{
	if ([[notification object] isEqualTo:[ioFlagsView window]])
		[ioFlagsView reloadData];
}


- (void) notifyIOFlagsChanged:(NSNotification *)notification
{
	[ioFlagsView reloadData];
}


- (void) notifyKM8EMount:(NSNotification *)notification
{
	if ([pdp8 hasKM8E])
		[self enableIODevice:userFLAG];
	else
		[self disableIODevice:userFLAG];
}


- (unsigned) numberOfAvailableFlags
{
	return 8 * sizeof(enabledFlags) - [deviceNames count];
}


- (unsigned long) addIODevice:(NSString *)name
{
	unsigned long ioflag;
	
	if ([deviceNames count] == 8 * sizeof(enabledFlags))
		return 0;	// all flags already allocated
	[deviceNames addObject:name];
	[ioFlagsView noteNumberOfRowsChanged];
	ioflag = 1 << ([deviceNames count] - 1);
	enabledFlags |= ioflag;
	return ioflag;
}


- (void) disableIODevice:(unsigned long)ioflag
{
	NSAssert1 ((((1 << [deviceNames count]) - 1) & ioflag) == ioflag, @"Invalid IO flag: %o", ioflag);
	enabledFlags &= ~ioflag;
	[pdp8 clearInterruptMaskBits:ioflag];
	[pdp8 clearIOFlagBits:ioflag];
	[ioFlagsView reloadData];
}


- (void) enableIODevice:(unsigned long)ioflag
{
	NSAssert1 ((((1 << [deviceNames count]) - 1) & ioflag) == ioflag, @"Invalid IO flag: %o", ioflag);
	enabledFlags |= ioflag;
	[ioFlagsView reloadData];
}


- (int) numberOfRowsInTableView:(NSTableView *)tableView
{
	return [deviceNames count];
}


- (BOOL) tableView:(NSTableView *)tableView shouldSelectRow:(int)row 
{
	return ((1 << row) & enabledFlags) != 0;
} 


- (id) tableView:(NSTableView *)tableView
	objectValueForTableColumn:(NSTableColumn *)column row:(int)row
{
	switch ([[column identifier] intValue]) {
	case NAME_COLUMN :
		if ((1 << row) & enabledFlags)
			return [deviceNames objectAtIndex:row];
		// disabled devices are gray
		return [[[NSAttributedString alloc] initWithString:[deviceNames objectAtIndex:row]
			attributes:[NSDictionary dictionaryWithObject:[NSColor grayColor]
				forKey:NSForegroundColorAttributeName]] autorelease];
	case IENABLE_COLUMN :
		return [NSNumber numberWithBool:[pdp8 getInterruptMaskBits:1 << row] != 0];
	case IOFLAG_COLUMN :
		return [NSNumber numberWithBool:[pdp8 getIOFlagBits:1 << row] != 0];
	}
	return nil;
}


- (void) tableView:(NSTableView *)tableView setObjectValue:(id)val
	forTableColumn:(NSTableColumn *)column row:(int)row
{
	// when the currently selected row becomes deactivated, the user can still click the check boxes
	if (((1 << row) & enabledFlags) == 0)
		return;
	switch ([[column identifier] intValue]) {
	case IENABLE_COLUMN :
		if (row == USER_MODE_ROW)	// can't clear or set this flag manually,
			break;			// it's set iff the KM8-E is present
		if ([val intValue])
			[pdp8 setInterruptMaskBits:1 << row];
		else
			[pdp8 clearInterruptMaskBits:1 << row];
		break;
	case IOFLAG_COLUMN :
		if ([val intValue])
			[pdp8 setIOFlagBits:1 << row];
		else
			[pdp8 clearIOFlagBits:1 << row];
		break;
	}
}


- (NSString *) tableView:(NSTableView *)tableView toolTipForCell:(NSCell *)cell
	rect:(NSRectPointer)rect tableColumn:(NSTableColumn *)column row:(int)row
	mouseLocation:(NSPoint)mouseLocation
{
	switch ([[column identifier] intValue]) {
	case NAME_COLUMN :
		break;
	case IENABLE_COLUMN :
		return NSLocalizedString(@"When the Interrupt Enable Flag is set, the "
			@"corresponding device can cause interrupts.", @"");
	case IOFLAG_COLUMN :
		return NSLocalizedString(@"A device raises its I/O Flag to signal to the CPU "
			@"that it has completed an I/O operation.", @"");
	}
	return nil;
}


@end
