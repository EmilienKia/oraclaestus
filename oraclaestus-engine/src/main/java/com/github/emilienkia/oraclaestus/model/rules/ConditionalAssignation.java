package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.*;
import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import lombok.Getter;

@Getter
public class ConditionalAssignation implements Rule {
    private final Identifier variableName;
    private final Expression condition;
    private final Expression value;

    public ConditionalAssignation(Identifier field, Expression condition, Expression value) {
        this.variableName = field;
        this.condition = condition;
        this.value = value;
    }

    @Override
    public void apply(EvaluationContext context) throws Return {
        Object conditionResult = condition.apply(context);
        if (Helper.toBool(conditionResult)) {
            Object valueResult = value.apply(context);
            context.setValue(variableName, valueResult);
        }
    }

    @Override
    public void dump() {
        System.out.print(variableName + " ?= ");
        condition.dump();
        System.out.print(" : ");
        value.dump();
        System.out.println();
    }
}
