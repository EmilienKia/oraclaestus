package com.github.emilienkia.oraclaestus.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class State implements Cloneable{

    Map<Identifier, Object> values = new HashMap<>();

    @Override
    public State clone() {
        try {
            State clone = (State) super.clone();
            clone.values = new HashMap<>(this.values);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Object getValue(String key) {
        return getValue(Identifier.fromString(key));
    }

    public Object getValue(Identifier key) {
        return values.get(key);
    }

    public void setValue(Identifier key, Object value) {
        values.put(key, value);
    }

    public void dump() {
        System.out.println("State dump:");
        for (Map.Entry<Identifier, Object> entry : values.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

}
