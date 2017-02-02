package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Foo on 3/2/2017.
 */

public class Helppanelview extends View {

    private Vibratormanager vibrator;

    public Helppanelview(Context context) {
        super(context);

        vibrator = new Vibratormanager(context);
    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

}
