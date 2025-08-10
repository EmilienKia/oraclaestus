package com.github.emilienkia.oraclaestus.model.functions;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public abstract class Function {

    @Getter
    Identifier name;

    @Getter
    TypeDescriptor<?> returnType;

    @Getter
    List<Variable<?>> parameters = new ArrayList<>();

    public abstract Object apply(EvaluationContext context, List<Object> arguments);

}
