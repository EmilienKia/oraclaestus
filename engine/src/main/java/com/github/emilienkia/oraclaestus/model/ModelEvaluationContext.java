package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import com.github.emilienkia.oraclaestus.model.functions.Function;
import com.github.emilienkia.oraclaestus.model.modules.Module;
import com.github.emilienkia.oraclaestus.model.types.EnumerableType;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModelEvaluationContext extends EvaluationContext {

    Simulation simulation;

    Model model;
    Entity entity;

    State oldState;
    State newState;

    Logger logger;

    public EnumerableType<?>.Instance getEnumerableValue(Identifier name) {
        if(model!=null) {
            return model.getEnumerationValue(name);
        }
        return null;
    }

    @Override
    public Object getMacroValue(Identifier name) {
        if(model != null && model.getMacros().containsKey(name)) {
            Expression macro = model.getMacros().get(name);
            if(macro != null) {
                return macro.apply(this);
            }
        }
        return null;
    }

    @Override
    public Function resolveFunction(Identifier name) {
        if(model != null) {
            Function function = model.getFunction(name);
            if(function != null) {
                return function;
            }
        }
        if(simulation!=null) {
            for(Module module : simulation.getModules().values()) {
                Function function = module.getFunction(name);
                if(function != null) {
                    return function;
                }
            }
        }
        return null;
    }

    @Override
    public Object getValue(Identifier name, boolean old) {
        if(old) {
            return getOldState().getValue(name);
        } else {
            // Look at enum values first
            EnumerableType<?>.Instance value = getEnumerableValue(name);
            if(value != null) {
                return value;
            }

            // Look at macro values
            Object macroValue = getMacroValue(name);
            if(macroValue != null) {
                return macroValue;
            }

            // Otherwise, look at the new state
            return getNewState().getValue(name);
        }
    }

    @Override
    public void setValue(Identifier name, Object value) {
        Variable<?> register = model.getRegister(name);
        if(register==null) {
            throw  new RuntimeException("Model does not contain register: " + name);
        }
        newState.setValue(name, register.getTypeDescriptor().cast(value));
    }

}
