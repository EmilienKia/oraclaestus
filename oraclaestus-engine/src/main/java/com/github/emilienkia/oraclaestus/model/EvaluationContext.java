package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.functions.Function;
import com.github.emilienkia.oraclaestus.model.functions.RuleGroupFunction;
import com.github.emilienkia.oraclaestus.model.types.EnumerableType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationContext {

    EvaluationContext parent = null;

    public EnumerableType<?>.Instance getEnumerableValue(String name) {
        return null;
    }

    public Object getMacroValue(Identifier name) {
        return null;
    }

    public Function resolveFunction(Identifier name) {
        if(parent != null) {
            return parent.resolveFunction(name);
        } else {
            return null;
        }
    }
    public Object getValue(Identifier name) {
        if(parent != null) {
            return parent.getValue(name);
        } else {
            return null;
        }
    }

    public void setValue(Identifier name, Object value) {
        if(parent != null) {
            parent.setValue(name, value);
        }
    }

}
