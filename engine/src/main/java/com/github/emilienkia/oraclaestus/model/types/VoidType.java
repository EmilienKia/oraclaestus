package com.github.emilienkia.oraclaestus.model.types;

public class VoidType implements TypeDescriptor<Void> {


    @Override
    public Void cast(Object value) {
        return null;
    }

    @Override
    public Type getType() {
        return Type.VOID;
    }
}
