/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PreferencesController.m - Controller for the preferences panel
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


#import "PreferencesController.h"
#import "NSMutableArray+BinarySearch.h"


#define PREFS_PANEL_TOPLEFT_KEY		@"PrefsPanel TopLeft"


@implementation NSBundle (OrderInPreferencesPanelToolbar)


- (NSComparisonResult) compareOrderInPreferencesPanelToolbarInfoPlistKey:(NSBundle *)bundle
{
	return [[self objectForInfoDictionaryKey:@"OrderInPreferencesPanelToolbar"]
		compare:[bundle objectForInfoDictionaryKey:@"OrderInPreferencesPanelToolbar"]];
}


@end


@implementation PreferencesController


#pragma mark Toolbar


- (void) setupToolbar
{
	NSToolbar *toolbar = [[[NSToolbar alloc] initWithIdentifier:@"PrefsPanelToolbar"] autorelease];
	[toolbar setAllowsUserCustomization:NO];
	[toolbar setAutosavesConfiguration:YES];
	[toolbar setDelegate:self];
	[prefPanel setToolbar:toolbar];
	[prefPanel setShowsToolbarButton:YES];
	[toolbar setSelectedItemIdentifier:[prefPaneIdentifiers objectAtIndex:0]];
}


- (NSToolbarItem *) toolbar:(NSToolbar *)toolbar itemForItemIdentifier:(NSString *)identifier
	willBeInsertedIntoToolbar:(BOOL)willBeInserted
{
	NSToolbarItem *toolbarItem =
		[[[NSToolbarItem alloc] initWithItemIdentifier:identifier] autorelease];
	NSBundle *bundle = [NSBundle bundleWithIdentifier:identifier];
	[toolbarItem setLabel:[bundle objectForInfoDictionaryKey:@"NSPrefPaneIconLabel"]];
	[toolbarItem setImage:[[[NSImage alloc] initWithContentsOfFile:[bundle pathForResource:
		[bundle objectForInfoDictionaryKey:@"NSPrefPaneIconFile"] ofType:nil]] autorelease]];
	[toolbarItem setTarget:self];
	[toolbarItem setAction:@selector(selectPreferencePane:)];
	return toolbarItem;
}


- (NSArray *) toolbarDefaultItemIdentifiers:(NSToolbar *)toolbar
{
	return prefPaneIdentifiers;
}


- (NSArray *) toolbarAllowedItemIdentifiers:(NSToolbar *)toolbar
{
	return prefPaneIdentifiers;
}


- (NSArray *) toolbarSelectableItemIdentifiers:(NSToolbar *)toolbar
{
	return prefPaneIdentifiers;
}


#pragma mark Initialization


- (void) findPrefPanesAtPath:(NSString *)path addToArray:(NSMutableArray *)array
{
	NSString *bundleName;
	NSBundle *bundle;
		
	NSDirectoryEnumerator *bundlePathEnum = [[NSFileManager defaultManager] enumeratorAtPath:path];
	while ((bundleName = [bundlePathEnum nextObject])) {
		if ([[bundleName pathExtension] isEqualToString:PREF_PANE_EXTENSION]) {
			[bundlePathEnum skipDescendents];
			bundle = [NSBundle bundleWithPath:[path stringByAppendingPathComponent:bundleName]];
			if (bundle)
				[array addObject:bundle toArraySortedBy:
					@selector(compareOrderInPreferencesPanelToolbarInfoPlistKey:)
					replaceExistingObject:YES];
		}
	}
}


- (NSArray *) findPrefPanes
{
	NSEnumerator *enumerator;
	NSBundle *bundle;	
	
	NSMutableArray *prefPaneBundles = [NSMutableArray array];
	[self findPrefPanesAtPath:[[NSBundle mainBundle] sharedSupportPath] addToArray:prefPaneBundles];
	[self findPrefPanesAtPath:[[NSBundle mainBundle] builtInPlugInsPath] addToArray:prefPaneBundles];
	NSMutableArray *prefPaneIdents = [NSMutableArray array];
	enumerator = [prefPaneBundles objectEnumerator];
	while ((bundle = [enumerator nextObject]))
		[prefPaneIdents addObject:[bundle bundleIdentifier]];
	return prefPaneIdents;
}


#pragma mark Pane Handling


- (void) resizeWindowForContentView:(NSView *)view
{
	NSRect windowFrame =
		[NSPanel contentRectForFrameRect:[prefPanel frame] styleMask:[prefPanel styleMask]];
	float newWindowHeight = NSHeight([view frame]);
	if ([[prefPanel toolbar] isVisible])
		newWindowHeight += NSHeight(windowFrame) - NSHeight([[prefPanel contentView] frame]);
	NSRect newWindowFrame = [NSPanel frameRectForContentRect:
		NSMakeRect(NSMinX(windowFrame), NSMaxY(windowFrame) - newWindowHeight,
			NSWidth(windowFrame), newWindowHeight) styleMask:[prefPanel styleMask]];
	[prefPanel setFrame:newWindowFrame display:YES animate:[prefPanel isVisible]];
 }


- (void) selectPreferencePaneWithIdentifier:(NSString *)identifier
{
	if (currentPrefPane) {
		if ([currentPrefPane shouldUnselect] != NSUnselectNow) {
			NSLog (@"Handle pref pane unwilling to unselect");
			return;
		}
		[currentPrefPane willUnselect];
		[[currentPrefPane mainView] removeFromSuperview];
		[currentPrefPane didUnselect];
		[currentPrefPane release];
		currentPrefPane = nil;
	}
	NSBundle *prefBundle = [NSBundle bundleWithIdentifier:identifier];
	currentPrefPane = [[[prefBundle principalClass] alloc] initWithBundle:prefBundle];
	if ([currentPrefPane loadMainView]) {
		[currentPrefPane willSelect];
		[self resizeWindowForContentView:[currentPrefPane mainView]];
		[prefPanel setContentView:[currentPrefPane mainView]];
		[prefPanel setTitle:[prefBundle objectForInfoDictionaryKey:@"PrefPaneWindowTitle"]];
		[currentPrefPane didSelect];
		[prefPanel setInitialFirstResponder:[currentPrefPane initialKeyView]];
	} else
		NSLog (@"Pref pane loadMainView failed");
}


- (IBAction) selectPreferencePane:(id)sender
{
	[[prefPanel toolbar] setSelectedItemIdentifier:[sender itemIdentifier]];
	[self selectPreferencePaneWithIdentifier:[sender itemIdentifier]];
}


- (void) windowWillClose:(NSNotification *)notification
{
	[currentPrefPane willUnselect];
	[currentPrefPane didUnselect];
}


- (void) windowDidMove:(NSNotification *)notificatin
{
	/* because of the panel resizing, we can't use the auto frame save feature */
	NSRect frame = [prefPanel frame];
	NSPoint topLeft = frame.origin;		// bottom left
	topLeft.y += NSHeight(frame);		// top left
	[[NSUserDefaults standardUserDefaults] setObject:
		[NSString stringWithFormat:@"%f %f", topLeft.x, topLeft.y] forKey:PREFS_PANEL_TOPLEFT_KEY];
}


- (void) windowDidBecomeKey:(NSNotification *)notification
{
	if (prefPaneIdentifiers == nil)  {	// initialization
		prefPaneIdentifiers = [[self findPrefPanes] retain];
		[self setupToolbar];
	}
	if (currentPrefPane == nil) {
		[self selectPreferencePaneWithIdentifier:[prefPaneIdentifiers objectAtIndex:0]];
		NSString *str = [[NSUserDefaults standardUserDefaults] objectForKey:PREFS_PANEL_TOPLEFT_KEY];
		if (str) {	// restore old panel position
			NSScanner *scanner = [NSScanner scannerWithString:str];
			NSPoint topLeft;
			if ([scanner scanFloat:&topLeft.x] && [scanner scanFloat:&topLeft.y])
				[prefPanel setFrameTopLeftPoint:topLeft];
			else
				[prefPanel center];
		} else
			[prefPanel center];
	}
}


@end
