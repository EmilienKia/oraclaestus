package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.*;
import lombok.Getter;

public class ReadValue implements Expression {

    @Getter
    private final Identifier identifier;

    public ReadValue(Identifier identifier) {
        this.identifier = identifier;
    }

    public ReadValue(String valueName) {
        identifier = Identifier.fromString(valueName);
    }

    @Override
    public Object apply(EvaluationContext context) {
        return context.getValue(identifier);

    }

    @Override
    public void dump() {
        System.out.print(" <<id:" + identifier + ">> ");
    }
}
