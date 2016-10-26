/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	NSFileManager+Additions.h - Additional functions for file management
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


#import "NSFileManager+Additions.h"


/*
	NSFileManager: Resolve an alias
	Original Source: <http://cocoa.karelia.com/Foundation_Categories/NSFileManager__Reso.m>
	(See copyright notice at <http://cocoa.karelia.com>)
*/


@implementation NSFileManager (Additions)


- (BOOL) fsRef:(FSRef *)fsRef forPath:(NSString *)path
{
	BOOL ok = NO;
	CFURLRef url = CFURLCreateWithFileSystemPath(NULL, (CFStringRef) path, kCFURLPOSIXPathStyle, NO);
	if (url) {
		ok = CFURLGetFSRef(url, fsRef);
		CFRelease (url);
	}
	return ok;
}


- (NSString *) pathResolved:(NSString *)path
{
	CFStringRef resolvedPath = nil;
	CFURLRef url = CFURLCreateWithFileSystemPath(NULL, (CFStringRef) path, kCFURLPOSIXPathStyle, NO);
	if (url != NULL) {
		FSRef fsRef;
		if (CFURLGetFSRef(url, &fsRef)) {
			Boolean targetIsFolder, wasAliased;
			if (FSResolveAliasFile(&fsRef, true, &targetIsFolder, &wasAliased) == noErr &&
				wasAliased) {
				CFURLRef resolvedurl = CFURLCreateFromFSRef(NULL, &fsRef);
				if (resolvedurl != NULL) {
					resolvedPath = CFURLCopyFileSystemPath(resolvedurl,
						kCFURLPOSIXPathStyle);
					CFRelease (resolvedurl);
				}
			}
		}
		CFRelease (url);
	}
	return [((NSString *) resolvedPath) autorelease];
}


- (BOOL) isAliasPath:(NSString *)path
{
	return [self pathResolved:path] != nil;
}


- (NSString *) resolveAliasPath:(NSString *)path
{
	NSString *resolved = [self pathResolved:path];
	return resolved ? resolved : path;
}


@end
