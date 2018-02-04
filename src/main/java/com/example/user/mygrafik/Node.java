package com.example.user.mygrafik;
public class Node {
    private Node left, right;
    private Operation operation;
    private double leftValue, rightValue;
    boolean argLeft = false, argRight = false;


    Node(Token left, Token right, Operation operation) {
        leftValue = Double.NaN;
        rightValue = Double.NaN;
        this.operation = operation;

        if (left.getType() == WorkTokens.NODE) {
            this.left = left.getNode();
        } else {
            leftValue = left.getScore(left);
            if (Double.isNaN(leftValue)) argLeft = true;
        }

        if (right.getType() == WorkTokens.NODE) {
            this.right = right.getNode();
        } else if (operation.isBinary()) {
            rightValue = right.getScore(right);
            if (Double.isNaN(rightValue)) argRight = true;
        }
    }

    Node(Token left, Operation operation) {
        leftValue = Double.NaN;
        rightValue = Double.NaN;
        this.operation = operation;

        if (left.getType() == WorkTokens.NODE) {
            this.left = left.getNode();
        } else {
            leftValue = left.getScore(left);
            if (Double.isNaN(leftValue)) argLeft = true;
        }
    }


    protected double calculate(double x) {
        double x1, x2;

        if (argLeft) x1 = x;
        else x1 = leftValue;
        if (argRight) x2 = x;
        else x2 = rightValue;

        if (Double.isNaN(x1)) {
            x1 = left.calculate(x);
        }
        if (operation.isBinary()) {
            if (Double.isNaN(x2))
                x2 = right.calculate(x);
        } else x2 = 0;

        return operation.deciding(x1, x2);
    }

    protected Derivative derivative() {
        Derivative d1, d2;
        if (argLeft) d1 = new Derivative("x", "1");
        else if (Double.isNaN(leftValue)) {
            d1 = left.derivative();
        } else d1 = new Derivative(leftValue + "", "0");

        if (!operation.isBinary()) return operation.searchDer(d1);

        if (argRight) d2 = new Derivative("x", "1");
        else if (Double.isNaN(rightValue)) {
            d2 = right.derivative();
        } else d2 = new Derivative(rightValue + "", "0");

        return operation.searchDer(d1, d2);
    }

    protected double reduction() {
        if(argLeft && argRight) return Double.NaN;

        if (argRight) {
            if (Double.isNaN(leftValue) )
                leftValue = left.reduction();

            if (leftValue == 0 && operation.getS().equals("*")) return 0;
            else return Double.NaN;
        }
        if (argLeft) {
            if (Double.isNaN(rightValue) && operation.isBinary())
                rightValue = right.reduction();

            if (rightValue == 0 && operation.getS().equals("*")) return 0;
            else return Double.NaN;
        }

        if (Double.isNaN(leftValue)) {
            leftValue = left.reduction();
        }
        if (operation.isBinary() && Double.isNaN(rightValue)) {
            rightValue = right.reduction();
        }

        if (Double.isNaN(leftValue) || Double.isNaN(rightValue))
            return Double.NaN;
        else
            return operation.deciding(leftValue, rightValue);
    }

}
