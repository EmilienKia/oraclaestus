package com.github.emilienkia.oraclaestus.model.variables;

import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.StateType;
import com.github.emilienkia.oraclaestus.model.types.StringType;
import com.github.emilienkia.oraclaestus.model.types.Type;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;

public class StateVariable extends Variable<StateType.Instance> {

    public StateVariable() {
    }

    public StateVariable(Identifier name, StateType.Instance defaultValue) {
        super(name, defaultValue);
    }

    public StateVariable(Identifier name, StateType state, String defaultValue) {
        super(name, state.cast(defaultValue));
    }

    @Override
    public Type getType() {
        return Type.STATE;
    }

    public TypeDescriptor<StateType.Instance> getTypeDescriptor() {
        return new StateType();
    }

}
