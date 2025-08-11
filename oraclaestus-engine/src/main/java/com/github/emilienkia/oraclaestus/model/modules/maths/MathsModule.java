package com.github.emilienkia.oraclaestus.model.modules.maths;

import com.github.emilienkia.oraclaestus.model.modules.Module;

public class MathsModule extends Module {

    public MathsModule() {
/*        addFunction(JavaFunction.builder()
                .name("rand")
                .returnType(Integer.class)
                .parameter("min", Integer.class, 0)
                .parameter("max", Integer.class, 100)
                .method(MathsModule::rand)
                .build()
        );*/

        add2FloatsToFloatFunction("rand", "min", 0.0f, "max", 100.0f, MathsModule::rand);

        addFloatToFloatFunction("cos", "angle", 0.0f, MathsModule::cos);
        addFloatToFloatFunction("sin", "angle", 0.0f, MathsModule::sin);
        addFloatToFloatFunction("tan", "angle", 0.0f, MathsModule::tan);
        addFloatToFloatFunction("acos", "value", 0.0f, MathsModule::acos);
        addFloatToFloatFunction("asin", "value", 0.0f, MathsModule::asin);
        addFloatToFloatFunction("atan", "value", 0.0f, MathsModule::atan);
    }

    private static Float rand(Float min, Float max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than Max");
        }
        return min + (float)(Math.random() * (max - min));
    }

    private static Float cos(Float angle) {
        return (float) Math.cos(angle);
    }
    private static Float sin(Float angle) {
        return (float) Math.sin(angle);
    }
    private static Float tan(Float angle) {
        return (float) Math.tan(angle);
    }
    private static Float acos(Float value) {
        return (float) Math.acos(value);
    }
    private static Float asin(Float value) {
        return (float) Math.asin(value);
    }
    private static Float atan(Float value) {
        return (float) Math.atan(value);
    }
}
