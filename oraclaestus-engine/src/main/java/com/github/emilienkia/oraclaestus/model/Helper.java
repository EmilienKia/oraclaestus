package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.types.*;

public class Helper {
    static public  boolean toBool(Object value) {
        return switch (value) {
            case Boolean bool -> bool;
            case Integer intVal -> intVal != 0;
            case Float floatVal -> floatVal != 0.0f;
            case String strVal -> !strVal.isEmpty();
            case null -> false;
            default -> false;
        };
    }

    static public TypeDescriptor<?> toTypeDescriptor(Class<?> clazz) {
        if (clazz == Integer.class || clazz == int.class || clazz == Long.class || clazz == long.class) {
            return new IntegerType();
        } else if (clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class) {
            return new FloatType();
        } else if (clazz == String.class) {
            return new StringType();
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return new BooleanType();
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }

    static public TypeDescriptor<?> toTypeDescriptorStrict(Class<?> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return new IntegerType();
        } else if (clazz == Float.class || clazz == float.class) {
            return new FloatType();
        } else if (clazz == String.class) {
            return new StringType();
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return new BooleanType();
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }
}
