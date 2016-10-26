/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	FloatingPointNumber.h - Floating point numbers for format conversions
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


#import "FloatingPointNumber.h"


@implementation FloatingPointNumber


+ (FloatingPointNumber *) floatingPointNumberWithExponent:(int)e bias:(int)b negative:(BOOL)n
	twoComplementMantissa:(BOOL)tcm mantissa:(unsigned long long)m
{
	return [[[self alloc]
		initWithExponent:e bias:b negative:n twoComplementMantissa:tcm mantissa:m] autorelease];
}

	
+ (FloatingPointNumber *) floatingPointNumberFromString:(NSString *)string
	withBias:(int)bias withTwoComplementMantissa:(BOOL)tcm error:(NSString **)error
{
	decimal dec;
	short ok;
	int exponent;
	BOOL negative;
	unsigned long long mantissa;
	
	const char *p = [string cStringUsingEncoding:NSMacOSRomanStringEncoding];
	short ix = 0;
	str2dec (p, &ix, &dec, &ok);
	if (! ok) {
		if (error)
			*error = NSLocalizedString(@"Invalid floating point number.", @"");
		return nil;
	}
	double d = dec2num(&dec);
	if (! isfinite(d)) {
		if (error)
			*error = NSLocalizedString(@"IEEE overflow.", @"");
		return nil;
	}
	if (d != 0.0 && ! isnormal(d)) {
		if (error)
			*error = NSLocalizedString(@"IEEE underflow.", @"");
		return nil;
	}
	if (d < 0.0) {
		negative = YES;
		d = -d;
	} else
		negative = NO;
	d = frexp(d, &exponent);
	mantissa = (unsigned long long) ldexp(d, 64);
	if (bias) {
		if (d != 0.0)
			exponent += bias;
		if (exponent < 0 || 2 * bias <= exponent) {
			if (error)
				*error = NSLocalizedString(@"Floating point number out of range.", @"");
			return nil;
		}
	} else {	// exponent must be a 12-bit two complement integer
		if ((exponent & ~07777) != 0 && (exponent & ~07777) != ~07777) {
			if (error)
				*error = NSLocalizedString(@"Floating point number out of range.", @"");
			return nil;
		} else
			exponent &= 07777;
	}
	if (negative && tcm)
		mantissa = ~mantissa + 1;
	return [FloatingPointNumber floatingPointNumberWithExponent:exponent
		bias:bias negative:negative twoComplementMantissa:tcm mantissa:mantissa];
}


- (FloatingPointNumber *) initWithExponent:(int)e bias:(int)b negative:(BOOL)n
	twoComplementMantissa:(BOOL)tcm mantissa:(unsigned long long)m
{
	self = [super init];
	exponent = e;
	bias = b;
	negative = n;
	twoComplementMantissa = tcm;
	mantissa = m;
	return self;
}


- (NSString *) stringValueWithPrecision:(int)precision
{
	if (exponent == 0 && mantissa == 0)
		return @"0.0";
	unsigned long long mant = (negative && twoComplementMantissa) ? (~mantissa + 1) : mantissa;
	if ((mant >> 63) == 0)
		return NSLocalizedString(@"(not normalized)", @"");
	double d = ldexp((double) mant, -64);
	if (negative)
		d = -d;
	int exp = exponent;
	if (bias)
		exp -= bias;
	else {
		if (exp == 04000)	/* exponent -0 */
			exp = 0;
		if (exp & 04000)	/* negative 12-bit two complement exponent */
			exp |= ~07777;
	}
	d = ldexp(d, exp);
	if (! isfinite(d))
		return NSLocalizedString(@"(IEEE overflow)", @"");
	if (! isnormal(d))
		return NSLocalizedString(@"(IEEE underflow)", @"");
	return [NSString stringWithFormat:@"%.*g", precision, d];
}


- (int) exponent
{
	return exponent;
}


- (int) bias
{
	return bias;
}


- (BOOL) negative
{
	return negative;
}


- (BOOL) twoComplementMantissa
{
	return twoComplementMantissa;
}


- (unsigned long long) mantissa
{
	return mantissa;
}


@end
