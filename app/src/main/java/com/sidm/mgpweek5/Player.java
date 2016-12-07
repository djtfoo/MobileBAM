package com.sidm.mgpweek5;

/**
 * Created by Foo on 3/12/2016.
 */

public class Player {

    public enum PLAYER_STATE {

        IDLE(0),
        MOVE(1),
        JUMP(2),
        MELEE_1(3),
        MELEE_2(4),
        MELEE_3(5),
        MELEE_JUMP(6),
        RANGED_ATTACK(7),
        HURT(8);

        private int value;

        PLAYER_STATE(int value) { this.value = value; }
        public int GetValue() { return value; }
    }

    private Vector2 position;
    private PLAYER_STATE state;
    private int hp;

    // put the sprites here

    // Constructor
    public Player() {
        position.SetZero();
        state = PLAYER_STATE.IDLE;
        hp = 100;
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
    public void Update(double dt) {

    }

    // Movement
    public void Move() {

    }

    public void Jump() {

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


    // State
    public PLAYER_STATE GetState() {
        return state;
    }

    // Attack
    public void DoMeleeAttack() {

    }

    public void DoRangedAttack() {

    }

}
