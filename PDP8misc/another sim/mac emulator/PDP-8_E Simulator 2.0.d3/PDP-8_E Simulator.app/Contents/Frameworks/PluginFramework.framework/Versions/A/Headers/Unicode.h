/*
 *	PDP-8/E Simulator
 *
 *	Copyright © 1994-2014 Bernhard Baehr
 *
 *	Unicode.h - Some unicode character constants
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


/* Don't use Unicode string constants (concatenation of string constants using the UNICODE_..._UTF8
   defines): with Xcode 3.1 beta (iPhone SDK beta), all Unicode strings in a compilation unit are replaced
   with the first one. Reported to Apple with bug ID 5788599. */
   
   
#define UNICODE_EM_DASH				0x2014		// —
//#define UNICODE_EM_DASH_UTF8			"\xe2\x80\x94"
#define UNICODE_LEFT_DOUBLEQUOTE		0x201c		// “
//#define UNICODE_LEFT_DOUBLEQUOTE_UTF8		"\xe2\x80\x9c"
#define UNICODE_RIGHT_DOUBLEQUOTE		0x201d		// ”
//#define UNICODE_RIGHT_DOUBLEQUOTE_UTF8	"\xe2\x80\x9d"
#define UNICODE_MIDDLE_DOT			0x00b7		// ·
//#define UNICODE_MIDDLE_DOT_UTF8		"\xc2\xb7"	// Middle Dot UTF8 is invisible in tool tips
#define UNICODE_BULLET_OPERATOR			0x2219		// ∙
//#define UNICODE_BULLET_OPERATOR_UTF8		"\xe2\x88\x99"	// Bullet Op. UTF8 is invisible in tool tips
#define UNICODE_BULLET				0x2022		// •
//#define UNICODE_BULLET_UTF8			"\xe2\x80\xa2"
#define UNICODE_DIAMOND				0x25c6		// ◆
//#define UNICODE_DIAMOND_UTF8			"\xe2\x97\x86"


