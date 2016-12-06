package com.sidm.mgpweek5;

/**
 * Created by Foo on 6/12/2016.
 */

public class Tower {

    private Vector2 position;
    private int hp;

    // Constructor
    protected Tower() {

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

    // HP
    public void SetHP(int hp) {
        this.hp = hp;
        if (this.hp < 0)
            this.hp = 0;
    }

    public int GetHP() {
        return hp;
    }

    public boolean IsDead() {
        if (hp == 0)
            return true;

        return false;
    }

    public void TakeDamage(int damage) {
        SetHP(hp - damage);
    }
}
