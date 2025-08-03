package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
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
    Map<String, Variable<?>> variables = new HashMap<>();

    List<Rule> rules = new ArrayList<>();

    public RuleGroup add(Rule rule) {
        rules.add(rule);
        return this;
    }

    public RuleGroup add(String name, Variable<?> variable) {
        variables.put(name, variable);
        return this;
    }



    @Override
    public void apply(EvaluationContext context) {
        for (Rule rule : rules) {
            rule.apply(context);
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
