package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.events.EventListener;
import com.github.emilienkia.oraclaestus.model.events.StateChangeEvent;
import com.github.emilienkia.oraclaestus.model.modules.LogModule;
import com.github.emilienkia.oraclaestus.model.modules.Module;
import com.github.emilienkia.oraclaestus.model.modules.MathsModule;
import com.github.emilienkia.oraclaestus.model.rules.Return;
import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;

@Data
@Slf4j
public class Simulation {

    Temporal time;
    Duration duration;

    String name;

    Map<String, Asset> assets = new HashMap<>();

    Map<Identifier, Module> modules = new HashMap<>();

    String loggerPrefix;
    Logger simulationLogger;
    Map<String, Logger> assetLoggers = new HashMap<>();

    public Simulation() {
        this.time = LocalDateTime.now();
        this.duration = Duration.ofSeconds(1);
    }

    public Simulation(Temporal time, Duration duration) {
        this.time = time;
        this.duration = duration;
    }

    public Simulation(Temporal time, Duration duration, String name) {
        this.time = time;
        this.duration = duration;
        setName(name);
    }

    static int counter = 0;
    private static String generateName() {

        return String.format("simu-%04x", counter++);
    }

    public void setName(String name) {
        this.name = name.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    public String getName() {
        if (name == null || name.isBlank()) {
            return generateName();
        }
        return name;
    }

    public void addModule(Identifier name, Module module) {
        modules.put(name, module);
    }

    public void registerDefaultModules() {
        addModule(Identifier.fromString("maths"), MathsModule.getModule());
        addModule(Identifier.fromString("log"), LogModule.getModule());
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
        simulationLogger = LoggerFactory.getLogger((loggerPrefix != null && !loggerPrefix.isBlank() ? loggerPrefix : this.getClass().getName()) + "." + getName());

        for (Asset asset : assets.values()) {
            Logger assetLogger = LoggerFactory.getLogger(simulationLogger.getName() + "." + asset.getId());
            assetLoggers.put(asset.getId(), assetLogger);

            State state = new State();
            ModelEvaluationContext context = new ModelEvaluationContext(
                    this,
                    asset.getModel(),
                    asset,
                    state,
                    state,
                    assetLogger
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
                    asset.getCurrentState().clone(),
                    assetLoggers.get(asset.getId())
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
