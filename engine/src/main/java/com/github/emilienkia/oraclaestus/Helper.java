package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.types.*;

public class Helper {
    static public  boolean toBool(Object value) {
        return switch (value) {
            case Boolean bool -> bool;
            case Integer intVal -> intVal != 0;
            case Float floatVal -> floatVal != 0.0f;
            case String strVal -> !strVal.isEmpty();
            case null, default -> false;
        };
    }

    static public TypeDescriptor<?> toTypeDescriptor(Class<?> clazz) {
        if (clazz == Integer.class || clazz == int.class || clazz == Long.class || clazz == long.class) {
            return IntegerType.get();
        } else if (clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class) {
            return FloatType.get();
        } else if (clazz == Number.class) {
            return new NumberType<>();
        } else if (clazz == String.class) {
            return StringType.get();
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return BooleanType.get();
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }

    static public TypeDescriptor<?> toTypeDescriptorStrict(Class<?> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return IntegerType.get();
        } else if (clazz == Float.class || clazz == float.class) {
            return FloatType.get();
        } else if (clazz == String.class) {
            return StringType.get();
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return BooleanType.get();
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }
}
