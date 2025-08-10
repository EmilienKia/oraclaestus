package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import com.github.emilienkia.oraclaestus.model.types.Type;

public class EnumVariable  extends Variable<EnumerationType.Instance> {

    public EnumVariable() {
    }

    public EnumVariable(Identifier name, EnumerationType.Instance defaultValue) {
        super(name, defaultValue);
    }

    public EnumVariable(Identifier name, EnumerationType enumeration, String defaultValue) {
        super(name, enumeration.cast(defaultValue));
    }

    @Override
    public Type getType() {
        return Type.ENUM;
    }

}
