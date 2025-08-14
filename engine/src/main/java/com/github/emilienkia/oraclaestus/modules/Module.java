package com.github.emilienkia.oraclaestus.modules;

import com.github.emilienkia.oraclaestus.Helper;
import com.github.emilienkia.oraclaestus.Identifier;
import com.github.emilienkia.oraclaestus.functions.Function;
import com.github.emilienkia.oraclaestus.functions.JavaFunction;
import com.github.emilienkia.oraclaestus.variables.GenericVariable;

import java.util.HashMap;
import java.util.Map;

public class Module {

    Map<Identifier, Function> functions = new HashMap<>();

    public Function getFunction(Identifier name) {
        return functions.get(name);
    }


    public void addFunction(Function function) {
        functions.put(function.getName(), function);
    }

    public void addNumberToNumberFunction(String name, String paramName, java.util.function.Function<Number,Number> function) {
        addFunction(name, Number.class, paramName, Number.class, null, function);
    }

    public void addNumberToNumberFunction(String name, String paramName, Number defValue, java.util.function.Function<Number,Number> function) {
        addFunction(name, Number.class, paramName, Number.class, defValue, function);
    }

    public void add2NumbersToNumbersFunction(String name, String paramName1, String paramName2, java.util.function.BiFunction<Number,Number,Number> function) {
        addFunction(name, Number.class, paramName1, Number.class, null, paramName2, Number.class, null, function);
    }

    public void add2NumbersToNumbersFunction(String name, String paramName1, Number defValue1, String paramName2, Number defValue2, java.util.function.BiFunction<Number,Number,Number> function) {
        addFunction(name, Number.class, paramName1, Number.class, defValue1, paramName2, Number.class, defValue2, function);
    }

    public void add3NumbersToNumbersFunction(String name, String paramName1, String paramName2, String paramName3, TriFunction<Number,Number,Number,Number> function) {
        addFunction(name, Number.class, paramName1, Number.class, null, paramName2, Number.class, null, paramName3, Number.class, null, function);
    }

    public void add3NumbersToNumbersFunction(String name, String paramName1, Number defValue1, String paramName2, Number defValue2, String paramName3, Number defValue3 , TriFunction<Number,Number,Number,Number> function) {
        addFunction(name, Number.class, paramName1, Number.class, defValue1, paramName2, Number.class, defValue2, paramName3, Number.class, defValue3, function);
    }


    public void addFloatToFloatFunction(String name, String paramName, java.util.function.Function<Float,Float> function) {
        addFunction(name, Float.class, paramName, Float.class, null, function);
    }

    public void addFloatToFloatFunction(String name, String paramName, Float defValue, java.util.function.Function<Float,Float> function) {
        addFunction(name, Float.class, paramName, Float.class, defValue, function);
    }

    public void add2FloatsToFloatFunction(String name, String paramName1, String paramName2, java.util.function.BiFunction<Float,Float,Float> function) {
        addFunction(name, Float.class, paramName1, Float.class, null, paramName2, Float.class, null, function);
    }

    public void add2FloatsToFloatFunction(String name, String paramName1, Float defValue1, String paramName2, Float defValue2, java.util.function.BiFunction<Float,Float,Float> function) {
        addFunction(name, Float.class, paramName1, Float.class, defValue1, paramName2, Float.class, defValue2, function);
    }

    public void add3FloatsToFloatFunction(String name, String paramName1, String paramName2, String paramName3, TriFunction<Float,Float,Float,Float> function) {
        addFunction(name, Float.class, paramName1, Float.class, null, paramName2, Float.class, null, paramName3, Float.class, null, function);
    }

    public void add3FloatsToFloatFunction(String name, String paramName1, Float defValue1, String paramName2, Float defValue2, String paramName3, Float defValue3 , TriFunction<Float,Float,Float,Float> function) {
        addFunction(name, Float.class, paramName1, Float.class, defValue1, paramName2, Float.class, defValue2, paramName3, Float.class, defValue3, function);
    }



    public void addIntegerToIntegerFunction(String name, String paramName, java.util.function.Function<Integer,Integer> function) {
        addFunction(name, Integer.class, paramName, Integer.class, null, function);
    }

    public void addIntegerToIntegerFunction(String name, String paramName, Integer defValue, java.util.function.Function<Integer,Integer> function) {
        addFunction(name, Integer.class, paramName, Integer.class, defValue, function);
    }

    public void add2IntegersToIntegerFunction(String name, String paramName1, String paramName2, java.util.function.BiFunction<Integer,Integer,Integer> function) {
        addFunction(name, Integer.class, paramName1, Integer.class, null, paramName2, Integer.class, null, function);
    }

    public void add2IntegersToIntegerFunction(String name, String paramName1, Integer defValue1, String paramName2, Integer defValue2, java.util.function.BiFunction<Integer,Integer,Integer> function) {
        addFunction(name, Integer.class, paramName1, Integer.class, defValue1, paramName2, Integer.class, defValue2, function);
    }

    public void add3IntegersToIntegerFunction(String name, String paramName1, String paramName2, String paramName3, TriFunction<Integer,Integer,Integer,Integer> function) {
        addFunction(name, Integer.class, paramName1, Integer.class, null, paramName2, Integer.class, null, paramName3, Integer.class, null, function);
    }

    public void add3IntegersToIntegerFunction(String name, String paramName1, Integer defValue1, String paramName2, Integer defValue2, String paramName3, Integer defValue3 , TriFunction<Integer,Integer,Integer,Integer> function) {
        addFunction(name, Integer.class, paramName1, Integer.class, defValue1, paramName2, Integer.class, defValue2, paramName3, Integer.class, defValue3, function);
    }



    public void addFloatToIntegerFunction(String name, String paramName, java.util.function.Function<Float,Integer> function) {
        addFunction(name, Integer.class, paramName, Float.class, null, function);
    }

    public void addFloatToIntegerFunction(String name, String paramName, Float defValue, java.util.function.Function<Float,Integer> function) {
        addFunction(name, Integer.class, paramName, Float.class, defValue, function);
    }


    public <R, T> void addFunction(String name, Class<R> returnClass, String paramName, Class<T> paramClass, T paramDefValue, java.util.function.Function<T, R> function) {
        addFunction(JavaFunction.builder()
                .name(name)
                .returnType(returnClass)
                .parameter(new GenericVariable(Helper.toTypeDescriptor(paramClass), Identifier.fromString(paramName), paramDefValue))
                .method((context, arguments) -> {
                    if (arguments.size() != 1 || !(paramClass.isInstance(arguments.get(0)))) {
                        throw new IllegalArgumentException(name + " function expects a " + paramClass.getName() + " argument");
                    }
                    T param = (T) arguments.get(0);
                    return function.apply(param);
                })
                .build()
        );
    }

    public <R, T1, T2> void addFunction(String name, Class<R> returnClass, String paramName1, Class<T1> paramClass1, T1 paramDefValue1, String paramName2, Class<T2> paramClass2, T2 paramDefValue2, java.util.function.BiFunction<T1, T2, R> function) {
        addFunction(JavaFunction.builder()
                .name(name)
                .returnType(returnClass)
                .parameter(new GenericVariable(Helper.toTypeDescriptor(paramClass1), Identifier.fromString(paramName1), paramDefValue1))
                .parameter(new GenericVariable(Helper.toTypeDescriptor(paramClass2), Identifier.fromString(paramName2), paramDefValue2))
                .method((context, arguments) -> {
                    if (arguments.size() != 2 || !(paramClass1.isInstance(arguments.get(0))) || !(paramClass2.isInstance(arguments.get(1)))) {
                        throw new IllegalArgumentException(name + " function expects " + paramClass1.getName() + " and " + paramClass2.getName() + " as arguments");
                    }
                    T1 param1 = (T1) arguments.get(0);
                    T2 param2 = (T2) arguments.get(1);
                    return function.apply(param1, param2);
                })
                .build()
        );
    }

    public interface TriFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }

    public <R, T1, T2, T3> void addFunction(String name, Class<R> returnClass, String paramName1, Class<T1> paramClass1, T1 paramDefValue1, String paramName2, Class<T2> paramClass2, T2 paramDefValue2, String paramName3, Class<T3> paramClass3, T3 paramDefValue3, TriFunction<T1, T2, T3, R> function) {
        addFunction(JavaFunction.builder()
                .name(name)
                .returnType(returnClass)
                .parameter(new GenericVariable(Helper.toTypeDescriptor(paramClass1), Identifier.fromString(paramName1), paramDefValue1))
                .parameter(new GenericVariable(Helper.toTypeDescriptor(paramClass2), Identifier.fromString(paramName2), paramDefValue2))
                .parameter(new GenericVariable(Helper.toTypeDescriptor(paramClass3), Identifier.fromString(paramName3), paramDefValue3))
                .method((context, arguments) -> {
                    if (arguments.size() != 3 || !(paramClass1.isInstance(arguments.get(0))) || !(paramClass2.isInstance(arguments.get(1))) || !(paramClass3.isInstance(arguments.get(2)))) {
                        throw new IllegalArgumentException(name + " function expects " + paramClass1.getName() + ", " + paramClass2.getName() + " and " + paramClass3.getName() + " as arguments");
                    }
                    T1 param1 = (T1) arguments.get(0);
                    T2 param2 = (T2) arguments.get(1);
                    T3 param3 = (T3) arguments.get(2);
                    return function.apply(param1, param2, param3);
                })
                .build()
        );
    }

}
