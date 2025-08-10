package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import com.github.emilienkia.oraclaestus.model.types.Type;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;

public class GenericVariable extends Variable<Object> {

    TypeDescriptor<?> type;

    public GenericVariable() {
    }

    public GenericVariable(TypeDescriptor<?> type, Identifier name, Expression initialExpression) {
        super(name, initialExpression);
        this.type = type;
    }

    public GenericVariable(TypeDescriptor<?> type, Identifier name, Object defaultValue) {
        super(name, type.cast(defaultValue));
        this.type = type;
    }

    @Override
    public Type getType() {
        return type.getType();
    }

}
