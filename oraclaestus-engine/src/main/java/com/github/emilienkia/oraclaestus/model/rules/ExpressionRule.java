package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import com.github.emilienkia.oraclaestus.model.expressions.FunctionCall;
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
