/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PaperTapeController.m - Controller for a paper tape reader and punch
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


#import "PluginFramework/NSControl+FileDrop.h"
#import "PluginFramework/Utilities.h"
#import "PluginFramework/Unicode.h"

#import "PaperTapeController.h"
#import "InputConsumerProtocol.h"
#import "PaperTapeProgressIndicator.h"


@implementation PaperTapeController


#define PAPER_TAPE_ON_TAG		0
#define PAPER_TAPE_OFF_TAG		1

#define PAPER_TAPE_READER		0
#define PAPER_TAPE_PUNCH		1

#define ANIMATION_DURATION		1.0	// seconds


- (void) setEnabled:(BOOL)flag
{
	[loadUnloadButton setEnabled:flag];
	[onOffButton setEnabled:flag && fileHandle != nil];
}


- (int) getChar
{
	NSAssertRunningOnMainThread ();		// for UI updates
	NSAssert (kind == PAPER_TAPE_READER, @"Cannot read from paper tape punch");
	int c = EOF;
	if (fileHandle) {
		if ([onOffButton selectedSegment] == PAPER_TAPE_ON_TAG) {
			NSData *data = [fileHandle readDataOfLength:1];
			if ([data length] == 1) {
				c = * (unsigned char *) [data bytes];
				[progressIndicator incrementBy:1];
				[progressIndicator animateForSeconds:ANIMATION_DURATION];
			} else
				[self loadUnloadClicked:self];
		}
	}
	return c;
}


- (void) putChar:(unsigned char)c handleBackspace:(BOOL)handleBackspace
{
	NSAssertRunningOnMainThread ();		// for UI updates
	NSAssert (kind == PAPER_TAPE_PUNCH, @"Cannot punch to paper tape reader");
	if (fileHandle && [onOffButton selectedSegment] == PAPER_TAPE_ON_TAG) {
		if (c == '\b' && handleBackspace) {
			long size = (long) [fileHandle offsetInFile];
			if (size > 0)
				[fileHandle truncateFileAtOffset:size - 1];
		} else
			[fileHandle writeData:[NSData dataWithBytes:&c length:1]];
		[progressIndicator animateForSeconds:ANIMATION_DURATION];
	}
}


- (void) mountFile:(NSFileHandle *)handle withPath:(NSString *)path
{
	fileHandle = handle;
	[fileHandle retain];
	[fileHandle seekToEndOfFile];
	unsigned long long offset = [fileHandle offsetInFile];
	[fileHandle seekToFileOffset:0];
	[progressIndicator setMaxValue:offset];
	[progressIndicator setDoubleValue:0];
	NSBundle *bundle = [NSBundle bundleForClass:[self class]];
	[loadUnloadButton setLabel:NSLocalizedStringFromTableInBundle(@"Unload", nil, bundle, @"")
		forSegment:0];
	[loadUnloadButton unregisterAsFileDropTarget];
	[onOffButton setEnabled:YES];
	[filenameField setStringValue:[[NSFileManager defaultManager] displayNameAtPath:path]];
	[filenameField setHidden:NO];
	[progressIndicator setHidden:NO];
}


- (void) panelDidEnd:(NSSavePanel *)panel return:(int)ret context:(void  *)context
{
	NSFileHandle *handle;
	
	if (ret == NSOKButton) {
		NSString *path = [panel filename];
		if (kind == PAPER_TAPE_READER)
			handle = [NSFileHandle fileHandleForReadingAtPath:path];
		else {
			[[NSFileManager defaultManager] createFileAtPath:path contents:nil attributes:nil];
			handle = [NSFileHandle fileHandleForWritingAtPath:path];
		}
		if (handle)
			[self mountFile:handle withPath:path];
		else {
			[panel close];
			NSBundle *bundle = [NSBundle bundleForClass:[self class]];
			NSAlert *alert = [[NSAlert alloc] init];
			[alert setMessageText:(kind == PAPER_TAPE_READER) ?
				NSLocalizedStringFromTableInBundle(
					@"Cannot open this paper tape file.", nil, bundle, @"") :
				NSLocalizedStringFromTableInBundle(
					@"Cannot create this paper tape file.", nil, bundle, @"")];
			[alert setInformativeText:path];
			[alert beginSheetModalForWindow:[loadUnloadButton window]
				modalDelegate:nil didEndSelector:nil contextInfo:nil];
			[alert release];
		}
	}
	[[NSUserDefaults standardUserDefaults] setObject:[panel directory] forKey:LAST_FILE_PANEL_DIR_KEY];
}


- (IBAction) loadUnloadClicked:(id)sender
{
	if (fileHandle) {
		[fileHandle closeFile];
		[fileHandle release];
		fileHandle = nil;
		NSBundle *bundle = [NSBundle bundleForClass:[self class]];
		[loadUnloadButton setLabel:NSLocalizedStringFromTableInBundle(@"Load", nil, bundle, @"")
			forSegment:0];
		[loadUnloadButton setEnabled:[onOffButton isEnabled]];	// maybe a sheet is visible
		[loadUnloadButton registerAsFileDropTarget];
		[onOffButton setEnabled:NO];
		[onOffButton selectSegmentWithTag:PAPER_TAPE_OFF_TAG];
		[filenameField setHidden:YES];
		[progressIndicator setHidden:YES];
	} else {
		NSSavePanel *panel = (kind == PAPER_TAPE_READER) ?
					[NSOpenPanel openPanel] : [NSSavePanel savePanel];
		[panel beginSheetForDirectory:
			[[NSUserDefaults standardUserDefaults] stringForKey:LAST_FILE_PANEL_DIR_KEY]
			file:nil modalForWindow:[loadUnloadButton window] modalDelegate:self
			didEndSelector:@selector(panelDidEnd:return:context:) contextInfo:nil];
	}
}


- (IBAction) onOffClicked:(id)sender
{
	if ([onOffButton selectedSegment] == PAPER_TAPE_ON_TAG) {
		[loadUnloadButton setEnabled:NO];
		if (kind == PAPER_TAPE_READER)
			[inputConsumer canContinueInput];
	} else {
		[loadUnloadButton setEnabled:YES];
		[progressIndicator stopAnimation:sender];
	}
}


- (void) awakeFromNib
{
	kind = [loadUnloadButton tag];
	ADJUST_TOOLBAR_CONTROL_FOR_TIGER (filenameField);
	ADJUST_TOOLBAR_CONTROL_FOR_TIGER (progressIndicator);
	[loadUnloadButton registerAsFileDropTarget];
}


#pragma mark FileDropControlTarget Protocol


- (BOOL) willAcceptFile:(NSString *)path
{
	NSFileHandle *handle = (kind == PAPER_TAPE_READER) ?
		[NSFileHandle fileHandleForReadingAtPath:path] :
		[NSFileHandle fileHandleForWritingAtPath:path];
	if (handle) {
		[handle closeFile];
		// [handle release];	// dont'd release, it crashs
	}
	return handle != nil;
}


- (BOOL) acceptFile:(NSString *)path
{
	NSFileHandle *handle = nil;
	if (kind == PAPER_TAPE_PUNCH) {
		NSBundle *bundle = [NSBundle bundleForClass:[self class]];
		NSAlert *alert = [[NSAlert alloc] init];
		[alert setMessageText:[NSString stringWithFormat:NSLocalizedStringFromTableInBundle(
			@"Do you really want to overwrite %C%@%C with the paper tape output?",
			nil, bundle, @""),
			UNICODE_LEFT_DOUBLEQUOTE,
			[[NSFileManager defaultManager] displayNameAtPath:path],
			UNICODE_RIGHT_DOUBLEQUOTE]];
		[alert setInformativeText:NSLocalizedStringFromTableInBundle(
			@"The current content of the file will be lost.", nil, bundle, @"")];
		[[alert addButtonWithTitle:NSLocalizedStringFromTableInBundle(
			@"Yes", nil, bundle, @"")] setKeyEquivalent:@"\r"];
		[[alert addButtonWithTitle:NSLocalizedStringFromTableInBundle(
			@"No", nil, bundle, @"")] setKeyEquivalent:@"\e"];
		[alert setAlertStyle:NSCriticalAlertStyle];
		if ([alert runModal] == NSAlertFirstButtonReturn &&
			(handle = [NSFileHandle fileHandleForWritingAtPath:path]))
			[handle truncateFileAtOffset:0];
		[alert release];
	} else
		handle = [NSFileHandle fileHandleForReadingAtPath:path];
	if (handle)
		[self mountFile:handle withPath:path];
	return handle != nil;
}


@end
