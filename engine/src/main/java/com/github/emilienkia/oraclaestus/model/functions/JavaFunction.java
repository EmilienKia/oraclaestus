package com.github.emilienkia.oraclaestus.model.functions;

import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Helper;
import com.github.emilienkia.oraclaestus.model.Identifier;
import com.github.emilienkia.oraclaestus.model.types.TypeDescriptor;
import com.github.emilienkia.oraclaestus.model.variables.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaFunction extends Function {

    GenericMethod method;
    Object object;

    public JavaFunction(Identifier name, TypeDescriptor<?> returnType, List<Variable<?>> parameters, GenericMethod method, Object object) {
        super(name, returnType, parameters, false);
        this.method = method;
        this.object = object;
    }

    @Override
    public Object apply(EvaluationContext context, List<Object> arguments) {
        if(method!=null) {
            // TODO check and cast arguments to match parameters
            Object result = method.invoke(context, arguments);
            // TODO Check and cast return type
            return result;
        } else {
            throw new IllegalStateException("Method is not defined for this JavaFunction : " + getName());
        }
    }

    @FunctionalInterface
    public interface GenericMethod {
        Object invoke(EvaluationContext context, List<Object> arguments);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        if(clazz == null || methodName == null || methodName.isBlank()) {
            throw new IllegalArgumentException("Class and method name cannot be null or empty");
        }
        return builder(clazz.getMethod(methodName, parameterTypes));
    }

    public static Builder builder(Method method) {
        if(method == null) {
            throw new IllegalArgumentException("Method cannot be null");
        }

        Builder builder = new Builder()
                .name(method.getName())
                .returnType(method.getReturnType());

        int paramCount = 0;
        for(Class<?> paramType : method.getParameterTypes()) {
            builder.parameter("param" + paramCount++, paramType, null);
        }

        builder.method((context, arguments) -> {
            try {
                Object[] params = new Object[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    params[i] = Helper.toTypeDescriptor(method.getParameterTypes()[i]).cast(arguments.get(i));
                }
                return method.invoke(builder.object, params);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking method " + method.getName(), e);
            }
        });

        return builder;
    }

    public static class Builder {
        private Identifier name;
        private TypeDescriptor<?> returnType;
        private List<Variable<?>> parameters = new ArrayList<>();
        private GenericMethod method;
        private Object object = null;

        public Builder name(String name) {
            this.name = Identifier.fromString(name);
            return this;
        }

        public Builder name(Identifier name) {
            this.name = name;
            return this;
        }

        public Builder returnType(Class<?> returnType) {
            this.returnType = Helper.toTypeDescriptor(returnType);
            return this;
        }

        public Builder returnType(TypeDescriptor<?> returnType) {
            this.returnType = returnType;
            return this;
        }

        public <T> Builder parameter(String name, Class<T> type, T defaultValue) {
            if(this.parameters == null) {
                this.parameters = new java.util.ArrayList<>();
            }
            this.parameters.add(new GenericVariable(Helper.toTypeDescriptor(type), Identifier.fromString(name), defaultValue));
            return this;
        }

        public <T> Builder parameter(Variable<T> variable) {
            this.parameters.add(variable);
            return this;
        }

        public Builder parameters(Variable<?> ... parameters) {
            this.parameters = Arrays.asList(parameters);
            return this;
        }

        public Builder parameters(List<Variable<?>> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder method(GenericMethod method) {
            this.method = method;
            return this;
        }

        public Builder parameterName(int index, String name) {
            if(name==null || name.isBlank()) {
                throw new IllegalArgumentException("Parameter name cannot be null or empty");
            }
            if (index < 0 || index >= parameters.size()) {
                throw new IndexOutOfBoundsException("Parameter index out of bounds: " + index);
            }
            Variable<?> variable = parameters.get(index);
            if (variable == null) {
                throw new IllegalArgumentException("Parameter at index " + index + " is null");
            }
            variable.setName(Identifier.fromString(name));
            return this;
        }

        public Builder parameterNames(String ... names) {
            if(names.length != parameters.size()) {
                throw new IllegalArgumentException("Number of names must match number of parameters");
            }
            for (int i = 0; i < names.length; i++) {
                parameterName(i, names[i]);
            }
            return this;
        }


        public Builder parameterNames(List<String> names) {
            if(names.size() != parameters.size()) {
                throw new IllegalArgumentException("Number of names must match number of parameters");
            }
            for (int i = 0; i < names.size(); i++) {
                parameterName(i, names.get(i));
            }
            return this;
        }

        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public JavaFunction build() {
            return new JavaFunction(name, returnType, parameters, method, object);
        }
    }

}
