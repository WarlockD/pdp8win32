/*****************************************************************************
PDP-8 Assembler Nuveau for ELE 375

  Written by Eirik Bakke on September 27, 2006
  See http://www.princeton.edu/~ebakke/pdp8 for TOOO list and documentation
  
******************************************************************************/

#include "pdpnasm.h"
#include "pdp8.h"

/* Globals ****************************************************************** */

/* Input/output files. */
static FILE *infile  = NULL;
static FILE *outfile = NULL;

/* Last line read. */
static char curline[BUF_SIZE];

/* Last character and token read, but not consumed, if any. For internal use by
   readChar and readToken only. */
static int savedchar = INVALID;
static token savedtok;

/* Current line number (1-based) and column (0-based) of the input. */
static int linenum = INVALID, colnum = INVALID;

/* Function Definitions ***************************************************** */

/* Read a line from the input to curline, stripping any newline at the end, and
   updating input position variables. Return FALSE if EOF is reached, otherwise
   TRUE. */
BOOL readLine(void) {
    char *newline;
    if (fgets(curline, sizeof(curline), infile) != NULL) {
        assert(linenum != INVALID);
        newline = strrchr(curline, '\n');
        if (newline != NULL)
            newline[0] = '\0';
        linenum++;
        colnum = 0;
        return TRUE;
    } else {
        linenum = INVALID;
        colnum  = INVALID;
        return FALSE;
    }
}

int readChar(void) {
    int ret = savedchar;

    savedchar = INVALID;
    if (ret == INVALID) {
        if (colnum == INVALID && !readLine())
            return INVALID;
        assert(colnum != INVALID);
        ret = toupper(curline[colnum++]);
        if (ret == 0 || ret == COMMENT_CHAR) {
            /* End of line or rest of it is comment. */
            ret = '\n';
            colnum = INVALID;
        }
        if (!isalnum(ret) && strchr(" +-\n\t,=*;$./", ret) == NULL) {
            linenum = linenum;
            colnum  = colnum;
            error("Illegal Character \'\\x%X\'", (unsigned char) ret);
        }
    }
    return ret;
}

void putbackChar(int c) {
    assert(((int) c) == ((char) c));
    assert(savedchar == INVALID); /* Can't putback twice. */
    savedchar = c;
}

void error(const char *format, ...) {
    va_list ap;
    int i;
    int errcol = colnum, errline = linenum;
    fprintf(stderr, "ERROR: ");
    va_start(ap, format);
    vfprintf(stderr, format, ap);
    va_end(ap);

    if (errline > 0) {
        fprintf(stderr, " at line %d:\n\n", linenum);

        /* Print context in which error occured. */
        rewindInput();
        while (readLine() && linenum <= errline + CONTEXT_LINES) {
            if (linenum >= errline - CONTEXT_LINES) {
                fprintf(stderr, (linenum == errline) ? "->" : "  ");
                fprintf(stderr,"| %s\n", curline);
            }
            if (linenum == errline && errcol != INVALID) {
                /* Print a caret under the current column. */
                for (i = 0; i < errcol + 4; i++)
                    fprintf(stderr, " ");
                fprintf(stderr, "^\n");
            }
        }
    } else {
        fprintf(stderr, ".\n");
    }
    shutdown(FALSE);
}

void shutdown(BOOL success) {
    if (infile != NULL)
        fclose(infile);
    if (outfile != NULL)
        fclose(outfile);
    exit(success ? EXIT_SUCCESS : EXIT_FAILURE);
}

void rewindInput(void) {
    rewind(infile);
    linenum       = 0;
    colnum        = INVALID;
    savedchar     = INVALID;
    savedtok.type = INVALID;
    symtab_set(".", INVALID, FALSE, TRUE); /* Reset PLC. */
}

void setWord(int adr, int val) {
   /* Object file output in psuedo-DEC bin format. An address is marked by all
      ones in bits 12-15. */
   static int objadr = INVALID; /* Current address in the object file. */
   static BOOL written[MEMSIZE]; /* Has this word been specified before? */
   assert(adr != INVALID);
   
   if (adr >= MEMSIZE)
       error("Memory address %o out of range", adr);
   if (written[adr])
       error("Word at address %o specified twice", adr);
   if ((val & WORDMASK) != val)
       error("Word %o out of range", val);

   written[adr] = TRUE;
   if (objadr == INVALID || adr != objadr)
       fprintf(outfile, "%o\n", MAKE_ADDRESS_RECORD(adr));
   fprintf(outfile, "%o\n", val);
   objadr = adr + 1;
}

int readString(char *buf, int n, matchFunct incharset) {
    int i = 0, c;
    
    while (TRUE) {
        c = readChar();
        if (c != INVALID && incharset(c) && i < (n - 1)) {
            buf[i++] = c;
        } else {
            if (c != INVALID)
                putbackChar(c);
            buf[i] = '\0';
            assert(strlen(buf) <= n);
            return (c == INVALID && i == 0) ? INVALID : i;
        }
    }
}

int issingletoken(int c) { return (strchr("+-\n,=*;$.", c) != NULL); }
int isspacetab(int c)    { return (strchr(" \t"       , c) != NULL); }

void readToken(token *tok) {
    char buf[BUF_SIZE];
    int read = 0;

    if (savedtok.type != INVALID) {
        memcpy(tok, &savedtok, sizeof(savedtok));
        savedtok.type = INVALID;
        return;
    }

    tok->type      = INVALID;
    tok->strval[0] = '\0';
    
    /* Skip leading space/tabs. */
    readString(buf, sizeof(buf), isspacetab);
    linenum = linenum;
    colnum  = colnum;
    
    if ((read = readString(buf, 2, issingletoken)) > 0) {
        /* Single-character token. */
        switch (buf[0]) {
            case       '\n':
                tok->type = ';';
            break; case '$':
                tok->type = '$';
                /* Skip remaining whitespace. */
                readString(buf, sizeof(buf), isspace);
                if (readChar() != INVALID)
                    error("Junk after pass termination");
            break; case '.':
                tok->type = 'd'; /* The '.' is treated as a special symbol. */
                strncpy(tok->strval, ".", sizeof(tok->strval));
            break; default:
                tok->type = buf[0];
        }
    } else if ((read = readString(buf, sizeof(buf), isalnum)) > 0) {
        if (strcmp("I", buf) == 0) {
            /* Indirect address pseudo-instruction. */
            tok->type = 'I';
        } else if (strcmp("FIXMRI", buf) == 0) {
            /* Fixed Memory Reference pseudo-instruction. */
            tok->type = 'M';
        } else {
            /* Identifiers, including integer literals. */
            tok->type = 'd';
            strncpy(tok->strval, buf, sizeof(tok->strval));
        }
    } else if (read == INVALID) { /* Earlier call yielded EOF. */
        /* Nothing to do; tok->type will remain INVALID. */
    } else {
        assert(0);
    }
}

void putbackToken(token *tok) {
    assert(savedtok.type == INVALID);
    memcpy(&savedtok, tok, sizeof(savedtok));
}

void printToken(token *tok) {
    FILE *o = stderr;
    if (tok->type == INVALID) {
        fprintf(o, "<INV>");
    } else {
        if (tok->type == ';')
            fprintf(o, ";\n");
        else
            fprintf(o, "%c", tok->type);
        if (strlen(tok->strval) > 0)
            fprintf(o, "\"%s\"", tok->strval);
        if (tok->type != ';')
            fprintf(o, " ");
    }
}

int readExpression(BOOL *noword, BOOL addsyms, BOOL musteval) {
    BOOL empty = TRUE, ended = FALSE, fixmri = FALSE;
    int curval = 0;
    token curtok;
    
    while (!ended) {
        readToken(&curtok);

        if (curtok.type == '+' || curtok.type == '-') {
            int tmp = readExpression(NULL, addsyms, musteval);
            if (musteval) {
                if (tmp == INVALID)
                    error("Illegal expression following \'%c\'", curtok.type);
                switch (curtok.type) {
                    case        '+':
                        curval = (curval + tmp) & WORDMASK;
                    break; case '-':
                        curval = (curval - tmp) & WORDMASK;
                    break; default:
                        assert(0);
                }
            }
            empty = FALSE;
        } else if (curtok.type == 'M') {
            fixmri = TRUE;
        } else if (empty && curtok.type == '*') {
            int tmp = readExpression(NULL, addsyms, TRUE);
            if (tmp == INVALID)
                error("Invalid target address");
            symtab_set(".", tmp, FALSE, TRUE);
            ended = TRUE;
        } else if (curtok.type == 'd') {
            /* Save identifier and read next token to see if the symbol is
               to be defined or evaluated. */
            char *ident;
            ident =  (char *) malloc(strlen(curtok.strval) + 1);
            assert(ident != NULL);
            strcpy(ident, curtok.strval);
            readToken(&curtok);

            if        (empty && curtok.type == ',') {
                /* Label-style symbol definition. */
                if (addsyms)
                    symtab_set(ident, symtab_get(".", NULL), FALSE, FALSE);
                putbackToken(&curtok);
                ended = TRUE;
            } else if (empty && curtok.type == '=') {
                /* EQU-style definition. */
                int tmp;
                tmp = readExpression(NULL, addsyms, TRUE);
                if (tmp == INVALID)
                    error("Invalid symbol definition value");
                if (addsyms)
                    symtab_set(ident, tmp, fixmri, FALSE);
                ended = TRUE;
            } else {
                /* Evaluation. */
                int eval;
                BOOL ismri = FALSE;
                /* Put back next token since we could not process it for now. */
                putbackToken(&curtok);
                
                eval = symtab_get(ident, &ismri);
                if (eval == INVALID) {
                    if (musteval)
                        error("Undefined symbol \"%s\"", ident);

                } else {
                    if (ismri) {
                        /* Memory reference instruction */
                        int tmp;
                        curval = eval;
                        
                        if (!empty)
                            error("Unexpected memory reference instruction %s",
                                    ident);
                        readToken(&curtok);
                        if (curtok.type == 'I') {
                            curval |= EMBED_INDIRECT(1);
                        } else {
                            putbackToken(&curtok);
                        }
                        tmp = readExpression(NULL, addsyms, musteval);
                        if (musteval) {
                            int page = tmp >> 7;
                
                            if (page != 0) {
                                if (page == (symtab_get(".", NULL) >> 7)) {
                                    /* Current page; use relative addressing */
                                    curval |= EMBED_PAGE(1);
                                } else {
                                    error("Invalid reference");
                                }
                            }
                            /* Embed the address, relative or absolute. */
                            curval |= EMBED_REL_ADRS(tmp);
                        }
                    } else {
                        curval |= eval;
                    }
                }
                empty = FALSE;
            }
            free(ident);
            ident = NULL;
        } else {
            /* EOF or bad input syntax. */
            putbackToken(&curtok);
            ended = TRUE;
        }
    }

    if (noword != NULL)
        *noword = empty;
    
    return (!empty || !musteval) ? curval : INVALID;
}

void parse(int passno) {
    token lasttok;
    int curval;
    BOOL terminated = FALSE, noword;
    assert(passno == 1 || passno == 2);
    
    rewindInput();
    
    while (!terminated) {
        curval = readExpression(&noword, passno == 1, passno == 2);
        readToken(&lasttok);
        
        switch (lasttok.type) {
            break; case '$':
               terminated = TRUE;
            break; case ',':
                /* No action (label added in readExpression). */
            break; case ';':
                if (passno == 2 && curval != INVALID)
                    setWord(symtab_get(".", NULL), curval);
                if (!noword)
                    /* Increase PLC. */
                    symtab_set(".", symtab_get(".", NULL) + 1, FALSE, TRUE);
            break; case INVALID:
                error("Missing source termination (use '$' at end)");
            break; default:
                error("Unexpected token of type \'%c\'", lasttok.type);
        } 
    }
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        fprintf(stderr, "PDP-8 Assembler Nuveau\n\n");
        error("Usage: \"%s asm-file.p8 obj-file.po\"", argv[0]);
    }
    if ((outfile = fopen(argv[2], "w")) == NULL)
        error("Couldn't open output file \"%s\"", argv[2]);

    if ((infile  = fopen(FIXTAB_FILE, "r")) == NULL)
        error("Couldn't open fixed symbol table file \"%s\"", FIXTAB_FILE);
    parse(1);

    fclose(infile);
    if ((infile  = fopen(argv[1],"r")) == NULL)
        error("Couldn't open input file \"%s\"", argv[1]);

    parse(1);
    parse(2);
    
    shutdown(TRUE);
    return 0; /* Avoid a compiler warning. */
}
