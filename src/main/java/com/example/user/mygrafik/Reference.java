package com.example.user.mygrafik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

public class Reference extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String s = intent.getStringExtra("activity");
        if (s.equals("ref"))
            setContentView(R.layout.activity_reference);
        else setContentView(R.layout.activity_avtor);
    }
}
