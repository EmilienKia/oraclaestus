package com.github.emilienkia.oraclaestus.variables;

import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.types.EnumerationType;
import com.github.emilienkia.oraclaestus.types.Type;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;

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

    public TypeDescriptor<EnumerationType.Instance> getTypeDescriptor() {
        return new EnumerationType();
    }
}
