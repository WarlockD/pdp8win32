/*
    This file is part of The Didactic PDP-8 Assembler
    Copyright (C) 2002 Toby Thain, toby@telegraphics.com.au

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by  
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License  
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

#include "asm.h"

int lasttok = 0,listpos = 0,words = 0,noloc = 0;
#define LIST_LINE 72
char listline[LIST_LINE+1];
FILE *listfile = NULL;

void dolisting(int act,int leng,char *text){
	int remain = LIST_LINE - listpos,
	    n = leng < remain ? leng : remain;
	//DPRINTF("--dolisting(%d,%d,\"%s\") remain=%d n=%d\n", act,leng,text,remain,n);

	/* lines longer than LIST_LINE will be truncated in the listing */

	if(n && act != 1){
		lasttok = listpos;
		strncat(listline+listpos,text,n);
		listpos += n;
	}
}
void newline(void){
	listline[listpos = 0] = 0;
}
/*
void listo(int a,int w){
	if(listpos){
		if(listfile) fprintf(listfile,listfmt,a,w,listline);
		newline();
	}else if(!noloc && listfile) 
		fprintf(listfile,listfmt_noloc,w,listline);
}
*/
void flushlist(void){
	if(listpos){
		listempty(listline);
		newline();
	}
}


