/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	KC8EA.h - KC8-EA Programmer’s Console for the PDP-8/E Simulator
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


@class KeepInMenuWindow, StateMachine;


@interface KC8EA : PDP8Plugin <NSCoding>
{
@private
	IBOutlet KeepInMenuWindow	*window;
	IBOutlet NSButton		*addr0;
	IBOutlet NSButton		*addr1;
	IBOutlet NSButton		*addr2;
	IBOutlet NSButton		*addr3;
	IBOutlet NSButton		*addr4;
	IBOutlet NSButton		*addr5;
	IBOutlet NSButton		*addr6;
	IBOutlet NSButton		*addr7;
	IBOutlet NSButton		*addr8;
	IBOutlet NSButton		*addr9;
	IBOutlet NSButton		*addr10;
	IBOutlet NSButton		*addr11;
	IBOutlet NSButton		*addr12;
	IBOutlet NSButton		*addr13;
	IBOutlet NSButton		*addr14;
	IBOutlet NSButton		*display0;
	IBOutlet NSButton		*display1;
	IBOutlet NSButton		*display2;
	IBOutlet NSButton		*display3;
	IBOutlet NSButton		*display4;
	IBOutlet NSButton		*display5;
	IBOutlet NSButton		*display6;
	IBOutlet NSButton		*display7;
	IBOutlet NSButton		*display8;
	IBOutlet NSButton		*display9;
	IBOutlet NSButton		*display10;
	IBOutlet NSButton		*display11;
	IBOutlet NSButton		*run;
	IBOutlet NSButton		*sr0;
	IBOutlet NSButton		*sr1;
	IBOutlet NSButton		*sr2;
	IBOutlet NSButton		*sr3;
	IBOutlet NSButton		*sr4;
	IBOutlet NSButton		*sr5;
	IBOutlet NSButton		*sr6;
	IBOutlet NSButton		*sr7;
	IBOutlet NSButton		*sr8;
	IBOutlet NSButton		*sr9;
	IBOutlet NSButton		*sr10;
	IBOutlet NSButton		*sr11;
	IBOutlet NSButton		*sw;
	IBOutlet NSButton		*addrload;
	IBOutlet NSButton		*extdaddrload;
	IBOutlet NSButton		*clear;
	IBOutlet NSButton		*cont;
	IBOutlet NSButton		*exam;
	IBOutlet NSButton		*halt;
	IBOutlet NSButton		*singstep;
	IBOutlet NSButton		*dep;
	IBOutlet NSButton		*knob;
	IBOutlet NSButton		*key;
	NSTimer				*goTimer;
	unsigned short			addressValue;
	unsigned short			displayValue;
	int				powerKeyPosition;
	StateMachine			*stateMachine;
}

- (IBAction) srClicked:(id)sender;
- (IBAction) swClicked:(id)sender;
- (IBAction) addrloadClicked:(id)sender;
- (IBAction) extdaddrloadClicked:(id)sender;
- (IBAction) clearClicked:(id)sender;
- (IBAction) contClicked:(id)sender;
- (IBAction) examClicked:(id)sender;
- (IBAction) haltClicked:(id)sender;
- (IBAction) stingstepClicked:(id)sender;
- (IBAction) depClicked:(id)sender;
- (IBAction) knobClicked:(id)sender;
- (IBAction) keyClicked:(id)sender;

@end
