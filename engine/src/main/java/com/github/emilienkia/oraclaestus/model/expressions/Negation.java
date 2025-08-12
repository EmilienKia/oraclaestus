package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;

public class Negation implements Expression{

    Expression expression;

    public Negation(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object apply(EvaluationContext context) {
        Object value = expression.apply(context);
        return switch(value) {
            case Boolean bool -> !bool;
            case Integer num -> -num;
            case Float num -> -num;
            default -> null;
        };
    }

    @Override
    public void dump() {
        System.out.print(" - ");
        expression.dump();
    }
}
