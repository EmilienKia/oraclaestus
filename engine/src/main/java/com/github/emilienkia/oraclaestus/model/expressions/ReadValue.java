package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.*;
import lombok.Getter;

public class ReadValue implements Expression {

    @Getter
    private boolean old = false;

    @Getter
    private final Identifier identifier;

    public ReadValue(Identifier identifier) {
        this.identifier = identifier;
    }

    public ReadValue(boolean isOld, Identifier identifier) {
        this.old = isOld;
        this.identifier = identifier;
    }

    public ReadValue(String valueName) {
        valueName = valueName.trim();
        if(valueName.startsWith("~")) {
            this.old = true;
            valueName = valueName.substring(1).trim();
        }
        identifier = Identifier.fromString(valueName);
    }

    public ReadValue(boolean isOld, String valueName) {
        this(valueName);
        old = isOld;
    }

    @Override
    public Object apply(EvaluationContext context) {
        return context.getValue(identifier, old);

    }

    @Override
    public void dump() {
        System.out.print(" <<id:" + identifier + ">> ");
    }
}
