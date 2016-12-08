package com.sidm.mgpweek5;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;

public class Mainmenu extends Activity implements View.OnClickListener {

    private Button btn_start;
    private Button btn_help;
    private Button btn_options;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        setContentView(R.layout.activity_mainmenu);
        //setContentView(R.layout.mainmenu);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        btn_help = (Button)findViewById(R.id.btn_help);
        btn_help.setOnClickListener(this);

        btn_options = (Button)findViewById(R.id.btn_options);
        btn_options.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        if (v == btn_start)
        {
            if (vibrator.hasVibrator())
                vibrator.vibrate(50);
            intent.setClass(this, Worldmappage.class);
        }

        else if (v == btn_help)
        {
            if (vibrator.hasVibrator())
                vibrator.vibrate(50);
            intent.setClass(this, Helppage.class);
        }

        else if (v == btn_options)
        {
            if (vibrator.hasVibrator())
                vibrator.vibrate(50);
            intent.setClass(this, Optionspage.class);
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
