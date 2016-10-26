/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK8EController.h - Controller for RK8-E register view
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


#import "PluginFramework/PDP8.h"
#import "PluginFramework/RegisterFormCell.h"
#import "PluginFramework/KeepInMenuWindow.h"

#import "RK8EController.h"
#import "RK8E.h"


@implementation RK8EController


/* The tags of the popups contain in the high byte the mask for the bits in the command register
   represented by the popup, and in the low byte the shift to move the bits of the popup to the
   correct position in the command register. The tags of the items of the popup menus are the item
   index, i. e. the bits to set when the item is selected. */


- (IBAction) commandPopupClicked:(id)sender
{
	unsigned short tag = [sender tag];
	unsigned short mask = (tag >> 8) << (tag & 0xff);
	[rk8e setCommand:([rk8e getCommand] & ~mask) | ([[sender selectedItem] tag] << (tag & 0xff))];
}


- (void) notifyCommandChanged:(NSNotification *)notification
{
	// NSLog (@"RK8EController notifyCommandChanged");
	unsigned short tag;
	unsigned short command = [rk8e getCommand];
#define SET_COMMAND_POPUP(popup) \
	tag = [(popup) tag]; [(popup) selectItemAtIndex:(command >> (tag & 0xff)) & (tag >> 8)]
	SET_COMMAND_POPUP (cmdFunction);
	SET_COMMAND_POPUP (cmdInterrupt);
	SET_COMMAND_POPUP (cmdSetDone);
	SET_COMMAND_POPUP (cmdBlockLength);
	SET_COMMAND_POPUP (cmdExtendedAddress);
	SET_COMMAND_POPUP (cmdDriveSelect);
	SET_COMMAND_POPUP (cmdBlockNumberMSB);
#undef  SET_COMMAND_POPUP
}


/* The tags of the status checkboxes are the corresponding bit masks of the flags in the status register. */


- (IBAction) statusCheckboxClicked:(id)sender
{
	if ([sender intValue])
		[rk8e setStatusBits:[sender tag] clearStatusBits:0];
	else
		[rk8e setStatusBits:0 clearStatusBits:[sender tag]];
}


- (void) notifyStatusChanged:(NSNotification *)notification
{
	// NSLog (@"RK8EController notifyStatusChanged");
	unsigned short status = [rk8e getStatus];
#define SET_STATUS_CHECKBOX(ckbox)	[(ckbox) setIntValue:(status & [(ckbox) tag]) ? 1 : 0]
	SET_STATUS_CHECKBOX (statusTransferDone);
	SET_STATUS_CHECKBOX (statusHeadInMotion);
	SET_STATUS_CHECKBOX (statusUnused);
	SET_STATUS_CHECKBOX (statusSeekFailed);
	SET_STATUS_CHECKBOX (statusFileNotReady);
	SET_STATUS_CHECKBOX (statusControlBusy);
	SET_STATUS_CHECKBOX (statusTimingError);
	SET_STATUS_CHECKBOX (statusWriteLockError);
	SET_STATUS_CHECKBOX (statusCRCError);
	SET_STATUS_CHECKBOX (statusDataRequestLate);
	SET_STATUS_CHECKBOX (statusDriveStatusError);
	SET_STATUS_CHECKBOX (statusCylinderAddressError);
#undef  SET_STATUS_CHECKBOX
}


- (void) setRK8EControllerEnabled:(BOOL)flag
{
	NSControl *control;
	NSEnumerator *enumerator = [[[rk8eBox contentView] subviews] objectEnumerator];
	while ((control = [enumerator nextObject]))
		[control setEnabled:flag];
}


- (void) notifyGo:(NSNotification *)notification
{
	[self setRK8EControllerEnabled:NO];
}


- (void) notifyStep:(NSNotification *)notification
{
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter postNotificationName:COMMAND_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:STATUS_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:MEMORYADDRESS_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:BLOCKNUMBER_CHANGED_NOTIFICATION object:self];
}


- (void) notifyStop:(NSNotification *)notification
{
	[self notifyStep:notification];
	[self setRK8EControllerEnabled:YES];
}


- (void) setupNotifications
{
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyCommandChanged:) name:COMMAND_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyStatusChanged:) name:STATUS_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyGo:) name:PDP8_GO_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyStop:) name:PDP8_STOP_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyStep:) name:PDP8_STEP_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyPluginsLoaded:)
		name:PLUGINS_LOADED_NOTIFICATION object:nil];
}


- (void) setupRegisters
{
	[commandRegister setupRegisterFor:rk8e
		getRegisterValue:@selector(getCommand) setRegisterValue:@selector(setCommand:)
		changedNotificationName:COMMAND_CHANGED_NOTIFICATION mask:07777 base:8];
	[statusRegister setupRegisterFor:rk8e
		getRegisterValue:@selector(getStatus) setRegisterValue:@selector(setStatus:)
		changedNotificationName:STATUS_CHANGED_NOTIFICATION mask:07777 base:8];
	[blockNumberRegister setupRegisterFor:rk8e
		getRegisterValue:@selector(getBlockNumber) setRegisterValue:@selector(setBlockNumber:)
		changedNotificationName:BLOCKNUMBER_CHANGED_NOTIFICATION mask:017777 base:8];
	[memoryAddressRegister setupRegisterFor:rk8e
		getRegisterValue:@selector(getMemoryAddress) setRegisterValue:@selector(setMemoryAddress:)
		changedNotificationName:MEMORYADDRESS_CHANGED_NOTIFICATION mask:077777 base:8];
}


- (void) notifyPluginsLoaded:(NSNotification *)notification
{
	[window orderBackFromDefaults:self];
}


- (void) awakeFromNib
{
	[self setupRegisters];
	[self setupNotifications];
}


@end
