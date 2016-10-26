/* pdp8_cpu.c: PDP-8 CPU simulator

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   The register state for the PDP-8 is:

   AC<0:11>		accumulator
   MQ<0:11>		multiplier-quotient
   L			link flag
   PC<0:11>		program counter
   IF<0:2>		instruction field
   IB<0:2>		instruction buffer
   DF<0:2>		data field
   UF			user flag
   UB			user buffer
   SF<0:6>		interrupt save field

   The PDP-8 has three instruction formats: memory reference, I/O transfer,
   and operate.  The memory reference format is:

     0  1  2  3  4  5  6  7  8  9 10 11
   +--+--+--+--+--+--+--+--+--+--+--+--+
   |   op   |in|zr|    page offset     |	memory reference
   +--+--+--+--+--+--+--+--+--+--+--+--+

   <0:2>	mnemonic	action

    000		AND		AC = AC & M[MA]
    001		TAD		L'AC = AC + M[MA]
    010		DCA		M[MA] = AC, AC = 0
    011		ISZ		M[MA] = M[MA] + 1, skip if M[MA] == 0
    100		JMS		M[MA] = PC, PC = MA + 1
    101		JMP		PC = MA

   <3:4>	mode		action
    00 	page zero		MA = IF'0'IR<5:11>
    01	current page		MA = IF'PC<0:4>'IR<5:11>
    10	indirect page zero	MA = xF'M[IF'0'IR<5:11>]
    11	indirect current page	MA = xF'M[IF'PC<0:4>'IR<5:11>]

   where x is D for AND, TAD, ISZ, DCA, and I for JMS, JMP.

   Memory reference instructions can access an address space of 32K words.
   The address space is divided into eight 4K word fields; each field is
   divided into thirty-two 128 word pages.  An instruction can directly
   address, via its 7b offset, locations 0-127 on page zero or on the current
   page.  All 32k words can be accessed via indirect addressing and the
   instruction and data field registers.  If an indirect address is in
   locations 0010-0017 of any field, the indirect address is incremented
   and rewritten to memory before use.
*/

/* The I/O transfer format is as follows:

     0  1  2  3  4  5  6  7  8  9 10 11
   +--+--+--+--+--+--+--+--+--+--+--+--+
   |   op   |      device     | pulse  |	I/O transfer
   +--+--+--+--+--+--+--+--+--+--+--+--+

   The IO transfer instruction sends the the specified pulse to the
   specified I/O device.  The I/O device may take data from the AC,
   return data to the AC, initiate or cancel operations, or skip on
   status.

   The operate format is as follows:

   +--+--+--+--+--+--+--+--+--+--+--+--+
   | 1| 1| 1| 0|  |  |  |  |  |  |  |  |	operate group 1
   +--+--+--+--+--+--+--+--+--+--+--+--+
	        |  |  |  |  |  |  |  |
	        |  |  |  |  |  |  |  +--- increment AC	3
	        |  |  |  |  |  |  +--- rotate 1 or 2	4
	        |  |  |  |  |  +--- rotate left		4
	        |  |  |  |  +--- rotate right		4
	        |  |  |  +--- complement L		2
	        |  |  +--- complement AC		2
	        |  +--- clear L				1
	        +-- clear AC				1

   +--+--+--+--+--+--+--+--+--+--+--+--+
   | 1| 1| 1| 1|  |  |  |  |  |  |  | 0|	operate group 2
   +--+--+--+--+--+--+--+--+--+--+--+--+
	        |  |  |  |  |  |  |
	        |  |  |  |  |  |  +--- halt		3
	        |  |  |  |  |  +--- or switch register	3
	        |  |  |  |  +--- reverse skip sense	1
	        |  |  |  +--- skip on L != 0		1
	        |  |  +--- skip on AC == 0		1
	        |  +--- skip on AC < 0			1
	        +-- clear AC				2

   +--+--+--+--+--+--+--+--+--+--+--+--+
   | 1| 1| 1| 1|  |  |  |  |  |  |  | 1|	operate group 3
   +--+--+--+--+--+--+--+--+--+--+--+--+
		|  |  |  | \______/
		|  |  |  |     |
		|  |  +--|-----+--- EAE command		3
		|  |     +--- AC -> MQ, 0 -> AC		2
		|  +--- MQ v AC --> AC			2
		+-- clear AC				1

  The operate instruction can be microprogrammed to perform operations
  on the AC, MQ, and link.
*/

/* This routine is the instruction decode routine for the PDP-8.
   It is called from the simulator control program to execute
   instructions in simulated memory, starting at the simulated PC.
   It runs until 'reason' is set non-zero.

   General notes:

   1. Reasons to stop.  The simulator can be stopped by:

	HALT instruction
	breakpoint encountered
	unimplemented instruction and stop_inst flag set
	I/O error in I/O simulator

   2. Interrupts.  Interrupts are maintained by three parallel variables:

	dev_done 	device done flags
	dev_enable	device interrupt enable flags
	int_req		interrupt requests

      In addition, int_req contains the interrupt enable flag, the
      CIF not pending flag, and the ION not pending flag.  If all
      three of these flags are set, and at least one interrupt request
      is set, then an interrupt occurs.

   3. Non-existent memory.  On the PDP-8, reads to non-existent memory
      return zero, and writes are ignored.  In the simulator, the
      largest possible memory is instantiated and initialized to zero.
      Thus, only writes outside the current field (indirect writes) need
      be checked against actual memory size.

   3. Adding I/O devices.  Three modules must be modified:

	pdp8_defs.h	add interrupt request definition
	pdp8_cpu.c	add IOT dispatch
	pdp8_sys.c	add pointer to data structures to sim_devices
*/

#include "pdp8_defs.h"

#define ILL_ADR_FLAG 0100000				/* invalid addr flag */
#define save_ibkpt (cpu_unit.u3)			/* saved bkpt addr */
#define UNIT_V_NOEAE	(UNIT_V_UF)			/* EAE absent */
#define UNIT_NOEAE	(1 << UNIT_V_NOEAE)

unsigned short M[MAXMEMSIZE] = { 0 };			/* main memory */
int saved_LAC = 0;					/* saved L'AC */
int saved_MQ = 0;					/* saved MQ */
int saved_PC = 0;					/* saved IF'PC */
int saved_DF = 0;					/* saved Data Field */
int IB = 0;						/* Instruction Buffer */
int SF = 0;						/* Save Field */
int emode = 0;						/* EAE mode */
int gtf = 0;						/* EAE gtf flag */
int SC = 0;						/* EAE shift count */
int UB = 0;						/* User mode Buffer */
int UF = 0;						/* User mode Flag */
int OSR = 0;						/* Switch Register */
int old_PC = 0;						/* old PC */
int dev_enable = INT_INIT_ENABLE;			/* dev intr enables */
int dev_done = 0;					/* dev done flags */
int int_req = 0;					/* intr requests */
int ibkpt_addr = ILL_ADR_FLAG;				/* breakpoint addr */
int stop_inst = 0;					/* trap on ill inst */
extern int sim_int_char;

int cpu_ex (int *vptr, int addr, UNIT *uptr, int sw);
int cpu_dep (int val, int addr, UNIT *uptr, int sw);
int cpu_reset (DEVICE *dptr);
int cpu_svc (UNIT *uptr);

/* CPU data structures

   cpu_dev	CPU device descriptor
   cpu_unit	CPU unit descriptor
   cpu_reg	CPU register list
   cpu_mod	CPU modifier list
*/

UNIT cpu_unit = { UDATA (&cpu_svc, UNIT_FIX, MEMSIZE) };

REG cpu_reg[] = {
	{ ORDATA (PC, saved_PC, 15) },
	{ ORDATA (AC, saved_LAC, 12) },
	{ FLDATA (L, saved_LAC, 12) },
	{ ORDATA (MQ, saved_MQ, 12) },
	{ ORDATA (SR, OSR, 12) },
	{ GRDATA (IF, saved_PC, 8, 3, 12) },
	{ GRDATA (DF, saved_DF, 8, 3, 12) },
	{ GRDATA (IB, IB, 8, 3, 12) },
	{ ORDATA (SF, SF, 7) },
	{ FLDATA (UB, UB, 0) },
	{ FLDATA (UF, UF, 0) },
	{ ORDATA (SC, SC, 5) },
	{ FLDATA (GTF, gtf, 0) },
	{ FLDATA (EMODE, emode, 0) },
	{ FLDATA (ION, int_req, INT_V_ION) },
	{ FLDATA (ION_DELAY, int_req, INT_V_NO_ION_PENDING) },
	{ FLDATA (CIF_DELAY, int_req, INT_V_NO_CIF_PENDING) },
	{ FLDATA (PWR_INT, int_req, INT_V_PWR) },
	{ FLDATA (UF_INT, int_req, INT_V_UF) },
	{ ORDATA (INT, int_req, INT_V_ION+1), REG_RO },
	{ ORDATA (DONE, dev_done, INT_V_DIRECT), REG_RO },
	{ ORDATA (ENABLE, dev_enable, INT_V_DIRECT), REG_RO },
	{ FLDATA (NOEAE, cpu_unit.flags, UNIT_V_NOEAE), REG_HRO },
	{ ORDATA (OLDPC, old_PC, 15), REG_RO },
	{ FLDATA (STOP_INST, stop_inst, 0) },
	{ ORDATA (BREAK, ibkpt_addr, 16) },
	{ ORDATA (WRU, sim_int_char, 8) },
	{ NULL }  };

MTAB cpu_mod[] = {
	{ UNIT_NOEAE, UNIT_NOEAE, "no EAE", "NOEAE", NULL },
	{ UNIT_NOEAE, 0, "EAE", "EAE", NULL },
	{ 0 }  };

DEVICE cpu_dev = {
	"CPU", &cpu_unit, cpu_reg, cpu_mod,
	1, 8, 15, 1, 8, 12,
	&cpu_ex, &cpu_dep, &cpu_reset,
	NULL, NULL, NULL };

int sim_instr (void)
{
extern int sim_interval;
register int PC, IR, MA, MB, IF, DF, LAC, MQ;
int device, pulse, temp, iot_data, reason;
extern int tti (int pulse, int AC);
extern int tto (int pulse, int AC);
extern int ptr (int pulse, int AC);
extern int ptp (int pulse, int AC);
extern int clk (int pulse, int AC);
extern int lpt (int pulse, int AC);
extern int rk (int pulse, int AC);
extern int rx (int pulse, int AC);
extern int rf60 (int pulse, int AC);
extern int rf61 (int pulse, int AC);
extern int rf62 (int pulse, int AC);
extern int rf64 (int pulse, int AC);
extern int sim_process_event (void);
extern int sim_activate (UNIT *uptr, int interval);

/* Restore register state */

PC = saved_PC & 007777;					/* load local copies */
IF = saved_PC & 070000;
DF = saved_DF & 070000;
LAC = saved_LAC & 017777;
MQ = saved_MQ & 07777;
int_req = INT_UPDATE;
reason = 0;

/* Main instruction fetch/decode loop */

while (reason == 0) {					/* loop until halted */
if (sim_interval <= 0) {				/* check clock queue */
	if (reason = sim_process_event ()) break;  }

if (int_req > INT_PENDING) {				/* interrupt? */
	int_req = int_req & ~INT_ION;			/* interrupts off */
	SF = (UF << 6) | (IF >> 9) | (DF >> 12);	/* form save field */
	IF = IB = DF = UF = UB = 0;			/* clear mem ext */
	old_PC = M[0] = PC;				/* save PC in 0 */
	PC = 1;  }					/* fetch next from 1 */

MA = IF | PC;						/* form PC */
if (MA == ibkpt_addr) {					/* breakpoint? */
	save_ibkpt = ibkpt_addr;			/* save ibkpt */
	ibkpt_addr = ibkpt_addr | ILL_ADR_FLAG;		/* disable */
	sim_activate (&cpu_unit, 1);			/* sched re-enable */
	reason = STOP_IBKPT;				/* stop simulation */
	break;  }

IR = M[MA];						/* fetch instruction */
PC = (PC + 1) & 07777;					/* increment PC */
int_req = int_req | INT_NO_ION_PENDING;			/* clear ION delay */
sim_interval = sim_interval - 1;

/* Instruction decoding.

   The opcode (IR<0:2>), indirect flag (IR<3>), and page flag (IR<4>)
   are decoded together.  This produces 32 decode points, four per
   major opcode.  For IOT, the extra decode points are not useful;
   for OPR, only the group flag (IR<3>) is used.

   The following macros define the address calculations for data and
   jump calculations.  Data calculations return a full 15b extended
   address, jump calculations a 12b field-relative address.

   Note that MA contains IF'PC.
*/

#define ZERO_PAGE MA = IF | (IR & 0177)
#define CURR_PAGE MA = (MA & 077600) | (IR & 0177)
#define INDIRECT if ((MA & 07770) != 00010) MA = DF | M[MA]; \
	else MA = DF | (M[MA] = (M[MA] + 1) & 07777)

#define ZERO_PAGE_J MA = IR & 0177
#define CURR_PAGE_J MA = (MA & 007600) | (IR & 0177)
#define INDIRECT_J if ((MA & 07770) != 00010) MA = M[MA]; \
	else MA = (M[MA] = (M[MA] + 1) & 07777)
#define CHANGE_FIELD IF = IB; UF = UB; \
	int_req = int_req | INT_NO_CIF_PENDING

switch ((IR >> 7) & 037) {				/* decode IR<0:4> */

/* Opcode 0, AND */

case 000:						/* AND, dir, zero */
	ZERO_PAGE;
	LAC = LAC & (M[MA] | 010000);
	break;
case 001:						/* AND, dir, curr */
	CURR_PAGE;
	LAC = LAC & (M[MA] | 010000);
	break;
case 002:						/* AND, indir, zero */
	ZERO_PAGE; INDIRECT;
	LAC = LAC & (M[MA] | 010000);
	break;
case 003:						/* AND, indir, curr */
	CURR_PAGE; INDIRECT;
	LAC = LAC & (M[MA] | 010000);
	break;

/* Opcode 1, TAD */

case 004:						/* TAD, dir, zero */
	ZERO_PAGE;
	LAC = (LAC + M[MA]) & 017777;
	break;
case 005:						/* TAD, dir, curr */
	CURR_PAGE;
	LAC = (LAC + M[MA]) & 017777;
	break;
case 006:						/* TAD, indir, zero */
	ZERO_PAGE; INDIRECT;
	LAC = (LAC + M[MA]) & 017777;
	break;
case 007:						/* TAD, indir, curr */
	CURR_PAGE; INDIRECT;
	LAC = (LAC + M[MA]) & 017777;
	break;

/* Opcode 2, ISZ */

case 010:						/* ISZ, dir, zero */
	ZERO_PAGE;
	M[MA] = MB = (M[MA] + 1) & 07777;
	if (MB == 0) PC = (PC + 1) & 07777;
	break;
case 011:						/* ISZ, dir, curr */
       	CURR_PAGE;
	M[MA] = MB = (M[MA] + 1) & 07777;
	if (MB == 0) PC = (PC + 1) & 07777;
	break;
case 012:						/* ISZ, indir, zero */
	ZERO_PAGE; INDIRECT;
	MB = (M[MA] + 1) & 07777;
	if (MEM_ADDR_OK (MA)) M[MA] = MB;
	if (MB == 0) PC = (PC + 1) & 07777;
	break;
case 013:						/* ISZ, indir, curr */
	CURR_PAGE; INDIRECT;
	MB = (M[MA] + 1) & 07777;
	if (MEM_ADDR_OK (MA)) M[MA] = MB;
	if (MB == 0) PC = (PC + 1) & 07777;
	break;

/* Opcode 3, DCA */

case 014:						/* DCA, dir, zero */
	ZERO_PAGE;
	M[MA] = LAC & 07777;
	LAC = LAC & 010000;
	break;
case 015:						/* DCA, dir, curr */
	CURR_PAGE;
	M[MA] = LAC & 07777;
	LAC = LAC & 010000;
	break;
case 016:						/* DCA, indir, zero */
	ZERO_PAGE; INDIRECT;
	if (MEM_ADDR_OK (MA)) M[MA] = LAC & 07777;
	LAC = LAC & 010000;
	break;
case 017:						/* DCA, indir, curr */
	CURR_PAGE; INDIRECT;
	if (MEM_ADDR_OK (MA)) M[MA] = LAC & 07777;
	LAC = LAC & 010000;
	break;

/* Opcode 4, JMS */

case 020:						/* JMS, dir, zero */
	ZERO_PAGE_J; CHANGE_FIELD;
	M[IF | MA] = old_PC = PC;
	PC = (MA + 1) & 07777;
	break;
case 021:						/* JMS, dir, curr */
	CURR_PAGE_J; CHANGE_FIELD;
	M[IF | MA] = old_PC = PC;
	PC = (MA + 1) & 07777;
	break;
case 022:						/* JMS, indir, zero */
	ZERO_PAGE; INDIRECT_J; CHANGE_FIELD;
	MA = IF | MA;
	old_PC = PC;
	if (MEM_ADDR_OK (MA)) M[MA] = PC;
	PC = (MA + 1) & 07777;
	break;
case 023:						/* JMS, indir, curr */
	CURR_PAGE; INDIRECT_J; CHANGE_FIELD;
	MA = IF | MA;
	old_PC = PC;
	if (MEM_ADDR_OK (MA)) M[MA] = PC;
	PC = (MA + 1) & 07777;
	break;

/* Opcode 5, JMP */

case 024:						/* JMP, dir, zero */
	ZERO_PAGE_J; CHANGE_FIELD;
	old_PC = PC;
	PC = MA;
	break;
case 025:						/* JMP, dir, curr */
	CURR_PAGE_J; CHANGE_FIELD;
	old_PC = PC;
	PC = MA;
	break;
case 026:						/* JMP, indir, zero */
	ZERO_PAGE; INDIRECT_J; CHANGE_FIELD;
	old_PC = PC;
	PC = MA;
	break;
case 027:						/* JMP, indir, curr */
	CURR_PAGE; INDIRECT_J; CHANGE_FIELD;
	old_PC = PC;
	PC = MA;
	break;

/* Opcode 7, OPR group 1 */

case 034:case 035:					/* OPR, group 1 */
	switch ((IR >> 4) & 017) {			/* decode IR<4:7> */
	case 0:						/* nop */
		break;
	case 1:						/* CML */
		LAC = LAC ^ 010000; break;
	case 2:						/* CMA */
		LAC = LAC ^ 07777; break;
	case 3:						/* CMA CML */
		LAC = LAC ^ 017777; break;
	case 4:						/* CLL */
		LAC = LAC & 07777; break;
	case 5:						/* CLL CML = STL */
		LAC = LAC | 010000; break;
	case 6:						/* CLL CMA */
		LAC = (LAC ^ 07777) & 07777; break;
	case 7:						/* CLL CMA CML */
		LAC = (LAC ^ 07777) | 010000; break;
	case 010:					/* CLA */
		LAC = LAC & 010000; break;
	case 011:					/* CLA CML */
		LAC = (LAC & 010000) ^ 010000; break;
	case 012:					/* CLA CMA = STA */
		LAC = LAC | 07777; break;
	case 013:					/* CLA CMA CML */
		LAC = (LAC | 07777) ^ 010000; break;
	case 014:					/* CLA CLL */
		LAC = 0; break;
	case 015:					/* CLA CLL CML */
		LAC = 010000; break;
	case 016:					/* CLA CLL CMA */
		LAC = 07777; break;
	case 017:					/* CLA CLL CMA CML */
		LAC = 017777; break;  }			/* end switch opers */
	if (IR & 01) LAC = (LAC + 1) & 017777;		/* IAC */
	switch ((IR >> 1) & 07) {			/* decode IR<8:10> */
	case 0: 					/* nop */
		break;
	case 1:						/* BSW */
		LAC = (LAC & 010000) | ((LAC >> 6) & 077)
			| ((LAC & 077) << 6); break;
	case 2:						/* RAL */
		LAC = ((LAC << 1) | (LAC >> 12)) & 017777; break;
	case 3:						/* RTL */
		LAC = ((LAC << 2) | (LAC >> 11)) & 017777; break;
	case 4:						/* RAR */
		LAC = ((LAC >> 1) | (LAC << 12)) & 017777; break;
	case 5:						/* RTR */
		LAC = ((LAC >> 2) | (LAC << 11)) & 017777; break;
	case 6:						/* RAL RAR - undef */
		LAC = LAC & (IR | 010000); break;	/* uses AND path */
	case 7:						/* RTL RTR - undef */
		LAC = (LAC & 010000) | (MA & 07600) | (IR & 0177);
		break;  }				/* uses address path */
	break;						/* end group 1 */

/* OPR group 2 */

case 036:case 037:					/* OPR, groups 2, 3 */
	if ((IR & 01) == 0) {				/* group 2 */
		switch ((IR >> 3) & 017) {		/* decode IR<6:8> */
		case 0: 				/* nop */
			break;
		case 1:					/* SKP */
			PC = (PC + 1) & 07777; break;
		case 2: 				/* SNL */
			if (LAC >= 010000) PC = (PC + 1) & 07777; break;
		case 3: 				/* SZL */
			if (LAC < 010000) PC = (PC + 1) & 07777; break;
		case 4: 				/* SZA */
			if ((LAC & 07777) == 0) PC = (PC + 1) & 07777; break;
		case 5: 				/* SNA */
			if ((LAC & 07777) != 0) PC = (PC + 1) & 07777; break;
		case 6:					/* SZA | SNL */
			if ((LAC == 0) || (LAC >= 010000))
				PC = (PC + 1) & 07777; break;
		case 7:					/* SNA & SZL */
			if ((LAC != 0) && (LAC < 010000))
				PC = (PC + 1) & 07777; break;
		case 010: 				/* SMA */
			if ((LAC & 04000) != 0) PC = (PC + 1) & 07777; break;
		case 011: 				/* SPA */
			if ((LAC & 04000) == 0) PC = (PC + 1) & 07777; break;
		case 012:				/* SMA | SNL */
			if (LAC >= 04000) PC = (PC + 1) & 07777; break;
		case 013:				/* SPA & SZL */
			if (LAC < 04000) PC = (PC + 1) & 07777; break;
		case 014:				/* SMA | SZA */
			if (((LAC & 04000) != 0) || ((LAC & 07777) == 0))
				PC = (PC + 1) & 07777; break;
		case 015:				/* SPA & SNA */
			if (((LAC & 04000) == 0) && ((LAC & 07777) != 0))
				PC = (PC + 1) & 07777; break;
		case 016:				/* SMA | SZA | SNL */
			if ((LAC >= 04000) || (LAC == 0))
				PC = (PC + 1) & 07777; break;
		case 017:				/* SPA & SNA & SZL */
			if ((LAC < 04000) && (LAC != 0))
				PC = (PC + 1) & 07777; break;
			}				/* end switch skips */
		if (IR & 0200) LAC = LAC & 010000;	/* CLA */
		if ((IR & 06) && UF) {			/* privileged? */
			int_req = int_req | INT_UF; break;  }
		if (IR & 04) LAC = LAC | OSR;		/* OSR */
		if (IR & 02) reason = STOP_HALT;	/* HLT */
		break;  }	                      	/* end group 2 */

/* OPR group 3 standard

   MQA!MQL exchanges AC and MQ, as follows:

	temp = MQ;
	MQ = LAC & 07777;
	LAC = LAC & 010000 | temp;
*/

	temp = MQ;					/* group 3 */
	if (IR & 0200) LAC = LAC & 010000;		/* CLA */
	if (IR & 0020) {				/* MQL */
		MQ = LAC & 07777;
		LAC = LAC & 010000;  }
	if (IR & 0100) LAC = LAC | temp;		/* MQA */
	if ((IR & 0056) && (cpu_unit.flags & UNIT_NOEAE)) {
		reason = stop_inst;			/* EAE not present */
		break;  }

/* OPR group 3 EAE

   The EAE operates in two modes:

	Mode A, PDP-8/I compatible
	Mode B, extended capability

   Mode B provides eight additional subfunctions; in addition, some
   of the Mode A functions operate differently in Mode B.

   The mode switch instructions are decoded explicitly and cannot be
   microprogrammed with other EAE functions (SWAB performs an MQL as
   part of standard group 3 decoding).  If mode switching is decoded,
   all other EAE timing is suppressed.
*/

	if (IR == 07431) {				/* SWAB */
		emode = 1;				/* set mode flag */
		break;  }
	if (IR == 07447) {				/* SWBA */
		emode = gtf = 0;			/* clear mode, gtf */
		break;  }

/* If not switching modes, the EAE operation is determined by the mode
   and IR<6,8:10>:

   <6:10>	mode A		mode B		comments

   0x000	NOP		NOP
   0x001	SCL		ACS
   0x010	MUY		MUY		if mode B, next = address
   0x011	DVI		DVI		if mode B, next = address
   0x100	NMI		NMI		if mode B, clear AC if
						 result = 4000'0000
   0x101	SHL		SHL		if mode A, extra shift
   0x110	ASR		ASR		if mode A, extra shift
   0x111	LSR		LSR		if mode A, extra shift
   1x000	SCA		SCA
   1x001	SCA + SCL	DAD
   1x010	SCA + MUY	DST
   1x011	SCA + DVI	SWBA            NOP if not detected earlier
   1x100	SCA + NMI	DPSZ		
   1x101	SCA + SHL	DPIC            must be combined with MQA!MQL
   1x110	SCA + ASR	DCM		must be combined with MQA!MQL
   1x111	SCA + LSR	SAM

   EAE instructions which fetch memory operands use the CPU's DEFER
   state to read the first word; if the address operand is in locations
   x0010 - x0017, it is autoincremented.
*/

/* EAE continued */

	switch ((IR >> 1) & 027) {			/* decode IR<6,8:10> */
	case 020:					/* mode A, B: SCA */
		LAC = LAC | SC;
		break;
	case 0:						/* mode A, B: NOP */
		break;
	case 021:					/* mode B: DAD */
		if (emode) {
			MA = IF | PC; INDIRECT;		/* defer state */
			MQ = MQ + M[MA];
			MA = DF | ((MA + 1) & 07777);
			LAC = (LAC & 07777) + M[MA] + (MQ >> 12);
			MQ = MQ & 07777;
			PC = (PC + 1) & 07777;
			break;  }
		LAC = LAC | SC;				/* mode A: SCA then */
	case 1:						/* mode B: ACS */
		if (emode) {
			SC = LAC & 037;
			LAC = LAC & 010000;
			break;  }
		SC = (~M[IF | PC]) & 037;		/* mode A: SCL */
		PC = (PC + 1) & 07777;
		break;
	case 022:					/* mode B: DST */
		if (emode) {
			MA = IF | PC; INDIRECT;		/* defer state */
			if (MEM_ADDR_OK (MA)) M[MA] = MQ & 07777;
			MA = DF | ((MA + 1) & 07777);
			if (MEM_ADDR_OK (MA))  M[MA] = LAC & 07777;
			PC = (PC + 1) & 07777;
			break;  }
		LAC = LAC | SC;				/* mode A: SCA then */
	case 2:						/* MUY */
		MA = IF | PC;
		if (emode) { INDIRECT; }		/* mode B: defer */
		temp = (MQ * M[MA]) + (LAC & 07777);
		LAC = (temp >> 12) & 07777;
		MQ = temp & 07777;
		PC = (PC + 1) & 07777;
		SC = 014;				/* 12 shifts */
		break;

/* EAE continued */

	case 023:					/* mode B: SWBA */
		if (emode) break;
		LAC = LAC | SC;				/* mode A: SCA then */
	case 3:						/* DVI */
		MA = IF | PC;
		if (emode) { INDIRECT; };		/* mode B: defer */
		if ((LAC & 07777) >= M[MA]) {		/* overflow? */
			LAC = LAC | 010000;		/* set link */
			MQ = ((MQ << 1) + 1) & 07777;	/* rotate MQ */
			SC = 01;  }			/* 1 shift */
		else {	temp = ((LAC & 07777) << 12) | MQ;
			MQ = temp / M[MA];
			LAC = temp % M[MA];
			SC = 015;  }			/* 13 shifts */
		PC = (PC + 1) & 07777;
		break;
	case 024:					/* mode B: DPSZ */
		if (emode) {
			if (((LAC | MQ) & 07777) == 0) PC = (PC + 1) & 07777;
			break;  }
		LAC = LAC | SC;				/* mode A: SCA then */
	case 4:						/* NMI */
		temp = (LAC << 12) | MQ;		/* preserve link */
		for (SC = 0; ((temp & 017777777) != 0) &&
			(temp & 040000000) == ((temp << 1) & 040000000); SC++)
			temp = temp << 1;
		LAC = (temp >> 12) & 017777;
		MQ = temp & 07777;
		if (emode && ((LAC & 07777) == 04000) && (MQ == 0))
			LAC = LAC & 010000;		/* clr if 4000'0000 */
		break;
	case 025:					/* mode B: DPIC */
		if (emode) {
			temp = (LAC + 1) & 07777;	/* SWP already done! */
			LAC = MQ + (temp == 0);
			MQ = temp;
			break;  }
		LAC = LAC | SC;				/* mode A: SCA then */
	case 5:						/* SHL */
		SC = (M[IF | PC] & 037) + (emode ^ 1);	/* shift+1 if mode A */
		temp = ((LAC << 12) | MQ) << SC;	/* preserve link */
		LAC = (temp >> 12) & 017777;
		MQ = temp & 07777;
		PC = (PC + 1) & 07777;
		SC = emode? 037: 0;			/* SC = 0 if mode A */
		break;

/* EAE continued */

	case 026:					/* mode B: DCM */
		if (emode) {
			temp = (-LAC) & 07777;		/* SWP already done! */
			LAC = (MQ ^ 07777) + (temp == 0);
			MQ = temp;
			break;  }
		LAC = LAC | SC;				/* mode A: SCA then */
	case 6:						/* ASR */
		SC = (M[IF | PC] & 037) + (emode ^ 1);	/* shift+1 if mode A */
		temp = ((LAC & 07777) << 12) | MQ;	/* sext from AC0 */
		if (LAC & 04000) temp = temp | ~037777777;
		if (emode && (SC != 0)) gtf = (temp >> (SC - 1)) & 1;
		temp = temp >> SC;
		if (LAC & 04000) temp = temp | (-1 << (32 - SC));
		LAC = (temp >> 12) & 017777;
		MQ = temp & 07777;
		PC = (PC + 1) & 07777;
		SC = emode? 037: 0;			/* SC = 0 if mode A */
		break;
	case 027:					/* mode B: SAM */
		if (emode) {
			MA = LAC & 07777;
			LAC = MQ + (MA ^ 07777) + 1;	/* L'AC = MQ - AC */
			gtf = (MA <= MQ) ^ ((MA ^ MQ) >> 11);
			break;  }
		LAC = LAC | SC;				/* mode A: SCA then */
	case 7:						/* LSR */
		SC = (M[IF | PC] & 037) + (emode ^ 1);	/* shift+1 if mode A */
		temp = ((LAC & 07777) << 12) | MQ;	/* clear link */
		if (emode && (SC != 0)) gtf = (temp >> (SC - 1)) & 1;
		temp = temp >> SC;
		LAC = (temp >> 12) & 07777;
		MQ = temp & 07777;
		PC = (PC + 1) & 07777;
		SC = emode? 037: 0;			/* SC = 0 if mode A */
		break;  }				/* end switch */
	break;						/* end case 7 */

/* Opcode 6, IOT */

case 030:case 031:case 032:case 033:			/* IOT */
	if (UF) {					/* privileged? */
		int_req = int_req | INT_UF;
		break;  }
	device = (IR >> 3) & 077;			/* device = IR<3:8> */
	pulse = IR & 07;				/* pulse = IR<9:11> */
	iot_data = LAC & 07777;				/* AC unchanged */
	switch (device) {				/* decode IR<3:8> */
	case 0:						/* CPU control */
		switch (pulse) {			/* decode IR<9:11> */
		case 0:					/* SKON */
			if (int_req & INT_ION) PC = (PC + 1) & 07777;
			int_req = int_req & ~INT_ION;
			break;
		case 1:					/* ION */
			int_req = (int_req | INT_ION) & ~INT_NO_ION_PENDING;
			break;
		case 2:					/* IOF */
			int_req = int_req & ~INT_ION;
			break;
		case 3:					/* SRQ */
			if (int_req & INT_ALL) PC = (PC + 1) & 07777;
			break;
		case 4:					/* GTF */
			LAC = (LAC & 010000) |
			      ((LAC & 010000) >> 1) | (gtf << 10) |
			      (((int_req & INT_ALL) != 0) << 9) |
			      (((int_req & INT_ION) != 0) << 7) | SF;
			break;
		case 5:					/* RTF */
			gtf = ((LAC & 02000) >> 10);
			UB = (LAC & 0100) >> 6;
			IB = (LAC & 0070) << 9;
			DF = (LAC & 0007) << 12;
			LAC = ((LAC & 04000) << 1) | iot_data;
			int_req = (int_req | INT_ION) & ~INT_NO_CIF_PENDING;
			break;
		case 6:					/* SGT */
			if (gtf) PC = (PC + 1) & 07777;
			break;
		case 7:					/* CAF */
			gtf = 0;
			emode = 0;
			int_req = int_req & INT_NO_CIF_PENDING;
			dev_done = 0;
			dev_enable = INT_INIT_ENABLE;
			LAC = 0;
			break;  }			/* end switch pulse */
		continue;				/* skip rest of IOT */

/* IOT, continued: memory extension */

	case 020:case 021:case 022:case 023:
	case 024:case 025:case 026:case 027:		/* memory extension */
		switch (pulse) {			/* decode IR<9:11> */
		case 1: 				/* CDF */
			DF = (IR & 0070) << 9;
			break;
		case 2:					/* CIF */
			IB = (IR & 0070) << 9;
			int_req = int_req & ~INT_NO_CIF_PENDING;
			break;
		case 3:					/* CDF CIF */
			DF = IB = (IR & 0070) << 9;
			int_req = int_req & ~INT_NO_CIF_PENDING;
			break;
		case 4:
			switch (device & 07) {		/* decode IR<5:3> */
			case 0:				/* CINT */
				int_req = int_req & ~INT_UF;
				break;
			case 1:				/* RDF */
				LAC = LAC | (DF >> 9);
				break;
			case 2:				/* RIF */
				LAC = LAC | (IF >> 9);
				break;
			case 3:				/* RIB */
				LAC = LAC | SF;
				break;
			case 4:				/* RMF */
				UB = (SF & 0100) >> 6;
				IB = (SF & 0070) << 9;
				DF = (SF & 0007) << 12;
				int_req = int_req & ~INT_NO_CIF_PENDING;
				break;
			case 5:				/* SINT */
				if (int_req & INT_UF) PC = (PC + 1) & 07777;
				break;
			case 6:				/* CUF */
				UB = 0;
				int_req = int_req & ~INT_NO_CIF_PENDING;
				break;
			case 7:				/* SUF */
				UB = 1;
				int_req = int_req & ~INT_NO_CIF_PENDING;
				break;  } 		/* end switch device */
			break;
		default:
			reason = stop_inst;
			break;  }			/* end switch pulse */
		continue;				/* skip rest of IOT */

/* IOT, continued: I/O devices */

	default:					/* unknown device */
		reason = stop_inst;			/* stop on flag */
		continue;				/* skip rest of IOT */
	case 010:					/* power fail */
		switch (pulse) {			/* decode IR<9:11> */
		case 1:					/* SBE */
			break;
		case 2: 				/* SPL */
			if (int_req & INT_PWR) PC = (PC + 1) & 07777;
			break;
		case 3: 				/* CAL */
			int_req = int_req & ~INT_PWR;
			break;
		default:
			reason = stop_inst;
			break;  }			/* end switch pulse */
		continue;				/* skip rest of IOT */
	case 1:						/* PTR */
		iot_data = ptr (pulse, iot_data);
		break;
	case 2:						/* PTP */
		iot_data = ptp (pulse, iot_data);
		break;
	case 3:						/* TTI */
		iot_data = tti (pulse, iot_data);
		break;
	case 4: 					/* TTO */
		iot_data = tto (pulse, iot_data);
		break;
	case 013:					/* CLK */
		iot_data = clk (pulse, iot_data);
		break;
	case 060:					/* RF08 */
		iot_data = rf60 (pulse, iot_data);
		break;
	case 061:
		iot_data = rf61 (pulse, iot_data);
		break;
	case 062:
		iot_data = rf62 (pulse, iot_data);
		break;
	case 064:
		iot_data = rf64 (pulse, iot_data);
		break;
	case 066:					/* LPT */
		iot_data = lpt (pulse, iot_data);
		break;
	case 074:					/* RK8E */
		iot_data = rk (pulse, iot_data);
		break;
	case 075:					/* RX8E */
		iot_data = rx (pulse, iot_data);
		break;  }				/* end switch device */
	LAC = (LAC & 010000) | (iot_data & 07777);
	if (iot_data & IOT_SKP) PC = (PC + 1) & 07777;
	if (iot_data >= IOT_REASON) reason = iot_data >> IOT_V_REASON;
	break;						/* end case 6 */
	}						/* end switch opcode */
}							/* end while */

/* Simulation halted */

saved_PC = IF | (PC & 07777);				/* save copies */
saved_DF = DF & 070000;
saved_LAC = LAC & 017777;
saved_MQ = MQ & 07777;
return reason;
}							/* end sim_instr */

/* Other CPU routines

	cpu_ex		read simulated memory
	cpu_dep		write simulated memory
	cpu_reset	reset the CPU
	cpu_svc		restore breakpoint after SCP service
*/

int cpu_ex (int *vptr, int addr, UNIT *uptr, int sw)
{
if (addr >= MEMSIZE) return SCPE_NXM;
if (vptr != NULL) *vptr = M[addr] & 07777;
return SCPE_OK;
}

int cpu_dep (int val, int addr, UNIT *uptr, int sw)
{
if (addr >= MEMSIZE) return SCPE_NXM;
M[addr] = val & 07777;
return SCPE_OK;
}

int cpu_reset (DEVICE *dptr)
{
int_req = (int_req & (~INT_ION)) | INT_NO_CIF_PENDING;
saved_DF = IB = (saved_PC >> 12) & 03;
UF = UB = gtf = emode = 0;
return SCPE_OK;
}

int cpu_svc (UNIT *uptr)
{
if ((ibkpt_addr & ~ILL_ADR_FLAG) == save_ibkpt) ibkpt_addr = save_ibkpt;
return SCPE_OK;
}
