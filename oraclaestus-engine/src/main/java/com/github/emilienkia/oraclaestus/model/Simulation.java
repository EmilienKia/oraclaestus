package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.events.EventListener;
import com.github.emilienkia.oraclaestus.model.events.StateChangeEvent;
import com.github.emilienkia.oraclaestus.model.modules.Module;
import com.github.emilienkia.oraclaestus.model.modules.maths.MathsModule;
import com.github.emilienkia.oraclaestus.model.rules.Return;
import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import lombok.Data;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.*;

@Data
public class Simulation {

    Temporal time;
    Duration duration;

    Map<String, Asset> assets = new HashMap<>();

    Map<Identifier, Module> modules = new HashMap<>();

    public Simulation(Temporal time, Duration duration) {
        this.time = time;
        this.duration = duration;
    }

    public void addModule(Identifier name, Module module) {
        modules.put(name, module);
    }

    public void registerDefaultModules() {
        addModule(Identifier.fromString("maths"), MathsModule.getModule());
    }

    public String addAsset(Asset asset) {
        assets.put(asset.getId(), asset);
        return asset.getId();
    }

    public State getCurrentState(String assetName) {
        Asset asset = assets.get(assetName);
        if (asset == null) {
            throw new IllegalArgumentException("Asset with name " + assetName + " does not exist.");
        }
        return asset.getCurrentState();
    }

    public void start() {
        for (Asset asset : assets.values()) {
            State state = new State();
            ModelEvaluationContext context = new ModelEvaluationContext(
                    this,
                    asset.getModel(),
                    asset,
                    state,
                    state
            );
            for(Map.Entry<Identifier, Variable<?>> entry : asset.getModel().getRegisters().entrySet()) {
                if(entry.getValue() instanceof Variable<?> variable) {
                    if(variable.getDefaultValue()!=null) {
                        Object defaultValue = variable.createDefaultValue();
                        state.setValue(entry.getKey(), defaultValue);
                    } else if(variable.getInitialExpression()!=null) {
                        Object obj = variable.getInitialExpression().apply(context);
                        state.setValue(entry.getKey(), obj);
                    }
                }
            }
            asset.setCurrentState(state);
        }
    }

    public void step() {
        Temporal newTime = time.plus(duration);

        Map<Asset, List<Identifier>> differences = new HashMap<>();

        for (Asset asset : assets.values()) {

            ModelEvaluationContext context = new ModelEvaluationContext(
                    this,
                    asset.getModel(),
                    asset,
                    asset.getCurrentState(),
                    asset.getCurrentState().clone()
            );

            // Apply rules to the asset
            for (RuleGroup rules : context.getAsset().getRuleGroups()) {
                try {
                    rules.apply(context);
                } catch (Return e) {
                    // Skip to next rule group
                }
            }

            // Update the current state of the asset
            asset.setCurrentState(context.getNewState());

            List<Identifier> diff = compareStates(context.getOldState(), context.getNewState());
            if (!diff.isEmpty()) {
                differences.put(asset, diff);
            }
        }

        // Update the simulation time
        time = newTime;

        // Analyze differences and notify
        analyzeDifferences(differences);
    }

     static List<Identifier> compareStates(State oldState, State newState) {
         List<Identifier> differences = new java.util.ArrayList<>();
         for (Identifier key : oldState.getValues().keySet()) {
             Object oldValue = oldState.getValue(key);
             Object newValue = newState.getValue(key);
             if (!oldValue.equals(newValue)) {
                 differences.add(key);
             }
         }
         return differences;
     }

    void analyzeDifferences(Map<Asset, List<Identifier>> diff) {
        for (Map.Entry<Asset, List<Identifier>> entry : diff.entrySet()) {
            Asset asset = entry.getKey();
            List<Identifier> changedKeys = entry.getValue();
            for(Identifier key : changedKeys) {
                Object oldValue = asset.getCurrentState().getValue(key);
                Object newValue = asset.getCurrentState().getValue(key);

                for (EventListener listener : eventListeners) {
                    listener.onStateChange(new StateChangeEvent(this, asset, key, oldValue, newValue));
                }
            }
        }
    }

    Set<EventListener> eventListeners = new HashSet<>();

    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }
    public void removeEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }





    public void dump() {
        System.out.println("Simulation time: " + time);
        for (Asset asset : assets.values()) {
            System.out.println("Asset ID: " + asset.getId());
            asset.getCurrentState().dump();
        }
    }
}
