package com.github.emilienkia.oraclaestus.types;

public class BooleanType implements TypeDescriptor<Boolean> {

    static final BooleanType INSTANCE = new BooleanType();

    private BooleanType() {
    }

    public static BooleanType get() {
        return INSTANCE;
    }

    @Override
    public Boolean cast(Object value) {
        switch(value) {
            case null:
                return null;
            case Boolean bool:
                return bool;
            case String str:
                return Boolean.parseBoolean(str);
            case Integer i:
                return i != 0; // Convert Integer to Boolean (0 -> false, non-zero -> true)
            case Long l:
                return l != 0L; // Convert Long to Boolean (0L -> false, non-zero -> true)
            case Float f:
                return f != 0.0f; // Convert Float to Boolean (0.0f -> false, non-zero -> true)
            case Double d:
                return d != 0.0; // Convert Double to Boolean (0.0 -> false, non-zero -> true)
            default:
                throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to Boolean");

        }
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }
}
