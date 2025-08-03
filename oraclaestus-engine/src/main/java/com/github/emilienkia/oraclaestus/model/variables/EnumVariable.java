package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import com.github.emilienkia.oraclaestus.model.types.Type;

public class EnumVariable  extends Variable<EnumerationType.Instance> {

    public EnumVariable() {
    }

    public EnumVariable(String name, EnumerationType.Instance defaultValue) {
        super(name, defaultValue);
    }

    public EnumVariable(String name, EnumerationType enumeration, String defaultValue) {
        super(name, enumeration.cast(defaultValue));
    }

    @Override
    public Type getType() {
        return Type.ENUM;
    }

}
