package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;


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
        if (leftValue != null && rightValue != null) {
            return switch (leftValue) {
                case Integer leftInt -> {
                    switch (rightValue) {
                        case Integer rightInt -> {
                            yield leftInt * rightInt;
                        }
                        case Float rightFloat -> {
                            yield (int) (leftInt * rightFloat);
                        }
                        case null -> {
                            yield 0;
                        }
                        default -> {
                            yield null;
                        }
                    }
                }

                case Float leftFloat -> {
                    switch (rightValue) {
                        case Integer rightInt -> {
                            yield leftFloat * rightInt;
                        }
                        case Float rightFloat -> {
                            yield leftFloat * rightFloat;
                        }
                        case null -> {
                            yield 0.0f;
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
        } else {
            return null;
        }
    }

    @Override
    public void dump() {
        leftExpression.dump();
        System.out.print(" * ");
        rightExpression.dump();
    }
}