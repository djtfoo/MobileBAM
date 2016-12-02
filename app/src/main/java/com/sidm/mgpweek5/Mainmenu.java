package com.sidm.mgpweek5;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;

public class Mainmenu extends Activity implements View.OnClickListener {

    private Button btn_start;
    private Button btn_help;
    private Button btn_options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
            intent.setClass(this, Worldmappage.class);
        }

        else if (v == btn_help)
        {
            intent.setClass(this, Helppage.class);
        }

        else if (v == btn_options)
        {
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
