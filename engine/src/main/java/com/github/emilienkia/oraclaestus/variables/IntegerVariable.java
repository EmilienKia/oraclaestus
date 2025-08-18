package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.IntegerType;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;

public class IntegerVariable extends Variable<Long> {

    public IntegerVariable(String name, Long defaultValue) {
        super(new Identifier(name), defaultValue);
    }

    public IntegerVariable(String name, Integer defaultValue) {
        super(new Identifier(name), (long)defaultValue);
    }


    public IntegerVariable(Identifier name, Long defaultValue) {
        super(name, defaultValue);
    }


    public IntegerVariable(Identifier name, Integer defaultValue) {
        super(name, (long)defaultValue);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }

    public TypeDescriptor<Long> getTypeDescriptor() {
        return IntegerType.get();
    }
}
