package com.github.emilienkia.oraclaestus.modules;

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

        addConstant("pi", Math.PI);
        addConstant("e", Math.E);
        addConstant("tau", Math.TAU);

        addNumberToNumberFunction("abs", "value", MathsModule::abs);

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
        addFloatToFloatFunction("toDegrees", "angle", MathsModule::toDegrees);
        addFloatToFloatFunction("toRadians", "angle", MathsModule::toRadians);

        addFloatToFloatFunction("ceil", "value", MathsModule::ceil);
        add2IntegersToIntegerFunction("ceilDiv", "dividend", null, "divisor", 1L, MathsModule::ceilDiv);
        add2IntegersToIntegerFunction("ceilMod", "dividend", null, "divisor", 1L, MathsModule::ceilMod);
        addFloatToFloatFunction("floor", "value", MathsModule::floor);
        add2IntegersToIntegerFunction("floorDiv", "dividend", null, "divisor", 1L, MathsModule::floorDiv);
        add2IntegersToIntegerFunction("floorMod", "dividend", null, "divisor", 1L, MathsModule::floorMod);
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

        addFunction("scalb", Double.class, "value", Double.class, null, "scaleFactor", Long.class, null, MathsModule::scalb);

        add2NumbersToNumbersFunction("max", "a", "b", MathsModule::max);
        add2NumbersToNumbersFunction("min", "a", "b", MathsModule::min);
    }

    private static Number abs(Number number) {
        if (number instanceof Long intValue) {
            return Math.abs(intValue);
        } else if (number instanceof Double floatValue) {
            return Math.abs(floatValue);
        }
        throw new IllegalArgumentException("Unsupported number type: " + number.getClass().getName());
    }



    private static Number rand(Number min, Number max) {
        if(min instanceof Long intMin) {
            int intMax = max.intValue();
            if (intMin > intMax) {
                throw new IllegalArgumentException("Min cannot be greater than Max");
            }
            return random.nextInt((int)(intMax - intMin) + 1) + intMin;
        } else if (min instanceof Double floatMin) {
            double floatMax = max.doubleValue();
            if (floatMin > floatMax) {
                throw new IllegalArgumentException("Min cannot be greater than Max");
            }
            return floatMin + random.nextFloat() * (floatMax - floatMin);
        }
        throw new IllegalArgumentException("Invalid argument");
    }

    private static Double randGaussian(Double mean, Double stddev) {
        if (stddev <= 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }
        return random.nextGaussian(mean, stddev);
    }



    private static Double cos(Double angle) {
        return Math.cos(angle);
    }

    private static Double cosh(Double angle) {
        return Math.cosh(angle);
    }

    private static Double sin(Double angle) {
        return Math.sin(angle);
    }

    private static Double sinh(Double angle) {
        return Math.sinh(angle);
    }

    private static Double tan(Double angle) {
        return Math.tan(angle);
    }

    private static Double tanh(Double angle) {
        return Math.tanh(angle);
    }

    private static Double acos(Double value) {
        return Math.acos(value);
    }

    private static Double asin(Double value) {
        return Math.asin(value);
    }

    private static Double atan(Double value) {
        return Math.atan(value);
    }

    private static Double atan2(Double y, Double x) {
        return Math.atan2(y, x);
    }

    private static Double hypot(Double y, Double x) {
        return Math.hypot(y, x);
    }

    private static Double toDegrees(Double value) {
        return Math.toDegrees(value);
    }

    private static Double toRadians(Double value) {
        return Math.toRadians(value);
    }

    private static Double ceil(Double value) {
        return Math.ceil(value);
    }

    private static Long ceilDiv(Long dividend, Long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.ceilDiv(dividend, divisor);
    }

    private static Long ceilMod(Long dividend, Long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.ceilMod(dividend, divisor);
    }

    private static Double floor(Double value) {
        return Math.floor(value);
    }

    private static Long floorDiv(Long dividend, Long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.floorDiv(dividend, divisor);
    }

    private static Long floorMod(Long dividend, Long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Math.floorMod(dividend, divisor);
    }

    private static Number clamp(Number value, Number min, Number max) {
        if (value instanceof Long intValue) {
            return Math.clamp(intValue, min.intValue(), max.intValue());
        } else if (value instanceof Double floatValue) {
            return Math.clamp(floatValue, min.floatValue(), max.floatValue());
        }
        throw new IllegalArgumentException("Unsupported number type: " + value.getClass().getName());
    }

    private static Double fma(Double a, Double b, Double c) {
        return Math.fma(a, b, c);
    }

    private static Long getExponent(Double f) {
        return (long)Math.getExponent(f);
    }

    private static Double rint(Double value) {
        return Math.rint(value);
    }

    private static Long round(Double value) {
        return Math.round(value);
    }

    private static Double exp(Double value) {
        return Math.exp(value);
    }
    private static Double expm1(Double value) {
        return Math.expm1(value);
    }

    private static Double log(Double value) {
        return Math.log(value);
    }

    private static Double log10(Double value) {
        return Math.log10(value);
    }

    private static Double log1p(Double value) {
        return Math.log1p(value);
    }

    private static Double pow(Double value, Double exponent) {
        return Math.pow(value, exponent);
    }

    private static Double cbrt(Double value) {
        return Math.cbrt(value);
    }

    private static Double sqrt(Double value) {
        return Math.sqrt(value);
    }

    private static Double scalb(Double value, Long scaleFactor) {
        return Math.scalb(value, scaleFactor.intValue());
    }

    private static Number max(Number a, Number b) {
        if (a instanceof Long intA) {
            return Math.max(intA, b.longValue());
        } else if (a instanceof Double floatA) {
            return Math.max(floatA, b.doubleValue());
        }
        throw new IllegalArgumentException("Unsupported number type: " + a.getClass().getName() + " or " + b.getClass().getName());
    }

    private static Number min(Number a, Number b) {
        if (a instanceof Long intA) {
            return Math.min(intA, b.longValue());
        } else if (a instanceof Double floatA) {
            return Math.min(floatA, b.doubleValue());
        }
        throw new IllegalArgumentException("Unsupported number type: " + a.getClass().getName() + " or " + b.getClass().getName());
    }
}
