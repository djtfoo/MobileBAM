package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.io.InterruptedIOException;

/**
 * Created by Jing Ting on 17/11/2016.
 */

public class Splashpage extends Activity {

    protected boolean _active = true;
    protected int _splashTime = 000;    // in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashpage);

        // thread for displaying the splash screen
        Thread splashThread = new Thread() {

            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(200);
                        if (_active) {
                            waited += 200;
                        }
                    }
                }
                catch (InterruptedException e) {
                    // do nothing
                }
                finally {
                    finish();
                    // Create new activity based on and intent with CurrentActivity
                    Intent intent = new Intent(Splashpage.this, Mainmenu.class);
                    startActivity(intent);
                }
            }
        };

        splashThread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            _active = false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
