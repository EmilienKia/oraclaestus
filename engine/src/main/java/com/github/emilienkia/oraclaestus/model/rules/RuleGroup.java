package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RuleGroup implements Rule {

    @Singular
    Map<Identifier, Variable<?>> variables = new HashMap<Identifier, Variable<?>>();

    List<Rule> rules = new ArrayList<>();

    public RuleGroup add(Rule rule) {
        rules.add(rule);
        return this;
    }

    public RuleGroup add(Identifier name, Variable<?> variable) {
        variables.put(name, variable);
        return this;
    }

    public Variable<?> getVariable(String name) {
        return getVariable(Identifier.fromString(name));
    }

    public Variable<?> getVariable(Identifier name) {
        return variables.get(name);
    }

    class RuleGroupEvaluationContext extends EvaluationContext {

        Map<Identifier, Object> values = new HashMap<>();

        public RuleGroupEvaluationContext(EvaluationContext parent) {
            super(parent);
            initialize();
        }

        void initialize() {
            for (Map.Entry<Identifier, Variable<?>> entry : variables.entrySet()) {
                Identifier name = entry.getKey();
                Variable<?> variable = entry.getValue();
                Object defaultValue = variable.createDefaultValue();
                values.put(name, defaultValue);
            }
        }

        @Override
        public Object getValue(Identifier name) {
            Variable<?> variable = variables.get(name);
            if (variable != null) {
                return values.get(name);
            }
            return super.getValue(name);
        }

        public void setValue(Identifier name, Object value) {
            Variable<?> variable = variables.get(name);
            if (variable!=null) {
                values.put(name, variable.getTypeDescriptor().cast(value));
            } else {
                super.setValue(name, value);
            }
        }
    }

    @Override
    public void apply(EvaluationContext context) throws Return {
        RuleGroupEvaluationContext localContext = new RuleGroupEvaluationContext(context);
        for (Rule rule : rules) {
            rule.apply(localContext);
        }
    }

    @Override
    public void dump() {
        System.out.println("{");
        for (Rule rule : rules) {
            rule.dump();
        }
        System.out.println("}");
    }
}
