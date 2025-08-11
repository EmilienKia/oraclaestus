package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import com.github.emilienkia.oraclaestus.model.types.Type;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;
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
