package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.rules.RuleGroup;
import lombok.AllArgsConstructor;
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
