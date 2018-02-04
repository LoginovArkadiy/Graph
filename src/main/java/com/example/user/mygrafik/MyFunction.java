package com.example.user.mygrafik;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

public class MyFunction {
    String s;
    final double p = 0.1;
    int cl, id;
    final int colors[] = new int[]{Color.rgb(58, 130, 0), Color.MAGENTA, Color.BLUE, Color.RED, Color.GREEN};
    float xs, xf;
    Path path;
    private Node tree;
    private FunctionParse fp;
    private Paint paint;


    MyFunction(String s, int id, float xbegin, Node tree, FunctionParse fp) {
        this.s = s;
        this.id = id;
        this.xs = xbegin;
        this.xf = xbegin;
        this.tree = tree;
        this.fp = fp;
        cl = (id < colors.length) ? colors[id] : Color.rgb(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(cl);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

    }

    protected float calculate(float x) {
        return (float) tree.calculate(x);
    }

    protected Canvas onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
        return canvas;
    }


    protected int getStep(double a, double b) throws MyException {
        Decider decider = new Decider();
        Derivative derivative;
        Node node = decider.createTree(fp);
        for (int i = 0 + 1; i < 4 + 1; i++) {
            node.reduction();
            derivative = node.derivative();
            Decider dec = new Decider();
            FunctionParse fp1 = dec.analyze(derivative.der);
            node = dec.createTree(fp1);

        }
        double max = -Double.MAX_VALUE;
        for (double i = a; i <= b; i += 0.1f) {
            max = Math.max(max, tree.calculate(i));
        }
        max = Math.abs(max);
        int count = (int) Math.pow(max * Math.pow(b - a, 5) / (p * 2880), 0.25);
        count = StaticClass.toFive(count);
        if (count <= 0) count = 5;
        return count;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
