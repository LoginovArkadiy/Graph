package com.example.user.mygrafik;

public class Operation {
    private String s, derUnLeft, derUnRight;
    private boolean binary;


    Operation(Token sign) {
        if (sign.isBinary())
            binary = true;
        else binary = false;
        s = sign.getS();
        String derUn = signDer();
        int i = derUn.indexOf('x');
        if (!s.equals("abs")) {
            derUnLeft = derUn.substring(0, i);
            derUnRight = derUn.substring(i + 1);
        }
    }

    private String signDer() {
        switch (s) {
            case "sin":
                return "cos(x)";
            case "cos":
                return "-sin(x)";
            case "tg":
                return "1/(cos(x)^2)";
            case "tan":
                return "1/(cos(x)^2)";
            case "ctg":
                return "-1/(sin(x)^2)";
            case "sqrt":
                return "1/ (2*sqrt(x))";
            case "asin":
                return "1/sqrt(1-x^2)";
            case "acos":
                return "-1/sqrt(1-x^2)";
            case "atan":
                return "1/(1+x^2)";
            case "actg":
                return "-1/(1+x^2)";
            case "ln":
                return "1/x";
            case "lg":
                return "1/(ln(10)*x)";
            default:
                return "1/(1+x^2)";
        }
    }

    public boolean isBinary() {
        return binary;
    }

    protected double deciding(double x1, double x2) {
        switch (s) {
            case "*":
                return x1 * x2;
            case "+":
                return x1 + x2;
            case "/":
                return x1 / x2;
            case "-":
                return x1 - x2;
            case "^":
                return Math.pow(x1, x2);
            case "log":
                return Math.log(x2) / Math.log(x1);
            //унарные
            case "sin":
                return Math.sin(x1);
            case "cos":
                return Math.cos(x1);
            case "tg":
                return Math.tan(x1);
            case "tan":
                return Math.tan(x1);
            case "ctg":
                return 1 / Math.tan(x1);
            case "sqrt":
                return Math.sqrt(x1);
            case "abs":
                return Math.abs(x1);
            case "asin":
                return Math.asin(x1);
            case "acos":
                return Math.acos(x1);
            case "atan":
                return Math.atan(x1);
            case "actg":
                return -Math.atan(x1) + Math.PI / 2;
            case "ln":
                return Math.log(x1);
            case "lg":
                return Math.log10(x1);
            default:
                return Math.atan(x1);
        }
    }

    protected Derivative searchDer(Derivative d) {
        d.reduction();
        String function = d.calculate(s + "(" + d.function + ")");
        String inder = d.der;
        if (s.equals("ln") || s.equals("lg") || s.equals("log"))
            d.function = d.setBracket(d.function);
        String outder = d.calculate(derUnLeft + d.function + derUnRight);
        String der = d.multiReduction(inder, outder);
        der = d.calculate(der);
        d.reduction();
        return new Derivative(function, der);
    }

    protected Derivative searchDer(Derivative d1, Derivative d2) {
        d1.reduction();
        d2.reduction();
        if (s.equals("+") || s.equals("-")) {
            return plusOrMinus(d1, d2);
        } else if (s.equals("*")) {
            return multi(d1, d2);
        } else if (s.equals("/")) {
            return div(d1, d2);
        } else if (s.equals("log")) {
            return loging(d1, d2);
        }

        return power(d1, d2);
    }


    private Derivative plusOrMinus(Derivative d1, Derivative d2) {
        String function = d1.sumReduction(d1.function, d2.function, s);
        String der = d1.sumReduction(d1.der, d2.der, s);
        Derivative d = new Derivative(function, der);
        d.reduction();
        return d;
    }

    private Derivative multi(Derivative d1, Derivative d2) {
        String f1 = d1.function, f2 = d2.function;
        String der1 = d1.der, der2 = d2.der;
        String function = d1.multiReduction(f1, f2);
        String der = d1.sumReduction(d1.multiReduction(der1, f2), d1.multiReduction(f1, der2), "+");
        Derivative d = new Derivative(function, der);
        d.reduction();
        return d;
    }

    private Derivative div(Derivative d1, Derivative d2) {
        String f1 = d1.function, f2 = d2.function;
        String der1 = d1.der, der2 = d2.der;
        String function = d1.divReduction(f1, f2);
        String nominator = d1.sumReduction(d1.multiReduction(der1, f2), d1.multiReduction(f1, der2), "-");
        String denominator = d1.powerReduction(f2, "2.0");
        String der = d1.divReduction(nominator, denominator);
        Derivative d = new Derivative(function, der);
        d.reduction();
        return d;
    }

    private Derivative power(Derivative d1, Derivative d2) {
        String f1 = d1.function, f2 = d2.function;
        String der1 = d1.der, der2 = d2.der;
        if (d2.haveX(f2)) return indicator(d1, d2);
        String der = d1.multiReduction(der1, d1.multiReduction(f2, d1.powerReduction(f1, f2 + "-1")));
        String function = d1.powerReduction(f1, f2);

        Derivative d = new Derivative(function, der);
        d.reduction();
        return d;
    }

    private Derivative indicator(Derivative d1, Derivative d2) {
        String f1 = d1.function, f2 = d2.function;
        String der1 = d1.der, der2 = d2.der;
        String function = d1.powerReduction(f1, f2);
        String der;
        der = d1.multiReduction(der2, function);
        der = d1.multiReduction("ln(" + f1 + ")", der);

        if (d1.haveX(f1)) {
            String s1 = d1.multiReduction(der1, d1.multiReduction(f2, d1.powerReduction(f1, f2 + "-1")));
            der = d1.sumReduction(s1, der, "+");
        }
        Derivative d = new Derivative(function, der);
        d.reduction();
        return d;
    }

    private Derivative loging(Derivative d1, Derivative d2) {
        String f1 = d1.function, f2 = d2.function;
        String der1 = d1.der, der2 = d2.der;
        String function = d1.logReduction(f1, f2);
        String der = d1.divReduction(der2, "x*ln(" + f1 + ")");
        Derivative d = new Derivative(function, der);
        d.reduction();
        return d;
    }

    protected String getS() {
        return s;
    }
}
