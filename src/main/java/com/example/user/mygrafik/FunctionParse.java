package com.example.user.mygrafik;


import android.util.Log;

import java.util.ArrayList;

public class FunctionParse {
    private String inf, pst;
    private ArrayList<Token> infTokens, pstTokens;

    FunctionParse(String inf) throws MyException {
        this.inf = inf;
        pst = "";
        infTokens = createTokens3(inf);
        pstTokens = new ArrayList<>();
    }

    protected ArrayList<Token> getInfTokens() {
        return infTokens;
    }

    protected ArrayList<Token> getPostTokens() {
        return pstTokens;
    }

    protected void setPostTokens(ArrayList<Token> tokens) throws MyException {
        pstTokens.clear();
        for (Token t : tokens) {
            if (t.getType() == WorkTokens.HARD_EXPRESSION) {
                String hard = t.getS();
                hard = hard.replace("  ", " ");
                String[] strToks = hard.split(" ");
                WorkTokens wt = new WorkTokens();
                for (int i = 0; i < strToks.length; i++) {
                    byte type = wt.typeToken(strToks[i]);
                    if (type == WorkTokens.DRIVEL) throw new MyException("Ошибка ввода");
                    pstTokens.add(new Token(strToks[i], type));
                }
                pst += hard + " ";
            } else {
                pstTokens.add(t);
                pst += t.getS() + " ";
            }
        }
        pst = pst.replace("  ", " ");
    }

    private void checkInfTokens(ArrayList<Token> in) throws MyException {
        Log.d("checkInf", " tokens1 = " + in.toString());
        for (int i = 0; i < in.size(); i++) {
            Token t = in.get(i);

            if (t.getType() == WorkTokens.COUNT) {
                if (t.getS().charAt(t.getS().length() - 1) == '.') {
                    String s = t.getS().substring(0, t.getS().length() - 1);
                    in.get(i).setS(s);
                }
                continue;
            }
            if (t.isBinary()) {
                if (i != 0) {
                    if (in.get(i - 1).isBinary())
                        throw new MyException("Входные данные не корректны");
                } else {
                    if (t.isHigh()) throw new MyException("'" + t.getS() + "' - бинарный знак");
                    if (t.getS().equals("+")) {
                        in.remove(0);
                    } else {
                       in.add(0, new Token("0", WorkTokens.COUNT));
                    }
                    continue;
                }
                if (i == in.size() - 1) throw new MyException("Входные данные не корректны");
                if (in.get(i + 1).isBinary()) throw new MyException("Входные данные не корректны");
            }
        }
        Log.d("checkInf", " tokens2 = " + in.toString());

    }

    private ArrayList<Token> createTokens3(String s0) throws MyException {
        ArrayList<Token> a = new ArrayList<>();
        WorkTokens wt = new WorkTokens();
        ArrayList<String> list = new ArrayList<>();
        list.add(s0);
        String[] signs = new String[]{"+", "-", "*", "/", "^"};

        for (String s : signs) {
            list = separation(list, s);
        }
        for (String s : list) {
            byte type = wt.typeToken(s);
            Log.d("HARD", s + " " + type);
            if (type != WorkTokens.DRIVEL & type != WorkTokens.HARD_EXPRESSION) {
                a.add(new Token(s, type));
                continue;
            }

            int i = s.indexOf('(');
            if (i < 0) throw new MyException("Ошибка ввода");
            String s1 = s.substring(i);
            String sign = s.substring(0, i);

            if (wt.typeToken(s1) == WorkTokens.HARD_EXPRESSION) {
                s1 = s1.substring(1, s1.length() - 1);
                Decider d = new Decider();
                if (sign.equals("")) {
                    Log.d("HARD", "NULL");
                    FunctionParse fp = d.analyze(s1);
                    a.add(new Token(fp.getPst(), WorkTokens.HARD_EXPRESSION));
                    continue;
                }
                if (sign.equals("log")) {
                    a.add(loging(s1, sign));
                    continue;
                }
                if (wt.typeToken(sign) == WorkTokens.HIGH_SIGN) {
                    Log.d("HARD", "HIGH");
                    FunctionParse fp = d.analyze(s1);
                    String hard = fp.getPst() + " " + sign;
                    hard = hard.replace("  ", " ");
                    a.add(new Token(hard, WorkTokens.HARD_EXPRESSION));
                    continue;
                }
                throw new MyException("Ошибка ввода");
            } else throw new MyException("Ошибка ввода");
        }

        checkInfTokens(a);
        return a;
    }

    private ArrayList<String> separation(ArrayList<String> list, String sign) {
        ArrayList<String> mas = new ArrayList<>();
        for (String s : list) {
            for (int i = 0, j = 0, count = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                if (ch == '(') {
                    count++;
                }
                if (ch == ')') {
                    count--;
                }

                if (count == 0 & sign.equals(ch + "")) {
                    String s1 = s.substring(j, i);
                    if (!s1.equals("")) mas.add(s1);
                    mas.add(sign);
                    j = i + 1;
                    continue;
                }
                if (i == s.length() - 1) {
                    mas.add(s.substring(j));
                }
            }
        }
        return mas;

    }

    private Token loging(String s1, String sign) throws MyException {
        Decider d = new Decider();
        if (sign.equals("log")) {
            Log.d("HARD", "LOG");
            int c = 0, index = 0;
            for (char ch : s1.toCharArray()) {
                if (ch == ';') {
                    c++;
                }
            }
            if (c != 1) throw new MyException("Аргумент и основание алгоритма разделяются ';'");
            index = s1.indexOf(';');
            String osn = s1.substring(0, index);
            String arg = s1.substring(index + 1);
            FunctionParse fpOsn = d.analyze(osn);
            d = new Decider();
            FunctionParse fpArg = d.analyze(arg);
            String hard = fpOsn.getPst() + " " + fpArg.getPst() + " " + sign;
            hard = hard.replace("  ", " ");
            return new Token(hard, WorkTokens.HARD_EXPRESSION);

        }
        throw new MyException("Ошибка ввода");
    }

    protected String getPst() {
        return pst;
    }


}
