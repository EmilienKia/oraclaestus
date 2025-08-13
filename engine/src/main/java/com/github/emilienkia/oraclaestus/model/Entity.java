package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import lombok.Data;

import java.util.List;

@Data
public class Entity {

    String id;
    String name;
    String type;

    Model model;

    List<RuleGroup> ruleGroups;

    State currentState;

}
