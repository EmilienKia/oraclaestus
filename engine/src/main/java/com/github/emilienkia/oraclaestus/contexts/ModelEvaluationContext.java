package com.github.emilienkia.oraclaestus.contexts;

import com.github.emilienkia.oraclaestus.*;
import com.github.emilienkia.oraclaestus.expressions.Expression;
import com.github.emilienkia.oraclaestus.functions.Function;
import com.github.emilienkia.oraclaestus.modules.Module;
import com.github.emilienkia.oraclaestus.types.EnumerableType;
import com.github.emilienkia.oraclaestus.variables.Variable;
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

    EntityState oldState;
    EntityState newState;

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
        if(!name.hasPrefix() &&  model != null) {
            Function function = model.getFunction(name);
            if(function != null) {
                return function;
            }
        }
        if(simulation!=null) {
            if(name.hasPrefix()) {
                // Has a prefix, look at the module name based on the prefix
                Module module = simulation.getModule(name.getPrefixAsIdentifier());
                if(module!=null) {
                    Function function = module.getFunction(name);
                    if (function != null) {
                        return function;
                    }
                }
            } else {
                // No prefix, iterate through modules to try to find a function with this name, as a last chance
                for (Module module : simulation.getModules().values()) {
                    Function function = module.getFunction(name);
                    if (function != null) {
                        return function;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object getValue(Identifier name, boolean old) {
        if(name.hasPrefix() && simulation!=null) {
            // Has a prefix, look at the module name based on the prefix
            Module module = simulation.getModule(name.getPrefixAsIdentifier());
            if(module!=null) {
                return module.getConstant(name);
            }
            return null;
        }
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
