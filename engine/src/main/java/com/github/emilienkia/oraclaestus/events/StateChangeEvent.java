package com.github.emilienkia.oraclaestus.events;

import com.github.emilienkia.oraclaestus.Entity;
import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.Simulation;

public record StateChangeEvent(
        Simulation simulation,
        Entity entity,
        Identifier stateName,
        Object oldValue,
        Object newValue
) {
}
