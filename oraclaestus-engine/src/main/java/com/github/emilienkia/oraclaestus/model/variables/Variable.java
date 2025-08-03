package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.types.Type;
import lombok.Data;

@Data
public abstract class Variable<T> {

    Variable() {
    }

    Variable(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    String name;
    T defaultValue;

    public abstract Type getType();

    public Object createDefaultValue() {
        return defaultValue;
    }

}
