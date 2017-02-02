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
    };

    public void Init(Context context, int screenWidth, int screenHeight)
    {
        hasCollider = false;
        spriteArray[0] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.explosion),
                        (int)(screenWidth * 0.1f), (int)(screenHeight * 0.1f), true), 0, 0, 15, 4);
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
