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
    'assign' IDENTIFIER (',' IDENTIFIER)* '=' expr ';'
    ;

instanceStmt:
    IDENTIFIER IDENTIFIER '(' '.' IDENTIFIER '(' IDENTIFIER ')' (',' '.' IDENTIFIER '(' IDENTIFIER ')')* ')' ';'
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
