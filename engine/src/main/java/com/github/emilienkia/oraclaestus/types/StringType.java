package com.github.emilienkia.oraclaestus.types;

public class StringType implements TypeDescriptor<String> {

    static final StringType INSTANCE = new StringType();

    private StringType() {
    }

    public static StringType get() {
        return INSTANCE;
    }

    @Override
    public String cast(Object value) {
        switch(value) {
            case null:
                return null;
            case String str:
                return str;
            default:
                return value.toString();
        }
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

}
