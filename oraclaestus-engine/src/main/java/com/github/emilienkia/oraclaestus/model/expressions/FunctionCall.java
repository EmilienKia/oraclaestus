package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.functions.Function;
import com.github.emilienkia.oraclaestus.model.functions.RuleGroupFunction;
import com.github.emilienkia.oraclaestus.model.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class FunctionCall implements Expression {

    @Getter
    Identifier functionName;

    @Getter
    List<Expression> arguments;

    @Override
    public Object apply(EvaluationContext context) {
        Function func = context.resolveFunction(functionName);
        if (func == null) {
            throw new IllegalArgumentException("Function '" + functionName + "' not found in the context.");
        }

        List<Object> evaluatedArgs = arguments.stream()
                .map(arg -> arg.apply(context))
                .toList();

        return func.apply(context, evaluatedArgs);
    }

    @Override
    public void dump() {

    }
}
