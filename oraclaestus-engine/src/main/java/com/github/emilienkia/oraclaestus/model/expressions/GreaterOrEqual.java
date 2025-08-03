package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;

public class GreaterOrEqual implements Expression {

    Expression leftExpression;
    Expression rightExpression;

    public GreaterOrEqual(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Object apply(EvaluationContext context) {
        Object leftValue = leftExpression.apply(context);
        Object rightValue = rightExpression.apply(context);

        if (leftValue == null || rightValue == null) return false;

        return switch (leftValue) {
            case Integer leftInt -> {
                switch (rightValue) {
                    case Integer rightInt -> {
                        yield leftInt >= rightInt;
                    }
                    case Float rightFloat -> {
                        yield leftInt >= rightFloat;
                    }
                    default -> {
                        yield false;
                    }
                }
            }
            case Float leftFloat -> {
                switch (rightValue) {
                    case Integer rightInt -> {
                        yield leftFloat >= rightInt;
                    }
                    case Float rightFloat -> {
                        yield leftFloat >= rightFloat;
                    }
                    default -> {
                        yield false;
                    }
                }
            }
            case String leftStr -> {
                switch (rightValue) {
                    case String rightStr -> {
                        yield leftStr.compareTo(rightStr) >= 0;
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
        System.out.print(" >= ");
        rightExpression.dump();
    }
}