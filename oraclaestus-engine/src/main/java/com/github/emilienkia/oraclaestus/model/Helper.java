package com.github.emilienkia.oraclaestus.model;

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
}
