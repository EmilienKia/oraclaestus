package com.github.emilienkia.oraclaestus.types;

public class VoidType implements TypeDescriptor<Void> {

    static final VoidType INSTANCE = new VoidType();

    private VoidType() {
        // Private constructor to enforce singleton pattern
    }

    public static VoidType get() {
        return INSTANCE;
    }

    @Override
    public Void cast(Object value) {
        return null;
    }

    @Override
    public Type getType() {
        return Type.VOID;
    }
}
