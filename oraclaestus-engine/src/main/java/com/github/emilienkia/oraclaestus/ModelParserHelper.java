package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.grammars.ModelBaseListener;
import com.github.emilienkia.oraclaestus.grammars.ModelLexer;
import com.github.emilienkia.oraclaestus.grammars.ModelParser;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;
import com.github.emilienkia.oraclaestus.model.expressions.*;
import com.github.emilienkia.oraclaestus.model.rules.*;
import com.github.emilienkia.oraclaestus.model.types.*;
import com.github.emilienkia.oraclaestus.model.variables.GenericVariable;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ModelParserHelper {

    public Model parseFile(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        return parse(fis);
    }

    public Model parseString(String input) throws IOException {
        InputStream stream = new java.io.ByteArrayInputStream(input.getBytes());
        return parse(stream);
    }

    public Model parse(InputStream stream) throws IOException {
        CharStream input = CharStreams.fromStream(stream);
        ModelLexer lexer = new ModelLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ModelParser parser = new ModelParser(tokens);

        StringBuilder errorBuilder = new StringBuilder();
        ANTLRErrorListener errorListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                errorBuilder.append("Line ").append(line).append(":").append(charPositionInLine)
                        .append(" ").append(msg).append("\n");
            }
        };

        parser.addErrorListener(errorListener);
        ModelParser.ModelContext context = parser.model();

        ModelListener listener = new ModelListener();
        ParseTreeWalker.DEFAULT.walk(listener, context);

        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        if(!errorBuilder.isEmpty()) {
            System.err.println(errorBuilder);
        }

        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new IOException("Parsing errors occurred: " + errorBuilder.toString());
        }

        return listener.model;
    }

    static class ModelListener extends ModelBaseListener {

        Model model = new Model();
        State state = new State();

        List<Expression> expressions = new ArrayList<>();

        TypeDescriptor<?> type = null;

        interface ParsingContext {
            default void addVariable(TypeDescriptor<?> type, String name, Object defaultValue) {}
            default void addRule(Rule rule) {}
            default void onExitRuleGroup() {}
        }

        Deque<ParsingContext> context = new ArrayDeque<>();


        class ModelParsingContext implements ParsingContext {
            @Getter
            List<Rule> rules = new ArrayList();

            @Override
            public void addVariable(TypeDescriptor<?> type, String name, Object value) {
                state.setValue(name, value);
                model.getRegisters().put(name, new GenericVariable(type, name, value));
            }

            @Override
            public void addRule(Rule rule) {
                rules.add(rule);
            }
        }

        static class RuleGroupParsingContext implements ParsingContext {
            @Getter
            RuleGroup ruleGroup = new RuleGroup();

            @Override
            public void addVariable(TypeDescriptor<?> type, String name, Object value) {
                ruleGroup.add(name, new GenericVariable(type, name, value));
            }
            @Override
            public void addRule(Rule rule) {
                ruleGroup.add(rule);
            }
        }

        static class ConditionalRuleParsingContext implements ParsingContext {
            @Getter
            List<Rule> rules = new ArrayList();

            @Getter @Setter
            Expression ifCondExpression;

            @Override
            public void addRule(Rule rule) {
                rules.add(rule);
            }
        }


        @Override public void enterModel(ModelParser.ModelContext ctx) {
            context.push(new ModelParsingContext());
        }
        @Override public void exitModel(ModelParser.ModelContext ctx) {
            context.pop();
        }

        @Override
        public void exitMetadataDeclaration(ModelParser.MetadataDeclarationContext ctx) {
            String key = ctx.metadata_name.getText();
            Object value = switch(ctx.metadata_value.stop.getType()) {
                case ModelParser.ID -> ctx.metadata_value.getText(); // TODO handle also booleans ?
                case ModelParser.STRING -> ctx.metadata_value.getText().substring(1, ctx.metadata_value.getText().length() - 1); // Remove quotes
                case ModelParser.NUMBER -> Float.parseFloat(ctx.metadata_value.getText());
                case ModelParser.BOOLEAN -> Boolean.parseBoolean(ctx.metadata_value.getText());
                default -> null;
            };

            switch(key) {
                case "name" -> model.setName(value!=null ? value.toString() : "");
                case "id" -> model.setId(value!=null ? value.toString() : "");
                default -> model.getMetadata().put(key, value);
            }
        }

        @Override
        public void exitVariableDeclaration(ModelParser.VariableDeclarationContext ctx) {
            String varName = ctx.var_name.getText();
            if(varName == null || varName.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Variable name cannot be null or empty");
            }
            if(this.type==null) {
                // TODO Error handling
                throw new IllegalArgumentException("Variable type cannot be null");
            }
            TypeDescriptor<?> type = this.type;
            this.type = null; // Reset type for next variable

            if(ctx.var_def_value != null) {
                Expression expression = expressions.removeLast();
                if(expression == null) {
                    // TODO Error handling
                    throw new IllegalArgumentException("Invalid default value for variable: " + varName);
                }
                Object value = expression.apply(new EvaluationContext(null, model, null, state, state));
                context.element().addVariable(type, varName, value);
            } else {
                context.element().addVariable(type, varName, null);
            }
        }

        @Override
        public void exitRules(ModelParser.RulesContext ctx) {
            ModelParsingContext modelContext = (ModelParsingContext) context.element();
            if(model==null) {
                // TODO Error handling
                throw new IllegalArgumentException("Model cannot be null");
            }
            if(modelContext.getRules().size()!=1) {
                // TODO Error handling
                throw new IllegalArgumentException("Model must have exactly one rule group");
            }
            Rule rule = modelContext.getRules().removeFirst();
            if(!(rule instanceof RuleGroup ruleGroup)) {
                // TODO Error handling
                throw new IllegalArgumentException("Model must have exactly one rule group");
            }
            model.getRuleGroups().add(ruleGroup);
        }

        @Override
        public void enterRuleGroup(ModelParser.RuleGroupContext ctx) {
            context.push(new RuleGroupParsingContext());
        }

        @Override
        public void exitRuleGroup(ModelParser.RuleGroupContext ctx) {
            RuleGroupParsingContext ruleGroupContext = (RuleGroupParsingContext) context.pop();
            if(ruleGroupContext == null) {
                // TODO Error handling
                throw new IllegalArgumentException("Invalid rule group: " + ctx.getText());
            }
            RuleGroup group = ruleGroupContext.getRuleGroup();
            context.element().addRule(group);

            context.element().onExitRuleGroup();
        }

        @Override
        public void exitConditionExpression(ModelParser.ConditionExpressionContext ctx) {
            ConditionalRuleParsingContext ruleContext = (ConditionalRuleParsingContext) this.context.element();
            if(ruleContext == null) {
                // TODO Error handling
                throw new IllegalArgumentException("Invalid condition context: " + ctx.getText());
            }
            if(expressions.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Unexpected expression in if rule: " + ctx.getText());
            }
            ruleContext.setIfCondExpression(expressions.removeLast());
        }

        @Override
        public void enterCondition(ModelParser.ConditionContext ctx) {
            context.push(new ConditionalRuleParsingContext());
        }

        @Override
        public void exitCondition(ModelParser.ConditionContext ctx) {
            ConditionalRuleParsingContext ruleContext = (ConditionalRuleParsingContext) this.context.pop();
            Expression condition = ruleContext.ifCondExpression;
            if(condition == null) {
                // TODO
                throw new IllegalArgumentException("Invalid condition: " + ctx.getText());
            }

            Rule thenRule = null;
            Rule elseRule = null;

            if(ruleContext.getRules().size()>2) {
                // TODO Error handling
            } else if(ruleContext.getRules().size() == 2) {
                elseRule = ruleContext.getRules().removeLast();
                thenRule = ruleContext.getRules().removeLast();
            } else if(ruleContext.getRules().size() == 1) {
                thenRule = ruleContext.getRules().removeLast();
            }

            context.element().addRule(new Condition(condition, thenRule, elseRule));
        }

        @Override
        public void exitAssignationRule(ModelParser.AssignationRuleContext ctx) {
            Expression expression = expressions.removeLast();
            if(expression == null) {
                // TODO
                throw new IllegalArgumentException("Invalid expression in rule: " + ctx.getText());
            }

            String variableName = ctx.rule_name.getText();
            String opName = ctx.op.getText();

            if(variableName == null || opName == null || variableName.isEmpty() || opName.isEmpty()) {
                // TODO
                throw new IllegalArgumentException("Invalid expression in rule: " + ctx.getText());
            }
            if(variableName.startsWith("~")) {
                // TODO
                throw new IllegalArgumentException("Variable to assign cannot be an old-state variable name: " + variableName);
            }

            context.element().addRule(switch(opName) {
                case "=" -> new Assignation(variableName, expression);
                case "+=" -> new Assignation(variableName, new Addition(new ReadValue(variableName), expression));
                case "-=" -> new Assignation(variableName, new Subtraction(new ReadValue(variableName), expression));
                case "*=" -> new Assignation(variableName, new Multiplication(new ReadValue(variableName), expression));
                case "/=" -> new Assignation(variableName, new Division(new ReadValue(variableName), expression));
                case "%=" -> new Assignation(variableName, new Modulo(new ReadValue(variableName), expression));
                default -> throw new IllegalArgumentException("Unknown operator: " + opName);
            });
        }

        @Override
        public void exitConditionnalAssignationRule(ModelParser.ConditionnalAssignationRuleContext ctx) {
            Expression expression = expressions.removeLast();
            if(expression == null) {
                // TODO
                throw new IllegalArgumentException("Invalid expression in rule: " + ctx.getText());
            }

            Expression condition = expressions.removeLast();
            if(condition == null) {
                // TODO
                throw new IllegalArgumentException("Invalid condition in rule: " + ctx.getText());
            }

            String variableName = ctx.rule_name.getText();

            if(variableName == null || variableName.isEmpty()) {
                // TODO
                throw new IllegalArgumentException("Invalid expression in rule: " + ctx.getText());
            }

            context.element().addRule(new ConditionalAssignation(variableName, condition, expression));
        }

        @Override
        public void exitBinaryExpression(ModelParser.BinaryExpressionContext ctx) {
            if(expressions.size() == 2) {
                Expression right = expressions.removeLast();
                Expression left = expressions.removeLast();
                if(left == null || right == null) {
                    throw new IllegalArgumentException("Invalid binary expression: " + ctx.getText());
                }
                switch(ctx.op.getText()) {
                    case "+" -> expressions.add(new Addition(left, right));
                    case "-" -> expressions.add(new Subtraction(left, right));
                    case "*" -> expressions.add(new Multiplication(left, right));
                    case "/" -> expressions.add(new Division(left, right));
                    case "%" -> expressions.add(new Modulo(left, right));
                    case "&&" -> expressions.add(new And(left, right));
                    case "||" -> expressions.add(new Or(left, right));
                    case "^" -> expressions.add(new Xor(left, right));
                    case "==" -> expressions.add(new Equal(left, right));
                    case "!=" -> expressions.add(new Different(left, right));
                    case ">" -> expressions.add(new Greater(left, right));
                    case "<" -> expressions.add(new Lesser(left, right));
                    case ">=" -> expressions.add(new GreaterOrEqual(left, right));
                    case "<=" -> expressions.add(new LesserOrEqual(left, right));
                    default -> throw new IllegalArgumentException("Unknown operator: " + ctx.op.getText());
                }

            } else {
                // TODO throw exception ?
            }
        }

        @Override
        public void exitTernaryExpression(ModelParser.TernaryExpressionContext ctx) {
            if(expressions.size() == 3) {
                Expression third = expressions.removeLast();
                Expression second = expressions.removeLast();
                Expression first = expressions.removeLast();
                if(first == null || second == null || third == null) {
                    throw new IllegalArgumentException("Invalid ternary expression: " + ctx.getText());
                }
                expressions.add(new Ternary(first, second, third));
            } else {
                // TODO throw exception ?
            }
        }

        @Override
        public void exitValueExpression(ModelParser.ValueExpressionContext ctx) {
            switch(ctx.value().stop.getType()) {
                case ModelParser.ID -> {
                    switch(ctx.getText()) {
                        case "true", "false" -> expressions.add(new ConstValue(Boolean.parseBoolean(ctx.getText())));
                        default              -> expressions.add(new ReadValue(ctx.getText()));
                    }
                }
                case ModelParser.STRING ->  expressions.add(new ConstValue(ctx.getText()
                        .substring(1, ctx.getText().length() - 1))); // Remove quotes));
                case ModelParser.NUMBER -> expressions.add(new ConstValue(Float.parseFloat(ctx.getText())));
                case ModelParser.BOOLEAN -> expressions.add(new ConstValue(Boolean.parseBoolean(ctx.getText())));
            }
        }

        @Override
        public void exitUnaryMinusExpression(ModelParser.UnaryMinusExpressionContext ctx) {
            Expression expression = expressions.removeLast();
            if(expression == null) {
                // TODO
                throw new IllegalArgumentException("Invalid expression in rule: " + ctx.getText());
            }
            expressions.add(new Negation(expression));
        }

        @Override
        public void exitIntegerType(ModelParser.IntegerTypeContext ctx) {
            type = new IntegerType();
        }

        @Override
        public void exitFloatType(ModelParser.FloatTypeContext ctx) {
            type = new FloatType();
        }

        @Override
        public void exitStringType(ModelParser.StringTypeContext ctx) {
            type = new StringType();
        }

        @Override
        public void exitBooleanType(ModelParser.BooleanTypeContext ctx) {
            type = new BooleanType();
        }

        @Override
        public void exitEnumType(ModelParser.EnumTypeContext ctx) {
            EnumerationType type = new EnumerationType();
            ctx.enum_value.stream().map(Token::getText).forEach(type::add);
            model.getEnumerations().add(type);
            this.type = type;
        }

    }

}
