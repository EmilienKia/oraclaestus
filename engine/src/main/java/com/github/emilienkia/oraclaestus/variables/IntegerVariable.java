package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.IntegerType;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;

public class IntegerVariable extends Variable<Integer> {

    public IntegerVariable() {
    }

    public IntegerVariable(String name, Integer defaultValue) {
        super(new Identifier(name), defaultValue);
    }


    public IntegerVariable(Identifier name, Integer defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }

    public TypeDescriptor<Integer> getTypeDescriptor() {
        return new IntegerType();
    }
}
