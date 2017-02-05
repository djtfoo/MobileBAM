package com.sidm.mgpweek5;

import android.graphics.Bitmap;

/**
 * Created by Foo on 6/12/2016.
 */

public class Projectile extends Gameobject {

    private Vector2 velocity = new Vector2();

    private double lifespan;
    private final double maxLifespan = 10.0;
    public int damage;

    // Constructor
    public Projectile() {
        super();
        velocity.SetZero();
        lifespan = 0.0;
        type = "projectile";
        spriteArray = new Spriteanimation[1];
    }

    // Velocity
    public void SetVelocity(float x, float y) {
        velocity.Set(x, y);
    }

    public void SetVelocity(Vector2 vel) {
        velocity = vel;
    }

    public Vector2 GetVelocity() {
        return velocity;
    }

    // Init
    public void Init(Bitmap sprite, int screenWidth, int screenHeight) {
        spriteArray[0] = new Spriteanimation(sprite, 0, 0, 4, 1);
    }

    public boolean CollidedWithTileMap()
    {
        int X = (int)((position.x) / Gamepanelsurfaceview.instance.map.tileSize_X);
        int Y = (int)((position.y) / Gamepanelsurfaceview.instance.map.tileSize_Y);
        if(Gamepanelsurfaceview.instance.map.tilemap[Y][X] == 1) {
            return true;
        }


        return false;

    }

    // Update
    @Override
    public void Update(float dt)
    {
        position.AddToThis(velocity.Multiply(dt));
        IncreaseLifespan(dt);
        if (HasExpired())
            toBeDestroyed = true;
    }

    // Lifespan
    public double GetLifespan() {
        return lifespan;
    }

    public void IncreaseLifespan(double dt) {
        lifespan += dt;
    }

    public boolean HasExpired() {
        if (maxLifespan > 10.0)
            return true;

        return false;
    }
}
