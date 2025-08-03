package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.rules.Rule;
import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import lombok.Data;

import java.util.List;

@Data
public class Asset {

    String id;
    String name;
    String type;

    Model model;

    List<RuleGroup> ruleGroups;

    State currentState;

}
