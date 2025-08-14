package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;
import com.github.emilienkia.oraclaestus.Helper;
import lombok.Getter;

public class Conditional implements Expression {
    @Getter
    private final Expression condition;
    @Getter
    private final Expression trueExpression;
    @Getter
    private final Expression falseExpression;

    public Conditional(Expression condition, Expression trueExpression, Expression falseExpression) {
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
