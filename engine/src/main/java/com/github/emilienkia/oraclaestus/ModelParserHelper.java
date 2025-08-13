package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.grammars.ModelBaseListener;
import com.github.emilienkia.oraclaestus.grammars.ModelLexer;
import com.github.emilienkia.oraclaestus.grammars.ModelParser;
import com.github.emilienkia.oraclaestus.model.functions.RuleGroupFunction;
import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.expressions.*;
import com.github.emilienkia.oraclaestus.model.expressions.FunctionCall;
import com.github.emilienkia.oraclaestus.model.rules.*;
import com.github.emilienkia.oraclaestus.model.types.*;
import com.github.emilienkia.oraclaestus.model.variables.GenericVariable;
import com.github.emilienkia.oraclaestus.model.variables.Variable;
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
            throw new IOException("Parsing errors occurred: " + errorBuilder);
        }

        return listener.model;
    }

    static class ModelListener extends ModelBaseListener {

        Model model = new Model();

        List<Expression> expressions = new ArrayList<>();

        TypeDescriptor<?> type = null;

        List<Identifier> identifiers = new ArrayList<>();

        interface ParsingContext {
            default void addVariable(TypeDescriptor<?> type, Identifier name, Expression initialExpression) {}
            default void addVariable(TypeDescriptor<?> type, Identifier name, Object initialValue) {}
            default void addRule(Rule rule) {}
            default void onExitRuleGroup() {}
        }

        Deque<ParsingContext> context = new ArrayDeque<>();


        class ModelParsingContext implements ParsingContext {
            @Getter
            List<Rule> rules = new ArrayList<>();

            @Override
            public void addVariable(TypeDescriptor<?> type, Identifier name, Expression initialExpression) {
                model.getRegisters().put(name, new GenericVariable(type, name, initialExpression));
            }

            @Override
            public void addVariable(TypeDescriptor<?> type, Identifier name, Object initialValue) {
                model.getRegisters().put(name, new GenericVariable(type, name, initialValue));
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
            public void addVariable(TypeDescriptor<?> type, Identifier name, Expression initialExpression) {
                ruleGroup.add(name, new GenericVariable(type, name, initialExpression));
                ruleGroup.getRules().add(new Assignation(name, initialExpression));
            }

            @Override
            public void addVariable(TypeDescriptor<?> type, Identifier name, Object initialValue) {
                ruleGroup.add(name, new GenericVariable(type, name, initialValue));
            }

            @Override
            public void addRule(Rule rule) {
                ruleGroup.add(rule);
            }
        }

        static class ConditionalRuleParsingContext implements ParsingContext {
            @Getter
            List<Rule> rules = new ArrayList<>();

            @Getter @Setter
            Expression ifCondExpression;

            @Override
            public void addRule(Rule rule) {
                rules.add(rule);
            }
        }

        static class FunctionParsingContext implements ParsingContext {

            @Getter
            List<Variable<?>> parameters = new ArrayList<>();

            @Getter @Setter
            TypeDescriptor<?> returnType;

            @Getter
            List<Rule> rules = new ArrayList<>();

            @Override
            public void addVariable(TypeDescriptor<?> type, Identifier name, Expression initialExpression) {
                parameters.add(new GenericVariable(type, name, initialExpression));
            }

            @Override
            public void addVariable(TypeDescriptor<?> type, Identifier name, Object initialValue) {
                parameters.add(new GenericVariable(type, name, initialValue));
            }

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
            if(identifiers.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Variable name cannot be null or empty");
            }
            Identifier varName = identifiers.removeLast();
            if(varName == null || !varName.isValid()) {
                // TODO Error handling
                throw new IllegalArgumentException("Variable name cannot be null or empty");
            }

            if(this.type==null) {
                // TODO Error handling
                throw new IllegalArgumentException("Variable type must be provided");
            }
            TypeDescriptor<?> type = this.type;

            if(ctx.var_def_value != null) {
                Expression expression = expressions.removeLast();
                if(expression instanceof ConstValue constValue) {
                    context.element().addVariable(type, varName, constValue.getValue());
                } else if(expression instanceof ReadValue readValue && readValue.getIdentifier() != null
                        && type instanceof EnumerableType<?> enumType) {
                    String valueName = readValue.getIdentifier().toString();
                    EnumerableType<?>.Instance value = (EnumerableType<?>.Instance) enumType.cast(valueName);
                    context.element().addVariable(type, varName, value);
                } else {
                    context.element().addVariable(type, varName, expression);
                }
            } else {
                context.element().addVariable(type, varName, null);
            }
        }

        @Override
        public void exitMacroDeclaration(ModelParser.MacroDeclarationContext ctx) {
            if(identifiers.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Macro name cannot be null or empty");
            }
            Identifier macroName = identifiers.removeLast();
            if(macroName == null || !macroName.isValid()) {
                // TODO Error handling
                throw new IllegalArgumentException("Macro name cannot be null or empty");
            }

            if(expressions == null || expressions.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Macro must have an expression");
            }

            Expression expression = expressions.removeLast();
            model.getMacros().put(macroName, expression);
        }

        @Override
        public void enterFunctionDeclaration(ModelParser.FunctionDeclarationContext ctx) {
            context.push(new FunctionParsingContext());
        }

        @Override
        public void exitFunctionDeclaration(ModelParser.FunctionDeclarationContext ctx) {
            FunctionParsingContext functionContext = (FunctionParsingContext) context.pop();
            if(functionContext==null) {
                // TODO Error handling
                throw new IllegalArgumentException("Invalid function context: " + ctx.getText());
            }
            if(identifiers.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Function name cannot be null or empty");
            }
            Identifier functionName = identifiers.removeLast();
            if(functionName == null || !functionName.isValid()) {
                // TODO Error handling
                throw new IllegalArgumentException("Function name cannot be null or empty");
            }

            if(functionContext.getRules().isEmpty() || !(functionContext.getRules().get(0) instanceof RuleGroup ruleGroup)) {
                // TODO Error handling
                throw new IllegalArgumentException("Function must have exactly one rule");
            }

            model.getFunctions().put(functionName,
                    new RuleGroupFunction(
                            functionName,
                            functionContext.getReturnType(),
                            functionContext.getParameters(),
                            ruleGroup
                    ));
        }

        @Override
        public void exitFunctionReturnType(ModelParser.FunctionReturnTypeContext ctx) {
            FunctionParsingContext functionContext = (FunctionParsingContext) context.element();
            if(functionContext==null) {
                // TODO Error handling
                throw new IllegalArgumentException("Invalid function context: " + ctx.getText());
            }
            functionContext.setReturnType(type);
            type = null;
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

            Identifier variableName = Identifier.fromString(ctx.rule_name.getText());
            String opName = ctx.op.getText();

            if(variableName == null || opName == null || !variableName.isValid() || opName.isEmpty()) {
                // TODO
                throw new IllegalArgumentException("Invalid expression in rule: " + ctx.getText());
            }
            if(variableName.isOld()) {
                // TODO
                throw new IllegalArgumentException("Variable to assign cannot be an old-state variable name: " + variableName);
            }

            context.element().addRule(new Assignation(variableName, switch(opName) {
                case "=" -> expression;
                case "+=" -> new Addition(new ReadValue(variableName), expression);
                case "-=" -> new Subtraction(new ReadValue(variableName), expression);
                case "*=" -> new Multiplication(new ReadValue(variableName), expression);
                case "/=" -> new Division(new ReadValue(variableName), expression);
                case "%=" -> new Modulo(new ReadValue(variableName), expression);
                default -> throw new IllegalArgumentException("Unknown operator: " + opName);
            }));
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

            Identifier variableName = Identifier.fromString(ctx.rule_name.getText());

            if(variableName == null || !variableName.isValid()) {
                // TODO
                throw new IllegalArgumentException("Invalid expression in rule: " + ctx.getText());
            }

            context.element().addRule(new ConditionalAssignation(variableName, condition, expression));
        }

        @Override
        public void exitReturnRule(ModelParser.ReturnRuleContext ctx) {
            Expression expression = null;
            if(!expressions.isEmpty()) {
                expression = expressions.removeLast();
            }
            context.element().addRule(new ReturnRule(expression));
        }

        @Override
        public void exitExpressionRule(ModelParser.ExpressionRuleContext ctx) {
            Expression expr = expressions.removeLast();
            if(expr==null) {
                // TODO Error handling
                throw new IllegalArgumentException("Invalid expression rule: " + ctx.getText());
            }
            context.element().addRule(new ExpressionRule(expr));
        }

        public void exitBinaryExpression(String op,ParserRuleContext ctx) {
            if(expressions.size() >= 2) {
                Expression right = expressions.removeLast();
                Expression left = expressions.removeLast();
                if(left == null || right == null) {
                    throw new IllegalArgumentException("Invalid binary expression: " + ctx.getText());
                }

                expressions.add(switch(op){
                    case "+" -> new Addition(left, right);
                    case "-" -> new Subtraction(left, right);
                    case "*" -> new Multiplication(left, right);
                    case "/" -> new Division(left, right);
                    case "%" -> new Modulo(left, right);
                    case "&", "&&" -> new And(left, right);
                    case "|", "||" -> new Or(left, right);
                    case "^", "^^" -> new Xor(left, right);
                    case "==" -> new Equal(left, right);
                    case "!=" -> new Different(left, right);
                    case ">" -> new Greater(left, right);
                    case "<" -> new Lesser(left, right);
                    case ">=" -> new GreaterOrEqual(left, right);
                    case "<=" -> new LesserOrEqual(left, right);
                    default -> throw new IllegalArgumentException("Unknown operator: " + op);
                });

            } else {
                System.err.println("Invalid binary expression: " + ctx.getText());
                // TODO throw exception ?
            }
        }

        @Override
        public void exitLogicalOrExpression(ModelParser.LogicalOrExpressionContext ctx) {
            if(ctx.op!=null) {
                exitBinaryExpression(ctx.op.getText() /*"||"*/, ctx);
            }
        }

        @Override
        public void exitLogicalAndExpression(ModelParser.LogicalAndExpressionContext ctx) {
            if(ctx.op!=null) {
                exitBinaryExpression(ctx.op.getText() /*"&&"*/, ctx);
            }
        }

        @Override
        public void exitExclusiveOrExpression(ModelParser.ExclusiveOrExpressionContext ctx) {
            if(ctx.op!=null) {
                exitBinaryExpression(ctx.op.getText() /*"^"*/, ctx);
            }
        }

        @Override
        public void exitEqualityExpression(ModelParser.EqualityExpressionContext ctx) {
            if(ctx.op!=null) {
                exitBinaryExpression(ctx.op.getText() /*"== !="*/, ctx);
            }
        }

        @Override
        public void exitRelationalExpression(ModelParser.RelationalExpressionContext ctx) {
            if (ctx.op != null) {
                exitBinaryExpression(ctx.op.getText() /*"< > <= >="*/, ctx);
            }
        }

        @Override
        public void exitAdditiveExpression(ModelParser.AdditiveExpressionContext ctx) {
            if(ctx.op!=null) {
                exitBinaryExpression(ctx.op.getText() /*"+ -"*/, ctx);
            }
        }

        @Override
        public void exitMultiplicativeExpression(ModelParser.MultiplicativeExpressionContext ctx) {
            if(ctx.op!=null) {
                exitBinaryExpression(ctx.op.getText() /*"* / %"*/, ctx);
            }
        }

        @Override
        public void exitConditionalExpression(ModelParser.ConditionalExpressionContext ctx) {
            if(ctx.cond!=null) {
                if (expressions.size() >= 3) {
                    Expression third = expressions.removeLast();
                    Expression second = expressions.removeLast();
                    Expression first = expressions.removeLast();
                    if (first == null || second == null || third == null) {
                        throw new IllegalArgumentException("Invalid conditional expression: " + ctx.getText());
                    }
                    expressions.add(new Conditional(first, second, third));
                }
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
        public void exitFunctionCall(ModelParser.FunctionCallContext ctx) {
            if(identifiers.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Function name cannot be null or empty");
            }
            Identifier functionName = identifiers.removeLast();
            if(functionName == null || !functionName.isValid()) {
                // TODO Error handling
                throw new IllegalArgumentException("Function name cannot be null or empty");
            }

            List<Expression> args = expressions;
            expressions = new ArrayList<>();

            expressions.add(new FunctionCall(functionName, args));
        }


        @Override
        public void exitValueExpression(ModelParser.ValueExpressionContext ctx) {
            switch(ctx.value().stop.getType()) {
                case ModelParser.ID -> {
                    switch(ctx.getText()) {
                        case "true", "false" -> expressions.add(new ConstValue(ConstValue.parseBoolean(ctx.getText())));
                        // TODO Look at enum or state values
                        default              -> expressions.add(new ReadValue(ctx.getText()));
                    }
                }
                case ModelParser.STRING ->  expressions.add(new ConstValue(ConstValue.parseString(ctx.getText())));
                case ModelParser.NUMBER -> expressions.add(new ConstValue(ConstValue.parseNumber(ctx.getText())));
                case ModelParser.BOOLEAN -> expressions.add(new ConstValue(ConstValue.parseBoolean(ctx.getText())));
            }
        }


        @Override
        public void exitVariableReferenceExpression(ModelParser.VariableReferenceExpressionContext ctx) {
            if(identifiers.isEmpty()) {
                // TODO Error handling
                throw new IllegalArgumentException("Invalid variable reference: " + ctx.getText());
            }
            Identifier id = identifiers.removeLast();
            if(id == null || !id.isValid()) {
                // TODO Error handling
                throw new IllegalArgumentException("Invalid variable identifier: " + ctx.getText());
            }
            if(id.toString().equals("true") || id.toString().equals("false")) {
                expressions.add(new ConstValue(Boolean.parseBoolean(id.toString())));
            } else {
                expressions.add(new ReadValue(id));
            }
        }

        @Override
        public void exitVariableIdentifier(ModelParser.VariableIdentifierContext ctx) {
            identifiers.add(new Identifier(ctx.var_name.stream().map(Token::getText).toList()));
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

        @Override
        public void exitStateType(ModelParser.StateTypeContext ctx) {
            StateType type = new StateType();
            ctx.enum_value.stream().map(Token::getText).forEach(type::add);
            model.getStates().add(type);
            this.type = type;
        }

    }

}
