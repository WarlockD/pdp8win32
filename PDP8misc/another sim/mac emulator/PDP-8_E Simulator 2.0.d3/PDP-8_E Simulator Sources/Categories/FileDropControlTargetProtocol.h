/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	FileDropControlTargetProtocol.h - Protocol to support file drop controls
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


/* When a NSControl is registered as a file drop destination (via [FileDrop registerAsFileDropTarget:]),
   the target of the control must implement the FileDragControlTarget protocol that is used to
   communicate between the drag actions and the control target. */
   

@protocol FileDropControlTarget

- (BOOL) willAcceptFile:(NSString *)path;	// check if file is allowed to be dropped
- (BOOL) acceptFile:(NSString *)path;		// actually perform the drop action, return success status

@end
