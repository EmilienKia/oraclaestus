package com.github.emilienkia.oraclaestus.model.events;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.Simulation;

public record StateChangeEvent(
        Simulation simulation,
        Asset asset,
        Identifier stateName,
        Object oldValue,
        Object newValue
) {
}
