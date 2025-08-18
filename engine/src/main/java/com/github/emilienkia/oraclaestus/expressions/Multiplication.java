package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;


public class Multiplication implements Expression {

    Expression leftExpression;
    Expression rightExpression;

    public Multiplication(Expression leftExpression, Expression rightExpression) {
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
                        yield leftInt * rightInt;
                    }
                    case Double rightFloat -> {
                        yield (int) (leftInt * rightFloat);
                    }
                    case null -> {
                        yield 0L;
                    }
                    default -> {
                        yield null;
                    }
                }
            }

            case Double leftFloat -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        yield leftFloat * rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftFloat * rightFloat;
                    }
                    case null -> {
                        yield 0.0;
                    }
                    default -> {
                        yield null;
                    }
                }
            }

            case String leftStr -> {
                switch (rightValue) {
                    case Integer rightInt -> {
                        yield leftStr.repeat(Math.max(0, rightInt));
                    }
                    case null -> {
                        yield "";
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
        System.out.print(" * ");
        rightExpression.dump();
    }
}