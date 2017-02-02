package com.sidm.mgpweek5;

import java.util.Vector;

/**
 * Created by Foo on 24/1/2017.
 */

public class Gameobject {

    protected Vector2 position = new Vector2();
    public Spriteanimation[] spriteArray;   // Sprite animation
    public String type;
    public boolean toBeDestroyed;
    public boolean shielded;
    public boolean hasCollider;
    protected Collider AABBCollider = new Collider();

    // GameObject List
    public static Vector<Gameobject> goList = new Vector<Gameobject>();
    public static Vector<Missile> missileList = new Vector<Missile>();

    // constructor
    protected Gameobject() {
        position.SetZero();
        type = "object";
        hasCollider = true;
        toBeDestroyed = false;
        shielded = false;
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

    // Update
    public void Update(float dt) {

    }

    // Getters & Setters
    public Collider GetCollider() { return AABBCollider; }

}
