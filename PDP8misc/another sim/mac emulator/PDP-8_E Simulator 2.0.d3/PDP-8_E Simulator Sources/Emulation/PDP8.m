/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	PDP8.m - The PDP-8/E Emulator Class
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


#include <zlib.h>

#define USE_PDP8_REGISTERS_DIRECTLY	1

#import "PDP8.h"
#import "itab.h"
#import "eae.h"
#import "iot.h"
#import "opr.h"
#import "Breakpoint.h"
#import "BreakpointArray.h"
#import "Utilities.h"
#import "PluginAPI.h"


#define PDP8_PREFS_KEY		@"PDP-8/E"

#define CODER_KEY_HAS_EAE	@"EAE"
#define CODER_KEY_HAS_KM8E	@"KM8E"
#define CODER_KEY_MEMSIZE	@"MEMSIZE"
#define CODER_KEY_TIMESHARING	@"TIMESHARING"
#define CODER_KEY_SR		@"SR"
#define CODER_KEY_LAC		@"LAC"
#define CODER_KEY_PC		@"PC"
#define CODER_KEY_SC		@"SC"
#define CODER_KEY_MQ		@"MQ"
#define CODER_KEY_GTF		@"GTF"
#define CODER_KEY_IF		@"IF"
#define CODER_KEY_IB		@"IB"
#define CODER_KEY_DF		@"DF"
#define CODER_KEY_UF		@"UF"
#define CODER_KEY_UB		@"UB"
#define CODER_KEY_SF		@"SF"
#define CODER_KEY_ENABLE	@"ENABLE"
#define CODER_KEY_INHIBIT	@"INHIBIT"
#define CODER_KEY_DELAY		@"DELAY"
#define CODER_KEY_USERMASK	@"USERMASK"
#define CODER_KEY_USERFLAG	@"USERFLAG"
#define CODER_KEY_EAEMODE	@"EAEMODE"
#define CODER_KEY_MEMORY	@"MEMORY"


PDP8	*pdp8;	/* the one and only instance of the PDP8 class, only used by the opcode C functions */


@implementation PDP8


#pragma mark Initialization


- (void) notifyApplicationWillFinishLaunching:(NSNotification *)notification
{
	// NSLog (@"PDP8 notifyApplicationWillFinishLaunching");
	NSData *data = [[NSUserDefaults standardUserDefaults] dataForKey:PDP8_PREFS_KEY];
	if (data) {
		NSKeyedUnarchiver *unarchiver = [[NSKeyedUnarchiver alloc] initForReadingWithData:data];
		[self initWithCoder:unarchiver];
		[unarchiver finishDecoding];
		[unarchiver release];
	} else {
		[self mountEAE:NO];
		[self mountKM8E:NO memorySize:PDP8_FIELDSIZE timesharingEnabled:NO];
		[self reset];
	}
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	// NSLog (@"PDP8 notifyApplicationWillTerminate");
	NSMutableData *data = [NSMutableData data];
	NSKeyedArchiver *archiver = [[NSKeyedArchiver alloc] initForWritingWithMutableData:data];
	[self encodeWithCoder:archiver];
	[archiver finishEncoding];
	[archiver release];
	[[NSUserDefaults standardUserDefaults] setObject:data forKey:PDP8_PREFS_KEY];
}


- (void) awakeFromNib
{
	pdp8 = self;
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyApplicationWillFinishLaunching:)
		name:NSApplicationWillFinishLaunchingNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil]; 
}


- (id) initWithCoder:(NSCoder *)coder
{
	self = [super init];
	// hardware configuration - note: redundant to prefs settings
	[self mountEAE:[coder decodeBoolForKey:CODER_KEY_HAS_EAE]];
	[self mountKM8E:[coder decodeBoolForKey:CODER_KEY_HAS_KM8E]
		memorySize:[coder decodeIntForKey:CODER_KEY_MEMSIZE]
		timesharingEnabled:[coder decodeBoolForKey:CODER_KEY_TIMESHARING]];
	// registers
	[self setSR:[coder decodeIntForKey:CODER_KEY_SR]];
	[self setLAC:[coder decodeIntForKey:CODER_KEY_LAC]];
	[self setPC:[coder decodeIntForKey:CODER_KEY_PC]];
	[self setSC:[coder decodeIntForKey:CODER_KEY_SC]];
	[self setMQ:[coder decodeIntForKey:CODER_KEY_MQ]];
	[self setGTF:[coder decodeIntForKey:CODER_KEY_GTF]];
	[self setIF:[coder decodeIntForKey:CODER_KEY_IF]];
	[self setIB:[coder decodeIntForKey:CODER_KEY_IB]];
	[self setDF:[coder decodeIntForKey:CODER_KEY_DF]];
	[self setUF:[coder decodeIntForKey:CODER_KEY_UF]];
	[self setUB:[coder decodeIntForKey:CODER_KEY_UB]];
	[self setSF:[coder decodeIntForKey:CODER_KEY_SF]];
	[self setEnable:[coder decodeIntForKey:CODER_KEY_ENABLE]];
	[self setInhibit:[coder decodeIntForKey:CODER_KEY_INHIBIT]];
	[self setDelay:[coder decodeIntForKey:CODER_KEY_DELAY]];
	[coder decodeBoolForKey:CODER_KEY_USERMASK] ?
		[self setInterruptMaskBits:userFLAG] : [self clearInterruptMaskBits:userFLAG];
	[coder decodeBoolForKey:CODER_KEY_USERFLAG] ?
		[self setIOFlagBits:userFLAG] : [self clearIOFlagBits:userFLAG];
	[self setEAEmode:[coder decodeIntForKey:CODER_KEY_EAEMODE]];
	// memory
	NSData *data = [coder decodeObjectForKey:CODER_KEY_MEMORY];
	unsigned long memsize = _hw.memsize * sizeof(mem[0]);
	int err = uncompress((void *) mem, &memsize, [data bytes], [data length]);
	if (err)
		NSLog (@"PDP8 decodeWithCoder memory uncompress error %d", err);
	return self;
}


- (void) encodeWithCoder:(NSCoder *)coder
{
	// hardware configuration - note: redundant to prefs settings
	[coder encodeBool:[self hasEAE] forKey:CODER_KEY_HAS_EAE];
	[coder encodeBool:[self hasKM8E] forKey:CODER_KEY_HAS_KM8E];
	[coder encodeInt:[self memorySize] forKey:CODER_KEY_MEMSIZE];
	[coder encodeBool:[self isTimesharingEnabled] forKey:CODER_KEY_TIMESHARING];
	// registers
	[coder encodeInt:[self getSR] forKey:CODER_KEY_SR];
	[coder encodeInt:[self getLAC] forKey:CODER_KEY_LAC];
	[coder encodeInt:[self getPC] forKey:CODER_KEY_PC];
	[coder encodeInt:[self getSC] forKey:CODER_KEY_SC];
	[coder encodeInt:[self getMQ] forKey:CODER_KEY_MQ];
	[coder encodeInt:[self getGTF] forKey:CODER_KEY_GTF];
	[coder encodeInt:[self getIF] forKey:CODER_KEY_IF];
	[coder encodeInt:[self getIB] forKey:CODER_KEY_IB];
	[coder encodeInt:[self getDF] forKey:CODER_KEY_DF];
	[coder encodeInt:[self getUF] forKey:CODER_KEY_UF];
	[coder encodeInt:[self getUB] forKey:CODER_KEY_UB];
	[coder encodeInt:[self getSF] forKey:CODER_KEY_SF];
	[coder encodeInt:[self getEnable] forKey:CODER_KEY_ENABLE];
	[coder encodeInt:[self getInhibit] forKey:CODER_KEY_INHIBIT];
	[coder encodeInt:[self getDelay] forKey:CODER_KEY_DELAY];
	[coder encodeBool:[self getInterruptMaskBits:userFLAG] ? YES : NO forKey:CODER_KEY_USERMASK];
	[coder encodeBool:[self getIOFlagBits:userFLAG] ? YES : NO forKey:CODER_KEY_USERFLAG];
	[coder encodeInt:[self getEAEmode] forKey:CODER_KEY_EAEMODE];
	// memory
	unsigned long datasize = compressBound(_hw.memsize * sizeof(mem[0]));
	NSMutableData *data = [NSMutableData dataWithLength:datasize];
	int err = compress2([data mutableBytes], &datasize, (void *) mem,
		_hw.memsize * sizeof(mem[0]), Z_BEST_COMPRESSION);
	if (err)
		NSLog (@"PDP8 encodeWithCoder memory compress2 error %d", err);
	[data setLength:datasize];
	[coder encodeObject:data forKey:CODER_KEY_MEMORY];
}


#pragma mark Hardware Configuration


- (void) mountEAE:(BOOL)mount
{
	static void (*eaetab[])(void) = {
		i7401, i7403, i7405, i7407, i7411, i7413, i7415, i7417,
		i7421, i7423, i7425, i7427, i7431, i7433, i7435, i7437,
		i7441, i7443, i7445, i7447, i7451, i7453, i7455, i7457,
		i7461, i7463, i7465, i7467, i7471, i7473, i7475, i7477,
		i7501, i7503, i7505, i7507, i7511, i7513, i7515, i7517,
		i7521, i7523, i7525, i7527, i7531, i7533, i7535, i7537,
		i7541, i7543, i7545, i7547, i7551, i7553, i7555, i7557,
		i7561, i7563, i7565, i7567, i7571, i7573, i7575, i7577,
		i7601, i7603, i7605, i7607, i7611, i7613, i7615, i7617,
		i7621, i7623, i7625, i7627, i7631, i7633, i7635, i7637,
		i7641, i7643, i7645, i7647, i7651, i7653, i7655, i7657,
		i7661, i7663, i7665, i7667, i7671, i7673, i7675, i7677,
		i7701, i7703, i7705, i7707, i7711, i7713, i7715, i7717,
		i7721, i7723, i7725, i7727, i7731, i7733, i7735, i7737,
		i7741, i7743, i7745, i7747, i7751, i7753, i7755, i7757,
		i7761, i7763, i7765, i7767, i7771, i7773, i7775, i7777
	};  
	static void (*noeaetab[])(void) = {
		i7401, i7421, i7401, i7421, i7501, i7521, i7501, i7521,
		i7601, i7621, i7601, i7621, i7701, i7721, i7701, i7721
	};
	
	int i, j;

	if (_hw.hasEAE == mount)
		return;
	_hw.hasEAE = mount;
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];	
	if (mount) {
		for (i = 07401, j = 0; i < 010000; i += 2)
			itab[i] = eaetab[j++];
		for (i = 017401, j = 0; i < 020000; i += 2)
			itab[i] = eaetab[j++];
	} else {
		for (i = 07401, j = 0; i < 010000; i += 2)
			itab[i] = noeaetab[j++ >> 3];
		for (i = 017401, j = 0; i < 020000; i += 2)
			itab[i] = noeaetab[j++ >> 3];
		SC = 0;
		GTF = 0;
		eaeMode = EAE_MODE_A;
		if (_state.running != GOING) {
			[defaultCenter postNotificationName:SC_CHANGED_NOTIFICATION object:self];
			[defaultCenter postNotificationName:GTF_CHANGED_NOTIFICATION object:self];
			[defaultCenter postNotificationName:EAE_MODE_CHANGED_NOTIFICATION object:self];
		}
	}
	[defaultCenter postNotificationName:EAE_MOUNT_NOTIFICATION object:self];
}


- (BOOL) hasEAE
{
	return _hw.hasEAE;
}


- (void) mountKM8E:(BOOL)mount memorySize:(unsigned)memsize timesharingEnabled:(BOOL)timesharing
{
	static const struct {
		ushort	addr;
		void	(*inst)(void);
	} km8etab[] = {
		{ 06201, i6201 }, { 06202, i6202 }, { 06203, i6203 }, { 06204, i6204 },
		{ 06211, i6211 }, { 06212, i6212 }, { 06213, i6213 }, { 06214, i6214 },
		{ 06221, i6221 }, { 06222, i6222 }, { 06223, i6223 }, { 06224, i6224 },
		{ 06231, i6231 }, { 06232, i6232 }, { 06233, i6233 }, { 06234, i6234 },
		{ 06241, i6241 }, { 06242, i6242 }, { 06243, i6243 }, { 06244, i6244 },
		{ 06251, i6251 }, { 06252, i6252 }, { 06253, i6253 }, { 06254, i6254 },
		{ 06261, i6261 }, { 06262, i6262 }, { 06263, i6263 }, { 06264, i6264 },
		{ 06271, i6271 }, { 06272, i6272 }, { 06273, i6273 }, { 06274, i6274 },
		{ 0, NULL }
	};

	int i;
	
	NSAssert ((memsize & ~0170000) == 0, @"Bad memory size");
	NSAssert ((mount && memsize > PDP8_FIELDSIZE) || memsize == PDP8_FIELDSIZE,
		@"Memory size does not match KM8-E state");
	NSAssert (mount || ! timesharing, @"Timesharing option does not match KM8-E state");

	if (_hw.hasKM8E == mount && _hw.hasKM8Etimesharing == timesharing && _hw.memsize == memsize)
		return;
	_hw.hasKM8E = mount;
	_hw.hasKM8Etimesharing = timesharing;
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	if (mount) {
		for (i = 0; km8etab[i].addr; i++)
			itab[km8etab[i].addr] = km8etab[i].inst;
		IMASK |= userFLAG;
	} else {
		for (i = 0; km8etab[i].addr; i++)
			itab[km8etab[i].addr] = i7000;
		DF = W_DF = 0;
		IF = IB = W_IB = 0;
		UF = UB = 0;
		SF = 0;
		IINHIBIT = 0;
		IMASK &= ~userFLAG;
		IOFLAGS &= ~userFLAG;
		if (_state.running != GOING) {
			[defaultCenter postNotificationName:DF_CHANGED_NOTIFICATION object:self];
			[defaultCenter postNotificationName:PROGRAM_COUNTER_CHANGED_NOTIFICATION object:self];
			[defaultCenter postNotificationName:UF_CHANGED_NOTIFICATION object:self];
			[defaultCenter postNotificationName:UB_CHANGED_NOTIFICATION object:self];
			[defaultCenter postNotificationName:SF_CHANGED_NOTIFICATION object:self];
			[defaultCenter postNotificationName:INHIBIT_CHANGED_NOTIFICATION object:self];
		}
	}
	i = _hw.memsize = memsize;
	W_DF = (DF < _hw.memsize) ? DF : 0100000;
	W_IB = (IB < _hw.memsize) ? IB : 0100000;
	while (i < 0100000)
		mem[i++] = 0;
	if (_state.running != GOING) {
		[defaultCenter postNotificationName:IOFLAGS_CHANGED_NOTIFICATION object:self];
		[defaultCenter postNotificationName:MEMORY_CHANGED_NOTIFICATION object:self];
	}
	[defaultCenter postNotificationName:KM8E_MOUNT_NOTIFICATION object:self];
}


- (BOOL) hasKM8E
{
	return _hw.hasKM8E;
}


- (BOOL) isTimesharingEnabled
{
	return _hw.hasKM8Etimesharing;
}


- (ushort) memorySize
{
	return _hw.memsize;
}


- (BOOL) isIOAddressAvailable:(int)addr
{
	int i;
	
	NSAssert1 ((addr & ~077) == 0, @"Bad I/O address %o", addr);
	addr = 06000 | (addr << 3);
	for (i = 0; i < 8; i++) {
		if (itab[addr | i] != i7000)
			return NO;
	}
	return YES;
}


- (void) setIOT:(NSValue *)iot forOpcode:(int)opcode
{
	NSAssert1 ((opcode & ~0777) == 06000, @"Invalid IOT %o", opcode);
	if (iot) {
		PDP8InstructionFunctionPointer p = [iot pointerValue];
		if (p)
			itab[opcode] = p;
	}
}


- (void) setPluginPointer:(void *)pointer forIOAddress:(int)addr
{
	NSAssert1 ((addr & ~077) == 0, @"Bad I/O address %o", addr);
	_state.pluginPointer[addr] = pointer;
}


#if ! defined(NS_BLOCK_ASSERTIONS)

- (id) pluginPointer:(Class)pluginClass;
{
	id pluginPointer = _state.pluginPointer[(_state.currInst >> 3) & 077];
	NSAssert (pluginPointer && [pluginPointer isMemberOfClass:pluginClass], @"Bad plugin pointer");
	return pluginPointer;
}

#endif


#pragma mark Utilities


- (NSString *) loadPaperTape:(NSString *)filename toField:(ushort)field
/* this code is based on a very careful analysis of the PDP-8 BIN loader */
{
	ushort c, x, oldchecksum;
	
	NSAssert1 ((field & ~07) == 0, @"Bad memory field 0%o", field);
	x = oldchecksum = 0;	/* to avoid compiler "used uninitialized" warning */
	field <<= 12;
	if (field >= _hw.memsize)
		return NSLocalizedString(@"Cannot load code to non-existing memory field.", @"");
	FILE *fp = fopen([filename cStringUsingEncoding:NSASCIIStringEncoding], "r");	
	if (! fp)
		return NSLocalizedString(@"Cannot open the paper tape for reading.", @"");
	ushort origin = 0;
	ushort checksum = 0;
	BOOL isRimTape = TRUE;
	BOOL lastWordWasOrigin = FALSE;
	for (c = 0200; c == 0200 && ! feof(fp); c = getc(fp))		/* leader */
		;
	while (c != 0200 && ! feof(fp)) {
		if ((c & 0300) == 0300) {		/* field setting */
			if (c != 0377) {		/* all holes punched are ignored */
				isRimTape = FALSE;	/* RIM cannot have field setting */
				field = ((ushort) (c & 070)) << 9;
				if (field >= _hw.memsize)
					field = 0100000u;
				/* field setting are not included in the checksum */
			}
			c = getc(fp);
		} else if ((c & 0300) == 0100) {	/* origin */
			lastWordWasOrigin = TRUE;
			origin = (c & 077) << 6;
			oldchecksum = checksum;
			checksum += c;
			c = getc(fp);
			origin += c;
			checksum += c;
			c = getc(fp);
		} else {				/* value to deposit in memory */
			if (! lastWordWasOrigin)
				isRimTape = FALSE;
			lastWordWasOrigin = FALSE;
			x = (c & 077) << 6;
			oldchecksum = checksum;
			checksum += c;
			c = getc(fp);
			x += c;
			checksum += c;
			c = getc(fp);
			if ((c != 0200 && ! feof(fp)) || isRimTape) {
				mem[field | origin] = x;
				origin = (origin + 1) & 07777;
			}
		}
	}
	fclose (fp);
	[[NSNotificationCenter defaultCenter]
		postNotificationName:MEMORY_CHANGED_NOTIFICATION object:self];
	if (! isRimTape && (oldchecksum & 07777) != (lastWordWasOrigin ? origin : x))
		return NSLocalizedString(@"The paper tape contains a checksum error.", @"");
	return nil;
}


- (void) notifyEverythingChanged
{
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	
	[defaultCenter postNotificationName:PROGRAM_COUNTER_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:ACCUMULATOR_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:SR_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:SC_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:GTF_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:MQ_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:EAE_MODE_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:DF_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:UF_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:UB_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:SF_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:ENABLE_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:DELAY_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:INHIBIT_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:IOFLAGS_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:MEMORY_CHANGED_NOTIFICATION object:self];
}


- (void) clearAllFlags
{
	[self setLAC:0];
	[self setEnable:0];
	[self setInhibit:0];
	[self setDelay:0];
	[self clearInterruptMaskBits:~userFLAG];
	[self clearIOFlagBits:~0];
	[self setEAEmode:EAE_MODE_A];
	[self setGTF:0];
	int i;
	for (i = 0; i < PDP8_IOADDRS; i++) {
		if (_state.pluginPointer[i])
			[(PDP8Plugin *) _state.pluginPointer[i] clearAllFlags:i];
	}
	[[NSNotificationCenter defaultCenter] postNotificationName:CLEAR_ALL_FLAGS_NOTIFICATION object:self];
}


- (void) loadExtendedAddress
{
	if (_hw.hasKM8E) {
		[self setIB:(SR & 070) >> 3];
		[self setIF:(SR & 070) >> 3];
		[self setDF:SR & 007];
		[self setUB:0];
		[self setUF:0];
		[self clearIOFlagBits:userFLAG];
	}
}


- (void) reset
{
	[self setProgramCounter:0200];
	[self setLAC:0];
	[self setSR:0];
	[self setSC:0];
	[self setMQ:0];
	[self setGTF:0];
	[self setDF:0];
	[self setUF:0];
	[self setUB:0];
	[self setSF:0];
	[self setEAEmode:EAE_MODE_A];
	[self setEnable:0];
	[self setDelay:0];
	[self setInhibit:0];
	[self clearInterruptMaskBits:~userFLAG];
	[self clearIOFlagBits:~0];
	[self clearMemory];
}


#pragma mark Step - Trace - Go


- (void) setTraceSpeed:(double)speed
{
	_state.usecTraceDelay = (unsigned) (speed * 1000000.0);
}


- (void) setGoSpeed:(int)goSpeed
{
	NSAssert (goSpeed == GO_AS_FAST_AS_POSSIBLE || goSpeed == GO_WITH_PDP8_SPEED ||
		goSpeed == GO_WITH_PDP8_SPEED_PRECISE, @"Illegal go speed specififed");
	/* reset the timer - required when the PDP-8 runs while this method is called */
	_state.executionTime = 0;
	_state.absoluteTime = mach_absolute_time();
	_state.goSpeed = goSpeed;
}


- (int) getGoSpeed
{
	return _state.goSpeed;
}


- (BOOL) isStopped
{
	return _state.running == STOPPED;
}


- (BOOL) isTracing
{
	return _state.running == TRACING;
}


- (BOOL) isGoing;
{
	return _state.running == GOING;
}


- (BOOL) isRunning
{
	return _state.running != STOPPED;
}


- (BOOL) isHalted
{
	return _state.halted;
}


- (void) setHalt:(BOOL)halt
{
	if (halt)
		[self stop];
	_state.halted = halt;
}


static void breakInstruction (void)		/* used for break opcodes */
{
	pdp8->_state.running = STOPPED;
	pdp8->PC--;
}


- (void) setupBreakpoints:(int)stopAddress
{
	NSEnumerator	*enumerator;
	Breakpoint	*next;
	
	NSAssert (stopAddress == -1 || (stopAddress & ~077777) == 0, @"Invalid stop address");

	// breakpoints
	enumerator = [breakpoints enumerator];
	bzero (bp, sizeof(bp));
	while ((next = (Breakpoint *)[enumerator nextObject]))
		bp[[next identifier]] = [next value];
	if (stopAddress >= 0)
		bp[stopAddress] = BREAKPOINT;

	// break opcodes
	NSAssert (saveopcodes == nil, @"saveopcodes not nil");
	saveopcodes = [[NSMutableDictionary alloc] init];
	enumerator = [breakopcodes enumerator];
	while ((next = (Breakpoint *)[enumerator nextObject])) {
		unsigned opcode = [next identifier];
		if ([next value] & USERMODE_BREAKOPCODE) {
			[saveopcodes setObject:[NSValue valueWithPointer:itab[opcode]]
				forKey:[NSNumber numberWithInt:opcode]];
			itab[opcode] = breakInstruction;
		}
		if ([next value] & SYSTEMMODE_BREAKOPCODE) {
			opcode |= 010000;
			[saveopcodes setObject:[NSValue valueWithPointer:itab[opcode]]
				forKey:[NSNumber numberWithInt:opcode]];
			itab[opcode] = breakInstruction;
		}
	}
}


- (void) resetBreakpoints
{
	NSNumber *next;
	
	NSEnumerator *enumerator = [saveopcodes keyEnumerator];	
	while ((next = (NSNumber *)[enumerator nextObject]))
		itab[[next intValue]] = [[saveopcodes objectForKey:next] pointerValue];
	[saveopcodes release];
	saveopcodes = nil;
}


- (void) sendStopNotification		// must run on main thread
{
	NSAssertRunningOnMainThread ();
	[self notifyEverythingChanged];
	[[NSNotificationCenter defaultCenter] postNotificationName:PDP8_STOP_NOTIFICATION object:self];
}


- (void) oneRunLoopPass
{
	NSAssertRunningOnMainThread ();
	[[NSRunLoop currentRunLoop] limitDateForMode:NSDefaultRunLoopMode];
}


- (void) pdp8Step
{
	/* Check for interrupts */
	if (IENABLE && (IOFLAGS & IMASK) && ! (IDELAY || IINHIBIT)) {
		mem[0] = PC;
		PC = 1;
		SF = (UF >> 6) | (IF >> 9) | (DF >> 12);
		W_IB = IB = IF = W_DF = DF = UB = UF = IENABLE = 0;
		EXECUTION_TIME (14);	/* count the execute cycle of JMS 0 */
	} else {
		IDELAY = 0;
		/* Fetch and execute an instruction */
		itab[UF | (_state.currInst = mem[IF | PC])]();
		/* Update the program counter */
		if (++PC & 010000)
			PC &= 07777;
	}
}


- (void) sendStepNotifications
{
	NSAssertRunningOnMainThread ();
	[self notifyEverythingChanged];
	[[NSNotificationCenter defaultCenter] postNotificationName:PDP8_STEP_NOTIFICATION object:self];
}

	
- (void) step
{
	NSAssert (_state.running == STOPPED, @"PDP-8 not stopped");
	[self pdp8Step];
	[self sendStepNotifications];
}


- (void) traceThread:(NSNumber *)stopAddress
{
	struct timeval tv0, tv1;
	struct timezone tz;
	
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	BOOL installBreakpoints = TRUE;
	[NSThread setThreadPriority:0];
	// low priority for the trace thread, so the main thread can process user interactions
	_state.running = TRACING;
	while (_state.running) {
		gettimeofday (&tv0, &tz);
		[self pdp8Step];
		[self performSelectorOnMainThread:@selector(sendStepNotifications)
			withObject:nil waitUntilDone:YES];
		if (installBreakpoints) {	// one step without breakpoints
			[self setupBreakpoints:[stopAddress intValue]];
			installBreakpoints = FALSE;
		}
		if (bp[IF | PC])
			_state.running = STOPPED;
		else if (_state.running) {
			/* Give the main thread the chance to handle events. When tracing with full
			   speed (state.usecTraceDelay == 0), on dual core machines, the trace thread
			   calls the step method so fast on the main thread that it can't handle user
			   events. */
			[self performSelectorOnMainThread:@selector(oneRunLoopPass)
				withObject:nil waitUntilDone:YES];
			gettimeofday (&tv1, &tz);
			timersub (&tv1, &tv0, &tv0);
			if (_state.usecTraceDelay > (ulong) tv0.tv_usec)
				usleep (_state.usecTraceDelay - tv0.tv_usec);
		}
	}
	[self resetBreakpoints];
	[self performSelectorOnMainThread:@selector(sendStopNotification)
		withObject:nil waitUntilDone:YES];
	[pool release];
}


- (void) trace:(int)stopAddress
{
	NSAssert (_state.running == STOPPED, @"PDP-8 not stopped");
	NSAssert (! _state.halted, @"PDP8 trace while halted");
	if (_state.halted)
		[self step];
	else {
		[[NSNotificationCenter defaultCenter] postNotificationName:PDP8_TRACE_NOTIFICATION
			object:self];
		[NSThread detachNewThreadSelector:@selector(traceThread:) toTarget:self
			withObject:[NSNumber numberWithInt:stopAddress]];
	}
}


- (void) goThread:(id)object
{
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	_state.running = GOING;
	_state.executionTime = 0;
	_state.absoluteTime = mach_absolute_time();
	while (_state.running) {
		[self pdp8Step];
		/* Check for breakpoints */
		if (bp[IF | PC])
			_state.running = STOPPED;
		/* Realtime speed */
		if (_state.goSpeed != GO_AS_FAST_AS_POSSIBLE) {
			uint64_t delayUntilAbsolute = _state.absoluteTime +
				nanoseconds2absolute(_state.executionTime * 100l);
			if (_state.goSpeed == GO_WITH_PDP8_SPEED_PRECISE) {
				// precise timing: delay after each PDP-8 instruction using busy waiting
				// this delay takes about 0.1 microseconds longer than required
				// (precision in the order of 0.1 PDP-8 instructions)
				while (mach_absolute_time() < delayUntilAbsolute)
					;
			} else if (mach_absolute_time() + nanoseconds2absolute(15000l) < delayUntilAbsolute) {
				// PDP-8 speed without precise timing: delay when a forerun of at least
				// 15 microseconds has accumulated (to avoid high kernel load on slower Macs)
				// mach_wait_until() delays about 10 microseconds longer than required
				// (precision in the order of about some dozen PDP-8 instructions)
				mach_wait_until (delayUntilAbsolute);
			}
			// consider the time lag due to too long delays for the next loop
			_state.absoluteTime = delayUntilAbsolute;
			_state.executionTime = 0;
		}
	}
	[self resetBreakpoints];
	[self performSelectorOnMainThread:@selector(sendStopNotification)
		withObject:nil waitUntilDone:YES];
	[pool release];
}


- (void) go:(int)stopAddress
{
	NSAssert (_state.running == STOPPED, @"PDP-8 not stopped");
	NSAssert (! _state.halted, @"PDP-8 trace while halted");
	if (_state.halted)
		[self step];
	else {
		[[NSNotificationCenter defaultCenter] postNotificationName:PDP8_GO_NOTIFICATION object:self];
		[self pdp8Step];	// one step without breakpoints and without GUI notifications
		[self setupBreakpoints:stopAddress];
		[NSThread detachNewThreadSelector:@selector(goThread:) toTarget:self withObject:nil];
	}
}


- (void) stop
{
	_state.running = STOPPED;
}


#pragma mark Register Access


#define NOTIFY(notification)	if (_state.running != GOING) {						\
					[[NSNotificationCenter defaultCenter]				\
						postNotificationName:(notification) object:self];	\
				}


- (ushort) getPC
{
	return PC;
}


- (void) setPC:(ushort)pc
{
	NSAssert1 ((pc & ~07777) == 0, @"Bad PC: 0%o", pc);
	PC = pc;
	NOTIFY (PROGRAM_COUNTER_CHANGED_NOTIFICATION);
}


- (ushort) getProgramCounter
{
	return IF | PC;
}


- (void) setProgramCounter:(ushort)programCounter
{
	NSAssert1 ((programCounter & ~077777) == 0, @"Bad IF|PC: 0%o", programCounter);
	NSAssert1 (programCounter < 010000 || _hw.hasKM8E, @"Can't set IF|PC to field %o without KM8-E",
		programCounter >> 12);
	IB = IF = programCounter & 070000;
	PC = programCounter & 007777;
	NOTIFY (PROGRAM_COUNTER_CHANGED_NOTIFICATION);
}


- (ushort) getIF
{
	return IF >> 12;
}


- (void) setIF:(ushort)_if
{
	NSAssert1 ((_if & ~07) == 0, @"Bad IF: 0%o", _if);
	IF = _if << 12;
	NOTIFY (PROGRAM_COUNTER_CHANGED_NOTIFICATION);
}	


- (ushort) getIB
{
	return IB >> 12;
}


- (void) setIB:(ushort)ib
{
	NSAssert1 ((ib & ~07) == 0, @"Bad IB: 0%o", ib);
	W_IB = IB = ib << 12;
	if (W_IB >= _hw.memsize)
		W_IB = 0100000;
	NOTIFY (PROGRAM_COUNTER_CHANGED_NOTIFICATION);
}	


- (ushort) getDF
{
	return DF >> 12;
}


- (void) setDF:(ushort)df
{
	NSAssert1 ((df & ~07) == 0, @"Bad DF: 0%o", df);
	W_DF = DF = df << 12;
	if (W_DF >= _hw.memsize)
		W_DF = 0100000;
	NOTIFY (DF_CHANGED_NOTIFICATION);
}	


- (ushort) getUF
{
	return UF >> 12;
}


- (void) setUF:(ushort)uf
{
	NSAssert1 ((uf & ~01) == 0, @"Bad UF: 0%o", uf);
	UF = uf << 12;
	NOTIFY (UF_CHANGED_NOTIFICATION);
}	


- (ushort) getUB
{
	return UB >> 12;
}


- (void) setUB:(ushort)ub
{
	NSAssert1 ((ub & ~01) == 0, @"Bad UB: %o", ub);
	UB = ub << 12;
	NOTIFY (UB_CHANGED_NOTIFICATION);
}	


- (ushort) getSF
{
	return SF;
}


- (void) setSF:(ushort)sf
{
	NSAssert1 ((sf & ~0177) == 0, @"Bad SF: 0%o", sf);
	SF = sf;
	NOTIFY (SF_CHANGED_NOTIFICATION);
}	


- (ushort) getSR
{
	return SR;
}


- (void) setSR:(ushort)sr
{
	NSAssert1 ((sr & ~077777) == 0, @"Bad SR: 0%o", sr);
	SR = sr;
	NOTIFY (SR_CHANGED_NOTIFICATION);
}


- (ushort) getL
{
	return AC >> 12;
}


- (void) setL:(ushort)l
{
	NSAssert1 ((l & ~01) == 0, @"Bad Link: 0%o", l);
	AC = (l << 12) | (AC & 07777);
	NOTIFY (ACCUMULATOR_CHANGED_NOTIFICATION);
}


- (ushort) getAC
{
	return AC & 07777;
}


- (void) setAC:(ushort)ac
{
	NSAssert1 ((ac & ~07777) == 0, @"Bad AC: 0%o", ac);
	AC = (AC & 010000) | ac;
	NOTIFY (ACCUMULATOR_CHANGED_NOTIFICATION);
}


- (ushort) getLAC
{
	return AC;
}


- (void) setLAC:(ushort)lac
{
	NSAssert1 ((lac & ~017777) == 0, @"Bad L|AC: 0%o", lac);
	AC = lac;
	NOTIFY (ACCUMULATOR_CHANGED_NOTIFICATION);	
}


- (ushort) getSC
{
	return SC;
}


- (void) setSC:(ushort)sc
{
	NSAssert1 ((sc & ~037) == 0, @"Bad SC: 0%o", sc);
	SC = sc;
	NOTIFY (SC_CHANGED_NOTIFICATION);
}

	
- (ushort) getGTF
{
	return GTF;
}


- (void) setGTF:(ushort)gtf
{
	NSAssert1 ((gtf & ~01) == 0, @"Bad GTF: 0%o", gtf);
	GTF = gtf;
	NOTIFY (GTF_CHANGED_NOTIFICATION);
}


- (ushort) getMQ
{
	return MQ;
}


- (void) setMQ:(ushort)mq
{
	NSAssert1 ((mq & ~07777) == 0, @"Bad MQ: 0%o", mq);
	MQ = mq;
	NOTIFY (MQ_CHANGED_NOTIFICATION);
}


- (char) getEAEmode
{
	return eaeMode;
}


- (void) setEAEmode:(char)mode
{
	NSAssert1 (mode == 'A' || mode == 'B', @"Bad EAE mode: %c", mode);
	eaeMode = mode;
	NOTIFY (EAE_MODE_CHANGED_NOTIFICATION);
}


- (ushort) getEnable
{
	return IENABLE;
}


- (void) setEnable:(ushort)enable
{
	NSAssert1 ((enable & ~01) == 0, @"Bad Interrupt Enable Flag: 0%o", enable);
	IENABLE = enable;
	NOTIFY (ENABLE_CHANGED_NOTIFICATION);
}


- (ushort) getDelay
{
	return IDELAY;
}


- (void) setDelay:(ushort)delay
{
	NSAssert1 ((delay & ~01) == 0, @"Bad Interrupt Delay Flag: 0%o", delay);
	IDELAY = delay;
	NOTIFY (DELAY_CHANGED_NOTIFICATION);
}


- (ushort) getInhibit
{
	return IINHIBIT;
}


- (void) setInhibit:(ushort)inhibit
{
	NSAssert1 ((inhibit & ~01) == 0, @"Bad Interrupt Inhibit Flag: 0%o", inhibit);
	IINHIBIT = inhibit;
	NOTIFY (INHIBIT_CHANGED_NOTIFICATION);
}


- (ulong) getInterruptMaskBits:(ulong)bitmask
{
	return IMASK & bitmask;
}


- (void) setInterruptMaskBits:(ulong)bitmask
{
	IMASK |= bitmask;
	NOTIFY (IOFLAGS_CHANGED_NOTIFICATION);
}


- (void) clearInterruptMaskBits:(ulong)bitmask
{
	IMASK &= ~bitmask;
	NOTIFY (IOFLAGS_CHANGED_NOTIFICATION);
}


- (ulong) getIOFlagBits:(ulong)bitmask
{
	return IOFLAGS & bitmask;
}


- (void) setIOFlagBits:(ulong)bitmask
{
	IOFLAGS |= bitmask;
	NOTIFY (IOFLAGS_CHANGED_NOTIFICATION);
}


- (void) clearIOFlagBits:(ulong)bitmask
{
	IOFLAGS &= ~bitmask;
	NOTIFY (IOFLAGS_CHANGED_NOTIFICATION);
}


- (BOOL) interruptRequest
{
	return (IOFLAGS & IMASK) ? YES : NO;
}


#pragma mark Memory Access


- (ushort) memoryAt:(int)address
{
	NSAssert1 ((address & ~077777) == 0, @"Bad address: 0%o", address);
	NSAssert2 ((mem[address] & ~07777) == 0,
		@"Bad memory content: pdp8.mem[%5.5o] = 0%o", address, mem[address]);
	return mem[address];
}


- (ushort) memoryAtNext:(int)address
{
	NSAssert1 ((address & ~077777) == 0, @"Bad address: 0%o", address);
	NSAssert2 ((mem[(address & 070000) | ((address + 1) &  07777)] & ~07777) == 0,
		@"Bad memory content: pdp8.mem[%5.5o] = 0%o",
		(address & 070000) | ((address + 1) &  07777),
		mem[(address & 070000) | ((address + 1) &  07777)]);
	return mem[(address & 070000) | ((address + 1) &  07777)];
}


- (ushort *) directMemoryAccess
{
	return mem;
}


- (void) notifyMemoryChanged
{
	NSAssertRunningOnMainThread ();
	NSAssert (_state.running != GOING, @"PDP-8 is running");
	[[NSNotificationCenter defaultCenter] postNotificationName:(MEMORY_CHANGED_NOTIFICATION) object:self];
}


- (void) directMemoryWriteFinished
{
	if (_state.running != GOING)
		[self performSelectorOnMainThread:@selector(notifyMemoryChanged)
			withObject:nil waitUntilDone:YES];
}


- (void) setMemoryAtAddress:(int)address toValue:(int)value
{
	NSAssert1 ((address & ~077777) == 0, @"Bad address: 0%o", address);
	NSAssert1 (address < _hw.memsize, @"Address out of available memory: 0%o", address);
	NSAssert2 ((value & ~07777) == 0, @"Bad value 0%o for pdp8.mem[%5.5o]", value, address);	
	mem[address] = value;
	NOTIFY (MEMORY_CHANGED_NOTIFICATION);
}


- (void) setMemoryAtNextAddress:(int)address toValue:(int)value
{
	NSAssert1 ((address & ~077777) == 0, @"Bad address: 0%o", address);
	NSAssert2 ((value & ~07777) == 0, @"Bad value 0%o for pdp8.mem[%5.5o]",
		value, (address & 070000) | ((address + 1) &  07777));	
	NSAssert1 (address < _hw.memsize, @"Address out of available memory: 0%o", address);
	mem[(address & 070000) | ((address + 1) &  07777)] = value;
	NOTIFY (MEMORY_CHANGED_NOTIFICATION);
}


- (void) setMemoryAtAddress:(int)address toValues:(NSArray *)values withMask:(BOOL)withMask
{
	int i;
	
	int count = [values count];
	NSAssert (values, @"values is nil");
	NSAssert1 ((address & ~077777) == 0, @"Bad start address: 0%o", address);
	NSAssert1 (((address + count - 1) & ~077777) == 0, @"Bad end address: 0%o", address + count - 1);
	NSAssert1 ((address + count - 1) < _hw.memsize,
		@"End address out of available memory: 0%o", address);
	for (i = 0; i < count; i++) {
		int value = [[values objectAtIndex:i] intValue];
		NSAssert2 ((value & ~(withMask ? 077777777 : 07777)) == 0,
			@"Bad mask/value 0%o for pdp8.mem[%5.5o]", value, address + i);
		int mask = withMask ? (value >> 12) : 07777;
		mem[address + i] = (mem[address + i] & ~mask) | (value & mask);
	}
	NOTIFY (MEMORY_CHANGED_NOTIFICATION);	
}


- (void) clearMemory
{
	bzero (mem, sizeof(mem));
	NOTIFY (MEMORY_CHANGED_NOTIFICATION);	
}


- (ushort) getCurrentOpcode
{
	return _state.currInst;
}


- (PDP8InstructionFunctionPointer) getNextInstruction
{
	NSAssert (_state.running != GOING, @"PDP-8 is running");
	_state.currInst = mem[IF | PC];		// the skiptest function may use the PLUGIN_POINTER() macro
	return itab[UF | mem[IF | PC]];
}


#pragma mark TSC8-75


- (ushort) getTSC8ertb
{
	return _tsc8.ertb;
}


- (void) setTSC8ertb:(ushort)ertb
{
	NSAssert1 ((ertb & ~07777) == 0, @"Bad ERTB: %o", ertb);
	_tsc8.ertb = ertb;
}


- (ushort) getTSC8eriot
{
	return _tsc8.eriot;
}


- (void) setTSC8eriot:(ushort)eriot
{
	NSAssert1 ((eriot & ~07777) == 0, @"Bad ERIOT: %o", eriot);
	_tsc8.eriot = eriot;
	NOTIFY (IOFLAGS_CHANGED_NOTIFICATION);		// ESME might change its skip behaviour on new ERIOT
}


- (ushort) getTSC8ecdf
{
	return _tsc8.ecdf;
}


- (void) setTSC8ecdf:(ushort)ecdf
{
	NSAssert1 ((ecdf & ~01) == 0, @"Bad ECDF: %o", ecdf);
	_tsc8.ecdf = ecdf;
	NOTIFY (IOFLAGS_CHANGED_NOTIFICATION);		// ESME might change its skip behaviour on new ECDF
}

- (ulong) getTSC8flag
{
	return _tsc8.flag;
}


- (void) setTSC8flag:(ulong)flag
{
	_tsc8.flag = flag;
}


- (BOOL) getTSC8esmeEnabled
{
	return _tsc8.esmeEnabled;
}


- (void) setTSC8esmeEnabled:(BOOL)enabled
{
	_tsc8.esmeEnabled = enabled;
	NOTIFY (IOFLAGS_CHANGED_NOTIFICATION);		// ESME might change its skip behaviour when enabled
}


@end
