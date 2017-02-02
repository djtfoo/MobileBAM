package com.sidm.mgpweek5;

import android.util.Log;

/**
 * Created by Arun on 2/2/2017.
 */

public class Missile extends Projectile
{
    enum MISSILE_STATE
    {
        LAUNCH(0),
        TRACKING(1);
        public int value;
        MISSILE_STATE(int value){ this.value = value;}
        int getValue() {return value;}
    }
    Collider unflippedAABB ;
    Collider flippedAABB ;

    public MISSILE_STATE state;
    private Vector2 LockedOn = new Vector2();
    Missile()
    {
        super();
    }

    void Init()
    {
        spriteArray = new Spriteanimation[1];
        type = "missile";
        state = MISSILE_STATE.LAUNCH;
        spriteArray[0] = new Spriteanimation(Gamepanelsurfaceview.instance.bitmapList.get("Missile"), 0, 0, 1, 1);
        spriteArray[0].SetFlipSprites(spriteArray[0].GetFlipVertical());

        unflippedAABB = new Collider();
        unflippedAABB.SetMinAABB(new Vector2(Gamepanelsurfaceview.instance.map.tileSize_X * -0.2f, Gamepanelsurfaceview.instance.map.tileSize_Y * -0.8f));
        unflippedAABB.SetMaxAABB(new Vector2(Gamepanelsurfaceview.instance.map.tileSize_X * 0.2f, Gamepanelsurfaceview.instance.map.tileSize_Y * 0.5f));

        flippedAABB = new Collider();
        flippedAABB.SetMinAABB(new Vector2(Gamepanelsurfaceview.instance.map.tileSize_X * -0.2f, Gamepanelsurfaceview.instance.map.tileSize_Y * -0.5f));
        flippedAABB.SetMaxAABB(new Vector2(Gamepanelsurfaceview.instance.map.tileSize_X * 0.2f, Gamepanelsurfaceview.instance.map.tileSize_Y * 0.8f));

        damage = 15;

        AABBCollider = unflippedAABB;
        Gameobject.missileList.add(this);
    }

    @Override
    public void Update(float dt)
    {
        switch(state.getValue())
        {
            case 0:
            position.y -= dt * Gamepanelsurfaceview.instance.Screenheight * 0.5f;
            if(position.y - spriteArray[0].getSpriteHeight() / 2.f < 0)
            {
                position.y = (-spriteArray[0].getSpriteHeight() / 2.f);
                position.x = Player.instance.GetPosition().x;
                spriteArray[0].SetFlipSprites(true);
                AABBCollider = flippedAABB;
                state = MISSILE_STATE.TRACKING;
            }
                break;
            case 1:
            position.y += dt * Gamepanelsurfaceview.instance.Screenheight * 0.5f;
            if(position.y - spriteArray[0].getSpriteHeight() / 2.f > Gamepanelsurfaceview.instance.Screenheight)
            {
                spriteArray[0].SetFlipSprites(false);
                position.y = spriteArray[0].getSpriteHeight() / 2.f + Gamepanelsurfaceview.instance.Screenheight;
                AABBCollider = unflippedAABB;
                state = MISSILE_STATE.LAUNCH;
            }
                break;

        }
    }
}
