package com.github.emilienkia.oraclaestus.modules;

import com.github.emilienkia.oraclaestus.contexts.EvaluationContext;
import com.github.emilienkia.oraclaestus.contexts.ModelEvaluationContext;
import com.github.emilienkia.oraclaestus.functions.Function;

import java.util.List;

import com.github.emilienkia.oraclaestus.variables.StringVariable;

public class LogModule extends Module {


    private static LogModule instance = null;

    public static LogModule getModule() {
        if (instance == null) {
            instance = new LogModule();
        }
        return instance;
    }

    private LogModule() {
        addFunction(new LogFunction("trace") {
            @Override
            public void doLog(ModelEvaluationContext context, String message, Object... args) {
                context.getLogger().trace(message, args);
            }
        });
        addFunction(new LogFunction("debug"){
            @Override
            public void doLog(ModelEvaluationContext context, String message, Object... args) {
                context.getLogger().debug(message, args);
            }
        });
        addFunction(new LogFunction("info"){
            @Override
            public void doLog(ModelEvaluationContext context, String message, Object... args) {
                context.getLogger().info(message, args);
            }
        });
        addFunction(new LogFunction("warn"){
            @Override
            public void doLog(ModelEvaluationContext context, String message, Object... args) {
                context.getLogger().warn(message, args);
            }
        });
        addFunction(new LogFunction("error"){
            @Override
            public void doLog(ModelEvaluationContext context, String message, Object... args) {
                context.getLogger().error(message, args);
            }
        });
    }

    abstract static class LogFunction extends Function {

        public LogFunction(String name) {
            super(name, null, List.of(new StringVariable("msg", "")), true);
        }

        public abstract void doLog(ModelEvaluationContext context, String message, Object... args);

        @Override
        public Object apply(EvaluationContext context, List<Object> arguments) {
            ModelEvaluationContext ctx = context.getContext(ModelEvaluationContext.class);
            if (ctx != null) {
                if(!arguments.isEmpty() && arguments.getFirst() instanceof String message) {
                    arguments.removeFirst();
                    doLog(ctx, message, arguments.toArray());
                }
            } else {
                // TODO handle case where context is not a ModelEvaluationContext
                // TODO throw an exception ?
            }
            return null;
        }
    }

}
