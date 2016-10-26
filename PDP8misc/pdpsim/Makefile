# Edit the following line to read vcc instead of cc if you are building
# on a vax running ultrix.
CC	= cc
CCFLAGS = -g0 -p0 -O2
LDFLAGS = -p0

PDP11OBJS = pdp11_cpu.o pdp11_fp.o pdp11_lp.o pdp11_rk.o pdp11_rl.o \
		pdp11_rx.o pdp11_stddev.o pdp11_sys.o scp.o scp_tty.o

PDP8OBJS = pdp8_clk.o pdp8_cpu.o pdp8_lp.o pdp8_pt.o pdp8_rf.o \
		pdp8_rk.o pdp8_rx.o pdp8_sys.o pdp8_tt.o scp.o scp_tty.o

all:	pdp8 pdp11

clean:
	rm -f a.out *.[os] core pdp8 pdp11

install: pdp8 pdp11
	mv pdp8 /usr/local
	chmod 755 /usr/local/pdp8
	mv pdp11 /usr/local
	chmod 755 /usr/local/pdp11

kit:
	@tar -cf pdp_simulators.tar 0readme.txt 0simdoc.txt Makefile *.com *.[ch]

pdp8:	$(PDP8OBJS)
	$(CC) $(LDFLAGS) -o pdp8 $(PDP8OBJS) -lm

pdp11:	$(PDP11OBJS)
	$(CC) $(LDFLAGS) -o pdp11 $(PDP11OBJS)

.c.s:
	$(CC) $(CCFLAGS) -S $*.c

.c.o:
	$(CC) $(CCFLAGS) -DTRUE=1 -DFALSE=0 -c $* $*.c

pdp11_cpu.o: ./pdp11_defs.h ./sim_defs.h pdp11_cpu.c

pdp11_fp.o: ./pdp11_defs.h ./sim_defs.h pdp11_fp.c

pdp11_lp.o: ./pdp11_defs.h ./sim_defs.h pdp11_lp.c

pdp11_rk.o: ./pdp11_defs.h ./sim_defs.h pdp11_rk.c

pdp11_rl.o: ./pdp11_defs.h ./sim_defs.h pdp11_rl.c

pdp11_rx.o: ./pdp11_defs.h ./sim_defs.h pdp11_rx.c

pdp11_stddev.o: ./pdp11_defs.h ./sim_defs.h pdp11_stddev.c

pdp11_sys.o: ./pdp11_defs.h ./sim_defs.h pdp11_sys.c

pdp8_clk.o: ./pdp8_defs.h ./sim_defs.h pdp8_clk.c

pdp8_cpu.o: ./pdp8_defs.h ./sim_defs.h pdp8_cpu.c

pdp8_lp.o: ./pdp8_defs.h ./sim_defs.h pdp8_lp.c

pdp8_pt.o: ./pdp8_defs.h ./sim_defs.h pdp8_pt.c

pdp8_rf.o: ./pdp8_defs.h ./sim_defs.h pdp8_rf.c

pdp8_rk.o: ./pdp8_defs.h ./sim_defs.h pdp8_rk.c

pdp8_rx.o: ./pdp8_defs.h ./sim_defs.h pdp8_rx.c

pdp8_sys.o: ./pdp8_defs.h ./sim_defs.h pdp8_sys.c

pdp8_tt.o: ./pdp8_defs.h ./sim_defs.h pdp8_tt.c

scp.o: ./sim_defs.h scp.c

scp_tty.o: scp_tty.c ./sim_defs.h
