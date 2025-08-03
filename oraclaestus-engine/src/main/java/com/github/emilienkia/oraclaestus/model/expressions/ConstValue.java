package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;

public class ConstValue implements Expression {

    private final Object value;

    public ConstValue(Object value) {
        this.value = value;
    }

    @Override
    public Object apply(EvaluationContext context) {
        return value;
    }

    @Override
    public void dump() {
        System.out.print("<<value:" + value + ">>");
    }

}
