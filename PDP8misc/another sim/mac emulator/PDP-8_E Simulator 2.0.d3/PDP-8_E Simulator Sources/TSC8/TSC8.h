/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	TSC8.h - TSC8-75 Board for the PDP-8/E Simulator
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

#import "PluginFramework/PluginAPI.h"


@class KeepInMenuWindow, RegisterFormCell;


@interface TSC8 : PDP8Plugin <NSCoding>
{
@private
	IBOutlet KeepInMenuWindow	*window;
	IBOutlet RegisterFormCell	*ertbRegister;
	IBOutlet RegisterFormCell	*eriotRegister;
	IBOutlet RegisterFormCell	*ecdfRegister;
	IBOutlet NSButton		*esmeButton;
@public
	// no registers here, the TSC8-75 registers are in pdp8->_tsc8
}

- (IBAction) esmeEnabledClicked:(id)sender;

@end
