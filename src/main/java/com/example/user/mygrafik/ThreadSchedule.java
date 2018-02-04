package com.example.user.mygrafik;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.SurfaceHolder;

import java.util.ArrayList;

public class ThreadSchedule extends Thread {
    boolean running = true, pause = true, line = false, cancel = false, checkPoint = false;
    SurfaceHolder holder;
    float width, height, mas, x0, y0, mas0, ts, nowRightHatch = 0, nowLeftHatch = 0, rightHatch = 0, leftHatch = 0, leftScreen = 0, rightScreen = 0;
    Path list1 = new Path(), pathHatch = new Path();
    ArrayList<MyFunction> array;
    MyPoint point;


    ThreadSchedule(SurfaceHolder holder, int width, int height) {
        this.height = height;
        this.holder = holder;
        this.width = width;
        x0 = width / 2;
        y0 = height / 2;
        mas = width / 30;
        mas0 = mas;
        ts = mas / 2;
        array = new ArrayList<>();
        updateList();
        point = new MyPoint(10000, 10000);
        leftScreen = -x0 / mas;
        rightScreen = (width - x0) / mas;
    }

    /**
     * изменение масштаба
     *
     * @param lvl на сколько
     */
    protected void updateMas(int lvl) {
        x0 = width / 2;
        y0 = height / 2;
        mas = mas0 * lvl;

        for (MyFunction f : array) {
            f.path.reset();
            f.xf = -x0 / mas;
            f.xs = -x0 / mas;
        }
        updateList();
        pause = true;

        ts = mas / 2;
    }

    /**
     * изменение начала координат
     *
     * @param deltaX по Х
     * @param deltaY по У
     */
    protected void changeBegin(int deltaX, int deltaY) {
        deltaX *= mas;
        deltaY *= mas;
        x0 -= deltaX;
        y0 -= deltaY;
        leftScreen = -x0 / mas;
        rightScreen = (width - x0) / mas;
        for (MyFunction f : array)
            f.path.offset(-deltaX, -deltaY);
        pause = true;
        if (MainActivity.mode == StaticClass.INTEGRAL) {
            if (rightHatch > nowRightHatch || leftHatch < nowLeftHatch) line = true;
            pathHatch.offset(-deltaX, -deltaY);
        }
    }

    protected void pointed(MyPoint point) {
        this.point = point;
    }

    protected void stopRun() {
        running = false;
    }

    /**
     * изменение клетки
     */
    private void updateList() {
        list1.reset();
        for (float l = width / 2; l < width; l += mas) {
            list1.moveTo(l, 0);
            list1.lineTo(l, height);
        }
        for (float l = width / 2; l > 0; l -= mas) {
            list1.moveTo(l, 0);
            list1.lineTo(l, height);
        }
        for (float l = height / 2; l > 0; l -= mas) {
            list1.moveTo(0, l);
            list1.lineTo(width, l);
        }
        for (float l = height / 2; l < height; l += mas) {
            list1.moveTo(0, l);
            list1.lineTo(width, l);
        }

    }

    /**
     * @param num   число
     * @param count количество знаков после запятой
     * @return
     */
    private String roundTo(float num, byte count) {
        String s = Float.toString(num);
        int i = 0;
        for (; i < s.length(); i++) {
            if (s.charAt(i) == '.') break;
        }
        try {
            return s.substring(0, i + count);
        } catch (StringIndexOutOfBoundsException e) {
            return s;
        }
    }

    /**
     * @param x  аргумент
     * @param id индекс функции в массиве
     * @return
     */

    private float f(float x, int id) {
        MyFunction function = array.get(id);
        return function.calculate(x);
    }

    /**
     * @param canvas
     * @return ставятся числа
     */
    private Canvas numbering(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(1);
        paint.setTextSize(ts);
        int left = (int) (-x0 / mas);
        left = StaticClass.toFive(left);
        int right = left + (int) (width / mas);
        right = StaticClass.toFive(right);
        int top = (int) (-y0 / mas);
        top = StaticClass.toFive(top);
        int bot = top + (int) (height / mas);
        bot = StaticClass.toFive(bot);
        while (left <= right) {
            if (left != 0) {
                paint.setColor(Color.DKGRAY);
                canvas.drawLine(x0 + left * mas, 0, x0 + left * mas, height, paint);
                paint.setColor(Color.BLACK);
                canvas.drawText(Integer.toString(left), x0 + (left - 0.5f) * mas, y0 + mas, paint);
            }
            left += 5;
        }
        while (top <= bot) {
            if (top != 0) {
                paint.setColor(Color.DKGRAY);
                canvas.drawLine(0, y0 + top * mas, width, y0 + top * mas, paint);
                paint.setColor(Color.BLACK);
                canvas.drawText(Integer.toString(-top), x0 + mas / 2, y0 + (top - 0.2f) * mas, paint);
            }
            top += 5;
        }
        return canvas;
    }

    /**
     * @param function функция из массива
     * @return новый pathHatch для этой функции
     */
    private Path createPath(MyFunction function) {
        Path path = function.path;
        float xs = function.xs;
        float xf = function.xf;
        boolean leftNaN = true;
        boolean rightNaN = true;
        final int n = ((xf < rightScreen) && (xs > leftScreen)) ? 30 : 60;

        for (int i = 0; i < n && xf <= rightScreen; i++, xf += 0.01f) {
            float x1 = x0 + xf * mas;
            float yf = f(xf, function.id);
            float yp = f(xf - 0.02f, function.id);
            float y1 = y0 - yf * mas;
            if (!Float.isNaN(y1)) {
                if (!rightNaN && Math.abs(yp - yf) < 10) {
                    path.lineTo(x1, y1);
                } else {
                    rightNaN = false;
                    path.moveTo(x1, y1);
                }

            } else {
                rightNaN = true;
            }
        }

        for (int i = 0; i < n && xs >= leftScreen; i++, xs -= 0.01f) {
            float x1 = x0 + xs * mas;
            float ys = f(xs, function.id);
            float yp = f(xs + 0.02f, function.id);
            float y1 = y0 - ys * mas;
            if (!Float.isNaN(y1)) {
                if (!leftNaN && Math.abs(yp - ys) < 10) {
                    path.lineTo(x1, y1);
                } else {
                    leftNaN = false;
                    path.moveTo(x1, y1);
                }
            } else {
                leftNaN = true;
            }
        }

        function.xs = (xs <= leftScreen) ? xs : xs + 0.05f;
        function.xf = (xf >= rightScreen) ? xf : xf - 0.05f;
        return path;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (running) {
            if (pause || checkPoint || line) {
                cancel = false;
                canvas = holder.lockCanvas(null);
                try {
                    synchronized (holder) {

                        canvas = drawField(canvas);// поле с клеточками
                        //функции
                        for (MyFunction function : array) {
                            if (function.xf < rightScreen || function.xs > leftScreen) {
                                function.path = createPath(function);
                                pause = true;
                            } else {
                                pause = false;
                                line = true;
                            }
                            canvas = function.onDraw(canvas);
                        }
                        canvas = drawTap(canvas);//определение координаты
                        canvas = drawHatching(canvas);//штриховка
                        canvas = numbering(canvas);//числа
                    }
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            } else cancel = true;
        }
    }

    /**
     * запускает штриховку
     */
    protected void line(float a, float b) {
        pathHatch.reset();
        rightHatch = b;
        leftHatch = a;
        nowRightHatch = Math.max(leftHatch, -x0 / mas);
        nowLeftHatch = nowRightHatch;
        //line = true;
    }

    /**
     * рисует одну линию
     */
    private Path pathLine(Path path) {
        if (nowRightHatch <= Math.min(rightHatch, rightScreen)) {
            path.moveTo(x0 + nowRightHatch * mas, y0);
            path.lineTo(x0 + nowRightHatch * mas, y0 - f(nowRightHatch, 0) * mas);
            nowRightHatch +=0.3f;
        }
        if (nowLeftHatch >= Math.max(leftHatch, leftScreen)) {
            path.moveTo(x0 + nowLeftHatch * mas, y0);
            path.lineTo(x0 + nowLeftHatch * mas, y0 - f(nowLeftHatch, 0) * mas);
            nowLeftHatch -= 0.3f;
        }
        if (nowRightHatch > Math.min(rightHatch, rightScreen) && nowLeftHatch < Math.max(leftHatch, leftScreen))
            line = false;
        return path;
    }

    private Canvas drawHatching(Canvas can) {
        if (line && StaticClass.INTEGRAL == MainActivity.mode) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.DKGRAY);
            p.setStrokeWidth(2);
            pathHatch = pathLine(pathHatch);
            can.drawPath(pathHatch, p);
            p.setColor(Color.BLUE);
            can.drawLine(x0 + leftHatch * mas, y0, x0 + leftHatch * mas, y0 - f(leftHatch, 0) * mas, p);
            can.drawLine(x0 + rightHatch * mas, y0, x0 + rightHatch * mas, y0 - f(rightHatch, 0) * mas, p);
            // if (nowRightHatch > rightHatch && ) line = false;

        } else line = false;
        return can;

    }


    /**
     * определение координат касания
     */
    private Canvas drawTap(Canvas canvas) {
        if (checkPoint && point.x != 10000) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.YELLOW);
            p.setStrokeWidth(2);
            canvas.drawLine(point.x, 0, point.x, height, p);
            canvas.drawLine(0, point.y, width, point.y, p);
            p.setColor(Color.RED);
            canvas.drawLine(point.x + mas0 * 5, 0, point.x + mas0 * 5, height, p);
            canvas.drawLine(0, point.y - mas0 * 5, width, point.y - mas0 * 5, p);
            String xStr = roundTo((point.x + mas0 * 5 - x0) / mas, (byte) 3), yStr = roundTo((y0 - (point.y - mas0 * 5)) / mas, (byte) 3);
            p.setColor(Color.RED);
            p.setTextSize(ts);
            p.setStrokeWidth(1);
            canvas.drawText(" (" + xStr + ";" + yStr + ")", point.x + 5 * mas0, height - 100, p);
        }
        return canvas;
    }

    private Canvas drawField(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(1);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(list1, paint);
        paint.setColor(Color.BLACK);

        paint.setStrokeWidth(3);
        canvas.drawLine(0, y0, width, y0, paint);
        canvas.drawLine(x0, 0, x0, height, paint);
        return canvas;
    }
}
