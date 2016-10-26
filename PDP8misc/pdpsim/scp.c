/* Simulator control program

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited
*/

#include "sim_defs.h"
#include <limits.h>
#include <signal.h>
#include <ctype.h>
#define EX_D	0					/* deposit */
#define EX_E	1					/* examine */
#define EX_I	2					/* interactive */
#define SWHIDE	(1 << 26)				/* enable hiding */
#define RU_RUN	0					/* run */
#define RU_GO	1					/* go */
#define RU_STEP 2					/* step */
#define RU_CONT 3					/* continue */
#define RU_BOOT 4					/* boot */
#define UPDATE_SIM_TIME(x) sim_time = sim_time + (x - sim_interval); \
	x = sim_interval

extern char sim_name[];
extern DEVICE *sim_devices[];
extern REG *sim_PC;
extern char *sim_stop_messages[];
extern int sim_instr (void);
extern int sim_load (FILE *ptr);
extern int ttinit (void);
extern int ttrunstate (void);
extern int ttcmdstate (void);
extern int ttclose (void);
UNIT *sim_clock_queue = NULL;
int stop_cpu = 0;
int sim_interval = 0;
static double sim_time;
static int noqueue_time;

#define print_val(a,b,c,d) fprint_val (stdout, (a), (b), (c), (d))
#define SZ_D(dp) (size_map[((dp) -> dwidth + 7)/8])
#define SZ_R(rp) (size_map[((rp) -> width + (rp) -> offset + 7)/8])

char *get_glyph (char *iptr, char *optr);
int get_switches (char *cptr);
unsigned int get_rval (REG *rptr);
void put_rval (REG *rptr, unsigned int val, unsigned int mask);
int fprint_val (FILE *stream, unsigned int val, int rdx, int wid, int fmt);
char *read_line (char *ptr, int size, FILE *stream);
DEVICE *find_device (char *ptr, int *iptr);
DEVICE *find_dev_from_unit (UNIT *uptr);
REG *find_reg (char *ptr, char **optr, DEVICE *dptr);
int reset_all (int start_device);
int attach_unit (UNIT *uptr, char *cptr);
int detach_all (int start_device);
int detach_unit (UNIT *uptr);
int ex_reg (int flag, int sw, REG *rptr);
int dep_reg (int flag, int sw, char *cptr, REG *rptr);
int ex_addr (int flag, int sw, unsigned int addr, DEVICE *dptr, UNIT *uptr);
int dep_addr (int flag, int sw, char *cptr, unsigned int addr, DEVICE *dptr,
	UNIT *uptr);
int sim_activate (UNIT *uptr, int interval);
int sim_cancel (UNIT *uptr);
int step_svc (UNIT *ptr);

UNIT step_unit = { UDATA (&step_svc, 0, 0)  };
char *scp_error_messages[] = {
	"Address space exceeded",
	"Unit not attached",
	"I/O error",
	"Checksum error",
	"Format error",
	"Unit not attachable",
	"File open error",
	"Memory exhausted",
	"Invalid argument",
	"Step expired",
	"Unknown command",
	"Read only argument",
	"Command not completed",
	"Simulation stopped",
	"Goodbye",
	"Console input I/O error",
	"Console output I/O error",
	"End of file",
	"Relocation error",
	"No settable parameters",
	"Unit already attached"  };

size_t size_map[] = { sizeof (char),
	sizeof (char), sizeof (short), sizeof (int), sizeof (int),
	sizeof (long), sizeof (long), sizeof (long), sizeof (long)  };

int main (int argc, char *argv[])
{
char cbuf[CBUFSIZE], gbuf[CBUFSIZE], *cptr;
int i, stat;
int reset_cmd (int flag, char *ptr);
int exdep_cmd (int flag, char *ptr);
int load_cmd (int flag, char *ptr);
int run_cmd (int flag, char *ptr);
int attach_cmd (int flag, char *ptr);
int detach_cmd (int flag, char *ptr);
int save_cmd (int flag, char *ptr);
int restore_cmd (int flag, char *ptr);
int exit_cmd (int flag, char *ptr);
int set_cmd (int flag, char *ptr);
int show_cmd (int flag, char *ptr);
int help_cmd (int flag, char *ptr);

static CTAB cmd_table[] = {
	{ "RESET", &reset_cmd, 0 },
	{ "EXAMINE", &exdep_cmd, EX_E },
	{ "IEXAMINE", &exdep_cmd, EX_E+EX_I },
	{ "DEPOSIT", &exdep_cmd, EX_D },
	{ "IDEPOSIT", &exdep_cmd, EX_D+EX_I },
	{ "RUN", &run_cmd, RU_RUN },
	{ "GO", &run_cmd, RU_GO }, 
	{ "STEP", &run_cmd, RU_STEP },
	{ "CONT", &run_cmd, RU_CONT },
	{ "BOOT", &run_cmd, RU_BOOT },
	{ "ATTACH", &attach_cmd, 0 },
	{ "DETACH", &detach_cmd, 0 },
	{ "SAVE", &save_cmd, 0 },
	{ "RESTORE", &restore_cmd, 0 },
	{ "GET", &restore_cmd, 0 },
	{ "LOAD", &load_cmd, 0 },
	{ "EXIT", &exit_cmd, 0 },
	{ "QUIT", &exit_cmd, 0 },
	{ "SET", &set_cmd, 0 },
	{ "SHOW", &show_cmd, 0 },
	{ "HELP", &help_cmd, 0 },
	{ NULL, NULL, 0 }  };

/* Main command loop */

printf ("\n%s simulator V1.0\n", sim_name);
if (ttinit () != SCPE_OK) {
	printf ("Fatal terminal initialization error\n");
	exit (0);  }
stop_cpu = 0;
sim_interval = 0;
sim_time = 0;
noqueue_time = 0;
sim_clock_queue = NULL;
reset_all (0);

do {	printf ("sim> ");				/* prompt */
	cptr = read_line (cbuf, CBUFSIZE, stdin);	/* read command line */
	stat = SCPE_UNK;
	if (cptr == NULL) continue;			/* ignore EOF */
	if (*cptr == 0) continue;			/* ignore blank */
	cptr = get_glyph (cptr, gbuf);			/* get command glyph */
	for (i = 0; cmd_table[i].name != NULL; i++) {
		if (MATCH_CMD (gbuf, cmd_table[i].name) == 0) {
			stat = cmd_table[i].action (cmd_table[i].arg, cptr);
			break;  }  }
	if (stat >= SCPE_BASE)
		printf ("%s\n", scp_error_messages[stat - SCPE_BASE]);
} while (stat != SCPE_EXIT);

detach_all (0);
ttclose ();
exit (0);
}

/* Exit command */

int exit_cmd (int flag, char *cptr)
{
return SCPE_EXIT;
}

/* Help command */

int help_cmd (int flag, char *cptr)
{
printf ("r{eset} {ALL|<device>}   reset simulator\n");
printf ("e{xamine} <list>         examine memory or registers\n");
printf ("ie{xamine} <list>        interactive examine memory or registers\n");
printf ("d{eposit} <list> <val>   deposit in memory or registers\n");
printf ("id{eposit} <list>        interactive deposit in memory or registers\n");
printf ("l{oad} <file>            load binary file\n");
printf ("ru{n} {new PC}           reset and start simulation\n");
printf ("go {new PC}              start simulation\n");
printf ("c{ont}                   continue simulation\n");
printf ("s{tep} {n}               simulate n instructions\n");
printf ("b(oot) <device>|<unit>   bootstrap device\n");
printf ("at{tach} <unit> <file>   attach file to simulated unit\n");
printf ("det{ach} <unit>          detach file from simulated unit\n");
printf ("sa{ve} <file>            save simulator to file\n");
printf ("rest{ore}|ge{t} <file>   restore simulator from file\n");
printf ("exi{t}|q{uit}            exit from simulation\n");
printf ("set <unit> <val>         set unit parameter\n");
printf ("show <device>            show device parameters\n");
printf ("sh{ow} c{onfiguration}   show configuration\n");
printf ("sh{ow} q{ueue}           show event queue\n");
printf ("sh{ow} t{ime}            show simulated time\n");
printf ("h{elp}                   type this message\n");
return SCPE_OK;
}

/* Set command */

int set_cmd (int flag, char *cptr)
{
int unitno, r;
char gbuf[CBUFSIZE];
DEVICE *dptr;
UNIT *uptr;
MTAB *mptr;

cptr = get_glyph (cptr, gbuf);				/* get glyph */
dptr = find_device (gbuf, &unitno);			/* find device */
if ((dptr == NULL) || (*cptr == 0)) return SCPE_ARG;	/* argument? */
cptr = get_glyph (cptr, gbuf);				/* get glyph */
if (*cptr != 0) return SCPE_ARG;			/* now eol? */
uptr = dptr -> units + unitno;
if (dptr -> modifiers == NULL) return SCPE_NOPARAM;	/* any modifiers? */
for (mptr = dptr -> modifiers; mptr -> mask != 0; mptr++) {
	if ((mptr -> mstring != NULL) &&
	    (MATCH_CMD (gbuf, mptr -> mstring) == 0)) {
		if ((mptr -> valid != NULL) &&
		   ((r = mptr -> valid (uptr, mptr -> match)) != SCPE_OK))
			return r;			/* invalid? */
		uptr -> flags = (uptr -> flags & ~(mptr -> mask)) |
			mptr -> match;			/* set new value */
		return SCPE_OK;  }  }
return SCPE_ARG;					/* no match */
}

/* Show command */

int show_cmd (int flag, char *cptr)
{
int i;
char gbuf[CBUFSIZE];
DEVICE *dptr;
int show_config (int flag, char *ptr);
int show_queue (int flag, char *ptr);
int show_time (int flag, char *ptr);
int show_device (DEVICE *dptr, int flag);
static CTAB show_table[] = {
	{ "CONFIGURATION", &show_config, 0 },
	{ "QUEUE", &show_queue, 0 },
	{ "TIME", &show_time, 0 },
	{ NULL, NULL, 0 }  };

cptr = get_glyph (cptr, gbuf);				/* get glyph */
for (i = 0; show_table[i].name != NULL; i++) {		/* find command */
	if (MATCH_CMD (gbuf, show_table[i].name) == 0)
		return show_table[i].action (show_table[i].arg, cptr);  }
dptr = find_device (gbuf, NULL);			/* find device */
if (dptr == NULL) return SCPE_ARG;
return show_device (dptr, dptr != sim_devices[0]);
}

/* Show processors */

int show_device (DEVICE *dptr, int flag)
{
int j;
UNIT *uptr;
MTAB *mptr;

printf ("%s", dptr -> name);
if (dptr -> numunits > 1) printf (", %d units\n", dptr -> numunits);
for (j = 0; j < dptr -> numunits; j++) {
	uptr = (dptr -> units) + j;
	if (dptr -> numunits > 1) printf ("  unit %d", j);
	if (uptr -> flags & UNIT_FIX)
		printf (", %dK%s", uptr -> capac / (flag? 1000: 1024),
		((dptr -> dwidth / dptr -> aincr) > 8)? "W": "B");
	if (uptr -> flags & UNIT_ATT)
		printf (", attached to %s", uptr -> filename);
	else if (uptr -> flags & UNIT_ATTABLE) printf (", not attached");
	if (dptr -> modifiers != NULL) {
		for (mptr = dptr -> modifiers; mptr -> mask != 0; mptr++) {
			if ((mptr -> pstring != NULL) &&
			   ((uptr -> flags & mptr -> mask) == mptr -> match))
				printf (", %s", mptr -> pstring);  }  }
	printf ("\n");  }
return SCPE_OK;
}

int show_config (int flag, char *cptr)
{
int i;
DEVICE *dptr;

printf ("%s simulator configuration\n\n", sim_name);
for (i = 0; (dptr = sim_devices[i]) != NULL; i++) show_device (dptr, i);
return SCPE_OK;
}

int show_queue (int flag, char *cptr)
{
DEVICE *dptr;
UNIT *uptr;
int accum;

if (sim_clock_queue == NULL) {
	printf ("%s event queue empty, time = %-16.0f\n", sim_name, sim_time);
	return SCPE_OK;  }
printf ("%s event queue status, time = %-16.0f\n", sim_name, sim_time);
accum = 0;
for (uptr = sim_clock_queue; uptr != NULL; uptr = uptr -> next) {
	if (uptr == &step_unit) printf ("  Step timer");
	else if ((dptr = find_dev_from_unit (uptr)) != NULL) {
		printf ("  %s", dptr -> name);
		if (dptr -> numunits > 1) printf (" unit %d",
			(uptr - dptr -> units) / sizeof (UNIT));  }
	else printf ("  Unknown");
	printf (" at %d\n", accum + uptr -> time);
	accum = accum + uptr -> time;  }
return SCPE_OK;
}

int show_time (int flag, char *cptr)
{
printf ("TIME:	%-16.0f\n", sim_time);
return SCPE_OK;
}

/* Reset command and routines

   re[set]		reset all devices
   re[set] all		reset all devices
   re[set] device	reset specific device
*/

int reset_cmd (int flag, char *cptr)
{
char gbuf[CBUFSIZE];
DEVICE *dptr;

if (*cptr == 0) return (reset_all (0));			/* reset(cr) */
cptr = get_glyph (cptr, gbuf);				/* get next glyph */
if (*cptr != 0) return SCPE_ARG;			/* now (cr)? */
if (strcmp (gbuf, "ALL") == 0) return (reset_all (0));
if ((dptr = find_device (gbuf, NULL)) == NULL) return SCPE_ARG;
if (dptr -> reset != NULL) return dptr -> reset (dptr);
else return SCPE_OK;
}

/* Reset devices start..end

   Inputs:
	start	=	number of starting device
   Outputs:
	status	=	error status
*/

int reset_all (int start)
{
DEVICE *dptr;
int i;

if ((start < 0) || (start > 1)) return SCPE_ARG;
for (i = start; (dptr = sim_devices[i]) != NULL; i++) {
	if (dptr -> reset != NULL) dptr -> reset (dptr);  }
return SCPE_OK;
}

/* Load command

   lo[ad] filename		load specified file
*/

int load_cmd (int flag, char *cptr)
{
FILE *loadfile;
int reason;

if (*cptr == 0) return SCPE_ARG;
loadfile = fopen (cptr, "r");
if (loadfile == NULL) return SCPE_OPENERR;
reason = sim_load (loadfile);
fclose (loadfile);
return reason;
}

/* Attach command

   at[tach] unit file	attach specified unit to file
*/

int attach_cmd (int flag, char *cptr)
{
char gbuf[CBUFSIZE];
int unitno;
DEVICE *dptr;
UNIT *uptr;

if (*cptr == 0) return SCPE_ARG;
cptr = get_glyph (cptr, gbuf);
if (*cptr == 0) return SCPE_ARG;
if ((dptr = find_device (gbuf, &unitno)) == NULL) return SCPE_ARG;
uptr = (dptr -> units) + unitno;
if (dptr -> attach != NULL) return dptr -> attach (uptr, cptr);
return attach_unit (uptr, cptr);
}

int attach_unit (UNIT *uptr, char *cptr)
{
DEVICE *dptr;
int reason;

if ((uptr -> flags & UNIT_ATTABLE) == 0) return SCPE_NOATT;
if ((dptr = find_dev_from_unit (uptr)) == NULL) return SCPE_NOATT;
if (uptr -> flags & UNIT_ATT) {
	reason = detach_unit (uptr);
	if (reason != SCPE_OK) return reason;  }
uptr -> filename = calloc (CBUFSIZE, sizeof (char));
if (uptr -> filename == NULL) return SCPE_MEM;
strncpy (uptr -> filename, cptr, CBUFSIZE);
uptr -> fileref = fopen (cptr, "r+");
if (uptr -> fileref == NULL) {
	uptr -> fileref = fopen (cptr, "w+");
	if (uptr -> fileref == NULL) return SCPE_OPENERR;
	printf ("%s: creating new file\n", dptr -> name);  }
if (uptr -> flags & UNIT_BUFABLE) {
	if ((uptr -> filebuf = calloc (SZ_D (dptr), uptr -> capac)) != NULL) {
		printf ("%s: buffering file in memory\n", dptr -> name);
		uptr -> hwmark = fread (uptr -> filebuf, SZ_D (dptr),
					uptr -> capac, uptr -> fileref);
		uptr -> flags = uptr -> flags | UNIT_BUF;  }
	else if (uptr -> flags & UNIT_MUSTBUF) return SCPE_MEM;  }
uptr -> flags = uptr -> flags | UNIT_ATT;
uptr -> pos = 0;
return SCPE_OK;
}

/* Detach command

   det[ach] all		detach all units
   det[ach] unit	detach specified unit
*/

int detach_cmd (int flag, char *cptr)
{
char gbuf[CBUFSIZE];
int unitno;
DEVICE *dptr;
UNIT *uptr;

if (*cptr == 0) return SCPE_ARG;
cptr = get_glyph (cptr, gbuf);
if (*cptr != 0) return SCPE_ARG;
if (strcmp (gbuf, "ALL") == 0) return (detach_all (0));
if ((dptr = find_device (gbuf, &unitno)) == NULL) return SCPE_ARG;
uptr = (dptr -> units) + unitno;
if ((uptr -> flags & UNIT_ATTABLE) == 0) return SCPE_NOATT;
if (dptr -> detach != NULL) return dptr -> detach (uptr);
return detach_unit (uptr);
}

/* Detach devices start..end

   Inputs:
	start	=	number of starting device
   Outputs:
	status	=	error status
*/

int detach_all (int start)
{
int i, j, reason;
DEVICE *dptr;
UNIT *uptr;

if ((start < 0) || (start > 1)) return SCPE_ARG;
for (i = start; (dptr = sim_devices[i]) != NULL; i++) {
	for (j = 0; j < dptr -> numunits; j++) {
		uptr = (dptr -> units) + j;
		if (dptr -> detach != NULL) reason = dptr -> detach (uptr);
		else reason = detach_unit (uptr);
		if (reason != SCPE_OK) return reason;  }  }
return SCPE_OK;
}

int detach_unit (UNIT *uptr)
{
DEVICE *dptr;

if (uptr == NULL) return SCPE_ARG;
if ((uptr -> flags & UNIT_ATT) == 0) return SCPE_OK;
if ((dptr = find_dev_from_unit (uptr)) == NULL) return SCPE_OK;
uptr -> flags = uptr -> flags & ~UNIT_ATT;
if (uptr -> flags & UNIT_BUF) {
	printf ("%s: writing buffer to file\n", dptr -> name);
	uptr -> flags = uptr -> flags & ~UNIT_BUF;
	rewind (uptr -> fileref);
	fwrite (uptr -> filebuf, SZ_D (dptr), uptr -> hwmark, uptr -> fileref);
	if (ferror (uptr -> fileref)) perror ("I/O error");
	cfree (uptr -> filebuf);
	uptr -> filebuf = NULL;  }
cfree (uptr -> filename);
uptr -> filename = NULL;
return (fclose (uptr -> fileref) == EOF)? SCPE_IOERR: SCPE_OK;
}

/* Save command

   sa[ve] filename		save state to specified file
*/

int save_cmd (int flag, char *cptr)
{
FILE *sfile;
int i, j, k, t, reason, zerocnt, high;
unsigned int val;
DEVICE *dptr;
UNIT *uptr;
REG *rptr;

#define WRITE_S(xx) fputs ((xx), sfile)
#define WRITE_C(xx) fputc ((xx), sfile)
#define WRITE_I(xx) fwrite (&(xx), sizeof (int), 1, sfile)

if (*cptr == 0) return SCPE_ARG;
if ((sfile = fopen(cptr, "w")) == NULL) return SCPE_OPENERR;
WRITE_S (sim_name);					/* sim name */
WRITE_C ('\n');
fwrite (&sim_time, sizeof (double), 1, sfile);		/* sim time */

for (i = 0; (dptr = sim_devices[i]) != NULL; i++) {	/* loop thru devices */
	WRITE_S (dptr -> name);				/* device name */
	WRITE_C ('\n');
	for (rptr = dptr -> registers;			/* loop thru regs */
		(rptr != NULL) && (rptr -> name != NULL); rptr++) {
		WRITE_S (rptr -> name);			/* name */
		WRITE_C ('\n');
		val = get_rval (rptr);
		WRITE_I (val);  }			/* value */
	WRITE_C ('\n');					/* end registers */
	for (j = 0; j < dptr -> numunits; j++) {
		uptr = (dptr -> units) + j;
		WRITE_I (j);				/* unit number */
		t = sim_atime (uptr); WRITE_I (t);
		WRITE_I (uptr -> u3);			/* dev specific */
		WRITE_I (uptr -> u4);
		if (uptr -> flags & UNIT_ATT) WRITE_S (uptr -> filename);
		WRITE_C ('\n');
		if (((uptr -> flags & (UNIT_FIX + UNIT_ATTABLE)) == UNIT_FIX) &&
		     (dptr -> examine != NULL) &&
		    ((high = uptr -> capac) != 0)) {	/* memory-like unit */
			WRITE_I (high);			/* memory limit */
			zerocnt = 0;
			for (k = 0; k < high; k = k + (dptr -> aincr)) {
				reason = dptr -> examine (&val, k, uptr, 0);
				if (reason != SCPE_OK) return reason;
				if (val == 0) zerocnt = zerocnt - 1;
				else {	if (zerocnt) WRITE_I (zerocnt);
					zerocnt = 0;
					WRITE_I (val);  }  }
			if (zerocnt) WRITE_I (zerocnt);  }
		else {	k = 0;
			WRITE_I (k);  }  }
	t = -1; WRITE_I (t);  }				/* end units */
WRITE_C ('\n');						/* end devices */
reason = (ferror (sfile))? SCPE_IOERR: SCPE_OK;		/* error during save? */
fclose (sfile);
return reason;
}

/* Restore command

   re[store] filename		restore state from specified file
*/

int restore_cmd (int flag, char *cptr)
{
char buf[CBUFSIZE];
FILE *rfile;
int i, j, data, reason, unitno, time, high;
unsigned int val, mask;
DEVICE *dptr;
UNIT *uptr;
REG *rptr;

#define READ_S(xx) if (read_line ((xx), CBUFSIZE, rfile) == NULL) \
	{ fclose (rfile); return SCPE_IOERR;  }
#define READ_I(xx) if (fread (&xx, sizeof (int), 1, rfile) <= 0) \
	{ fclose (rfile); return SCPE_IOERR;  }

if (*cptr == 0) return SCPE_ARG;
if ((rfile = fopen(cptr, "r")) == NULL) return SCPE_OPENERR;
READ_S (buf);						/* sim name */
if (strcmp (buf, sim_name)) {
	printf ("Wrong system type: %s\n", buf);
	fclose (rfile);
	return SCPE_OK;  }
fread (&sim_time, sizeof (double), 1, rfile);		/* sim time */

for ( ;; ) {						/* device loop */
	READ_S (buf);					/* read device name */
	if (buf[0] == 0) break;				/* last? */
	if ((dptr = find_device (buf, NULL)) == NULL) {
		printf ("Invalid device name: %s\n", buf);
		fclose (rfile);
		return SCPE_INCOMP;  }
	for ( ;; ) {					/* register loop */
		READ_S (buf);				/* read reg name */
		if (buf[0] == 0) break;			/* last? */
		READ_I (val);				/* read value */
		if ((rptr = find_reg (buf, NULL, dptr)) == NULL) {
			printf ("Invalid register name: %s\n", buf);
			continue;  }
		mask = (1 << rptr -> width) - 1;
		if (val > mask) printf ("Invalid register value: %s\n", buf);
		else put_rval (rptr, val, mask);  }	/* end reg loop */
	for ( ;; ) {					/* unit loop */
		READ_I (unitno);			/* unit number */
		if (unitno < 0) break;
		if (unitno >= dptr -> numunits) {
			printf ("Invalid unit number %s%d\n", dptr -> name,
				unitno);
			fclose (rfile);
			return SCPE_INCOMP;  }
		READ_I (time);				/* event time */
		uptr = (dptr -> units) + unitno;
		sim_cancel (uptr);
		if (time > 0) sim_activate (uptr, time);
		READ_I (uptr -> u3);			/* device specific */
		READ_I (uptr -> u4);
		READ_S (buf);				/* attached file */
		if (buf[0] != 0) {
			reason = attach_unit (uptr, buf);
			if (reason != SCPE_OK) return reason;  }
		READ_I (high);				/* memory capacity */
		if ((high > 0) &&			/* validate if > 0 */
		   (((uptr -> flags & (UNIT_FIX + UNIT_ATTABLE)) != UNIT_FIX) ||
		     (high > uptr -> capac) || (dptr -> deposit == NULL))) {
			printf ("Invalid memory bound: %u\n", high);
			fclose (rfile);
			return SCPE_OK;  }
		for (i = 0; i < high; i = i + (dptr -> aincr)) {
			READ_I (data);
			if (data < 0) {
				for (j = data + 1; j < 0; j++) {
					reason = dptr -> deposit (0, i, uptr, 0);
					if (reason != SCPE_OK) return reason;
					i = i + (dptr -> aincr);  }
				data = 0;  }
			reason = dptr -> deposit (data, i, uptr, 0);
			if (reason != SCPE_OK) return reason;  }
		}					/* end unit loop */
	}						/* end device loop */
fclose (rfile);
return SCPE_OK;
}

/* Run, go, cont, step commands

   ru[n] [new PC]	reset and start simulation
   go [new PC]		start simulation
   co[nt]		start simulation
   s[tep] [step limit]	start simulation for 'limit' instructions
   b[oot] device	bootstrap from device and start simulation
*/

int run_cmd (int flag, char *cptr)
{
char gbuf[CBUFSIZE], *tptr;
int i, j, val, unitno, r, step;
DEVICE *dptr;
UNIT *uptr;
void int_handler (int signal);

step = 0;
if (((flag == RU_RUN) || (flag == RU_GO)) && (*cptr != 0)) {	/* run or go */
	cptr = get_glyph (cptr, gbuf);			/* get PC, store */
	if ((r = dep_reg (0, 0, gbuf, sim_PC)) != SCPE_OK) return r;  }

if (flag == RU_STEP) {					/* step */
	if (*cptr == 0) step = 1;
	else {	cptr = get_glyph (cptr, gbuf);
		errno = 0;
		step = strtoul (gbuf, &tptr, 10);	/* rtn 0 on error */
		if (errno || (step == 0)) return SCPE_ARG;  }  }

if (flag == RU_BOOT) {					/* boot */
	if (*cptr == 0) return SCPE_ARG;
	cptr = get_glyph (cptr, gbuf);
	if ((dptr = find_device (gbuf, &unitno)) == NULL) return SCPE_ARG;
	if (dptr -> boot == NULL) return SCPE_ARG;
	uptr = dptr -> units + unitno;
	if ((uptr -> flags & UNIT_ATTABLE) == 0) return SCPE_NOATT;
	if ((uptr -> flags & UNIT_ATT) == 0) return SCPE_UNATT;
	if ((r = dptr -> boot (unitno)) != SCPE_OK) return r;  }

if (*cptr != 0) return SCPE_ARG;

if ((flag == RU_RUN) || (flag == RU_BOOT)) {		/* run or boot */
	sim_interval = 0;				/* reset queue */
	sim_time = 0;
	noqueue_time = 0;
	sim_clock_queue = NULL;
	reset_all (0);  }				/* reset devices */
for (i = 1; (dptr = sim_devices[i]) != NULL; i++) {
	for (j = 0; j < dptr -> numunits; j++) {
		uptr = (dptr -> units) + j;
		if ((uptr -> flags & (UNIT_ATT + UNIT_SEQ)) ==
		    (UNIT_ATT + UNIT_SEQ))
			fseek (uptr -> fileref, uptr -> pos, SEEK_SET);  }  }
stop_cpu = 0;
if ((int) signal (SIGINT, int_handler) == -1) {		/* set WRU */
	printf ("Simulator interrupt handler setup failed\n");
	return SCPE_OK;  }
if (ttrunstate ()) {					/* set console */
	ttcmdstate ();
	printf ("Simulator terminal setup failed\n");
	return SCPE_OK;  }
if (step) sim_activate (&step_unit, step);		/* set step timer */
r = sim_instr();

ttcmdstate ();						/* restore console */
signal (SIGINT, SIG_DFL);				/* cancel WRU */
sim_cancel (&step_unit);				/* cancel step timer */
if (sim_clock_queue != NULL) {				/* update sim time */
	UPDATE_SIM_TIME (sim_clock_queue -> time);  }
else {	UPDATE_SIM_TIME (noqueue_time);  }
#ifdef VMS
printf ("\n");
#endif
if (r >= SCPE_BASE) printf ("\n%s, PC: ", scp_error_messages[r - SCPE_BASE]);
else printf ("\n%s, PC: ", sim_stop_messages[r]);
print_val (i = get_rval (sim_PC), sim_PC -> radix,
	sim_PC -> width, sim_PC -> flags & REG_FMT);
if (((dptr = sim_devices[0]) != NULL) &&
     (dptr -> examine != NULL) &&
     (dptr -> examine (&val, i, dptr -> units, SWMASK ('V')) == SCPE_OK)) {
	printf (" (");
	print_val (val, dptr -> dradix, dptr -> dwidth, PV_RZRO);
	printf (")");  }
printf ("\n");
return SCPE_OK;
}

/* Run time routines */

/* Unit service for step timeout, originally scheduled by STEP n command

   Return step timeout SCP code, will cause simulation to stop
*/

int step_svc (UNIT *uptr)
{
return SCPE_STEP;
}

/* Signal handler for ^C signal

   Set stop simulation flag
*/

void int_handler (int sig)
{
stop_cpu = 1;
return;
}

/* Examine/deposit commands

   ex[amine] [unit] list		examine
   de[posit] [unit] list val		deposit
   ie[xamine] [unit] list		interactive examine
   id[eposit] [unit] list		interactive deposit

   list					list of addresses and registers
	addr[:addr|-addr]		address range
	ALL				all addresses
	register[:register|-register]	register range
	STATE				all registers
*/

int exdep_cmd (int flag, char *cptr)
{
char gbuf[CBUFSIZE], *gptr, *tptr;
int sw, reason, unitno;
unsigned int low, high;
DEVICE *dptr;
UNIT *uptr;
REG *lowr, *highr;
int exdep_addr_loop (int flag, int sw, char *ptr, unsigned int low,
	unsigned int high, DEVICE *dptr, UNIT *uptr);
int exdep_reg_loop (int flag, int sw, char *ptr, REG *lptr, REG *hptr);

if (*cptr == 0) return SCPE_ARG;			/* err if no args */
cptr = get_glyph (cptr, gbuf);
if ((sw = get_switches (gbuf)) != 0) {			/* try for switches */
	if ((sw < 0) || (*cptr == 0)) return SCPE_ARG;	/* err if no args */
	cptr = get_glyph (cptr, gbuf);  }		/* if found, advance */
if ((dptr = find_device (gbuf, &unitno)) != NULL) {	/* try for unit */
	if (*cptr == 0) return SCPE_ARG;		/* err if no args */
	cptr = get_glyph (cptr, gbuf);  }		/* if found, advance */
else {	dptr = sim_devices[0];				/* CPU is default */
	unitno = 0;  }
if ((*cptr == 0) == (flag == 0)) return SCPE_ARG;	/* eol if needed? */

gptr = gbuf;
uptr = (dptr -> units) + unitno;
while (*gptr != 0) {
	errno = 0;
	low = strtoul (gptr, &tptr, dptr -> aradix);
	if ((errno == 0) && (gptr != tptr)) {
		high = low;
		if ((*tptr == '-') || (*tptr == ':')) {
			gptr = tptr + 1;
			errno = 0;
			high = strtoul (gptr, &tptr, dptr -> aradix);
			if (errno || (gptr == tptr)) return SCPE_ARG;  }
		if (*tptr == ',') tptr++;
		else if (*tptr != 0) return SCPE_ARG;
		reason = exdep_addr_loop (flag, sw, cptr, low, high,
			dptr, uptr);
		if (reason != SCPE_OK) return reason; }

	else if (strncmp (gptr, "ALL", strlen ("ALL")) == 0) {
		tptr = gptr + strlen ("ALL");
		if (*tptr == ',') tptr++;
		else if (*tptr != 0) return SCPE_ARG;
		if ((uptr -> capac == 0) | (flag == EX_E)) return SCPE_ARG;
		high = (uptr -> capac) - (dptr -> aincr);
		reason = exdep_addr_loop (flag, sw, cptr, 0, high, dptr, uptr);
		if (reason != SCPE_OK) return reason; }

	else if (strncmp (gptr, "STATE", strlen ("STATE")) == 0) {
		tptr = gptr + strlen ("STATE");
		if (*tptr == ',') tptr++;
		else if (*tptr != 0) return SCPE_ARG;
		if ((lowr = dptr -> registers) == NULL) return SCPE_ARG;
		for (highr = lowr; highr -> name != NULL; highr++) ;
		reason = exdep_reg_loop (flag, sw+SWHIDE, cptr, lowr, --highr);
		if (reason != SCPE_OK) return reason; }

	else {	lowr = find_reg (gptr, &tptr, dptr);
		if (lowr == NULL) return SCPE_ARG;
		highr = lowr;
		if ((*tptr == '-') || (*tptr == ':')) {
			highr = find_reg (tptr + 1, &tptr, dptr);
			if (highr == NULL) return SCPE_ARG;  }
		if (*tptr == ',') tptr++;
		else if (*tptr != 0) return SCPE_ARG;
		reason = exdep_reg_loop (flag, sw, cptr, lowr, highr);
		if (reason != SCPE_OK) return reason; }

	gptr = tptr;  }					/* end while */
return SCPE_OK;
}

/* Loop controllers for examine/deposit

   exdep_reg_loop	examine/deposit range of registers
   exdep_addr_loop	examine/deposit range of addresses
*/

int exdep_reg_loop (int flag, int sw, char *cptr, REG *lowr, REG *highr)
{
int reason;
REG *rptr;

if ((lowr == NULL) || (highr == NULL)) return SCPE_ARG;
if (lowr > highr) return SCPE_ARG;
for (rptr = lowr; rptr <= highr; rptr++) {
	if ((sw & SWHIDE) && (rptr -> flags & REG_HIDDEN)) continue;
	if (flag != EX_D) {
		reason = ex_reg (flag, sw, rptr);
		if (reason != SCPE_OK) return reason; }
	if (flag != EX_E) {
		reason = dep_reg (flag, sw, cptr, rptr);
		if (reason != SCPE_OK) return reason;  }  }
return SCPE_OK;
}

int exdep_addr_loop (int flag, int sw, char *cptr, unsigned int low,
	unsigned int high, DEVICE *dptr, UNIT *uptr)
{
int reason;
unsigned int i, mask;

mask = (1 << dptr -> awidth) - 1;
if ((low > mask) || (high > mask)) return SCPE_ARG;
if (low > high) return SCPE_ARG;
for (i = low; i <= high; i = i + (dptr -> aincr)) {
	if (flag != EX_D) {
		reason = ex_addr (flag, sw, i, dptr, uptr);
		if (reason != SCPE_OK) return reason; }
	if (flag != EX_E) {
		reason = dep_addr (flag, sw, cptr, i, dptr, uptr);
		if (reason != SCPE_OK) return reason;  }  }
return SCPE_OK;
}

/* Examine register routine

   Inputs:
	flag	=	type of ex/mod command (ex, iex, idep)
	sw	=	switches
	rptr	=	pointer to register descriptor
   Outputs:
	return	=	error status
*/

int ex_reg (int flag, int sw, REG *rptr)
{
unsigned int val;

if (rptr == NULL) return SCPE_ARG;
printf ("%s:	", rptr -> name);
if ((flag & EX_E) == 0) return SCPE_OK;
val = get_rval (rptr);
print_val (val, rptr -> radix, rptr -> width, rptr -> flags & REG_FMT);
if (flag & EX_I) printf ("	");
else printf ("\n");
return SCPE_OK;
}

/* Get register value

   Inputs:
	rptr	=	pointer to register descriptor
   Outputs:
	return	=	register value
*/

unsigned int get_rval (REG *rptr)
{
unsigned int val;
size_t sz;

sz = SZ_R (rptr);
if (sz == sizeof (char)) val = *((unsigned char *) rptr -> loc);
else if (sz == sizeof (short)) val = *((unsigned short *) rptr -> loc);
else if (sz == sizeof (int)) val = *((unsigned int *) rptr -> loc);
else if (sz == sizeof (long)) val = *((unsigned long *) rptr -> loc);
val = (val >> rptr -> offset) & ((1 << rptr -> width) - 1);
return val;
}

/* Deposit register routine

   Inputs:
	flag	=	type of deposit (normal/interactive)
	sw	=	switches
	cptr	=	pointer to input string
	rptr	=	pointer to register descriptor
   Outputs:
	return	=	error status
*/

int dep_reg (int flag, int sw, char *cptr, REG *rptr)
{
char *tptr, gbuf[CBUFSIZE];
unsigned int val, mask;

if ((cptr == NULL) || (rptr == NULL)) return SCPE_ARG;
if (rptr -> flags & REG_RO) return SCPE_RO;
if (flag & EX_I) {
	cptr = read_line (gbuf, CBUFSIZE, stdin);
	if (cptr == NULL) return 1;			/* force exit */
	if (*cptr == 0) return SCPE_OK;	 }		/* success */
errno = 0;
mask = (1 << rptr -> width) - 1;
val = strtoul (cptr, &tptr, rptr -> radix);
if (errno || (cptr == tptr) || (val > mask)) return SCPE_ARG;
put_rval (rptr, val, mask);
return SCPE_OK;
}

/* Put register value

   Inputs:
	rptr	=	pointer to register descriptor
	val	=	new value
	mask	=	mask
   Outputs:
	none
*/

void put_rval (REG *rptr, unsigned int val, unsigned int mask)
{
size_t sz;
#define PUT_RVAL(sz,rp,val,msk) \
	*((unsigned sz *) rp -> loc) = (*((unsigned sz *) rp -> loc) & \
        ~((msk) << (rp) -> offset)) | ((val) << (rp) -> offset)

sz = SZ_R (rptr);
if (sz == sizeof (char)) PUT_RVAL (char, rptr, val, mask);
if (sz == sizeof (short)) PUT_RVAL (short, rptr, val, mask);
if (sz == sizeof (int)) PUT_RVAL (int, rptr, val, mask);
if (sz == sizeof (long)) PUT_RVAL (long, rptr, val, mask);
return;
}

/* Examine address routine

   Inputs:
	flag	=	type of ex/mod command (ex, iex, idep)
	sw	=	switches
	addr	=	address to examine
	dptr	=	pointer to device
	uptr	=	pointer to unit
   Outputs:
	return	=	error status
*/

int ex_addr (int flag, int sw, unsigned int addr, DEVICE *dptr, UNIT *uptr)
{
unsigned int val, mask, loc;
int reason;
size_t sz;

if (dptr == NULL) return SCPE_ARG;
val = 0;
print_val (addr, dptr -> aradix, dptr -> awidth, PV_LEFT);
printf (":	");
if ((flag & EX_E) == 0) return SCPE_OK;
if (dptr -> examine != NULL) {
	reason = dptr -> examine (&val, addr, uptr, sw);
	if (reason != SCPE_OK) return reason;  }
else {	if ((uptr -> flags & UNIT_ATT) == 0) return SCPE_UNATT;
	if ((uptr -> flags & UNIT_FIX) && (addr >= uptr -> capac))
		return SCPE_NXM;
	sz = SZ_D (dptr);
	loc = addr / dptr -> aincr;
	if (uptr -> flags & UNIT_BUF) {
		if (sz == sizeof (char))
			val = *(((unsigned char *) uptr -> filebuf) + loc);
		else if (sz == sizeof (short))
			val = *(((unsigned short *) uptr -> filebuf) + loc);
		else if (sz == sizeof (int))
			val = *(((unsigned int *) uptr -> filebuf) + loc);
		else if (sz == sizeof (long))
			val = *(((unsigned long *) uptr -> filebuf) + loc);  }
	else {	fseek (uptr -> fileref, sz * loc, SEEK_SET);
		fread (&val, sz, 1, uptr -> fileref);
		if (feof (uptr -> fileref)) {
			if (uptr -> flags & UNIT_FIX) val = 0;
			else return SCPE_EOF;  }
	 	else if (ferror (uptr -> fileref)) {
			clearerr (uptr -> fileref);
			return SCPE_IOERR;  }  }  }
mask = (1 << dptr -> dwidth) - 1;
print_val (val & mask, dptr -> dradix, dptr -> dwidth, PV_RZRO);
if (flag & EX_I) printf ("	");
else printf ("\n");
return SCPE_OK;
}

/* Deposit address routine

   Inputs:
	flag	=	type of deposit (normal/interactive)
	sw	=	switches
	cptr	=	pointer to input string
	addr	=	address to examine
	dptr	=	pointer to device
	uptr	=	pointer to unit
   Outputs:
	return	=	error status
*/

int dep_addr (int flag, int sw, char *cptr, unsigned int addr,
	DEVICE *dptr, UNIT *uptr)
{
unsigned int val, mask, loc;
size_t sz;
char *tptr, gbuf[CBUFSIZE];

if (dptr == NULL) return SCPE_ARG;
if (flag & EX_I) {
	cptr = read_line (gbuf, CBUFSIZE, stdin);
	if (cptr == NULL) return 1;			/* force exit */
	if (*cptr == 0) return SCPE_OK;	 }		/* success */
errno = 0;
val = strtoul (cptr, &tptr, dptr -> dradix);
mask = (1 << dptr -> dwidth) - 1;
if (errno || (cptr == tptr) || (val > mask)) return SCPE_ARG;
if (dptr -> deposit != NULL) return dptr -> deposit (val, addr, uptr, sw);
if ((uptr -> flags & UNIT_ATT) == 0) return SCPE_UNATT;
if ((uptr -> flags & UNIT_FIX) && (addr >= uptr -> capac)) return SCPE_NXM;
sz = SZ_D (dptr);
loc = addr / dptr -> aincr;
if (uptr -> flags & UNIT_BUF) {
	if (sz == sizeof (char))
		*(((unsigned char *) uptr -> filebuf) + loc) = val;
	else if (sz == sizeof (short))
		*(((unsigned short *) uptr -> filebuf) + loc) = val;
	else if (sz == sizeof (int))
		*(((unsigned int *) uptr -> filebuf) + loc) = val;
	else if (sz == sizeof (long))
		*(((unsigned long *) uptr -> filebuf) + loc) = val;
	if (loc >= uptr -> hwmark) uptr -> hwmark = loc + 1;  }
else {	fseek (uptr -> fileref, sz * loc, SEEK_SET);
	fwrite (&val, sz, 1, uptr -> fileref);
	if (ferror (uptr -> fileref)) {
		clearerr (uptr -> fileref);
		return SCPE_IOERR;  }  }
return SCPE_OK;
}

/* String processing routines

   read_line		read line

   Inputs:
	cptr	=	pointer to buffer
	size	=	maximum size
	stream	=	pointer to input stream
   Outputs:
	optr	=	pointer to first non-blank character
			NULL if EOF
*/

char *read_line (char *cptr, int size, FILE *stream)
{
char *tptr;

cptr = fgets (cptr, size, stream);			/* get cmd line */
if (cptr == NULL) return NULL;				/* ignore EOF */
for (tptr = cptr; tptr < (cptr + size); tptr++)		/* remove cr */
	if (*tptr == '\n') *tptr = 0; 
while (isspace (*cptr)) cptr++;				/* absorb spaces */
return cptr;
}

/* get_glyph		get next glyph

   Inputs:
	iptr	=	pointer to input string
	optr	=	pointer to output string
   Outputs
	result	=	pointer to next character in input string
*/

char *get_glyph (char *iptr, char *optr)
{
while ((isspace (*iptr) == 0) && (*iptr != 0)) {
	if (islower (*iptr)) *optr = toupper (*iptr);
	else *optr = *iptr;
	iptr++; optr++;  }
*optr = 0;
while (isspace (*iptr)) iptr++;				/* absorb spaces */
return iptr;
}

/* Find_device		find device matching input string

   Inputs:
	cptr	=	pointer to input string
	iptr	=	pointer to unit number (can be null)
   Outputs:
	result	=	pointer to device
	*iptr	=	unit number, if valid
*/

DEVICE *find_device (char *cptr, int *iptr)
{
int i, unitno, lenn;
char *tptr;
DEVICE *dptr;

for (i = 0; (dptr = sim_devices[i]) != NULL; i++) {
	lenn = strlen (dptr -> name);
	if (strncmp (cptr, dptr -> name, lenn) != 0) continue;
	cptr = cptr + lenn;
	if (*cptr == 0) unitno = 0;
	else {	errno = 0;
		unitno = strtoul (cptr, &tptr, 10);
		if (errno || (cptr == tptr) || (*tptr != 0)) return NULL;  }
	if (unitno >= dptr -> numunits) return NULL;
	if (iptr != NULL) *iptr = unitno;
	return sim_devices[i];  }	
return NULL;
}

/* Find_dev_from_unit	find device for unit

   Inputs:
	uptr	=	pointer to unit
   Outputs:
	result	=	pointer to device
*/

DEVICE *find_dev_from_unit (UNIT *uptr)
{
DEVICE *dptr;
int i,j;

for (i = 0; (dptr = sim_devices[i]) != NULL; i++) {
	for (j = 0; j < dptr -> numunits; j++) {
		if (uptr == (dptr -> units + j)) return dptr;  }  }
return NULL;
}

/* find_reg		find register matching input string

   Inputs:
	cptr	=	pointer to input string
	optr	=	pointer to output pointer (can be null)
	dptr	=	pointer to device
   Outputs:
	result	=	pointer to register, NULL if error
	*optr	=	pointer to next character in input string
*/

REG *find_reg (char *cptr, char **optr, DEVICE *dptr)
{
char *tptr;
REG *rptr;

if ((cptr == NULL) || (dptr == NULL)) return NULL;
if (dptr -> registers == NULL) return NULL;
tptr = cptr;
do { tptr++; } while (isalnum (*tptr) || (*tptr == '_'));
for (rptr = dptr -> registers; rptr -> name != NULL; rptr++) {
	if (strncmp (cptr, rptr -> name, tptr - cptr) == 0) {
		if (optr != NULL) *optr = tptr;
		return rptr;  }  }
return NULL;
}

/* get_switches		get switches from input string

   Inputs:
	cptr	=	pointer to input string
   Outputs:
	sw	=	switch bit mask
			0 if no switches, -1 if error
*/

int get_switches (char *cptr)
{
int sw;

if (*cptr != '-') return 0;
sw = 0;
for (cptr++; (isspace (*cptr) == 0) && (*cptr != 0); cptr++) {
	if (isalpha (*cptr) == 0) return -1;
	sw = sw | SWMASK (*cptr);  }
return sw;
}

/* General radix printing routine

   Inputs:
	stream	=	stream designator
	val	=	value to print
	radix	=	radix to print
	width	=	width to print
	format	=	leading zeroes format
   Outputs:
	status	=	error status
*/

int fprint_val (FILE *stream, unsigned int val, int radix,
	int width, int format)
{
#define MAX_WIDTH (CHAR_BIT * sizeof (unsigned int))
unsigned int mask, digit;
int d, ndigits;
double fptest;
char dbuf[MAX_WIDTH + 1];

for (d = 0; d < MAX_WIDTH; d++) dbuf[d] = (format == PV_RZRO)? '0': ' ';
dbuf[MAX_WIDTH] = 0;
d = MAX_WIDTH;
do {	d = d - 1;
	digit = val % (unsigned) radix;
	val = val / (unsigned) radix;
	dbuf[d] = (digit <= 9)? '0' + digit: 'A' + (digit - 10);
   } while ((d > 0) && (val != 0));

if (format != PV_LEFT) {
	mask = (1 << width) - 1;
	fptest = (double) radix;
	ndigits = 1;
	while (fptest < (double) mask) {
		fptest = fptest * (double) radix;
		ndigits = ndigits + 1; }
	if (MAX_WIDTH - ndigits < d) d = MAX_WIDTH - ndigits;  }
if (fputs (&dbuf[d], stream) == EOF) return SCPE_IOERR;
return SCPE_OK;
}

/* Event queue routines

	sim_activate		add entry to event queue
	sim_cancel		remove entry from event queue
	sim_process_event	process entries on event queue
	sim_is_active		see if entry is on event queue
	sim_atime		return absolute time for an entry
	sim_gtime		return global time

   Asynchronous events are set up by queueing a unit data structure
   to the event queue with a timeout (in simulator units, relative
   to the current time).  Each simulator 'times' these events by
   counting down interval counter sim_interval.  When this reaches
   zero the simulator calls sim_process_event to process the event
   and to see if further events need to be processed, or sim_interval
   reset to count the next one.

   The event queue is maintained in clock order; entry timeouts are
   RELATIVE to the time in the previous entry.

   Sim_process_event - process event

   Inputs:
	none
   Outputs:
	reason		reason code returned by any event processor,
			or 0 (SCPE_OK) if no exceptions
*/

int sim_process_event (void)
{
UNIT *uptr;
int reason;

if (stop_cpu) {						/* stop CPU? */
	stop_cpu = 0;
	return SCPE_STOP;  }
if (sim_clock_queue == NULL) {				/* queue empty? */
	UPDATE_SIM_TIME (noqueue_time);			/* update sim time */
	sim_interval = noqueue_time = NOQUEUE_WAIT;	/* flag queue empty */
	return SCPE_OK;  }
UPDATE_SIM_TIME (sim_clock_queue -> time);		/* update sim time */
do {	uptr = sim_clock_queue;				/* get first */
	sim_clock_queue = uptr -> next;			/* remove first */
	uptr -> next = NULL;				/* hygiene */
	uptr -> time = 0;
	if (sim_clock_queue != NULL) sim_interval = sim_clock_queue -> time;
	else sim_interval = noqueue_time = NOQUEUE_WAIT;
	reason = (uptr -> action == NULL)? SCPE_OK: uptr -> action (uptr);
   } while ((reason == SCPE_OK) && (sim_interval == 0));

/* Empty queue forces sim_interval != 0 */

return reason;
}							/* end sim_process */

/* Activate (queue) event

   Inputs:
	uptr	=	pointer to unit
	event_time =	relative timeout
   Outputs:
	reason	=	result (SCPE_OK if ok)
*/

int sim_activate (UNIT *uptr, int event_time)
{
UNIT *cptr, *prvptr;
int accum;

if (event_time < 0) return SCPE_ARG;
if (sim_is_active (uptr)) return SCPE_OK;		/* already active? */
if (sim_clock_queue == NULL) { UPDATE_SIM_TIME (noqueue_time);  }
else  {	UPDATE_SIM_TIME (sim_clock_queue -> time);  }	/* update sim time */

prvptr = NULL;
accum = 0;
for (cptr = sim_clock_queue; cptr != NULL; cptr = cptr -> next) {
	if (event_time < accum + cptr -> time) break;
	accum = accum + cptr -> time;
	prvptr = cptr;  }
if (prvptr == NULL) {					/* insert at head */
	cptr = uptr -> next = sim_clock_queue;
	sim_clock_queue = uptr;  }
else {	cptr = uptr -> next = prvptr -> next;		/* insert at prvptr */
	prvptr -> next = uptr;  }
uptr -> time = event_time - accum;
if (cptr != NULL) cptr -> time = cptr -> time - uptr -> time;
sim_interval = sim_clock_queue -> time;
return SCPE_OK;
}

/* Cancel (dequeue) event

   Inputs:
	uptr	=	pointer to unit
   Outputs:
	reason	=	result (SCPE_OK if ok)

*/

int sim_cancel (UNIT *uptr)
{
UNIT *cptr, *nptr;

if (sim_clock_queue == NULL) return SCPE_OK;
UPDATE_SIM_TIME (sim_clock_queue -> time);		/* update sim time */
nptr = NULL;
if (sim_clock_queue == uptr) nptr = sim_clock_queue = uptr -> next;
else {	for (cptr = sim_clock_queue; cptr != NULL; cptr = cptr -> next) {
		if (cptr -> next == uptr) {
			nptr = cptr -> next = uptr -> next;
			break;  }  }  }			/* end queue scan */
if (nptr != NULL) nptr -> time = nptr -> time + uptr -> time;
uptr -> next = NULL;					/* hygiene */
uptr -> time = 0;
if (sim_clock_queue != NULL) sim_interval = sim_clock_queue -> time;
else sim_interval = noqueue_time = NOQUEUE_WAIT;
return SCPE_OK;
}

/* Test for entry in queue

   Inputs:
	uptr	=	pointer to unit
   Outputs:
	result	=	TRUE if active, FALSE if inactive
*/

int sim_is_active (UNIT *uptr)
{
UNIT *cptr;

for (cptr = sim_clock_queue; cptr != NULL; cptr = cptr -> next) {
	if (cptr == uptr) return TRUE;  }  		/* already active? */
return FALSE;
}

/* Return entry absolute time

   Inputs:
	uptr	=	pointer to unit
   Outputs:
	atime	=	absolute activation time, 0 if inactive
*/

int sim_atime (UNIT *uptr)
{
UNIT *cptr;
int accum;

accum = 0;
for (cptr = sim_clock_queue; cptr != NULL; cptr = cptr -> next) {
	accum = accum + cptr -> time;
	if (cptr == uptr) return accum;  }
return 0;
}

/* Return global time

   Inputs: none
   Outputs:
	time	=	global time
*/

double sim_gtime(void)
{
if (sim_clock_queue == NULL) { UPDATE_SIM_TIME (noqueue_time);  }
else  {	UPDATE_SIM_TIME (sim_clock_queue -> time);  }
return sim_time;
}
