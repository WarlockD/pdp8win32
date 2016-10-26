/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PluginAPI.m - Plugin API Definitions for PDP-8/E I/O Device Plugins
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
 

#import "PluginAPI.h"


@implementation PDP8Plugin


SETUP_PDP8_POINTER_FOR_IOTS


- (NSBundle *) bundle
{
	return bundle;
}


- (void) setBundle:(NSBundle *)bndl
{
	bundle = bndl;
}


- (unsigned) pluginVersion
{
	return CURRENT_PLUGIN_API_VERSION;
}


- (NSString *) pluginName
{
	return [[bundle bundlePath] lastPathComponent];
}


- (void *) pluginPointer
{
	return (void *) self;
}


- (NSString *) ioInformationPlistName
{
	return DEFAULT_IO_INFO_FILENAME;
}


- (NSDictionary *) ioInformation
{
	return [NSDictionary dictionaryWithContentsOfFile:
		[bundle pathForResource:[self ioInformationPlistName] ofType:@"plist"]];
}


- (NSArray *) iotsForAddress:(int)ioAddress
{
	return nil;
}


- (NSArray *) skiptestsForAddress:(int)ioAddress
{
	return nil;
}


- (void) setIOFlag:(unsigned long)flag forIOFlagName:(NSString *)name;
{
}


- (void) loadNibs
{
	NSString *resourceName;
	
	NSString *resourcePath = [[NSBundle bundleForClass:[self class]] resourcePath];
	NSDirectoryEnumerator *resourcePathEnum =
		[[NSFileManager defaultManager] enumeratorAtPath:resourcePath];
	while (resourcePathEnum && (resourceName = [resourcePathEnum nextObject])) {
		if ([[resourceName pathExtension] isEqualToString:@"nib"])
			[NSBundle loadNibNamed:[resourceName lastPathComponent] owner:self];
	}
}


- (void) pluginDidLoad
{
}


- (void) CAF:(int)ioAddress
{
}


- (void) clearAllFlags:(int)ioAddress
{
}


- (void) resetDevice
{
}


@end
