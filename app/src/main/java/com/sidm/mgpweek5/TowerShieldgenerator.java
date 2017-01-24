package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Foo on 24/1/2017.
 */

public class TowerShieldgenerator extends Tower {

    public TowerShieldgenerator(int hp) {
        super(hp);
    }

    public void Init(Context context, int screenWidth, int screenHeight)
    {
        spriteArray[0] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.tower_shieldgenerator),
                        screenWidth / 4, screenHeight / 5, true), 0, 0, 4, 1);
    }
}
