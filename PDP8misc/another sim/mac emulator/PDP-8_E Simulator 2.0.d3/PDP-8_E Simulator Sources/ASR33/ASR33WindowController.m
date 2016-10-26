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


#import "PluginFramework/RegisterFormCell.h"
#import "PluginFramework/KeepInMenuWindow.h"

#import "ASR33WindowController.h"
#import "ASR33.h"


@implementation ASR33WindowController


#define TTY_TOOLBAR_ITEM_IDENTIFIER		@"ttyTBItemIdentifier"
#define READER_TOOLBAR_ITEM_IDENTIFIER		@"readerTBItemIdentifier"
#define PUNCH_TOOLBAR_ITEM_IDENTIFIER		@"punchTBItemIdentifier"


#pragma mark Toolbar


- (void) setupToolbar
{
	NSToolbar *toolbar = [[[NSToolbar alloc] initWithIdentifier:@"ASR33WindowToolbar"] autorelease];
	[toolbar setAllowsUserCustomization:NO];
	[toolbar setAutosavesConfiguration:YES];
	[toolbar setDisplayMode:NSToolbarDisplayModeIconAndLabel];
	[toolbar setSizeMode:NSToolbarSizeModeDefault];
	[toolbar setDelegate:self];
	[window setToolbar:toolbar];
	[window setShowsToolbarButton:YES];
	ADJUST_TOOLBAR_CONTROL_FOR_TIGER ([kbb controlView]);
	ADJUST_TOOLBAR_CONTROL_FOR_TIGER ([tto controlView]);
}


- (NSToolbarItem *) toolbar:(NSToolbar *)toolbar itemForItemIdentifier:(NSString *)itemIdent
	willBeInsertedIntoToolbar:(BOOL) willBeInserted
{
	NSRect rect;

	NSBundle *bundle = [NSBundle bundleForClass:[self class]];
	NSToolbarItem *toolbarItem = [[[NSToolbarItem alloc] initWithItemIdentifier:itemIdent] autorelease];

	if ([itemIdent isEqual:TTY_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedStringFromTableInBundle(
			@"ASR 33 Teletype", nil, bundle, @"")];
		[toolbarItem setPaletteLabel:NSLocalizedStringFromTableInBundle(
			@"ASR 33 Teletype", nil, bundle, @"")];
		[toolbarItem setToolTip:NSLocalizedStringFromTableInBundle(
			@"This area contains the registers and switches of the ASR 33 teletype",
			nil, bundle, @"")];
		[toolbarItem setView:ttyToolbarView];
		rect = [ttyToolbarView frame];
		[toolbarItem setMinSize:NSMakeSize(NSWidth(rect), NSHeight(rect))];
		[toolbarItem setMaxSize:NSMakeSize(NSWidth(rect), NSHeight(rect))];
	} else if ([itemIdent isEqual:READER_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedStringFromTableInBundle(
			@"Low Speed Paper Tape Reader", nil, bundle, @"")];
		[toolbarItem setPaletteLabel:NSLocalizedStringFromTableInBundle(
			@"Low Speed Paper Tape Reader", nil, bundle, @"")];
		[toolbarItem setToolTip:NSLocalizedStringFromTableInBundle(
			@"This area displays the state of the low speed paper tape reader",
			nil, bundle, @"")];
		[toolbarItem setView:readerToolbarView];
		rect = [readerToolbarView frame];
		[toolbarItem setMinSize:NSMakeSize(NSWidth(rect), NSHeight(rect))];
		[toolbarItem setMaxSize:NSMakeSize(NSWidth(rect), NSHeight(rect))];
	} else if ([itemIdent isEqual:PUNCH_TOOLBAR_ITEM_IDENTIFIER]) {
		[toolbarItem setLabel:NSLocalizedStringFromTableInBundle(
			@"Low Speed Paper Tape Punch", nil, bundle, @"")];
		[toolbarItem setPaletteLabel:NSLocalizedStringFromTableInBundle(
			@"Low Speed Paper Tape Punch", nil, bundle, @"")];
		[toolbarItem setToolTip:NSLocalizedStringFromTableInBundle(
			@"This area displays the state of the low speed paper tape punch",
			nil, bundle, @"")];
		[toolbarItem setView:punchToolbarView];
		rect = [punchToolbarView frame];
		[toolbarItem setMinSize:NSMakeSize(NSWidth(rect), NSHeight(rect))];
		[toolbarItem setMaxSize:NSMakeSize(NSWidth(rect), NSHeight(rect))];
	} else 
		toolbarItem = nil;
	return toolbarItem;
}


- (NSArray *) toolbarDefaultItemIdentifiers:(NSToolbar *)toolbar
{
	return [NSArray arrayWithObjects: 
		TTY_TOOLBAR_ITEM_IDENTIFIER, NSToolbarSeparatorItemIdentifier,
		READER_TOOLBAR_ITEM_IDENTIFIER, NSToolbarSeparatorItemIdentifier,
		PUNCH_TOOLBAR_ITEM_IDENTIFIER,
		nil];
}


- (NSArray *) toolbarAllowedItemIdentifiers:(NSToolbar *)toolbar
{
	return [NSArray arrayWithObjects:
		TTY_TOOLBAR_ITEM_IDENTIFIER, READER_TOOLBAR_ITEM_IDENTIFIER,
		PUNCH_TOOLBAR_ITEM_IDENTIFIER, NSToolbarSeparatorItemIdentifier, 
		nil];
}


#pragma mark Notifications


- (void) windowWillBeginSheet:(NSNotification *)notification
{
	[localOnline setEnabled:NO];
	[kbb setEnabled:NO];
	[tto setEnabled:NO];
	[readerController setEnabled:NO];
	[punchController setEnabled:NO];
}


- (void) windowDidEndSheet:(NSNotification *)notification;
{
	[localOnline setEnabled:YES];
	[kbb setEnabled:YES];
	[tto setEnabled:YES];
	[readerController setEnabled:YES];
	[punchController setEnabled:YES];
}


- (void) notifyPluginsLoaded:(NSNotification *)notification
{
	[window orderBackFromDefaults:self];
}


- (void) setupNotifications
{
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter addObserver:self
		selector:@selector(notifyOnlineChanged:) name:TTY_ONLINE_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyPluginsLoaded:)
		name:PLUGINS_LOADED_NOTIFICATION object:nil];
}


#pragma mark Controls


- (IBAction) localOnlineClicked:(id)sender
{
	[asr33 setOnline:[sender selectedSegment]];
}


- (void) notifyOnlineChanged:(NSNotification *)notification
{
	[localOnline setSelectedSegment:[asr33 getOnline]];
}


#pragma mark Initialization


- (void) setWindowTitle:(NSString *)title
{
	BOOL isAuxTTY = ! [[window title] isEqualToString:title];
	NSRect oldFrame = [window frame];
	[window setTitle:title];
	[window setFrameAutosaveName:title];
	/* this is a hack to move the AuxTTY window away from the exact position of the ConTTY when the
	   simulator starts for the first time without existing preferences file */
	if (isAuxTTY && NSEqualRects(oldFrame, [window frame]))
		[window setFrameOrigin:NSMakePoint(oldFrame.origin.x + 20, oldFrame.origin.y - 20)];
}


- (void) setupRegisters
{
	[kbb setupRegisterFor:asr33 getRegisterValue:@selector(getKBB) setRegisterValue:@selector(setKBB:)
		changedNotificationName:KBB_CHANGED_NOTIFICATION mask:0377 base:8];
	[tto setupRegisterFor:asr33 getRegisterValue:@selector(getTTO) setRegisterValue:@selector(setTTO:)
		changedNotificationName:TTO_CHANGED_NOTIFICATION mask:0377 base:8];
}


- (void) awakeFromNib
{
	[self setupToolbar];
	[self setupNotifications];
	[self setupRegisters];
}


@end
