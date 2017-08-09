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
    'assign' IDENTIFIER (',' IDENTIFIER)* valvePhase '=' expr ';'
    ;

valvePhase:
    ('with' 'valve' '(' IDENTIFIER ')')?
    ;

instanceStmt:
    IDENTIFIER IDENTIFIER '(' '.' IDENTIFIER '(' IDENTIFIER ')' (',' '.' IDENTIFIER '(' IDENTIFIER ')')* ')' ';'
    ;

valveStmt:
    'valve' IDENTIFIER '(' '.src' '(' IDENTIFIER ')' ',' '.dst' '(' IDENTIFIER ')' ',' '.ctl' '(' IDENTIFIER ')' ')' ';'
    ;

expr:
    expr OPERATOR expr
    | '(' OPERATOR expr ')'
    | '(' expr ')'
    | primary
    ;
    // Mux(Node(f1, f2), Node(f1, f2, f3));

primary:
    IDENTIFIER '(' expr (',' expr)* ')'
    | IDENTIFIER
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
