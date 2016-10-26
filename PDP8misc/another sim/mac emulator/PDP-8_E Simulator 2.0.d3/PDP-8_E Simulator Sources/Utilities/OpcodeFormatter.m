/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	OpcodeFormatter.m - Formatter for the Opcode column of the CPU window
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


#import "OpcodeFormatter.h"
#import "Opcode.h"
#import "Assembler.h"
#import "Disassembler.h"


@implementation OpcodeFormatter


+ (OpcodeFormatter *) formatterWithPDP8:(PDP8 *)p8 addressGetter:(id <OpcodeFormatterAddressGetter>)addrGetter
{
	return [[[OpcodeFormatter alloc] initWithPDP8:p8 addressGetter:addrGetter] autorelease];
}


- (OpcodeFormatter *) initWithPDP8:(PDP8 *)p8 addressGetter:(id <OpcodeFormatterAddressGetter>)addrGetter;
{
	pdp8 = p8;
	addressGetter = addrGetter;
	return self;
}


- (NSString *) stringForObjectValue:(Opcode *)opcode
{
	// with Leopard, the formatter is called once for a Text Cell - don't know why
	if (! [opcode isKindOfClass:[Opcode class]])
		return nil;
	return  [[Disassembler sharedDisassembler] disassembleOpcodeForPDP8:pdp8
		atAddress:[opcode address] showOperandsAtPC:TRUE];
}


- (NSString *) editingStringForObjectValue:(Opcode *)opcode
{
	return [[Disassembler sharedDisassembler] disassembleOpcodeForPDP8:pdp8
		atAddress:[opcode address] showOperandsAtPC:FALSE];
}


- (BOOL) getObjectValue:(Opcode **)opcode forString:(NSString *)string errorDescription:(NSString **)error
{
	*opcode = [[Assembler sharedAssembler]
		assemble:string atAddress:[addressGetter getCurrentAddress] error:error];
	return *opcode != nil;
}


@end
