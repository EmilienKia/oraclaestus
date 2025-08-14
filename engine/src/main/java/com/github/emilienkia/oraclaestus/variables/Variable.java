package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.expressions.Expression;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;
import lombok.Data;

@Data
public abstract class Variable<T> {

    Variable() {
    }

    Variable(Identifier name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    Variable(Identifier name, Expression initialExpression) {
        this.name = name;
        this.initialExpression = initialExpression;
    }

    Identifier name;
    T defaultValue;
    Expression initialExpression;

    public abstract Type getType();

    public abstract TypeDescriptor<T> getTypeDescriptor();

    public Object createDefaultValue() {
        return defaultValue;
    }

}
