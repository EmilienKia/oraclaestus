package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.expressions.Expression;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;

public class GenericVariable extends Variable<Object> {

    TypeDescriptor<?> type;

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

    @Override
    public TypeDescriptor<Object> getTypeDescriptor() {
        return (TypeDescriptor<Object>)type;
    }
}
