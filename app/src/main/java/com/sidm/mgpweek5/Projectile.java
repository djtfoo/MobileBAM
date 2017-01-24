package com.sidm.mgpweek5;

import android.graphics.Bitmap;

/**
 * Created by Foo on 6/12/2016.
 */

public class Projectile extends Gameobject {

    private Vector2 velocity = new Vector2();

    private double lifespan;
    private final double maxLifespan = 10.0;

    // Constructor
    public Projectile() {
        super();
        velocity.SetZero();
        lifespan = 0.0;
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
    public void Init() {

    }

    // Update
    @Override
    public void Update(float dt) {

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
