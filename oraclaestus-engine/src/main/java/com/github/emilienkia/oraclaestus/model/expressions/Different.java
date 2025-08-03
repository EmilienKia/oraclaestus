package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;

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
            case Integer leftInt -> {
                switch (rightValue) {
                    case Integer rightInt -> {
                        yield !leftInt.equals(rightInt);
                    }
                    case Float rightFloat -> {
                        yield leftInt.floatValue() != rightFloat;
                    }
                    default -> {
                        yield true;
                    }
                }
            }
            case Float leftFloat -> {
                switch (rightValue) {
                    case Integer rightInt -> {
                        yield leftFloat != rightInt.floatValue();
                    }
                    case Float rightFloat -> {
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
