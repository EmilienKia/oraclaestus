package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.expressions.FunctionCall;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FunctionCallRule implements Rule {

    @Getter
    FunctionCall functionCall;

    @Override
    public void apply(EvaluationContext context) throws Return {
        functionCall.apply(context);
    }

    @Override
    public void dump() {
        functionCall.dump();
        System.out.println();
    }
}
