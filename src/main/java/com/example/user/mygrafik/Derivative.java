package com.example.user.mygrafik;

public class Derivative {
    String function, der;

    public Derivative(String function, String der) {
        this.function = function;
        this.der = der;
    }

    protected boolean haveX(String s) {
        for (char ch : s.toCharArray()) {
            if (ch == 'x') return true;
        }
        return false;
    }

    protected boolean isCount(String s) {
        int count = 0;
        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch) | ch == '.') count++;
        }
        return (count == s.length());
    }

    protected boolean isZero(String s) {
        if (isCount(s))
            return Double.parseDouble(s) == 0.0;
        return false;
    }

    protected boolean isOne(String s) {
        if (isCount(s))
            return Double.parseDouble(s) == 1.0;
        return false;
    }

    protected String setBracket(String s) {
        for (int i = 0, b = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(') b++;
            if (ch == ')') b--;
            if (b == 0 && (ch == '+' | ch == '-')) {
                return "(" + s + ")";
            }
        }
        return s;
    }

    protected boolean isVar(String s) {
        return s.equals("x") | s.equals("pi") | s.equals("e");
    }

    protected String calculate(String s) {
        if (!haveX(s) && !isVar(s) && !isCount(s)) {
            Decider decider = new Decider();
            FunctionParse fp;
            Node tree;
            try {
                fp = decider.analyze(s);
                tree = decider.createTree(fp);
                return Double.toString(tree.calculate(0));
            } catch (MyException e) {
                return String.valueOf(Double.NaN);
            }
        }
        return s;

    }

    protected void reduction() {
        function = calculate(function);
        der = calculate(der);
    }

    protected String sumReduction(String s1, String s2, String sign) {
        s1 = calculate(s1);
        s2 = calculate(s2);
        if (isZero(s1) | isZero(s2)) {
            if (isZero(s1) & isZero(s2)) {
                return "0";
            }
            if (sign.equals("+"))
                return isZero(s1) ? s2 : s1;
            if (sign.equals("-")) {
                return isZero(s2) ? s1 : "-" + setBracket(s2);
            }
        }
        return s1 + sign + s2;
    }

    /**
     * следует чутка отдебажить
     */
    protected String multiReduction(String s1, String s2) {
        s1 = calculate(s1);
        s2 = calculate(s2);
        if (isZero(s1) | isZero(s2)) {
            return "0";    //вот тут  вдруг одно ноль а другое не парсится
        }
        if (isOne(s1) | isOne(s2)) {
            return isOne(s1) ? s2 : s1;
        }
        s1 = setBracket(s1);
        s2 = setBracket(s2);
        return setBracket(s1) + "*" + setBracket(s2);
    }

    protected String divReduction(String s1, String s2) {
        s1 = calculate(s1);
        s2 = calculate(s2);
        if (isZero(s1) | isZero(s2)) {
            return "0";    //вот тут
        }
        if (isOne(s2)) {
            return s1;
        }

        s1 = setBracket(s1);
        if (haveX(s2) & !isVar(s2)) {
            s2 = "(" + s2 + ")";
        }
        return setBracket(s1) + "/" + setBracket(s2);
    }

    /**
     * ДЕБААААААААГ показательные вообше не сделал!
     */
    protected String powerReduction(String s1, String s2) {
        s1 = calculate(s1);
        s2 = calculate(s2);
        if (haveX(s1) & !isVar(s1)) {
            s1 = "(" + s1 + ")";
        }
        if (!isCount(s2) & !isVar(s2)) {
            s2 = "(" + s2 + ")";
        }
        if (isOne(s2)) {
            return s1;
        }
        String s = s1 + "^" + s2;
        return setBracket(calculate(s));
    }


    protected String logReduction(String s1, String s2) {
        s1 = calculate(s1);
        s2 = calculate(s2);
        return "log(" + s1 + ";" + s2 + ")";
    }

}