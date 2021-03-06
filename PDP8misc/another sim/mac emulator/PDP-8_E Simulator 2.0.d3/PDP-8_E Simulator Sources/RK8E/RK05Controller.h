/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK05Controller.h - Controller for RK05 view
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

#import "PluginFramework/FileDropControlTargetProtocol.h"


@class RK05;


@interface RK05Controller : NSObject <NSCoding, FileDropControlTarget> {
@private
	IBOutlet NSButton	*mountUnmountButton;
	IBOutlet NSButton	*writeProtectCheckbox;
	IBOutlet NSTextField	*filenameField;
	IBOutlet RK05		*rk05;
	NSString		*decpackPath;
}

- (IBAction) mountUnmountClicked:(id)sender;
- (IBAction) writeProtectClicked:(id)sender;

@end
