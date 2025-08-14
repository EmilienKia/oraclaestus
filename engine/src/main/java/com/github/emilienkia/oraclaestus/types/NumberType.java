package com.github.emilienkia.oraclaestus.types;

public class NumberType<T extends Number> implements TypeDescriptor<T> {

    @Override
    public T cast(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return (T) value;
        }
        if (value instanceof String) {
            try {
                return (T) Double.valueOf((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert String to Number: " + value, e);
            }
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to Number");
    }

    @Override
    public Type getType() {
        return Type.NUMBER;
    }


}
