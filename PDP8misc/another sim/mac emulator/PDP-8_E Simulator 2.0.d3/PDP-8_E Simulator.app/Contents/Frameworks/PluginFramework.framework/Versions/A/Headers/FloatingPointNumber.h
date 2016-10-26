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


#import <Cocoa/Cocoa.h>


@interface FloatingPointNumber : NSObject
{
@private
	int	exponent;		// Exponent of the floating point number
	int	bias;			// Bias of the exponent
					// Bias == 0 means that exponent is a 12-bit two complement number
	BOOL	negative;		// Sign of the floating point number
	BOOL	twoComplementMantissa;	// True iff mantissa is given in two complement
	unsigned long long mantissa;	// 64-bit mantissa
}

+ (FloatingPointNumber *) floatingPointNumberWithExponent:(int)e bias:(int)b negative:(BOOL)n
	twoComplementMantissa:(BOOL)tcm mantissa:(unsigned long long)m;
+ (FloatingPointNumber *) floatingPointNumberFromString:(NSString *)string withBias:(int)bias
	withTwoComplementMantissa:(BOOL)tcm error:(NSString **)error;

- (FloatingPointNumber *) initWithExponent:(int)e bias:(int)b negative:(BOOL)n
	twoComplementMantissa:(BOOL)tcm mantissa:(unsigned long long)m;
- (NSString *) stringValueWithPrecision:(int)precision;
- (int) exponent;
- (int) bias;
- (BOOL) negative;
- (BOOL) twoComplementMantissa;
- (unsigned long long) mantissa;

@end
