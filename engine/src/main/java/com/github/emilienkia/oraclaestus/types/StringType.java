package com.github.emilienkia.oraclaestus.types;

public class StringType implements TypeDescriptor<String> {

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
