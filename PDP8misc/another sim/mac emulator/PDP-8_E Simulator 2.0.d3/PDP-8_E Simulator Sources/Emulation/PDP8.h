/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PDP8.h - The PDP-8/E Emulator Class
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


@class BreakpointArray;


#define MEMORY_CHANGED_NOTIFICATION		@"pdp8MemoryChangedNotification"
#define PROGRAM_COUNTER_CHANGED_NOTIFICATION	@"pdp8PCChangedNotification"
#define ACCUMULATOR_CHANGED_NOTIFICATION	@"pdp8LACChangedNotification"
#define SR_CHANGED_NOTIFICATION			@"pdp8SRChangedNotification"
#define SC_CHANGED_NOTIFICATION			@"pdp8SCChangedNotification"
#define GTF_CHANGED_NOTIFICATION		@"pdp8GTFChangedNotification"
#define MQ_CHANGED_NOTIFICATION			@"pdp8MQChangedNotification"
#define EAE_MODE_CHANGED_NOTIFICATION		@"pdp8EAEModeChangedNotification"
#define DF_CHANGED_NOTIFICATION			@"pdp8DFChangedNotification"
#define UF_CHANGED_NOTIFICATION			@"pdp8UFChangedNotification"
#define UB_CHANGED_NOTIFICATION			@"pdp8UBChangedNotification"
#define SF_CHANGED_NOTIFICATION			@"pdp8SFChangedNotification"
#define ENABLE_CHANGED_NOTIFICATION		@"pdp8EnableChangedNotification"
#define DELAY_CHANGED_NOTIFICATION		@"pdp8DelayChangedNotification"
#define INHIBIT_CHANGED_NOTIFICATION		@"pdp8InhibitChangedNotification"
#define IOFLAGS_CHANGED_NOTIFICATION		@"pdp8IOFlagsChangedNotification"

#define EAE_MOUNT_NOTIFICATION			@"pdp8EAEMountNotification"
#define KM8E_MOUNT_NOTIFICATION			@"pdp8KM8EMountNotification"

#define CLEAR_ALL_FLAGS_NOTIFICATION		@"pdp8ClearAllFlagsNotification"

#define PDP8_STEP_NOTIFICATION			@"pdp8StepNotification"
#define PDP8_TRACE_NOTIFICATION			@"pdp8TraceNotification"
#define PDP8_GO_NOTIFICATION			@"pdp8GoNotification"
#define PDP8_STOP_NOTIFICATION			@"pdp8StopNotification"


#define PDP8_FIELDSIZE	0010000			/* size of a 4K PDP-8 memory field */
#define PDP8_MEMSIZE	0100000			/* maximal PDP-8 memory size is 32K */
#define PDP8_IOADDRS	0100			/* number of I/O addresses: 6xx? */

#define EAE_MODE_A	'A'			/* values for pdp8->eaeMode */
#define EAE_MODE_B	'B'

#define userFLAG	1ul			/* hard coded user mode flag (see IOFlagController) */

#define STOPPED		0			/* for state.running */
#define GOING		1
#define TRACING		2
#define RUNNING		(GOING | TRACING)

#define GO_AS_FAST_AS_POSSIBLE		0	/* for state.goSpeed, setGoSpeed: and getGoSpeed */
#define GO_WITH_PDP8_SPEED		1
#define GO_WITH_PDP8_SPEED_PRECISE	2

#define NO_STOP_ADDRESS	-1			/* for trace: and go: without stop address */


typedef unsigned long	ulong;
typedef void		(*PDP8InstructionFunctionPointer)(void);


/* Structure with information about the optional devices currently simulated */
typedef struct {
	ushort	memsize;		/* memory size in 12-bit words - Data Break I/O devices must know
					   this value to avoid writing into non-existing PDP-8 memory */
	BOOL	hasEAE;			/* the simulated hardware has an EAE */
	BOOL	hasKM8E;		/* the simulated hardware has an KM8-E */
	BOOL	hasKM8Etimesharing;	/* the time sharing jumper of the KM8-E is present */
}	HW;


/* Registers of the TSC8-75 board are kept within the PDP-8 class */
typedef struct {
	ulong	flag;		/* interrupt mask of the TSC8-75 (0 when TSC8-75 not present) */
	ushort	eriot;		/* last instruction (IOT, JMP, JMS) that caused an user mode trap */
	ushort	ertb;		/* address of last JMP or JMS that caused an user mode trap */
	ushort	ecdf;		/* set when a CDF is executed in user mode, cleared by ECDF or ESME */
	ushort	esmeEnabled;	/* TSC8-75 supports the ESME mode, i. e. the IOT 6365 */
}	TSC8_75;


/* Structure with information about the internal state of the PDP-8/E emulation */
typedef struct {
	ushort	running;	/* STOPPED, TRACING or GOING */
	BOOL	halted;		/* YES iff the HALT or SINGSTEP key of the KC8-EA console is pressed */
	ushort	currInst;	/* code of current PDP-8 instruction, the functions */
				/* implementing the PDP-8 instructions can inspect this */
	ulong	executionTime;	/* every PDP-8 instruction must add to this value the execution */
				/* time (in 0.1 microseconds) of the instruction */
				/* for IOTs of I/O devices implemented by plugins these are: */
				/* 12 (1.2 microseconds) for IOTs of devices directly attached to */
				/* the OMNIBUS; */
				/* for devices attached via the KA8-E Positive I/O Bus Interface: */
				/* 12 (1.2 microseconds) for IOTs ending in 0 */
				/* 26 (2.6 microseconds) for IOTs ending in 1, 2 or 4 */
				/* 36 (3.6 microseconds) for IOTs ending in 3, 5 or 6 */
				/* 46 (4.6 microseconds) for IOTs ending in 7 */
				/* use the macro EXECUTION_TIME() for this purpose */
	uint64_t absoluteTime;	/* Mach kernel absolut time corresponding to PDP-8 executionTime */
	int	goSpeed;	/* GO_AS_FAST_AS_POSSIBLE, GO_WITH_PDP8_SPEED, GO_WITH_PDP8_SPEED_PRECISE */
	ulong	usecTraceDelay;	/* delay (in microseconds) between two instructions in trace mode */
	void	*pluginPointer[PDP8_IOADDRS];	/* for each I/O address, a pointer pointing to the plugin */
}	STATE;


@interface PDP8 : NSObject <NSCoding> {
@public
/* The attributes are public, so the C functions implementing the PDP-8 instructions can
   access them directly. No other Cocoa code should use them directly. To ensure this,
   the register names are mapped to dummy names with the #defines below. In the source
   codes files with the instruction C functions, #define USE_PDP8_REGISTERS_DIRECTLY
   to make the registers available. */
   
/* Registers of the PDP-8/E CPU */
	ushort	SR;
	ushort	AC;		/* including L flag */
	ushort	PC;
	ushort	SC;
	ushort	MQ;
	ushort	GTF;
	ushort	IF;		/* W: for write access to nonexisting memory */
	ushort	IB, W_IB;	/* W_IB == IB when memory exists, */
	ushort	DF, W_DF;	/* W_IB == 0100000 when memory does not exist */
					/* write accesses to current IF are only by JMS, */
					/* but then IB is used, so no W_IF needed. */
					/* dto. for DF, W_DF */
					/* field registers are 0n0000 for field n (0 <= n <= 7) */
	ushort	UF;		
	ushort	UB;		/* UF, UB is 000000 or 010000 */
	ushort	SF;
	ushort	IENABLE;
	ushort	IINHIBIT;
	ushort	IDELAY;
	ulong	IMASK;
	ulong	IOFLAGS;
	char	eaeMode;	/* 'A' - Mode A, 'B' - Mode B */
	ushort	mem[PDP8_MEMSIZE + PDP8_FIELDSIZE];	/* 9th field for non-existing core */

/* additional attributes */
	TSC8_75	_tsc8;
	HW	_hw;
	STATE	_state;
	
@private
	IBOutlet BreakpointArray	*breakpoints;
	IBOutlet BreakpointArray	*breakopcodes;
	char				bp[PDP8_MEMSIZE];
	NSMutableDictionary		*saveopcodes;
}


- (void) mountEAE:(BOOL)mount;		// mount or unmount
- (BOOL) hasEAE;

- (void) mountKM8E:(BOOL)mount memorySize:(unsigned)memsize timesharingEnabled:(BOOL)timesharing;
- (BOOL) hasKM8E;
- (BOOL) isTimesharingEnabled;
- (ushort) memorySize;			// memory size in 12-bit words, e. g. 010000 for a 4K machine

- (BOOL) isIOAddressAvailable:(int)addr;
- (void) setIOT:(NSValue *)iot forOpcode:(int)opcode;
- (void) setPluginPointer:(void *)pointer forIOAddress:(int)addr;	// see PLUGIN_POINTER macro below

- (NSString *) loadPaperTape:(NSString *)filename toField:(ushort)field;
// returns an error message; nil == everything ok

- (void) setTraceSpeed:(double)speed;	// trace with frequency of 1/speed Hz
- (void) setGoSpeed:(int)goSpeed;	// see state.goSpeed and the three GO_ macros
- (int) getGoSpeed;			// may change with a NSUserDefaultsDidChangeNotification

- (void) step;
- (void) trace:(int)stopAddress;	// stopAddress == -1 for no stop address
- (void) go:(int)stopAddress;
- (void) stop;				// stop the running or tracing PDP-8

- (void) clearAllFlags;
- (void) loadExtendedAddress;
- (void) reset;

- (BOOL) isStopped;
- (BOOL) isTracing;
- (BOOL) isGoing;
- (BOOL) isRunning;			// running in trace or go mode

- (BOOL) isHalted;			// restart is inhibited (HALT or SINGSTEP console key is up)
- (void) setHalt:(BOOL)halt;		// stop and inhibit restart (for the HALT or SINGSTEP console keys)

- (ushort) getPC;
- (void) setPC:(ushort)pc;
- (ushort) getProgramCounter;				// returns IF | PC
- (void) setProgramCounter:(ushort)programCounter;	// sets IB, IF and PC
- (ushort) getIF;
- (void) setIF:(ushort)_if;
- (ushort) getIB;
- (void) setIB:(ushort)ib;
- (ushort) getDF;
- (void) setDF:(ushort)df;
- (ushort) getUF;
- (void) setUF:(ushort)uf;
- (ushort) getUB;
- (void) setUB:(ushort)ub;
- (ushort) getSF;
- (void) setSF:(ushort)sf;
- (ushort) getSR;
- (void) setSR:(ushort)sr;
- (ushort) getL;
- (void) setL:(ushort)l;
- (ushort) getAC;
- (void) setAC:(ushort)ac;
- (ushort) getLAC;
- (void) setLAC:(ushort)lac;
- (ushort) getSC;
- (void) setSC:(ushort)sc;
- (ushort) getGTF;
- (void) setGTF:(ushort)gtf;
- (ushort) getMQ;
- (void) setMQ:(ushort)mq;
- (char) getEAEmode;
- (void) setEAEmode:(char)mode;
- (ushort) getEnable;
- (void) setEnable:(ushort)enable;
- (ushort) getDelay;
- (void) setDelay:(ushort)delay;
- (ushort) getInhibit;
- (void) setInhibit:(ushort)inhibit;
- (ulong) getInterruptMaskBits:(ulong)bitmask;
- (void) setInterruptMaskBits:(ulong)bitmask;
- (void) clearInterruptMaskBits:(ulong)bitmask;
- (ulong) getIOFlagBits:(ulong)bitmask;
- (void) setIOFlagBits:(ulong)bitmask;
- (void) clearIOFlagBits:(ulong)bitmask;
- (BOOL) interruptRequest;
- (ushort) memoryAt:(int)address;
- (ushort) memoryAtNext:(int)address;
- (ushort *) directMemoryAccess;	// pointer to the PDP-8 memory, only for Data Break I/O devices
- (void) directMemoryWriteFinished;	// call this method when the memory modifying Data Break is finished
- (void) setMemoryAtAddress:(int)address toValue:(int)value;
- (void) setMemoryAtNextAddress:(int)address toValue:(int)value;
- (void) setMemoryAtAddress:(int)address toValues:(NSArray *)values withMask:(BOOL)withMask;
- (void) clearMemory;

- (ushort) getCurrentOpcode;
- (PDP8InstructionFunctionPointer) getNextInstruction;

- (ushort) getTSC8ertb;
- (void) setTSC8ertb:(ushort)ertb;
- (ushort) getTSC8eriot;
- (void) setTSC8eriot:(ushort)eriot;
- (ushort) getTSC8ecdf;
- (void) setTSC8ecdf:(ushort)ecdf;
- (ulong) getTSC8flag;
- (void) setTSC8flag:(ulong)flag;
- (BOOL) getTSC8esmeEnabled;
- (void) setTSC8esmeEnabled:(BOOL)enabled;


#if ! defined(NS_BLOCK_ASSERTIONS)
- (id) pluginPointer:(Class)pluginClass;	// with assertion; see PLUGIN_POINTER macro below
#endif

@end


#if USE_PDP8_REGISTERS_DIRECTLY
extern PDP8	*pdp8;
#define EXECUTION_TIME(time)	(pdp8->_state.executionTime += (time))
#if defined(NS_BLOCK_ASSERTIONS)
#define PLUGIN_POINTER(plugin)	((plugin *) pdp8->_state.pluginPointer[(pdp8->_state.currInst >> 3) & 077])
#else
#define PLUGIN_POINTER(plugin)	((plugin *) [pdp8 pluginPointer:[plugin class]])
#endif
#else
#define SR		__dont_use_SR__
#define AC		__dont_use_AC__
#define PC		__dont_use_PC__
#define SC		__dont_use_SC__
#define MQ		__dont_use_MQ__
#define GTF		__dont_use_GTF__
#define IF		__dont_use_IF__
#define IB		__dont_use_IB__
#define W_IB		__dont_use_W_IB__
#define DF		__dont_use_DF__
#define W_DF		__dont_use_W_DF__
#define UF		__dont_use_UF__
#define UB		__dont_use_UB__
#define SF		__dont_use_SF__
#define IENABLE		__dont_use_IENABLE__
#define IINHIBIT	__dont_use_IINHIBIT__
#define IDELAY		__dont_use_IDELAY__
#define IMASK		__dont_use_IMASK__
#define IOFLAGS		__dont_use_IOFLAGS__
#define eaeMode		__dont_use_eaeMode__
#define mem		__dont_use_mem__
#define _tsc8		__dont_use_tsc8__
#define _hw		__dont_use_hw__
#define _state		__dont_use_state__
#endif
