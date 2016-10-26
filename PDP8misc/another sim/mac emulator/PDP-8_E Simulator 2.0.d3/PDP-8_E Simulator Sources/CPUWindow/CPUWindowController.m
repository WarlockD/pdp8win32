/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	CPUWindowController.m - Controller for the CPU window
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


#import "CPUWindowController.h"
#import "MainController.h"
#import "PDP8.h"
#import "RegisterFormCell.h"
#import "GeneralPreferences.h"
#import "Unicode.h"
#import "Utilities.h"


@implementation CPUWindowController


#define STOP_TOOLBAR_ITEM_IDENTIFIER		@"stopTBItemIdentifier"
#define GO_TOOLBAR_ITEM_IDENTIFIER		@"goTBItemIdentifier"
#define TRACE_TOOLBAR_ITEM_IDENTIFIER		@"traceTBItemIdentifier"
#define STEP_TOOLBAR_ITEM_IDENTIFIER		@"stepTBItemIdentifier"
#define BREAKPOINT_TOOLBAR_ITEM_IDENTIFIER	@"breakpointTBItemIdentifier"
#define BOOTSTRAP_TOOLBAR_ITEM_IDENTIFIER	@"bootstrapTBItemIdentifier"
#define RESET_TOOLBAR_ITEM_IDENTIFIER		@"resetTBItemIdentifier"
#define MEMORYINSPECTOR_TOOLBAR_ITEM_IDENTIFIER	@"memoryInspectorTBItemIdentifier"

#define RESIZE_WINDOW_ON_PDP8STOP_NOTIFICATION	@"cpuwindowResizeOnPDP8StopNotification"


#pragma mark Toolbar


- (void) setupToolbar
{
	NSToolbar *toolbar = [[[NSToolbar alloc] initWithIdentifier:@"CPUWindowToolbar"] autorelease];
	[toolbar setAllowsUserCustomization:YES];
	[toolbar setAutosavesConfiguration:YES];
	[toolbar setDisplayMode: NSToolbarDisplayModeIconOnly];
	[toolbar setSizeMode:NSToolbarSizeModeSmall];
	[toolbar setDelegate:self];
	[window setToolbar:toolbar];
	[window setShowsToolbarButton:YES];
}


- (NSToolbarItem *) toolbar:(NSToolbar *)toolbar itemForItemIdentifier:(NSString *)itemIdent
	willBeInsertedIntoToolbar:(BOOL) willBeInserted
{
	NSToolbarItem *toolbarItem = [[[NSToolbarItem alloc]
		initWithItemIdentifier:itemIdent] autorelease];

	if ([itemIdent isEqual:STOP_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Stop", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Stop", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Stops the running PDP-8/E", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"stopToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(stop:)];
	} else if ([itemIdent isEqual:GO_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Go", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Go", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Starts the simulated PDP-8/E to run continuously", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"goToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(go:)];
	} else if ([itemIdent isEqual:TRACE_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Trace", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Trace", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Starts the simulated PDP-8/E to run in trace mode", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"traceToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(trace:)];
	} else if ([itemIdent isEqual:STEP_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Step", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Step", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Executes a single PDP-8/E instruction", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"stepToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(step:)];
	} else if ([itemIdent isEqual:BREAKPOINT_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Breakpoints", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Breakpoints", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Shows or hides the Breakpoints & Break Opcodes Panel", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"breakpointsToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(showHideBreakpointPanel:)];
	} else if ([itemIdent isEqual:BOOTSTRAP_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Bootstrap Loader", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Bootstrap Loader", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Shows or hides the Bootstrap Loader Panel", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"bootstrapToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(showHideBootstrapPanel:)];
	} else if ([itemIdent isEqual:RESET_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Reset", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Reset", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Resets registers and memory of the PDP-8/E", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"resetToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(reset:)];
	} else if ([itemIdent isEqual:MEMORYINSPECTOR_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedString(@"Memory Inspector", @"")];
		[toolbarItem setPaletteLabel:NSLocalizedString(@"Memory Inspector", @"")];
		[toolbarItem setToolTip:NSLocalizedString(
			@"Shows or hides the memory inspector drawer", @"")];
		[toolbarItem setImage:[NSImage imageNamed:@"memoryInspectorToolbarIcon"]];
		[toolbarItem setTarget:[NSApp delegate]];
		[toolbarItem setAction:@selector(toggleMemoryInspectorDrawer:)];
	} else 
		toolbarItem = nil;
	return toolbarItem;
}


- (NSArray *) toolbarDefaultItemIdentifiers:(NSToolbar *)toolbar
{
	return [NSArray arrayWithObjects:
		NSToolbarFlexibleSpaceItemIdentifier,
		RESET_TOOLBAR_ITEM_IDENTIFIER,
		NSToolbarSpaceItemIdentifier,
		BREAKPOINT_TOOLBAR_ITEM_IDENTIFIER, BOOTSTRAP_TOOLBAR_ITEM_IDENTIFIER,
		NSToolbarSpaceItemIdentifier,
		STOP_TOOLBAR_ITEM_IDENTIFIER, STEP_TOOLBAR_ITEM_IDENTIFIER,
		TRACE_TOOLBAR_ITEM_IDENTIFIER, GO_TOOLBAR_ITEM_IDENTIFIER,
		NSToolbarSpaceItemIdentifier,
		MEMORYINSPECTOR_TOOLBAR_ITEM_IDENTIFIER,
		nil];
}


- (NSArray *) toolbarAllowedItemIdentifiers:(NSToolbar *)toolbar
{
	return [NSArray arrayWithObjects:
		STOP_TOOLBAR_ITEM_IDENTIFIER, STEP_TOOLBAR_ITEM_IDENTIFIER,
		TRACE_TOOLBAR_ITEM_IDENTIFIER, GO_TOOLBAR_ITEM_IDENTIFIER,
		BREAKPOINT_TOOLBAR_ITEM_IDENTIFIER, BOOTSTRAP_TOOLBAR_ITEM_IDENTIFIER,
		MEMORYINSPECTOR_TOOLBAR_ITEM_IDENTIFIER, RESET_TOOLBAR_ITEM_IDENTIFIER,
		NSToolbarCustomizeToolbarItemIdentifier, NSToolbarSeparatorItemIdentifier,
		NSToolbarFlexibleSpaceItemIdentifier, NSToolbarSpaceItemIdentifier, 
		nil];
}


#pragma mark Notifications


- (void) setupNotifications
{
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];

	[defaultCenter addObserver:self
		selector:@selector(notifyResizeWindowOnPDP8Stop:)
		name:RESIZE_WINDOW_ON_PDP8STOP_NOTIFICATION object:nil];
	[defaultCenter addObserver:self
		selector:@selector(notifyStopPDP8:) name:PDP8_STOP_NOTIFICATION object:nil];
	[defaultCenter addObserver:self
		selector:@selector(notifyTracePDP8:) name:PDP8_TRACE_NOTIFICATION object:nil];
	[defaultCenter addObserver:self
		selector:@selector(notifyGoPDP8:) name:PDP8_GO_NOTIFICATION object:nil];
		
	[defaultCenter addObserver:self selector:@selector(notifyEnableChanged:)
		name:ENABLE_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyDelayChanged:)
		name:DELAY_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyInhibitChanged:)
		name:INHIBIT_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyEAEModeChanged:)
		name:EAE_MODE_CHANGED_NOTIFICATION object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyEAEMount:)
		name:EAE_MOUNT_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyKM8EMount:)
		name:KM8E_MOUNT_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyPreferencesChanged:)
		name:NSUserDefaultsDidChangeNotification object:nil];
		
	[defaultCenter addObserver:self selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil]; 
}


- (void) notifyResizeWindowOnPDP8Stop:(NSNotification *)notification
{
	// NSLog (@"CPUWindowController got a notifyResizeWindowOnPDP8Stop notification");
	NSRect rect = [window frame];
	rect.size.height += normalContentHeight;
	rect.origin.y -= normalContentHeight;
	normalContentHeight = 0;
	[window setFrame:rect display:YES animate:YES];
	[window setTitle:NSLocalizedString(@"PDP-8/E CPU", @"")];
	[[window contentView] setAutoresizesSubviews:YES];
	[[window toolbar] validateVisibleItems];
}


- (void) notifyStopPDP8:(NSNotification *)notification
{
	// NSLog (@"CPUWindowController got a STOP PDP8 notification");
	// delay the resizing until all GUI elements in the CPU window and other windows are updated
	[[NSNotificationQueue defaultQueue] enqueueNotification:
		[NSNotification notificationWithName:RESIZE_WINDOW_ON_PDP8STOP_NOTIFICATION object:self]
		postingStyle:NSPostWhenIdle];
}


- (void) setGoWindowTitle
{
	NSString *title;

	switch ([[NSUserDefaults standardUserDefaults] integerForKey:GENERAL_PREFS_GO_SPEED_KEY]) {
	case GO_AS_FAST_AS_POSSIBLE :
		title = NSLocalizedString(
			@"PDP-8/E CPU " /* UNICODE_EM_DASH_UTF8 */ "%C" " running as fast as possible", @"");
		break;
	case GO_WITH_PDP8_SPEED :
		title = NSLocalizedString(
			@"PDP-8/E CPU " /* UNICODE_EM_DASH_UTF8 */ "%C" " running with real speed", @"");
		break;
	case GO_WITH_PDP8_SPEED_PRECISE	:
		title = NSLocalizedString(
			@"PDP-8/E CPU " /* UNICODE_EM_DASH_UTF8 */ "%C" " running with precise timing", @"");
		break;
	default :
		title = @"";
		NSAssert (FALSE, @"Illegal go speed in the preferences");
		break;
	}
	title = [NSString stringWithFormat:title, UNICODE_EM_DASH];
	[window setTitle:title];
}


- (void) notifyPreferencesChanged:(NSNotification *)notification
{
	if ([pdp8 isGoing])
		[self setGoWindowTitle];
}


- (void) notifyGoPDP8:(NSNotification *)notification
{
	// NSLog (@"CPUWindowController got a GO PDP8 notification");
	NSRect rect = [window frame];
	normalContentHeight = [[window contentView] frame].size.height;
	rect.origin.y += normalContentHeight;
	rect.size.height -= normalContentHeight;
	[[window contentView] setAutoresizesSubviews:NO];
	[window setFrame:rect display:YES animate:YES];
	[self setGoWindowTitle];
	[[window toolbar] validateVisibleItems];
}


- (void) notifyTracePDP8:(NSNotification *)notification
{
	// NSLog (@"CPUWindowController got a TRACE PDP8 notification");
	[[window toolbar] validateVisibleItems];
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	// NSLog (@"CPUWindowController notifyApplicationWillTerminate");
	/* When the simulator quits while the PDP-8 is running, the CPU window is shrunk, and this size
	   is stored in the user defaults. On the next launch, it resizes to the default height at the top
	   of the screen and not to the last position/size. To avoid that, resize it before quitting. */
	if (normalContentHeight) {
		[window orderOut:self];
		NSRect rect = [window frame];
		rect.size.height += normalContentHeight;
		rect.origin.y -= normalContentHeight;
		normalContentHeight = 0;
		[window setFrame:rect display:NO];
		[window saveFrameUsingName:[window frameAutosaveName]];
	}
}


#pragma mark Delegate Methods


- (NSSize) windowWillResize:(NSWindow *)sender toSize:(NSSize)newSize
{
	return normalContentHeight ? [window frame].size : newSize;
}


- (BOOL) windowShouldZoom:(NSWindow *)sender toFrame:(NSRect)newFrame
{
	return normalContentHeight ? NO : YES;
}


#pragma mark Registers


- (void) notifyEAEMount:(NSNotification *)notification
{
	BOOL hasEAE = [pdp8 hasEAE];
	[sc setEnabled:hasEAE];
	[gtf setEnabled:hasEAE];
	[mode setEnabled:hasEAE];
	[a setEnabled:hasEAE];
	[b setEnabled:hasEAE];
}


- (IBAction) eaeModeButtonClick:(id)sender
{
	[pdp8 setEAEmode:EAE_MODE_A + [sender tag]];
}


- (void) notifyEAEModeChanged:(NSNotification *)notification
{
	[a setIntValue:EAE_MODE_B - [pdp8 getEAEmode]];
	[b setIntValue:[pdp8 getEAEmode] - EAE_MODE_A];
}


- (void) notifyKM8EMount:(NSNotification *)notification
{
	BOOL hasKM8E = [pdp8 hasKM8E];
	[df setEnabled:hasKM8E];
	[_if setEnabled:hasKM8E];
	[ib setEnabled:hasKM8E];
	[uf setEnabled:hasKM8E];
	[ub setEnabled:hasKM8E];
	[sf setEnabled:hasKM8E];
	[inhibit setEnabled:hasKM8E];
}


- (IBAction) enableCheckboxClicked:(id)sender
{
	[pdp8 setEnable:[sender intValue]];
}


- (void) notifyEnableChanged:(NSNotification *)notification
{
	[enable setIntValue:[pdp8 getEnable]];
}


- (IBAction) delayCheckboxClicked:(id)sender;
{
	[pdp8 setDelay:[sender intValue]];
}


- (void) notifyDelayChanged:(NSNotification *)notification
{
	[delay setIntValue:[pdp8 getDelay]];
}


- (IBAction) inhibitCheckboxClicked:(id)sender;
{
	[pdp8 setInhibit:[sender intValue]];
}


- (void) notifyInhibitChanged:(NSNotification *)notification
{
	[inhibit setIntValue:[pdp8 getInhibit]];
}


#pragma mark Initialization


- (void) setupRegisters
{
	// KK8-E CPU
	[sr setupRegisterFor:pdp8 getRegisterValue:@selector(getSR) setRegisterValue:@selector(setSR:)
		changedNotificationName:SR_CHANGED_NOTIFICATION mask:07777 base:8];
	[l setupRegisterFor:pdp8 getRegisterValue:@selector(getL) setRegisterValue:@selector(setL:)
		changedNotificationName:ACCUMULATOR_CHANGED_NOTIFICATION mask:01 base:8];
	[ac setupRegisterFor:pdp8 getRegisterValue:@selector(getAC) setRegisterValue:@selector(setAC:)
		changedNotificationName:ACCUMULATOR_CHANGED_NOTIFICATION mask:07777 base:8];
	[pc setupRegisterFor:pdp8 getRegisterValue:@selector(getPC) setRegisterValue:@selector(setPC:)
		changedNotificationName:PROGRAM_COUNTER_CHANGED_NOTIFICATION mask:07777 base:8];
	// KE8-E EAE
	[sc setupRegisterFor:pdp8 getRegisterValue:@selector(getSC) setRegisterValue:@selector(setSC:)
		changedNotificationName:SC_CHANGED_NOTIFICATION mask:037 base:8];
	[gtf setupRegisterFor:pdp8 getRegisterValue:@selector(getGTF) setRegisterValue:@selector(setGTF:)
		changedNotificationName:GTF_CHANGED_NOTIFICATION mask:01 base:8];
	[mq setupRegisterFor:pdp8 getRegisterValue:@selector(getMQ) setRegisterValue:@selector(setMQ:)
		changedNotificationName:MQ_CHANGED_NOTIFICATION mask:07777 base:8];
	// KM8-E Memory Extension
	[df setupRegisterFor:pdp8 getRegisterValue:@selector(getDF) setRegisterValue:@selector(setDF:)
		changedNotificationName:DF_CHANGED_NOTIFICATION mask:07 base:8];
	[_if setupRegisterFor:pdp8 getRegisterValue:@selector(getIF) setRegisterValue:@selector(setIF:)
		changedNotificationName:PROGRAM_COUNTER_CHANGED_NOTIFICATION mask:07 base:8];
	[ib setupRegisterFor:pdp8 getRegisterValue:@selector(getIB) setRegisterValue:@selector(setIB:)
		changedNotificationName:PROGRAM_COUNTER_CHANGED_NOTIFICATION mask:07 base:8];
	[uf setupRegisterFor:pdp8 getRegisterValue:@selector(getUF) setRegisterValue:@selector(setUF:)
		changedNotificationName:UF_CHANGED_NOTIFICATION mask:01 base:8];
	[ub setupRegisterFor:pdp8 getRegisterValue:@selector(getUB) setRegisterValue:@selector(setUB:)
		changedNotificationName:UB_CHANGED_NOTIFICATION mask:01 base:8];
	[sf setupRegisterFor:pdp8 getRegisterValue:@selector(getSF) setRegisterValue:@selector(setSF:)
		changedNotificationName:SF_CHANGED_NOTIFICATION mask:0177 base:8];
}


- (void) awakeFromNib
{
	[self setupToolbar];
	[self setupNotifications];
	[self setupRegisters];
	/* set max width = min width of the panel: IB only allows max width one pixel more
	   than min width, so the user can resize the width for one pixel - bug in IB? */
	NSSize size = [window minSize];
	size.height = [window maxSize].height;
	[window setMaxSize:size];
}


@end
