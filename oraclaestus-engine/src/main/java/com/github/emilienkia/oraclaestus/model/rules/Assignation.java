package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.*;
import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import lombok.Getter;

@Getter
public class Assignation implements Rule {

    Identifier variableName;
    Expression expression;

    public Assignation(String varName, Expression expression) {
        this(Identifier.fromString(varName), expression);
    }

    public Assignation(Identifier varName, Expression expression) {
        if (varName == null || !varName.isValid()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        variableName = varName;
        this.expression = expression;
    }

    @Override
    public void apply(EvaluationContext context) throws Return {
        Object value = expression.apply(context);
        context.setValue(variableName, value);
    }

    @Override
    public void dump() {
        System.out.print(variableName + " = ");
        expression.dump();
        System.out.println();
    }
}
