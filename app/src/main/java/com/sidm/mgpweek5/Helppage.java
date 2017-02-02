package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Foo on 1/12/2016.
 */

public class Helppage extends Activity {

    //private Button btn_back;

    //private Vibratormanager vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //vibrator = new Vibratormanager(this);

        //setContentView(R.layout.helppage);
        setContentView(new Helppanelview(this));

        //btn_back = (Button)findViewById(R.id.btn_back);
        //btn_back.setOnClickListener(this);
    }

    //public void onClick(View v) {
    //    Intent intent = new Intent();
    //    if (v == btn_back)
    //    {
    //        vibrator.Vibrate(50);
    //        intent.setClass(this, Mainmenu.class);
    //    }
//
    //    startActivity(intent);
    //}

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
