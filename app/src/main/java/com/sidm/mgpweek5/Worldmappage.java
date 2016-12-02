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

public class Worldmappage extends Activity implements View.OnClickListener {

    private Button btn_back3;
    private Button btn_boss1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.worldmappage);

        btn_back3 = (Button)findViewById(R.id.btn_back3);
        btn_back3.setOnClickListener(this);

        btn_boss1 = (Button)findViewById(R.id.btn_boss1);
        btn_boss1.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        if (v == btn_back3)
        {
            intent.setClass(this, Mainmenu.class);
        }

        if (v == btn_boss1)
        {
            intent.setClass(this, Gamepage.class);
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
