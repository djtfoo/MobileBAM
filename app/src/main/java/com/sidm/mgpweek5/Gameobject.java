package com.sidm.mgpweek5;

import java.util.Vector;

/**
 * Created by Foo on 24/1/2017.
 */

public class Gameobject {

    protected Vector2 position = new Vector2();
    public Spriteanimation[] spriteArray;   // Sprite animation

    // GameObject List
    public static Vector<Gameobject> goList = new Vector<Gameobject>();

    // constructor
    protected Gameobject() {
        position.SetZero();
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
}
