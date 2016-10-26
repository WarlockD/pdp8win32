/* pdp11_fp.c: PDP-11 floating point simulator (32b version)

   Copyright (c) 1993, 1994, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   This module simulates the PDP-11 floating point unit (FP11 series).
   It is called from the instruction decoder for opcodes 170000:177777.

   The floating point unit recognizes three instruction formats:

   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+	no operand
   | 1  1  1  1| 0  0  0  0  0  0|      opcode     |	170000:
   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+	170077

   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+	one operand
   | 1  1  1  1| 0  0  0| opcode |    dest spec    |	170100:
   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+	170777

   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+	register + operand
   | 1  1  1  1|   opcode  | fac |    dest spec    |	171000:
   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+	177777

   The instruction space is further extended through use of the floating
   point status register (FPS) mode bits.  Three mode bits affect how
   instructions are interpreted:

	FPS_D		if 0, floating registers are single precision
			if 1, floating registers are double precision

	FPS_L		if 0, integer operands are word
			if 1, integer operands are longword

	FPS_T		if 0, floating operations are rounded
			if 1, floating operations are truncated

   FPS also contains the condition codes for the floating point unit,
   and exception enable bits for individual error conditions.  Exceptions
   cause a trap through 0244, unless the individual exception, or all
   exceptions, are disabled.  Illegal address mode, undefined variable,
   and divide by zero abort the current instruction; all other exceptions
   permit the instruction to complete.  (Aborts are implemented as traps
   that request an "interrupt" trap.  If an interrupt is pending, it is
   serviced; if not, trap_req is updated and processing continues.)

   Floating point specifiers are similar to integer specifiers, with
   the length of the operand being up to 8 bytes.  In two specific cases,
   the floating point unit reads or writes only two bytes, rather than
   the length specified by the operand type:

	register	for integers, only 16b are accessed; if the
			operand is 32b, these are the high order 16b
			of the operand

	immediate	for integers or floating point, only 16b are
			accessed;  if the operand is 32b or 64b, these
			are the high order 16b of the operand
*/

#include "pdp11_defs.h"

/* Floating point status register */

#define FPS_ER		0100000				/* error */
#define FPS_ID		0040000				/* interrupt disable */
#define FPS_IUV		0004000				/* int on undef var */
#define FPS_IU		0002000				/* int on underflow */
#define FPS_IV		0001000				/* int on overflow */
#define FPS_IC		0000400				/* int on conv error */
#define FPS_D		0000200				/* single/double */
#define FPS_L		0000100				/* word/long */
#define FPS_T		0000040				/* round/truncate */
#define FPS_N		(1 << PSW_V_N)
#define FPS_Z		(1 << PSW_V_Z)
#define FPS_V		(1 << PSW_V_V)
#define FPS_C		(1 << PSW_V_C)
#define FPS_CC		(FPS_N + FPS_Z + FPS_V + FPS_C)
#define FPS_RW		(FPS_ER + FPS_ID + FPS_IUV + FPS_IU + FPS_IV + \
			FPS_IC + FPS_D + FPS_L + FPS_T + FPS_CC)

/* Floating point exception codes */

#define FEC_OP		2				/* illegal op/mode */
#define FEC_DZRO	4				/* divide by zero */
#define FEC_ICVT	6				/* conversion error */
#define FEC_OVFLO	8				/* overflow */
#define FEC_UNFLO	10				/* underflow */
#define FEC_UNDFV	12				/* undef variable */

/* Floating point format, all assignments 32b relative */

#define FP_V_SIGN	(63 - 32)			/* high lw: sign */
#define FP_V_EXP	(55 - 32)			/* exponent */
#define FP_V_HB		FP_V_EXP			/* hidden bit */
#define FP_V_F0		(48 - 32)			/* fraction 0 */
#define FP_V_F1		(32 - 32)			/* fraction 1 */
#define FP_V_FROUND	(31 - 32)			/* f round point */
#define FP_V_F2		16				/* low lw: fraction 2 */
#define FP_V_F3		0				/* fraction 3 */
#define FP_V_DROUND	(-1)				/* d round point */
#define FP_M_EXP	0377
#define FP_SIGN		(1 << FP_V_SIGN)
#define FP_EXP		(FP_M_EXP << FP_V_EXP)
#define FP_HB		(1 << FP_V_HB)
#define FP_FRACH	((1 << FP_V_HB) - 1)
#define FP_FRACL	0xFFFFFFFF
#define FP_BIAS		0200				/* exponent bias */
#define FP_GUARD	3				/* guard bits */

/* Data lengths */

#define WORD		2
#define LONG		4
#define QUAD		8

/* Double precision operations on 64b quantities */

#define F_LOAD(qd,ac,ds) ds.h = ac.h; ds.l = (qd)? ac.l: 0
#define F_LOAD_P(qd,ac,ds) ds -> h = ac.h; ds -> l = (qd)? ac.l: 0
#define F_LOAD_FRAC(qd,ac,ds) ds.h = (ac.h & FP_FRACH) | FP_HB; \
	ds.l = (qd)? ac.l: 0
#define F_STORE(qd,sr,ac) ac.h = sr.h; if ((qd)) ac.l = sr.l
#define F_STORE_P(qd,sr,ac) ac.h = sr -> h; if ((qd)) ac.l = sr -> l
#define F_GET_FRAC_P(sr,ds) ds.l = sr -> l; \
	ds.h = (sr -> h & FP_FRACH) | FP_HB
#define F_ADD(s2,s1,ds) ds.l = (s1.l + s2.l) & 0xFFFFFFFF; \
	ds.h = (s1.h + s2.h + (ds.l < s2.l)) & 0xFFFFFFFF
#define F_SUB(s2,s1,ds) ds.h = (s1.h - s2.h - (s1.l < s2.l)) & 0xFFFFFFFF; \
	ds.l = (s1.l - s2.l) & 0xFFFFFFFF
#define F_LSH_1(ds) ds.h = ((ds.h << 1) | ((ds.l >> 31) & 1)) & 0xFFFFFFFF; \
	ds.l = (ds.l << 1) & 0xFFFFFFFF
#define F_LSH_3(ds) ds.h = ((ds.h << 3) | ((ds.l >> 29) & 7)) & 0xFFFFFFFF; \
	ds.l = (ds.l << 3) & 0xFFFFFFFF
#define F_RSH_1(ds) ds.l = ((ds.l >> 1) & 0x7FFFFFFF) | ((ds.h & 1) << 31); \
	ds.h = ((ds.h >> 1) & 0x7FFFFFFF)
#define F_RSH_3(ds) ds.l = ((ds.l >> 3) & 0x1FFFFFFF) | ((ds.h & 7) << 29); \
	ds.h = ((ds.h >> 3) & 0x1FFFFFFF)
#define F_LSH(sr,n,ds) \
	ds.h = (((n) >= 32)? (sr.l << ((n) - 32)): \
		(sr.h << (n)) | ((sr.l >> (32 - (n))) & ((1 << (n)) - 1))) \
		& 0xFFFFFFFF; \
	ds.l = ((n) >= 32)? 0: (sr.l << (n)) & 0xFFFFFFFF
#define F_RSH(sr,n,ds) \
	ds.l = (((n) >= 32)? (sr.h >> ((n) - 32)) & ((1 << (64 - (n))) - 1): \
		((sr.l >> (n)) & ((1 << (32 - (n))) - 1)) | \
		(sr.h << (32 - (n)))) & 0xFFFFFFFF; \
	ds.h = ((n) >= 32)? 0: \
		((sr.h >> (n)) & ((1 << (32 - (n))) - 1)) & 0xFFFFFFFF
#define F_LT(x,y) ((x.h < y.h) || ((x.h == y.h) && (x.l < y.l)))
#define F_LT_AP(x,y) (((x -> h & ~FP_SIGN) < (y -> h & ~FP_SIGN)) || \
	(((x -> h & ~FP_SIGN) == (y -> h & ~FP_SIGN)) && (x -> l < y -> l)))
#if (FP_GUARD == 3)
#define F_LSH_GUARD(ds) F_LSH_3 (ds)
#define F_RSH_GUARD(ds) F_RSH_3 (ds)
#else
#define F_LSH_GUARD(ds) F_LSH (ds, FP_GUARD, ds)
#define F_RSH_GUARD(ds) F_RSH (ds, FP_GUARD, ds)
#endif

#define GET_BIT(ir,n) (((ir) >> n) & 1)
#define GET_SIGN(ir) GET_BIT((ir), FP_V_SIGN)
#define GET_EXP(ir) (((ir) >> FP_V_EXP) & FP_M_EXP)
#define GET_SIGN_L(ir) GET_BIT((ir), 31)
#define GET_SIGN_W(ir) GET_BIT((ir), 15)

extern jmp_buf save_env;
extern int FEC, FEA, FPS;
extern int CPUERR, trap_req;
extern int N, Z, V, C;
extern int R[8];
extern fpac_t FR[6];
extern int GeteaW (int spec);
extern int ReadW (int addr);
extern void WriteW (int data, int addr);
fpac_t zero_fac = { 0, 0 };
fpac_t one_fac = { 1, 0 };
fpac_t fround_fac = { (1 << (FP_V_FROUND + 32)), 0 };
fpac_t fround_guard_fac = { 0, (1 << (FP_V_FROUND + FP_GUARD)) };
fpac_t dround_guard_fac = { (1 << (FP_V_DROUND + FP_GUARD)), 0 };
fpac_t fmask_fac = { 0xFFFFFFFF, (1 << (FP_V_HB + FP_GUARD + 1)) - 1 };
int backup_PC;
int fpnotrap (int code);
int GeteaFP (int spec, int len);
unsigned int ReadI (int addr, int spec, int len);
void ReadFP (fpac_t *fac, int addr, int spec, int len);
void WriteI (int data, int addr, int spec, int len);
void WriteFP (fpac_t *data, int addr, int spec, int len);
int setfcc (int old_status, int result_high, int newV);
int addfp11 (fpac_t *src1, fpac_t *src2);
int mulfp11 (fpac_t *src1, fpac_t *src2);
int divfp11 (fpac_t *src1, fpac_t *src2);
int modfp11 (fpac_t *src1, fpac_t *src2, fpac_t *frac);
void frac_mulfp11 (fpac_t *src1, fpac_t *src2);
int roundfp11 (fpac_t *src);
int round_and_pack (fpac_t *fac, int exp, fpac_t *frac);

/* Set up for instruction decode and execution */

void fp11 (int IR)
{
int dst, ea, ac, dstspec;
int i, qdouble, lenf, leni;
int newV, exp, sign;
fpac_t fac, fsrc, modfrac;
static unsigned int i_limit[2][2] =
	{ { 0x80000000, 0x80010000}, { 0x80000000, 0x80000001 } };

backup_PC = PC;						/* save PC for FEA */
ac = (IR >> 6) & 03;					/* fac is IR<7:6> */
dstspec = IR & 077;
qdouble = FPS & FPS_D;
lenf = qdouble? QUAD: LONG;
switch ((IR >> 8) & 017) {				/* decode IR<11:8> */
case 0:
	switch (ac) {					/* decode IR<7:6> */
	case 0:						/* specials */
		if (IR == 0170000) {			/* CFCC */
			N = (FPS >> PSW_V_N) & 1;
			Z = (FPS >> PSW_V_Z) & 1;
			V = (FPS >> PSW_V_V) & 1;
			C = (FPS >> PSW_V_C) & 1;  }
		else if (IR == 0170001)			/* SETF */
			FPS = FPS & ~FPS_D;
		else if (IR == 0170002)			/* SETI */
			FPS = FPS & ~FPS_L;
		else if (IR == 0170011)			/* SETD */
			FPS = FPS | FPS_D;
		else if (IR == 0170012)			/* SETL */
			FPS = FPS | FPS_L;
		else fpnotrap (FEC_OP);
		break;
	case 1:						/* LDFPS */
		dst = (dstspec <= 07)? R[dstspec]: ReadW (GeteaW (dstspec));
		FPS = dst & FPS_RW;
		break;
	case 2:						/* STFPS */
		FPS = FPS & FPS_RW;
		if (dstspec <= 07) R[dstspec] = FPS;
		else WriteW (FPS, GeteaW (dstspec));
		break;
	case 3:						/* STST */
		if (dstspec <= 07) R[dstspec] = FEC;
		else WriteI ((FEC << 16) | FEA, GeteaFP (dstspec, LONG),
			 dstspec, LONG);
		break;  }				/* end switch <7:6> */
	break;						/* end case 0 */

/* "Easy" instructions */

case 1:
	switch (ac) {					/* decode IR<7:6> */
	case 0:						/* CLRf */
		WriteFP (&zero_fac, GeteaFP (dstspec, lenf), dstspec, lenf);
		FPS = (FPS & ~FPS_CC) | FPS_Z;
		break;
	case 1:						/* TSTf */
		ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
		FPS = setfcc (FPS, fsrc.h, 0);
		break;
	case 2:						/* ABSf */
		ReadFP (&fsrc, ea = GeteaFP (dstspec, lenf), dstspec, lenf);
		if (GET_EXP (fsrc.h) == 0) fsrc = zero_fac;
		else fsrc.h = fsrc.h & ~FP_SIGN;
		WriteFP (&fsrc, ea, dstspec, lenf);
		FPS = setfcc (FPS, fsrc.h, 0);
		break;
	case 3:						/* NEGf */
		ReadFP (&fsrc, ea = GeteaFP (dstspec, lenf), dstspec, lenf);
		if (GET_EXP (fsrc.h) == 0) fsrc = zero_fac;
		else fsrc.h = fsrc.h ^ FP_SIGN;
		WriteFP (&fsrc, ea, dstspec, lenf);
		FPS = setfcc (FPS, fsrc.h, 0);
		break;  }				/* end switch <7:6> */
	break;						/* end case 1 */
case 5:							/* LDf */
	ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
	F_STORE (qdouble, fsrc, FR[ac]);
	FPS = setfcc (FPS, fsrc.h, 0);
	break;
case 010:						/* STf */
	F_LOAD (qdouble, FR[ac], fac);
	WriteFP (&fac, GeteaFP (dstspec, lenf), dstspec, lenf);
	break;
case 017:						/* LDCff' */
	ReadFP (&fsrc, GeteaFP (dstspec, 12 - lenf), dstspec, 12 - lenf);
	if ((FPS & (FPS_D + FPS_T)) == 0) newV = roundfp11 (&fsrc);
	else newV = 0;
	F_STORE (qdouble, fsrc, FR[ac]);
	FPS = setfcc (FPS, fsrc.h, newV);
	break;
case 014:						/* STCff' */
	F_LOAD (qdouble, FR[ac], fac);
	if ((FPS & (FPS_D + FPS_T)) == FPS_D) newV = roundfp11 (&fac);
	else newV = 0;
	WriteFP (&fac, GeteaFP (dstspec, 12 - lenf), dstspec, 12 - lenf);
	FPS = setfcc (FPS, fac.h, newV);
	break;

/* Compare instruction */

case 7:							/* CMPf */
	ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
	F_LOAD (qdouble, FR[ac], fac);
	if (GET_EXP (fsrc.h | fac.h) == 0) {		/* both zero? */
		F_STORE (qdouble, zero_fac, FR[ac]);
		FPS = (FPS & ~FPS_CC) | FPS_Z;
		break;  }
	if ((fsrc.h == fac.h) && (fsrc.l == fac.l)) {	/* equal? */
		FPS = (FPS & ~FPS_CC) | FPS_Z;
		break;  }
	FPS = (FPS & ~FPS_CC) | ((fsrc.h >> (FP_V_SIGN - PSW_V_N)) & FPS_N);
	if ((GET_SIGN (fsrc.h ^ fac.h) == 0) && (GET_EXP (fac.h) != 0) &&
		F_LT (fsrc, fac)) FPS = FPS ^ FPS_N;
	break;

/* Load and store exponent instructions */

case 015:						/* LDEXP */
	dst = (dstspec <= 07)? R[dstspec]: ReadW (GeteaW (dstspec));
	F_LOAD (qdouble, FR[ac], fac);
	fac.h = (fac.h & ~FP_EXP) | (((dst + FP_BIAS) & FP_M_EXP) << FP_V_EXP);
	newV = 0;
	if ((dst > 0177) || (dst <= 0177600)) {
		if (dst < 0100000) {
			if (fpnotrap (FEC_OVFLO)) fac = zero_fac;
			newV = FPS_V;  }
		else {	if (fpnotrap (FEC_UNFLO)) fac = zero_fac;  }  }
	F_STORE (qdouble, fac, FR[ac]);
	FPS = setfcc (FPS, fac.h, newV);
	break;	
case 012:						/* STEXP */
	dst = (GET_EXP (FR[ac].h) - FP_BIAS) & 0177777;
	N = GET_SIGN_W (dst);
	Z = (dst == 0);
	V = 0;
	C = 0;
	FPS = (FPS & ~FPS_CC) | (N << PSW_V_N) | (Z << PSW_V_N);
	if (dstspec <= 07) R[dstspec] = dst;
	else WriteW (dst, GeteaW (dstspec));
	break;

/* Integer convert instructions */

case 016:						/* LDCif */
	leni = FPS & FPS_L? LONG: WORD;
	if (dstspec <= 07) fac.l = R[dstspec] << 16;
	else fac.l = ReadI (GeteaFP (dstspec, leni), dstspec, leni);
	fac.h = 0;
	if (fac.l) {
		if (sign = GET_SIGN_L (fac.l)) fac.l = -fac.l;
		for (i = 0; GET_SIGN_L (fac.l) == 0; i++) fac.l = fac.l << 1;
		exp = ((FPS & FPS_L)? FP_BIAS + 32: FP_BIAS + 16) - i;
		fac.h = (sign << FP_V_SIGN) | (exp << FP_V_EXP) |
		  ((fac.l >> (31 - FP_V_HB)) & FP_FRACH);
		fac.l = (fac.l << (FP_V_HB + 1)) & FP_FRACL;
		if ((FPS & (FPS_D + FPS_T)) == 0) roundfp11 (&fac);  }
	F_STORE (qdouble, fac, FR[ac]);
	FPS = setfcc (FPS, fac.h, 0);
	break;
case 013:						/* STCfi */
	sign = GET_SIGN (FR[ac].h);			/* get sign, */
	exp = GET_EXP (FR[ac].h);			/* exponent, */
	F_LOAD_FRAC (qdouble, FR[ac], fac);		/* fraction */
	if (FPS & FPS_L) {
		leni = LONG;
		i = FP_BIAS + 32;  }
	else {	leni = WORD;
		i = FP_BIAS + 16;  }
	C = 0;
	if (exp <= FP_BIAS) dst = 0;
	else if (exp > i) {
		dst = 0;
		C = 1;  }
	else {	F_RSH (fac, FP_V_HB + 1 + i - exp, fsrc);
		dst = fsrc.l;
		if (fsrc.l >= i_limit[leni == LONG][sign]) {
			dst = 0;
			C = 1;  }
		else if (sign) dst = (-dst);  }
	N = GET_SIGN_L (dst);
	Z = (dst == 0);
	V = 0;
	if (C) fpnotrap (FEC_ICVT);
	FPS = (FPS & ~FPS_CC) | (N << PSW_V_N) |
		(Z << PSW_V_N) | (C << PSW_V_C);
	if (dstspec <= 07) R[dstspec] = (dst >> 16) & 0177777;
	else WriteI (dst, GeteaFP (dstspec, leni), dstspec, leni);
	break;

/* Calculation instructions */

case 2:							/* MULf */
	ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
	F_LOAD (qdouble, FR[ac], fac);
	newV = mulfp11 (&fac, &fsrc);
	F_STORE (qdouble, fac, FR[ac]);
	FPS = setfcc (FPS, fac.h, newV);
	break;
case 3:							/* MODf */
	ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
	F_LOAD (qdouble, FR[ac], fac);
	newV = modfp11 (&fac, &fsrc, &modfrac);
	F_STORE (qdouble, fac, FR[ac | 1]);
	F_STORE (qdouble, modfrac, FR[ac]);
	FPS = setfcc (FPS, fac.h, newV);
	break;
case 4:							/* ADDf */
	ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
	F_LOAD (qdouble, FR[ac], fac);
	newV = addfp11 (&fac, &fsrc);
	F_STORE (qdouble, fac, FR[ac]);
	FPS = setfcc (FPS, fac.h, newV);
	break;
case 6:							/* SUBf */
	ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
	F_LOAD (qdouble, FR[ac], fac);
	if (GET_EXP (fsrc.h) != 0) fsrc.h = fsrc.h ^ FP_SIGN;
	newV = addfp11 (&fac, &fsrc);
	F_STORE (qdouble, fac, FR[ac]);
	FPS = setfcc (FPS, fac.h, newV);
	break;
case 011:						/* DIVf */
	ReadFP (&fsrc, GeteaFP (dstspec, lenf), dstspec, lenf);
	F_LOAD (qdouble, FR[ac], fac);
	newV = divfp11 (&fac, &fsrc);
	F_STORE (qdouble, fac, FR[ac]);
	FPS = setfcc (FPS, fac.h, newV);
	break;  }					/* end switch fop */
return;
}							/* end fp11 */

/* Effective address calculation for fp operands

   Inputs:
	spec	=	specifier
	len	=	length
   Outputs:
	VA	=	virtual address

   Warnings:
	- Do not call this routine for integer mode 0 operands
	- Do not call this routine more than once per instruction
*/

int GeteaFP (int spec, int len)
{
int adr, reg, ds;
extern int cm, isenable, dsenable, MMR0, MMR1;

reg = spec & 07;					/* reg number */
ds = (reg == 7)? isenable: dsenable;			/* dspace if not PC */
switch (spec >> 3) {					/* case on spec */
case 0:							/* floating AC */
	if (reg >= 06) { fpnotrap (FEC_OP); ABORT (TRAP_INT);  }
	return 0;
case 1:							/* (R) */
	return (R[reg] | ds);
case 2:							/* (R)+ */
	if (reg == 7) len = 2;
	R[reg] = ((adr = R[reg]) + len) & 0177777;
	if (update_MM) MMR1 = (len << 3) | reg;
	return (adr | ds);
case 3:							/* @(R)+ */
	R[reg] = ((adr = R[reg]) + 2) & 0177777;
	if (update_MM) MMR1 = 020 | reg;
	adr = ReadW (adr | ds);
	return (adr | dsenable);
case 4:							/* -(R) */
	adr = R[reg] = (R[reg] - len) & 0177777;
	if (update_MM) MMR1 = (((-len) & 037) << 3) | reg;
	if ((adr < STKLIM) && (reg == 6) && (cm == KERNEL)) {
		setTRAP (TRAP_YEL);
		setCPUERR (CPUE_YEL);  }
	return (adr | ds);
case 5:							/* @-(R) */
	adr = R[reg] = (R[reg] - 2) & 0177777;
	if (update_MM) MMR1 = 0360 | reg;
	if ((adr < STKLIM) && (reg == 6) && (cm == KERNEL)) {
		setTRAP (TRAP_YEL);
		setCPUERR (CPUE_YEL);  }
	adr = ReadW (adr | ds);
	return (adr | dsenable);
case 6:							/* d(r) */
	adr = ReadW (PC | isenable);
	PC = (PC + 2) & 0177777;
	return (((R[reg] + adr) & 0177777) | dsenable);
case 7:							/* @d(R) */
	adr = ReadW (PC | isenable);
	PC = (PC + 2) & 0177777;
	adr = ReadW (((R[reg] + adr) & 0177777) | dsenable);
	return (adr | dsenable);  }			/* end switch */
}							/* end GeteaFP */

/* Read integer operand

   Inputs:
	VA	=	virtual address, VA<18:16> = mode, I/D space
	spec	=	specifier
	len	=	length (2/4 bytes)
   Outputs:
	data	=	data read from memory or I/O space
*/

unsigned int ReadI (int VA, int spec, int len)
{
if ((len == WORD) || (spec == 027)) return (ReadW (VA) << 16);
return ((ReadW (VA) << 16) | ReadW ((VA & ~0177777) | ((VA + 2) & 0177777)));
}

/* Read floating operand

   Inputs:
	fptr	=	pointer to output
	VA	=	virtual address, VA<18:16> = mode, I/D space
	spec	=	specifier
	len	=	length (4/8 bytes)
*/

void ReadFP (fpac_t *fptr, int VA, int spec, int len)
{
int exta;

if (spec <= 07) {
	F_LOAD_P (len == QUAD, FR[spec], fptr);
	return;  }
if (spec == 027) {
	fptr -> h = (ReadW (VA) << FP_V_F0);
	fptr -> l = 0;  }
else {	exta = VA & ~0177777;
	fptr -> h = (ReadW (VA) << FP_V_F0) |
		(ReadW (exta | ((VA + 2) & 0177777)) << FP_V_F1);
	if (len == QUAD) fptr -> l = 
		(ReadW (exta | ((VA + 4) & 0177777)) << FP_V_F2) |
		(ReadW (exta | ((VA + 6) & 0177777)) << FP_V_F3);
	else fptr -> l = 0;  }
if ((GET_SIGN (fptr -> h) != 0) && (GET_EXP (fptr -> h) == 0) &&
	(fpnotrap (FEC_UNDFV) == 0)) ABORT (TRAP_INT);
return;
}

/* Write integer result

   Inputs:
	data	=	data to be written
	VA	=	virtual address, VA<18:16> = mode, I/D space
	spec	=	specifier
	len	=	length
   Outputs: none
*/

void WriteI (int data, int VA, int spec, int len)
{
WriteW ((data >> 16) & 0177777, VA);
if ((len == WORD) || (spec == 027)) return;
WriteW (data & 0177777, (VA & ~0177777) | ((VA + 2) & 0177777));
return;
}

/* Write floating result

   Inputs:
	fptr	=	pointer to data to be written
	VA	=	virtual address, VA<18:16> = mode, I/D space
	spec	=	specifier
	len	=	length
   Outputs: none
*/

void WriteFP (fpac_t *fptr, int VA, int spec, int len)
{
int exta;

if (spec <= 07) {
	F_STORE_P (len == QUAD, fptr, FR[spec]);
	return;  }
WriteW ((fptr -> h >> FP_V_F0) & 0177777, VA);
if (spec == 027) return;
exta = VA & ~0177777;
WriteW ((fptr -> h >> FP_V_F1) & 0177777, exta | ((VA + 2) & 0177777));
if (len == LONG) return;
WriteW ((fptr -> l >> FP_V_F2) & 0177777, exta | ((VA + 4) & 0177777));
WriteW ((fptr -> l >> FP_V_F3) & 0177777, exta | ((VA + 6) & 0177777));
return;
}

/* Floating point add

   Inputs:
	facp	=	pointer to src1 (output)
	fsrcp	=	pointer to src2
   Outputs:
	ovflo	=	overflow variable
*/

int addfp11 (fpac_t *facp, fpac_t *fsrcp)
{
int facexp, fsrcexp, ediff;
fpac_t facfrac, fsrcfrac;

if (F_LT_AP (facp, fsrcp)) {				/* if !fac! < !fsrc! */
	facfrac = *facp;
	*facp = *fsrcp;					/* swap operands */
	*fsrcp = facfrac;  }
facexp = GET_EXP (facp -> h);				/* get exponents */
fsrcexp = GET_EXP (fsrcp -> h);
if (facexp == 0) {					/* fac = 0? */
	*facp = fsrcexp? *fsrcp: zero_fac;		/* result fsrc or 0 */
	return 0;  }
if (fsrcexp == 0) return 0;				/* fsrc = 0? no op */
ediff = facexp - fsrcexp;				/* exponent diff */
if (ediff >= 60) return 0;				/* too big? no op */
F_GET_FRAC_P (facp, facfrac);				/* get fractions */
F_GET_FRAC_P (fsrcp, fsrcfrac);
F_LSH_GUARD (facfrac);					/* guard fractions */
F_LSH_GUARD (fsrcfrac);
if (GET_SIGN (facp -> h) != GET_SIGN (fsrcp -> h)) {	/* signs different? */
	if (ediff) { F_RSH (fsrcfrac, ediff, fsrcfrac);  } /* sub, shift fsrc */
	F_SUB (fsrcfrac, facfrac, facfrac);		/* sub fsrc from fac */
	if ((facfrac.h | facfrac.l) == 0)  {     	/* result zero? */
		*facp = zero_fac;			/* no overflow */
		return 0;  }
	if (ediff <= 1) {				/* big normalize? */
		if ((facfrac.h & (0x00FFFFFF << FP_GUARD)) == 0) {
			F_LSH (facfrac, 24, facfrac);
			facexp = facexp - 24;  }
		if ((facfrac.h & (0x00FFF000 << FP_GUARD)) == 0) {
			F_LSH (facfrac, 12, facfrac);
			facexp = facexp - 12;  }
		if ((facfrac.h & (0x00FC0000 << FP_GUARD)) == 0) {
			F_LSH (facfrac, 6, facfrac);
			facexp = facexp - 6;  }  }
	while (GET_BIT (facfrac.h, FP_V_HB + FP_GUARD) == 0) {
		F_LSH_1 (facfrac);
		facexp = facexp - 1;  }  }
else {	if (ediff) { F_RSH (fsrcfrac, ediff, fsrcfrac);  } /* add, shift fsrc */
	F_ADD (fsrcfrac, facfrac, facfrac);		/* add fsrc to fac */
	if (GET_BIT (facfrac.h, FP_V_HB + FP_GUARD + 1)) {
		F_RSH_1 (facfrac);			/* carry out, shift */
		facexp = facexp + 1;  }  }
return round_and_pack (facp, facexp, &facfrac);
}							/* end addfp11 */

/* Floating point multiply

   Inputs:
	facp	=	pointer to src1 (output)
	fsrcp	=	pointer to src2
   Outputs:
	ovflo	=	overflow indicator
*/

int mulfp11 (fpac_t *facp, fpac_t *fsrcp)
{
int facexp, fsrcexp;
fpac_t facfrac, fsrcfrac;

facexp = GET_EXP (facp -> h);				/* get exponents */
fsrcexp = GET_EXP (fsrcp -> h);
if ((facexp == 0) || (fsrcexp == 0)) {			/* test for zero */
	*facp = zero_fac;
	return 0;  }
F_GET_FRAC_P (facp, facfrac);				/* get fractions */
F_GET_FRAC_P (fsrcp, fsrcfrac);
facexp = facexp + fsrcexp - FP_BIAS;			/* calculate exp */
facp -> h = facp -> h  ^ fsrcp -> h;			/* calculate sign */
frac_mulfp11 (&facfrac, &fsrcfrac);			/* multiply fracs */

/* Multiplying two numbers in the range [.5,1) produces a result in the
   range [.25,1).  Therefore, at most one bit of normalization is required
   to bring the result back to the range [.5,1).
*/

if (GET_BIT (facfrac.h, FP_V_HB + FP_GUARD) == 0) {
	F_LSH_1 (facfrac);
	facexp = facexp - 1;  }
return round_and_pack (facp, facexp, &facfrac);
}							/* end mulfp11 */

/* Floating point mod

   Inputs:
	facp	=	pointer to src1 (integer result)
	fsrcp	=	pointer to src2
	fracp	=	pointer to fractional result
   Outputs:
	ovflo	=	overflow indicator

   See notes on multiply for initial operation
*/

int modfp11 (fpac_t *facp, fpac_t *fsrcp, fpac_t *fracp)
{
int facexp, fsrcexp;
fpac_t facfrac, fsrcfrac, fmask;

facexp = GET_EXP (facp -> h);				/* get exponents */
fsrcexp = GET_EXP (fsrcp -> h);
if ((facexp == 0) || (fsrcexp == 0)) {			/* test for zero */
	*fracp = zero_fac;
	*facp = zero_fac;
	return 0;  }
F_GET_FRAC_P (facp, facfrac);				/* get fractions */
F_GET_FRAC_P (fsrcp, fsrcfrac);
facexp = facexp + fsrcexp - FP_BIAS;			/* calculate exp */
fracp -> h = facp -> h = facp -> h ^ fsrcp -> h;	/* calculate sign */
frac_mulfp11 (&facfrac, &fsrcfrac);			/* multiply fracs */

/* Multiplying two numbers in the range [.5,1) produces a result in the
   range [.25,1).  Therefore, at most one bit of normalization is required
   to bring the result back to the range [.5,1).
*/

if (GET_BIT (facfrac.h, FP_V_HB + FP_GUARD) == 0) {
	F_LSH_1 (facfrac);
	facexp = facexp - 1;  }

/* There are three major cases of MODf:

   1. Exp <= FP_BIAS (all fraction).  Return 0 as integer, product as
      fraction.  Underflow can occur.
   2. Exp > FP_BIAS + #fraction bits (all integer).  Return product as
      integer, 0 as fraction.  Overflow can occur.
   3. FP_BIAS < exp <= FP_BIAS + #fraction bits.  Separate integer and
      fraction and return both.  Neither overflow nor underflow can occur.
*/

	if (facexp <= FP_BIAS) {			/* case 1 */
		*facp = zero_fac;
		return round_and_pack (fracp, facexp, &facfrac);  }
	if (facexp > ((FPS & FPS_D)? FP_BIAS + 56: FP_BIAS + 24)) {
		*fracp = zero_fac;			/* case 2 */
		return round_and_pack (facp, facexp, &facfrac);  }
	F_RSH (fmask_fac, facexp - FP_BIAS, fmask);	/* shift mask */
	fsrcfrac.l = facfrac.l & fmask.l;		/* extract fraction */
	fsrcfrac.h = facfrac.h & fmask.h;
	if ((fsrcfrac.h | fsrcfrac.l) == 0) *fracp = zero_fac;
	else {	F_LSH (fsrcfrac, facexp - FP_BIAS, fsrcfrac);
		fsrcexp = FP_BIAS;
		if ((fsrcfrac.h & (0x00FFFFFF << FP_GUARD)) == 0) {
			F_LSH (fsrcfrac, 24, fsrcfrac);
			fsrcexp = fsrcexp - 24;  }
		if ((fsrcfrac.h & (0x00FFF000 << FP_GUARD)) == 0) {
			F_LSH (fsrcfrac, 12, fsrcfrac);
			fsrcexp = fsrcexp - 12;  }
		if ((fsrcfrac.h & (0x00FC0000 << FP_GUARD)) == 0) {
			F_LSH (fsrcfrac, 6, fsrcfrac);
			fsrcexp = fsrcexp - 6;  }
		while (GET_BIT (fsrcfrac.h, FP_V_HB + FP_GUARD) == 0) {
			F_LSH_1 (fsrcfrac);
			fsrcexp = fsrcexp - 1;  }
		round_and_pack (fracp, fsrcexp, &fsrcfrac);  }
	facfrac.l = facfrac.l & ~fmask.l;
	facfrac.h = facfrac.h & ~fmask.h;
	return round_and_pack (facp, facexp, &facfrac);
}

/* Fraction multiply

   Inputs:
	f1p	=	pointer to multiplier (output)
	f2p	=	pointer to multiplicand fraction

   Note: the inputs are unguarded; the output is guarded.

   This routine performs a classic shift-and-add multiply.  The low
   order bit of the multiplier is tested; if 1, the multiplicand is
   added into the high part of the double precision result.  The
   result and the multiplier are both shifted right 1.

   For the 24b x 24b case, this routine develops 48b of result.
   For the 56b x 56b case, this routine only develops the top 64b
   of the the result.  Because the inputs are normalized fractions,
   the interesting part of the result is the high 56+guard bits.
   Everything shifted off to the right, beyond 64b, plays no part
   in rounding or the result.

   There are many possible optimizations in this routine: scanning
   for groups of zeroes, particularly in the 56b x 56b case; using
   "extended multiply" capability if available in the hardware.
*/

void frac_mulfp11 (fpac_t *f1p, fpac_t *f2p)
{
fpac_t result, mpy, mpc;
int i;

result = zero_fac;					/* clear result */
mpy = *f1p;						/* get operands */
mpc = *f2p;
F_LSH_GUARD (mpc);					/* guard multipicand */
if ((mpy.l | mpc.l) == 0) {				/* 24b x 24b? */
	for (i = 0; i < 24; i++) {
		if (mpy.h & 1) result.h = result.h + mpc.h;
		F_RSH_1 (result);
		mpy.h = mpy.h >> 1;  }  }
else {	if (mpy.l != 0) {				/* 24b x 56b? */
		for (i = 0; i < 32; i++) {
			if (mpy.l & 1) { F_ADD (mpc, result, result);  }
			F_RSH_1 (result);
			mpy.l = mpy.l >> 1;  }  }
	for (i = 0; i < 24; i++) {
		if (mpy.h & 1) { F_ADD (mpc, result, result);  }
		F_RSH_1 (result);
		mpy.h = mpy.h >> 1;  }  }
*f1p = result;
return;
}

/* Floating point divide

   Inputs:
	facp	=	pointer to dividend (output)
	fsrcp	=	pointer to divisor
   Outputs:
	ovflo	=	overflow indicator
*/

int divfp11 (fpac_t *facp, fpac_t *fsrcp)
{
int facexp, fsrcexp, i, count, qd;
fpac_t facfrac, fsrcfrac, quo;

fsrcexp = GET_EXP (fsrcp -> h);				/* get divisor exp */
if (fsrcexp == 0) {					/* divide by zero? */
	fpnotrap (FEC_DZRO);
	ABORT (TRAP_INT);  }
facexp = GET_EXP (facp -> h);				/* get dividend exp */
if (facexp == 0) {					/* test for zero */
	*facp = zero_fac;				/* result zero */
	return 0;  }
F_GET_FRAC_P (facp, facfrac);				/* get fractions */
F_GET_FRAC_P (fsrcp, fsrcfrac);
F_LSH_GUARD (facfrac);					/* guard fractions */
F_LSH_GUARD (fsrcfrac);
facexp = facexp - fsrcexp + FP_BIAS + 1;		/* calculate exp */
facp -> h = facp -> h ^ fsrcp -> h;			/* calculate sign */
qd = FPS & FPS_D;
count = FP_V_HB + FP_GUARD + (qd? 33: 1);		/* count = 56b/24b */

quo = zero_fac;
for (i = count; (i > 0) && ((facfrac.h | facfrac.l) != 0); i--) {
	F_LSH_1 (quo);					/* shift quotient */
	if (!F_LT (facfrac, fsrcfrac)) {		/* divd >= divr? */
		F_SUB (fsrcfrac, facfrac, facfrac);	/* divd - divr */
		if (qd) quo.l = quo.l | 1;		/* double or single? */
		else quo.h = quo.h | 1;  }
	F_LSH_1 (facfrac);  }				/* shift divd */
if (i > 0) { F_LSH (quo, i, quo);  }			/* early exit? */

/* Dividing two numbers in the range [.5,1) produces a result in the
   range [.5,2).  Therefore, at most one bit of normalization is required
   to bring the result back to the range [.5,1).  The choice of counts
   and quotient bit positions makes this work correctly.
*/

if (GET_BIT (quo.h, FP_V_HB + FP_GUARD) == 0) {
	F_LSH_1 (quo);
	facexp = facexp - 1;  }
return round_and_pack (facp, facexp, &quo);
}							/* end divfp11 */

/* Update floating condition codes
   Note that FC is only set by STCfi via the integer condition codes

   Inputs:
	oldst	=	current status
	result	=	high result
	newV	=	new V
   Outputs:
	newst	=	new status
*/

int setfcc (int oldst, int result, int newV)
{
oldst = (oldst & ~FPS_CC) | newV;
if (GET_SIGN (result)) oldst = oldst | FPS_N;
if (GET_EXP (result) == 0) oldst = oldst | FPS_Z;
return oldst;
}

/* Round (in place) floating point number to f_floating

   Inputs:
	fptr	=	pointer to floating number
   Outputs:
	ovflow	=	overflow
*/

int roundfp11 (fpac_t *fptr)
{
fpac_t outf;

outf = *fptr;						/* get argument */
F_ADD (fround_fac, outf, outf);				/* round */
if (GET_SIGN (outf.h ^ fptr -> h)) {			/* flipped sign? */
	outf.h = (outf.h ^ FP_SIGN) & 0xFFFFFFFF;	/* restore sign */
	if (fpnotrap (FEC_OVFLO)) *fptr = zero_fac; 	/* if no int, clear */
	else *fptr = outf;				/* return rounded */
	return FPS_V;  }				/* overflow */
else {	*fptr = outf;					/* round was ok */
	return 0;  }					/* no overflow */
}

/* Round result of calculation, test overflow, pack

   Input:
	facp	=	pointer to result, sign in place
	exp	=	result exponent, right justified
	fracp	=	pointer to result fraction, right justified with
			guard bits
   Outputs:
	ovflo	=	overflow indicator
*/

int round_and_pack (fpac_t *facp, int exp, fpac_t *fracp)
{
fpac_t frac;

frac = *fracp;						/* get fraction */
if ((FPS & FPS_T) == 0) { 
	if (FPS & FPS_D) { F_ADD (dround_guard_fac, frac, frac);  }
	else { F_ADD (fround_guard_fac, frac, frac);  }
	if (GET_BIT (frac.h, FP_V_HB + FP_GUARD + 1)) {
		F_RSH_1 (frac);
		exp = exp + 1;  }  }
F_RSH_GUARD (frac);
facp -> l = frac.l & FP_FRACL;
facp -> h = (facp -> h & FP_SIGN) | (exp << FP_V_EXP) | (frac.h & FP_FRACH);
if (exp > 0377) {
	if (fpnotrap (FEC_OVFLO)) *facp = zero_fac;
	return FPS_V;  }
if ((exp <= 0) && (fpnotrap (FEC_UNFLO))) *facp = zero_fac;
return 0;
}

/* Process floating point exception

   Inputs:
	code	=	exception code
   Outputs:
	int	=	FALSE if interrupt enabled, TRUE if disabled
*/

int fpnotrap (int code)
{
static int test_code[] = { 0, 0, 0, FPS_IC, FPS_IV, FPS_IU, FPS_IUV };

if ((code >= FEC_ICVT) && (code <= FEC_UNDFV) &&
    ((FPS & test_code[code >> 1]) == 0)) return TRUE;
FPS = FPS | FPS_ER;
FEC = code;
FEA = (backup_PC - 2) & 0177777;
if ((FPS & FPS_ID) == 0) setTRAP (TRAP_FPE);
return FALSE;
}
