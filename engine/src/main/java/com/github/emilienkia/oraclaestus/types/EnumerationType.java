package com.github.emilienkia.oraclaestus.types;

import com.github.emilienkia.oraclaestus.Identifier;
import lombok.Getter;

public class EnumerationType extends EnumerableType<EnumerationType.Instance> {

    static int count = 0;

    @Getter
    Identifier enumTypeName;

    public EnumerationType() {
        this("anonym-enum-" + count++);
    }

    public EnumerationType(String enumTypeName) {
        this(Identifier.fromString(enumTypeName));
    }

    public EnumerationType(Identifier enumTypeName) {
        if(enumTypeName == null || !enumTypeName.isValid()) {
            throw new IllegalArgumentException("Enumeration type name cannot be null or empty");
        }
        this.enumTypeName = enumTypeName;
    }

    @Override
    EnumerationType.Instance newInstance(int value, String name) {
        return new EnumerationType.Instance(value, name);
    }

    @Override
    public Instance cast(Object value) {
        return switch(value) {
            case Instance val -> val;
            case String name -> (Instance) get(name);
            case Integer intValue -> (Instance) get(intValue);
            case null, default -> null;
        };
    }

    @Override
    public Instance get(int value) {
        return (Instance)super.get(value);
    }

    @Override
    public Instance get(Identifier name) {
        return (Instance)super.get(name);
    }

    @Override
    public Instance get(String name) {
        return (Instance)super.get(name);
    }

    @Override
    public Type getType() {
        return Type.ENUM;
    }

    public class Instance extends EnumerableType<EnumerationType.Instance>.Instance {

        public Instance(int value, String name) {
            super(value, name);
        }

        public EnumerationType getEnumerationType() {
            return EnumerationType.this;
        }

    }

}
