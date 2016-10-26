/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK05.m - Class implementing a RK05 DECpack drive
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


#include <sys/stat.h>

#import "PluginFramework/PDP8.h"
#import "PluginFramework/Utilities.h"

#import "RK05.h"
#import "RK8E.h"


@implementation RK05


/* "commands" for a RK05 drive to be performed asynchroniously, bit coded in self.cmd */
#define RK05_RECALIBRATE	0400
#define RK05_SET_CRC_STATE	0200
#define RK05_DELAY		0100
#define RK05_SEEK		0040
#define RK05_FLUSH_CYL		0020
#define RK05_READ_CYL		0010
#define RK05_INPUT_BLOCK	0004
#define RK05_OUTPUT_BLOCK	0002
#define RK05_RAISE_FLAG		0001

#define NO_COMMANDS_AVAILABLE	0
#define COMMANDS_AVAILABLE	1


- (int) driveNumber
{
	return driveNumber;
}


- (void) setDriveNumber:(short)drvNum
{
	driveNumber = drvNum;
}


- (void) setPDP8:(PDP8 *)p8
{
	pdp8 = p8;
}


- (void) setWriteProtected:(BOOL)writeProtected
{
	locked = writeProtected;
	[[NSNotificationCenter defaultCenter] postNotificationName:WRITEPROTECT_CHANGED_NOTIFICATION
		object:self];
}


- (BOOL) isWriteProtected
{
	return locked;
}


- (BOOL) flush
{
	BOOL error = NO;
	if (decpack && dirty) {
		if (fseek(decpack, cyl * RK05_BUFSIZE, SEEK_SET) < 0)
			error = YES;
		else if (fwrite(buffer, 1, RK05_BUFSIZE, decpack) != RK05_BUFSIZE)
			error = YES;
		dirty = NO;
	}
	return error;
}


- (BOOL) canMount:(NSString *)path
{
	NSAssert (decpack == NULL, @"A DECpack is already mounted");
	FILE *handle = fopen([path cStringUsingEncoding:NSUTF8StringEncoding], "r+");
	if (handle) {
		if (flock(fileno(handle), LOCK_EX | LOCK_NB)) {
			fclose (handle);
			return NO;
		}
		flock (fileno(handle), LOCK_UN);
		return YES;
	}
	return NO;
}


- (int) mount:(NSString *)path create:(BOOL)create
{
	NSAssert (decpack == NULL, @"A DECpack is already mounted");
	int err = 0;
	decpack = fopen([path cStringUsingEncoding:NSUTF8StringEncoding], create ? "w+" : "r+");
	if (decpack) {
		if (flock(fileno(decpack), LOCK_EX | LOCK_NB)) {
			fclose (decpack);
			decpack = NULL;
			err = -2;
		}
	} else
		err = -1;
	cyl = -1;
	dirty = NO;
	memset (crcstate, 0, sizeof(crcstate));
	[rk8e updateStatusMountFlags];
	return err;
}


- (BOOL) unmount
{
	NSAssert (decpack != NULL, @"Cannot unmount without mounted DECpack");
	BOOL error = [self flush];
	flock (fileno(decpack), LOCK_UN);
	fclose (decpack);
	decpack = NULL;
	[rk8e updateStatusMountFlags];
	return error;
}


- (BOOL) isMounted
{
	return decpack != NULL;
}


- (BOOL) isBusy
{
	return cmd != 0;
}


- (BOOL) isCalibrating
{
	return (cmd & RK05_RECALIBRATE) ? YES : NO;
}


- (void) stopCalibrating
{
	cmd &= ~RK05_RECALIBRATE;
}


- (void) abortAllCommands
{
	cmd = 0;
}


- (void) setStatusAndAbortAllCommands
{
	if (cmd) {
		unsigned short set = 0;
		if (cmd & RK05_RECALIBRATE)
			set |= STATUS_CONTROL_BUSY;
		else
			set |= (STATUS_DONE | STATUS_ERROR);
		if (cmd & RK05_SEEK)
			set |= STATUS_HEAD_IN_MOTION;
		cmd = 0;
		[rk8e setStatusBits:set clearStatusBits:0];
	}
}


- (void) setRecalibrating
{
	durationMicroSeconds = 85000;		// 85 ms == 200 track movement
	cmd |= RK05_RECALIBRATE | RK05_DELAY;
}


- (void) setFlushCylinder
{
	if (dirty)
		cmd |= RK05_FLUSH_CYL;
}


- (void) setReadCylinder:(unsigned short)cylinder
{
	if (cyl != cylinder) {
		newcyl = cylinder;
		cmd |= RK05_READ_CYL;	
	}
}


- (void) setRaiseFlag
{
	cmd |= RK05_RAISE_FLAG;
}


- (void) clearRaiseFlag
{
	cmd &= ~RK05_RAISE_FLAG;
}


- (void) setRead:(BOOL)read write:(BOOL)write all:(BOOL)all newBlock:(unsigned short)newBlock
{
	long d;

	if (write && locked) {
		[rk8e setStatusBits:STATUS_WRITE_LOCK_ERROR | STATUS_ERROR clearStatusBits:0];
		return;
	}
	if (newBlock > 014537) {
		[rk8e setStatusBits:STATUS_DONE | STATUS_ERROR clearStatusBits:0];
		return;
	}
	if (! read && ! write && [self isMounted]) {	// seek only
		/* set on DISK ADDRESS ACKNOWLAGE; it is set again 
		   on seek complete when command bit 4 is on */ 
		[rk8e setStatusBits:STATUS_DONE clearStatusBits:0];
		cmd |= RK05_SEEK;
	}
	if (cyl != (newBlock >> 5)) {
		if (dirty)
			cmd |= RK05_FLUSH_CYL;
		newcyl = newBlock >> 5;
		cmd |= RK05_READ_CYL;
		d = newcyl - cyl;
		if (d < 0)
			d = -d;
		d = 9621l + 379l * d;
		/* 9.621 ms for seek start/stop, 0.379 ms per track */
	} else if (read || write) {
		d = ((long) (newBlock & 017)) - ((long) (blk & 017));
		if (d < 0)
			d += 16;
		/* d == # sectors between the last accessed sector and the new sector */
		if (d <= 1) {	/* correctly, this must be "if (d == 1)", but
			MAINDEC-08-DHRKB-E-PB test 28 (consecutive sector read all test)
			seems to have a little bug: it starts with WRITE ALL sector 36,
			then READ ALL sector 0 (two sectors distance) and often fails
			immediately with Transfer Done not set. The test should start
			with sector 37. */
			if (all) {	/* read or write ALL can read consecutive sectors */
				d = 1;
			} else {	/* normal read can't read consecutive sectors */
				if (d == 1)
					d = 16;
			}
		}
		d *= 2500;	/* 2.5 ms per sector, 40 ms per revolution */
	} else
		d = 0;
	durationMicroSeconds = d;
	cmd |= RK05_DELAY;
	blk = newBlock;		/* DMAN may destroy rk8e.block */
	if (read)
		cmd |= RK05_INPUT_BLOCK | RK05_RAISE_FLAG;
	if (write)
		cmd |= RK05_OUTPUT_BLOCK | RK05_RAISE_FLAG;
}


- (void) start
{
	NSDate *delayUntil = [[NSDate alloc] initWithTimeIntervalSinceNow:0.005];	// 5 ms
	startAtMachAbsolute = mach_absolute_time();
	if ([commandsLock lockWhenCondition:NO_COMMANDS_AVAILABLE beforeDate:delayUntil]) {
		[commandsLock unlockWithCondition:COMMANDS_AVAILABLE];
	}
	[delayUntil release];	// don't use autorelease, this floods the PDP-8 run thread autorelease pool
}


static ushort CRC (const unsigned char *p)	/* CRC with polygon x^16 + x^15 + x^2 + 1 */
{
	register short		i, j;
	register unsigned long	data;
	register ushort		crc;

	crc = i = 0;
	while (i < 384) {
		data = p[i++] << 4;
		data |= p[i] >> 4;
		data |= p[i++] << 20;
		data |= p[i++] << 12;
		for (j = 0; j < 24; j++) {
			if ((data ^ crc) & 0x1)
				crc = (crc >> 1) ^ 0xa001;
			else
				crc >>= 1;
			data >>= 1;
		}
	}
	return (crc);
}


- (void) processCommand
{
	register ushort		*field;
	register unsigned char	*bp;
	register ushort		i;
	register ushort		words;
	unsigned short		rk8eBlock;
	unsigned short		rk8eCommand;
	unsigned short		clear, set;

	/* after processing a command, return, so that the next call can process higher priority
	   commands that may have been added in the meantime by the RK8-E control */
	if (cmd & RK05_FLUSH_CYL) {
		[rk8e  lockControl];
		cmd &= ~RK05_FLUSH_CYL;
		if (driveNumber == [rk8e getCurrentDriveNumber])
			[rk8e setStatusBits:STATUS_HEAD_IN_MOTION clearStatusBits:0];
		cmd |= RK05_SEEK;
		if (fseek(decpack, cyl * RK05_BUFSIZE, SEEK_SET) < 0)
			[rk8e setStatusBits:STATUS_SEEK_FAILED clearStatusBits:0];
		else if (fwrite(buffer, 1, RK05_BUFSIZE, decpack) != RK05_BUFSIZE)
			[rk8e setStatusBits:STATUS_ERROR clearStatusBits:0];
		else
			dirty = 0;
		[rk8e unlockControl];
		return;
	}
	if (cmd & RK05_READ_CYL) {
		[rk8e lockControl];
		cmd &= ~RK05_READ_CYL;
		if (cyl != newcyl) {
			if (driveNumber == [rk8e getCurrentDriveNumber])
				[rk8e setStatusBits:STATUS_HEAD_IN_MOTION clearStatusBits:0];
			cmd |= RK05_SEEK;
			cyl = newcyl;
			if (fseek(decpack, 0, SEEK_END) < 0)
				[rk8e setStatusBits:STATUS_SEEK_FAILED clearStatusBits:0];
			else if (ftell(decpack) < (cyl + 1) * RK05_BUFSIZE)
				bzero (buffer, RK05_BUFSIZE);
			else if (fseek(decpack, cyl * RK05_BUFSIZE, SEEK_SET) < 0)
				[rk8e setStatusBits:STATUS_SEEK_FAILED clearStatusBits:0];
			else if (fread(buffer, 1, RK05_BUFSIZE, decpack) != RK05_BUFSIZE)
				[rk8e setStatusBits:STATUS_ERROR clearStatusBits:0];
		}
		[rk8e unlockControl];
		return;
	}
	if (cmd & RK05_INPUT_BLOCK) {
		[rk8e lockControl];
		if (! [rk8e isCRCOK])
			[rk8e setStatusBits:STATUS_CYLINDER_ADDRESS_ERROR clearStatusBits:0];
/*
 *	Read a sector from the RK8E disk file.  The file packing scheme:
 *		<- 8 bits->
 *		+---------+	0H => High order of pdp8 word 0
 *		|   0H    |	0L => Low order of pdp8 word 0
 *		+----+----+	1H => High order of pdp8 word 1
 *		| 0L | 1H |	1L => Low order of pdp8 word 1
 *		+----+----+
 *		|   1L    |	In other words, pdp8 words are written
 *		+----+----+	in bit order (high to low).
 */
 		rk8eBlock = [rk8e getBlockNumber];
		bp = buffer + (rk8eBlock & 037) * 384;
		[rk8e setCRC:CRC(bp)];
		rk8eCommand = [rk8e getCommand];
		words = (rk8eCommand & COMMAND_HALF_BLOCK) ? 64 : 128;	/* half of words to transfer */
		field = [pdp8 directMemoryAccess] + ((rk8eCommand & 070) << 9);
		i = [rk8e getCurrentAddress];
		if (field < [pdp8 directMemoryAccess] + [pdp8 memorySize]) {
			while (words--) {	/* two words per loop */
				field[i & 07777] = *bp++ << 4;
				field[i++ & 07777] |= *bp >> 4;
				field[i & 07777] = (*bp++ & 017) << 8;
				field[i++ & 07777] |= *bp++;
			}
			[pdp8 directMemoryWriteFinished];
		}
		words = (rk8eCommand & COMMAND_HALF_BLOCK) ? 128 : 256;
		[rk8e setCurrentAddress:([rk8e getCurrentAddress] + words) & 07777];
		cmd &= ~RK05_INPUT_BLOCK;
		if (crcstate[rk8eBlock >> 3] & (1 << (rk8eBlock & 7)))
			[rk8e setStatusBits:STATUS_CRC_ERROR clearStatusBits:0];
		[rk8e unlockControl];
		return;
	}
	if (cmd & RK05_OUTPUT_BLOCK) {
		[rk8e lockControl];
		if (! [rk8e isCRCOK])
			[rk8e setStatusBits:STATUS_CYLINDER_ADDRESS_ERROR clearStatusBits:0];
		rk8eBlock = [rk8e getBlockNumber];
		bp = (unsigned char *) buffer + (rk8eBlock & 037) * 384;
		rk8eCommand = [rk8e getCommand];
		words = (rk8eCommand & COMMAND_HALF_BLOCK) ? 64 : 128;	/* half of words to transfer */
		field = [pdp8 directMemoryAccess] + ((rk8eCommand & 070) << 9);
		i = [rk8e getCurrentAddress];
		while (words--) {	/* two words per loop */
			*bp++ = field[i & 07777] >> 4;
			*bp = field[i++ & 07777] << 4;
			*bp++ |= field[i & 07777] >> 8;
			*bp++ = field[i++ & 07777];
		}
		if (rk8eCommand & COMMAND_HALF_BLOCK) {
			for (i = 0; i < 192; i++)
				*bp++ = 0;
		}
		[rk8e setCRC:CRC(bp - 384)];
		words = (rk8eCommand & COMMAND_HALF_BLOCK) ? 128 : 256;
		[rk8e setCurrentAddress:([rk8e getCurrentAddress] + words) & 07777];
		dirty = true;
		cmd &= ~RK05_OUTPUT_BLOCK;
		cmd |= RK05_SET_CRC_STATE;
		[rk8e unlockControl];
		return;
	}
	if (cmd & RK05_DELAY) {
		// don't lock the control
		cmd &= ~RK05_DELAY;
		int speed = [pdp8 getGoSpeed];
		if (speed != GO_AS_FAST_AS_POSSIBLE) {
			uint64_t delayUntilAbsolute = startAtMachAbsolute +
				nanoseconds2absolute(durationMicroSeconds * 1000ll);
			if (speed == GO_WITH_PDP8_SPEED)
				mach_wait_until (delayUntilAbsolute);
			else {
				while (mach_absolute_time() < delayUntilAbsolute)
					;
			}
		}
		// don't unlock the control
		return;
	}
	if (cmd & RK05_SET_CRC_STATE) {
		[rk8e lockControl];
		cmd &= ~RK05_SET_CRC_STATE;
		words = 1 << (blk & 7);
		if ([rk8e isCRCOK])
			crcstate[blk >> 3] &= ~words;
		else
			crcstate[blk >> 3] |= words;
		[rk8e unlockControl];
		return;
	}
	if (cmd & (RK05_RECALIBRATE | RK05_SEEK | RK05_RAISE_FLAG)) {
		[rk8e lockControl];
		set = clear = 0;
		if (cmd & RK05_RAISE_FLAG) {
			set = STATUS_DONE;
			if (driveNumber == [rk8e getCurrentDriveNumber])
				clear |= STATUS_HEAD_IN_MOTION;
			if (! [rk8e isCRCOK])
				set |= STATUS_CYLINDER_ADDRESS_ERROR;
		}
		if (cmd & (RK05_RECALIBRATE | RK05_SEEK)) {
			if (driveNumber == [rk8e getCurrentDriveNumber]) {
				if ([rk8e getCommand] & COMMAND_SET_DONE_ON_SEEK_DONE)
					set = STATUS_DONE;
				clear = STATUS_HEAD_IN_MOTION;
			}
		}
		[rk8e setStatusBits:set clearStatusBits:clear];
		cmd &= ~(RK05_RAISE_FLAG | RK05_RECALIBRATE | RK05_SEEK);
		[rk8e unlockControl];
		return;
	}
}


- (void) rk05Thread:(id)object
{
	[[NSAutoreleasePool alloc] init];
	for (;;) {
		[commandsLock lockWhenCondition:COMMANDS_AVAILABLE];
		while (cmd)
			[self processCommand];
		[commandsLock unlockWithCondition:NO_COMMANDS_AVAILABLE];
	}
}


- (void) awakeFromNib
{
	commandsLock = [[NSConditionLock alloc] initWithCondition:NO_COMMANDS_AVAILABLE];
	[NSThread detachNewThreadSelector:@selector(rk05Thread:) toTarget:self withObject:nil];
}


@end
