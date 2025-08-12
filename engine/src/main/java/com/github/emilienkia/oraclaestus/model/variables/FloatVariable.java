package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.FloatType;
import com.github.emilienkia.oraclaestus.model.types.Type;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;

public class FloatVariable extends Variable<Float> {

    public FloatVariable() {
    }

    public FloatVariable(String name, Float defaultValue) {
        super(Identifier.fromString(name), defaultValue);
    }

    public FloatVariable(Identifier name, Float defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }

    public TypeDescriptor<Float> getTypeDescriptor() {
        return new FloatType();
    }
}
