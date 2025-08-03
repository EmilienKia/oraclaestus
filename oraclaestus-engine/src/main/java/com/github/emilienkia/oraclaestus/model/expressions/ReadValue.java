package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;
import com.github.emilienkia.oraclaestus.model.types.EnumerationType;

public class ReadValue implements Expression {

    private final String valueName;

    public ReadValue(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public Object apply(EvaluationContext context) {
        return context.getValue(valueName);
    }

    @Override
    public void dump() {
        System.out.print(" <<valueName:" + valueName + ">> ");
    }
}
