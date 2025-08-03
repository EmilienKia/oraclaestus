package com.github.emilienkia.oraclaestus.model.types;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class EnumerationType implements TypeDescriptor<EnumerationType.Instance> {

    int next = 0;

    Map<Integer, String> enumValues = new HashMap<>();
    Map<String, Integer> enumNames = new HashMap<>();

    public EnumerationType() {
    }

    public EnumerationType add(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if(enumNames.containsKey(name)) {
            throw new IllegalArgumentException("Name already exists: " + name);
        }

        int value = next++;

        enumValues.put(value, name);
        enumNames.put(name, value);
        return this;
    }

    public int getCount() {
        return enumValues.size();
    }

    public Integer getValue(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        Integer value = enumNames.get(name);
        if(value == null) {
            return null;
        }
        return value;
    }

    @Override
    public Instance cast(Object value) {
        return switch(value) {
            case Instance val -> val;
            case String name -> {
                if(name.isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be null or empty");
                }
                Integer intValue = enumNames.get(name);
                if(intValue == null) {
                    throw new IllegalArgumentException("Name does not exist in enumeration: " + name);
                }
                yield new Instance(intValue);
            }
            case Integer intValue -> {
                if(!enumValues.containsKey(intValue)) {
                    throw new IllegalArgumentException("Value does not exist in enumeration: " + intValue);
                }
                yield new Instance(intValue);
            }
            case null, default -> {yield null;}
        };
    }

    @Override
    public Type getType() {
        return Type.ENUM;
    }

    public class Instance {
        @Getter
        int value = 0;

        public Instance() {
        }

        public Instance(int value) {
            if(!enumValues.containsKey(value)) {
                throw new IllegalArgumentException("Value does not exist in enumeration: " + value);
            }
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && obj instanceof Instance instance && instance.value == this.value;
        }

        public EnumerationType getEnumeration() {
            return EnumerationType.this;
        }

    }

}
