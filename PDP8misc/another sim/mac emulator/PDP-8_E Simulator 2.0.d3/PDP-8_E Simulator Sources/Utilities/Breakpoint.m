/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Breakpoint.m - Class for a single breakpoint
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


#import "Breakpoint.h"


@implementation Breakpoint


+ (Breakpoint *) breakpointWithIdentifier:(unsigned)ident value:(unsigned)val
{
	return [[[self alloc] initWithIdentifier:ident value:val] autorelease];
}


- (id) initWithCoder:(NSCoder *)coder
{
	self = [super init];
	identifier = [coder decodeIntForKey:@"id"];
	value = [coder decodeIntForKey:@"val"];
	return self;
}


- (void) encodeWithCoder:(NSCoder *)coder
{
	[coder encodeInt:identifier forKey:@"id"];
	[coder encodeInt:value forKey:@"val"];
}


- (Breakpoint *) initWithIdentifier:(unsigned)ident value:(unsigned)val
{
	self = [super init];
	identifier = ident;
	value = val;
	return self;
}


- (unsigned) identifier
{
	return identifier;
}


- (unsigned) value
{
	return value;
}


- (void) setValue:(unsigned)val
{
	value = val;
}


- (NSComparisonResult) compareAddress:(Breakpoint *)breakpoint
{
	if (identifier < [breakpoint identifier])
		return NSOrderedAscending;
	if (identifier > [breakpoint identifier])
		return NSOrderedDescending;
	return NSOrderedSame;
}


@end
