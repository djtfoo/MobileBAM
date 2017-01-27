package com.sidm.mgpweek5;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Arun on 26/1/2017.
 */

public class JoyStick extends GUIbutton
{
    public Vector2 touchPos;
    protected Bitmap bmFG;

    protected JoyStick(String name)
    {
        super(name);
        touchPos = new Vector2(0, 0);
    }

    void SetTouchPos(Vector2 CurrentTouchPos)
    {
        //touchPos = new Vector2(CurrentTouchPos.x - 172, CurrentTouchPos.y - 172);

        touchPos = new Vector2(CurrentTouchPos.x - Size / 2.f + 8, CurrentTouchPos.y - Size / 2.f + 8);
    }

    Vector2 GetTouchPos()
    {
        Vector2 dir = touchPos.Subtract(Pos).GetNormalized();
        Vector2 Maximum = touchPos.Subtract(Pos).GetNormalized().Multiply(Size / 2.f);
       Vector2 result;
        if(touchPos.Subtract(Pos).GetLength() > Size / 2.f)
        {
            result = Maximum;
            result.AddToThis(Pos);
        }else
        {
            result = touchPos;
        }
        return result;
    }

    Vector2 GetValue()
    {
        Vector2 result = new Vector2(0,0);
        Vector2 Maximum = touchPos.Subtract(Pos).GetNormalized().Multiply(Size / 2.f);
        //float x = (touchPos.x - Pos.x) / -180.f;
        //float y = (touchPos.y - Pos.y) / -180.f;

        float x = (touchPos.x - Pos.x) / -Size / 2.f;
        float y = (touchPos.y - Pos.y) / -Size / 2.f;

        if(x > 1)
            x = 1;
        else if(x < -1)
            x = -1;
        if(y > 1)
            y = 1;
        else if(y < -1)
            y = -1;
        result.Set(x, y);
        return result;
    }

    public void Update()
    {

    }
}
