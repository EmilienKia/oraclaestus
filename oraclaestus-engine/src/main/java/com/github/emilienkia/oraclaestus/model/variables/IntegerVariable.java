package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.types.Type;

public class IntegerVariable extends Variable<Integer> {

    public IntegerVariable() {
    }

    public IntegerVariable(String name, Integer defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }

}
