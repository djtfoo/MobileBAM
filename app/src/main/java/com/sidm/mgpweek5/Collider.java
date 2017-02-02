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

    public static boolean CheckPointToAABB(Vector2 point, Gameobject go)
    {
        if(point.x < go.GetCollider().GetMaxAABB().x + go.position.x && point.x > go.GetCollider().GetMinAABB().x + go.position.x
                && point.y < go.GetCollider().GetMaxAABB().y + go.position.y && point.y > go.GetCollider().GetMinAABB().y + go.position.y)
        {
            return true;
        }

        return false;
    }

    public static boolean CheckAABBtoAABB(Gameobject go1, Gameobject go2)
    {
        if(go1.GetCollider().GetMinAABB().x + go1.position.x < go2.GetCollider().GetMaxAABB().x + go2.position.x &&
                go1.GetCollider().GetMaxAABB().x + go1.position.x > go2.GetCollider().GetMinAABB().x + go2.position.x &&
                go1.GetCollider().GetMinAABB().y + go1.position.y < go2.GetCollider().GetMaxAABB().y + go2.position.y &&
                go1.GetCollider().GetMaxAABB().y + go1.position.y > go2.GetCollider().GetMinAABB().y + go2.position.y)
        {
            return true;
        }

        return false;
    }

}
