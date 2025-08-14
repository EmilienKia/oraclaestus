package com.github.emilienkia.oraclaestus.rules;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;
import com.github.emilienkia.oraclaestus.expressions.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ExpressionRule implements Rule {

    @Getter
    Expression expression;

    @Override
    public void apply(EvaluationContext context) throws Return {
        expression.apply(context);
    }

    @Override
    public void dump() {
        expression.dump();
        System.out.println();
    }
}
