grammar LFR;

lfr:
    (verilog_modules)* EOF
    ;

verilog_modules:
    'module' IDENTIFIER '(' ')' ';' verilog_stmts 'endmodule'
    ;

verilog_stmts:
    (verilog_stmt)*
    ;

verilog_stmt:
    flow_input_decl
    | flow_output_decl
    | control_input_decl
    | channel_decl
    | assign_stmt
    ;

flow_input_decl:
    'finput' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

flow_output_decl:
    'foutput' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

control_input_decl:
    'cinput' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

channel_decl:
    'fchannel' IDENTIFIER (',' IDENTIFIER)* ';'
    ;

assign_stmt:
    'assign' IDENTIFIER '=' expr ';'
    ;

expr:
    expr OPERATOR expr
    | expr OPERATOR expr
    | expr OPERATOR expr
    | expr OPERATOR expr
    | '(' expr OPERATOR ')'
    | '(' expr ')'
    | IDENTIFIER
    ;

IDENTIFIER:
    [A-Za-z_][A-Za-z0-9_]*
    ;

OPERATOR:
    '+' | '-' | '*' | '/' | '&' | '#' | '$' | '@' | '!' | '~'
    ;

WS:
    [ \t\r\n]+ -> skip;
