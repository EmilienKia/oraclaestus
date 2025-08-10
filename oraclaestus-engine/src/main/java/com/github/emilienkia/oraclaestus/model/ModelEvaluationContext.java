package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import com.github.emilienkia.oraclaestus.model.functions.Function;
import com.github.emilienkia.oraclaestus.model.modules.Module;
import com.github.emilienkia.oraclaestus.model.types.EnumerableType;
import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import com.github.emilienkia.oraclaestus.model.types.StateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModelEvaluationContext extends EvaluationContext {

    Simulation simulation;

    Model model;
    Asset asset;

    State oldState;
    State newState;

    public EnumerableType<?>.Instance getEnumerableValue(Identifier name) {
        if(model!=null) {
            // Look at state values first
            {
                StateType.Instance value = model.getStateValue(name);
                if (value != null) {
                    return value;
                }
            }

            // Look at enum values first
            {
                EnumerationType.Instance value = model.getEnumerationValue(name);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    public Object getMacroValue(Identifier name) {
        if(model != null && model.getMacros().containsKey(name)) {
            Expression macro = model.getMacros().get(name);
            if(macro != null) {
                return macro.apply(this);
            }
        }
        return null;
    }

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

    public Object getValue(Identifier name) {
        if(name.isOld()) {
            return getOldState().getValue(name.getNewIdentifier());
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

    public void setValue(Identifier name, Object value) {
        if(!model.getRegisters().containsKey(name)) {
            throw  new RuntimeException("Model does not contain register: " + name);
        }
        // TODO Check type compatibility or cast the value
        newState.setValue(name, value);
    }

}
