package com.example.user.mygrafik;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

public class Decider {
    private HashMap<String, Double> variables;

    public Decider() {
        variables = new HashMap<>();
        variables.put("pi", Math.PI);
        variables.put("e", Math.E);
        variables.put("x", 1.0);
    }

    protected FunctionParse analyze(String s) throws MyException {
        s = s.replace(",", ".");
        s = s.replace(" ", "");
        if (s.equals("")) throw new MyException("Введите функцию");
        FunctionParse fp = new FunctionParse(s);
        try {
            fp = readFunc(fp);
        } catch (MyException e) {
            throw e;
        }
        return fp;
    }

    protected Node createTree(FunctionParse fp) throws MyException {
        ArrayList<Token> tokens = fp.getPostTokens();
        Log.d("createTree0", tokens.toString());
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).isSign()) {
                Token x2 = tokens.get(i - 1), x1;
                if (tokens.get(i).isBinary()) {
                    if (i - 2 >= 0) {
                        x1 = tokens.get(i - 2);
                        Token token = new Token("", WorkTokens.NODE);
                        token.setNode(new Node(x1, x2, new Operation(tokens.get(i))));
                        tokens.add(i + 1, token);
                        int j = i > 1 ? 0 : 1;
                        int z = i > 1 ? 2 : 1;
                        for (; j < 3; j++) tokens.remove(i - z);
                        //Log.d("createTree1", tokens.toString());
                    } else throw new MyException("Некорректный ввод");
                } else {
                    Token token = new Token("", WorkTokens.NODE);
                    token.setNode(new Node(x2, new Operation(tokens.get(i))));
                    tokens.add(i + 1, token);
                    for (int j = 0; j < 2; j++) tokens.remove(i - 1);
                    //   Log.d("createTree1", tokens.toString());
                }
                // Log.d("createTree2", tokens.toString());
                i = 0;
            }
        }
        Token t = tokens.get(0);
        if (t.getType() != WorkTokens.NODE) {
            t.setNode(new Node(t, new Token("0", WorkTokens.COUNT), new Operation(new Token("+", WorkTokens.LAW_SIGN))));
        }
        return t.getNode();
    }

    /**
     * обработка строки  и преобразование ее в постфиксную строку
     */
    private FunctionParse readFunc(FunctionParse fp) throws MyException {
        ArrayList<Token> infTokens = fp.getInfTokens();
        Log.d("readFunc", "BEGIN infTokens = " + infTokens);
        for (int i = 0; i < infTokens.size(); i++) {
            if (infTokens.get(i).getS().equals("^")) {
                infTokens = veryHighPriority(infTokens, i, "^");
                i = 0;
            }
        }
        for (int i = 0; i < infTokens.size(); i++) {
            if (infTokens.get(i).getS().equals("log")) {
                infTokens = veryHighPriority(infTokens, i, "log");
                i = 0;
            }
        }

        Deque<Token> values = new ArrayDeque<>();
        Deque<Token> signs = new ArrayDeque<>();
        ArrayList<Token> pst = new ArrayList<>();

        for (Token t : infTokens) {
            switch (t.getType()) {
                case WorkTokens.COUNT:
                    setValue(signs, values, pst, t);
                    break;
                case WorkTokens.LETTER:
                    setValue(signs, values, pst, t);
                    break;
                case WorkTokens.HARD_EXPRESSION:
                    setValue(signs, values, pst, t);
                    break;
                case WorkTokens.HIGH_SIGN:
                    setSign(signs, values, pst, t);
                    break;
                case WorkTokens.LAW_SIGN:
                    setSign(signs, values, pst, t);
                    break;

            }
            Log.d("ReadFunc", t.toString() + "  pst0  =  " + pst.toString() + "  " + "\n values = " + values.toString() + "\n signs = " + signs.toString());
        }
        Log.d("ReadFunc", "pst1  =  " + pst.toString() + "\n values = " + values.toString() + "\n signs = " + signs.toString());

        while (!values.isEmpty()) {
            pst.add(values.pop());
        }

        Log.d("ReadFunc", "pst2  =  " + pst.toString() + "\n values = " + values.toString() + "\n signs = " + signs.toString());
        while (!signs.isEmpty()) {
            pst.add(signs.pollLast());
        }
        Log.d("ReadFunc", "pst3  =  " + pst.toString());
        fp.setPostTokens(pst);
        return fp;
    }

    private void setSign(Deque<Token> signs, Deque<Token> values, ArrayList<Token> pst, Token t) throws MyException {
        if (t.getType() == WorkTokens.LAW_SIGN) {
            if (!signs.isEmpty()) {
                Token sign = signs.pollLast();
                if (!values.isEmpty()) {
                    pst.add(values.pollLast());
                }
                pst.add(sign);
            } else {
                if (!values.isEmpty())
                    pst.add(values.pollLast());

            }
        }
        signs.addLast(t);
    }

    private void setValue(Deque<Token> signs, Deque<Token> values, ArrayList<Token> pst, Token t) throws MyException {
        Log.d("setValue", "BEGIN");
        if (signs.size() > 0) {

            Token sign = signs.peekLast();
            Log.d("setValue", "FIRST IF   sign = " + sign.toString());
            if (sign.isBinary()) {
                Log.d("setValue", "BINARY IF    sign = " + sign.toString() + " " + sign.isBinary());
                if (sign.isHigh()) {
                    Log.d("setValue", "HIGH IF");
                    if (!values.isEmpty()) {
                        pst.add(values.pollLast());
                    }
                    Log.d("setValue", "sign = " + sign + "\n values = " + values.toString() + "\n token = " + t);
                    pst.add(t);
                    pst.add(signs.pollLast());
                    return;
                }
            } else {
                Log.d("setValue", "UNARY IF  " + sign);
                pst.add(t);
                pst.add(signs.pop());
                return;
            }
        }
        values.addLast(t);
    }

    /**
     * log a(b); a ^ b
     */
    private ArrayList<Token> veryHighPriority(ArrayList<Token> a, int i, String sign) {
        String s = a.get(i - 1).getS() + " " + a.get(i + 1).getS() + " " + sign;
        Token token = new Token(s, WorkTokens.HARD_EXPRESSION);
        a.add(i - 1, token);
        for (int j = 0; j < 3; j++) {
            a.remove(i);
        }
        Log.d("readFunc", "POWERING infTokens = " + a.toString());

        return a;
    }
}
