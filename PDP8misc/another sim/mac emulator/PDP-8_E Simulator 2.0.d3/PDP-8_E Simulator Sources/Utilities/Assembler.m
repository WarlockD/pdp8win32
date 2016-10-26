/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Assembler.h - Inline assembler for PDP-8/E instructions
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


#import "Assembler.h"
#import "Opcode.h"


@implementation Assembler


#define isNothing		0
#define isNumber		1
#define isIOT			2
#define needsOperand		4
#define isMRI			8
#define isIndirect		16
#define isOPR1			32
#define isOPR2			64
#define isOPR3a			128 
#define isOPR3b			256
#define isRelativeAddress	512


#define asmNoError			nil
#define asmBadNumber			NSLocalizedString(@"Invalid octal number.", @"")
#define asmUndefSymbol			NSLocalizedString(@"Undefined opcode.", @"")
#define asmBadAddress			NSLocalizedString(@"Illegal address.", @"")
#define asmBadRelativeAddress		NSLocalizedString(@"Illegal relative address.", @"")
#define asmCannotStartWithThis		NSLocalizedString(@"Opcode expected.", @"")
#define asmRelativeAddressNotAllowedHere NSLocalizedString(@"Relative address not allowed here.", @"")
#define asmIndirectNotAllowedHere	NSLocalizedString(@"Indirect addressing not allowed here.", @"")
#define asmOffpageReference		NSLocalizedString(@"Offpage reference.", @"")
#define asmEndExpected			NSLocalizedString(@"End of line expected.", @"")
#define asmIllegalMicroprogramming	NSLocalizedString(@"Invalid combination of opcodes.", @"")
#define asmOperandNotAllowedHere	NSLocalizedString(@"Operand not allowed here.", @"")
#define asmBadFieldNumber		NSLocalizedString(@"Bad field number.", @"")
#define asmNeedsOperand			NSLocalizedString(@"Operand expected.", @"")


+ (Assembler *) sharedAssembler
{
	static Assembler *sharedAssembler;

	if (! sharedAssembler)
		sharedAssembler = [[self alloc] init];
	return sharedAssembler;
}


- (Assembler *) init
{
	NSString *key, *flagOrOpcode;
	NSNumber *newFlag;
	NSEnumerator *arrayEnum;
	int opcode, flag, n;
	char buf[128];
	
	NSDictionary *dict = [NSDictionary dictionaryWithContentsOfFile:
		[[NSBundle mainBundle] pathForResource:@"assembler" ofType:@"plist"]];
	NSEnumerator *dictEnum = [dict keyEnumerator];
	
	NSDictionary *flags = [NSDictionary dictionaryWithObjectsAndKeys:
		[NSNumber numberWithInt:isMRI], @"isMRI",
		[NSNumber numberWithInt:isIOT], @"isIOT",
		[NSNumber numberWithInt:isOPR1], @"isOPR1",
		[NSNumber numberWithInt:isOPR2], @"isOPR2",
		[NSNumber numberWithInt:isOPR3a], @"isOPR3a",
		[NSNumber numberWithInt:isOPR3b], @"isOPR3b",
		[NSNumber numberWithInt:isIndirect], @"isIndirect",
		[NSNumber numberWithInt:needsOperand], @"needsOperand",
		nil];
	
	self = [super init];
	symtab = [[NSMutableDictionary alloc] init];
	while ((key = [dictEnum nextObject])) {
		arrayEnum = [[dict valueForKey:key] objectEnumerator];
		flag = 0;
		opcode = -1;
		while ((flagOrOpcode = [arrayEnum nextObject])) {
			newFlag = [flags objectForKey:flagOrOpcode];
			if (newFlag)
				flag |= [newFlag intValue];
			else {
				[flagOrOpcode getCString:buf
					maxLength:(sizeof(buf) - 1) encoding:NSMacOSRomanStringEncoding];
				n = sscanf(buf, "%o", &opcode);
				NSAssert1 (n == 1 && (opcode & ~07777) == 0,
					@"Invalid parameter for opcode %@", key);
				if (n) ;	// to avoid Analyzer warning "Value of n never used"
			}
		}
		NSAssert1 (flag != 0 && opcode != -1, @"Incomplete parameters for opcode %@", key);
		[symtab setObject:[NSNumber numberWithInt:((flag << 12) | opcode)] forKey:key];
	}
	return self;
}


- (char *) lookupSymbol:(char *)s getOctal:(long *)octal getFlags:(ushort *)flags error:(NSString **)err
{
	char *t, c;
	unsigned long n;
	NSNumber *sym;
	
	*err = asmNoError;
	while (*s == ' ')
		s++;
	if (*s == '\0') {
		*octal = 0;
		*flags = isNothing;
		return (s);
	}
	if (*s == '.') {
		s++;
		while (*s == ' ')
			s++;
		if (*s == '+' || *s == '-') {
			c = *s++;
			while (*s == ' ')
				s++;
		} else if (*s) {
			*err = asmBadRelativeAddress;
			return (s);
		} else {
			*flags = isRelativeAddress;
			*octal = 0;
			return (s);
		}
	} else
		c = '\0';
	t = s;
	while ('0' <= *t && *t <= '7')
		t++;
	if (t > s) {
		t = s;
		for (n = 0; '0' <= *s && *s <= '7'; s++) {
			n = 8 * n + *s - '0';
			if (n & ~07777) {
				*err = c ? asmBadRelativeAddress : asmBadNumber;
				return (t);
			}
		}
		if (c) {
			*flags = isRelativeAddress;
			*octal = (c == '+') ? n : -n;
		} else {
			*flags = isNumber;
			*octal = n;
		}
		return (s);
	}
	if (c) {
		*err = asmBadRelativeAddress;
		return (s);
	}
	while (*t && *t != ' ')
		t++;
	c = *t;
	*t = '\0';
	sym = [symtab objectForKey:[NSString stringWithCString:s encoding:NSMacOSRomanStringEncoding]];
	*t = c;
	if (sym == nil) {
		*err = (*s == '8' || *s == '9') ? asmBadNumber : asmUndefSymbol;
		t = s;
	} else {
		*octal = [sym intValue] & 07777;
		*flags = [sym intValue] >> 12;
	}
	return (t);
}


- (Opcode *) assemble:(NSString *)source atAddress:(int)address error:(NSString **)error
{
	char buf[128];
	char *bp, *oldbp;
	long w, word, word1;
	ushort cl, flags;
	
	int errpos = 0;
	NSString *err = asmNoError;
	Opcode *opcode = [Opcode opcodeWithAddress:address];
	
	[[source uppercaseString] getCString:buf
		maxLength:(sizeof(buf) - 1) encoding:NSMacOSRomanStringEncoding];
	bp = buf;
	word = 0;
	word1 = -1;
	flags = isNothing;
	for (;;) {
		oldbp = bp;
		bp = [self lookupSymbol:bp getOctal:&w getFlags:&cl error:&err];
		while (*oldbp == ' ')
			oldbp++;
		errpos = oldbp - buf;
		if (err)
			goto error;
		if (cl == isNothing)
			break;
		if (flags == isNothing && 
			(cl & (isIndirect | isRelativeAddress))) {
			err = asmCannotStartWithThis;
			goto error;
		}
		if (cl == isRelativeAddress) {
			if (! (flags & isMRI) || 
				(word != 04000 && word != 05000)) {
				err = asmRelativeAddressNotAllowedHere;
				goto error;
			}
			cl = isNumber;
			w = (w + (long int) address) & 07777;
		}
		if (! (flags & isMRI) && (cl & isIndirect)) {
			err = asmIndirectNotAllowedHere;
			goto error;
		}
		if (flags & (isIndirect | isMRI)) {
			if (flags & cl & isIndirect) {
				err = asmIndirectNotAllowedHere;
				goto error;
			}
			if (cl == isNumber) {
				if (w < 0 || w > 07777) {
					err = asmBadAddress;
					goto error;
				}
				if (w >= 0200) {
					if ((w & 07600) != (address & 07600)) {
						err = asmOffpageReference;
						goto error;
					}
					w = 0200 | (w & 0177);
				}
				flags &= ~needsOperand;
			} else if (! (cl & isIndirect)) {
				err = asmIllegalMicroprogramming;
				goto error;
			}
		}
		if (flags & isIOT) {
			if (! (cl & (isIOT | isNumber))) {
				err = asmIllegalMicroprogramming;
				goto error;
			}
			if (cl == isNumber) {
				if (! (flags & needsOperand)) {
					err = asmOperandNotAllowedHere;
					goto error;
				}
				if (w < 010)
					w <<= 3;
				if (w & ~070) {
					err = asmBadFieldNumber;
					goto error;
				}
				flags &= ~needsOperand;
			} else if ((word & 07770) != (w & 07770)) {
				err = asmIllegalMicroprogramming;
				goto error;
			}
		}
		if (flags & (isOPR1 | isOPR2 | isOPR3a | isOPR3b)) {
			if (cl == isNumber) {
				if (! (flags & needsOperand)) {
					err = asmOperandNotAllowedHere;
					goto error;
				}
				flags &= ~needsOperand;
				word1 = w;
				w = 0;
			} else {
				if (! (cl & flags)) {
					err = asmIllegalMicroprogramming;
					goto error;
				}
				cl &= flags | needsOperand;
			}
		}
		if (flags & isNumber) {
			err = asmEndExpected;
			goto error;
		}
		flags |= cl;
		word |= w;
	}
	if (flags & needsOperand) {
		err = asmNeedsOperand;
		goto error;
	}
	[opcode setWord0:(word & 07777)];
	if (word1 != -1)
		[opcode setWord1:(word1 & 07777)];
error :
	if (err) {
		if (error)
			*error = [NSString stringWithFormat:@"%d %@", errpos, err];
		return nil;
	}
	return opcode;
}


- (void) addMnemonic:(NSString *)mnemonic forIOT:(int)opcode
{
	NSAssert1 ((opcode & ~00777) == 06000, @"Invalid IOT %o", opcode);
	[symtab setObject:[NSNumber numberWithInt:((isIOT << 12) | opcode)] forKey:mnemonic];
}


@end
