#include "pdpnasm.h"

/* Globals ****************************************************************** */

/* Symbol table. */
static int nosymbols = 0;
static char *symnames[MAX_SYMBOLS];
static int   symvals[MAX_SYMBOLS];
static BOOL  symmri[MAX_SYMBOLS];

/* Function Definitions ***************************************************** */

static int symtab_getIndex(char *name) {
    int i;
    for (i = 0; i < nosymbols && strcmp(symnames[i], name) != 0; i++)
        ;
    return i;    
}

int symtab_get(char *name, BOOL *mri) {
    int i, intlit;
    char *endptr;
    assert(name != NULL);
    assert(strlen(name) > 0);

    if (isdigit(name[0])) {
        /* Integer literal */
        intlit = strtol(name, &endptr, 8);
        if (endptr[0] != '\0')
            error("Malformed integer literal \"%s\"", name);
        if (intlit > INT_MAX || intlit < INT_MIN)
            error("Integer literal out of range");
        return (int) intlit;
    } else {
        i = symtab_getIndex(name);
        if (i == nosymbols)
            return INVALID;

        if (mri != NULL)
            *mri = symmri[i];
        return symvals[i];
    }
}

void symtab_set(char *name, int val, BOOL mri, BOOL redef) {
    char *namebuf = NULL;
    int i;
    assert(name != NULL);

    i = symtab_getIndex(name);    
    
    if (i < nosymbols && !redef)
        error("Redefinition of symbol \"%s\"", name);
    if (i == MAX_SYMBOLS)
        error("Internal symbol table too small (increase MAX_SYMBOLS)");
    namebuf = (char *) malloc(strlen(name) + 1);
    assert(namebuf != NULL);
    strcpy(namebuf, name);
    symnames[i] = namebuf; 
    symvals[i]  = val;
    symmri[i]   = mri;
    if (i == nosymbols)
        nosymbols++;
}

