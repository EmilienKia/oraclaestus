package com.github.emilienkia.oraclaestus.model.functions;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;
import com.github.emilienkia.oraclaestus.model.types.VoidType;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
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
        this.returnType = returnType != null ? returnType : new VoidType();
        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
        this.isVarArgs = isVarArgs;
    }

    public abstract Object apply(EvaluationContext context, List<Object> arguments);

}
