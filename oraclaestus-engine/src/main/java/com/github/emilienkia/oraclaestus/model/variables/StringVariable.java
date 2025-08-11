package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.StringType;
import com.github.emilienkia.oraclaestus.model.types.Type;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;
import lombok.Data;

@Data
public class StringVariable extends Variable<String> {

    public StringVariable() {
    }

    public StringVariable(String name, String defaultValue) {
        super(Identifier.fromString(name), defaultValue);
    }

    public StringVariable(Identifier name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    public TypeDescriptor<String> getTypeDescriptor() {
        return new StringType();
    }

}
