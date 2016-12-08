package com.sidm.mgpweek5;

/**
 * Created by Foo on 3/12/2016.
 */

public class Entity {

    protected Vector2 position = new Vector2();
    protected int hp;
    // Sprite animation
    public Spriteanimation[] spriteArray;

    // Constructor
    protected Entity(int hp) {
        position.SetZero();
        hp = 1000;
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

    public void TakeDamage(int damage) {
        SetHP(hp - damage);
        if (this.hp < 0)
            this.hp = 0;
    }

    public boolean IsDead() {
        if (hp <= 0)
            return true;

        return false;
    }

    // Update
    public void Update() {

    }

}
