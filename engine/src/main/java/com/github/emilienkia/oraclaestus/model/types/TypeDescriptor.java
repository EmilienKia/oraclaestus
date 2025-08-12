package com.github.emilienkia.oraclaestus.model.types;

public interface TypeDescriptor<T> {

    T cast(Object value);

    Type getType();

}
