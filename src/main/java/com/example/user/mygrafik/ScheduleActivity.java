package com.example.user.mygrafik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class ScheduleActivity extends AppCompatActivity {
    Context context;
    LinearLayout surface;
    ScheduleManager manager;
    Button adder, back, bias;
    SeekBar sb;
    EditText et1;
    AlertDialog dialog;
    boolean check = false;
    int n = 0;
    LinearLayout sw;
    float x0 = 0, y0 = 0, x1, y1;
    TextView twlistFunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        surface = (LinearLayout) findViewById(R.id.surface);
        adder = (Button) findViewById(R.id.adder);
        back = (Button) findViewById(R.id.back);
        bias = (Button) findViewById(R.id.bias);
        et1 = (EditText) findViewById(R.id.et1);
        sw = (LinearLayout) findViewById(R.id.scr);
        sb = (SeekBar) findViewById(R.id.seekBar);
        twlistFunc = (TextView) findViewById(R.id.listfunc);
        manager = new ScheduleManager(this);
        surface.addView(manager);
        context = this;
        adder.setOnClickListener(clickListener);
        back.setOnClickListener(clickListener);
        bias.setOnClickListener(clickListener);
        surface.setOnTouchListener(touch);
        sb.setOnSeekBarChangeListener(seekBarChangeListener);
         //createDialog();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ThreadSchedule thread = manager.getThread();
            switch (v.getId()) {
                case R.id.back:
                    if (thread.cancel) {
                        n--;
                        thread.array.remove(n);
                        sw.removeView(findViewById(n));
                        thread.pause = true;
                        if (n == 0) back.setVisibility(View.INVISIBLE);
                    } else {
                        Toast toast;
                        toast = Toast.makeText(context, "Попробуйте снова, когда достроится график \n или включите режим смещения", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.END | Gravity.TOP, 0, 0);
                        toast.show();
                    }
                    break;
                case R.id.bias:
                    thread.checkPoint = !thread.checkPoint;
                    if (thread.checkPoint)
                        bias.setBackgroundResource(R.drawable.flip);

                    else {
                        thread.point = new MyPoint(10000, 10000);
                        bias.setBackgroundResource(R.drawable.tap);
                    }
                    break;
                case R.id.adder:
                    addFunction(et1.getText().toString(), thread);
                   /* String list = twlistFunc.getText().toString();
                    dialog.show();*/
                    break;
            }
        }
    };

    private void createDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
        Button b1 = (Button) view.findViewById(R.id.b1);
        Button b2 = (Button) view.findViewById(R.id.b2);
        ad.setView(view);
        final EditText et = (EditText) view.findViewById(R.id.et);
        final ThreadSchedule thread = manager.getThread();
        ad.setCancelable(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // twlistFunc.setText(et.getText());
                addFunction(et.getText().toString(), thread);
                dialog.cancel();
            }
        });
        dialog = ad.create();
    }

    private void addFunction(String s, ThreadSchedule thread) {
        Decider decider = new Decider();
        FunctionParse fp;
        Node tree;
        Toast toast;
        try {
            fp = decider.analyze(s);
            tree = decider.createTree(fp);
            tree.reduction();
            Derivative derivative = tree.derivative();
            // Toast.makeText(context, derivative.der, Toast.LENGTH_LONG).show();
            MyFunction myFunction = new MyFunction(s, n, -thread.x0 / thread.mas, tree, fp);
            thread.array.add(myFunction);
            thread.pause = true;
            toast = Toast.makeText(context, "Подождите...", Toast.LENGTH_SHORT);
            TextView tw1 = new TextView(ScheduleActivity.this);
            LinearLayout.LayoutParams tw1params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tw1.setLayoutParams(tw1params);
            tw1.setText((n + 1) + ". y = " + s + "  y'= " + derivative.der);
            tw1.setTextColor(myFunction.cl);
            tw1.setTextSize(thread.mas0 * 0.67f);
            twlistFunc.setTextSize(thread.mas0 * 0.67f);
            sw.addView(tw1);
            tw1.setId(n);
            n++;
            if (n > 0) back.setVisibility(View.VISIBLE);
        } catch (MyException e) {
            toast = Toast.makeText(context, "Ошибка: " + e.toString(), Toast.LENGTH_LONG);
        }
        toast.setGravity(Gravity.END | Gravity.TOP, 0, 0);
        toast.show();
    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ThreadSchedule thread = manager.getThread();

            if (progress % 10 == 0 && progress != 0 && thread.cancel) {
                thread.updateMas(progress / 10);
                Toast toast = Toast.makeText(context, "Подождите...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.END | Gravity.TOP, 0, 0);
                toast.show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    };


    View.OnTouchListener touch = new View.OnTouchListener() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent().setClass(context, MainActivity.class);
        startActivity(intent);
        manager.getThread().stopRun();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        for (int i = n - 1; i >= 0; i--) {
            sw.removeView(findViewById(i));
            manager.getThread().array.remove(i);
        }
        n = 0;
        back.setVisibility(View.INVISIBLE);
        manager.getThread().stopRun();
    }

}
