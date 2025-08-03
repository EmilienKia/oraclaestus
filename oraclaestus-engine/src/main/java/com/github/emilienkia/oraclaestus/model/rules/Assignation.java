package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;
import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import lombok.Getter;

@Getter
public class Assignation implements Rule {

    String variableName;
    Expression expression;

    public Assignation(String varName, Expression expression) {
        if (varName == null || varName.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        variableName = varName;
        this.expression = expression;
    }

    @Override
    public void apply(EvaluationContext context) {
        Object value = expression.apply(context);
        context.getNewState().setValue(variableName, value);
    }

    @Override
    public void dump() {
        System.out.print(variableName + " = ");
        expression.dump();
        System.out.println();
    }
}
