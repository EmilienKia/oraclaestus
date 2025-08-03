package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.rules.Rule;
import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.model.types.EnumerationType;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    Map<String, Variable<?>> registers = new HashMap<>();

    @Singular
    List<EnumerationType> enumerations = new ArrayList<>();

    @Singular
    List<RuleGroup> ruleGroups = new ArrayList<>();

    public Asset createAsset(String name) {
        Asset asset = new Asset();
        asset.setId(this.id + "-" + name);
        asset.setName(name);
        asset.setModel(this);

        asset.setRuleGroups(this.ruleGroups.stream().toList());

        State state = new State();
        state.setValues(registers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createDefaultValue())));

        asset.setCurrentState(state);

        return asset;
    }

    public EnumerationType.Instance getEnumerationValue(String name) {
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
        System.out.println("Registers:");
        for (Map.Entry<String, Variable<?>> entry : registers.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().getType() + " = " + entry.getValue().createDefaultValue());
        }
        System.out.println("Enumerations: (TODO)");
        System.out.println("Rules:");
        for (RuleGroup ruleGroup : ruleGroups) {
            ruleGroup.dump();
        }
    }
}
