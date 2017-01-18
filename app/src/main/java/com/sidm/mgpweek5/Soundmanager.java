package com.sidm.mgpweek5;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by Foo on 13/1/2017.
 */

public class Soundmanager {
    private MediaPlayer BGM;

    private SoundPool Sounds;
    private AudioAttributes audioAttributes;
    private int SFXslash1;

    // Constructor
    Soundmanager(Context context)
    {
        BGM = new MediaPlayer();
        BGM = MediaPlayer.create(context, R.raw.boss1_bgm);

        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        Sounds = new SoundPool.Builder().setAudioAttributes(audioAttributes)
                .setMaxStreams(2).build();

        SFXslash1 = Sounds.load(context, R.raw.slash1_sfx, 1);
    }

    public void SetBGMVolume(float LeftBGMVol, float RightBGMVol)
    {
        BGM.setVolume(LeftBGMVol, RightBGMVol);
    }

    public void PauseBGM()
    {
        BGM.pause();
    }

    public void PlayBGM()
    {
        BGM.start();
        BGM.setLooping(true);
    }

    public void StopBGM()
    {
        BGM.stop();
    }

    public void PlaySFXSlash1()
    {
        Sounds.play(SFXslash1, 1.f, 1.f, 0, 0, 1.5f);
    }

    public void Exit()
    {
        Sounds.unload(SFXslash1);
        Sounds.release();
    }

}
