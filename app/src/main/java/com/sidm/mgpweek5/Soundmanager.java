package com.sidm.mgpweek5;

import android.content.Context;
import android.content.SharedPreferences;
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
    private int SFXportal;
    private int SFXJump;
    private int SFXArrowShot;

    SharedPreferences SharedPref_musicVol;
    int musicVol;

    SharedPreferences SharedPref_sfxVol;
    int sfxVol;

    // Constructor
    public Soundmanager(Context context)
    {
        BGM = new MediaPlayer();
        BGM = MediaPlayer.create(context, R.raw.boss1_bgm);

        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        Sounds = new SoundPool.Builder().setAudioAttributes(audioAttributes)
                .setMaxStreams(2).build();

        SFXslash1 = Sounds.load(context, R.raw.slash1_sfx, 1);
        SFXportal = Sounds.load(context, R.raw.portal_sfx, 1);
        SFXJump = Sounds.load(context, R.raw.jump_sfx, 1);
        SFXArrowShot = Sounds.load(context, R.raw.arrowshot_sfx, 1);

        SharedPref_musicVol = context.getSharedPreferences("MusicVolume", Context.MODE_PRIVATE);
        musicVol = SharedPref_musicVol.getInt("MusicVolume", 100);

        SharedPref_sfxVol = context.getSharedPreferences("SFXVolume", Context.MODE_PRIVATE);
        sfxVol = SharedPref_sfxVol.getInt("SFXVolume", 100);
    }

    public void PauseBGM()
    {
        BGM.pause();
    }

    public void PlayBGM()
    {
        float volume = musicVol / 100.f;
        BGM.setVolume(volume, volume);
        BGM.start();
        BGM.setLooping(true);
    }

    public void StopBGM()
    {
        BGM.stop();
    }

    public void PlaySFXSlash1()
    {
        float volume = sfxVol / 100.f;
        Sounds.play(SFXslash1, volume, volume, 0, 0, 1.5f);
    }

    public void PlaySFXPortal()
    {
        float volume = sfxVol / 100.f;
        Sounds.play(SFXportal, volume, volume, 0, 0, 1.5f);
    }

    public void PlaySFXJump()
    {
        float volume = sfxVol / 100.f;
        Sounds.play(SFXJump, volume, volume, 0, 0, 1.5f);
    }

    public void PlaySFXArrowShot()
    {
        float volume = sfxVol / 100.f;
        Sounds.play(SFXArrowShot, volume, volume, 0, 0, 1.5f);
    }

    public void Exit()
    {
        Sounds.unload(SFXslash1);
        Sounds.release();
    }

}
