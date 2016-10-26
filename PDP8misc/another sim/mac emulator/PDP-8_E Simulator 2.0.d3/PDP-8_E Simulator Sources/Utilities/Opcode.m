/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Opcode.m - Class representing a PDP-8 opcode
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


#import "Opcode.h"


@implementation Opcode


+ (Opcode *) opcodeWithAddress:(int)addr
{
	return [[[self alloc] initWithAddress:addr] autorelease];
}


+ (Opcode *) opcodeWithAddress:(int)addr value:(int)value
{
	return [[[self alloc] initWithAddress:addr value:value] autorelease];
}


- (id) copyWithZone:(NSZone *)zone
{
	Opcode *new = [[Opcode alloc] initWithAddress:address];
	
	new->word0 = word0;
	new->word1 = word1;
	return new;
}


- (Opcode *) initWithAddress:(int)addr
{
	self = [super init];
	address = addr;
	word0 = word1 = -1;
	return self;
}


- (Opcode *) initWithAddress:(int)addr value:(int)value
{
	self = [super init];
	address = addr;
	word0 = value;
	word1 = -1;
	return self;
}


- (int) address
{
	return address;
}


- (int) word0
{
	return word0;
}


- (void) setWord0:(int)w0
{
	word0 = w0;
}


- (int) word1
{
	return word1;
}


- (void) setWord1:(int)w1
{
	word1 = w1;
}


@end
