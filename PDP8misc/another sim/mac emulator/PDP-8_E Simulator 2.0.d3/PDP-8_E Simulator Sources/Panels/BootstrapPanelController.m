/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	BootstrapPanelController.h - Controller for the bootstrap panel
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
 

#import "BootstrapPanelController.h"
#import "PDP8.h"


@implementation BootstrapPanelController


#define LOW_SPEED_RIM_TAG	0
#define HIGH_SPEED_RIM_TAG	1
#define BIN_LOADER_TAG		2
#define RK8E_BOOTCODE_TAG	3

#define LOW_SPEED_RIM_TAPE	@"lowspeedRIM.bin"
#define HIGH_SPEED_RIM_TAPE	@"highspeedRIM.bin"
#define BIN_LOADER_TAPE		@"binLoader.rim"
#define RK8E_BOOTCODE_TAPE	@"rk8eBootcode.bin"


- (void) awakeFromNib
{
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyMemorySizeChanged:)
		name:KM8E_MOUNT_NOTIFICATION object:nil];
}


- (BOOL) validateMenuItem:(NSMenuItem *)menuItem	// only the Load Bootstrap Loader menu item
{
	return ! [pdp8 isGoing];
}


- (void) notifyMemorySizeChanged:(NSNotification *)notification
{
	[ifStepper setMaxValue:([pdp8 memorySize] >> 12) - 1];
	[[ifStepper target] performSelector:[ifStepper action] withObject:ifStepper];
}


- (IBAction) loadBootstrapLoader:(id)sender
{
	unsigned addr, field;
	NSString *error, *path, *file;
	
	NSBundle *mainBundle = [NSBundle mainBundle];

	switch ([[loaderRadioButtons selectedCell] tag]) {
	case LOW_SPEED_RIM_TAG :
		path = [mainBundle pathForResource:LOW_SPEED_RIM_TAPE ofType:nil];
		file = LOW_SPEED_RIM_TAPE;
		field = [ifStepper intValue];
		addr = 07756;
		break;
	case HIGH_SPEED_RIM_TAG :
		path = [mainBundle pathForResource:HIGH_SPEED_RIM_TAPE ofType:nil];
		file = HIGH_SPEED_RIM_TAPE;
		field = [ifStepper intValue];
		addr = 07756;
		break;
	case BIN_LOADER_TAG :
		path = [mainBundle pathForResource:BIN_LOADER_TAPE ofType:nil];
		file = BIN_LOADER_TAPE;
		field = [ifStepper intValue];
		addr = 07777;
		break;
	case RK8E_BOOTCODE_TAG :
		path = [mainBundle pathForResource:RK8E_BOOTCODE_TAPE ofType:nil];
		file = RK8E_BOOTCODE_TAPE;
		field = 0;
		addr = 00027;
		break;
	default :	/* to avoid compiler "used uninitialized" warning */
		path = file = nil;
		field = addr = 0;
		break;
	}
	error = [pdp8 loadPaperTape:path toField:field];
	if (error) {
		if (! path)
			path = [NSString stringWithFormat:NSLocalizedString(
				@"The resource file %@ is missing in the application bundle.", @""), file];
		NSAlert *alert = [[NSAlert alloc] init];
		[alert setAlertStyle:NSWarningAlertStyle];
		[alert setMessageText:error];
		[alert setInformativeText:path];
		[alert runModal];
		[alert release];
	} else if ([adjustPCCheckbox intValue]) {
		[pdp8 setPC:addr];
		[pdp8 setIF:field];
		[pdp8 setIB:field];
	}
}


@end
