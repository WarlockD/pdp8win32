CC=gcc
CCFLAGS=-g -Wall -ansi -pedantic
PDP8ASM_OBJS=pdpnasm.o symtab.o
ARC_FILES=fixtab.p8 pdp8.h pdpnasm.c symtab.c pdpnasm.h Makefile

all: pdpnasm pdpnasm.zip

pdpnasm: $(PDP8ASM_OBJS) 
	$(CC) $(CCFLAGS) $(PDP8ASM_OBJS) -o pdpnasm

clean:
	rm -f pdpnasm pdpnasm.exe pdpnasm.zip
	rm -f *.o *.po *~ *.stackdump

%.o : %.c
	$(CC) $(CCFLAGS) -c -o $@ $<

pdpnasm.zip: $(ARC_FILES)
	zip pdpnasm.zip $(ARC_FILES)
    