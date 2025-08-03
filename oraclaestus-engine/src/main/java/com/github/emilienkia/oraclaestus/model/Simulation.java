package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.rules.Rule;
import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import lombok.Data;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;

@Data
public class Simulation {

    Temporal time;
    Duration duration;

    Map<String, Asset> assets = new HashMap<>();

    public Simulation(Temporal time, Duration duration) {
        this.time = time;
        this.duration = duration;
    }

    public void addAsset(Asset asset) {
        assets.put(asset.getId(), asset);
    }

    public void step() {
        Temporal newTime = time.plus(duration);

        for (Asset asset : assets.values()) {

            EvaluationContext context = new EvaluationContext(
                    null,
                    asset.getModel(),
                    asset,
                    asset.getCurrentState(),
                    asset.getCurrentState().clone()
            );

            // Apply rules to the asset
            for (RuleGroup rules : context.getAsset().getRuleGroups()) {
                rules.apply(context);
            }

            // Update the current state of the asset
            asset.setCurrentState(context.getNewState());
        }

        // Update the simulation time
        time = newTime;
    }

    public void dump() {
        System.out.println("Simulation time: " + time);
        for (Asset asset : assets.values()) {
            System.out.println("Asset ID: " + asset.getId());
            asset.getCurrentState().dump();
        }
    }
}
