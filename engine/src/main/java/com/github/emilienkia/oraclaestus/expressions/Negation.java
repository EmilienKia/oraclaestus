package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;

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
