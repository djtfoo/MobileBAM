package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Foo on 1/12/2016.
 */

public class Optionspage extends Activity implements View.OnClickListener {

    private Button btn_back2;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        setContentView(R.layout.optionspage);

        btn_back2 = (Button)findViewById(R.id.btn_back2);
        btn_back2.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        if (v == btn_back2)
        {
            if (vibrator.hasVibrator())
                vibrator.vibrate(50);
            intent.setClass(this, Mainmenu.class);
        }

        startActivity(intent);
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
