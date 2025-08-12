package com.github.emilienkia.oraclaestus.model.modules;

import java.util.random.RandomGenerator;

public class MathsModule extends Module {

    private static final RandomGenerator random = RandomGenerator.getDefault();

    private static MathsModule instance = null;

    public static MathsModule getModule() {
        if (instance == null) {
            instance = new MathsModule();
        }
        return instance;
    }

    private MathsModule() {

        addNumberToNumberFunction("abs", "number", MathsModule::abs);

        add2NumbersToNumbersFunction("rand", "min", "max", MathsModule::rand);
        add2FloatsToFloatFunction("randGaussian", "mean", "stdev", MathsModule::randGaussian);

        addFloatToFloatFunction("cos", "angle", MathsModule::cos);
        addFloatToFloatFunction("cosh", "angle", MathsModule::cosh);
        addFloatToFloatFunction("sin", "angle", MathsModule::sin);
        addFloatToFloatFunction("sinh", "angle", MathsModule::sinh);
        addFloatToFloatFunction("tan", "angle", MathsModule::tan);
        addFloatToFloatFunction("tanh", "angle", MathsModule::tanh);
        addFloatToFloatFunction("acos", "value", MathsModule::acos);
        addFloatToFloatFunction("asin", "value",  MathsModule::asin);
        addFloatToFloatFunction("atan", "value", MathsModule::atan);
        add2FloatsToFloatFunction("atan2", "y", "x", MathsModule::atan2);
        add2FloatsToFloatFunction("hypot", "y", "x", MathsModule::hypot);
        addFloatToFloatFunction("toDegrees", "value", MathsModule::toDegrees);
        addFloatToFloatFunction("toRadians", "value", MathsModule::toRadians);

        addFloatToFloatFunction("ceil", "value", MathsModule::ceil);
        add2IntegersToIntegerFunction("ceilDiv", "dividend", null, "divisor", 1, MathsModule::ceilDiv);
        add2IntegersToIntegerFunction("ceilDivExact", "dividend", null, "divisor", 1, MathsModule::ceilDivExact);
        add2IntegersToIntegerFunction("ceilMod", "dividend", null, "divisor", 1, MathsModule::ceilMod);
        addFloatToFloatFunction("floor", "value", MathsModule::floor);
        add2IntegersToIntegerFunction("floorDiv", "dividend", null, "divisor", 1, MathsModule::floorDiv);
        add2IntegersToIntegerFunction("floorDivExact", "dividend", null, "divisor", 1, MathsModule::floorDivExact);
        add2IntegersToIntegerFunction("floorMod", "dividend", null, "divisor", 1, MathsModule::floorMod);
        add3NumbersToNumbersFunction("clamp", "value", "min", "max", MathsModule::clamp);
        add3FloatsToFloatFunction("fma", "a", "b", "c", MathsModule::fma);
        addFloatToIntegerFunction("getExponent", "f", MathsModule::getExponent);
        addFloatToFloatFunction("rint", "value", MathsModule::rint);
        addFloatToIntegerFunction("round", "value", MathsModule::round);

        addFloatToFloatFunction("exp", "value", MathsModule::exp);
        addFloatToFloatFunction("expm1", "value", MathsModule::expm1);
        addFloatToFloatFunction("log", "value", MathsModule::log);
        addFloatToFloatFunction("log10", "value", MathsModule::log10);
        addFloatToFloatFunction("log1p", "value", MathsModule::log1p);
        add2FloatsToFloatFunction("pow", "value", "exponent", MathsModule::pow);
        addFloatToFloatFunction("cbrt", "value", MathsModule::cbrt);
        addFloatToFloatFunction("sqrt", "value", MathsModule::sqrt);

        addFunction("scalb", Float.class, "value", Float.class, null, "scaleFactor", Integer.class, null, MathsModule::scalb);


        add2NumbersToNumbersFunction("max", "a", "b", MathsModule::max);
        add2NumbersToNumbersFunction("min", "a", "b", MathsModule::min);
    }

    private static Number abs(Number number) {
        if (number instanceof Integer intValue) {
            return Math.abs(intValue);
        } else if (number instanceof Float floatValue) {
            return Math.abs(floatValue);
        }
        throw new IllegalArgumentException("Unsupported number type: " + number.getClass().getName());
    }




    private static Number rand(Number min, Number max) {
        if(min instanceof Integer intMin) {
            int intMax = max.intValue();
            if (intMin > intMax) {
                throw new IllegalArgumentException("Min cannot be greater than Max");
            }
            return random.nextInt((intMax - intMin) + 1) + intMin;
        } else if (min instanceof Float floatMin) {
            float floatMax = max.floatValue();
            if (floatMin > floatMax) {
                throw new IllegalArgumentException("Min cannot be greater than Max");
            }
            return floatMin + random.nextFloat() * (floatMax - floatMin);
        }
        throw new IllegalArgumentException("Invalid argument");
    }

    private static Float randGaussian(Float mean, Float stddev) {
        if (stddev <= 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }
        return (float)random.nextGaussian(mean, stddev);
    }



    private static Float cos(Float angle) {
        return (float) Math.cos(angle);
    }

    private static Float cosh(Float angle) {
        return (float) Math.cosh(angle);
    }

    private static Float sin(Float angle) {
        return (float) Math.sin(angle);
    }

    private static Float sinh(Float angle) {
        return (float) Math.sinh(angle);
    }

    private static Float tan(Float angle) {
        return (float) Math.tan(angle);
    }

    private static Float tanh(Float angle) {
        return (float) Math.tanh(angle);
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

    private static Float atan2(Float y, Float x) {
        return (float) Math.atan2(y, x);
    }

    private static Float hypot(Float y, Float x) {
        return (float) Math.hypot(y, x);
    }

    private static Float toDegrees(Float value) {
        return (float) Math.toDegrees(value);
    }

    private static Float toRadians(Float value) {
        return (float) Math.toRadians(value);
    }

    private static Float ceil(Float value) {
        return (float) Math.ceil(value);
    }

    private static Integer ceilDiv(Integer dividend, Integer divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.ceilDiv(dividend, divisor);
    }

    private static Integer ceilDivExact(Integer dividend, Integer divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.ceilDivExact(dividend, divisor);
    }

    private static Integer ceilMod(Integer dividend, Integer divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.ceilMod(dividend, divisor);
    }

    private static Float floor(Float value) {
        return (float) Math.floor(value);
    }


    private static Integer floorDiv(Integer dividend, Integer divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.floorDiv(dividend, divisor);
    }

    private static Integer floorDivExact(Integer dividend, Integer divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.floorDivExact(dividend, divisor);
    }

    private static Integer floorMod(Integer dividend, Integer divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.floorMod(dividend, divisor);
    }

    private static Number clamp(Number value, Number min, Number max) {
        if (value instanceof Integer intValue) {
            return Math.clamp(intValue, min.intValue(), max.intValue());
        } else if (value instanceof Float floatValue) {
            return Math.clamp(floatValue, min.floatValue(), max.floatValue());
        }
        throw new IllegalArgumentException("Unsupported number type: " + value.getClass().getName());
    }

    private static Float fma(Float a, Float b, Float c) {
        return Math.fma(a, b, c);
    }

    private static Integer getExponent(Float f) {
        return Math.getExponent(f);
    }

    private static Float rint(Float value) {
        return (float) Math.rint(value);
    }

    private static Integer round(Float value) {
        return Math.round(value);
    }

    private static Float exp(Float value) {
        return (float) Math.exp(value);
    }
    private static Float expm1(Float value) {
        return (float) Math.expm1(value);
    }

    private static Float log(Float value) {
        return (float) Math.log(value);
    }

    private static Float log10(Float value) {
        return (float) Math.log10(value);
    }

    private static Float log1p(Float value) {
        return (float) Math.log1p(value);
    }

    private static Float pow(Float value, Float exponent) {
        return (float) Math.pow(value, exponent);
    }

    private static Float cbrt(Float value) {
        return (float) Math.cbrt(value);
    }

    private static Float sqrt(Float value) {
        return (float) Math.sqrt(value);
    }

    private static float scalb(Float value, Integer scaleFactor) {
        return Math.scalb(value, scaleFactor);
    }

    private static Number max(Number a, Number b) {
        if (a instanceof Integer intA) {
            return Math.max(intA, b.intValue());
        } else if (a instanceof Float floatA) {
            return Math.max(floatA, b.floatValue());
        }
        throw new IllegalArgumentException("Unsupported number type: " + a.getClass().getName() + " or " + b.getClass().getName());
    }

    private static Number min(Number a, Number b) {
        if (a instanceof Integer intA) {
            return Math.min(intA, b.intValue());
        } else if (a instanceof Float floatA) {
            return Math.min(floatA, b.floatValue());
        }
        throw new IllegalArgumentException("Unsupported number type: " + a.getClass().getName() + " or " + b.getClass().getName());
    }
}
