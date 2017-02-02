package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Arun on 1/2/2017.
 */

public class Particle extends Gameobject
{

    Particle()
    {
        super();
        spriteArray = new Spriteanimation[1];
        type = "particle";
    };

    public void Init()
    {
        hasCollider = false;
        spriteArray[0] = new Spriteanimation(Gamepanelsurfaceview.instance.bitmapList.get("explosion"), 0, 0, 8, 4);
    }

    public void Update(float dt)
    {
        spriteArray[0].update(dt);
        if(spriteArray[0].getCurrentFrame() == 3)
        {
            toBeDestroyed = true;
        }
    }

}
