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


#import <Cocoa/Cocoa.h>


@class RK8E, RegisterFormCell, KeepInMenuWindow;


@interface RK8EController : NSObject {
@private
	IBOutlet RK8E			*rk8e;
	IBOutlet KeepInMenuWindow	*window;
	IBOutlet NSBox			*rk8eBox;
	IBOutlet RegisterFormCell	*commandRegister;
	IBOutlet NSPopUpButton		*cmdFunction;
	IBOutlet NSPopUpButton		*cmdInterrupt;
	IBOutlet NSPopUpButton		*cmdSetDone;
	IBOutlet NSPopUpButton		*cmdBlockLength;
	IBOutlet NSPopUpButton		*cmdExtendedAddress;
	IBOutlet NSPopUpButton		*cmdDriveSelect;
	IBOutlet NSPopUpButton		*cmdBlockNumberMSB;
	IBOutlet RegisterFormCell	*statusRegister;
	IBOutlet NSButton		*statusTransferDone;
	IBOutlet NSButton		*statusHeadInMotion;
	IBOutlet NSButton		*statusUnused;
	IBOutlet NSButton		*statusSeekFailed;
	IBOutlet NSButton		*statusFileNotReady;
	IBOutlet NSButton		*statusControlBusy;
	IBOutlet NSButton		*statusTimingError;
	IBOutlet NSButton		*statusWriteLockError;
	IBOutlet NSButton		*statusCRCError;
	IBOutlet NSButton		*statusDataRequestLate;
	IBOutlet NSButton		*statusDriveStatusError;
	IBOutlet NSButton		*statusCylinderAddressError;
	IBOutlet RegisterFormCell	*blockNumberRegister;
	IBOutlet RegisterFormCell	*memoryAddressRegister;
}

- (IBAction) commandPopupClicked:(id)sender;
- (IBAction) statusCheckboxClicked:(id)sender;

@end
