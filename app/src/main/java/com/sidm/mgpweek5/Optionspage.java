package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

/**
 * Created by Foo on 1/12/2016.
 */

public class Optionspage extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Button btn_back2;

    private Vibratormanager vibrator;

    // Music Volume
    private SeekBar musicBar;
    private SharedPreferences SharedPref_musicVol;
    private SharedPreferences.Editor editMusicVol;
    private int musicVol;

    // SFX Volume
    private SeekBar sfxBar;
    private SharedPreferences SharedPref_sfxVol;
    private SharedPreferences.Editor editSFXVol;
    private int sfxVol;

    // Vibration
    private Switch vibrationSwitch;
    private SharedPreferences SharedPref_vibration;
    private SharedPreferences.Editor editVibration;
    private boolean vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // hide the title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        vibrator = new Vibratormanager(this);

        setContentView(R.layout.optionspage);

        // Shared Prefernces - Music
        SharedPref_musicVol = getSharedPreferences("MusicVolume", Context.MODE_PRIVATE);
        editMusicVol = SharedPref_musicVol.edit();
        musicVol = SharedPref_musicVol.getInt("MusicVolume", 100);

        musicBar = (SeekBar)findViewById(R.id.musicBar);
        musicBar.setOnSeekBarChangeListener(this);
        musicBar.setProgress(musicVol);
        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                musicVol = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Shared Preferences - SFX
        SharedPref_sfxVol = getSharedPreferences("SFXVolume", Context.MODE_PRIVATE);
        editSFXVol = SharedPref_sfxVol.edit();
        sfxVol = SharedPref_sfxVol.getInt("SFXVolume", 100);

        sfxBar = (SeekBar)findViewById(R.id.sfxBar);
        sfxBar.setOnSeekBarChangeListener(this);
        sfxBar.setProgress(sfxVol);
        sfxBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                sfxVol = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Shared Preferences - Vibration
        SharedPref_vibration = getSharedPreferences("Vibration", Context.MODE_PRIVATE);
        editVibration = SharedPref_vibration.edit();
        vibration = SharedPref_vibration.getBoolean("Vibration", true);


        btn_back2 = (Button)findViewById(R.id.btn_back2);
        btn_back2.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        if (v == btn_back2)
        {
            vibrator.Vibrate(50);

            editMusicVol.putInt("MusicVolume", musicVol);
            editMusicVol.commit();

            editSFXVol.putInt("SFXVolume", sfxVol);
            editSFXVol.commit();

            editVibration.putBoolean("Vibration", vibration);
            editVibration.commit();

            intent.setClass(this, Mainmenu.class);
        }

        startActivity(intent);
    }

    // for Seek Bar
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
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
