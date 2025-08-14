package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.expressions.Expression;
import com.github.emilienkia.oraclaestus.functions.Function;
import com.github.emilienkia.oraclaestus.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.types.CustomType;
import com.github.emilienkia.oraclaestus.types.EnumerationType;
import com.github.emilienkia.oraclaestus.variables.Variable;
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
    Map<String, CustomType<?>> customTypes = new HashMap<>();

    @Singular
    List<RuleGroup> ruleGroups = new ArrayList<>();

    public Entity createEntity(String name) {
        return new Entity(this.id + "-" + name,
                name, this,
                this.ruleGroups.stream().toList()
        );
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
