package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.types.Type;

public class FloatVariable extends Variable<Float> {

    public FloatVariable() {
    }

    public FloatVariable(String name, Float defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }
}
