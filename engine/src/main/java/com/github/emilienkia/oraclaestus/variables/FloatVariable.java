package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.FloatType;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;

public class FloatVariable extends Variable<Double> {

    public FloatVariable(String name, Double defaultValue) {
        super(Identifier.fromString(name), defaultValue);
    }

    public FloatVariable(String name, Float defaultValue) {
        super(Identifier.fromString(name), (double)defaultValue);
    }

    public FloatVariable(Identifier name, Double defaultValue) {
        super(name, defaultValue);
    }


    public FloatVariable(Identifier name, Float defaultValue) {
        super(name, (double)defaultValue);
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }

    public TypeDescriptor<Double> getTypeDescriptor() {
        return FloatType.get();
    }
}
