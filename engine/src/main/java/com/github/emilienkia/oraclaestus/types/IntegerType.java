package com.github.emilienkia.oraclaestus.types;

public class IntegerType extends NumberType<Long> {

    static final IntegerType INSTANCE = new IntegerType();

    private IntegerType() {
    }

    public static IntegerType get() {
        return INSTANCE;
    }

    @Override
    public Long cast(Object value) {
        switch(value) {
            case null:
                return null;
            case Integer i:
                return i.longValue();
            case Long l:
                return l;
            case Double d:
                return d.longValue();
            case Float f:
                return f.longValue();
            case String str:
                try {
                    return Long.parseLong(str);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot convert String to Integer: " + value, e);
                }
            case Boolean b:
                return b ? 1L : 0L; // Convert Boolean to Integer (true -> 1, false -> 0)
            default:
                throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to Integer");
        }
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }
}
