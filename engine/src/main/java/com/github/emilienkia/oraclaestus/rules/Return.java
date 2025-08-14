package com.github.emilienkia.oraclaestus.rules;

import lombok.Getter;

public class Return extends Throwable {
    @Getter
    private final Object value;

    public Return(Object value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Return value: " + value;
    }
}
