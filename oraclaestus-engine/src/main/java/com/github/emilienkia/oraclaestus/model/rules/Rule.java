package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.*;

public interface Rule extends Dumpable {
    void apply(EvaluationContext context);
}
