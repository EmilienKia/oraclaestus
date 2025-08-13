package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import com.github.emilienkia.oraclaestus.model.functions.Function;
import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import com.github.emilienkia.oraclaestus.model.types.StateType;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    String id;
    String name;

    @Singular("metadata")
    Map<String, Object> metadata = new HashMap<>();

    @Singular
    Map<Identifier, Variable<?>> registers = new HashMap<>();

    @Singular
    Map<Identifier, Expression> macros = new HashMap<>();

    @Singular
    Map<Identifier, Function> functions = new HashMap<>();

    @Singular
    List<EnumerationType> enumerations = new ArrayList<>();

    @Singular
    List<StateType> states = new ArrayList<>();

    @Singular
    List<RuleGroup> ruleGroups = new ArrayList<>();

    public Entity createEntity(String name) {
        Entity entity = new Entity();
        entity.setId(this.id + "-" + name);
        entity.setName(name);
        entity.setModel(this);

        entity.setRuleGroups(this.ruleGroups.stream().toList());

        State state = new State();
        state.setValues(registers.entrySet().stream().collect(HashMap::new, (map, elem) -> map.put(elem.getKey(), elem.getValue().createDefaultValue()), HashMap::putAll));
        entity.setCurrentState(state);
        return entity;
    }


    public Variable<?> getRegister(String name) {
        return getRegister(Identifier.fromString(name));
    }

    public Variable<?> getRegister(Identifier name) {
        return registers.get(name);
    }

    public Expression getMacro(String name) {
        return getMacro(Identifier.fromString(name));
    }

    public Expression getMacro(Identifier name) {
        return macros.get(name);
    }

    public Function getFunction(String name) {
        return getFunction(Identifier.fromString(name));
    }

    public Function getFunction(Identifier name) {
        return functions.get(name);
    }



    public StateType.Instance getStateValue(Identifier name) {
        for (StateType state : states) {
            Integer value = state.getValue(name);
            if (value != null) {
                return state.cast(value);
            }
        }
        return null;
    }


    public EnumerationType.Instance getEnumerationValue(Identifier name) {
        for (EnumerationType enumeration : enumerations) {
            Integer value = enumeration.getValue(name);
            if (value != null) {
                return enumeration.cast(value);
            }
        }
        return null;
    }

    public void dump() {
        System.out.println("Model: " + name);
        System.out.println("ID: " + id);
        for(Map.Entry<String, Object> entry : metadata.entrySet()) {
            System.out.println("Metadata: " + entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("States: (TODO)");
        System.out.println("Registers:");
        for (Map.Entry<Identifier, Variable<?>> entry : registers.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().getType() + " = " + entry.getValue().createDefaultValue());
        }
        System.out.println("Enumerations: (TODO)");
        System.out.println("Rules:");
        for (RuleGroup ruleGroup : ruleGroups) {
            ruleGroup.dump();
        }
    }

}
