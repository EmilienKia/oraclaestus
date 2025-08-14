package com.github.emilienkia.oraclaestus.model;

import java.util.HashMap;
import java.util.Map;


public class SimulationState implements Cloneable{

    Map<String, EntityState> states = new HashMap<>();

    @Override
    public SimulationState clone() {
        try {
            SimulationState clone = (SimulationState) super.clone();
            clone.states = new HashMap<>();
            for (Map.Entry<String, EntityState> entry : this.states.entrySet()) {
                clone.states.put(entry.getKey(), entry.getValue().clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void setEntityState(String entityId, EntityState entityState) {
        states.put(entityId, entityState);
    }


    public EntityState getEntityState(String entityId) {
        return states.get(entityId);
    }

    public Object getEntityValue(String entityId, String key) {
        EntityState entityState = states.get(entityId);
        if (entityState != null) {
            return entityState.getValue(key);
        }
        return null;
    }

    public Object getEntityValue(String entityId, Identifier key) {
        EntityState entityState = states.get(entityId);
        if (entityState != null) {
            return entityState.getValue(key);
        }
        return null;
    }

}
