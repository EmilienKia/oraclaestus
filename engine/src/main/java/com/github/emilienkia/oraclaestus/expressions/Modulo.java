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
        if (leftValue != null && rightValue != null) {
            return switch (leftValue) {
                case Integer leftInt -> {
                    switch (rightValue) {
                        case Integer rightInt -> {
                            if (rightInt == 0) yield null; // Division by zero
                            yield leftInt % rightInt;
                        }
                        case Float rightFloat -> {
                            if (rightFloat == 0.0f) yield null; // Division by zero
                            yield leftInt % rightFloat;
                        }
                        case null -> {
                            yield null;
                        }
                        default -> {
                            yield null;
                        }
                    }
                }

                case Float leftFloat -> {
                    switch (rightValue) {
                        case Integer rightInt -> {
                            if (rightInt == 0) yield null; // Division by zero
                            yield leftFloat % rightInt;
                        }
                        case Float rightFloat -> {
                            if (rightFloat == 0.0f) yield null; // Division by zero
                            yield leftFloat % rightFloat;
                        }
                        case null -> {
                            yield null;
                        }
                        default -> {
                            yield null;
                        }
                    }
                }

                default -> null;
            };
        } else {
            return null;
        }
    }

    @Override
    public void dump() {
        leftExpression.dump();
        System.out.print(" % ");
        rightExpression.dump();
    }
}