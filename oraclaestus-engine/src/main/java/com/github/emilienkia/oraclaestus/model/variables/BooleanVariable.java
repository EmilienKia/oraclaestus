package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.types.Type;

public class BooleanVariable extends Variable<Boolean> {

    public BooleanVariable() {
    }

    public BooleanVariable(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }
}
