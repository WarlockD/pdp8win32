/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK8E.m - RK8-E Disk Cartridge System for the PDP-8/E Simulator
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


#define USE_RK8E_REGISTERS_DIRECTLY 1

#import "PluginFramework/PDP8.h"

#import "RK8E.h"
#import "RK8Eiot.h"
#import "RK05.h"
#import "RK05Controller.h"


#define CODER_KEY_COMMAND	@"cmd"
#define CODER_KEY_STATUS	@"status"
#define CODER_KEY_BLOCK		@"block"
#define CODER_KEY_CURADDR	@"curaddr"
#define CODER_KEY_IOFLAG	@"ioflag"
#define CODER_KEY_IOMASK	@"iomask"
#define CODER_KEY_MAINT		@"maint"
#define CODER_KEY_LDB0		@"ldb0"
#define CODER_KEY_LDB1		@"ldb1"
#define CODER_KEY_LDB2		@"ldb2"
#define CODER_KEY_LDB3		@"ldb3"
#define CODER_KEY_LDBFILL	@"ldbfill"
#define CODER_KEY_UDB		@"udb"
#define CODER_KEY_CRC		@"crc"
#define CODER_KEY_CHECKCRC	@"checkcrc"
#define CODER_KEY_SHIFTS	@"shifts"
#define CODER_KEY_WORDCOUNT	@"wordcount"
#define CODER_KEY_SHIFT_ENABLE	@"shiftena"


@implementation RK8E


#pragma mark Plugin Methods


SETUP_PDP8_POINTER_FOR_IOTS


- (NSArray *) iotsForAddress:(int)ioAddress
{
	return [NSArray arrayWithObjects:
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:i6741],
		[NSValue valueWithPointer:i6742],
		[NSValue valueWithPointer:i6743],
		[NSValue valueWithPointer:i6744],
		[NSValue valueWithPointer:i6745],
		[NSValue valueWithPointer:i6746],
		[NSValue valueWithPointer:i6747],
		nil];
}


- (NSArray *) skiptestsForAddress:(int)ioAddress
{
	return [NSArray arrayWithObjects:
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:s6741],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:nil],
		nil];

}


- (void) setIOFlag:(unsigned long)flag forIOFlagName:(NSString *)name;
{
	ioflag = flag;
}


- (void) resetInternalRegisters
{
	maint = NO;
	ldb[0] = ldb[1] = ldb[2] = ldb[3] = 0;
	ldbfill = 0;
	udb = 0;
	crc = 0;
	checkcrc = 0;
	shifts = 0;
	wordcount = 0;
	shiftEnabled = NO;
}


- (void) CAF:(int)ioAddress
{
	command = 0;
	block = 0;
	status = [rk05[0] isMounted] ? 0 : STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY;
	curaddr = 0;
	[self resetInternalRegisters];
	[pdp8 clearIOFlagBits:ioflag];
	[pdp8 clearInterruptMaskBits:ioflag];
	[rk05[0] abortAllCommands];
	[rk05[1] abortAllCommands];
	[rk05[2] abortAllCommands];
	[rk05[3] abortAllCommands];	
}


- (void) clearAllFlags:(int)ioAddress
{
	[self setCommand:0];
	[self setBlockNumber:0];
	[self setStatus:0];
	[self updateStatusMountFlags];
	[self setMemoryAddress:0];	
	[self resetInternalRegisters];
	[pdp8 clearIOFlagBits:ioflag];
	[pdp8 clearInterruptMaskBits:ioflag];
	[rk05[0] abortAllCommands];
	[rk05[1] abortAllCommands];
	[rk05[2] abortAllCommands];
	[rk05[3] abortAllCommands];
}


- (void) resetDevice
{
	[self clearAllFlags:0];
	[rk05[0] setWriteProtected:NO];
	[rk05[1] setWriteProtected:NO];
	[rk05[2] setWriteProtected:NO];
	[rk05[3] setWriteProtected:NO];
}


#pragma mark Register Access


- (unsigned short) getCommand
{
	return command;
}


- (void) setCommand:(unsigned short)cmd
{
	NSAssert1 ((cmd & ~07777) == 0, @"Bad RK8-E Command: 0%o", cmd);
	command = cmd;
	if (command & COMMAND_INTERRUPT_ON_DONE)
		[pdp8 setInterruptMaskBits:ioflag];
	else
		[pdp8 clearInterruptMaskBits:ioflag];
	[self updateStatusMountFlags];
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter postNotificationName:COMMAND_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:MEMORYADDRESS_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:BLOCKNUMBER_CHANGED_NOTIFICATION object:self];
}


- (void) notifyPDP8IOFlagsChanged:(NSNotification *)notification
{
	//NSLog (@"RK8E notifyPDP8IOFlagsChanged");
	if ([pdp8 getInterruptMaskBits:ioflag])
		command |= COMMAND_INTERRUPT_ON_DONE;
	else
		command &= ~COMMAND_INTERRUPT_ON_DONE;
	[[NSNotificationCenter defaultCenter] postNotificationName:COMMAND_CHANGED_NOTIFICATION object:self];
	if ([pdp8 getIOFlagBits:ioflag]) {
		if (! (status & STATUS_RAISE_FLAG_MASK))
			status |= STATUS_DONE;
	} else
		status &= ~STATUS_RAISE_FLAG_MASK;
	[[NSNotificationCenter defaultCenter] postNotificationName:STATUS_CHANGED_NOTIFICATION object:self];
}


- (unsigned short) getStatus
{
	return status;
}


- (void) setStatus:(unsigned short)stat
{
	NSAssert1 ((stat & ~07777) == 0, @"Bad RK8-E Status: 0%o", stat);
	status = stat;
	if (status & STATUS_RAISE_FLAG_MASK)
		[pdp8 setIOFlagBits:ioflag];
	else
		[pdp8 clearIOFlagBits:ioflag];
	[[NSNotificationCenter defaultCenter] postNotificationName:STATUS_CHANGED_NOTIFICATION object:self];
}


- (void) updateStatusMountFlags
{
	NSAssertRunningOnMainThread ();
	if ([rk05[(command >> 1) & 3] isMounted])
		status &= ~(STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY);
	else
		status |= STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY;
	[[NSNotificationCenter defaultCenter] postNotificationName:STATUS_CHANGED_NOTIFICATION object:self];
}


- (void) notifyStatusChangedWhenNotRunning
{
	NSAssertRunningOnMainThread ();
	NSAssert (! [pdp8 isGoing], @"PDP-8 is running");
	[[NSNotificationCenter defaultCenter] postNotificationName:STATUS_CHANGED_NOTIFICATION object:self];
}


- (void) setStatusBits:(unsigned)set clearStatusBits:(unsigned)clear
{
	status |= set;
	status &= ~clear;
	if (status & STATUS_RAISE_FLAG_MASK)
		[pdp8 setIOFlagBits:ioflag];
	else
		[pdp8 clearIOFlagBits:ioflag];
	if (! [pdp8 isGoing])
		[self performSelectorOnMainThread:@selector(notifyStatusChangedWhenNotRunning)
			withObject:nil waitUntilDone:YES];
}


- (unsigned short) getBlockNumber
{
	return ((command & 1) << 12) | block;
}


- (void) setBlockNumber:(unsigned short)blk
{
	NSAssert1 ((blk & ~017777) == 0, @"Bad RK8-E Block Number: 0%o", blk);
	block = blk & 07777;
	[[NSNotificationCenter defaultCenter] postNotificationName:BLOCKNUMBER_CHANGED_NOTIFICATION
		object:self];
	[self setCommand:([self getCommand] & 07776) | (blk >> 12)];
}


- (unsigned short) getMemoryAddress
{
	return ((command & 070) << 9) | curaddr;
}


- (void) setMemoryAddress:(unsigned short)addr
{
	NSAssert1 ((addr & ~077777) == 0, @"Bad RK8-E Memory Address: 0%o", addr);
	curaddr = addr & 07777;
	[[NSNotificationCenter defaultCenter] postNotificationName:MEMORYADDRESS_CHANGED_NOTIFICATION
		object:self];
	[self setCommand:([self getCommand] & 07707) | ((addr >> 9) & 070)];
}


- (unsigned short) getCurrentAddress
{
	return curaddr;
}


- (void) setCurrentAddress:(unsigned short)addr
{
	NSAssert1 ((addr & ~07777) == 0, @"Bad Current Address: 0%o", addr);
	curaddr = addr;
}


- (unsigned short) getCurrentDriveNumber
{
	return (command >> 1) & 3;
}


- (void) setCRC:(unsigned)newCRC
{
	crc = checkcrc = newCRC;
}


- (BOOL) isCRCOK
{
	return crc == checkcrc;
}


- (void) lockControl
{
	[controlLock lock];
}


- (void) unlockControl
{
	[controlLock unlock];
}


#pragma mark Initialization


- (id) initWithCoder:(NSCoder *)coder
{
	self = [super init];
	[self setCommand:[coder decodeIntForKey:CODER_KEY_COMMAND]];
	[self setStatus:[coder decodeIntForKey:CODER_KEY_STATUS]];
	[self setBlockNumber:[coder decodeIntForKey:CODER_KEY_BLOCK]];
	[self setCurrentAddress:[coder decodeIntForKey:CODER_KEY_CURADDR]];
	[coder decodeBoolForKey:CODER_KEY_IOFLAG] ?
		[pdp8 setIOFlagBits:ioflag] : [pdp8 clearIOFlagBits:ioflag];
	[coder decodeBoolForKey:CODER_KEY_IOMASK] ?
		[pdp8 setInterruptMaskBits:ioflag] : [pdp8 clearInterruptMaskBits:ioflag];	
	maint = [coder decodeBoolForKey:CODER_KEY_MAINT];
	ldb[0] = [coder decodeIntForKey:CODER_KEY_LDB0];
	ldb[1] = [coder decodeIntForKey:CODER_KEY_LDB1];
	ldb[2] = [coder decodeIntForKey:CODER_KEY_LDB2];
	ldb[3] = [coder decodeIntForKey:CODER_KEY_LDB3];
	ldbfill = [coder decodeIntForKey:CODER_KEY_LDBFILL];
	udb = [coder decodeIntForKey:CODER_KEY_UDB];
	crc = [coder decodeIntForKey:CODER_KEY_CRC];
	checkcrc = [coder decodeIntForKey:CODER_KEY_CHECKCRC];
	shifts = [coder decodeIntForKey:CODER_KEY_SHIFTS];
	wordcount = [coder decodeIntForKey:CODER_KEY_WORDCOUNT];
	shiftEnabled = [coder decodeBoolForKey:CODER_KEY_SHIFT_ENABLE];
	return self;
}


- (void) encodeWithCoder:(NSCoder *)coder
{
	[coder encodeInt:[self getCommand] forKey:CODER_KEY_COMMAND];
	[coder encodeInt:[self getStatus] forKey:CODER_KEY_STATUS];
	[coder encodeInt:[self getBlockNumber] forKey:CODER_KEY_BLOCK];
	[coder encodeInt:[self getCurrentAddress] forKey:CODER_KEY_CURADDR];
	[coder encodeBool:[pdp8 getIOFlagBits:ioflag] ? YES : NO forKey:CODER_KEY_IOFLAG];
	[coder encodeBool:[pdp8 getInterruptMaskBits:ioflag] ? YES : NO forKey:CODER_KEY_IOMASK];
	[coder encodeBool:maint forKey:CODER_KEY_MAINT];
	[coder encodeInt:ldb[0] forKey:CODER_KEY_LDB0];
	[coder encodeInt:ldb[1] forKey:CODER_KEY_LDB1];
	[coder encodeInt:ldb[2] forKey:CODER_KEY_LDB2];
	[coder encodeInt:ldb[3] forKey:CODER_KEY_LDB3];
	[coder encodeInt:ldbfill forKey:CODER_KEY_LDBFILL];
	[coder encodeInt:udb forKey:CODER_KEY_UDB];
	[coder encodeInt:crc forKey:CODER_KEY_CRC];
	[coder encodeInt:checkcrc forKey:CODER_KEY_CHECKCRC];
	[coder encodeInt:shifts forKey:CODER_KEY_SHIFTS];
	[coder encodeInt:wordcount forKey:CODER_KEY_WORDCOUNT];
	[coder encodeBool:shiftEnabled forKey:CODER_KEY_SHIFT_ENABLE];
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	// NSLog (@"RK8E notifyApplicationWillTerminate");
	NSMutableData *data = [NSMutableData data];
	NSKeyedArchiver *archiver = [[NSKeyedArchiver alloc] initForWritingWithMutableData:data];
	[self encodeWithCoder:archiver];
	[rk05Controller_0 encodeWithCoder:archiver];
	[rk05Controller_1 encodeWithCoder:archiver];
	[rk05Controller_2 encodeWithCoder:archiver];
	[rk05Controller_3 encodeWithCoder:archiver];
	[archiver finishEncoding];
	[archiver release];
	[[NSUserDefaults standardUserDefaults] setObject:data forKey:[self pluginName]];
}


- (void) pluginDidLoad
{
	rk05[0] = rk05_0; [rk05[0] setPDP8:pdp8];
	rk05[1] = rk05_1; [rk05[1] setPDP8:pdp8];
	rk05[2] = rk05_2; [rk05[2] setPDP8:pdp8];
	rk05[3] = rk05_3; [rk05[3] setPDP8:pdp8];
	NSData *data = [[NSUserDefaults standardUserDefaults] dataForKey:[self pluginName]];
	if (data) {
		NSKeyedUnarchiver *unarchiver = [[NSKeyedUnarchiver alloc] initForReadingWithData:data];
		self = [self initWithCoder:unarchiver];
		[rk05Controller_0 initWithCoder:unarchiver];
		[rk05Controller_1 initWithCoder:unarchiver];
		[rk05Controller_2 initWithCoder:unarchiver];
		[rk05Controller_3 initWithCoder:unarchiver];
		[unarchiver finishDecoding];
		[unarchiver release];
	} 
	controlLock = [[NSLock alloc] init];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil]; 
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyPDP8IOFlagsChanged:)
		name:IOFLAGS_CHANGED_NOTIFICATION object:nil]; 
}


@end
