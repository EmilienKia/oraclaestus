package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;
import lombok.Getter;

public class Addition implements Expression {

    @Getter
    Expression leftExpression;
    @Getter
    Expression rightExpression;

    public Addition(Expression leftExpression, Expression rightExpression) {
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
                        yield leftInt + rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftInt + rightFloat;
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
                        yield leftFloat + rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftFloat + rightFloat;
                    }
                    case null -> {
                        yield leftFloat;
                    }
                    default -> {
                        yield null;
                    }
                }
            }

            case String leftStr -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        yield leftStr + rightInt;
                    }
                    case Double rightFloat -> {
                        yield leftStr + rightFloat;
                    }
                    case String rightStr -> {
                        yield leftStr + rightStr;
                    }
                    case null -> {
                        yield leftStr;
                    }
                    default -> {
                        yield null;
                    }
                }
            }

            case null -> {
                yield rightValue;
            }

            default -> null;
        };
    }

    @Override
    public void dump() {
        leftExpression.dump();
        System.out.print(" + ");
        rightExpression.dump();
    }
}
