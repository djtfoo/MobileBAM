package com.sidm.mgpweek5;

/**
 * Created by Foo on 26/1/2017.
 */

public class Collider {

    // min point is the top left corner;
    // max point is the bot right corner
    private Vector2 minAABB = new Vector2();
    private Vector2 maxAABB = new Vector2();

    public Collider() { }

    public Collider(Vector2 min, Vector2 max)
    {
        minAABB = min;
        maxAABB = max;
    }

    // Getters & Setters
    public Vector2 GetMinAABB()
    {
        return minAABB;
    }

    public Vector2 GetMaxAABB()
    {
        return maxAABB;
    }

    public void SetMinAABB(Vector2 min)
    {
        minAABB = min;
    }

    public void SetMaxAABB(Vector2 max)
    {
        maxAABB = max;
    }
}