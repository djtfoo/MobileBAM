package com.sidm.mgpweek5;

/**
 * Created by Foo on 3/12/2016.
 */

public class Entity {

    public enum ENTITY_STATE {

        IDLE(0),
        DAMAGED(1);

        private int value;

        ENTITY_STATE(int value) { this.value = value; }
        public int GetValue() { return value; }

    }

    private Vector2 position;
    private ENTITY_STATE state;
    private int hp;

    // Constructor
    Entity() {
        position.SetZero();
        state = ENTITY_STATE.IDLE;
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
    }

    public boolean IsDead() {
        if (hp <= 0)
            return true;

        return false;
    }

    // state
    public ENTITY_STATE GetState() {
        return state;
    }

}
