grammar Model;

model
    : metadata (registers|rules|functions)*
    ;

metadata
    : metadata_line*
    ;

metadata_line
    : metadata_name=ID (':' metadata_value=value_or_id)? # MetadataDeclaration
    ;

registers
    : 'registers' '{' (var_decl|macro_decl)* '}'
    ;

var_decl
    : var_name=identifier ':' var_type=type ('=' var_def_value=expression)? # VariableDeclaration
    ;

macro_decl
    : macro_name=identifier ':=' expression # MacroDeclaration
    ;

rules
    : 'rules' rule_group
    ;

rule_group
    : '{' rule_decl* '}' # RuleGroup
    ;

rule_decl
    : condition_rule        # ConditionRule
    | 'return' expression?  # ReturnRule
    | rule_group            # RuleGroupRule
    | var_decl              # VariableDeclarationRule
    | rule_name=ID op=('='|'+='|'-='|'*='|'/='|'%=') expression # AssignationRule
    | rule_name=ID '?=' expression ':' expression               # ConditionnalAssignationRule
    | expression            # ExpressionRule
    ;

condition_rule: 'if' '(' condition=cond_expr ')' rule_group ('else' (condition_rule|rule_group))? # Condition ;
cond_expr : expression # ConditionExpression;

functions: 'functions' '{' function_decl* '}';

function_decl:
    func_name=identifier '(' (params+=var_decl (',' params+=var_decl)*)? ')' (':' return_type=function_return_type)? block=rule_group # FunctionDeclaration
    ;

function_return_type: type #FunctionReturnType;

//
// Expressions :
//

expression : conditionalExpression;

conditionalExpression
    : logicalOrExpression (cond='?' conditionalExpression ':' conditionalExpression)? // # ConditionalExpression
    ;

logicalOrExpression
    : logicalAndExpression (op=('|'|'||') logicalOrExpression)? // # LogicalOrExpression
    ;

logicalAndExpression
    : exclusiveOrExpression (op=('&'|'&&') logicalAndExpression)? // # LogicalAndExpression
    ;

exclusiveOrExpression
    : equalityExpression (op=('^'|'^^') exclusiveOrExpression)? // # ExclusiveOrExpression
    ;

equalityExpression
    : relationalExpression (op=('==' | '!=') equalityExpression)? // # EqualityExpression
    ;

relationalExpression
    : additiveExpression (op=('<' | '>' | '<=' | '>=') relationalExpression)? // #  RelationalExpression
    ;

additiveExpression
    : multiplicativeExpression (op=('+' | '-') additiveExpression)? // # AdditiveExpression
    ;

multiplicativeExpression
    : unaryExpression (op=('*' | '/' | '%') multiplicativeExpression)? // # MultiplicativeExpression
    ;

unaryExpression
    : '-' unaryExpression # UnaryMinusExpression
    | primaryExpression # NoUnaryExpression
    ;

primaryExpression
    : '(' expression ')'    # ParenthesizedExpression
    | functionCall          # FunctionCallExpression
    | value                 # ValueExpression
    | identifier            # VariableReferenceExpression
    ;

functionCall : func_name=identifier '(' param_value+=expression (',' param_value+=expression)* ')';

identifier
    : var_name+=ID ('.' var_name+=ID)* # VariableIdentifier
    ;

type
    : 'int'             # IntegerType
    | 'float'           # FloatType
    | 'string'          # StringType
    | 'boolean'         # BooleanType
    | 'enum'  '{' enum_value+=ID (',' enum_value+=ID)* '}'   # EnumType
    | 'state' '{' enum_value+=ID (',' enum_value+=ID)* '}'   # StateType
    ;

value_or_id : value | ID ;

value
    : BOOLEAN
    | STRING
    | NUMBER
    ;


ID : [~]?[a-zA-Z_][a-zA-Z0-9_]* ;

STRING : ('"' (~["\r\n])* '"') ;

NUMBER : [0-9]+ ('.' [0-9]+)? ;

BOOLEAN : 'true' | 'false' ;

LINE_COMMENT : '#' ~[\r\n]* -> channel(HIDDEN);
EOL : [\r\n]+ -> skip ;
WS  : [ \t]+ -> skip ;
