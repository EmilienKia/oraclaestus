package com.github.emilienkia.oraclaestus.rules;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;
import com.github.emilienkia.oraclaestus.expressions.Expression;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ReturnRule implements Rule{

    @Getter
    Expression expression;

    @Override
    public void apply(EvaluationContext context) throws Return {
        Object value = null;
        if(expression !=null) {
            value = expression.apply(context);
        }
        throw new Return(value);
    }

    @Override
    public void dump() {
        System.out.print("return ");
        expression.dump();
        System.out.println();
    }
}
