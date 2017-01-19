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
    private final int maxHP = 100;

    // for movement
    private final int SPEED = 600;
    // jump
    private boolean isInAir = false;
    private float jumpSpeed = 0.f;
    private final float gravity = 500.f;

    // Sprite animation
    public Spriteanimation[] spriteArray;
    private boolean flipSprites;

    // Constructor
    public Player() {
        position.SetZero();
        state = PLAYER_STATE.MOVE;
        hp = maxHP;

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
    public void Update(double dt)
    {
    }

    // Check collision
    private boolean CheckCollisionLeftRight(Tilemap map, int X, int Y)
    {
        if (map.tilemap[Y][X] == 1)
            return true;

        if (isInAir && map.tilemap[Y + 1][X] == 1)
            return true;

        return false;
    }

    private boolean CheckCollisionUpDown(Tilemap map, int X, int Y) {
        if (map.tilemap[Y][X] == 1)
            return true;

        return false;
    }

    // Movement
    public void MoveLeft(float deltaTime, Tilemap map)
    {
        float newPosX = position.x - deltaTime * SPEED;
        int X = (int)((newPosX - 0.1f * map.tileSize_X) / map.tileSize_X);
        int Y = (int)(position.y / map.tileSize_Y);

        if (!CheckCollisionLeftRight(map, X, Y)) {
            position.x = newPosX;
        }

    }

    public void MoveRight(float deltaTime, Tilemap map)
    {
        float newPosX = position.x + deltaTime * SPEED;
        int X = (int)((newPosX + 0.1f * map.tileSize_X) / map.tileSize_X);
        int Y = (int)(position.y / map.tileSize_Y);

        if (!CheckCollisionLeftRight(map, X, Y)) {
            position.x = newPosX;
        }
    }

    // Jump
    public void CheckIsInAir(Tilemap map) {
        int X;
        if (flipSprites)    // facing left
        {
            X = (int)((position.x + 0.3f * map.tileSize_X) / map.tileSize_X);
        }
        else
        {
            X = (int)((position.x - 0.4f * map.tileSize_X) / map.tileSize_X);
        }
        int Y = (int)((position.y + map.tileSize_Y) / map.tileSize_Y);

        if (!CheckCollisionUpDown(map, X, Y)) {
            isInAir = true;
        }
    }

    public boolean IsInAir() {
        return isInAir;
    }

    public void Jump() {
        if (!isInAir)
        {
            isInAir = true;
            jumpSpeed = -600.f;
        }
    }

    public void UpdateJump(float deltaTime, Tilemap map) {
        jumpSpeed += gravity * deltaTime;

        if (jumpSpeed <= 0.f) {
            UpdateJumpUpwards(deltaTime, map);
        }
        else {
            UpdateFreefall(deltaTime, map);
        }
    }

    public void UpdateJumpUpwards(float deltaTime, Tilemap map) {
        float newPosY = position.y + jumpSpeed * deltaTime;

        int X, X2;
        if (flipSprites)    // facing left
        {
            X = (int)((position.x + 0.3f * map.tileSize_X) / map.tileSize_X);
            X2 = (int)((position.x) / map.tileSize_X);
        }
        else
        {
            X = (int)((position.x - 0.4f * map.tileSize_X) / map.tileSize_X);
            X2 = (int)((position.x) / map.tileSize_X);
        }
        int Y = (int)((newPosY - map.tileSize_Y + (0.9f * map.tileSize_Y)) / map.tileSize_Y);

        if (CheckCollisionUpDown(map, X, Y) || CheckCollisionUpDown(map, X2, Y)) {
            jumpSpeed = 0.f;
        }
        else {   // move
            position.y = newPosY;
        }
    }

    public void UpdateFreefall(float deltaTime, Tilemap map) {
        float newPosY = position.y + jumpSpeed * deltaTime;

        int X;
        if (flipSprites)    // facing left
        {
            X = (int)((position.x  + 0.3f * map.tileSize_X) / map.tileSize_X);
        }
        else
        {
            X = (int)((position.x - 0.4f * map.tileSize_X) / map.tileSize_X);
        }
        int Y = (int)((newPosY + map.tileSize_Y) / map.tileSize_Y);

        if (CheckCollisionUpDown(map, X, Y)) {
            jumpSpeed = 0.f;
            isInAir = false;
            position.y = (Y - 1) * map.tileSize_Y;
        }
        else {  // move
            position.y = newPosY;
        }
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

    public boolean IsDead() {
        if (hp == 0)
            return true;

        return false;
    }

    public void TakeDamage(int damage) {
        SetHP(hp - damage);
    }


    // State & Sprites
    public PLAYER_STATE GetState() {
        return state;
    }

    public void SetState(PLAYER_STATE state) {
        this.state = state;
    }

    public boolean GetFlipSprite() { return flipSprites; }

    public void SetFlipSprite(boolean flip)
    {
        flipSprites = flip;
    }

    public void FlipSpriteAnimation()
    {
        spriteArray[state.value].SetFlipSprites(flipSprites);
    }

    // Attack
    public void DoMeleeAttack() {

    }

    public void DoRangedAttack() {

    }

}
