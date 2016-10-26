/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	StateMachine.c - State Machine for the KC8-EA Programmer’s Console
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


#import "PluginFramework/PDP8.h"

#import "StateMachine.h"


// these bits are in the positions of the "state" display
#define FETCH		04000
#define DEFER		02000
#define EXECUTE		01000
#define NO_CYCLE	00000

// opcodes in the instruction register
#define IR_AND		00000
#define IR_TAD		00100
#define IR_ISZ		00200
#define IR_DCA		00300
#define IR_JMS		00400
#define IR_JMP		00500
#define IR_IOT		00600
#define IR_OPR		00700

// values for mdDir: to or from memory
#define MB_TO_MEM	00000
#define MB_FROM_MEM	00040

// coder keys
#define CODER_KEY_CYCLE			@"cycle"
#define CODER_KEY_MD			@"md"
#define CODER_KEY_MDDIR			@"mdDir"
#define CODER_KEY_IR			@"ir"
#define CODER_KEY_CPMA			@"cpma"
#define CODER_KEY_BUS			@"bus"
#define CODER_KEY_PAUSE			@"pause"
#define CODER_KEY_AUTOINDEX		@"autoindex"
#define CODER_KEY_INTERRUPT_IN_PROGRESS	@"interruptInProgress"
#define CODER_KEY_OLDPC			@"oldpc"
#define CODER_KEY_OLDINST		@"oldinst"


@implementation StateMachine


- (StateMachine *) initWithPDP8:(PDP8 *)p8
{
	self = [super init];
	pdp8 = p8;
	[self updateState:NO];
	return self;
}


- (StateMachine *) initWithCoder:(NSCoder *)coder pdp8:(PDP8 *)p8
{
	self = [self initWithCoder:coder];
	pdp8 = p8;
	[self updateState:NO];
	return self;
}


- (id) initWithCoder:(NSCoder *)coder
{
	self = [super init];
	cycle = [coder decodeIntForKey:CODER_KEY_CYCLE];
	md = [coder decodeIntForKey:CODER_KEY_MD];
	mdDir = [coder decodeIntForKey:CODER_KEY_MDDIR];
	ir = [coder decodeIntForKey:CODER_KEY_IR];
	cpma = [coder decodeIntForKey:CODER_KEY_CPMA];
	bus = [coder decodeIntForKey:CODER_KEY_BUS];
	pause = [coder decodeBoolForKey:CODER_KEY_PAUSE];
	autoindex = [coder decodeIntForKey:CODER_KEY_AUTOINDEX];
	interruptInProgress = [coder decodeBoolForKey:CODER_KEY_INTERRUPT_IN_PROGRESS];
	oldpc = [coder decodeIntForKey:CODER_KEY_OLDPC];
	oldinst = [coder decodeIntForKey:CODER_KEY_OLDINST];
	return self;
}


- (void) encodeWithCoder:(NSCoder *)coder
{
	[coder encodeInt:cycle forKey:CODER_KEY_CYCLE];
	[coder encodeInt:md forKey:CODER_KEY_MD];
	[coder encodeInt:mdDir forKey:CODER_KEY_MDDIR];
	[coder encodeInt:ir forKey:CODER_KEY_IR];
	[coder encodeInt:cpma forKey:CODER_KEY_CPMA];
	[coder encodeInt:bus forKey:CODER_KEY_BUS];
	[coder encodeBool:pause forKey:CODER_KEY_PAUSE];
	[coder encodeInt:autoindex forKey:CODER_KEY_AUTOINDEX];
	[coder encodeBool:interruptInProgress forKey:CODER_KEY_INTERRUPT_IN_PROGRESS];
	[coder encodeInt:oldpc forKey:CODER_KEY_OLDPC];
	[coder encodeInt:oldinst forKey:CODER_KEY_OLDINST];
}


- (unsigned short) state:(BOOL)sw
{
	return cycle | ir | mdDir | (sw ? 00010 : 0) | (pause ? 00004 : 0);
}


- (unsigned short) status
{
	return ([pdp8 getL] ? 04000 : 0)				// Link
		| ([pdp8 getGTF] ? 02000 : 0)				// Greater Than Flag
		| ([pdp8 interruptRequest] ? 01000 : 0)			// Interrupt Request
		| ([pdp8 getInhibit] || [pdp8 getDelay] ? 00400 : 0)	// NO INT
		| ([pdp8 getEnable] ? 00200 : 0)			// Interrupt On
		| (interruptInProgress ? 0 : [pdp8 getUF] ? 00100 : 0)	// User Flag
		| ([pdp8 getIF] << 3)					// Instruction Field
		| [pdp8 getDF];						// Data Field
}


- (unsigned short) md
{
	return md;
}


- (unsigned short) bus
{
	return bus;
}


- (unsigned short) cpma
{
	return cpma;
}


- (BOOL) isInFetchCycle
{
	return cycle == FETCH;
}


- (void) loadAddress
{
	if ([pdp8 isStopped]) {
		cpma = ([pdp8 getIF] << 12) | [pdp8 getSR];
		bus = [pdp8 getSR];
		[pdp8 setPC:bus];
		cycle = FETCH;
	}
}


- (void) loadExtendedAddress
{
	if ([pdp8 isStopped]) {
		bus = [pdp8 getSR];
		[pdp8 loadExtendedAddress];
	}
}


- (void) setDatabus
{
	bus = [pdp8 getAC];
	if ((md & 07200) == 07200)		// 7200 == CLA at seq. 1
		bus = 0;
	if ((md & 07501) == 07501)		// 7501 == MQA at seq. 2
		bus |= [pdp8 getMQ];
	if ((md & 07404) == 07404)		// 7404 == OSR at seq. 3
		bus |= [pdp8 getSR];
	/* the bus setting may be wrong for group 3 OPRs implemented by the EAE */
}


- (void) updateState:(BOOL)exitFromGo
{
	autoindex = -1;
	if ([pdp8 isStopped]) {
		if ([pdp8 getProgramCounter] != oldpc || [pdp8 memoryAt:oldpc] != oldinst) {
			oldpc = cpma = [pdp8 getProgramCounter];
			oldinst = [pdp8 memoryAt:oldpc];
			cycle = FETCH;
			if (exitFromGo) {
				ir = ([pdp8 getCurrentOpcode] & 07000) >> 3;
				mdDir = MB_FROM_MEM;
				md = [pdp8 getCurrentOpcode];
				[self setDatabus];
			}
		}
	} else {
		/* set state at the end of the next instructions fetch cycle,
		   before the instruction is executed */
		bus = [pdp8 getAC];
		if ([pdp8 getEnable] && [pdp8 interruptRequest] &&
			! ([pdp8 getDelay] || [pdp8 getInhibit])) {	/* interrupt */
			cpma = 0;
			ir = IR_JMS;
			cycle = EXECUTE;
		} else {				/* normal instruction fetch */
			md = [pdp8 memoryAt:[pdp8 getProgramCounter]];
			mdDir = MB_FROM_MEM;
			ir = (md & 07000) >> 3;
			cpma = ([pdp8 getIF] << 12) |
				((md & 00200) ? (cpma & 07600) : 0) | (md & 0177);
			if (ir <= IR_JMP && (md & 00400))	/* indirect MRI */
				cycle = DEFER;
			else if (ir < IR_JMP)			/* direct MRI != JMP */
				cycle = EXECUTE;
			else {					/* OPR, IOT or direct JMP */
				cycle = FETCH;
				if (ir == IR_JMP)
					cpma = ([pdp8 getIB] << 12) | (cpma & 07777);
				else {
					cpma = ([pdp8 getIF] << 12) | (([pdp8 getPC] + 1) & 07777);
					[self setDatabus];
					if (ir == IR_IOT)
						pause = YES;
				}
			}
		}
	}
}


- (void) eaeExecute
/* check for memory cycles of EAE operate instructions */
{
	switch (md) {
	case 07403 :	/* SCL */
		if ([pdp8 getEAEmode] == EAE_MODE_A) {
			cpma = ([pdp8 getIF] << 12) | (([pdp8 getPC] + 1) & 07777);
			md = [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
		}
		break;
	case 07405 :	/* MUY */
	case 07407 :	/* DVI */
		cpma = ([pdp8 getIF] << 12) | (([pdp8 getPC] + 1) & 07777);
		md = [pdp8 memoryAt:cpma];
		mdDir = MB_FROM_MEM;
		if ([pdp8 getEAEmode] == EAE_MODE_B) {
			if ((cpma & 07770) == 010) {
				autoindex = cpma;
				md = (md + 1) & 07777;
				mdDir = MB_TO_MEM;
			}
			cpma = ([pdp8 getDF] << 12) | md;
			md = (cpma == autoindex) ? ([pdp8 memoryAt:cpma] + 1) & 07777 : [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
		}
		break;
	case 07413 :	/* SHL */
	case 07415 :	/* ASR */
	case 07417 : 	/* LSR */
		cpma = ([pdp8 getIF] << 12) | (([pdp8 getPC] + 1) & 07777);
		md = [pdp8 memoryAt:cpma];
		mdDir = MB_FROM_MEM;
		break;
	case 07443 :	/* DAD */
		if ([pdp8 getEAEmode] == EAE_MODE_B) {
			cpma = ([pdp8 getIF] << 12) | (([pdp8 getPC] + 1) & 07777);
			md = [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
			if ((cpma & 07770) == 010) {
				autoindex = cpma;
				md = (md + 1) & 07777;
				mdDir = MB_TO_MEM;
			} 
			cpma = ([pdp8 getDF] << 12) | ((md + 1) & 07777);
			md = (cpma == autoindex) ? ([pdp8 memoryAt:cpma] + 1) & 07777 : [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
		}
		break;
	case 07445 :	/* DST */
		if ([pdp8 getEAEmode] == EAE_MODE_B) {
			cpma = ([pdp8 getIF] << 12) | (([pdp8 getPC] + 1) & 07777);
			md = [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
			if ((cpma & 07770) == 010) {
				autoindex = cpma;
				md = (md + 1) & 07777;
				mdDir = MB_TO_MEM;
			} 
			cpma = ([pdp8 getDF] << 12) | ((md + 1) & 07777);
			md = [pdp8 getAC];
			mdDir = MB_TO_MEM;
		}
		break;
	}
}


- (void) executeSingleCycle
{
	switch (cycle) {
	case FETCH :
		autoindex = -1;
		bus = [pdp8 getAC];
		if ([pdp8 getEnable] && ! ([pdp8 getDelay] || [pdp8 getInhibit]) && [pdp8 interruptRequest]) {
			interruptInProgress = YES;		/* interrupt */
			cpma = 0;
			ir = IR_JMS;
			cycle = EXECUTE;
		} else {					/* normal instruction fetch */
			cpma = [pdp8 getProgramCounter];
			md = [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
			ir = (md & 07000) >> 3;
			cpma = ([pdp8 getIF] << 12) |
				((md & 0200) ? (cpma & 07600) : 0) | (md & 0177);
			if (ir <= IR_JMP && (md & 0400))
				cycle = DEFER;
			else if (ir < IR_JMP)
				cycle = EXECUTE;
			else {
				[self setDatabus];
				[self eaeExecute];
				if (ir == IR_IOT) {
					bus = [pdp8 getAC];
					pause = YES;
				}
				[pdp8 step];
			}
		}
		break;
	case DEFER :
		if ((cpma & 07770) == 00010) {	/* autoindex */
			autoindex = cpma;
			md = ([pdp8 memoryAt:cpma] + 1) & 07777;
			mdDir = MB_TO_MEM;	/* incremented value is written back */
		} else {
			md = [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
		}
		cpma = ([pdp8 getDF] << 12) | md;
		cycle = EXECUTE;
		break;
	case EXECUTE :
		switch (ir) {
		case IR_AND :
			md = (cpma == autoindex) ? ([pdp8 memoryAt:cpma] + 1) & 07777 : [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
			break;
		case IR_TAD :
			md = (cpma == autoindex) ? ([pdp8 memoryAt:cpma] + 1) & 07777 : [pdp8 memoryAt:cpma];
			mdDir = MB_FROM_MEM;
			break;
		case IR_ISZ :
			md = (cpma == autoindex) ? ([pdp8 memoryAt:cpma] + 1) & 07777 : [pdp8 memoryAt:cpma];
			md = (md + 1) & 07777;
			mdDir = MB_TO_MEM;
			break;
		case IR_DCA :
			bus = md = [pdp8 getAC];
			mdDir = MB_TO_MEM;
			break;
		case IR_JMS :
			md = ([pdp8 getPC] + (interruptInProgress ? 0 : 1)) & 07777;
			mdDir = MB_TO_MEM;
			break;
		case IR_JMP :
			break;
		}
		interruptInProgress = NO;
		cycle = FETCH;
		[pdp8 step];
		break;
	}
}


- (void) examine
{
	md = [pdp8 memoryAt:cpma];
	[pdp8 setPC:([pdp8 getPC] + 1) & 07777];
	cpma = [pdp8 getProgramCounter];
}


- (void) deposit
{
	cpma = [pdp8 getProgramCounter];
	md = bus = [pdp8 getSR];
	if ([pdp8 memorySize] > cpma)
		[pdp8 setMemoryAtAddress:cpma toValue:md];
	mdDir = MB_TO_MEM;
	[pdp8 setPC:([pdp8 getPC] + 1) & 07777];
	cpma = [pdp8 getProgramCounter];
}


@end
