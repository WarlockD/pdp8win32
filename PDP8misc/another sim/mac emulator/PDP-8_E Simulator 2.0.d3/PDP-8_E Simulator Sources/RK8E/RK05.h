/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK05.h - Class implementing a RK05 DECpack drive
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


#define WRITEPROTECT_CHANGED_NOTIFICATION	@"rk05WriteProtectChangedNotification"

#define RK05_BUFSIZE		((long) 32 * 384)	/* 32 blocks with 256 12-bit-words */
#define RK05_BLOCKS		(203 * 2 * 16)		/* 203 tracks, 2 surfaces, 16 blocks per track */


@class RK8E, PDP8;


@interface RK05 : NSObject {
@private
	IBOutlet RK8E	*rk8e;
	PDP8		*pdp8;
	short		driveNumber;
	FILE		*decpack;
	volatile unsigned short	cmd;
	short		cyl;
	BOOL		dirty;
	BOOL		locked;
	short		newcyl;
	unsigned short	blk;
	unsigned char	crcstate[RK05_BLOCKS / 8];	// for any block, a bit indicating that the CRC
							// word virtually stored on the disk is correct
	unsigned char	buffer[RK05_BUFSIZE];		// buffer for one RK05 cylinder
	unsigned long	durationMicroSeconds;		// duration of the current I/O operation
	uint64_t	startAtMachAbsolute;		// Mach absolute time when drive started working
	NSConditionLock	*commandsLock;
}

- (int) driveNumber;
- (void) setDriveNumber:(short)drvNum;
- (void) setPDP8:(PDP8 *)p8;
- (void) setWriteProtected:(BOOL)writeProtected;
- (BOOL) isWriteProtected;
- (BOOL) flush;
- (BOOL) canMount:(NSString *)path;
- (int) mount:(NSString *)path create:(BOOL)create;	// 0 = ok, -1 = can't open file, -2 = file is locked
- (BOOL) unmount;
- (BOOL) isMounted;
- (BOOL) isBusy;
- (BOOL) isCalibrating;
- (void) stopCalibrating;
- (void) abortAllCommands;
- (void) setStatusAndAbortAllCommands;
- (void) setRecalibrating;
- (void) setFlushCylinder;
- (void) setReadCylinder:(unsigned short)cylinder;
- (void) setRaiseFlag;
- (void) clearRaiseFlag;
- (void) setRead:(BOOL)read write:(BOOL)write all:(BOOL)all newBlock:(unsigned short)newBlock;
- (void) start;

@end
