grammar LFR;

lfr:
    (verilogModules)* EOF
    ;

verilogModules:
    'module' IDENTIFIER '(' ')' ';' (verilogStmt)* 'endmodule'
    | 'module' IDENTIFIER '(' IDENTIFIER (',' IDENTIFIER)* ')' ';' (verilogStmt)* 'endmodule'
    ;

verilogStmt:
    flowInputDecl
    | flowOutputDecl
    | controlInputDecl
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

controlInputDecl:
    'cinput' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

nodeDecl:
    'fnode' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

assignStmt:
    'assign' assignTarget (',' assignTarget)* '=' expr ';'
    ;

assignTarget:
    IDENTIFIER valvePhase
    ;

instanceStmt:
    IDENTIFIER IDENTIFIER '(' '.' IDENTIFIER '(' IDENTIFIER valvePhase ')' (',' '.' IDENTIFIER '(' IDENTIFIER valvePhase ')')* ')' ';'
    ;

valveStmt:
    'valve' '(' IDENTIFIER ',' IDENTIFIER ',' IDENTIFIER ')' ';'
    | 'valve' '(' '.src' '(' IDENTIFIER ')' ',' '.dst' '(' IDENTIFIER ')' ',' '.ctl' '(' IDENTIFIER ')' ')' ';'
    ;

expr:
    '(' expr ')'
    | primary
    ;

primary:
    IDENTIFIER '(' expr (',' expr)* ')' valvePhase
    | IDENTIFIER valvePhase
    ;

valvePhase:
    ('%' IDENTIFIER)?
    ;

RESERVED:
    'module'
    | 'endmodule'
    | 'finput'
    | 'foutput'
    | 'cinput'
    | 'fnode'
    | 'assign'
    | 'valve'
    ;

IDENTIFIER:
    [A-Za-z_][A-Za-z0-9_]*
    ;

WHITESPACE:
    [ \t\r\n]+ -> skip;

COMMENT:
    '/' '/' ~[\r\n]* -> skip;
