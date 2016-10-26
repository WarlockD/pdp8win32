/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	NSControl+FileDrop.m - Category to support file drop for controls
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


#import "NSControl+FileDrop.h"
#import "FileDropControlTargetProtocol.h"
#import "NSFileManager+Additions.h"


@implementation NSControl (FileDrop)


- (void) registerAsFileDropTarget
{
	[self registerForDraggedTypes:[NSArray arrayWithObject:NSFilenamesPboardType]];
}


- (void) unregisterAsFileDropTarget
{
	[self unregisterDraggedTypes];
}


- (NSDragOperation) draggingEntered:(id <NSDraggingInfo>)sender
{
	NSAssert ([[self target] conformsToProtocol:@protocol(FileDropControlTarget)],
		@"Control target does not conform to the DragDropControlTarget protocol");
	if (! [self isEnabled])
		return NSDragOperationNone;
	if (([sender draggingSourceOperationMask] & NSDragOperationGeneric) != NSDragOperationGeneric)
		return NSDragOperationNone;
	NSArray *paths = [[sender draggingPasteboard] propertyListForType:NSFilenamesPboardType];
	if ([paths count] != 1)		// HIG: don't accept a drag with more than one allowed item
		return NSDragOperationNone;
	if (! [[self target] willAcceptFile:[paths objectAtIndex:0]])
		return NSDragOperationNone;
	return NSDragOperationCopy;
}


- (BOOL) performDragOperation:(id <NSDraggingInfo>)sender
{
	return [[self target] acceptFile:[[NSFileManager defaultManager] resolveAliasPath:
		[[[sender draggingPasteboard] propertyListForType:NSFilenamesPboardType] objectAtIndex:0]]];
}


@end
