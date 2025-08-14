package com.github.emilienkia.oraclaestus.types;

public class IntegerType extends NumberType<Integer> {

    @Override
    public Integer cast(Object value) {
        switch(value) {
            case null:
                return null;
            case Integer i:
                return i;
            case Long l:
                return l.intValue();
            case Double d:
                return d.intValue();
            case Float f:
                return f.intValue();
            case String str:
                try {
                    return Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot convert String to Integer: " + value, e);
                }
            case Boolean b:
                return b ? 1 : 0; // Convert Boolean to Integer (true -> 1, false -> 0)
            default:
                throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to Integer");
        }
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }
}
