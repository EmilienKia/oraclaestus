package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.types.Type;
import lombok.Data;

@Data
public class StringVariable extends Variable<String> {

    public StringVariable() {
    }

    public StringVariable(String name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

}
