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

    Map<String, Entity> entities = new HashMap<>();

    Map<Identifier, Module> modules = new HashMap<>();

    String loggerPrefix;
    Logger simulationLogger;
    Map<String, Logger> entityLoggers = new HashMap<>();

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

    public String addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
        return entity.getId();
    }

    public State getCurrentState(String entityName) {
        Entity entity = entities.get(entityName);
        if (entity == null) {
            throw new IllegalArgumentException("Entity with name " + entityName + " does not exist.");
        }
        return entity.getCurrentState();
    }

    public void start() {
        simulationLogger = LoggerFactory.getLogger((loggerPrefix != null && !loggerPrefix.isBlank() ? loggerPrefix : this.getClass().getName()) + "." + getName());

        for (Entity entity : entities.values()) {
            Logger entityLogger = LoggerFactory.getLogger(simulationLogger.getName() + "." + entity.getId());
            entityLoggers.put(entity.getId(), entityLogger);

            State state = new State();
            ModelEvaluationContext context = new ModelEvaluationContext(
                    this,
                    entity.getModel(),
                    entity,
                    state,
                    state,
                    entityLogger
            );
            for(Map.Entry<Identifier, Variable<?>> entry : entity.getModel().getRegisters().entrySet()) {
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
            entity.setCurrentState(state);
        }
    }

    public void step() {
        Temporal newTime = time.plus(duration);

        Map<Entity, List<Identifier>> differences = new HashMap<>();

        for (Entity entity : entities.values()) {

            ModelEvaluationContext context = new ModelEvaluationContext(
                    this,
                    entity.getModel(),
                    entity,
                    entity.getCurrentState(),
                    entity.getCurrentState().clone(),
                    entityLoggers.get(entity.getId())
            );

            // Apply rules to the entity
            for (RuleGroup rules : context.getEntity().getRuleGroups()) {
                try {
                    rules.apply(context);
                } catch (Return e) {
                    // Skip to next rule group
                }
            }

            // Update the current state of the entity
            entity.setCurrentState(context.getNewState());

            List<Identifier> diff = compareStates(context.getOldState(), context.getNewState());
            if (!diff.isEmpty()) {
                differences.put(entity, diff);
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

    void analyzeDifferences(Map<Entity, List<Identifier>> diff) {
        for (Map.Entry<Entity, List<Identifier>> entry : diff.entrySet()) {
            Entity entity = entry.getKey();
            List<Identifier> changedKeys = entry.getValue();
            for(Identifier key : changedKeys) {
                Object oldValue = entity.getCurrentState().getValue(key);
                Object newValue = entity.getCurrentState().getValue(key);

                for (EventListener listener : eventListeners) {
                    listener.onStateChange(new StateChangeEvent(this, entity, key, oldValue, newValue));
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
        for (Entity entity : entities.values()) {
            System.out.println("Entity ID: " + entity.getId());
            entity.getCurrentState().dump();
        }
    }
}
