package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.*;

public class And implements Expression {

    Expression leftExpression;
    Expression rightExpression;

    public And(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Object apply(EvaluationContext context) {
        Object leftValue = leftExpression.apply(context);
        Object rightValue = rightExpression.apply(context);

        boolean leftBool = Helper.toBool(leftValue);
        boolean rightBool = Helper.toBool(rightValue);

        return leftBool && rightBool;
    }

    @Override
    public void dump() {
        leftExpression.dump();
        System.out.print(" && ");
        rightExpression.dump();
    }

}
