package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationContext {

    EvaluationContext parent;

    Model model;
    Asset asset;

    State oldState;
    State newState;

    public EnumerationType.Instance getEnumerationValue(String name) {
        if(model!=null) {
            // Look at enum values first
            EnumerationType.Instance value = model.getEnumerationValue(name);
            if(value != null) {
                return value;
            }
        }
        return null;
    }

    public Object getValue(String name) {
        if(name.startsWith("~")) {
            return getOldState().getValue(name.substring(1));
        } else {
            // Look at enum values first
            EnumerationType.Instance value = getEnumerationValue(name);
            if(value != null) {
                return value;
            }
            // Otherwise, look at the new state
            return getNewState().getValue(name);
        }
    }

}
