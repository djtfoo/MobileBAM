package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        JUMP_ATTACK(6),
        RANGED_ATTACK(7),
        HURT(8),

        STATES_TOTAL(9);

        private int value;

        PLAYER_STATE(int value) { this.value = value; }
        public int GetValue() { return value; }
    }

    private Vector2 position = new Vector2();
    private PLAYER_STATE state;
    private int hp;
    private final int SPEED = 600;

    // Sprite animation
    public Spriteanimation[] spriteArray;

    // Constructor
    public Player() {
        position.SetZero();
        state = PLAYER_STATE.MOVE;
        hp = 100;

        spriteArray = new Spriteanimation[PLAYER_STATE.STATES_TOTAL.GetValue()];
    }

    public void Init(Context context, int screenWidth, int screenHeight) {
        // Load sprites
        spriteArray[Player.PLAYER_STATE.IDLE.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_idle),
                        screenWidth / 4 * 2, screenHeight / 5, true), 0, 0, 4, 2);
        spriteArray[Player.PLAYER_STATE.MOVE.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_moving),
                        screenWidth / 4 * 6, screenHeight / 5, true), 0, 0, 4, 6);
        spriteArray[Player.PLAYER_STATE.JUMP.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_jump),
                        screenWidth / 4, screenHeight / 5, true), 0, 0, 4, 1);
        spriteArray[Player.PLAYER_STATE.MELEE_1.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_melee1),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[Player.PLAYER_STATE.MELEE_2.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_melee2),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[Player.PLAYER_STATE.MELEE_3.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_melee3),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[Player.PLAYER_STATE.JUMP_ATTACK.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_jumpattack),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[Player.PLAYER_STATE.RANGED_ATTACK.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_attackranged),
                        screenWidth / 4 * 5, screenHeight / 5, true), 0, 0, 4, 5);
        spriteArray[Player.PLAYER_STATE.HURT.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_hurt),
                        screenWidth / 4 * 2, screenHeight / 5, true), 0, 0, 4, 2);
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

    public void MoveLeft(float deltaTime)
    {
        position.x -= deltaTime * SPEED;
    }

    public void MoveRight(float deltaTime)
    {
        position.x += deltaTime * SPEED;
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
