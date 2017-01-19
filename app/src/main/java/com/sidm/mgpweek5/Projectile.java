package com.sidm.mgpweek5;

import android.graphics.Bitmap;

/**
 * Created by Foo on 6/12/2016.
 */

public class Projectile {

    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();

    private Bitmap image;

    private double lifespan;

    // Constructor
    public Projectile() {
        position.SetZero();
        velocity.SetZero();
        lifespan = 0.0;
    }

    // Position
    public void SetPosition(float x, float y) {
        position.Set(x, y);
    }

    public void SetPosition(Vector2 pos) {
        position = pos;
    }

    public Vector2 GetPosition() {
        return position;
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

    // Update
    public void Update() {

    }

    // Lifespan
    public double GetLifespan() {
        return lifespan;
    }

    public void IncreaseLifespan(double dt) {
        lifespan += dt;
    }

    public boolean HasExpired() {
        if (lifespan > 10.0)
            return true;

        return false;
    }
}
