package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.FloatType;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;

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
