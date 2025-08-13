package com.github.emilienkia.oraclaestus.model.events;

import com.github.emilienkia.oraclaestus.model.Entity;
import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.Simulation;

public record StateChangeEvent(
        Simulation simulation,
        Entity entity,
        Identifier stateName,
        Object oldValue,
        Object newValue
) {
}
