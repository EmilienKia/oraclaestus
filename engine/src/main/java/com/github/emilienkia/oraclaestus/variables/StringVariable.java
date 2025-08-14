package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.StringType;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;
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
