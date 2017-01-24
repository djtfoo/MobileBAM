package com.sidm.mgpweek5;

/**
 * Created by Foo on 3/12/2016.
 */

public class Entity extends Gameobject {

    protected int hp;
    protected final int maxHP;

    // Constructor
    protected Entity(int hp) {
        super();
        maxHP = hp;
        this.hp = maxHP;
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

    public int GetMaxHP() { return maxHP; }

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

}
