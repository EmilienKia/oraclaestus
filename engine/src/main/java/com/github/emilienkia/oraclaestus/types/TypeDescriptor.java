package com.github.emilienkia.oraclaestus.types;

public interface TypeDescriptor<T> {

    T cast(Object value);

    Type getType();

}
