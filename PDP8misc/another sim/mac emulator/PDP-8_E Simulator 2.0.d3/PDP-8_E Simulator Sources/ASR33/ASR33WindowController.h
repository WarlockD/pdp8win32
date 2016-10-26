/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	ASR33WindowController.h - ASR 33 Teletype Window Controller
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


@class ASR33, PaperTapeController, ASR33TextView, RegisterFormCell, KeepInMenuWindow;


@interface ASR33WindowController : NSObject
{
@private
	IBOutlet ASR33			*asr33;
	IBOutlet KeepInMenuWindow	*window;
	IBOutlet NSView			*ttyToolbarView;
	IBOutlet NSView			*readerToolbarView;
	IBOutlet NSView			*punchToolbarView;
	IBOutlet NSSegmentedControl	*localOnline;
	IBOutlet RegisterFormCell	*kbb;
	IBOutlet RegisterFormCell	*tto;
	IBOutlet PaperTapeController	*readerController;
	IBOutlet PaperTapeController	*punchController;
}

- (IBAction) localOnlineClicked:(id)sender;
- (void) setWindowTitle:(NSString *)title;

@end
