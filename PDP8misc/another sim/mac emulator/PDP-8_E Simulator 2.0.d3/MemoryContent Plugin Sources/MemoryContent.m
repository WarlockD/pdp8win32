/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryContent.m - Memory Content Viewer for the PDP-8/E Simulator
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

#import "MemoryContent.h"
#import "MemoryContentView.h"


#define MEMORY_CONTENT_UPDATE_NOTIFICATION		@"MemoryContentUpdateNotification"


@implementation MemoryContent


#pragma mark Memory Content


- (void) update
{
	[[window contentView] update];
	[window flushWindow];
}


#pragma mark Notifications


- (void) notifyMemoryChanged:(NSNotification *)notification
{
	/* coalesc the multiple MEMORY_CHANGED_NOTIFICATIONS (caused by the different register update
	   notifications) to one UPDATE_MEMORY_NOTIFICATION to avoid time consuming repeated memory
	   content updates */
	// NSLog (@"MemoryContent notifyMemoryChanged");
	[[NSNotificationQueue defaultQueue] enqueueNotification:
		[NSNotification notificationWithName:MEMORY_CONTENT_UPDATE_NOTIFICATION object:self]
		postingStyle:NSPostASAP coalesceMask:NSNotificationCoalescingOnName forModes:nil];
}


- (void) notifyUpdateMemoryContent:(NSNotification *) notification
{
	[self update];
}


- (void) goTimerFireMethod:(NSTimer *)timer
{
	[self update];
}


- (void) notifyGo:(NSNotification *) notification
{
	// hardcoded refresh rate at 60 Hz
	goTimer = [NSTimer scheduledTimerWithTimeInterval:1/60.0 target:self
		selector:@selector(goTimerFireMethod:) userInfo:nil repeats:YES];
}


- (void) notifyStop:(NSNotification *) notification
{
	[goTimer invalidate];
	goTimer = nil;
}


#pragma mark Initialization


- (id) initWithCoder:(NSCoder *)coder
{
}


- (void) encodeWithCoder:(NSCoder *)coder
{
}


- (void) notifyPluginsLoaded:(NSNotification *)notification
{
	[[window contentView] setMemory:[pdp8 directMemoryAccess]];
	[window orderBackFromDefaults:self];
}


- (void) pluginDidLoad
{
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter addObserver:self selector:@selector(notifyPluginsLoaded:)
		name:PLUGINS_LOADED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyMemoryChanged:)
		name:MEMORY_CHANGED_NOTIFICATION object:nil];
		
	[defaultCenter addObserver:self selector:@selector(notifyUpdateMemoryContent:)
		name:MEMORY_CONTENT_UPDATE_NOTIFICATION object:nil];

	[defaultCenter addObserver:self selector:@selector(notifyGo:)
		name:PDP8_GO_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifyStop:)
		name:PDP8_STOP_NOTIFICATION object:nil];
}


@end
