/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	ASR33.m - ASR 33 Teletype for the PDP-8/E Simulator
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


#define USE_ASR33_REGISTERS_DIRECTLY 1

#import "PluginFramework/PDP8.h"
#import "PluginFramework/Utilities.h"

#import "ASR33.h"
#import "ASR33TextView.h"
#import "ASR33WindowController.h"
#import "ASR33iot.h"
#import "ASR33Preferences.h"
#import "InputConsumerProtocol.h"
#import "TypeaheadBuffer.h"
#import "PaperTapeController.h"


#define ASR33_CONTTY_PLUGIN_NAME	NSLocalizedStringFromTableInBundle( \
					@"ASR 33 Console Teletype.pdp8Plugin", nil, [self bundle], @"")
#define ASR33_AUXTTY_IO_INFO_FILENAME	@"auxtty-io-info"

#define CODER_KEY_KBB			@"kbb"
#define CODER_KEY_TTO			@"tto"
#define CODER_KEY_INFLAG		@"inflag"
#define CODER_KEY_INMASK		@"inmask"
#define CODER_KEY_OUTFLAG		@"outflag"
#define CODER_KEY_OUTMASK		@"outmask"
#define CODER_KEY_ONLINE		@"online"

#define NO_OUTPUT_AVAILABLE		0
#define OUTPUT_AVAILABLE		1
#define NO_INPUT_AVAILABLE		0
#define INPUT_AVAILABLE			1

#define TELETYPE_DELAY			100000		// 100.000 microseconds = 0.1 second

#define SOUND_BACKSPACE			@"tty-backspace"
#define SOUND_BELL			@"tty-bell"
#define SOUND_CARRIAGE_RETURN		@"tty-carriage-return"
#define SOUND_KEYSTROKE1		@"tty-keystroke1"
#define SOUND_KEYSTROKE2		@"tty-keystroke2"
#define SOUND_KEYSTROKE3		@"tty-keystroke3"
#define SOUND_KEYSTROKE4		@"tty-keystroke4"
#define SOUND_SPACE			@"tty-space"
#define SOUND_TYPE			@"mp3"


@interface NSSound (SetVolume)

- (void) setVolume:(float)volume;	// this is a Leopard method, not available with Tiger

@end


@implementation ASR33


#pragma mark Plugin Methods


SETUP_PDP8_POINTER_FOR_IOTS


- (NSString *) ioInformationPlistName
{
	isConsoleTTY = [[self pluginName] isEqualToString:ASR33_CONTTY_PLUGIN_NAME];
	return isConsoleTTY ? [super ioInformationPlistName] : ASR33_AUXTTY_IO_INFO_FILENAME;
}


- (NSArray *) iotsForAddress:(int)ioAddress
{
	if (inAddress == 0) {
		inAddress = ioAddress;
		return [NSArray arrayWithObjects:
			[NSValue valueWithPointer:i6030],
			[NSValue valueWithPointer:i6031],
			[NSValue valueWithPointer:i6032],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:i6034],
			[NSValue valueWithPointer:i6035],
			[NSValue valueWithPointer:i6036],
			[NSValue valueWithPointer:nil],
			nil];
	} else {
		outAddress = ioAddress;
		return [NSArray arrayWithObjects:
			[NSValue valueWithPointer:i6040],
			[NSValue valueWithPointer:i6041],
			[NSValue valueWithPointer:i6042],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:i6044],
			[NSValue valueWithPointer:i6045],
			[NSValue valueWithPointer:i6046],
			[NSValue valueWithPointer:nil],
			nil];
	}
}


- (NSArray *) skiptestsForAddress:(int)ioAddress
{
	return ioAddress == inAddress ?
		[NSArray arrayWithObjects:
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:s6031],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			nil] :
		[NSArray arrayWithObjects:
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:s6041],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:s6045],
			[NSValue valueWithPointer:nil],
			[NSValue valueWithPointer:nil],
			nil];
}


- (void) setIOFlag:(unsigned long)flag forIOFlagName:(NSString *)name;
{
	if (inflag)
		outflag = flag;
	else
		inflag = flag;
}


- (void) CAF:(int)ioAddress
{
	if (ioAddress == inAddress) {		// CAF is called twice, ignore second call
		KBB = 0;
		TTO = 0;		
		[pdp8 setInterruptMaskBits:inflag | outflag];
		[pdp8 clearIOFlagBits:inflag | outflag];
	}
}


- (void) clearAllFlags:(int)ioAddress
{
/* Don't modify the online state - software can't turn the TTY power knob.
   Don't modify the on/off state of the reader/punch (else the user cannot
   load BIN tapes as described in the Small Computer Systems Handbook,
   where he must operate CLEAR/CONT *after* turning on the reader). */
	if (ioAddress == inAddress) {		// CAF is called twice, ignore second call
		[self setKBB:0];
		[self setTTO:0];
		[pdp8 setInterruptMaskBits:inflag | outflag];
		[pdp8 clearIOFlagBits:inflag | outflag];
	}
}


- (void) resetDevice
{
	[typeaheadBuffer flush:self];
	[self setKBB:0];
	[self setTTO:0];
	[pdp8 setInterruptMaskBits:inflag | outflag];
	[pdp8 clearIOFlagBits:inflag | outflag];
	[self setOnline:YES];
}


#pragma mark Thread Handling


- (void) playSound:(unsigned short)key
{
	NSString *soundname;
	
	NSAssertRunningOnMainThread ();
	key &= 0177;
	if (playSound || key == '\a') {
		if (isblank(key))
			soundname = SOUND_SPACE;
		else if (key == '\b')
			soundname = SOUND_BACKSPACE;
		else if (key == '\a')
			soundname = SOUND_BELL;
		else if (key == '\r')
			soundname = SOUND_CARRIAGE_RETURN;
		else if (isupper(key))
			soundname = SOUND_KEYSTROKE1;
		else if (islower(key))
			soundname = SOUND_KEYSTROKE2;
		else if (isdigit(key))
			soundname = SOUND_KEYSTROKE3;
		else 
			soundname = SOUND_KEYSTROKE4;
		NSSound *sound = [[NSSound alloc] initWithContentsOfFile:
			[[self bundle] pathForResource:soundname ofType:SOUND_TYPE] byReference:YES];
		[sound autorelease];
		if ([sound respondsToSelector:@selector(setVolume:)])
			[sound setVolume:soundVolume];
		[sound play];
	}
}


- (void) canContinueInput
{
	if ([inputLock tryLockWhenCondition:NO_INPUT_AVAILABLE])
		[inputLock unlockWithCondition:INPUT_AVAILABLE];
	/* else
		User typed so fast that input would be lost, but the typeahead buffer keeps this input. */
}


- (void) canContinueOutput
{
	if ([outputLock tryLockWhenCondition:NO_OUTPUT_AVAILABLE])
		[outputLock unlockWithCondition:OUTPUT_AVAILABLE];
	else
		NSLog (@"PDP-8 software bug: TPC or TLS executed before preceding TTY output finished");
}


- (void) setTeletypeOutputFlag
{
	NSAssertRunningOnMainThread ();
	[pdp8 setIOFlagBits:outflag];
}


- (void) processInput
{
	NSAssertRunningOnMainThread ();
	input &= 0377;		// strip off Unicode characters etc.
	if (online) {
		[self setKBB:input];
		[pdp8 setIOFlagBits:inflag];
	} else {
		[self playSound:input];
		[punch putChar:input & punchMask handleBackspace:YES];
		[textview putChar:input & 0177];
	}
}


- (void) processOutput
{
	NSAssertRunningOnMainThread ();
	[self playSound:output];
	[self setTTO:output];
	[punch putChar:output & punchMask handleBackspace:NO];
	[textview putChar:output & 0177];
}


- (void) getReaderChar
{
	NSAssertRunningOnMainThread ();
	input = [reader getChar];
}


- (uint64_t) realtimeDelay:(uint64_t)maTime
{
	if (runWithRealtimeSpeed) {
		uint64_t us = absolute2nanoseconds(mach_absolute_time() - maTime) / 1000;
		if (us < TELETYPE_DELAY)
			usleep (TELETYPE_DELAY - us);
		maTime = mach_absolute_time();
	}
	return maTime;
}


- (void) asr33InputThread:(id)object
{
	[[NSAutoreleasePool alloc] init];
	for (;;) {
		[inputLock lockWhenCondition:INPUT_AVAILABLE];
		uint64_t maTime = mach_absolute_time();
		while ((! online || [pdp8 getIOFlagBits:inflag] == 0) &&
			([self performSelectorOnMainThread:@selector(getReaderChar)
				withObject:nil waitUntilDone:YES], input != EOF)) {
			maTime = [self realtimeDelay:maTime];
			[self performSelectorOnMainThread:@selector(processInput)
				withObject:nil waitUntilDone:YES];
		}
		while ([typeaheadBuffer hasCharacters] &&
			(! online || [pdp8 isStopped] || [pdp8 getIOFlagBits:inflag] == 0)) {
			input = [typeaheadBuffer getNextChar] | 0200;
			maTime = [self realtimeDelay:maTime];
			[self performSelectorOnMainThread:@selector(processInput)
				withObject:nil waitUntilDone:YES];
		}
		[inputLock unlockWithCondition:NO_INPUT_AVAILABLE];
	}
}


- (void) asr33OutputThread:(id)object
{
	[[NSAutoreleasePool alloc] init];
	[outputOnline lock];
	for (;;) {
		if (! online)
			[outputOnline wait];
		[outputLock lockWhenCondition:OUTPUT_AVAILABLE];
		[self performSelectorOnMainThread:@selector(processOutput)
			withObject:nil waitUntilDone:YES];
		if (runWithRealtimeSpeed)
			usleep (TELETYPE_DELAY);
		[outputLock unlockWithCondition:NO_OUTPUT_AVAILABLE];
		[self performSelectorOnMainThread:@selector(setTeletypeOutputFlag)
			withObject:nil waitUntilDone:YES];
	}
}


#pragma mark Register Access


- (unsigned short) getKBB
{
	return KBB;
}


- (void) setKBB:(unsigned short)kbb
{
	NSAssert1 ((kbb & ~0377) == 0, @"Bad KBB: 0%o", kbb);
	KBB = kbb;
	[[NSNotificationCenter defaultCenter] postNotificationName:KBB_CHANGED_NOTIFICATION object:self];
}


- (unsigned short) getTTO
{
	return TTO;
}


- (void) setTTO:(unsigned short)tto
{
	NSAssert1 ((tto & ~0377) == 0, @"Bad TTO: 0%o", tto);
	TTO = tto;
	[[NSNotificationCenter defaultCenter] postNotificationName:TTO_CHANGED_NOTIFICATION object:self];
}


- (BOOL) getOnline
{
	return online;
}


- (void) setOnline:(BOOL)onlineOffline
{
	online = onlineOffline;
	[outputOnline signal];
	if (! online)
		[self canContinueInput];
	[[NSNotificationCenter defaultCenter]
		postNotificationName:TTY_ONLINE_CHANGED_NOTIFICATION object:self];
}


#pragma mark Initialization


- (id) initWithCoder:(NSCoder *)coder
{
	self = [super init];
	[self setKBB:[coder decodeIntForKey:CODER_KEY_KBB]];
	[self setTTO:[coder decodeIntForKey:CODER_KEY_TTO]];
	[coder decodeBoolForKey:CODER_KEY_INFLAG] ?
		[pdp8 setIOFlagBits:inflag] : [pdp8 clearIOFlagBits:inflag];
	[coder decodeBoolForKey:CODER_KEY_INMASK] ?
		[pdp8 setInterruptMaskBits:inflag] : [pdp8 clearInterruptMaskBits:inflag];
	[coder decodeBoolForKey:CODER_KEY_OUTFLAG] ?
		[pdp8 setIOFlagBits:outflag] : [pdp8 clearIOFlagBits:outflag];
	[coder decodeBoolForKey:CODER_KEY_OUTMASK] ?
		[pdp8 setInterruptMaskBits:outflag] : [pdp8 clearInterruptMaskBits:outflag];
	[self setOnline:[coder decodeBoolForKey:CODER_KEY_ONLINE]];
	return self;
}


- (void) encodeWithCoder:(NSCoder *)coder
{
	[coder encodeInt:[self getKBB] forKey:CODER_KEY_KBB];
	[coder encodeInt:[self getTTO] forKey:CODER_KEY_TTO];
	[coder encodeBool:[pdp8 getIOFlagBits:inflag] ? YES : NO forKey:CODER_KEY_INFLAG];
	[coder encodeBool:[pdp8 getInterruptMaskBits:inflag] ? YES : NO forKey:CODER_KEY_INMASK];
	[coder encodeBool:[pdp8 getIOFlagBits:outflag] ? YES : NO forKey:CODER_KEY_OUTFLAG];
	[coder encodeBool:[pdp8 getInterruptMaskBits:outflag] ? YES : NO forKey:CODER_KEY_OUTMASK];
	[coder encodeBool:[self getOnline] forKey:CODER_KEY_ONLINE];
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	// NSLog (@"ASR33 notifyApplicationWillTerminate");
	[[NSNotificationCenter defaultCenter] removeObserver:self name:NSUserDefaultsDidChangeNotification
		object:nil];
	NSMutableData *data = [NSMutableData data];
	NSKeyedArchiver *archiver = [[NSKeyedArchiver alloc] initForWritingWithMutableData:data];
	[self encodeWithCoder:archiver];
	[archiver finishEncoding];
	[archiver release];
	[[NSUserDefaults standardUserDefaults] setObject:data forKey:[self pluginName]];
}


- (void) notifyPreferencesChanged:(NSNotification *)notification
{
	// NSLog (@"ASR33 notifyPreferencesChanged");
	NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
	runWithRealtimeSpeed = [defaults integerForKey:ASR33_PREFS_SPEED_KEY];
	playSound = runWithRealtimeSpeed && [defaults boolForKey:ASR33_PREFS_PLAY_SOUND];
	soundVolume = [defaults objectForKey:ASR33_PREFS_SOUND_VOLUME] ?
		[defaults floatForKey:ASR33_PREFS_SOUND_VOLUME] : 0.5f;
	punchMask = [defaults boolForKey:ASR33_PREFS_MASK_HIGHBIT_KEY] ? 0177 : 0377;
}


- (void) pluginDidLoad
{
	NSData *data = [[NSUserDefaults standardUserDefaults] dataForKey:[self pluginName]];
	if (data) {
		NSKeyedUnarchiver *unarchiver = [[NSKeyedUnarchiver alloc] initForReadingWithData:data];
		self = [self initWithCoder:unarchiver];
		[unarchiver finishDecoding];
		[unarchiver release];
	} else {
		[self setOnline:YES];
	}
	inputLock = [[NSConditionLock alloc] initWithCondition:NO_INPUT_AVAILABLE];
	outputLock = [[NSConditionLock alloc] initWithCondition:NO_OUTPUT_AVAILABLE];
	outputOnline = [[NSCondition alloc] init];
	[NSThread detachNewThreadSelector:@selector(asr33InputThread:) toTarget:self withObject:nil];
	[NSThread detachNewThreadSelector:@selector(asr33OutputThread:) toTarget:self withObject:nil];
	[windowController setWindowTitle:[[self pluginName] stringByDeletingPathExtension]];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil]; 
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyPreferencesChanged:)
		name:NSUserDefaultsDidChangeNotification object:nil];
	[self notifyPreferencesChanged:nil];
}


@end
