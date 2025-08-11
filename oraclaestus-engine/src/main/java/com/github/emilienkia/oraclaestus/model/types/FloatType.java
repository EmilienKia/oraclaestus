package com.github.emilienkia.oraclaestus.model.types;

public class FloatType extends NumberType<Float> {

    @Override
    public Float cast(Object value) {
        switch(value) {
            case null:
                return null;
            case Float f:
                return f;
            case Double d:
                return d.floatValue();
            case Long l:
                return l.floatValue();
            case Integer i:
                return i.floatValue();
            case String str:
                try {
                    return Float.parseFloat(str);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot convert String to Float: " + value, e);
                }
            case Boolean b:
                return b ? 1.0f : 0.0f; // Convert Boolean to Float (true -> 1.0, false -> 0.0)
            default:
                throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to Float");
        }
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }
}
