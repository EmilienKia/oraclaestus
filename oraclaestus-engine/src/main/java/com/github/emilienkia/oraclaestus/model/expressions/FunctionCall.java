package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.functions.Function;
import com.github.emilienkia.oraclaestus.model.Identifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
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

        // Ensure we call the function with enough parameters with good type
        List<Object> evaluatedArgs = new ArrayList<>(arguments.size());
        int argCount = Math.min(func.getParameters().size(), arguments.size());
        int i = 0;
        for(; i<argCount; i++) {
            Expression arg = arguments.get(i);
            // Evaluate then cast arguments
            Object evaluatedArg = arg.apply(context);
            Object castedArg = func.getParameters().get(i).getTypeDescriptor().cast(evaluatedArg);
            evaluatedArgs.add(castedArg);
        }
        for(; i<func.getParameters().size(); i++) {
            // Fill missing arguments with default values
            evaluatedArgs.add(func.getParameters().get(i).createDefaultValue());
        }
        if(func.isVarArgs()) {
            // If the function is varargs, we can pass the remaining arguments as an array
            for (; i < arguments.size(); i++) {
                evaluatedArgs.add(arguments.get(i).apply(context));
            }
        }

        return func.apply(context, evaluatedArgs);
    }

    @Override
    public void dump() {

    }
}
