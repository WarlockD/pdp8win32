/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	KC8EA.m - KC8-EA Programmer’s Console for the PDP-8/E Simulator
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
#import "PluginFramework/KeepInMenuWindow.h"

#import "KC8EA.h"
#import "StateMachine.h"
#import "BackgroundView.h"


// notifications
#define KC8EA_UPDATE_DISPLAY_NOTIFICATION	@"kc8eaUpdateDisplayNotification"
#define KC8EA_SW_CHANGED_NOTIFICATION		@"kc8eaSWChangedNotification"
	/* Notification posted when the SW switch changes; notification object is a NSNumber
	   with the new state of SW (0 or 1) */

// coder keys
#define CODER_KEY_POWER_KEY			@"power"
#define CODER_KEY_DISPLAY_SELECTOR_KNOB		@"knob"
#define CODER_KEY_SW				@"sw"
#define CODER_KEY_HALT				@"halt"
#define CODER_KEY_SINGSTEP			@"singstep"

// tag values of the display selection knob
#define DISPLAY_STATE	0
#define DISPLAY_STATUS	1
#define DISPLAY_AC	2
#define DISPLAY_MD	3
#define DISPLAY_MQ	4
#define DISPLAY_BUS	5

// tag values of the power key
#define KEY_OFF		0
#define KEY_POWER	1
#define KEY_PANEL_LOCK	2

// states of the console switches
#define UP		1
#define DOWN		0

// states of the lights
#define OFF		0
#define ON		1


@implementation KC8EA


SETUP_PDP8_POINTER_FOR_IOTS


#pragma mark GUI


- (void) updateDisplay
{
	unsigned short val = 0;
	if ([key tag] == KEY_POWER) {
		switch ([knob tag]) {
		case DISPLAY_STATE :
			val = [stateMachine state:! [sw state]];
			break;
		case DISPLAY_STATUS :
			val = [stateMachine status];
			break;
		case DISPLAY_AC :
			val = [pdp8 getAC];
			break;
		case DISPLAY_MD :
			val = [stateMachine md];
			break;
		case DISPLAY_MQ :
			val = [pdp8 getMQ];
			break;
		case DISPLAY_BUS :
			val = [stateMachine bus];
			break;
		default :
			NSAssert (FALSE, @"Invalid display selection knob tag");
			break;
		}
	}
	if (displayValue != val) {	// optimization
		[display0  setState:val & 00001];
		[display1  setState:val & 00002];
		[display2  setState:val & 00004];
		[display3  setState:val & 00010];
		[display4  setState:val & 00020];
		[display5  setState:val & 00040];
		[display6  setState:val & 00100];
		[display7  setState:val & 00200];
		[display8  setState:val & 00400];
		[display9  setState:val & 01000];
		[display10 setState:val & 02000];
		[display11 setState:val & 04000];
		displayValue = val;
	}
}


- (void) updateAddress
{
	unsigned short addr = 0;
	if ([key tag] == KEY_POWER)
		addr = [stateMachine cpma];
	if (addressValue != addr) {	// optimization
		[addr0  setState:addr & 000001];
		[addr1  setState:addr & 000002];
		[addr2  setState:addr & 000004];
		[addr3  setState:addr & 000010];
		[addr4  setState:addr & 000020];
		[addr5  setState:addr & 000040];
		[addr6  setState:addr & 000100];
		[addr7  setState:addr & 000200];
		[addr8  setState:addr & 000400];
		[addr9  setState:addr & 001000];
		[addr10 setState:addr & 002000];
		[addr11 setState:addr & 004000];
		[addr12 setState:addr & 010000];
		[addr13 setState:addr & 020000];
		[addr14 setState:addr & 040000];
		addressValue = addr;
	}
}


- (void) update
{
	[self updateDisplay];
	[self updateAddress];
}


- (IBAction) srClicked:(id)sender
{
	[pdp8 setSR:[pdp8 getSR] ^ (04000 >> [sender tag])];
}


- (IBAction) swClicked:(id)sender
{
	[self updateDisplay];
	/* The SW switch is not used by the simulator, but plugins that want to know about the
	   corresponding OMNIBUS signal can listen for this notification */
	[[NSNotificationCenter defaultCenter] postNotificationName:KC8EA_SW_CHANGED_NOTIFICATION
		object:[NSNumber numberWithInt:[sender state] == UP]];
}


- (IBAction) addrloadClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK) {
		[stateMachine loadAddress];
		[self update];
	}
}


- (IBAction) extdaddrloadClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK) {
		[stateMachine loadExtendedAddress];
		[self update];
	}
}


- (IBAction) clearClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK)
		[pdp8 clearAllFlags];
}


- (IBAction) contClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK && [pdp8 isStopped]) {
		[run setState:ON];
		[window display];
		if ([singstep state] == DOWN) {
			[stateMachine executeSingleCycle];
			[self update];
			[run setState:OFF];
		} else if ([halt state] == DOWN) {
			do {
				[stateMachine executeSingleCycle];
				[self update];
				[window display];
			} while (! [stateMachine isInFetchCycle]);
			[run setState:OFF];
		} else {
			while (! [stateMachine isInFetchCycle])
				[stateMachine executeSingleCycle];
			[[NSApp delegate] performSelector:@selector(go:) withObject:self];

		}
	}
}


- (IBAction) examClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK && [pdp8 isStopped]) {
		[run setState:ON];
		[window display];
		[stateMachine examine];
		[self update];
		[run setState:OFF];
	}
}


- (IBAction) haltClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK) {
		[pdp8 setHalt:[sender state] == DOWN || [singstep state] == DOWN];
		[NSApp setWindowsNeedUpdate:YES];	// cause Step/Trace/Go CPU window toolbar items update
	}
}


- (IBAction) stingstepClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK) {
		[pdp8 setHalt:[sender state] == DOWN || [halt state] == DOWN];
		[NSApp setWindowsNeedUpdate:YES];	// cause Step/Trace/Go CPU window toolbar items update
	}
}


- (IBAction) depClicked:(id)sender
{
	if (powerKeyPosition != KEY_PANEL_LOCK && [pdp8 isStopped]) {
		[run setState:ON];
		[window display];
		[stateMachine deposit];
		[self update];
		[run setState:OFF];
	}
}


- (IBAction) knobClicked:(id)sender
{
	[self updateDisplay];
}


- (IBAction) keyClicked:(id)sender
{
	NSEventType eventType = [[NSApp currentEvent] type];
	switch ([sender tag]) {
	case KEY_OFF :
		if (eventType == NSLeftMouseUp || eventType == NSKeyDown) {
			NSAlert *alert = [[NSAlert alloc] init];
			NSBundle *bundle = [NSBundle bundleForClass:[self class]];
			[alert setMessageText:NSLocalizedStringFromTableInBundle(
				@"Do you really want to power off the PDP-8/E Simulator?", nil, bundle, @"")];
			[alert addButtonWithTitle:
				NSLocalizedStringFromTableInBundle(@"No", nil, bundle, @"")];
			[alert addButtonWithTitle:
				NSLocalizedStringFromTableInBundle(@"Yes", nil, bundle, @"")];
			if ([alert runModal] ==  NSAlertFirstButtonReturn) {
				[[sender cell] setTag:eventType == NSKeyDown ? KEY_POWER : powerKeyPosition];
				[self update];
			} else
				[NSApp terminate:self];
			[alert release];
		}
		break;
	case KEY_POWER :
		powerKeyPosition = KEY_POWER;
		[self update];
		[pdp8 setHalt:[halt state] == DOWN || [singstep state] == DOWN];
		[NSApp setWindowsNeedUpdate:YES];	// cause Step/Trace/Go CPU window toolbar items update
		break;
	case KEY_PANEL_LOCK :
		powerKeyPosition = KEY_PANEL_LOCK;
		[self update];
		[pdp8 setHalt:NO];
		[NSApp setWindowsNeedUpdate:YES];	// cause Step/Trace/Go CPU window toolbar items update
		break;
	default :
		NSAssert (FALSE, @"Invalid power key tag");
		break;
	}
}


#pragma mark Notifications


- (void) goTimerFireMethod:(NSTimer *)timer
{
	[stateMachine updateState:NO];
	[self update];
}


- (void) notifyGo:(NSNotification *)notification
{
	// hardcoded refresh rate at 60 Hz
	goTimer = [NSTimer scheduledTimerWithTimeInterval:1/60.0 target:self
		selector:@selector(goTimerFireMethod:) userInfo:nil repeats:YES];
	[run setState:ON];
}


- (void) notifyStep:(NSNotification *)notification
{
	[stateMachine updateState:NO];
	[self update];
}


- (void) notifyTrace:(NSNotification *)notification
{
	[run setState:YES];
}


- (void) notifyStop:(NSNotification *)notification
{
	[goTimer invalidate];
	goTimer = nil;
	[run setState:OFF];
	[stateMachine updateState:YES];
	[self update];
}


- (void) notifyPDP8Changed:(NSNotification *)notification
{
	// NSLog (@"KC8EA notifyPDP8Changed");
	[[NSNotificationQueue defaultQueue] enqueueNotification:
		[NSNotification notificationWithName:KC8EA_UPDATE_DISPLAY_NOTIFICATION object:self]
		postingStyle:NSPostASAP coalesceMask:NSNotificationCoalescingOnName forModes:nil];
}


- (void) notifyUpdateDisplay:(NSNotification *) notification
{
	// NSLog (@"KC8EA notifyUpdateDisplay");
	[stateMachine updateState:NO];
	[self update];
}


- (void) notifySRChanged:(NSNotification *)notification
{
	unsigned short sreg = [pdp8 getSR];
	[sr0  setState:sreg & 04000 ? 1 : 0];
	[sr1  setState:sreg & 02000 ? 1 : 0];
	[sr2  setState:sreg & 01000 ? 1 : 0];
	[sr3  setState:sreg & 00400 ? 1 : 0];
	[sr4  setState:sreg & 00200 ? 1 : 0];
	[sr5  setState:sreg & 00100 ? 1 : 0];
	[sr6  setState:sreg & 00040 ? 1 : 0];
	[sr7  setState:sreg & 00020 ? 1 : 0];
	[sr8  setState:sreg & 00010 ? 1 : 0];
	[sr9  setState:sreg & 00004 ? 1 : 0];
	[sr10 setState:sreg & 00002 ? 1 : 0];
	[sr11 setState:sreg & 00001 ? 1 : 0];
}


- (void) notifyClearAllFlags:(NSNotification *)notification
{
	[clear highlight:UP];
	[NSTimer scheduledTimerWithTimeInterval:0.1 target:self
		selector:@selector(unhighliteClearTimerFireMethod:) userInfo:nil repeats:NO];
}


- (void) unhighliteClearTimerFireMethod:(NSTimer *)timer
{
	[clear highlight:DOWN];
}


#pragma mark Initialization


- (void) resetDevice
{
	[[key cell] setTag:KEY_POWER];
	[[knob cell] setTag:DISPLAY_AC];
	[sw setState:UP];
	[halt setState:UP];
	[singstep setState:UP];
	[stateMachine updateState:NO];
	[self update];
}


- (id) init
{
	self = [super init];
	powerKeyPosition = KEY_POWER;
	[[key cell] setTag:powerKeyPosition];
	[[knob cell] setTag:DISPLAY_AC];
	[sw setState:UP];
	addressValue = displayValue = -1;
	/* dummy assignments to suppress Analyzer warning "never used" for the GUI elements not
	   accessed by source code; for the sake of completeness, they are connected in Interface Builder */
	addrload = nil;
	extdaddrload = nil;
	cont = nil;
	dep = nil;
	exam = nil;
	return self;	
}


- (id) initWithCoder:(NSCoder *)coder
{
	self = [super init];
	powerKeyPosition = [coder decodeIntForKey:CODER_KEY_POWER_KEY];
	[[key cell] setTag:powerKeyPosition];
	[[knob cell] setTag:[coder decodeIntForKey:CODER_KEY_DISPLAY_SELECTOR_KNOB]];
	[sw setState:[coder decodeBoolForKey:CODER_KEY_SW]];
	[halt setState:[coder decodeBoolForKey:CODER_KEY_HALT]];
	[singstep setState:DOWN];	// to cause the HALT cell update shaddow to be called
	[singstep setState:[coder decodeBoolForKey:CODER_KEY_SINGSTEP]];
	[pdp8 setHalt:[key tag] == KEY_PANEL_LOCK ? NO : ([halt state] == DOWN || [singstep state] == DOWN)];
	addressValue = displayValue = -1;
	return self;
}


- (void) encodeWithCoder:(NSCoder *)coder
{
	[coder encodeInt:powerKeyPosition forKey:CODER_KEY_POWER_KEY];
	[coder encodeInt:[knob tag] forKey:CODER_KEY_DISPLAY_SELECTOR_KNOB];
	[coder encodeBool:[sw state] forKey:CODER_KEY_SW];
	[coder encodeBool:[halt state] forKey:CODER_KEY_HALT];
	[coder encodeBool:[singstep state] forKey:CODER_KEY_SINGSTEP];
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	// NSLog (@"KC8EA notifyApplicationWillTerminate");
	NSMutableData *data = [NSMutableData data];
	NSKeyedArchiver *archiver = [[NSKeyedArchiver alloc] initForWritingWithMutableData:data];
	[self encodeWithCoder:archiver];
	[stateMachine encodeWithCoder:archiver];
	[archiver finishEncoding];
	[archiver release];
	[[NSUserDefaults standardUserDefaults] setObject:data forKey:[self pluginName]];
}


- (void) notifyPluginsLoaded:(NSNotification *)notification
{
	[self notifySRChanged:nil];
	[self swClicked:sw];
	[self notifyUpdateDisplay:nil];
	[window orderBackFromDefaults:self];
}


- (void) pluginDidLoad
{
	NSData *data = [[NSUserDefaults standardUserDefaults] dataForKey:[self pluginName]];
	if (data) {
		NSKeyedUnarchiver *unarchiver = [[NSKeyedUnarchiver alloc] initForReadingWithData:data];
		self = [self initWithCoder:unarchiver];
		stateMachine = [[StateMachine alloc] initWithCoder:unarchiver pdp8:pdp8];
		[unarchiver finishDecoding];
		[unarchiver release];
	} else {
		self = [self init];
		stateMachine = [[StateMachine alloc] initWithPDP8:pdp8];
	}
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter addObserver:self selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyPluginsLoaded:)
		name:PLUGINS_LOADED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyGo:) name:PDP8_GO_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyStop:) name:PDP8_STOP_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyStep:) name:PDP8_STEP_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyTrace:) name:PDP8_TRACE_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifySRChanged:) name:SR_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:MEMORY_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:PROGRAM_COUNTER_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:ACCUMULATOR_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:SC_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:GTF_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:MQ_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:EAE_MODE_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:DF_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:UF_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:UB_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:SF_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:ENABLE_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:DELAY_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:INHIBIT_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPDP8Changed:) name:IOFLAGS_CHANGED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyUpdateDisplay:) name:KC8EA_UPDATE_DISPLAY_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyClearAllFlags:) name:CLEAR_ALL_FLAGS_NOTIFICATION object:nil];
	[(BackgroundView *) [window contentView] setBackgroundImage:@"background" ofType:@"png"];
}


@end
