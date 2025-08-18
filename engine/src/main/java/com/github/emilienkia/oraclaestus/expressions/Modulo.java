package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;

public class Modulo implements Expression {

    Expression leftExpression;
    Expression rightExpression;

    public Modulo(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Object apply(EvaluationContext context) {
        Object leftValue = leftExpression.apply(context);
        Object rightValue = rightExpression.apply(context);

        return switch (leftValue) {
            case Long leftInt -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        if (rightInt == 0) yield null; // Division by zero
                        yield leftInt % rightInt;
                    }
                    case Double rightFloat -> {
                        if (rightFloat == 0.0f) yield null; // Division by zero
                        yield leftInt % rightFloat;
                    }
                    case null, default -> {
                        yield null;
                    }
                }
            }

            case Double leftFloat -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        if (rightInt == 0) yield null; // Division by zero
                        yield leftFloat % rightInt;
                    }
                    case Double rightFloat -> {
                        if (rightFloat == 0.0f) yield null; // Division by zero
                        yield leftFloat % rightFloat;
                    }
                    case null, default -> {
                        yield null;
                    }
                }
            }

            default -> null;
        };
    }

    @Override
    public void dump() {
        leftExpression.dump();
        System.out.print(" % ");
        rightExpression.dump();
    }
}