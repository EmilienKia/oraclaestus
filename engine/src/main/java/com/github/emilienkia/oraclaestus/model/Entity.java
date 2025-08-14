package com.github.emilienkia.oraclaestus.model;

import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Entity {

    String id;
    String name;

    Model model;

    List<RuleGroup> ruleGroups;
}
