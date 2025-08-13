Model file
=================
Oraclaestus uses a specific file format to define entity models and their behaviors.
This is called the 'Model File'.
This file type is a specific text file format described below.

The model file starts with a list of metadata definitions, followed by multiple sections.
Sections can be defined in any order. Many sections of the same type can be defined.

At the end, if multiple sections of the same type are defined, they are merged as if their content were defined 
in a single continuous section.

A section is defined by its type name ('registers', 'functions' or 'rules')
followed by its definition encapsulated between curly braces `{ ... }`.

Comments are supported in the model file, on the for of end-of-line comments starting with `#`.

## Sections

### Metadata
Metadata is defined at the beginning of the file, and is used to provide information about the model.
It is defined as a list of key-value pairs.

```plaintext
name: "Test model"
id:   test_model
```

### Register section

The register section is used to define entity registers.
This section is a list of variable definitions on the form ``<name>: <type> [= <value>]``.

For example:

```plaintext
registers {
    name: string = "Hello !"
    value: int = 42
    isActive: boolean = true
}
```
### Rule section

The rule section is used to define entity ruleset.
These rules are the statements executed at every step of the simulation.

This section is a rule group, aka. a list of rules encapsulated between curly braces `{ ... }`.
There rule groups 

For example:

```plaintext
rules {
    i += 42
    s = "test"
    b = true
    f = 3.0 + 0.14
    if(b) {
        i += 1
    } else if(i >= 100) {
        s = "test2"
    } else {
        s = "test3"
    }
}
```

#### Statements
Statements allowed in the rules section are:
- Assignments: `<var> = <expr>` 
- Conditional assignments `<var> ?= <cond> : <expr>`
- Conditional statements: `if(<cond>) { <statements> } else { <statements> }`
- Sub groups: `{ <statements> }`
- Expressions (see below)
- Variable declarations: `var <name>: <type> [= <value>]?`

Another statement is allowed only on function bodies:
- Return statement: `return <expr>`



### Function section

The function section is used to define entity functions.
These function can be called in the expressions of rules section, or from other functions.

These functions are defined as a list of function definitions on the form:
`<name>( [<arg1>: <type1> [ = defValue1 ] [, <arg2>: <type2> [ = defValue1 ] ]* ]? ) [: <retType]> { <body> }`

Where the argN are the name of the arguments, the TypeN are the types of the arguments.
The defValueN are the default values of the arguments and are optional.
The return type is optional, and if not defined, it may be void or any type.

The body is a list of statements, like in the rule groups in the rule section.

For example:

```plaintext
functions {
    add(a: int, b: int): int {
        return a + b
    }
    isEven(n: int): boolean {
        return n % 2 == 0
    }
}
```

## Expressions

<<To be continued>>

