/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	OctalFormatter.m - Formatter for octal numbers
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


#import "OctalFormatter.h"


@implementation OctalFormatter


+ (OctalFormatter *) formatterWithBitMask:(unsigned)mask wildcardAllowed:(BOOL)withWildcard
{
	return [[[OctalFormatter alloc] initWithBitMask:mask wildcardAllowed:withWildcard] autorelease];
}


- (OctalFormatter *) initWithBitMask:(unsigned)mask wildcardAllowed:(BOOL)withWildcard
{
	if ((self = [super init])) {
		wildcardAllowed = withWildcard;
		bitMask = mask;
		int digits = 0;
		while (mask) {
			digits++;
			mask >>= 3;
		}   
		outputFormat = [[NSString alloc] initWithFormat:@"%%%d.%do", digits, digits];
		numberOfWords = 1;
	}
	return self;
}


- (void) setNumberOfWords:(unsigned)n
{
	numberOfWords = n;
}


- (NSString *) stringForObjectValue:(id)value
{
	unsigned i;
	
	NSMutableString *string = nil;
	if ([value isKindOfClass:[NSNumber class]])
		string = [NSString stringWithFormat:outputFormat, [value intValue]];
	else if ([value isKindOfClass:[NSArray class]]) {
		unsigned count = [value count];
		if (numberOfWords < count)
			count = numberOfWords;
		string = [NSMutableString stringWithCapacity:5 * count];
		for (i = 0; i < count; i++) {
			if (i > 0)
				[string appendString:@" "];
			[string appendFormat:outputFormat, [[value objectAtIndex:i] intValue]];
		}
	}
	return string;
}


- (BOOL) getObjectValue:(id *)value forString:(NSString *)string errorDescription:(NSString **)error
/* For string without placeholder, value becomes a NSNumber (for numberOfWords == 1) or an
   NSArray of NSNumbers (for numberOfWords > 1). For strings with placeholder, values becomes
   an asencding sorted NSArray of NSNumbers (for numberOfWords == 1) or an NSArray of such NSArrays
   (for numberOfWords > 1). */
{
	char buf[128];
	char *bp = buf;
	NSMutableArray *values = [NSMutableArray arrayWithCapacity:numberOfWords];
	[string getCString:buf maxLength:(sizeof(buf) - 1) encoding:NSMacOSRomanStringEncoding];
	while (*bp == ' ')
		bp++;
	while (('0' <= *bp && *bp < '8') || (wildcardAllowed && *bp == '#')) {
		unsigned val = 0;
		unsigned pattern = 0;
		while (('0' <= *bp && *bp < '8') || (wildcardAllowed && *bp == '#')) {
			pattern <<= 3;
			if ('0' <= *bp && *bp < '8')
				val = val * 8  + (*bp - '0');
			else {
				val *= 8;
				pattern |= 7;
			}
			bp++;
		}
		while (*bp == ' ')
			bp++;
		if ((val | pattern) & ~bitMask) {
			if (error)
				*error = [NSString stringWithFormat:NSLocalizedString(
					@"The octal number is of range. The largest allowed number is %o.",
					@""), bitMask];
			return FALSE;
		}
		if (pattern) {
			unsigned long mask = pattern;
			NSMutableArray *patternValues = [NSMutableArray arrayWithCapacity:8];
			do {
				[patternValues insertObject:[NSNumber numberWithInt:val + pattern]
					atIndex:0];
				pattern = (pattern - 1) & mask;
			} while (pattern != mask);
			[values addObject:patternValues];
		} else
			[values addObject:[NSNumber numberWithInt:val]];
	}
	if (*bp || [values count] != numberOfWords) {
		if (error)
			*error = (numberOfWords == 1) ?
				(wildcardAllowed ?
					NSLocalizedString(
						@"Invalid input. An octal number is expected. "
						@"The wildcard character # is allowed.", @"") :
					NSLocalizedString(
						@"Invalid input. An octal number is expected.", @"")) :
				[NSString stringWithFormat:wildcardAllowed ?
					NSLocalizedString(
						@"Invalid input. %d octal numbers are expected. "
						@"The wildcard character # is allowed.", @"") :
					NSLocalizedString(
						@"Invalid input. %d octal numbers are expected.", @""),
					numberOfWords];
		return FALSE;					
	}
	*value = (numberOfWords == 1) ? [values objectAtIndex:0] : values;
	return TRUE;
}


@end
