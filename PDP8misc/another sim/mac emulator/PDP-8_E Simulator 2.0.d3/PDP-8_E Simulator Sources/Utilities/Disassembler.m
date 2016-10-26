/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Disassembler.m - Disassembler for PDP-8/E instructions
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


#import "Disassembler.h"
#import "Unicode.h"
#import "PDP8.h"


@implementation Disassembler


+ (Disassembler *) sharedDisassembler
{
	static Disassembler *sharedDisassembler;

	if (! sharedDisassembler)
		sharedDisassembler = [[self alloc] init];
	return sharedDisassembler;
}


- (id) init
{
	NSDictionary *dict = [NSDictionary dictionaryWithContentsOfFile:
		[[NSBundle mainBundle] pathForResource:@"disassembler" ofType:@"plist"]];
		
	self = [super init];
	mriOpcodes = [[dict objectForKey:@"MRI"] retain];
	iotOpcodes = [[dict objectForKey:@"IOT"] retain];
	oprGroup1Opcodes = [[dict objectForKey:@"OPR Group 1"] retain];
	oprGroup2Opcodes = [[dict objectForKey:@"OPR Group 2"] retain];
	eaeModeAOpcodes = [[dict objectForKey:@"EAE Mode A"] retain];
	eaeModeBOpcodes = [[dict objectForKey:@"EAE Mode B"] retain];
	return self;
}


- (NSString *) disassembleOpcodeForPDP8:(PDP8 *)pdp8
	atAddress:(int)addr showOperandsAtPC:(BOOL)showOpAtPC
{
	short inst, word1, word2, ad, d, e, pc, field;
	char str[128], *p;
	
	NSCAssert1 ((addr & ~077777) == 0, @"Bad address %o", addr);
	if (addr >= [pdp8 memorySize])
		return NSLocalizedString(@"n/a", @"");
	p = str;
	pc = [pdp8 getProgramCounter];
	inst = [pdp8 memoryAt:addr];
	word1 = [pdp8 memoryAtNext:addr];	
	switch (inst & 07000) {
	case 00000 :	/* AND */
	case 01000 :	/* TAD */
	case 02000 :	/* ISZ */
	case 03000 :	/* DCA */
	case 04000 :	/* JMS */
	case 05000 :	/* JMP */
		[[mriOpcodes objectAtIndex:(inst >> 9)]
			getCString:p maxLength:(sizeof(str) - 1) encoding:NSASCIIStringEncoding];
		p += strlen(p);
		p += sprintf(p, " %s", (inst & 0400) ? "I " : "");
		ad = ((inst & 0200) ? (addr & 07600) : 0) | (inst & 0177);
		d = ad - (addr & 07777);
		if (inst >= 04000 && ! (inst & 0400)) {
			if (d == 0)
				p += sprintf(p, ".");
			else if (-5 < d && d < 5)
				p += sprintf(p, ".%+d" /* %d (not %o) to get sign */, d);
			else
				p += sprintf(p, "%o", ad);
		 } else
    			p += sprintf(p, "%o", ad);
    		if (addr == pc && showOpAtPC) {
			field = ((inst >= 04000 && ! (inst & 0400) && [pdp8 getIB] != [pdp8 getIF]) ?
				[pdp8 getIB] : [pdp8 getIF]) << 12;
			d = ((inst & 0400) && ad > 07 && ad < 020) ? 1 : 0;
				/* indirect auto index register access */
			word1 = field | ad;
			ad = ([pdp8 memoryAt:(field | ad)] + d) & 07777;
			if (inst < 05000 || inst >= 05400)
				p += sprintf(p, "  (%4.4o)", ad);
			if (inst < 05000 && (inst & 0400)) {
		    		field = ((inst < 04000) ? [pdp8 getDF] : [pdp8 getIB]) << 12;
		    		d = (d && word1 == (field | ad)) ? 1 : 0;
		    		ad = ([pdp8 memoryAt:(field | ad)] + d) & 07777;
				p += sprintf(p, " ((%4.4o))", ad);
			}
		}
		break;
	case 06000 :	/* IOT */
		[[iotOpcodes objectAtIndex:(inst & 0777)]
			getCString:p maxLength:sizeof(str) encoding:NSASCIIStringEncoding];
		p += strlen(p);
		break;
	case 07000 :	/* OPR	*/
		switch (inst & 07401) {
		case 07000 :	/* OPR Group I */
		case 07001 :
			[[oprGroup1Opcodes objectAtIndex:(inst & 0377)]
				getCString:p maxLength:sizeof(str) encoding:NSASCIIStringEncoding];
			p += strlen(p);
			break;
		case 07400 :	/* OPR Group II */
			[[oprGroup2Opcodes objectAtIndex:((inst & 0377) >> 1)]
				getCString:p maxLength:sizeof(str) encoding:NSASCIIStringEncoding];
			p += strlen(p);
			break;
/*
 *		|           |           |           |
 *		|---|---|---|---|---|---|---|---|---|
 *		| 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10| 11|
 *		| 1 |CLA|MQA|SCA|MQL|   |   |   | 1 |	Group III Mode A
 *		|---|---|---|---|---|---|---|---|---|
 *	Sequence:     1   2       2  \____3____/
 *					  V
 *		0 = NOP   2 = MUY#   4 = NMI    6 = ASR#
 *		1 = SCL#  3 = DVI#   5 = SHL#   7 = LSR#
 *
 *		|           |           |           |
 *		|---|---|---|---|---|---|---|---|---|
 *		| 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10| 11|
 *		| 1 |CLA|MQA|   |MQL|   |   |   | 1 |	Group III Mode B
 *		|---|---|---|---|---|---|---|---|---|
 *	Sequence:     1   2  \    2            /
 *			      \_______3_______/
 *				      V
 *		0 = NOP    4 = NMI    10 = SCA    14 = DPSZ
 *		1 = ACS    5 = SHL#   11 = DAD#   15 = DPIC*
 *		2 = MUY#   6 = ASR#   12 = DST#   16 = DCM*
 *		3 = DVI#   7 = LSR#   13 = SWBA   17 = SAM
 *		# = 2-word instructions
 *		* = (MQL & MQA must be set)
 */
		case 07401 :	/* EAE */
			d = (inst & 0377) >> 1 ;
			if ([pdp8 getEAEmode] == EAE_MODE_A) {
				[[eaeModeAOpcodes objectAtIndex:d]
					getCString:p maxLength:sizeof(str) encoding:NSASCIIStringEncoding];
				p += strlen(p);
				switch (inst & 016) {
				case 002 :	/* SCL */
				case 004 :	/* MUY */
				case 006 :	/* DVI */
				case 012 :	/* SHL */
				case 014 :	/* ASR */
				case 016 :	/* LSR */
					if (inst != 07447) { /* SWBA */
						p += sprintf(p, " %o", word1);
						/* word1 is operand */
					}
					break;
				default :
					break;
				}
			} else {	/* EAE mode B */
				[[eaeModeBOpcodes objectAtIndex:d]
					getCString:p maxLength:sizeof(str) encoding:NSASCIIStringEncoding];
				p += strlen(p);
				switch (inst & 056) {
				case 04 :	/* MUY */
				case 06 :	/* DVI */
					p += sprintf(p, " %o", word1);
					/* DF|word1 points to operand word */
					if (addr == pc && showOpAtPC) {
						ad = (addr & 070000) | ((addr + 1) & 07777);
						d = ((ad & 07770) == 010) ? 1 : 0;	/* autoindex? */
						word1 = ([pdp8 getDF] << 12) | ((word1 + d) & 07777);
						d = (d && (ad == word1)) ? 1 : 0;
							/* operand == autoindexed ptr? */
						p += sprintf(p, "  (%4.4o)",
							([pdp8 memoryAt:word1] + d) & 07777);
					}
					break;
				case 012 :	/* SHL */
				case 014 :	/* ASR */
				case 016 :	/* LSR */
					p += sprintf(p, " %o", word1);
					/* word1 is operand */
					break;
				case 042 :	/* DAD */
				case 044 :	/* DST */
					p += sprintf(p, " %o", word1);
					/* DF|word1 points to op double word */
					/* DAD adds op double word to AC'MQ */
					/* DST overwrites op dword with AC'MQ */
					if (addr == pc && showOpAtPC) {
						ad = (addr & 070000) | ((addr + 1) & 07777);
						d = ((ad & 07770) == 010) ? 1 : 0;	/* autoindex? */
						word2 = ([pdp8 getDF] << 12) | ((word1 + d + 1) & 07777);
						word1 = ([pdp8 getDF] << 12) | ((word1 + d) & 07777);
						e = (d && (ad == word2)) ? 1 : 0;
							/* operand == autoindexed ptr? */
						d = (d && (ad == word1)) ? 1 : 0;
						p += sprintf(p, "  (%4.4o%4.4o)",
							([pdp8 memoryAt:word2] + e) & 07777,
							([pdp8 memoryAt:word1] + d) & 07777);
					}
					break;
				}
			}
			break;
		}
		break;
	}
	*p = '\0';	/* to avoid Xcode analyzer warning */
	return [NSString stringWithCString:str encoding:NSASCIIStringEncoding];
}


- (NSString *) operandInfoForPDP8:(PDP8 *)pdp8 atAddress:(int)addr
{
	NSMutableString *string;
	int inst, op, ad, oldad, field, d, d1, mark, i;

	inst = [pdp8 memoryAt:addr];
	if (inst >= 06000)
		return nil;		/* no MRI */
	if (05000 <= inst && inst < 05400)
		return nil;		/* direct JMP */
	ad = ((inst & 0200) ? (addr & 07600) : 0) | (inst & 0177);
	d = ((inst & 0400) && ad > 07 && ad < 020) ? 1 : 0;	/* indirect auto index register access */
	ad |= addr & 070000;
	op = ([pdp8 memoryAt:ad] + d) & 07777;
	string = [NSMutableString stringWithFormat:@"%5.5o: %4.4o", ad, op];
	if (inst < 05000 && (inst & 0400)) {
		[string appendString:@"\n\n"];
		oldad = ad;
		ad = op;
		mark = ((inst < 04000) ? [pdp8 getDF] : [pdp8 getIB]) << 12;
		for (i = 0; i < 4; i++) {
			field = i << 12;
		part2 :
		    	d1 = (d && oldad == (field | ad)) ? 1 : 0;
		    	op = ([pdp8 memoryAt:field | ad] + d1) & 07777;
			[string appendFormat:@"%5.5o: %4.4o", field | ad, op];
			if (field == mark)
				[string appendFormat:@"%C", UNICODE_DIAMOND];
		    	if (field & 040000)
		    		[string appendString:@"\n"];
		    	else {
		    		[string appendString:@"\t"];
		    		field |= 040000;
		    		goto part2;
		    	}
		}
	}
	return string;
}


- (void) addMnemonic:(NSString *)mnemonic forIOT:(int)opcode
{
	NSAssert1 ((opcode & ~00777) == 06000, @"Invalid IOT %o", opcode);
	[iotOpcodes replaceObjectAtIndex:opcode & 0777 withObject:mnemonic];
}


@end
