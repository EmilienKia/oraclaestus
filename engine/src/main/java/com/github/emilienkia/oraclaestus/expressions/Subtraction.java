package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;

public class Subtraction implements Expression {

    Expression leftExpression;
    Expression rightExpression;

    public Subtraction(Expression leftExpression, Expression rightExpression) {
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
                        yield leftInt - rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftInt - rightFloat;
                    }
                    case null -> {
                        yield leftInt;
                    }
                    default -> {
                        yield null;
                    }
                }
            }

            case Double leftFloat -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        yield leftFloat - rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftFloat - rightFloat;
                    }
                    case null -> {
                        yield leftFloat;
                    }
                    default -> {
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
        System.out.print(" - ");
        rightExpression.dump();
    }
}
