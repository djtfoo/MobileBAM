package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by Foo on 8/12/2016.
 */

public class Bossdragon extends Entity {

    public enum BOSSDRAGON_STATE {

        IDLE(0),
        //DAMAGED(1);

        STATES_TOTAL(1);

        private int value;

        BOSSDRAGON_STATE(int value) { this.value = value; }
        public int GetValue() { return value; }
    }

    public Collider[] HitBoxes;

    private BOSSDRAGON_STATE state;

    // Constructor
    public Bossdragon() {
        super(500);
        state = BOSSDRAGON_STATE.IDLE;
        type = "boss";
        spriteArray = new Spriteanimation[BOSSDRAGON_STATE.STATES_TOTAL.GetValue()];
    }

    public void Init(Context context, int screenWidth, int screenHeight)
    {
        spriteArray[Bossdragon.BOSSDRAGON_STATE.IDLE.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.dragon_idle),
                        (int)(screenWidth / 1.2f) * 4, (int)(screenHeight / 1.2f), true), 0, 0, 4, 4);

        int spriteWidth = spriteArray[Bossdragon.BOSSDRAGON_STATE.IDLE.GetValue()].getSpriteWidth();
        int spriteHeight = spriteArray[Bossdragon.BOSSDRAGON_STATE.IDLE.GetValue()].getSpriteHeight();

        HitBoxes = new Collider[1];

        HitBoxes[0] = new Collider();
        HitBoxes[0].SetMaxAABB(new Vector2(spriteWidth * 0.083f - (spriteWidth * 0.362f), spriteHeight * 0.135f +spriteHeight*0.056f));
        HitBoxes[0].SetMinAABB(new Vector2(-(spriteWidth * 0.083f) - (spriteWidth * 0.362f), -(spriteHeight * 0.135f) +spriteHeight*0.056f));

        //spriteArray[Bossdragon.BOSSDRAGON_STATE.IDLE.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
        //        (BitmapFactory.decodeResource
        //                        (context.getResources(), R.drawable.dragon_idle),
        //                2, 2, true), 0, 0, 4, 4);
    }

    BOSSDRAGON_STATE GetState() {
        return state;
    }

    @Override
    public void Update(float dt)
    {
        spriteArray[state.GetValue()].update(dt);
    }
}
