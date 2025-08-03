package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.*;

public class Ternary implements Expression {
    private final Expression condition;
    private final Expression trueExpression;
    private final Expression falseExpression;

    public Ternary(Expression condition, Expression trueExpression, Expression falseExpression) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    @Override
    public Object apply(EvaluationContext context) {
        Object cond = condition.apply(context);
        return Helper.toBool(cond) ? trueExpression.apply(context) : falseExpression.apply(context);
    }

    @Override
    public void dump() {
        condition.dump();
        System.out.print(" ? ");
        trueExpression.dump();
        System.out.print(" + ");
        falseExpression.dump();
    }

}
