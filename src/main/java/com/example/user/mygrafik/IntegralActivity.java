package com.example.user.mygrafik;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IntegralActivity extends AppCompatActivity {
    LinearLayout surface, main;
    ScheduleManager manager;
    EditText eda, edb, edf;
    TextView otvet;
    Button button;
    Context context;
    private final int N = 12000;
    String s;
    MyFunction function;
    float x0 = 0, y0 = 0, x1, y1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral);
        surface = (LinearLayout) findViewById(R.id.surface);
        main = (LinearLayout) findViewById(R.id.main);
        context = this;
        eda = (EditText) findViewById(R.id.eda);
        edb = (EditText) findViewById(R.id.edb);
        edf = (EditText) findViewById(R.id.edfunc);
        otvet = (TextView) findViewById(R.id.otvet);
        button = (Button) findViewById(R.id.raschet);
        button.setOnClickListener(click);
        manager = new ScheduleManager(this);
        surface.addView(manager);
        surface.setOnTouchListener(touchListener);
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ThreadSchedule thread = manager.getThread();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x0 = event.getX();
                    y0 = event.getY();
                    if (thread.checkPoint)
                        thread.pointed(new MyPoint(x0, y0));
                    break;
                case MotionEvent.ACTION_MOVE:
                    x1 = event.getX();
                    y1 = event.getY();
                    if (thread.checkPoint) thread.pointed(new MyPoint(event.getX(), event.getY()));
                    else if (thread.cancel) {
                        int deltaX = (int) ((x0 - x1) / thread.mas),
                                deltaY = (int) ((y0 - y1) / thread.mas);
                        if (deltaX != 0 || deltaY != 0) {
                            thread.changeBegin(deltaX, deltaY);
                            x0 = x1;
                            y0 = y1;
                        }
                    }
                    break;
            }
            return true;
        }
    };

    private short check(String a, String b) {
        if (a.length() == 0 || b.length() == 0)
            return -1;
        if (Double.parseDouble(a) >= Double.parseDouble(b)) return -2;
        return 0;
    }


    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            s = edf.getText().toString();
            Decider decider = new Decider();
            FunctionParse fp;
            Node tree;
            ThreadSchedule thread = manager.getThread();
            thread.array.clear();
            try {
                fp = decider.analyze(s);
                tree = decider.createTree(fp);
                tree.reduction();
                function = new MyFunction(s, 0, -thread.x0 / thread.mas, tree, fp);
            } catch (MyException e) {
                otvet.setText(e.toString().substring(20));
                return;
            }

            Log.d("IntegralActivity", "s = " + s);
            thread.array.add(function);
            thread.pause = true;
            String strA = eda.getText().toString(), strB = edb.getText().toString();
            short check = check(strA, strB);
            if (check == 0) {
                float a = Float.parseFloat(strA);
                float b = Float.parseFloat(strB);

                thread.line(a, b);
                otvet.setText("" + simpson((double) a, (double) b));
               /* Task task = new Task(a, b);
                task.execute();*/
            } else {
                String error;
                if (check == -2)
                    error = "Верхний предел не может быть больше нижнего!";
                else error = "Заполните все ячейки!";
                otvet.setText(error);
            }

        }
    };

    private double simpson(double a, double b) {
        try {
            int n = function.getStep(a, b);

            double h = (b - a) / (2 * n);
            double sum1 = 0;
            for (int i = 1; i < 2 * n; i += 2) {
                sum1 += f(a + i * h);
            }
            sum1 *= 4;
            double sum2 = 0;
            for (int i = 2; i < 2 * n; i += 2) {
                sum2 += f(a + i * h);
            }
            sum2 *= 2;
            return (h / 3) * (f(a) + f(b) + sum1 + sum2);
        } catch (MyException e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            return Double.NaN;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.getThread().stopRun();
        Intent intent = new Intent().setClass(context, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        manager.getThread().stopRun();
    }

    private double f(double x) {
        return (double) function.calculate((float) x);
    }
}
