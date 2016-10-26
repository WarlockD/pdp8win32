/* description: Parses end executes mathematical expressions. */

/* lexical grammar */
%{ 
var WORDMASK= 07777; /* words are 12 bits */
var OPCODE = 07000;
var OPCODE_IOT = 06000;
var OPCODE_OPR = 07000;
/* microcoded instructions */
var GROUP_BIT = 0400;
var REVERSE_SENSE = 010;
var ROTATES = 014; /* group 1 rotates */
var SKIPS = 0160; /* group 2 skips */
/* memory reference instructions */
var PAGE_ADDR = 0177;
var CURR_PAGE = 0200;
var INDIRECT_MODE = 0400;
/* IOT microinstructions */
var DEVICE_SELECT = 0770;
var USER_SYMBOL = -1; /* default type */

	/* symbol flags */
var F_NONE = 0;
var F_USER=01; /* symbol was user-created rather than built-in */
var F_ASSIGNED=02; /* symbol has been given a value */

var F_MRI=0400; /* symbol represents a memory-reference instruction */
var F_INDIRECT = 01;
var F_ZEROPAGE = 02;

var lineno = 0;
var symflag = F_USER;
var casesense = true;
var inputradix = 0,pageno= 0,casesense = 0
var symbol_table = {};
var pass = 1;
var warnings = [ ];
var errors = [];
var fixmri = 'SYMBOL';
function warn(msg) {
	var m = { lineno: lineno, msg : msg };
	warnings.push(m);
}
function error(msg) {
	var m = { lineno: lineno, msg : msg };
	errors.push(m);
}
function fatal(msg) {
	throw msg;
}

function doassign(name, v, tok, relmode){ 
	var p = symbol_table[name];
	if( (p != undefined) && pass==1  && (p.flags & F_ASSIGNED))
		warn("symbol redefined");
	if(p == undefined) { p = symbol_table[name] = { name : name };
	p.value = v;
	p.token = tok;
	p.flags |= F_ASSIGNED;
	p.pageno = pageno;
	p.lineno = lineno-1;
	p.relmode = relmode;
}
function dosymbol(name,tok) {
	if(!casesense) name = name.toUpperCase();
	var p = symbol_table[name];
	if(p != undefined) {
		warn(name + " redefined");
	} else {
		p = { name : name, value : 0, token : tok, flags : symflag, type : USER_SYMBOL, pageno : pageno, lineno : lineno-1 };
		symbol_table[name] = p;
	}
	return p;
}
function lookup(name) { 
	return symbol_table[name]; 
	}
function mri(opcode,mod, addr){
	int page = addr & ~PAGE_ADDR,
	    curpage = curloc & ~PAGE_ADDR;
	if(page != 0){
		if(mod & F_ZEROPAGE) 
			warn("conflicting use of zero page modifier?");
		opcode |= CURR_PAGE;
		if(page != curpage) 
			warn("illegal out-of-page reference");
	}
	if(mod & F_INDIRECT)
		opcode |= INDIRECT_MODE;
	return opcode | (addr & PAGE_ADDR);
}
function ISCLA(op) { return ((op & ~GROUP_BIT) == 07200); }
function ISSKP(op) { return ((op & (SKIPS|REVERSE_SENSE)) == REVERSE_SENSE); }
function DIFFGROUPS(op1,op2) { return ((op1 ^ op2) & GROUP_BIT); }

function combine(op1,op2){ /* combine non-memory reference instructions */
	var combop = op1|op2;
	if((op1&OPCODE)==OPCODE_OPR && (op2&OPCODE)==OPCODE_OPR){
		/* are opcodes from different groups? */
		if(DIFFGROUPS(op1,op2)){
			/* special case CLA, it is in both groups
			   so can always be combined */
			if(ISCLA(op1)) 
				op1 ^= GROUP_BIT;
			else if(ISCLA(op2)) 
				op2 ^= GROUP_BIT;
			else
				fatal("can't combine microinstructions from different groups");
		}
		if(!DIFFGROUPS(op1,op2)){
			if(op1 & GROUP_BIT){
				/* group 2: check for incompatible skips: */
				if( ( (op1 & SKIPS) && (op2 & SKIPS) /* both conditional */
				  && ((op1^op2) & REVERSE_SENSE) ) /* and different senses */
				|| ( (combop & SKIPS) /* or, either is conditional */
				     && (ISSKP(op1) || ISSKP(op2)) ) ) /* and one is unconditional */
				fatal("can't combine these skips");
			}else{
				/* group 1: are both rotates? */
				if( (combop & ROTATES) == ROTATES )
					fatal("can't combine rotates");
				else if( (combop & ROTATES) && (combop & 01) )
					fatal("can't combine rotate with IAC");
			}
		}
	}else if((op1&OPCODE)==OPCODE_IOT && (op2&OPCODE)==OPCODE_IOT){
		if( (op1^op2) & DEVICE_SELECT )
			fatal("can't combine I/O transfers on different devices");
	}else
		fatal("can't combine memory-reference instructions");
	return combop;
}
%}
%lex

%%

\n|\r|\r\n|\f			%{ ++lineno; return 'TOK_SEP'; }% 
[ \t]+		 			/* white space */
\/[^\f\n\r]*	 		/* comment */
\;						return 'TOK_SEP';
[0-9]+          		return 'NUMBER'
i|I						return 'I';
z|Z						return 'Z';
[A-Za-z][A-Za-z0-9]* 	return dosymbol(yytext,'SYMBOL').token;
'FIELD'					return 'FIELD';
'EXPUNGE'				return 'EXPUNGE';
'FIXMRI'				return 'FIXMRI';
'DECIMAL'				return 'DECIMAL';
'OCTAL'					return 'OCTAL';
'FIXTAB'				return 'FIXTAB';
[\-+.*,=]				return yytext.charAt(0); 
\$		return 0; /* end of input */

.		return 'INVALID';


/lex

/* operator associations and precedence */

%left '+' '-'
%left '%' '^'
%left '&' '|'
%left UMINUS

%start expressions

%% /* language grammar */

stmts: /* empty */
	| stmts stmt TOK_SEP { flushlist(); }
	| error TOK_SEP { yyerrok; } 
		/* on error, skip to end of statement and recover */
	;
assign : SYMBOL '=' assignval {  $$ = symbol_table[$1] = { name : $1, mri: fixmri, value: $3 }; }
	;
labelinstr: SYMBOL ',' instr { 
	if( pass==1 && (symbol_table[$1].flags & F_ASSIGNED) )
				warn("label already defined; ignoring this definition");
			else 
				doassign($1,curloc,"SYMBOL',0);
	$$ = $4; 
	}
	;

asminstr : instr | labelinstr ;

stmt: /*empty*/ 
	| '*' expr { setorg($2); DPRINTF("-- set origin = %#o\n",$2); } 
	| asminstr { assemble($1,0); DPUTS("-- assemble word"); }
	| assign { /*throw away final value of assignments*/ }

	| 'FIELD' 'NUMBER' {  }
	| 'EXPUNGE' { expunge();  }
	| 'FIXMRI' { fixmri = 'MRI' } 
	  assign { fixmri = 'SYMBOL';  }
	| 'PAUSE'{  }
	| 'FIXTAB' {  }
	| 'DECIMAL' { radix = 10;  }
	| 'OCTAL' { radix = 8;  }
	;
	/* optional addressing modifier */
mod:  'I' { $$ = F_INDIRECT;  }
	| 'Z' { $$ = F_ZEROPAGE;  }
	;
mods: /*empty*/ { $$ = 0; }
	| mods mod { $$ = $1 | $2; }
	;
mri: 'MRI' mods expr { $$ = mri(symbol_table[$1].value,$2,$3); }
	;
term: 'NUMBER' { $$ = parseInt($1,radix); }
	| 'SYMBOL' {
			if(pass==2 && !($1->flags & F_ASSIGNED))
				fatal("undefined symbol");
			$$ = $1.value;
		}
	| '.' { $$ = curloc; }
	;	
comb: term | comb term { $$ = combine($1,$2);  }
	;
expr: comb
	| expr '+' expr { $$ = ($1 + $3) ; DPUTS(" (add) "); }
	| expr '-' expr { $$ = ($1 - $3) ; DPUTS(" (subtract) "); }
	| '-' expr { $$ = -$2 ; DPUTS(" (negate)"); }
	;
instr: expr | mri ;


