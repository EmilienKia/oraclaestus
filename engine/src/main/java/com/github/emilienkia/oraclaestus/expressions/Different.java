package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;

public class Different implements Expression {

    Expression leftExpression;
    Expression rightExpression;

    public Different(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Object apply(EvaluationContext context) {
        Object leftValue = leftExpression.apply(context);
        Object rightValue = rightExpression.apply(context);

        if (leftValue == null && rightValue == null) return false;
        if (leftValue == null || rightValue == null) return true;

        return switch (leftValue) {
            case Long leftInt -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        yield !leftInt.equals(rightInt);
                    }
                    case Double rightFloat -> {
                        yield leftInt.floatValue() != rightFloat;
                    }
                    default -> {
                        yield true;
                    }
                }
            }
            case Double leftFloat -> {
                switch (rightValue) {
                    case Long rightInt -> {
                        yield leftFloat != rightInt.floatValue();
                    }
                    case Double rightFloat -> {
                        yield !leftFloat.equals(rightFloat);
                    }
                    default -> {
                        yield true;
                    }
                }
            }
            case String leftStr -> {
                switch (rightValue) {
                    case String rightStr -> {
                        yield !leftStr.equals(rightStr);
                    }
                    default -> {
                        yield true;
                    }
                }
            }
            case Boolean leftBool -> {
                switch (rightValue) {
                    case Boolean rightBool -> {
                        yield !leftBool.equals(rightBool);
                    }
                    default -> {
                        yield true;
                    }
                }
            }
            default -> {
                yield !leftValue.equals(rightValue);
            }
        };
    }

    @Override
    public void dump() {
        leftExpression.dump();
        System.out.print(" != ");
        rightExpression.dump();
    }
}
