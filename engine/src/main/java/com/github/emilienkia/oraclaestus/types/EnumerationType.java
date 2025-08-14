package com.github.emilienkia.oraclaestus.types;

public class EnumerationType extends EnumerableType<EnumerationType.Instance> {

    public EnumerationType() {
    }

    @Override
    public Instance cast(Object value) {
        return switch(value) {
            case Instance val -> val;
            case String name -> {
                if(name.isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be null or empty");
                }
                Integer intValue = names.get(name);
                if(intValue == null) {
                    throw new IllegalArgumentException("Name does not exist in enumeration: " + name);
                }
                yield new Instance(intValue);
            }
            case Integer intValue -> {
                if(!values.containsKey(intValue)) {
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

    public class Instance extends EnumerableType<EnumerationType.Instance>.Instance {

        public Instance() {
        }

        public Instance(int value) {
            super(value);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && obj instanceof Instance instance && instance.value == this.value;
        }

        public EnumerationType getEnumerationType() {
            return EnumerationType.this;
        }

    }

}
