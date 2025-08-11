package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.BooleanType;
import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import com.github.emilienkia.oraclaestus.model.types.Type;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;

public class BooleanVariable extends Variable<Boolean> {

    public BooleanVariable() {
    }

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
        return new BooleanType();
    }
}
