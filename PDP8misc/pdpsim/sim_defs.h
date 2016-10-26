/* Simulator definitions

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibted

   The interface between the simulator control package (SCP) and the
   simulator consists of the following routines and data structures

	sim_name		simulator name string
	sim_devices[]		array of pointers to simulated devices
	sim_PC			pointer to saved PC register descriptor
	sim_interval		simulator interval to next event
	sim_stop_messages	array of pointers to stop messages
	sim_instr()		instruction execution routine
	sim_load()		binary loader routine
*/

#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>

/* Simulator status codes

   0			ok
   1 - (SCPE_BASE - 1)	simulator specific
   SCPE_BASE - n	general
*/

#define SCPE_OK		0				/* normal return */
#define SCPE_BASE	32				/* base for messages */
#define SCPE_NXM	(SCPE_BASE + 0)			/* nxm */
#define SCPE_UNATT	(SCPE_BASE + 1)			/* no file */
#define SCPE_IOERR 	(SCPE_BASE + 2)			/* I/O error */
#define SCPE_CSUM	(SCPE_BASE + 3)			/* loader cksum */
#define SCPE_FMT	(SCPE_BASE + 4)			/* loader format */
#define SCPE_NOATT	(SCPE_BASE + 5)			/* not attachable */
#define SCPE_OPENERR	(SCPE_BASE + 6)			/* open error */
#define SCPE_MEM	(SCPE_BASE + 7)			/* alloc error */
#define SCPE_ARG	(SCPE_BASE + 8)			/* argument error */
#define SCPE_STEP	(SCPE_BASE + 9)			/* step expired */
#define SCPE_UNK	(SCPE_BASE + 10)		/* unknown command */
#define SCPE_RO		(SCPE_BASE + 11)		/* read only */
#define SCPE_INCOMP	(SCPE_BASE + 12)		/* incomplete */
#define SCPE_STOP	(SCPE_BASE + 13)		/* sim stopped */
#define SCPE_EXIT	(SCPE_BASE + 14)		/* sim exit */
#define SCPE_TTIERR	(SCPE_BASE + 15)		/* console tti err */
#define SCPE_TTOERR	(SCPE_BASE + 16)		/* console tto err */
#define SCPE_EOF	(SCPE_BASE + 17)		/* end of file */
#define SCPE_REL	(SCPE_BASE + 18)		/* relocation error */
#define SCPE_NOPARAM	(SCPE_BASE + 19)		/* no parameters */
#define SCPE_ALATT	(SCPE_BASE + 20)		/* already attached */
#define SCPE_KFLAG	01000				/* tti data flag */

/* Constants */

#define CBUFSIZE	128				/* string buf size */
#define PV_RZRO		0				/* right, zero fill */
#define PV_RSPC		1				/* right, space fill */
#define PV_LEFT		2				/* left justify */

/* Default timing parameters */

#define KBD_POLL_WAIT	5000				/* keyboard poll */
#define SERIAL_IN_WAIT	100				/* serial in time */
#define SERIAL_OUT_WAIT	10				/* serial output */
#define NOQUEUE_WAIT	10000				/* min check time */

/* Convert switch letter to bit mask */

#define SWMASK(x) (1 << (((int) (x)) - ((int) 'A')))

/* String match */

#define MATCH_CMD(ptr,cmd) strncmp ((ptr), (cmd), strlen (ptr))

/* Device data structure */

struct device {
	char		*name;				/* name */
	struct unit 	*units;				/* units */
	struct reg	*registers;			/* registers */
	struct mtab	*modifiers;			/* modifiers */
	int		numunits;			/* #units */
	int		aradix;				/* address radix */
	int		awidth;				/* address width */
	int		aincr;				/* addr increment */
	int		dradix;				/* data radix */
	int		dwidth;				/* data width */
	int		(*examine)();			/* examine routine */
	int		(*deposit)();			/* deposit routine */
	int		(*reset)();			/* reset routine */
	int		(*boot)();			/* boot routine */
	int		(*attach)();			/* attach routine */
	int		(*detach)();			/* detach routine */
};

/* Unit data structure

   Parts of the unit structure are device specific, that is, they are
   not referenced by the simulator control package and can be freely
   used by device simulators.  Fields starting with 'buf', and flags
   starting with 'UF', are device specific.  The definitions given here
   are for a typical sequential device.
*/

struct unit {
	struct unit	*next;				/* next active */
	int		(*action)();			/* action routine */
	char		*filename;			/* open file name */
	FILE		*fileref;			/* file reference */
	void		*filebuf;			/* memory buffer */
	int		hwmark;				/* high water mark */
	int		time;				/* time out */
	int		flags;				/* flags */
	int		capac;				/* capacity */
	int		pos;				/* file position */
	int		buf;				/* buffer */
	int		wait;				/* wait */
	int		u3;				/* device specific */
	int		u4;				/* device specific */
};

/* Unit flags */

#define UNIT_ATTABLE	000001				/* attachable */
#define UNIT_RO		000002				/* read only */
#define UNIT_FIX	000004				/* fixed capacity */
#define UNIT_SEQ	000010				/* sequential */
#define UNIT_ATT	000020				/* attached */
/* unused		000040 */
#define UNIT_BUFABLE	000100				/* bufferable */
#define UNIT_MUSTBUF	000200				/* must buffer */
#define UNIT_BUF	000400				/* buffered */
#define UNIT_V_UF	9				/* device specific */

/* Register descriptor */

struct reg {
	char		*name;				/* name */
	void		*loc;				/* location */
	int		radix;				/* radix */
	int		width;				/* width */
	int		offset;				/* starting bit */
	int		flags;				/* flags */
};

#define REG_FMT		003				/* see PV_x */
#define REG_RO		004				/* read only */
#define REG_HIDDEN	010				/* hidden */
#define REG_HRO		(REG_RO + REG_HIDDEN)		/* hidden, read only */

/* Command table */

struct ctab {
	char		*name;				/* name */
	int		(*action)();			/* action routine */
	int		arg;				/* argument */
};

/* Modifier table */

struct mtab {
	int	mask;					/* mask */
	int	match;					/* match */
	char	*pstring;				/* print string */
	char	*mstring;				/* match string */
	int	(*valid)();				/* validation routine */
};

/* The following macros define structure contents */

#define UDATA(act,fl,cap) NULL,act,NULL,NULL,NULL,0,0,(fl),(cap),0,0

#define ORDATA(nm,loc,wd) "nm", &(loc), 8, (wd), 0
#define DRDATA(nm,loc,wd) "nm", &(loc), 10, (wd), 0
#define HRDATA(nm,loc,wd) "nm", &(loc), 16, (wd), 0
#define FLDATA(nm,loc,pos) "nm", &(loc), 2, 1, (pos)
#define GRDATA(nm,loc,rdx,wd,pos) "nm", &(loc), (rdx), (wd), (pos)

/* Typedefs for principal structures */

typedef struct device DEVICE;
typedef struct unit UNIT;
typedef struct reg REG;
typedef struct ctab CTAB;
typedef struct mtab MTAB;
