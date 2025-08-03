grammar Model;

model
    : metadata (registers|rules|functions)*
    ;

metadata
    : metadata_line*
    ;

metadata_line
    : metadata_name=ID (':' metadata_value=value)? # MetadataDeclaration
    ;

registers : 'registers' '{' var_decl* '}' ;

var_decl
    : var_name=ID ':' var_type=type ('=' var_def_value=expression)? # VariableDeclaration
    ;

rules
    : 'rules' rule_group
    ;

rule_group
    : '{' rule_decl* '}' # RuleGroup
    ;

rule_decl
    : condition_rule # ConditionRule
    | rule_group     # RuleGroupRule
    | var_decl       # VariableDeclarationRule
    | rule_name=ID op=('='|'+='|'-='|'*='|'/='|'%=') expression # AssignationRule
    | rule_name=ID '?=' expression ':' expression               # ConditionnalAssignationRule
    ;

condition_rule: 'if' '(' condition=cond_expr ')' rule_group ('else' (condition_rule|rule_group))? # Condition ;
cond_expr : expression # ConditionExpression;



functions: 'functions' '{' function_decl* '}';

function_decl:
    func_name=ID '(' (params+=var_decl (',' params+=var_decl)*)? ')' (':' return_type=type)? block=rule_group
    ;





expression
    : '(' expression ')' # ParenthesizedExpression
    | '-' expression # UnaryMinusExpression
    | expression op=('+'|'-'|'*'|'/'|'%'|'&&'|'||'|'^'|'=='|'!='|'<'|'>'|'<='|'>=') expression # BinaryExpression
    | expression '?' expression ':' expression # TernaryExpression
    | value # ValueExpression
    ;

type
    : 'int'             # IntegerType
    | 'float'           # FloatType
    | 'string'          # StringType
    | 'boolean'         # BooleanType
    | 'enum' '{' enum_value+=ID (',' enum_value+=ID)* '}'   # EnumType
    ;

value
    : BOOLEAN
    | STRING
    | ID
    | NUMBER
    ;

ID  : [~]?[a-zA-Z_][a-zA-Z0-9_]* ;

STRING : ('"' (~["\r\n])* '"') ;

NUMBER : [0-9]+ ('.' [0-9]+)? ;

BOOLEAN : ('true'|'false') ;

LINE_COMMENT : '#' ~[\r\n]* -> channel(HIDDEN);
EOL : [\r\n]+ -> skip ;
WS  : [ \t]+ -> skip ;
