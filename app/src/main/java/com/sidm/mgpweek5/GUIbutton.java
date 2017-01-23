package com.sidm.mgpweek5;

import android.graphics.Bitmap;

/**
 * Created by Arun on 8/12/2016.
 */

public class GUIbutton {
    private int Size;
    private Vector2 Pos;
    private boolean Pressed;
    private Bitmap bm;
    private Bitmap bm_pressed;
    public int PointerIndex;
    private String name;

    GUIbutton(String name)
    {
        Size = 1;
        Pos = new Vector2();
        PointerIndex = -1;
        Pressed = false;
        SetName(name);
    }

    GUIbutton(int Size, Vector2 Pos)
    {
        this.Size = Size;
        this.Pos = Pos;
    }

    public void SetBitMap(Bitmap bm)
    {
        this.bm = bm;
    }

    public void SetBitMapPressed(Bitmap bm)
    {
        this.bm_pressed = bm;
    }

    public Bitmap GetBitMap()
    {
        return bm;
    }

    public Bitmap GetBitMapPressed()
    {
        return bm_pressed;
    }

    public boolean isPressed()
    {
        return Pressed;
    }

    public void SetButtonPos(int x, int y)
    {
        this.Pos.x = x;
        this.Pos.y = y;
    }

    public void SetButtonSize(int size)
    {
        this.Size = size;
    }

    public int GetButtonSize()
    {
        return Size;
    }

    public Vector2 GetPosition()
    {
        return Pos;
    }

    public void SetPressed(boolean status)
    {
        this.Pressed = status;
    }

    public void SetName(String name) { this.name = name; }

    public String GetName() { return name; }
}
