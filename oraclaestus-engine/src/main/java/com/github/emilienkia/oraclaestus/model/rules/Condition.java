package com.github.emilienkia.oraclaestus.model.rules;

import com.github.emilienkia.oraclaestus.model.*;
import com.github.emilienkia.oraclaestus.model.expressions.Expression;
import lombok.Getter;

@Getter
public class Condition implements Rule {

    Expression condition;
    Rule thenRule;
    Rule elseRule;

    public Condition(Expression condition, Rule thenRule) {
        this.condition = condition;
        this.thenRule = thenRule;
    }

    public Condition(Expression condition, Rule thenRule, Rule elseRule) {
        this.condition = condition;
        this.thenRule = thenRule;
        this.elseRule = elseRule;
    }

    @Override
    public void apply(EvaluationContext context) {
        Object cond = condition.apply(context);
        if (Helper.toBool(cond)) {
            if(thenRule != null) {
                thenRule.apply(context);
            }
        } else if (elseRule != null) {
            elseRule.apply(context);
        }
    }

    @Override
    public void dump() {
        System.out.print("if( ");
        condition.dump();
        System.out.print(" ) then ");
        thenRule.dump();
        if (elseRule != null) {
            System.out.print(" else ");
            elseRule.dump();
        }
    }
}
