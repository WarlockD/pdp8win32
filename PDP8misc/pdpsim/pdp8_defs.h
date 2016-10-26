/* pdp8_defs.h: PDP-8 simulator definitions

   Copyright (c) 1993, Robert M Supnik, Digital Equipment Corporation
   Commercial use prohibited

   02-May-94	RMS	Added non-existent memory handling.
*/

#include "sim_defs.h"					/* simulator defns */

/* Constants */

#define MEMSIZE		32768				/* 1-8 * 4096 */
#define MAXMEMSIZE	32768

#if MEMSIZE < MAXMEMSIZE				/* need mem check? */
#define MEM_ADDR_OK(x) (x < MEMSIZE)
#else							/* no... */
#define MEM_ADDR_OK(x) TRUE
#endif

#define EAE		0				/* defined if EAE */

/* Simulator stop codes */

#define STOP_RSRV	1				/* must be 1 */
#define STOP_HALT	2				/* HALT */
#define STOP_IBKPT	3				/* breakpoint */

/* IOT subroutine return codes */

#define IOT_V_SKP	12				/* skip */
#define IOT_V_REASON	13				/* reason */
#define IOT_SKP		(1 << IOT_V_SKP)
#define IOT_REASON	(1 << IOT_V_REASON)
#define IORETURN(f,v)	((f)? (v): SCPE_OK)		/* stop on error */

/* Interrupt flags

   The interrupt flags consist of three groups:

   1.	Devices with individual interrupt enables.  These record
	their interrupt requests in device_done and their enables
	in device_enable, and must occupy the low bit positions.

   2.	Devices without interrupt enables.  These record their
	interrupt requests directly in int_req, and must occupy
	the middle bit positions.

   3.	Overhead.  These exist only in int_req and must occupy the
	high bit positions.

   Because the PDP-8 does not have priority interrupts, the order
   of devices within groups does not matter.
*/

#define INT_V_START	0				/* enable start */
#define INT_V_LPT	(INT_V_START+0)			/* line printer */
#define INT_V_PTP	(INT_V_START+1)			/* tape punch */
#define INT_V_PTR	(INT_V_START+2)			/* tape reader */
#define INT_V_TTO	(INT_V_START+3)			/* terminal */
#define INT_V_TTI	(INT_V_START+4)			/* keyboard */
#define INT_V_CLK	(INT_V_START+5)			/* clock */
#define INT_V_DIRECT	(INT_V_START+6)			/* direct start */
#define INT_V_RX	(INT_V_DIRECT+0)		/* RX8E */
#define INT_V_RK	(INT_V_DIRECT+1)		/* RK8E */
#define INT_V_RF	(INT_V_DIRECT+2)		/* RF08 */
#define INT_V_PWR	(INT_V_DIRECT+3)		/* power int */
#define INT_V_UF	(INT_V_DIRECT+4)		/* user int */
#define INT_V_OVHD	(INT_V_DIRECT+5)		/* overhead start */
#define INT_V_NO_ION_PENDING (INT_V_OVHD+0)		/* ion pending */
#define INT_V_NO_CIF_PENDING (INT_V_OVHD+1)		/* cif pending */
#define INT_V_ION	(INT_V_OVHD+2)			/* interrupts on */

#define INT_LPT		(1 << INT_V_LPT)
#define INT_PTP		(1 << INT_V_PTP)
#define INT_PTR		(1 << INT_V_PTR)
#define INT_TTO		(1 << INT_V_TTO)
#define INT_TTI		(1 << INT_V_TTI)
#define INT_CLK		(1 << INT_V_CLK)
#define INT_RX		(1 << INT_V_RX)
#define INT_RK		(1 << INT_V_RK)
#define INT_RF		(1 << INT_V_RF)
#define INT_PWR		(1 << INT_V_PWR)
#define INT_UF		(1 << INT_V_UF)
#define INT_NO_ION_PENDING (1 << INT_V_NO_ION_PENDING)
#define INT_NO_CIF_PENDING (1 << INT_V_NO_CIF_PENDING)
#define INT_ION		(1 << INT_V_ION)
#define INT_DEV_ENABLE	((1 << INT_V_DIRECT) - 1)	/* devices w/enables */
#define INT_ALL		((1 << INT_V_OVHD) - 1)		/* all interrupts */
#define INT_INIT_ENABLE	(INT_TTI+INT_TTO+INT_PTR+INT_PTP+INT_LPT)
#define INT_PENDING	(INT_ION+INT_NO_CIF_PENDING+INT_NO_ION_PENDING)
#define INT_UPDATE	((int_req & ~INT_DEV_ENABLE) | (dev_done & dev_enable))
