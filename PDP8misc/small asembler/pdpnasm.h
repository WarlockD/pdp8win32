#ifndef PDP8ASM_H
#define PDF8ASM_H

#include <limits.h>
#include <stddef.h>
#include <stdlib.h>
#include <assert.h>
#include <stdio.h>
#include <stdarg.h>
#include <ctype.h>
#include <string.h>
#include <malloc.h>

/* Constants and Type Declarations ****************************************** */
typedef enum { FALSE = 0, TRUE = 1 } BOOL;
enum {
    INVALID = INT_MIN,
    BUF_SIZE = 1024,
    MAX_SYMBOLS = 512,
    CONTEXT_LINES = 3  /* Number of lines to show on each side of an error */
};

#define FIXTAB_FILE "fixtab.p8"

typedef struct {
    /* The type of token. Identifiers have type 'd', and include symbol names,
       integer literals, and the '.' symbol. Other tokens are single-charactered
       and are denoted by that character. May be INVALID. */
    int  type;
    /* Identifier if type 'd', otherwise empty. */
    char strval[BUF_SIZE];
} token;

/* Character matching functions, such as isalnum() or isalpha(). */
typedef int (*matchFunct)(int);

/* Function Declarations **************************************************** */

/* Returns INVALID if symbol does not exist. Octal integer literals and the
   symbol "." are regarded as special built-in symbols. mri may be NULL. */
int symtab_get(char *name, BOOL *mri);

void symtab_set(char *name, int val, BOOL mri, BOOL redef);

/* Clean up and exit with return code according to success. */
void shutdown(BOOL success);

/* Rewind to the beginning of input file and update position variables. */
void rewindInput(void);

/* Print a printf-style error message and exit. The context around the current
   line and column, if any, will also be printed. */
void error(const char *format, ...)
        /* Enable gcc format string checking: */
        __attribute__((format(printf,1,2)));

/* Set a word in the target memory space by appending data to the output object
   file. */
void setWord(int adr, int val);

/* Read a character in the PAL-III subset of ASCII from the input. Alphabetic
   characters are automatically capitalized. Comments are stripped at this level
   so that character set restrictions can be relaxed in that case. Return
   INVALID if EOF is reached. */
int readChar(void);

void putbackChar(int c);

/* Read up to n-1 characters matched by the given function into buf, and add a
   null character at the end. Return the number of characters actually read (not
   including the terminator), or INVALID if EOF is reached before any character
   could be read. */
int readString(char *buf, int n, matchFunct incharset);

int issingletoken(int c);
int isspacetab(int c);

/* Read a token into the specified buffer. If EOF is reached, tok->type is set
   INVALID. */
void readToken(token *tok);

void putbackToken(token *tok);

/* For debugging purposes. */
void printToken(token *tok);

/* Parse a statement; if it is an expression, return its value, otherwise return
   INVALID. If noword is not NULL, set it to FALSE if the statement is an
   expression. If addsyms is TRUE, symbols may be defined during parsing. If
   musteval is TRUE, unresolved symbols will be treated as errors, otherwise
   it will just cause the return value to be INVALID. */
int readExpression(BOOL *noword, BOOL addsyms, BOOL musteval);

void parse(int passno);

#endif
