package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.BooleanType;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;

public class BooleanVariable extends Variable<Boolean> {

    public BooleanVariable(String name, Boolean defaultValue) {
        super(Identifier.fromString(name), defaultValue);
    }

    public BooleanVariable(Identifier name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    public TypeDescriptor<Boolean> getTypeDescriptor() {
        return BooleanType.get();
    }
}
