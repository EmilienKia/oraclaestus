package com.github.emilienkia.oraclaestus.functions;

import com.github.emilienkia.oraclaestus.Dumpable;
import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;
import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.rules.Return;
import com.github.emilienkia.oraclaestus.rules.Rule;
import com.github.emilienkia.oraclaestus.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.types.TypeDescriptor;
import com.github.emilienkia.oraclaestus.variables.Variable;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleGroupFunction extends Function implements Dumpable {

    @Getter
    RuleGroup ruleGroup;

    public RuleGroupFunction(Identifier name, TypeDescriptor<?> returnType, List<Variable<?>> parameters, RuleGroup ruleGroup) {
        super(name, returnType, parameters, false);
        this.ruleGroup = ruleGroup;
    }

    @Override
    public void dump() {
        System.out.print("Function: " + name + " ( ");
        for (Variable<?> param : parameters) {
            System.out.print(param.getName() + " : " + param.getType() + ", ");
        }
        System.out.println(") : " + (returnType!=null ? returnType.getType() : "<void>" ));
        ruleGroup.dump();
    }


    class FunctionEvaluationContext extends EvaluationContext {

        Map<Identifier, Object> values = new HashMap<>();

        public FunctionEvaluationContext(EvaluationContext parent, List<Object> arguments) {
            super(parent);
            initialize(arguments);
        }

        void initialize(List<Object> arguments) {
            for(int i = 0; i < parameters.size(); i++) {
                Variable<?> param = parameters.get(i);
                values.put(param.getName(), i < arguments.size() ? param.getTypeDescriptor().cast(arguments.get(i)) : param.createDefaultValue());
            }
        }

        @Override
        public Object getValue(Identifier name, boolean old) {
            if(!name.hasPrefix()) {
                // Function local have not prefix
                if (values.containsKey(name)) {
                    if(old) {
                        // TODO Log a warning, old values are for entity's registers only
                    }
                    return values.get(name);
                }
            }
            return super.getValue(name, old);
        }

        @Override
        public void setValue(Identifier name, Object value) {
            Variable<?> register = parameters.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
            if(register!=null) {
                values.put(name, register.getTypeDescriptor().cast(value));
            } else {
                super.setValue(name, value);
            }
        }
    }

    @Override
    public Object apply(EvaluationContext context, List<Object> arguments) {
        try {
            FunctionEvaluationContext localContext = new FunctionEvaluationContext(context, arguments);
            for (Rule rule : ruleGroup.getRules()) {
                rule.apply(localContext);
            }
        } catch (Return e) {
            return e.getValue();
        }
        return null;
    }
}
