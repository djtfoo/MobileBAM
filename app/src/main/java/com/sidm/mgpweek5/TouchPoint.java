package com.sidm.mgpweek5;

/**
 * Created by Arun on 8/12/2016.
 */

public class TouchPoint {
    private int ID;
    private Vector2 InitialPoint;
    private Vector2 CurrentPoint;

    TouchPoint()
    {
        ID = 0;
        InitialPoint = new Vector2();
        CurrentPoint = new Vector2();
    }

    public void SetID(int ID)
    {
        this.ID = ID;
    }

    public int GetID()
    {
        return ID;
    }

    public Vector2 GetInitialPoint()
    {
        return InitialPoint;
    }

    public Vector2 GetCurrentPoint()
    {
        return CurrentPoint;
    }

    public void SetCurrentPoint(int x, int y)
    {
        CurrentPoint.Set(x, y);
    }

    public void SetInitialPoint(int x, int y)
    {
        InitialPoint.Set(x, y);
    }
}
