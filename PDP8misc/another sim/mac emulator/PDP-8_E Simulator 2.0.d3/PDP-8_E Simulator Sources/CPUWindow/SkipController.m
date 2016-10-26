/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	SkipController.h - Controller for the skip indicator in the CPU window
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


#import "SkipController.h"
#import "TableCornerView.h"
#import "PDP8.h"
#import "mri.h"
#import "eae.h"
#import "iot.h"
#import "opr.h"


#define UPDATE_SKIPINDICATOR_NOTIFICATION	@"updateSkipIndicatorNotification"


#define SKIP_IMAGE_NAME				@"skipArrow"
#define INTERRUPT_IMAGE_NAME			@"interruptArrow"


// Key Value pair for the skiptest dictionary initialization
#define KV(i, s)	[NSValue valueWithPointer:s], [NSValue valueWithPointer:i]


typedef unsigned (*PDP8SkiptestFunctionPointer)(void);
				

@implementation SkipController


- (void) awakeFromNib
{
	NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];

	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:MEMORY_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:PROGRAM_COUNTER_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:ACCUMULATOR_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:GTF_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:MQ_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:EAE_MODE_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:DF_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:UF_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:ENABLE_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:DELAY_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:INHIBIT_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:IOFLAGS_CHANGED_NOTIFICATION object:nil];
	[defaultCenter addObserver:self selector:@selector(notifySkipIndicator:)
		name:SF_CHANGED_NOTIFICATION object:nil];	// required for TSC8 ESME
	[defaultCenter addObserver:self selector:@selector(notifyUpdateSkipIndicator:)
		name:UPDATE_SKIPINDICATOR_NOTIFICATION object:nil]; 
	/* PDP-8 notifications that probably can't change the skip/interrupt indicator:
	   SR_CHANGED_NOTIFICATION
	   SC_CHANGED_NOTIFICATION
	   UB_CHANGED_NOTIFICATION
	   SF_CHANGED_NOTIFICATION
	*/
	dictionary = [[NSMutableDictionary alloc] initWithObjectsAndKeys:
	/* MRI skip instructions */
		KV(i2000, s2000), KV(i2200, s2200), KV(i2400, s2400), KV(i2410, s2410),
		KV(i2600, s2600), KV(i2610, s2610),
	/* EAE skip instructions */
		KV(i7451, s7451),
	/* IOT skip instructions */
		KV(i6000, s6000), KV(i6003, s6003), KV(i6006, s6006), KV(i6254, s6254),
	/* OPR skip instructions */
		KV(i7410, s7410), KV(i7412, s7410), KV(i7414, s7410), KV(i7416, s7410),
		KV(i7420, s7420), KV(i7422, s7420), KV(i7424, s7420), KV(i7426, s7420),
		KV(i7430, s7430), KV(i7432, s7430), KV(i7434, s7430), KV(i7436, s7430),
		KV(i7440, s7440), KV(i7442, s7440), KV(i7444, s7440), KV(i7446, s7440),
		KV(i7450, s7450), KV(i7452, s7450), KV(i7454, s7450), KV(i7456, s7450),
		KV(i7460, s7460), KV(i7462, s7460), KV(i7464, s7460), KV(i7466, s7460),
		KV(i7470, s7470), KV(i7472, s7470), KV(i7474, s7470), KV(i7476, s7470),
		KV(i7500, s7500), KV(i7502, s7500), KV(i7504, s7500), KV(i7506, s7500),
		KV(i7510, s7510), KV(i7512, s7510), KV(i7514, s7510), KV(i7516, s7510),
		KV(i7520, s7520), KV(i7522, s7520), KV(i7524, s7520), KV(i7526, s7520),
		KV(i7530, s7530), KV(i7532, s7530), KV(i7534, s7530), KV(i7536, s7530),
		KV(i7540, s7540), KV(i7542, s7540), KV(i7544, s7540), KV(i7546, s7540),
		KV(i7550, s7550), KV(i7552, s7550), KV(i7554, s7550), KV(i7556, s7550),
		KV(i7610, s7410), KV(i7612, s7410), KV(i7614, s7410), KV(i7616, s7410),
		KV(i7620, s7420), KV(i7622, s7420), KV(i7624, s7420), KV(i7626, s7420),
		KV(i7630, s7430), KV(i7632, s7430), KV(i7634, s7430), KV(i7636, s7430),
		KV(i7640, s7440), KV(i7642, s7440), KV(i7644, s7440), KV(i7646, s7440),
		KV(i7650, s7450), KV(i7652, s7450), KV(i7654, s7450), KV(i7656, s7450),
		KV(i7660, s7460), KV(i7662, s7460), KV(i7664, s7460), KV(i7666, s7460),
		KV(i7670, s7470), KV(i7672, s7470), KV(i7674, s7470), KV(i7676, s7470),
		KV(i7700, s7500), KV(i7702, s7500), KV(i7704, s7500), KV(i7706, s7500),
		KV(i7710, s7510), KV(i7712, s7510), KV(i7714, s7510), KV(i7716, s7510),
		KV(i7720, s7520), KV(i7722, s7520), KV(i7724, s7520), KV(i7726, s7520),
		KV(i7730, s7530), KV(i7732, s7530), KV(i7734, s7530), KV(i7736, s7530),
		KV(i7740, s7540), KV(i7742, s7540), KV(i7744, s7540), KV(i7746, s7540),
		KV(i7750, s7550), KV(i7752, s7550), KV(i7754, s7550), KV(i7756, s7550),
		KV(i7760, s7560), KV(i7762, s7560), KV(i7764, s7560), KV(i7766, s7560),
		KV(i7770, s7570), KV(i7772, s7570), KV(i7774, s7570), KV(i7776, s7570),
		nil
	];
}


- (void) notifySkipIndicator:(NSNotification *)notification
{
	/* coalesc the multiple ..._CHANGED_NOTIFICATIONS to one UPDATE_SKIPINDICATOR_NOTIFICATION
	   to avoid time consuming repeated updates */
	// NSLog (@"notifySkipIndicator");
	[[NSNotificationQueue defaultQueue] enqueueNotification:
		[NSNotification notificationWithName:UPDATE_SKIPINDICATOR_NOTIFICATION object:self]
		postingStyle:NSPostASAP coalesceMask:NSNotificationCoalescingOnName forModes:nil];
}


- (void) notifyUpdateSkipIndicator:(NSNotification *)notification
{
	// NSLog (@"SkipController notifyUpdateSkipIndicator");
	NSAssert (! [pdp8 isGoing], @"PDP-8 is running");
	if ([pdp8 getEnable] && ([pdp8 getIOFlagBits:~0] & [pdp8 getInterruptMaskBits:~0]) &&
		! ([pdp8 getDelay] || [pdp8 getInhibit]))
		[view setImageNamed:INTERRUPT_IMAGE_NAME toolTip:
			NSLocalizedString(@"An interrupt is pending", @"")];
	else {
		NSValue *skipfunc = [dictionary objectForKey:
			[NSValue valueWithPointer:[pdp8 getNextInstruction]]];
		if (skipfunc) {
			if (((PDP8SkiptestFunctionPointer)[skipfunc pointerValue])())
				[view setImageNamed:SKIP_IMAGE_NAME toolTip:
					NSLocalizedString(@"The next instruction will be skipped", @"")];
			else
				[view setImageNamed:nil toolTip:nil];
		} else
			[view setImageNamed:nil toolTip:nil];
	}
}


- (void) addSkiptest:(NSValue *)skiptest forInstruction:(NSValue *)instruction
{
	if (skiptest && [skiptest pointerValue] && instruction && [instruction pointerValue])
		[dictionary setObject:skiptest forKey:instruction];
}


@end
