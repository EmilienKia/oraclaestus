package com.github.emilienkia.oraclaestus.types;

public class FloatType extends NumberType<Double> {

    static final FloatType INSTANCE = new FloatType();

    private FloatType() {
    }

    public static FloatType get() {
        return INSTANCE;
    }

    @Override
    public Double cast(Object value) {
        switch(value) {
            case null:
                return null;
            case Float f:
                return f.doubleValue();
            case Double d:
                return d;
            case Long l:
                return l.doubleValue();
            case Integer i:
                return i.doubleValue();
            case String str:
                try {
                    return Double.parseDouble(str);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot convert String to Float: " + value, e);
                }
            case Boolean b:
                return b ? 1.0 : 0.0; // Convert Boolean to Float (true -> 1.0, false -> 0.0)
            default:
                throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to Float");
        }
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }
}
