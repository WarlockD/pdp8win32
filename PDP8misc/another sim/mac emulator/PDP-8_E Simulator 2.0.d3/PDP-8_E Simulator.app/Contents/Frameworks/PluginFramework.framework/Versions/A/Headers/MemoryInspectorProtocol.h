/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	MemoryInspectorProtocol.h - Protocol for Memory Inspectors
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


/* A Memory Inspector is a subclass of NSFormatter that conforms to the MemoryInspector protocol.
   To add a new inspector, simply include a new class in a plugin; it will automatically be detected.
   The NSFormatter methods work on a NSArray of wordsPerRow 12-bit PDP-8 words. The order of the
   inspector in the popup menu of the memory inspector drawer is determined by orderInMemoryInspectorMenu;
   the built-in inspectors use numbers 10, 20,... */
   

@protocol MemoryInspector

+ (void) load;		// empty dummy method to cause ZeroLink to load the class in the debug builds 

- (NSNumber *) orderInMemoryInspectorMenu;
- (NSString *) menuTitle;
- (unsigned) wordsPerRow;
- (unsigned) contentWidthInCharacters;
- (BOOL) needsMemoryAlignment;
- (NSString *) toolTipForContentColumn;

@end
