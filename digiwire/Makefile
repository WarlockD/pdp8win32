#
# $RCSfile: Makefile,v $		$Revision: 1.21 $
#
# $Date: 1998/12/20 06:22:44 $		$Locker:  $
#
# --------------------------------------------------------------------
# DigiTcl 0.3.0 - An Elementary Digital Simulator 
# (C) 1995-1998 Donald C. Craig (donald@cs.mun.ca)
# 
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# --------------------------------------------------------------------
#
# Makefile for the simulator engine
#
EXEC=digisim
CFLAGS=-Wall -g
CC=g++

SRCS=comp.cpp complib.cpp connect.cpp main.cpp parser.cpp port.cpp \
	rtcomp.cpp signal.cpp wire.cpp

OBJS=comp.o complib.o connect.o main.o parser.o port.o \
	rtcomp.o signal.o wire.o

.SUFFIXES: .cpp

.cpp.o:	
	$(CC) -c $(CFLAGS) $<

$(EXEC) : $(OBJS)
	$(CC) -o $(EXEC) $(OBJS)

depend:
	rm -rf .depend
	sed -n "1,/^# DO NOT DELETE THIS LINE/p" Makefile > .depend
	$(CC) -MM $(CFLAGS) $(SRCS) >> .depend
	mv Makefile Makefile.bak
	mv .depend Makefile

clean :
	rm -f $(EXEC) $(OBJS) .depend Makefile.bak

# DO NOT DELETE THIS LINE
comp.o:		comp.cpp sim.h port.h connect.h signal.h comp.h

complib.o:	complib.cpp complib.h sim.h comp.h port.h connect.h \
		signal.h wire.h

connect.o:	connect.cpp sim.h comp.h connect.h signal.h

main.o:		main.cpp sim.h rtcomp.h comp.h parser.h signal.h wire.h \
		connect.h complib.h port.h

parser.o:	parser.cpp wire.h sim.h connect.h signal.h rtcomp.h comp.h \
		parser.h

port.o:		port.cpp sim.h port.h connect.h signal.h comp.h

rtcomp.o:	rtcomp.cpp port.h sim.h connect.h signal.h rtcomp.h comp.h \
		complib.h wire.h

signal.o:	signal.cpp signal.h sim.h

wire.o:		wire.cpp sim.h wire.h connect.h signal.h
