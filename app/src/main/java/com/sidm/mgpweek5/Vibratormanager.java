package com.sidm.mgpweek5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

/**
 * Created by Foo on 2/2/2017.
 */

public class Vibratormanager {

    private Vibrator vibrator;

    private SharedPreferences SharedPref_vibration;
    private boolean vibration;

    public Vibratormanager(Context context)
    {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        SharedPref_vibration = context.getSharedPreferences("Vibration", Context.MODE_PRIVATE);
        vibration = SharedPref_vibration.getBoolean("Vibration", true);
    }

    public boolean HasVibrator()
    {
        return (vibrator.hasVibrator() && vibration);
    }

    public void Vibrate(long ms)
    {
        if (HasVibrator())
            vibrator.vibrate(50);
    }
}
