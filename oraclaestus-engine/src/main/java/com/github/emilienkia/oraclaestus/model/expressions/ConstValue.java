package com.github.emilienkia.oraclaestus.model.expressions;

import com.github.emilienkia.oraclaestus.model.Asset;
import com.github.emilienkia.oraclaestus.model.EvaluationContext;
import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.State;
import lombok.Getter;

public class ConstValue implements Expression {

    @Getter
    private final Object value;

    public ConstValue(Object value) {
        this.value = value;
    }

    @Override
    public Object apply(EvaluationContext context) {
        return value;
    }

    @Override
    public void dump() {
        System.out.print("<<value:" + value + ">>");
    }

    public static Number parseNumber(String str) {
        if(str==null || str.isBlank()) {
            return null;
        }
        if(str.contains(".")) {
            return Float.parseFloat(str);
        } else {
            return Integer.parseInt(str);
        }
    }

    public static String parseString(String str) {
        if(str==null || str.isBlank()) {
            return null;
        }
        str = str.trim();
        if(str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length() - 1);
        } else if(str.startsWith("'") && str.endsWith("'")) {
            str = str.substring(1, str.length() - 1);
        }
        // TODO Add escaping
        return str;
    }

    public static Boolean parseBoolean(String str) {
        if(str==null || str.isBlank()) {
            return null;
        }
        return Boolean.parseBoolean(str);
    }

}
