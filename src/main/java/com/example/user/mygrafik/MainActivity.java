package com.example.user.mygrafik;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button draw, integral, ref, list;
    Context context;
    static int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        draw = (Button) findViewById(R.id.draw);
        integral = (Button) findViewById(R.id.integral);
        ref = (Button) findViewById(R.id.ref);
        list = (Button) findViewById(R.id.list);
        draw.setOnClickListener(click);
        integral.setOnClickListener(click);
        list.setOnClickListener(click);
        ref.setOnClickListener(click);
        context = this;
    }

    @Override
    public void onBackPressed() {

    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.draw:
                    mode = StaticClass.GRAPH;
                    Log.d("MainActivity", "Draw");
                    startActivity(intent.setClass(context, ScheduleActivity.class));
                    break;
                case R.id.integral:
                    mode = StaticClass.INTEGRAL;
                    Log.d("MainActivity", "Integral");
                    startActivity(intent.setClass(context, IntegralActivity.class));
                    break;
                case R.id.list:
                    intent.putExtra("activity", "list");
                    startActivity(intent.setClass(context, Reference.class));
                    break;
                case R.id.ref:
                    intent.putExtra("activity", "ref");
                    startActivity(intent.setClass(context, Reference.class));
            }
        }
    };

}
