#ifndef PDP8_H
#define PDF8_H

/* Constants and macros related to instruction encoding on the PDP-8. 
     Modified 27 Sep 2006 by Eirik Bakke (based on Prof. Wolf's original header)
*/

#define COMMENT_CHAR '/' /* the comment initiator character */

#define BIN_ADRS_MASK (0xf000) /* ones in bits 12-15 */
#define MAKE_ADDRESS_RECORD(anum) ( anum | BIN_ADRS_MASK )
#define IS_ADDRESS_RECORD(anum) ( (anum & BIN_ADRS_MASK) == BIN_ADRS_MASK )
#define DECODE_ADDRESS_RECORD(anum) (anum & (~BIN_ADRS_MASK))

#define NBITS 12 /* number of bits in a PDP-8 word */
#define WORDMASK 0xfff /* masks an integer to PDP-8 size */
#define MEMSIZE 4096

/* Opcodes for encoded instructions. */
#define AND_INSTR 0x0
#define TAD_INSTR 0x1
#define ISZ_INSTR 0x2
#define DCA_INSTR 0x3
#define JMS_INSTR 0x4
#define JMP_INSTR 0x5
#define IOT_INSTR 0x6
#define MICRO_INSTR 0x7

#define GROUP1 0x0
#define GROUP2 0x1

/* group 1 microinstructions */
#define IAC_MICRO 0x1
#define ROTATE_2_MICRO 0x2
#define ROTATE_AC_LEFT_MICRO 0x4
#define ROTATE_AC_RIGHT_MICRO 0x8
#define CML_MICRO 0x10
#define CMA_MICRO 0x20
#define CLL_MICRO 0x40
#define CLA_MICRO 0x80

/* group 2 microinstructions */
#define HLT_MICRO 0x2
#define OSR_MICRO 0x4
#define REVERSE_SKIP_MICRO 0x8
#define SNL_MICRO 0x10
#define SZA_MICRO 0x20
#define SMA_MICRO 0x40
#define CLA_MICRO 0x80

/* instruction manipulation */
#define EMBED_OPCODE(opcode) (opcode << 9)
#define EXTRACT_OPCODE(instr) ((instr >> 9) & 0x7)
#define EMBED_INDIRECT(indirbit) (indirbit << 8)
#define EXTRACT_INDIRECT(instr) ((instr >> 8) & 0x1)
#define EMBED_PAGE(pagebit) (pagebit << 7)
#define EXTRACT_PAGE(instr) ((instr >> 7) & 0x1)
#define EMBED_REL_ADRS(adrs) (adrs & 0x7f)
#define EXTRACT_REL_ADRS(instr) (instr & 0x7f) /* gets relative address */
#define EMBED_GROUP(arg) (arg << 8)
#define EXTRACT_GROUP(instr) ((instr >> 8) & 0x1)
#define EMBED_DEVICE(dev) (dev << 3)
#define EXTRACT_DEVICE(instr) ((instr >> 3) & 0x3f)
#define EMBED_IOT4(iot) (iot << 2)
#define EXTRACT_IOT4(instr) ((instr >> 2) & 0x1)
#define EMBED_IOT2(iop) (iop << 1)
#define EXTRACT_IOT2(instr) ((instr >> 1) & 0x1)
#define EMBED_IOT1(iop) (iop)
#define EXTRACT_IOT1(instr) (instr & 0x1)

#endif /* !PDP8_H */
