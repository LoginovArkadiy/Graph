package com.example.user.mygrafik;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ScheduleManager extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;

    ThreadSchedule thread;


    ThreadSchedule getThread() {
        return thread;
    }


    public ScheduleManager(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new ThreadSchedule(holder, getWidth(), getHeight());
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
