   1            //
   2            //	PDP-8/E Simulator
   3            //
   4            //	Copyright (©) 1994-2014 Bernhard Baehr
   5            //
   6            //	colorfun.pa - Demo program for the Memory Content Plugin
   7            //
   8            //	This file is part of PDP-8/E Simulator.
   9            //
  10            //	PDP-8/E Simulator is free software: you can redistribute it and/or modify
  11            //	it under the terms of the GNU General Public License as published by
  12            //	the Free Software Foundation, either version 3 of the License, or
  13            //	(at your option) any later version.
  14            //
  15            //	This program is distributed in the hope that it will be useful,
  16            //	but WITHOUT ANY WARRANTY; without even the implied warranty of
  17            //	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  18            //	GNU General Public License for more details.
  19            //
  20            //	You should have received a copy of the GNU General Public License
  21            //	along with this program.  If not, see <http://www.gnu.org/licenses/>.
  22            //
  23            
  24            /
  25            /	ASSEMBLE THIS PROGRAM WITH PAL8, THEN LOAD THE BIN FILE TO FIELD 0
  26            /	START AT 200 WITH A PDP-8/E WITH ALL EIGHT MEMORY FIELDS AND REALTIME SPEED
  27            /	USE THE SWITCH REGISTER TO MODIFY THE COLOR SCHEME
  28            /	USE THE HALT KEY TO STOP THE PROGRAM, OTHERWISE IT WILL NEVER HALT
  29            /
  30            /	IT USES THE FIRST 28 WORDS OF MEMORY, ALL OTHER MEMORY IS FILLED WITH COLOR
  31            /
  32            
  33 00000 0020 	FIELD 0
  34            	*0
  35 00000 0034 PTR,	END
  36 00001 0034 FIRSTC,	END		/ ADDRESS OF FIRST LOCATION IN FIELD 0 TO PUT COLOR
  37 00002 0000 COLOR,	0
  38 00003 6171 CDFINI,	6171		/ = 6171 + 0010 = 6201 = CDF 0
  39 00004 0100 CDF8,	0100
  40            
  41            	*5
  42 00005 1020 NEXTC,	TAD FIELD	/ USE FIELD FOR "RANDOMIZATION"
  43 00006 7404 	OSR		/ GET SWITCH REGISTER TO CHANGE COLOR SCHEME
  44 00007 3002 	DCA COLOR	/ NEXT COLOR
  45            
  46            	*10		/ MAIN ROUTINE (LOCATED IN AUTOINCREMENT REGION!)
  47 00010 7307 MAIN,	CLA CLL IAC RTL
  48 00011 7004 	RAL		/ AC=0010
  49 00012 1020 	TAD FIELD	/ AC=CDF INSTRUCTION FOR NEXT FIELD
  50 00013 3020 	DCA FIELD	/ WRITE CDF INSTRUCTION FOR NEXT FIELD
  51 00014 1020 	TAD FIELD
  52 00015 0004 	AND CDF8	/ CHECK FOR SWITCH TO "FIELD 8" (6301)
  53 00016 7640 	SZA CLA
  54 00017 5027 	JMP RESET
  55 00020 6171 FIELD,	6171		/ NOTE: THIS IS LOCATION 20 (NOT AUTOINCREMENTING!)
  56 00021 1002 	TAD COLOR
  57 00022 3400 	DCA I PTR	/ PUT COLOR TO MEMORY CELL
  58 00023 2000 	ISZ PTR		/ FIELD COMPLETE?
  59 00024 5021 	JMP .-3		/ LOOP FOR COMPLETE FIELD
  60 00025 1002 	TAD COLOR
  61 00026 5005 	JMP NEXTC	/ NEXT INSTRUCTIONS IN THE GAP BETWEEN DATA AND MAIN
  62            
  63 00027 1003 RESET,	TAD CDFINI	/ RESET THE CDF INSTRUCTION
  64 00030 3020 	DCA FIELD
  65 00031 1001 	TAD FIRSTC
  66 00032 3000 	DCA PTR		/ SET POINTER TO END ADDRESS FOR FIELD 0 TO AVOID OVERWRITING THIS PROGRAM
  67 00033 5010 	JMP MAIN
  68 00034 0000 END,	0
  69            
  70            	*200
  71 00200 5010 START,	JMP MAIN
  72            
  73            $
