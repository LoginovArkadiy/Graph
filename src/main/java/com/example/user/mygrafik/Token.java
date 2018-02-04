package com.example.user.mygrafik;

import java.util.HashMap;

public class Token {

    final String[] biSings = new String[]{"+", "-", "*", "/", "^", "log"};
    final String[] uSings = new String[]{"sin", "cos", "tg", "tan", "ctg", "sqrt", "asin", "acos", "atan", "atg", "actg", "lg", "ln"};
    private String s;
    private byte type;
    private boolean sign = false;
    private boolean binary = false;
    private FunctionParse fp;
    private HashMap<String, Double> variables;
    Node node;


    public Token(String s, byte type) {
        this.s = s;
        this.type = type;
        switch (type) {
            case WorkTokens.HIGH_SIGN:
                sign = true;
                binary = search(s);
                break;
            case WorkTokens.LAW_SIGN:
                sign = true;
                break;
        }

        variables = new HashMap<>();
        variables.put("pi", Math.PI);
        variables.put("e", Math.E);

    }

    public Token(FunctionParse fp) {
        s = fp.getPst();
        this.fp = fp;
    }

    @Override
    public String toString() {
        return s;
    }

    protected boolean isSign() {
        return sign;
    }

    protected boolean isBinary() {
        return search(s);
    }

    protected String getS() {
        return s;
    }

    protected void setS(String s) {
        this.s = s;
    }


    protected byte getType() {
        return type;
    }

    private boolean search(String s) {
        for (String s1 : biSings) {
            if (s1.equals(s)) {
                return true;
            }
        }
        return false;
    }

    protected double getScore(Token t) {
        if (t.getS().equals("x")) return Double.NaN;
        else if (t.getType() == WorkTokens.LETTER)
            return variables.get(t.getS());
        else return Double.parseDouble(t.getS());
    }

    protected void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    protected boolean isHigh() {
        if (type == WorkTokens.HIGH_SIGN) return true;
        return false;
    }

}
