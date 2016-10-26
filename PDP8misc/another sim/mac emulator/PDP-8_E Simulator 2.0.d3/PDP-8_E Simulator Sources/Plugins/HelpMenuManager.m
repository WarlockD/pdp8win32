/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	HelpMenuManager.m - Manager for Help menu items of plugins
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


#import "Utilities.h"
#import "HelpMenuManager.h"
#import "NSFileManager+Additions.h"


@implementation HelpMenuManager


+ (HelpMenuManager *) sharedHelpMenuManager
{
	static HelpMenuManager *sharedHelpMenuManager;

	if (! sharedHelpMenuManager)
		sharedHelpMenuManager = [[self alloc] init];
	return sharedHelpMenuManager;
}


- (HelpMenuManager *) init
{
	self = [super init];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil];
	registeredHelpBookDomains = [[NSMutableArray alloc] initWithCapacity:10];
	return self;
}


- (void) unregisterHelpBookForDomain:(NSString *)domain
// see http://lists.apple.com/archives/carbon-development/2003/Nov/msg00090.html
{
	NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
	NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithDictionary:
		[defaults persistentDomainForName:@"com.apple.help"]];
	[dict removeObjectForKey:domain];
	[defaults setPersistentDomain:dict forName:@"com.apple.help"];
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	NSString *domain;
	
	NSEnumerator *enumerator = [registeredHelpBookDomains objectEnumerator];
	while ((domain = [enumerator nextObject]))
		[self unregisterHelpBookForDomain:domain];
}


- (NSString *) helpUrlForId:(NSString *)id
// extract the URL of the help page from ~/Library/Preferences/com.apple.help.plist (Lion and better)
// for Lion and Mountain Lion, the ID is the title of the help book
// for Mavericks, the ID is the bundle identifier of the plugin containing the help book
{
	NSArray *array;
	
	NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [defaults persistentDomainForName:@"com.apple.help"];
	
	dict = [dict valueForKey:@"RegisteredBooks"];
	NSEnumerator *enumerator = [dict objectEnumerator];
	while ((array = [enumerator nextObject])) {
		dict = [array objectAtIndex:0];
		if ([id isEqualToString:[dict objectForKey:@"id"]])
			return [NSString stringWithFormat:@"%@index.html", [dict objectForKey:@"url"]];
	}
	return nil;
}


- (void) openHelpUrl:(NSString *)url
// open the HelpViewer.app via AppleScript with the "handle URL" command (Lion only)
{
	NSString *source = [NSString stringWithFormat:
		@"tell app \"HelpViewer\"\nactivate\nhandle URL \"%@\"\nend tell", url];
	NSAppleScript *script = [[[NSAppleScript alloc] initWithSource:source] autorelease];
	NSDictionary *error = nil;
	[script executeAndReturnError:&error];
	if (error)
		NSLog (@"AppleScript error while opening HelpViewer: %@", error);
}


- (void) showHelp:(id)sender
{
	NSString *title = [sender title];

	if (runningOnLionOrNewer())
		// Help book lookup does not work as before, don't know why.
		// We now use AppleScript to open HelpViewer with the URL of the help page.
		[self openHelpUrl:[self helpUrlForId:(runningOnMavericksOrNewer() ?
			[sender representedObject] : title)]];
	else
		AHGotoPage (title, NULL, NULL);
}


- (void) addHelpMenuItem:(NSString *)title id:(NSString *)id
{
	NSMenu *helpMenu = [[[NSApp mainMenu] itemWithTitle:NSLocalizedString(@"Help", @"")] submenu];
	int n = [helpMenu numberOfItems];
	int i;
	for (i = 1; i < n; i++) {
		switch ([[[helpMenu itemAtIndex:i] title] compare:title]) {
		case NSOrderedSame :
			n = -1;
			break;
		case NSOrderedDescending :
			n = i;
			break;
		default :
			break;
		}
	}
	if (n > 0) {
		NSMenuItem *item = [helpMenu insertItemWithTitle:title action:@selector(showHelp:)
			keyEquivalent:@"" atIndex:n];
		[item setTarget:self];
		[item setRepresentedObject:id];
	}
}


- (void) addBundleHelp:(NSBundle *)bundle
{
	FSRef fsRef;
	
	if ([[NSFileManager defaultManager] fsRef:&fsRef forPath:[bundle bundlePath]]) {
		[self unregisterHelpBookForDomain:[bundle bundleIdentifier]];
		if (AHRegisterHelpBook(&fsRef) == noErr) {
			[registeredHelpBookDomains addObject:[bundle bundleIdentifier]];
			NSString *bookTitle = [[bundle infoDictionary] objectForKey:@"CFBundleHelpBookName"];
			if (bookTitle)
				[self addHelpMenuItem:bookTitle id:[bundle bundleIdentifier]];
		}
	}
}


@end
