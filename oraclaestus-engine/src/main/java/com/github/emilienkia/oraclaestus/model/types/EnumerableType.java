package com.github.emilienkia.oraclaestus.model.types;

import com.github.emilienkia.oraclaestus.model.Identifier;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public abstract class EnumerableType<T> implements TypeDescriptor<T> {

    int next = 0;

    Map<Integer, String> values = new HashMap<>();
    Map<String, Integer> names = new HashMap<>();

    public EnumerableType<T> add(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if(names.containsKey(name)) {
            throw new IllegalArgumentException("Name already exists: " + name);
        }

        int value = next++;

        values.put(value, name);
        names.put(name, value);
        return this;
    }

    public int getCount() {
        return values.size();
    }

    public Integer getValue(Identifier name) {
        if(name == null || name.isValid()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        Integer value = names.get(name.getLast());
        if(value == null) {
            return null;
        }
        return value;
    }

    public Integer getValue(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        Integer value = names.get(name.trim());
        if(value == null) {
            return null;
        }
        return value;
    }

    public abstract T cast(Object value);


    public class Instance {
        @Getter
        int value = 0;


        public Instance() {
        }

        public Instance(int value) {
            if(!values.containsKey(value)) {
                throw new IllegalArgumentException("Value does not exist in enumeration: " + value);
            }
            this.value = value;
        }

        public EnumerableType<T> getEnumerable() {
            return EnumerableType.this;
        }

    }

}
