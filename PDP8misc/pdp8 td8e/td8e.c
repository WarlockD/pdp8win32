/* File: td8e.c
   Author: Gerold Pauler, 10711 Berlin, Germany
   Date: Sep 14, 2003
   Language: C (UNIX)
   Purpose: DEC TD8 TU56 tape interface

   Based on the description in the
   pdp8/e pdp8/f pdp8/m Maintenance Man. Digital Equipment Corporation, 1972.
   Small Computer Handbook Digital Equipment Corporation, 1966/67, 1967 or 1973.
   TD8E DECtape Diagnostic Digital Equipment Corporation, 1971,72.

   A simulated TU56 tape is a file containing the string "tu56" as the
   first 4 characters of the first 256 byte block.  The actual data
   sectors of the file occupy consecutive 129 word blocks starting with
   byte 4.
*/

#include "realtime.h"
#include "bus.h"
#include <stdio.h>

#ifdef DEBUG
#define PRINTDEB printf
#else
#define PRINTDEB
#endif

/***********/
/* formats */
/***********/

/* TU56 physical tape layout */
#define lines_per_word     4
#define lines_per_cell     6
#define cells_per_block   96
#define words_per_block  129
#define blocks_per_tape 1474
#define cells_per_tape  (blocks_per_tape * cells_per_block)
#define lines_per_tape  (cells_per_tape * lines_per_cell)

#define min_line           0
#define max_line        (lines_per_tape - 1)

#define REV_END_MARK     055
#define INTER_BLOCK      025
#define BLOCK_MARK       026
#define REV_GUARD_MARK   032
#define BLOCK_START      010
#define DATA_MARK        070
#define BLOCK_END        073
#define GUARD_MARK       051
#define REV_BLOCK_MARK   045
#define END_MARK         022

/* layout of file used to emulate tape */
/* bytes 0 through 3 are "tu56" */

#define DJG	/* to use image files from David Gessweins website */
#ifdef DJG
#define tag_length 0
#else
#define tag_length 4
#endif

/**********/
/* delays */
/**********/

/* TD8E interface to TU56 controller delay, per data chunk */
#define single_line_time ((30 * microsecond) / IOFUDGE ) /* 3-bit Bytes */

/* TU56 seek times */
#define uts_time         ((120 * millisecond) / IOFUDGE )

/*********************************************************/
/* Interface between device implementation and "console" */
/*********************************************************/

/* files used to simulate the device */
static FILE *tape[2];
static char tapename[2][NAME_LENGTH];
static int current_line[2];
static int cell[cells_per_block + 1];

/* timers used to simulate delays between I/O initiation and completion */
static struct timer td8e_delay;
static struct timer tu56_delay;

static tapeclose(u)
int u;
{
	if (tape[u] != NULL) {
		fclose( tape[u] );
		tape[u] = NULL;
		tapename[u][0] = '\0';
	}
}

static int tapeopen(u, f)
int u;
char * f;
{
	tapeclose(u);
	set_file_name( tapename[u], f );
	if ((tape[u] = fopen( tapename[u], "r+" )) == NULL) { /* can't open */
		tapename[u][0] = '\0';
	}
#ifndef DJG
	else { /* the file exists, is it an TU56 tape? */
		if (fread( tapetag[u], tag_length, 1, tape[u] ) == 1) {
			/* tag readable */
			if ((tapetag[u][0] != 't') || (tapetag[u][1] != 'u')
			 || (tapetag[u][2] != '5') || (tapetag[u][3] != '6')) {
				/* bad header */
				tapeclose(u);
			}
		} else { /* couldn't read tag */
			tapeclose(u);
		};
	}
#endif
	current_line[u] = min_line + 2520; /* should be randomized */
	return (tape[u] != NULL);
}

td8epower() /* power-on initialize */
{
	int i = 0;
	int j = 0;

	PRINTDEB( "td8epower\n" );
	cell[i++] = INTER_BLOCK;
	cell[i++] = BLOCK_MARK;
	cell[i++] = REV_GUARD_MARK;
	for ( j=1; j <= 4; j++ ) {
		cell[i++] = BLOCK_START;
	}	
	for ( j=1; j <= 82; j++ ) {
		cell[i++] = DATA_MARK;
	}
	for ( j=1; j <= 4; j++ ) {
		cell[i++] = BLOCK_END;
	}	
	cell[i++] = GUARD_MARK;
	cell[i++] = REV_BLOCK_MARK;
	cell[i++] = INTER_BLOCK;
	cell[i] = END_MARK;

	tape[0] = NULL;
	tape[1] = NULL;
	tapename[0][0] = '\0';
	tapename[1][0] = '\0';
	init_timer( td8e_delay );
	init_timer( tu56_delay );
	register_device( tapeopen, tapeclose, 1,
			 "DTA1", "-- TU56 tape drive 1     ",
			 tapename[1] );
	register_device( tapeopen, tapeclose, 0,
			 "DTA0", "-- TU56 tape drive 0     ",
			 tapename[0] );
}


/*************************************/
/* "officially visible" device state */
/*************************************/

static int command_register = 0;
static int diff_command_register = 0;
static int old_command_register = 0;
static int mark_track = 0;

#define unit       ((command_register >> 11) & 1)
#define direction  ((command_register >> 10) & 1)
#define stop_go    ((command_register >> 9) & 1)
#define read_write ((command_register >> 8) & 1)
#define write_lock ((command_register >> 7) & 1)
#define sel_tm_err ((command_register >> 6) & 1)

#define set_sel_tm_err (command_register = (command_register & 07377) | 0100)

static int data_register = 0;

static int single_line_flag = 0;
static int quad_line_flag = 0;
static int qlines = 0;

static int uts = 0;
static int tapedir = 0;

/*************************/
/* Device implementation */
/*************************/

#define current_cell  (int)((current_line[unit] / lines_per_cell) \
				% cells_per_block)
#define current_block (int)((current_line[unit] / lines_per_cell) \
				/ cells_per_block)

static int checksum( block )
int block;
{
	int i = 0;
//	int cs = 025;
	int cs = 0;
	unsigned short tmp_data[words_per_block];
	
	fseek( tape[unit],
		(long int)((block * words_per_block) * 2) + tag_length,
		SEEK_SET );
	if ( fread(tmp_data, 2, words_per_block, tape[unit])
			!= words_per_block ) {
		command_register |= 1 << 7;
	}
	for ( i = 0; i < words_per_block; i++ ) {
		cs ^= (tmp_data[i] & 077);
		cs ^= (tmp_data[i] >> 6) & 077;
		cs &= 077;
	}
	PRINTDEB( "checksum %o, block %d\n", cs, block );
	return( (cs << 6) & 07700 );
}

static void tu56_event(p)
int p;
{ /* called from timer as each 3bit line spins by tape head */
	int markbit = 0;
	unsigned short tmp_data = 0; 
	long int current_word = 0L;
	long int block_line = 0L;

	PRINTDEB( "tu56_event\n" );
	if (tape[unit] == NULL) {
		set_sel_tm_err;
		/* schedule next line to spin by head */
		schedule( &tu56_delay, single_line_time, tu56_event, 0 );
		return;
	}

	PRINTDEB( "stop_go %d, uts %d, qlines %d\n", stop_go, uts, qlines );
	if (stop_go == 1) {
		if (uts == 1) {
			single_line_flag = 1;
			qlines = ++qlines % lines_per_word;
			if (qlines == 0) {
				if ( quad_line_flag == 1 ) {
					set_sel_tm_err;
				}
				quad_line_flag = 1;
			}
		}
		else {
			single_line_flag = 0;
			quad_line_flag = 0;
		}
		/* make it spin even on speedup*/
		if (tapedir == 0) {
			current_line[unit]++;
			if (current_line[unit] > max_line + 4096L) {
				current_line[unit] = max_line + 4091L;
			}
		}
		else {
			current_line[unit]--;
			if (current_line[unit] < -4096L) {
				current_line[unit] = - 4091L;
			}
		}
	}
	else { /* stop_go = 0 */
		single_line_flag = 0;
		quad_line_flag = 0;
		if ( uts == 0 ) {
			/* spin on break till uts timed out*/
			if (tapedir == 0) {
				current_line[unit]++;
				if (current_line[unit] > max_line + 4096L) {
					current_line[unit] = max_line + 4091L;
				}
			}
			else {
				current_line[unit]--;
				if (current_line[unit] < -4096L) {
					current_line[unit] = -4091L;
				}
			}
		}
	}

	if ( current_line[unit] <= -100L ) {
		markbit = (REV_END_MARK >>
			(5 + (current_line[unit] % lines_per_cell))) & 1;
	}
	else if ( current_line[unit] <= 0L ) {
		markbit = (INTER_BLOCK >>
			(5 + (current_line[unit] % lines_per_cell))) & 1;
	}
	else if ( current_line[unit] < max_line ) {
		markbit = (cell[current_cell] >>
			(5 - (current_line[unit] % lines_per_cell))) & 1;
	}
	else if ( current_line[unit] < (max_line + 100L) ) {
		markbit = (INTER_BLOCK >>
			(5 - (current_line[unit] % lines_per_cell))) & 1;
	}
	else if ( current_line[unit] >= (max_line + 100L) ) {
		markbit = (END_MARK >>
			(5 - (current_line[unit] % lines_per_cell))) & 1;
	}
	PRINTDEB( "current_line %dl, mod %d, qlines %d\n", current_line[unit],
			current_line[unit] % lines_per_cell, qlines);

	PRINTDEB( "markbit %o,", markbit );
	markbit ^= tapedir;
	PRINTDEB( " %o\n", markbit );

	if ( uts == 1 ) {
		mark_track = ( (mark_track << 1) & 076 ) | markbit;
		command_register = ( command_register & 07700 ) | mark_track;
		PRINTDEB( "mark_track %o\n", mark_track );
	}
	if ( mark_track == BLOCK_MARK ) {
		if ( read_write == 0 ) {
			data_register = ( current_block ) & 07777;
			PRINTDEB( "block %d\n", data_register );
		}
	}
	else if ( mark_track == REV_BLOCK_MARK ) {
		if ( read_write == 0 ) {
			data_register = ( current_block ) & 07777;
			PRINTDEB( "block %d\n", data_register );
		}
	}
	else if ( (mark_track == BLOCK_START) &&
			((current_cell == 4) || (current_cell == 91)) ) {
		if ( read_write == 0 ) {
			if ( current_cell == 4 ) {
				data_register = 07777;
			}
			else if ( current_cell == 91 ) {
				data_register =
					checksum( current_block );
			}
		}
	}
	else if ( (read_write == 0) && (quad_line_flag == 1) &&
			(current_cell == 4) && (tapedir == 1) ) {
		data_register = 0;
	}
	else if ( (read_write == 0) && (quad_line_flag == 1) &&
			(current_cell == 91) ) {
			data_register = checksum( current_block );
	}
	else if ( (current_cell >=  5) && (current_cell <= 90) &&
		  (quad_line_flag == 1) ) {
		block_line = current_line[unit] -
			(current_block * cells_per_block * lines_per_cell) -
			(5 * lines_per_cell);
		current_word = (long)(block_line / lines_per_word) +
				(long)(current_block * words_per_block);
		PRINTDEB( "block %d, cell %d, bline %d, word %dl",
				current_block, current_cell, block_line,
				current_word );
		fseek( tape[unit],
			(long)((current_word * 2L) + tag_length), SEEK_SET );
		if ( read_write == 0 ) { /* read */
			if ( fread(&tmp_data, 2, 1, tape[unit]) != 1 ) {
				command_register |= 1 << 7;
			}
			data_register = tmp_data & 07777;
		}
		else { /* write */
			tmp_data = data_register & 07777;
			if ( fwrite(&tmp_data, 2, 1, tape[unit]) != 1 ) {
				command_register |= 1 << 7;
			}
		}
	}

	/* schedule next line to spin by head */
	schedule( &tu56_delay, single_line_time, tu56_event, 0 );
}

/* events to which the TD8E controller responds */
#define td8_uts      1 /* write command register */
#define td8_delay    2 /* write command register */

static void td8e_event(p)
int p;
{ /* called from timer when an interface delay has completed */
	PRINTDEB( "td8e_event\n" );
	switch (p) {
	case td8_uts:
		schedule( &td8e_delay, uts_time, td8e_event, td8_delay );
		tapedir = direction;
		mark_track = 0;
		uts = 0;
		break;
	case td8_delay:
		uts = 1;
		break;
	}
}


/***********************************************/
/* Initialization used by CAF and reset switch */
/***********************************************/

td8einit() /* console reset or programmed reset */
{
	int u;
	PRINTDEB( "td8einit\n" );
	for (u = 0; u <= 1; u++) {
		if ( (current_line[u] <= min_line)
		   ||(current_line[u] >= max_line)) {
			current_line[u] = min_line + 2520;
		}
	}

	diff_command_register = 0;
	old_command_register = 0;
	command_register = 0;
	data_register = 0;
	
	mark_track = 0;
	uts = 0;
	single_line_flag = 0;
	quad_line_flag= 0;
	qlines = 0;

	schedule( &tu56_delay, single_line_time, tu56_event, 0 );
}

/********************/
/* IOT Instructions */
/********************/

td8edev(op)
int op;
{
	PRINTDEB( "td8edev" );
	switch (op & 7) {
	case 00: /* NOP */
		break;
	case 01: /* SDSS */
		PRINTDEB( " --- SDSS\n" );
		if (single_line_flag == 1) {
			pc = (pc + 1) & 07777;
		}
		break;
	case 02: /* SDST */
		PRINTDEB( " --- SDST --- at %o\n", cpma );
		if (sel_tm_err == 1) {
			pc = (pc + 1) & 07777;
		}
		break;
	case 03: /* SDSQ */
		PRINTDEB( " --- SDSQ\n" );
		if (quad_line_flag == 1) {
			pc = (pc + 1) & 07777;
		}
		break;
	case 04: /* SDLC */
		PRINTDEB( " --- SDLC ac=%o --- command reg=%o --- at %o\n",
				ac,command_register, cpma );
		/*
		 * set command register and clear accu
		 * as a side effect reset select time error
		 */
		diff_command_register = ( ac ^ command_register ) & 07400;
		PRINTDEB( " ---------- diff command reg=%o\n",
				diff_command_register );
		old_command_register = command_register & 07400;
		command_register = (command_register & 0200) | (ac & 07400);
		ac = 0;
		/*
		 * only read/write flag changed?
		 */
		if ( ((diff_command_register >> 8) & 017) == 01 ) {
			PRINTDEB( " ---------- only R/W\n" );
			schedule( &tu56_delay, single_line_time, tu56_event,0 );
		}
		/*
		 * other flags changed (too)?
		 */
		else if ( ((diff_command_register >> 9) & 07) == 0 ){
			PRINTDEB( " ---------- nothing changed\n" );
			schedule( &tu56_delay, single_line_time, tu56_event,0 );
		}
		else {
			int delay;
			PRINTDEB( " ---------- normal uts\n" );
			switch ( (diff_command_register >> 9) & 07 ) {
			default:	/* 1, 2, 4, 5, 6 */
				delay = uts_time;
				break;
		/*
		 * stop/go and direction changed at once?
		 * this means break penalty
		 */
			PRINTDEB( " ---------- break penalty\n" );
			case 3:
			case 7:
				delay = 2 * uts_time;
				break;
			}
			mark_track = 0;
			uts = 0;
			single_line_flag = 0;
			quad_line_flag = 0;
			qlines = 0;
			schedule( &tu56_delay, single_line_time, tu56_event,0 );
			schedule( &td8e_delay, delay, td8e_event, td8_uts );
		}
		break;
	case 05: /* SDLD */
		PRINTDEB( " --- SDLD ac=%o --- at %o\n", ac, cpma );
		data_register = ac;
		single_line_flag = 0;
		quad_line_flag = 0;
		qlines = 0;
		break;
	case 06: /* SDRC */
		PRINTDEB( " --- SDRC cr=%o --- at %o\n", command_register,
				cpma );
		ac = command_register;
		single_line_flag = 0;
		quad_line_flag = 0;
		qlines = 0;
		break;
	case 07: /* SDRD */
		PRINTDEB( " --- SDRD dr=%o --- at %o\n", data_register, cpma );
		ac = data_register;
		single_line_flag = 0;
		quad_line_flag = 0;
		qlines = 0;
		break;
	}
}
