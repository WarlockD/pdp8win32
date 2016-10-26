/* description: Parses end executes mathematical expressions. */

/* lexical grammar */
%{
var lineno = 0;
var inputradix = 0,pageno= 0,casesense = 0
var symbol_table = {};
%}
%lex

%%

\n|\r|\r\n|\f	%{ ++lineno; return 'TOK_SEP'; }% 
[ \t]+		 /* whitespace */
\/[^\f\n\r]*	 /* comment */
\;		return 'TOK_SEP';
[0-9]+          return 'NUMBER'
i|I		return 'I';
z|Z		return 'Z';
[A-Za-z][A-Za-z0-9]* return 'TOK_SYM';
'FIELD'
[\-+.*,=]		return yytext.charAt(0); 
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
assign : TOK_SYM '=' assignval { doassign($1,$$ = $3,fixmri,0); }

labelinstr: TOK_SYM ',' {
		if( pass==1 && ($1->flags & F_ASSIGNED) )
			warn("label already defined; ignoring this definition");
		else 
			doassign($1,curloc,TOK_SYM,0);
	}
	instr { $$ = $4; }
	;

asminstr : instr | labelinstr ;

expressions
    : e EOF
        {return $1;}
    ;

e
    : e '+' e
        {$$ = $1+$3;}
    | e '-' e
        {$$ = $1-$3;}
    | e '*' e
        {$$ = $1*$3;}
    | e '/' e
        {$$ = $1/$3;}
    | e '^' e
        {$$ = Math.pow($1, $3);}
    | '-' e %prec UMINUS
        {$$ = -$2;}
    | '(' e ')'
        {$$ = $2;}
    | NUMBER
        {$$ = Number(yytext);}
    | E
        {$$ = Math.E;}
    | PI
        {$$ = Math.PI;}
    ;
