package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.model.functions.Function;
import com.github.emilienkia.oraclaestus.model.functions.RuleGroupFunction;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.expressions.*;
import com.github.emilienkia.oraclaestus.model.expressions.FunctionCall;
import com.github.emilienkia.oraclaestus.model.rules.*;
import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import com.github.emilienkia.oraclaestus.model.types.Type;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class ModelParserHelperTest {

    @Test
    void testParseModelFile() throws IOException {

        String source =
"""
name: "Test model"
id:   test_model

registers {
    s: string = "Hello, World!"
    i: int = 42
    e: enum {
        RED,
        GREEN,
        BLUE
    } = RED
    b: boolean = true
}

rules {
    temp : float = 4.0
    i = i + 4
    s += "!"
    e = BLUE
    b = i > 4 ? true : false
}
""";

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);

        assertThat(model).isNotNull();
        assertThat(model.getName()).isEqualTo("Test model");
        assertThat(model.getId()).isEqualTo("test_model");

        assertThat(model.getRegisters()).hasSize(4);

        {
            Variable<?> s = model.getRegister("s");
            assertThat(s).isNotNull();
            assertThat(s.getType()).isEqualTo(Type.STRING);
            assertThat(s.getDefaultValue()).isNotNull().asString().isEqualTo("Hello, World!");
        }

        {
            Variable<?> s = model.getRegister("i");
            assertThat(s).isNotNull();
            assertThat(s.getType()).isEqualTo(Type.INTEGER);
            assertThat(s.getDefaultValue()).isNotNull().isInstanceOf(Integer.class).isEqualTo(42);
        }
    }


    @Test
    void testParsingMetadata() throws IOException {
        String source =
"""
name: "Test model"
id:   test_model

registers {
    s: string = "Hello, World!"
    i: int = 42
    e: enum {
        RED,
        GREEN,
        BLUE
    } = RED
    b: boolean = true
}

rules {
    temp : float = 4.0
    i = i + 4
}
""";

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);

        assertThat(model).isNotNull();
        assertThat(model.getName()).isEqualTo("Test model");
        assertThat(model.getId()).isEqualTo("test_model");
    }

    @Test
    void testParsingRegisters() throws IOException {
        String source =
"""
name: "Test model"
id:   test_model

registers {
    s: string = "Hello, World!"
    i: int = 42
    e: enum {
        RED,
        GREEN,
        BLUE
    } = GREEN
    b: boolean = true
    st: enum {
        ON,
        OFF,
        UNKNOWN
    } = ON
    m := b ? 5 : i + 1
    t.t : int = i
    m.m := t.t + 1
}

rules {
    temp : float = 4.0
    i = i + 4
}
""";

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);
        assertThat(model).isNotNull();

        assertThat(model.getRegisters()).hasSize(6);

        {
            Variable<?> var = model.getRegister("s");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.STRING);
            assertThat(var.getDefaultValue()).isNotNull().asString().isEqualTo("Hello, World!");
        }

        {
            Variable<?> var = model.getRegister("i");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.INTEGER);
            assertThat(var.getDefaultValue()).isNotNull().isInstanceOf(Integer.class).isEqualTo(42);
        }

        {
            Variable<?> var = model.getRegister("e");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.ENUM);
            assertThat(var.getDefaultValue()).isNotNull().isInstanceOf(EnumerationType.Instance.class);
            EnumerationType.Instance enumValue = (EnumerationType.Instance)var.getDefaultValue();
            EnumerationType type = enumValue.getEnumerationType() ;
            assertThat(type).isNotNull();
            assertThat(type.getCount()).isEqualTo(3);
            assertThat(type.getValue("RED")).isEqualTo(0);
            assertThat(type.getValue("GREEN")).isEqualTo(1);
            assertThat(type.getValue("BLUE")).isEqualTo(2);
            assertThat(enumValue.getValue()).isEqualTo(1); // GREEN
            // TODO Add enum declaration check
        }

        {
            Variable<?> var = model.getRegister("b");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.BOOLEAN);
            assertThat(var.getDefaultValue()).isNotNull().isInstanceOf(Boolean.class).isEqualTo(true);
        }

        {
            Variable<?> var = model.getRegister("st");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.ENUM);
            assertThat(var.getDefaultValue()).isNotNull().isInstanceOf(EnumerationType.Instance.class);
            EnumerationType.Instance stateValue = (EnumerationType.Instance)var.getDefaultValue();
            EnumerationType type = stateValue.getEnumerationType() ;
            assertThat(type).isNotNull();
            assertThat(type.getCount()).isEqualTo(3);
            assertThat(type.getValue("ON")).isEqualTo(0);
            assertThat(type.getValue("OFF")).isEqualTo(1);
            assertThat(type.getValue("UNKNOWN")).isEqualTo(2);
            assertThat(stateValue.getValue()).isEqualTo(0); // ON

        }

        {
            Variable<?> var = model.getRegister("t.t");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.INTEGER);
            assertThat(var.getDefaultValue()).isNull();
            assertThat(var.getInitialExpression()).isNotNull().isInstanceOf(ReadValue.class);
        }

        assertThat(model.getMacros()).hasSize(2);

        {
            Expression macro = model.getMacro("m");
            assertThat(macro).isNotNull();
            assertThat(macro).isInstanceOf(Conditional.class);
            Conditional conditional = (Conditional) macro;
            assertThat(conditional.getCondition()).isNotNull().isInstanceOf(ReadValue.class);
            assertThat(conditional.getTrueExpression()).isNotNull().isInstanceOf(ConstValue.class);
            assertThat(conditional.getFalseExpression()).isNotNull().isInstanceOf(Addition.class);
        }

        {
            Expression macro = model.getMacro("m.m");
            assertThat(macro).isNotNull();
            assertThat(macro).isInstanceOf(Addition.class);
            Addition addition = (Addition) macro;
            assertThat(addition.getLeftExpression()).isNotNull().isInstanceOf(ReadValue.class);
            ReadValue left = (ReadValue) addition.getLeftExpression();
            assertThat(left.getIdentifier()).isEqualTo("t.t");
            assertThat(addition.getRightExpression()).isNotNull().isInstanceOf(ConstValue.class);
            ConstValue right = (ConstValue) addition.getRightExpression();
            assertThat(right.getValue()).isEqualTo(1);
        }

    }


    @Test
    void testParsingTypes() throws IOException {
        String source =
                """
                types {
                    enum color {
                        RED,
                        GREEN,
                        BLUE
                    }
                }
                registers {
                    c: color = RED
                }
                
                rules {
                    c = GREEN
                }
                """;

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);
        assertThat(model).isNotNull();

        assertThat(model.getEnumerations()).hasSize(1);
        {
            assertThat(model.getCustomTypes()).hasSize(1);
            assertThat(model.getCustomTypes().get("color")).isNotNull().isInstanceOf(EnumerationType.class);
            EnumerationType colorType = (EnumerationType) model.getCustomTypes().get("color");
            assertThat(colorType.getCount()).isEqualTo(3);
            assertThat(colorType.getValue("RED")).isEqualTo(0);
            assertThat(colorType.getValue("GREEN")).isEqualTo(1);
            assertThat(colorType.getValue("BLUE")).isEqualTo(2);
        }

        assertThat(model.getRegisters()).hasSize(1);
        {
            Variable<?> c = model.getRegister("c");
            assertThat(c).isNotNull();
            assertThat(c.getType()).isEqualTo(Type.ENUM);
            assertThat(c.getDefaultValue()).isNotNull().isInstanceOf(EnumerationType.Instance.class);
            EnumerationType.Instance enumValue = (EnumerationType.Instance)c.getDefaultValue();
            EnumerationType type = enumValue.getEnumerationType() ;
            assertThat(type).isNotNull();
            assertThat(type.getCount()).isEqualTo(3);
            assertThat(type.getValue("RED")).isEqualTo(0);
            assertThat(type.getValue("GREEN")).isEqualTo(1);
            assertThat(type.getValue("BLUE")).isEqualTo(2);
            assertThat(enumValue.getValue()).isEqualTo(0); // RED
        }

        assertThat(model.getRuleGroups()).hasSize(1);
        RuleGroup ruleGroup = model.getRuleGroups().getFirst();
        assertThat(ruleGroup).isNotNull();
        assertThat(ruleGroup.getRules()).hasSize(1);

        {
            Rule rule = ruleGroup.getRules().getFirst();
            assertThat(rule).isNotNull().isInstanceOf(Assignation.class);
            Assignation assignation = (Assignation) rule;
            assertThat(assignation.getVariableName()).isEqualTo("c");
            assertThat(assignation.getExpression()).isNotNull().isInstanceOf(ReadValue.class);
            ReadValue readValue = (ReadValue) assignation.getExpression();
            assertThat(readValue.getIdentifier()).isEqualTo("GREEN");
        }

    }


    @Test
    void testParsingRules() throws IOException {
        String source =
"""
name: "Test model"
id:   test_model

registers {
    i: int = 42
}

rules {
    i = 4               # Rule 0 : Assignation
    i ?= i > 5 : 4      # Rule 1 : Conditional assignation
    a : int = 0         # Rule # : Variable declaration
    if( i > 50 ) {      # Rule 2 : Condition
        i = 50
    } else if ( i < 10 )  {
        i = 10
    } else {
        i = i + 1
    }
    {                   # Rule 3 : subgroup
        b : int = 5
        i = i * 2
        i = i - 1
        {
            c : int = 10
            i = i % 5
        }
    }
}
""";

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);

        assertThat(model).isNotNull();
        assertThat(model.getRuleGroups()).hasSize(1);

        RuleGroup ruleGroup = model.getRuleGroups().getFirst();
        assertThat(ruleGroup).isNotNull();
        assertThat(ruleGroup.getRules()).hasSize(4);

        assertThat(ruleGroup.getVariables()).hasSize(1);
        assertThat(ruleGroup.getVariable("a")).isNotNull();
        assertThat(ruleGroup.getVariable("b")).isNull();
        assertThat(ruleGroup.getVariable("c")).isNull();


        {   // Rule 0 : Assignation
            Rule rule = ruleGroup.getRules().getFirst();
            assertThat(rule).isNotNull().isInstanceOf(Assignation.class);
            Assignation assignation = (Assignation) rule;
            assertThat(assignation.getVariableName()).isEqualTo("i");
            // TODO
        }

        {   // Rule 1 : Conditional assignation
            Rule rule = ruleGroup.getRules().get(1);
            assertThat(rule).isNotNull().isInstanceOf(ConditionalAssignation.class);
            ConditionalAssignation assignation = (ConditionalAssignation) rule;
            assertThat(assignation.getVariableName()).isEqualTo("i");
            assertThat(assignation.getCondition()).isNotNull().isInstanceOf(Greater.class);
            // TODO
        }

        {   // Rule 2 : Condition
            Rule rule = ruleGroup.getRules().get(2);
            assertThat(rule).isNotNull().isInstanceOf(Condition.class);
            Condition condition = (Condition) rule;
            assertThat(condition.getCondition()).isNotNull().isInstanceOf(Greater.class);

            assertThat(condition.getThenRule()).isNotNull().isInstanceOf(RuleGroup.class);
            RuleGroup thenRule = (RuleGroup) condition.getThenRule();
            // TODO

            assertThat(condition.getElseRule()).isNotNull().isInstanceOf(Condition.class);
            Condition elseRule = (Condition) condition.getElseRule();
            assertThat(elseRule.getCondition()).isNotNull().isInstanceOf(Lesser.class);
            assertThat(elseRule.getThenRule()).isNotNull().isInstanceOf(RuleGroup.class);
            assertThat(elseRule.getElseRule()).isNotNull().isInstanceOf(RuleGroup.class);
            // TODO
        }

        {   // Rule 3 : Subgroup
            Rule rule = ruleGroup.getRules().get(3);
            assertThat(rule).isNotNull().isInstanceOf(RuleGroup.class);
            RuleGroup subGroup = (RuleGroup) rule;
            assertThat(subGroup.getRules()).hasSize(3);
            assertThat(subGroup.getVariables()).hasSize(1);
            assertThat(subGroup.getVariable("a")).isNull();
            assertThat(subGroup.getVariable("b")).isNotNull();
            assertThat(ruleGroup.getVariable("c")).isNull();

            {
                Rule subRule = subGroup.getRules().get(0);
                assertThat(subRule).isNotNull().isInstanceOf(Assignation.class);
                Assignation assignation = (Assignation) subRule;
                assertThat(assignation.getVariableName()).isEqualTo("i");
                assertThat(assignation.getExpression()).isNotNull().isInstanceOf(Multiplication.class);
            }

            {
                Rule subRule = subGroup.getRules().get(1);
                assertThat(subRule).isNotNull().isInstanceOf(Assignation.class);
                Assignation assignation = (Assignation) subRule;
                assertThat(assignation.getVariableName()).isEqualTo("i");
                assertThat(assignation.getExpression()).isNotNull().isInstanceOf(Subtraction.class);
            }

            {
                Rule subRule = subGroup.getRules().get(2);
                assertThat(subRule).isNotNull().isInstanceOf(RuleGroup.class);
                RuleGroup subRule2 = (RuleGroup) subRule;
                assertThat(subRule2.getVariables()).hasSize(1);
                assertThat(subRule2.getVariable("a")).isNull();
                assertThat(subRule2.getVariable("b")).isNull();
                assertThat(subRule2.getVariable("c")).isNotNull();

                assertThat(subRule2.getRules()).hasSize(1);

                {
                    Rule subRule3 = subRule2.getRules().get(0);
                    assertThat(subRule3).isNotNull().isInstanceOf(Assignation.class);
                    Assignation assignation = (Assignation) subRule3;
                    assertThat(assignation.getVariableName()).isEqualTo("i");
                    assertThat(assignation.getExpression()).isNotNull().isInstanceOf(Modulo.class);
                }

            }
        }

        // TODO test all the expressions and the expression priority construction
    }



    @Test
    void testParsingFunctions() throws IOException {
        String source =
"""
name: "Test model"
id:   test_model

registers {
    i: int = 42
}

functions {
    add( a : int, b : int = 0) : int {
        c : int = a + b
        c *= 2
        return c + 1
    }
}

rules {
    i = add(2, 5)
    add(4, 8)
}
""";

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);

        assertThat(model).isNotNull();
        assertThat(model.getFunctions()).hasSize(1);

        {   // Function add
            Function function = model.getFunction("add");
            assertThat(function).isNotNull();
            assertThat(function.getName()).isNotNull().isEqualTo("add");
            assertThat(function.getParameters()).hasSize(2);

            assertThat(function.getParameters().get(0)).isNotNull().isInstanceOf(Variable.class);
            Variable<?> paramA = function.getParameters().get(0);
            assertThat(paramA.getName()).isEqualTo("a");
            assertThat(paramA.getType()).isEqualTo(Type.INTEGER);
            assertThat(paramA.getDefaultValue()).isNull();

            assertThat(function.getParameters().get(1)).isNotNull().isInstanceOf(Variable.class);
            Variable<?> paramB = function.getParameters().get(1);
            assertThat(paramB.getName()).isEqualTo("b");
            assertThat(paramB.getType()).isEqualTo(Type.INTEGER);
            assertThat(paramB.getDefaultValue()).isNotNull().isInstanceOf(Integer.class).isEqualTo(0);

            assertThat(function.getReturnType()).isNotNull();
            assertThat(function.getReturnType().getType()).isEqualTo(Type.INTEGER);

            assertThat(function).isInstanceOf(RuleGroupFunction.class);
            RuleGroupFunction ruleGroupFunction = (RuleGroupFunction) function;
            assertThat(ruleGroupFunction.getRuleGroup()).isNotNull();

            assertThat(ruleGroupFunction.getRuleGroup().getVariables()).hasSize(1);
            assertThat(ruleGroupFunction.getRuleGroup().getVariable("c")).isNotNull();
            assertThat(ruleGroupFunction.getRuleGroup().getVariable("c").getType()).isEqualTo(Type.INTEGER);

            assertThat(ruleGroupFunction.getRuleGroup().getRules()).hasSize(3);

            {
                assertThat(ruleGroupFunction.getRuleGroup().getRules().get(0)).isNotNull().isInstanceOf(Assignation.class);
                Assignation assignation = (Assignation) ruleGroupFunction.getRuleGroup().getRules().get(0);
                assertThat(assignation.getVariableName()).isEqualTo("c");
                assertThat(assignation.getExpression()).isNotNull().isInstanceOf(Addition.class);
                // TODO
            }

            {
                assertThat(ruleGroupFunction.getRuleGroup().getRules().get(1)).isNotNull().isInstanceOf(Assignation.class);
                Assignation assignation = (Assignation) ruleGroupFunction.getRuleGroup().getRules().get(1);
                assertThat(assignation.getVariableName()).isEqualTo("c");
                // TODO
            }

            {
                assertThat(ruleGroupFunction.getRuleGroup().getRules().get(2)).isNotNull().isInstanceOf(ReturnRule.class);
                ReturnRule returnRule = (ReturnRule) ruleGroupFunction.getRuleGroup().getRules().get(2);
                assertThat(returnRule.getExpression()).isNotNull().isInstanceOf(Addition.class);
                // TODO
            }
        }

        assertThat(model.getRuleGroups()).hasSize(1);
        RuleGroup ruleGroup = model.getRuleGroups().getFirst();
        assertThat(ruleGroup).isNotNull();
        assertThat(ruleGroup.getRules()).hasSize(2);

        {
            Rule rule = ruleGroup.getRules().get(0);
            assertThat(rule).isNotNull().isInstanceOf(Assignation.class);
            Assignation assignation = (Assignation) rule;
            assertThat(assignation.getVariableName()).isEqualTo("i");
            assertThat(assignation.getExpression()).isNotNull().isInstanceOf(FunctionCall.class);
            FunctionCall functionCall = (FunctionCall) assignation.getExpression();
            assertThat(functionCall.getFunctionName()).isEqualTo("add");
            assertThat(functionCall.getArguments()).hasSize(2);

            assertThat(functionCall.getArguments().get(0)).isNotNull().isInstanceOf(ConstValue.class);
            ConstValue arg1 = (ConstValue) functionCall.getArguments().get(0);
            assertThat(arg1.getValue()).isEqualTo(2);

            assertThat(functionCall.getArguments().get(1)).isNotNull().isInstanceOf(ConstValue.class);
            ConstValue arg2 = (ConstValue) functionCall.getArguments().get(1);
            assertThat(arg2.getValue()).isEqualTo(5);
        }

        {
            Rule rule = ruleGroup.getRules().get(1);
            assertThat(rule).isNotNull().isInstanceOf(ExpressionRule.class);
            ExpressionRule expressionRule = (ExpressionRule) rule;

            assertThat(expressionRule.getExpression()).isNotNull().isInstanceOf(FunctionCall.class);
            FunctionCall functionCall = (FunctionCall)expressionRule.getExpression();

            assertThat(functionCall.getFunctionName()).isEqualTo("add");
            assertThat(functionCall.getArguments()).hasSize(2);

            assertThat(functionCall.getArguments().get(0)).isNotNull().isInstanceOf(ConstValue.class);
            ConstValue arg1 = (ConstValue) functionCall.getArguments().get(0);
            assertThat(arg1.getValue()).isEqualTo(4);

            assertThat(functionCall.getArguments().get(1)).isNotNull().isInstanceOf(ConstValue.class);
            ConstValue arg2 = (ConstValue) functionCall.getArguments().get(1);
            assertThat(arg2.getValue()).isEqualTo(8);
        }

    }
}
