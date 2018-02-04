package com.example.user.mygrafik;

public class WorkTokens {


    static final byte LAW_SIGN = 0;
    static final byte HIGH_SIGN = 1;
    static final byte COUNT = 2;
    static final byte LETTER = 3;
    static final byte HARD_EXPRESSION = 4;
    static final byte DRIVEL = 5;
    static final byte NODE = 6;

    static final String[] signs = new String[]{"+", "-", "*", "/", "^", "sin", "cos", "tg", "tan", "ctg",
            "acos", "asin", "atan", "actg", "atg", "abs", "sqrt", "lg", "ln", "log"};
    static final String[] variables = new String[]{"x", "e", "pi"};

    WorkTokens() {

    }

    protected byte typeToken(String s) {
        for (String sign : signs) {
            if (sign.equals(s))
                if (s.equals("+") || s.equals("-"))
                    return LAW_SIGN;
                else return HIGH_SIGN;
        }

        byte countOfPoints = 0;
        boolean isCount = true;
        for (int i = 0; i < s.length(); i++) {
            Character c = s.charAt(i);
            if (c == '.') countOfPoints++;
            if ((!Character.isDigit(c) && c != '.') || countOfPoints > 1) {
                isCount = false;
                break;
            }
        }
        if (isCount) return COUNT;

        for (String var : variables) {
            if (s.equals(var))
                return LETTER;
        }

        if (s.charAt(0) == '(') {
            int countBrackets = 1;
            for (int i = 1; i < s.length(); i++) {
                if (s.charAt(i) == '(') countBrackets++;
                else if (s.charAt(i) == ')') countBrackets--;
            }

            if (countBrackets == 0)
                return HARD_EXPRESSION;
        }
        return DRIVEL;
    }
}
