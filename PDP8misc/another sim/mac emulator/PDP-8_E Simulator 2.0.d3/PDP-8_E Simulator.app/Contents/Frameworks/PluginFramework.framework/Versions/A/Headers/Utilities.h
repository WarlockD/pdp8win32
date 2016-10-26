/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Utilities.h - Some general utilities and macros, esp. for Tiger compatibility
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
#include <mach/mach_time.h>


// Constants from NSApplication.h that are not in the 10.4 SDK
#define NSAppKitVersionNumber10_4	824
#define NSAppKitVersionNumber10_5	949
#define NSAppKitVersionNumber10_6	1038
#define NSAppKitVersionNumber10_7	1138
#define NSAppKitVersionNumber10_8	1187
#define NSAppKitVersionNumber10_9	1265


#define runningOnTiger()		(floor(NSAppKitVersionNumber) == NSAppKitVersionNumber10_4)
#define runningOnTigerOrNewer()		(NSAppKitVersionNumber >= NSAppKitVersionNumber10_4)
#define runningOnLeopard()		(floor(NSAppKitVersionNumber) == NSAppKitVersionNumber10_5)
#define runningOnLeopardOrNewer()	(NSAppKitVersionNumber >= NSAppKitVersionNumber10_5)
#define runningOnSnowLeopard()		(floor(NSAppKitVersionNumber) == NSAppKitVersionNumber10_6)
#define runningOnSnowLeopardOrNewer()	(NSAppKitVersionNumber >= NSAppKitVersionNumber10_6)
#define runningOnLion()			(floor(NSAppKitVersionNumber) == NSAppKitVersionNumber10_7)
#define runningOnLionOrNewer()		(NSAppKitVersionNumber >= NSAppKitVersionNumber10_7)
#define runningOnMountainLion()		(floor(NSAppKitVersionNumber) == NSAppKitVersionNumber10_8)
#define runningOnMountainLionOrNewer()	(NSAppKitVersionNumber >= NSAppKitVersionNumber10_8)
#define runningOnMavericks()		(floor(NSAppKitVersionNumber) == NSAppKitVersionNumber10_9)
#define runningOnMavericksOrNewer()	(NSAppKitVersionNumber >= NSAppKitVersionNumber10_9)


#define ADJUST_TOOLBAR_CONTROL_FOR_TIGER(view)			\
	{							\
		if (runningOnTiger()) {				\
			NSPoint p = [(view) frame].origin;	\
			p.x += (float) 2.0;			\
			[(view) setFrameOrigin:p];		\
		}						\
	}


// Assertion for running on main thread works only with Leopard
// isMainThread is a 10.5 method not declared in the 10.4 SDK, so performSelector to avoid warning
#define NSAssertRunningOnMainThread()									\
	NSAssert (runningOnTiger() ? TRUE : (int) [NSThread performSelector:@selector(isMainThread)],	\
		@"Called from non-main thread")


// Macro to print a log message when the code is compiled with asserts
#if defined(NS_BLOCK_ASSERTIONS)
#define LOG_ASSERTING()
#else
#define LOG_ASSERTING()		NSLog (@"%@ - Compiled with assertions.", [self class])
#endif


// NSCondition is available since 10.0, but missing in the headers until 10.5, see NSLock.h
@interface NSCondition : NSObject <NSLocking>
{
@private
	void *_priv;
}

- (void) wait;
- (BOOL) waitUntilDate:(NSDate *)limit;
- (void) signal;
- (void) broadcast;

@end


// [[NSUserDefaults standardUserDefaults] stringForKey:LAST_FILE_PANEL_DIR_KEY]
// returns the default start directory for file dialogs
#define LAST_FILE_PANEL_DIR_KEY		@"LastPanelDir"


// Maximum macro
#define max(a, b) ((a) < (b) ? (b) : (a))


// see Technical Q&A QA1398
uint64_t nanoseconds2absolute (uint64_t nanoseconds);
uint64_t absolute2nanoseconds (uint64_t absolute);

