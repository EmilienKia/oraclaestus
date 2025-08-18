package com.github.emilienkia.oraclaestus.functions;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;
import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;
import com.github.emilienkia.oraclaestus.types.VoidType;
import com.github.emilienkia.oraclaestus.variables.Variable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class Function {

    @Getter
    Identifier name;

    @Getter
    TypeDescriptor<?> returnType;

    @Getter
    List<Variable<?>> parameters = new ArrayList<>();

    @Getter
    boolean isVarArgs;

    public Function(String name, TypeDescriptor<?> returnType, List<Variable<?>> parameters, boolean isVarArgs) {
        this(Identifier.fromString(name), returnType, parameters, isVarArgs);
    }

    public Function(Identifier name, TypeDescriptor<?> returnType, List<Variable<?>> parameters, boolean isVarArgs) {
        this.name = name;
        this.returnType = returnType != null ? returnType : VoidType.get();
        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
        this.isVarArgs = isVarArgs;
    }

    public abstract Object apply(EvaluationContext context, List<Object> arguments);

}
