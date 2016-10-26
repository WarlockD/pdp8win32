/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK8E.h - RK8-E Disk Cartridge System for the PDP-8/E Simulator
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

#import "PluginFramework/PluginAPI.h"


#define COMMAND_CHANGED_NOTIFICATION		@"rk8eCommandChangedNotification"
#define STATUS_CHANGED_NOTIFICATION		@"rk8eStatusChangedNotification"
#define BLOCKNUMBER_CHANGED_NOTIFICATION	@"rk8eBlockNumberChangedNotification"
#define MEMORYADDRESS_CHANGED_NOTIFICATION	@"rk8eMemoryAddressChangedNotification"


/* RK8-E status flags */
#define STATUS_DONE			04000
#define STATUS_HEAD_IN_MOTION		02000
#define STATUS_UNUSED			01000
#define STATUS_SEEK_FAILED		00400
#define STATUS_FILE_NOT_READY		00200
#define STATUS_CONTROL_BUSY		00100
#define STATUS_TIMING_ERROR		00040
#define STATUS_WRITE_LOCK_ERROR		00020
#define STATUS_CRC_ERROR		00010
#define STATUS_DATA_REQUEST_LATE	00004
#define STATUS_ERROR			00002
#define STATUS_CYLINDER_ADDRESS_ERROR	00001

//#define STATUS_RAISE_FLAG_MASK	04153	// This is the value from PDP-8/E Simulator 1.x. Why?
#define STATUS_RAISE_FLAG_MASK		04142


/* RK8-E command bits */
#define COMMAND_FUNCTION_MASK		07000
#define COMMAND_FUNCTION_READ		00000
#define COMMAND_FUNCTION_READ_ALL	01000
#define COMMAND_FUNCTION_LOCK		02000
#define COMMAND_FUNCTION_SEEK		03000
#define COMMAND_FUNCTION_WRITE		04000
#define COMMAND_FUNCTION_WRITE_ALL	05000
#define COMMAND_INTERRUPT_ON_DONE	00400
#define COMMAND_SET_DONE_ON_SEEK_DONE	00200
#define COMMAND_HALF_BLOCK		00100
#define COMMAND_EXTENDED_ADDRESS_MASK	00070
#define COMMAND_DRIVE_SELECT_MASK	00006
#define COMMAND_BLOCK_NUMBER_MSB	00001


@class RK05, RK05Controller;


@interface RK8E : PDP8Plugin <NSCoding>
{
@private
	IBOutlet RK05		*rk05_0;
	IBOutlet RK05		*rk05_1;
	IBOutlet RK05		*rk05_2;
	IBOutlet RK05		*rk05_3;
	IBOutlet RK05Controller	*rk05Controller_0;
	IBOutlet RK05Controller	*rk05Controller_1;
	IBOutlet RK05Controller	*rk05Controller_2;
	IBOutlet RK05Controller	*rk05Controller_3;
	NSLock			*controlLock;
@public
/* The attributes are public, so the C functions implementing the PDP-8 instructions can
   access them directly. No other Cocoa code should use them directly. To ensure this,
   the register names are mapped to dummy names with the #defines below. In the source
   codes files with the instruction C functions, #define USE_RK8E_REGISTERS_DIRECTLY
   to make the registers available. */
	RK05			*rk05[4];	// array of the four pointers above set while initialization
	unsigned short		command;
	volatile unsigned short	status;
	unsigned short		block;		// 13 bit value
	unsigned short		curaddr;
	unsigned long		ioflag;
	BOOL			maint;		// the following registers for maintenance mode
	unsigned short		ldb[4];		// lower data buffer
	unsigned char		ldbfill;	// how many lower data buffers are full
	unsigned short		udb;		// upper data buffer
	unsigned short		crc;		// checksum
	unsigned short		checkcrc;	// needed for check if DMAN can destroy the CRC
	unsigned char		shifts;		// how many shifts occured for udb or ldb
	unsigned short		wordcount;	// word count for data breaks
	BOOL			shiftEnabled;	// DMAN with AC=2000 was issued
}

- (unsigned short) getCommand;
- (void) setCommand:(unsigned short)cmd;
- (unsigned short) getStatus;
- (void) setStatus:(unsigned short)stat;
- (unsigned short) getBlockNumber;			// 13 bit value including most significant bit
- (void) setBlockNumber:(unsigned short)blk;		// from command register
- (unsigned short) getMemoryAddress;			// 15 bit value including extended address
- (void) setMemoryAddress:(unsigned short)addr;		// from command register
- (unsigned short) getCurrentAddress;
- (void) setCurrentAddress:(unsigned short)addr;

- (unsigned short) getCurrentDriveNumber;
- (void) updateStatusMountFlags;
- (void) setStatusBits:(unsigned)set clearStatusBits:(unsigned)clear;
- (void) setCRC:(unsigned)newCRC;
- (BOOL) isCRCOK;
- (void) lockControl;
- (void) unlockControl;

@end


#if ! USE_RK8E_REGISTERS_DIRECTLY
#define rk05		__dont_use_rk05__
#define command		__dont_use_command__
#define status		__dont_use_status__
#define block		__dont_use_block__
#define address		__dont_use_address__
#define curaddr		__dont_use_curraddr__
#define ioflag		__dont_use_ioflag__
#define maint		__dont_use_maint__
#define ldb		__dont_use_ldb__
#define ldbfill		__dont_use_ldbfill__
#define crc		__dont_use_crc__
#define shifts		__dont_use_shifts__
#define wordcount	__dont_use_wordcount__
#define checkcrc	__dont_use_checkcrc__
#define shiftEnabled	__dont_use_shiftEnabled__
#endif
