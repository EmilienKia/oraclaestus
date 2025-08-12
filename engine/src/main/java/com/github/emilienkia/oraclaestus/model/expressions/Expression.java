package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.*;

public interface Expression extends Dumpable {
    Object apply(EvaluationContext context);
}
