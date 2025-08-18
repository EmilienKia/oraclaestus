package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;

public class Lesser implements Expression {

    Expression leftExpression;
    Expression rightExpression;

    public Lesser(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Object apply(EvaluationContext context) {
        Object leftValue = leftExpression.apply(context);
        Object rightValue = rightExpression.apply(context);

        if (leftValue == null || rightValue == null) return false;

        return switch (leftValue) {
            case Long leftInt -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        yield leftInt < rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftInt < rightFloat;
                    }
                    default -> {
                        yield false;
                    }
                }
            }
            case Double leftFloat -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        yield leftFloat < rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftFloat < rightFloat;
                    }
                    default -> {
                        yield false;
                    }
                }
            }
            case String leftStr -> {
                switch (rightValue) {
                    case String rightStr -> {
                        yield leftStr.compareTo(rightStr) < 0;
                    }
                    default -> {
                        yield false;
                    }
                }
            }
            default -> {
                yield false;
            }
        };
    }

    @Override
    public void dump() {
        leftExpression.dump();
        System.out.print(" < ");
        rightExpression.dump();
    }
}