/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PluginAPI.h - Plugin API Definitions for PDP-8/E I/O Device Plugins
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

#import "Utilities.h"		// for LOG_ASSERTING()


#define PLUGIN_API_VERSION_0		0
#define CURRENT_PLUGIN_API_VERSION	PLUGIN_API_VERSION_0	// see pluginVersion method

#define DEFAULT_IO_INFO_FILENAME	@"io-info"	// see iotInformationPlistName method
							// keys in this property list:
#define IO_INFO_IOFLAGS_KEY		@"ioflags"	// an array of I/O flag names in the plist
#define IO_INFO_IOADDRESSES_KEY		@"ioaddresses"	// an array of I/O addresses in the plist
#define IO_INFO_IOTS_KEY		@"iots"		// an array of IOT mnemonics in the plist

/* Every plugin must call this macro to initialize the (plugin local) global pdp8 variable used by the
   IOTs to access the PDP-8 registers directly. When only the PluginAPI class in the PluginFramework
   sets up the pdp8 variable, plugins get an other uninitialized pdp8 variable and crash. Don't know why,
   some kind of linker problem. */
#define SETUP_PDP8_POINTER_FOR_IOTS							\
	PDP8 *pdp8;									\
	static void setPDP8 (PDP8 *p8) { pdp8 = p8; }					\
	- (void) setPDP8:(PDP8 *)p8 { pdp8 = p8; setPDP8 (p8); LOG_ASSERTING(); }

/* Notification that is posted after all plugins are loaded. Plugins are loaded on the
   NSApplicationWillFinishLaunchingNotification, then on the NSApplicationDidFinishLaunchingNotification,
   the PLUGINS_LOADED_NOTIFICATION is posted. Use this notification e. g. to make plugin windows visible
   to avoid screen flicker. */
#define PLUGINS_LOADED_NOTIFICATION	@"PluginsLoadedNotification"


@class PDP8;


@interface PDP8Plugin : NSObject
{
@private
	NSBundle	*bundle;
@protected
	PDP8		*pdp8;
}

- (NSBundle *) bundle;			// returns the plugin bundle private variable
- (void) setBundle:(NSBundle *)bndl;	// sets the plugin bundle private variable
- (void) setPDP8:(PDP8 *)p8;		// gives the plugin a pointer to the global PDP-8 object
					// see SETUP_PDP8_POINTER_FOR_IOTS macro above
- (unsigned) pluginVersion;		// always returns CURRENT_PLUGIN_API_VERSION
- (NSString *) pluginName;		// returns the plugin name, i. e. file system name of the bundle
- (void *) pluginPointer;		// returns a pointer of the plugin instance that can be accessed
					// by the IOT functions via the PLUGIN_POINTER macro from pdp8.h
					// useful for multiinstance plugins to access the correct instance
- (NSString *) ioInformationPlistName;	// returns the name (in the localizable resources directory)
					// for the property list that contains the I/O flag and IOT info
- (NSDictionary *) ioInformation;	// returns a dictionary with information about I/O flags and IOTs
- (NSArray *) iotsForAddress:(int)ioAddress;
					// get 8 IOTs for an I/O address (nil for unused)
- (NSArray *) skiptestsForAddress:(int)ioAddress;
					// get 8 skiptests for an I/O address (nil for unused)
					// called after the iotsForAddress method
- (void) setIOFlag:(unsigned long)flag forIOFlagName:(NSString *)name;
					// called for each I/O flag name from the property list in that order
- (void) loadNibs;			// called after it is known that the I/O device fits into the PDP-8,
					// called after the methods above, so they must not access resources
					// from nibs
- (void) pluginDidLoad;			// called after the plugin has loaded and the PDP-8, the IOTs and
					// I/O flags have been setup and the nibs are loaded
- (void) CAF:(int)ioAddress;		// called when the CPU performs the CAF instruction; must not update
					// the GUI e. g. by sending notifications; is called from non-main
					// thread) (note: for devices without I/O adresses, it is not called)
- (void) clearAllFlags:(int)ioAddress;	// called when the user operates the Clear key of the KC8-EA console
					// (or the equivalent menu item); is called from main thread and must
					// update the GUI e. g. by sending notifications
					// (note: for devices without I/O addresses, it is not called, but
					// you can listen for the CLEAR_ALL_FLAGS_NOTIFICAITON)
- (void) resetDevice;			// called when the user resets the simulator (might be nearly
					// the same as clearAllFlags:)

@end
