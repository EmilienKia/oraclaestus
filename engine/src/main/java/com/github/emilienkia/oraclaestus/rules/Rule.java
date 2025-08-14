package com.github.emilienkia.oraclaestus.rules;

import com.github.emilienkia.oraclaestus.Dumpable;
import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;

public interface Rule extends Dumpable {
    void apply(EvaluationContext context) throws Return;
}
