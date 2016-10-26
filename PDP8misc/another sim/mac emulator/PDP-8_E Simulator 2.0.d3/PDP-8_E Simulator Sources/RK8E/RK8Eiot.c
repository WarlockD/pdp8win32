/*
 *	PDP-8/E Simulator Source Code
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	RK8Eiot.h - IOTs for the RK8-E plugin
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
#define USE_PDP8_REGISTERS_DIRECTLY 1

#include "PluginFramework/PDP8.h"

#include "RK8Eiot.h"
#include "RK8E.h"
#include "RK05.h"


void i6741 (void)				/* DSKP		6741	*/
{
	RK8E *rk8e = PLUGIN_POINTER(RK8E);
	[rk8e lockControl];
	if (pdp8->IOFLAGS & rk8e->ioflag)
		pdp8->PC++;

	[rk8e unlockControl];
	EXECUTION_TIME (26);
}


unsigned s6741 (void)				/* DSKP		6741	skiptest */
{
	return pdp8->IOFLAGS & PLUGIN_POINTER(RK8E)->ioflag;
}


void i6742 (void)				/* DCLR		6742	*/
{
	RK05 *pack;
	
	RK8E *rk8e = PLUGIN_POINTER(RK8E);
	[rk8e lockControl];
	rk8e->status = 0;
	switch (pdp8->AC & 3) {
	case 03 :
		/* Unlock the drive - else MAINDEC-08-DHRKA-B-PB (diskless control test)
		   fails on second pass at test 15 because the drives are write protected.
		   (What happens with a real RK8-E in this case?)
		if (! rk8e.pack[(pdp8.AC & 06) >> 1].pb.ioRefNum)
			rk8e.pack[(pdp8.AC & 06) >> 1].locked = false;
		*/
		/* undefined 03 does the same as 00 - fall through */
	case 00 :
		pdp8->AC &= 010000 ;
		pdp8->IOFLAGS &= ~rk8e->ioflag;
		pack = rk8e->rk05[(rk8e->command >> 1) & 3];
		if (! [pack isMounted])
			rk8e->status |= STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY;
		if ([pack isCalibrating]) {
			rk8e->status |= STATUS_CONTROL_BUSY | STATUS_ERROR;
			pdp8->IOFLAGS |= rk8e->ioflag;
		}
		break;
	case 01 :
		pdp8->AC &= 010000;
		pdp8->IMASK &= ~rk8e->ioflag;
		pdp8->IOFLAGS &= ~rk8e->ioflag;
		rk8e->command = 0;
		rk8e->block &= ~010000;	/* CRC is not affected */
		rk8e->curaddr = 0;
		[rk8e->rk05[0] abortAllCommands];
		[rk8e->rk05[1] abortAllCommands];
		[rk8e->rk05[2] abortAllCommands];
		[rk8e->rk05[3] abortAllCommands];
		rk8e->maint = false;
		rk8e->ldbfill = 0;
		rk8e->ldb[0] = 0;
		rk8e->ldb[1] = 0;
		rk8e->ldb[2] = 0;
		rk8e->ldb[3] = 0;
		rk8e->udb = 0;
		rk8e->wordcount = 0;
		rk8e->shifts = 0;
		if (! [rk8e->rk05[0] isMounted])
			rk8e->status |= STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY;
		break;
	case 02 :	/* recalibrate to track 0 */
		pdp8->AC &= 010000;
		pack = rk8e->rk05[(rk8e->command >> 1) & 3];
		BOOL wasCalibrating = [pack isCalibrating];
		[pack setRecalibrating];
		if ([pack isMounted]) {
			[pack setFlushCylinder];
			[pack setReadCylinder:0];
			rk8e->status |= STATUS_DONE;
			pdp8->IOFLAGS |= rk8e->ioflag;
			if ([pack isBusy]) {
				[pack setRaiseFlag];
				[pack start];
			}
		} else {
			rk8e->status |= STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY | STATUS_ERROR;
			if (wasCalibrating)
				rk8e->status |= STATUS_CONTROL_BUSY;
			if (! rk8e->maint)
				pdp8->IOFLAGS |= rk8e->ioflag;
			[pack start];
		}
		break ;
	}
	[rk8e unlockControl];
	EXECUTION_TIME (26);
}


void i6743 (void)				/* DLAG		6743	*/
{
	RK8E *rk8e = PLUGIN_POINTER(RK8E);
	[rk8e lockControl];
	rk8e->wordcount = (rk8e->command & COMMAND_HALF_BLOCK) ? 128 : 0;
	RK05 *pack = rk8e->rk05[(rk8e->command >> 1) & 3];
	if ((! [pack isWriteProtected] || rk8e->maint) && ! [pack isCalibrating]) {
		rk8e->block = ((rk8e->command & 1) << 12) | (pdp8->AC & 07777);
		rk8e->crc = rk8e->checkcrc = rk8e->block & 017740;
		/* cylinder in 16 bit format 000xxxxxxxx00000*/
	}
	pdp8->AC &= 010000;
	[pack setStatusAndAbortAllCommands];
	switch (rk8e->command & 07000) {
	case 00000 :	/* Read Data */
		[pack setRead:YES write:NO all:NO newBlock:rk8e->block];
		break;
	case 01000 :	/* Read All */
		[pack setRead:YES write:NO all:YES newBlock:rk8e->block];
		break ;
	case 02000 :	/* Set Write Protect */
		[pack setWriteProtected:YES];
		break ;
	case 03000 :	/* Seek Only */
		[pack setRead:NO write:NO all:NO newBlock:rk8e->block];
		break ;
	case 04000 :	/* Write Data */
		[pack setRead:NO write:YES all:NO newBlock:rk8e->block];
		break;
	case 05000 :	/* Write All */
		[pack setRead:NO write:YES all:YES newBlock:rk8e->block];
		break;
	case 06000 :	/* unused */
	case 07000 :	/* unused */
		pdp8->IOFLAGS |= rk8e->ioflag;
		rk8e->status |= STATUS_DONE | STATUS_TIMING_ERROR;
		break;
	}
	if ([pack isMounted])
		[pack start];
	else {
		[pack abortAllCommands];
		rk8e->status |= STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY | STATUS_ERROR;
		pdp8->IOFLAGS |= rk8e->ioflag;
	}
	[rk8e unlockControl];
	EXECUTION_TIME (36);
}


void i6744 (void)				/* DLCA		6744	*/
{
	RK8E *rk8e = PLUGIN_POINTER(RK8E);
	[rk8e lockControl];
	rk8e->curaddr = pdp8->AC & 07777;
	pdp8->AC &= 010000;
	if ([rk8e->rk05[(rk8e->command >> 1) & 3] isCalibrating]) {
		rk8e->status |= STATUS_CONTROL_BUSY;
		pdp8->IOFLAGS |= rk8e->ioflag;
	}
	[rk8e unlockControl];
	EXECUTION_TIME (26);
}


void i6745 (void)				/* DRST		6745	*/
{
	RK8E *rk8e = PLUGIN_POINTER(RK8E);
	[rk8e lockControl];
	pdp8->AC = (pdp8->AC & 010000) | rk8e->status;
	[rk8e unlockControl];
	EXECUTION_TIME (36);
}


void i6746 (void)				/* DLDC		6746	*/
{
	RK8E *rk8e = PLUGIN_POINTER(RK8E);
	[rk8e lockControl];
	rk8e->status = 0;
	pdp8->IOFLAGS &= ~rk8e->ioflag;
	RK05 *pack = rk8e->rk05[(rk8e->command >> 1) & 3];
	if (! rk8e->maint || ! [pack isCalibrating]) {
		rk8e->command = pdp8->AC & 07777;
		if (rk8e->command & 1)
			rk8e->block |= 010000;	/* CRC is not affected */
		pack = rk8e->rk05[(rk8e->command >> 1) & 3];
	}
	pdp8->AC &= 010000;
	if ([pack isMounted]) {
		if ([pack isBusy])
			rk8e->status |= STATUS_HEAD_IN_MOTION;	/* still seeking */
		// detected by Clang: rk8e->status is already 0
		// else
		//	rk8e->status &= ~STATUS_HEAD_IN_MOTION;	/* seek already done */
		if ((rk8e->command & COMMAND_SET_DONE_ON_SEEK_DONE)) {
			if (! [pack isBusy]) {
				rk8e->status |= STATUS_DONE;
				pdp8->IOFLAGS |= rk8e->ioflag;
			}
		} else
			[pack clearRaiseFlag];
	} else {
		rk8e->status = STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY;
		if ([pack isCalibrating]) {
			rk8e->status |= STATUS_CONTROL_BUSY | STATUS_ERROR;
			pdp8->IOFLAGS |= rk8e->ioflag;
		}
	}
	if (rk8e->command & COMMAND_INTERRUPT_ON_DONE)
		pdp8->IMASK |= rk8e->ioflag;
	else
		pdp8->IMASK &= ~rk8e->ioflag;
	[rk8e unlockControl];
	EXECUTION_TIME (36);
}


void i6747 (void)				/* DMAN		6747	*/
{
	RK8E *rk8e = PLUGIN_POINTER(RK8E);
	[rk8e lockControl];
	RK05 *pack = rk8e->rk05[(rk8e->command >> 1) & 3];
	if (pdp8->AC & 04000) {
		/* enter maintenance mode */
		rk8e->maint = true;
		rk8e->shifts = 0;
		rk8e->shiftEnabled = false;
		if (! (rk8e->status & ~(STATUS_HEAD_IN_MOTION | STATUS_FILE_NOT_READY | STATUS_ERROR)))
			pdp8->IOFLAGS &= ~rk8e->ioflag;
	}
	if (rk8e->maint) {
		switch (pdp8->AC & 07760) {
		case 02000 :
			/* enable to shift to lower data buffer */
			rk8e->shiftEnabled = true;
			break;
		case 01200 :
			/* combination of 01000 and 00200 where the bit shifted into lower data
			   buffer is the logical OR of the bits from CRC and surface & sector */
			rk8e->ldb[0] = ((rk8e->block << 11) | (rk8e->ldb[0] >> 1)) & 07777;
			rk8e->crc = ((pdp8->AC & 2) << 14) | (rk8e->crc >> 1);
			rk8e->block = (rk8e->crc & 017740) | ((rk8e->block >> 1) & 037);
			if (++rk8e->shifts == 12 && rk8e->ldbfill == 0) {
				rk8e->ldbfill = 1;
				rk8e->shifts = 0;
			}
			break;
		case 01000 :
			/* shift CRC to lower data buffer and AC10 to CRC */
			rk8e->ldb[0] = ((rk8e->crc << 11) | (rk8e->ldb[0] >> 1)) & 07777;
			rk8e->crc = ((pdp8->AC & 2) << 14) | (rk8e->crc >> 1);
			rk8e->block = (rk8e->crc & 017740) | (rk8e->block & 037);
			if (++rk8e->shifts == 12 && rk8e->ldbfill == 0) {
				rk8e->ldbfill = 1;
				rk8e->shifts = 0;
			}
			if (! rk8e->shiftEnabled && ! rk8e->ldbfill) {
				rk8e->status |= STATUS_DATA_REQUEST_LATE;
				pdp8->IOFLAGS |= rk8e->ioflag;
			}
			if ([pack isCalibrating]) {
				rk8e->status |= STATUS_DONE;
				pdp8->IOFLAGS |= rk8e->ioflag;
			}
			break;
		case 00400 :
			/* shift command register to lower data buffer */
			rk8e->ldb[0] = ((rk8e->ldb[0] << 1) | (rk8e->command >> 11)) & 0177777;
			rk8e->command = (rk8e->command << 1) & 07777;
			rk8e->block &= ~010000;
			rk8e->crc &= ~010000;
			if (rk8e->command & COMMAND_INTERRUPT_ON_DONE)
				pdp8->IMASK |= rk8e->ioflag;
			else
				pdp8->IMASK &= ~rk8e->ioflag;
			if (++rk8e->shifts == 12 && rk8e->ldbfill == 0)
				rk8e->ldbfill = 1;
			break;
		case 00200 :
			/* shift surface and sector to lower data buffer */
			rk8e->ldb[0] = ((rk8e->block << 11) | (rk8e->ldb[0] >> 1)) & 07777;
			rk8e->block = (rk8e->block & 07740) | ((rk8e->block & 037) >> 1);
			if (++rk8e->shifts == 12 && rk8e->ldbfill == 0) {
				rk8e->ldbfill = 1;
				rk8e->shifts = 0;
			}
			break;
		case 00100 :
			/* shift AC10 to upper data buffer, buffer should sink in the silo when full */
			if (rk8e->ldbfill < 4 && rk8e->wordcount < 256) {
				rk8e->udb = ((pdp8->AC & 2) << 10) | (rk8e->udb >> 1);
				if (++rk8e->shifts == 12) {
					if ((rk8e->command & COMMAND_HALF_BLOCK) && rk8e->wordcount >= 128)
						rk8e->udb = 0;
					rk8e->ldb[3] = rk8e->ldb[2];
					rk8e->ldb[2] = rk8e->ldb[1];
					rk8e->ldb[1] = rk8e->ldb[0];
					rk8e->ldb[0] = rk8e->udb;
					rk8e->ldbfill++;
					rk8e->wordcount++;
					rk8e->shifts = 0;
				}
				if (((rk8e->command & COMMAND_HALF_BLOCK) && rk8e->wordcount == 128 &&
					rk8e->shifts == 0)
					|| (rk8e->wordcount == 255 && rk8e->shifts == 11)) {
					rk8e->status |= STATUS_DONE;
					pdp8->IOFLAGS |= rk8e->ioflag;
				}
			} else {
				rk8e->status |= STATUS_DATA_REQUEST_LATE;
				pdp8->IOFLAGS |= rk8e->ioflag;
			}
			break;
		case 00040 :
			/* single cycle data break */
			if ((rk8e->command & 07000) == 04000 || (rk8e->command & 07000) == 05000) {
				/* write */
				if (rk8e->wordcount < 256 && rk8e->ldbfill < 4) {
					rk8e->ldb[3] = rk8e->ldb[2];
					rk8e->ldb[2] = rk8e->ldb[1];
					rk8e->ldb[1] = rk8e->ldb[0];
					rk8e->ldb[0] =
						pdp8->mem[((rk8e->command & 070) << 9) | rk8e->curaddr];
					rk8e->ldbfill++;
					rk8e->wordcount++;
				}
			} else {	/* read */
				if (((rk8e->command & 070) << 9) < pdp8->_hw.memsize) {
					pdp8->mem[((rk8e->command & 070) << 9) | rk8e->curaddr] =
						(rk8e->ldbfill > 0) ? rk8e->ldb[--rk8e->ldbfill] : 0;
				}
			}
			rk8e->curaddr = (rk8e->curaddr + 1) & 07777;
			break;
		case 04020 :
		case 00020 :
			/* read lower data buffer into AC */
			if (rk8e->ldbfill > 0)
				pdp8->AC = (pdp8->AC & 010000) | (rk8e->ldb[--rk8e->ldbfill] & 07777);
			else
				pdp8->AC &= 010000;
			break;
		}
	}
	[pack stopCalibrating];
	[rk8e unlockControl];
	EXECUTION_TIME (46);
}
