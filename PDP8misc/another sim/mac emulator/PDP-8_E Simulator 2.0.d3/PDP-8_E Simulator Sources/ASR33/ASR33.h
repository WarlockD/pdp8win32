/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	ASR33.h - ASR 33 Teletype for the PDP-8/E Simulator
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

#import "InputConsumerProtocol.h"


#define KBB_CHANGED_NOTIFICATION	@"asr33KeyboardBufferChangedNotification"
#define TTO_CHANGED_NOTIFICATION	@"asr33TeletypeOutputChangedNotification"
#define TTY_ONLINE_CHANGED_NOTIFICATION	@"asr33TTYOnlineChangedNotification"


@class NSCondition, PaperTapeController, ASR33TextView, ASR33WindowController, TypeaheadBuffer;


@interface ASR33 : PDP8Plugin <NSCoding, InputConsumer>
{
@public
/* The attributes are public, so the C functions implementing the PDP-8 instructions can
   access them directly. No other Cocoa code should use them directly. To ensure this,
   the register names are mapped to dummy names with the #defines below. In the source
   codes files with the instruction C functions, #define USE_ASR33_REGISTERS_DIRECTLY
   to make the registers available. */
	unsigned short				KBB;
	unsigned long				inflag;
	unsigned long				outflag;
	short int				input;		// int: can be EOF == -1
	unsigned short				output;
@private
	unsigned short				TTO;		// private: not accessed by IOTs
	unsigned short				inAddress;
	unsigned short				outAddress;
	unsigned short				punchMask;
	IBOutlet PaperTapeController		*reader;
	IBOutlet PaperTapeController		*punch;
	IBOutlet ASR33TextView			*textview;
	IBOutlet ASR33WindowController		*windowController;
	IBOutlet TypeaheadBuffer		*typeaheadBuffer;
	BOOL					online;
	NSConditionLock				*inputLock;
	NSConditionLock				*outputLock;
	NSCondition				*outputOnline;
	BOOL					runWithRealtimeSpeed;
	BOOL					playSound;
	float					soundVolume;
	BOOL					isConsoleTTY;
}

- (unsigned short) getKBB;
- (void) setKBB:(unsigned short)kbb;
- (unsigned short) getTTO;
- (void) setTTO:(unsigned short)tto;
- (BOOL) getOnline;
- (void) setOnline:(BOOL)onlineOffline;
- (void) canContinueOutput;

@end


#if ! USE_ASR33_REGISTERS_DIRECTLY
#define KBB		__dont_use_KBB__
#define inflag		__dont_use_inflag__
#define outflag		__dont_use_outflag__
#define input		__dont_use_input__
#define output		__dont_use_output__
#endif
