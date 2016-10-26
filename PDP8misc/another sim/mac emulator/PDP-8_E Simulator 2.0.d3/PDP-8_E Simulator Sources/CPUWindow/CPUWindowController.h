/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	CPUWindowController.h - Controller for the CPU window
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


@class RegisterFormCell, EnableDisableTextField, PDP8;


@interface CPUWindowController : NSObject
{
@private
	IBOutlet NSWindow		*window;
	IBOutlet RegisterFormCell	*sr;
	IBOutlet RegisterFormCell	*l;
	IBOutlet RegisterFormCell	*ac;
	IBOutlet RegisterFormCell	*pc;
	IBOutlet RegisterFormCell	*sc;
	IBOutlet RegisterFormCell	*gtf;
	IBOutlet RegisterFormCell	*mq;
	IBOutlet EnableDisableTextField	*mode;
	IBOutlet NSButton		*a;
	IBOutlet NSButton		*b;
	IBOutlet RegisterFormCell	*df;
	IBOutlet RegisterFormCell	*_if;
	IBOutlet RegisterFormCell	*ib;
	IBOutlet RegisterFormCell	*uf;
	IBOutlet RegisterFormCell	*ub;
	IBOutlet RegisterFormCell	*sf;
	IBOutlet NSButton		*enable;
	IBOutlet NSButton		*delay;
	IBOutlet NSButton		*inhibit;
	IBOutlet PDP8			*pdp8;
	float				normalContentHeight;
}

- (IBAction) eaeModeButtonClick:(id)sender;
- (IBAction) enableCheckboxClicked:(id)sender;
- (IBAction) delayCheckboxClicked:(id)sender;
- (IBAction) inhibitCheckboxClicked:(id)sender;

@end
