package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.expressions.Greater;
import com.github.emilienkia.oraclaestus.model.expressions.Lesser;
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

/*        if( i > 50 ) {
            s = "i is greater than 50"
        } else {
            s = "i is less than or equal to 50"
        }*/

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);

        assertThat(model).isNotNull();
        assertThat(model.getName()).isEqualTo("Test model");
        assertThat(model.getId()).isEqualTo("test_model");

        assertThat(model.getRegisters()).hasSize(4);

        {
            Variable<?> s = model.getRegisters().get("s");
            assertThat(s).isNotNull();
            assertThat(s.getType()).isEqualTo(Type.STRING);
            assertThat(s.getDefaultValue()).isNotNull().asString().isEqualTo("Hello, World!");
        }

        {
            Variable<?> s = model.getRegisters().get("i");
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
}

rules {
    temp : float = 4.0
    i = i + 4
}
""";

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);
        assertThat(model).isNotNull();

        assertThat(model.getRegisters()).hasSize(4);

        {
            Variable<?> var = model.getRegisters().get("s");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.STRING);
            assertThat(var.getDefaultValue()).isNotNull().asString().isEqualTo("Hello, World!");
        }

        {
            Variable<?> var = model.getRegisters().get("i");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.INTEGER);
            assertThat(var.getDefaultValue()).isNotNull().isInstanceOf(Integer.class).isEqualTo(42);
        }

        {
            Variable<?> var = model.getRegisters().get("e");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.ENUM);
            assertThat(var.getDefaultValue()).isNotNull().isInstanceOf(EnumerationType.Instance.class);
            EnumerationType.Instance enumValue = (EnumerationType.Instance)var.getDefaultValue();
            EnumerationType type = enumValue.getEnumeration() ;
            assertThat(type).isNotNull();
            assertThat(type.getCount()).isEqualTo(3);
            assertThat(type.getValue("RED")).isEqualTo(0);
            assertThat(type.getValue("GREEN")).isEqualTo(1);
            assertThat(type.getValue("BLUE")).isEqualTo(2);
            assertThat(enumValue.getValue()).isEqualTo(1); // GREEN
            // TODO Add enum declaration check
        }

        {
            Variable<?> var = model.getRegisters().get("b");
            assertThat(var).isNotNull();
            assertThat(var.getType()).isEqualTo(Type.BOOLEAN);
            Object defaultValue = var.getDefaultValue();
            assertThat(var.getDefaultValue()).isNotNull().isInstanceOf(Boolean.class).isEqualTo(true);
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
    i = 4
    i ?= i > 5 : 4
    if( i > 50 ) {
        i = 50
    } else if ( i < 10 )  {
        i = 10
    } else {
        i = i + 1
    }
}
""";

        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(source);

        assertThat(model).isNotNull();
        assertThat(model.getRuleGroups()).hasSize(1);

        RuleGroup ruleGroup = model.getRuleGroups().getFirst();
        assertThat(ruleGroup).isNotNull();
        assertThat(ruleGroup.getRules()).hasSize(3);

        {
            Rule rule = ruleGroup.getRules().getFirst();
            assertThat(rule).isNotNull().isInstanceOf(Assignation.class);
            Assignation assignation = (Assignation) rule;
            assertThat(assignation.getVariableName()).isEqualTo("i");
            // TODO
        }

        {
            Rule rule = ruleGroup.getRules().get(1);
            assertThat(rule).isNotNull().isInstanceOf(ConditionalAssignation.class);
            ConditionalAssignation assignation = (ConditionalAssignation) rule;
            assertThat(assignation.getVariableName()).isEqualTo("i");
            assertThat(assignation.getCondition()).isNotNull().isInstanceOf(Greater.class);
            // TODO
        }

        {
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

        // TODO test all the expressions and the expression priority construction
    }


}