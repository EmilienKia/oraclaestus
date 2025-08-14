package com.github.emilienkia.oraclaestus.expressions;

import com.github.emilienkia.oraclaestus.Dumpable;
import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;

public interface Expression extends Dumpable {
    Object apply(EvaluationContext context);
}
