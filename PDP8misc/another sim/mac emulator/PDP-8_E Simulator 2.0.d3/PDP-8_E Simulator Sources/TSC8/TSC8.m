/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	TSC8.m - TSC8-75 Board for the PDP-8/E Simulator
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
#import "PluginFramework/KeepInMenuWindow.h"
#import "PluginFramework/RegisterFormCell.h"

#import "TSC8.h"
#import "TSC8iot.h"


#define CODER_KEY_TSC8_ERTB		@"tsc8ERTB"
#define CODER_KEY_TSC8_ERIOT		@"tsc8ERIOT"
#define CODER_KEY_TSC8_ECDF		@"tsc8ECDF"
#define CODER_KEY_TSC8_ESME_ENABLED	@"tsc8ESME"
#define CODER_KEY_TSC8_IOFLAG		@"tsc8IOFLAG"
#define CODER_KEY_TSC8_IOMASK		@"tsc8IOMASK"

#define ERTB_CHANGED_NOTIFICATION	@"tsc8ERTBChangedNotification"
#define ERIOT_CHANGED_NOTIFICATION	@"tsc8ERIOTChangedNotification"
#define ECDF_CHANGED_NOTIFICATION	@"tsc8ECDFChangedNotification"


@implementation TSC8


#pragma mark Register Access


- (unsigned short) getERTB
{
	return [pdp8 getTSC8ertb];
}


- (void) setERTB:(ushort)ertb
{
	[pdp8 setTSC8ertb:ertb];
	[[NSNotificationCenter defaultCenter] postNotificationName:ERTB_CHANGED_NOTIFICATION object:self];	
}


- (unsigned short) getERIOT
{
	return [pdp8 getTSC8eriot];
}


- (void) setERIOT:(ushort)eriot
{
	[pdp8 setTSC8eriot:eriot];
	[[NSNotificationCenter defaultCenter] postNotificationName:ERIOT_CHANGED_NOTIFICATION object:self];	
}


- (BOOL) getESMEenabled
{
	return [pdp8 getTSC8esmeEnabled];
}


- (void) setESMEenabled:(BOOL)enabled
{
	[pdp8 setTSC8esmeEnabled:enabled];
	[esmeButton setIntValue:enabled];
}


- (IBAction) esmeEnabledClicked:(id)sender
{
	[self setESMEenabled:[sender intValue]];
}


- (unsigned short) getECDF
{
	return [pdp8 getTSC8ecdf];
}


- (void) setECDF:(ushort)ecdf
{
	[pdp8 setTSC8ecdf:ecdf];
	[[NSNotificationCenter defaultCenter] postNotificationName:ECDF_CHANGED_NOTIFICATION object:self];	
}


#pragma mark Plugin Methods


SETUP_PDP8_POINTER_FOR_IOTS


- (NSArray *) iotsForAddress:(int)ioAddress
{
	return [NSArray arrayWithObjects:
		[NSValue valueWithPointer:i6360],
		[NSValue valueWithPointer:i6361],
		[NSValue valueWithPointer:i6362],
		[NSValue valueWithPointer:i6363],
		[NSValue valueWithPointer:i6364],
		[NSValue valueWithPointer:i6365],
		[NSValue valueWithPointer:i6366],
		[NSValue valueWithPointer:i6367],
		nil];
}


- (NSArray *) skiptestsForAddress:(int)ioAddress
{
	return [NSArray arrayWithObjects:
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:s6361],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:s6363],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:s6365],
		[NSValue valueWithPointer:nil],
		[NSValue valueWithPointer:nil],
		nil];

}


- (void) setIOFlag:(unsigned long)flag forIOFlagName:(NSString *)name;
{
	[pdp8 setTSC8flag:flag];
}


- (void) CAF:(int)ioAddress
{
	[pdp8 clearIOFlagBits:[pdp8 getTSC8flag]];
	[pdp8 clearInterruptMaskBits:[pdp8 getTSC8flag]];
	[pdp8 setTSC8ertb:0];
	[pdp8 setTSC8eriot:0];
	[pdp8 setTSC8ecdf:0];
}


- (void) clearAllFlags:(int)ioAddress
{
	[pdp8 clearIOFlagBits:[pdp8 getTSC8flag]];
	[pdp8 clearInterruptMaskBits:[pdp8 getTSC8flag]];
	[self setERTB:0];
	[self setERIOT:0];
	[self setECDF:0];
}


- (void) resetDevice
{
	[self clearAllFlags:0];
}


#pragma mark Notifications


- (void) notifyGo:(NSNotification *)notification
{
	[ertbRegister setEnabled:NO];
	[eriotRegister setEnabled:NO];
	[ecdfRegister setEnabled:NO];
}


- (void) notifyStep:(NSNotification *)notification
{
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter postNotificationName:ERTB_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:ERIOT_CHANGED_NOTIFICATION object:self];
	[defaultCenter postNotificationName:ECDF_CHANGED_NOTIFICATION object:self];
}


- (void) notifyStop:(NSNotification *)notification
{
	[self notifyStep:notification];
	[ertbRegister setEnabled:YES];
	[eriotRegister setEnabled:YES];
	[ecdfRegister setEnabled:YES];
}


#pragma mark Initialization


- (id) initWithCoder:(NSCoder *)coder
{
	self = [super init];
	[self setERTB:[coder decodeIntForKey:CODER_KEY_TSC8_ERTB]];
	[self setERIOT:[coder decodeIntForKey:CODER_KEY_TSC8_ERIOT]];
	[self setECDF:[coder decodeIntForKey:CODER_KEY_TSC8_ECDF]];
	[self setESMEenabled:[coder decodeBoolForKey:CODER_KEY_TSC8_ESME_ENABLED]];
	unsigned long flag = [pdp8 getTSC8flag];
	[coder decodeBoolForKey:CODER_KEY_TSC8_IOFLAG] ?
		[pdp8 setIOFlagBits:flag] : [pdp8 clearIOFlagBits:flag];
	[coder decodeBoolForKey:CODER_KEY_TSC8_IOMASK] ?
		[pdp8 setInterruptMaskBits:flag] : [pdp8 clearInterruptMaskBits:flag];	
	return self;
}


- (void) encodeWithCoder:(NSCoder *)coder
{
	[coder encodeInt:[self getERTB] forKey:CODER_KEY_TSC8_ERTB];
	[coder encodeInt:[self getERIOT] forKey:CODER_KEY_TSC8_ERIOT];
	[coder encodeInt:[self getECDF] forKey:CODER_KEY_TSC8_ECDF];
	[coder encodeBool:[self getESMEenabled] forKey:CODER_KEY_TSC8_ESME_ENABLED];
	unsigned long flag = [pdp8 getTSC8flag];
	[coder encodeBool:[pdp8 getIOFlagBits:flag] ? YES : NO forKey:CODER_KEY_TSC8_IOFLAG];
	[coder encodeBool:[pdp8 getInterruptMaskBits:flag] ? YES : NO forKey:CODER_KEY_TSC8_IOMASK];
}


- (void) notifyApplicationWillTerminate:(NSNotification *)notification
{
	// NSLog (@"TSC8 notifyApplicationWillTerminate");
	NSMutableData *data = [NSMutableData data];
	NSKeyedArchiver *archiver = [[NSKeyedArchiver alloc] initForWritingWithMutableData:data];
	[self encodeWithCoder:archiver];
	[archiver finishEncoding];
	[archiver release];
	[[NSUserDefaults standardUserDefaults] setObject:data forKey:[self pluginName]];
}


- (void) notifyPluginsLoaded:(NSNotification *)notification
{
	[window orderBackFromDefaults:self];
}


- (void) pluginDidLoad
{
	[ertbRegister setupRegisterFor:self
		getRegisterValue:@selector(getERTB) setRegisterValue:@selector(setERTB:)
		changedNotificationName:ERTB_CHANGED_NOTIFICATION mask:07777 base:8];
	[eriotRegister setupRegisterFor:self
		getRegisterValue:@selector(getERIOT) setRegisterValue:@selector(setERIOT:)
		changedNotificationName:ERIOT_CHANGED_NOTIFICATION mask:07777 base:8];
	[ecdfRegister setupRegisterFor:self
		getRegisterValue:@selector(getECDF) setRegisterValue:@selector(setECDF:)
		changedNotificationName:ECDF_CHANGED_NOTIFICATION mask:01 base:8];
	NSData *data = [[NSUserDefaults standardUserDefaults] dataForKey:[self pluginName]];
	if (data) {
		NSKeyedUnarchiver *unarchiver = [[NSKeyedUnarchiver alloc] initForReadingWithData:data];
		self = [self initWithCoder:unarchiver];
		[unarchiver finishDecoding];
		[unarchiver release];
	}
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
	[defaultCenter addObserver:self selector:@selector(notifyApplicationWillTerminate:)
		name:NSApplicationWillTerminateNotification object:nil]; 
	[defaultCenter addObserver:self selector:@selector(notifyPluginsLoaded:)
		name:PLUGINS_LOADED_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyGo:) name:PDP8_GO_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyStop:) name:PDP8_STOP_NOTIFICATION object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self
		selector:@selector(notifyStep:) name:PDP8_STEP_NOTIFICATION object:nil];
}


@end
