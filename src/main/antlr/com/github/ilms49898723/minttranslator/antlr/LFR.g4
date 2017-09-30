grammar LFR;

lfr:
    (verilogModules)* EOF
    ;

verilogModules:
    'module' IDENTIFIER '(' ')' ';' (verilogStmt)* 'endmodule'
    ;

verilogStmt:
    flowInputDecl
    | flowOutputDecl
    | flowPortDecl
    | controlInputDecl
    | controlPortDecl
    | nodeDecl
    | assignStmt
    | instanceStmt
    | valveStmt
    ;

flowInputDecl:
    'finput' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

flowOutputDecl:
    'foutput' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

flowPortDecl:
    'fport' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

controlInputDecl:
    'cinput' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

controlPortDecl:
    'cport' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

nodeDecl:
    'fnode' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

assignStmt:
    'assign' valvePhase IDENTIFIER (',' IDENTIFIER)* '=' expr ';'
    ;

valvePhase:
    ('valve' '(' IDENTIFIER ')' 'on')?
    ;

instanceStmt:
    IDENTIFIER IDENTIFIER '(' '.' IDENTIFIER '(' IDENTIFIER ')' (',' '.' IDENTIFIER '(' IDENTIFIER ')')* ')' ';'
    ;

valveStmt:
    'valve' IDENTIFIER '(' IDENTIFIER ',' IDENTIFIER ',' IDENTIFIER ')' ';'
    | 'valve' IDENTIFIER '(' '.src' '(' IDENTIFIER ')' ',' '.dst' '(' IDENTIFIER ')' ',' '.ctl' '(' IDENTIFIER ')' ')' ';'
    ;

expr:
    expr OPERATOR expr
    | '(' OPERATOR expr ')'
    | '(' expr ')'
    | primary
    ;

primary:
    IDENTIFIER '(' expr (',' expr)* ')'
    | IDENTIFIER
    ;

RESERVED:
    'module'
    | 'endmodule'
    | 'finput'
    | 'foutput'
    | 'cinput'
    | 'fnode'
    | 'assign'
    | 'with'
    | 'valve'
    ;

IDENTIFIER:
    [A-Za-z_][A-Za-z0-9_]*
    ;

OPERATOR:
    [A-Za-z_][A-Za-z0-9_]*
    | [A-Za-z_]*[+\-*/~!@#$%^&\\]+
    ;

WHITESPACE:
    [ \t\r\n]+ -> skip;
