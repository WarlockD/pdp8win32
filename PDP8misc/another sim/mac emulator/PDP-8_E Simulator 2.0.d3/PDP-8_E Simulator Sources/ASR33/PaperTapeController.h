/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PaperTapeController.h - Controller for a paper tape reader and punch
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


@protocol InputConsumer;
@class PaperTapeProgressIndicator;


@interface PaperTapeController : NSObject <FileDropControlTarget> {
@private
	IBOutlet NSSegmentedControl		*loadUnloadButton;
	IBOutlet NSSegmentedControl		*onOffButton;
	IBOutlet NSTextField			*filenameField;
	IBOutlet PaperTapeProgressIndicator	*progressIndicator;
	IBOutlet id <InputConsumer>		inputConsumer;
	NSFileHandle				*fileHandle;
	int					kind;	/* PAPER_TAPE_READER or PAPER_TAPE_PUNCH */
}

- (void) setEnabled:(BOOL)flag;
- (IBAction) loadUnloadClicked:(id)sender;
- (IBAction) onOffClicked:(id)sender;
- (int) getChar;							 // must be called on main thread
- (void) putChar:(unsigned char)c handleBackspace:(BOOL)handleBackspace; // must be called on main thread

@end
