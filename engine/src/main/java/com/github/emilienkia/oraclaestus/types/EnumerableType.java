package com.github.emilienkia.oraclaestus.types;

import com.github.emilienkia.oraclaestus.Identifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class EnumerableType<T extends EnumerableType<T>.Instance> implements CustomType<T> {

    int next = 0;

    Collection<T> values = new ArrayList<>();

    public Instance get(int value) {
        return values.stream().filter(t -> t.getValue() == value)
                .findFirst()
                .orElse(null);
    }

    public Instance get(Identifier name) {
        if(name == null || !name.isValid()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return get(name.getLast());
    }

    public Instance get(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return values.stream().filter(t -> t.getName() != null && t.getName().equals(name.trim()))
                .findFirst()
                .orElse(null);
    }

    abstract T newInstance(int value, String name);
    public abstract T cast(Object value);

    public EnumerableType<T> add(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if(get(name)!=null) {
            throw new IllegalArgumentException("Name already exists: " + name);
        }
        int value = next++;

        T instance = newInstance(value, name.trim());
        values.add(instance);
        return this;
    }

    public int getCount() {
        return values.size();
    }

    public class Instance {
        @Getter
        int value = 0;

        @Getter
        String name;

        Instance(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public EnumerableType<T> getEnumerable() {
            return EnumerableType.this;
        }
    }

}
