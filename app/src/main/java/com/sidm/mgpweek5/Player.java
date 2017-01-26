package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

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
    private PLAYER_STATE attackState;
    private int hp;
    private final int maxHP = 100;

    // for movement
    private int SPEED;  // movement speed
    // jump
    private boolean isInAir = false;
    private float jumpSpeed = 0.f;
    private float gravity;

    private boolean bStartedAttack = false;
    private boolean bFinishedFrame0 = false;
    private boolean bFinishedAttackAnimation = false;
    private boolean bAttackButtonPressed = false;
    private boolean bShootArrow = false;
    private boolean bReleasedArrow = false;

    private Collider AABBCollider;
    private Collider unflippedAABBCollider;
    private Collider flippedAABBCollider;

    // Sprite animation
    public Spriteanimation[] spriteArray;
    private boolean flipSprites;

    // Constructor
    public Player() {
        position.SetZero();
        state = PLAYER_STATE.MOVE;
        attackState = PLAYER_STATE.IDLE;
        hp = maxHP;

        spriteArray = new Spriteanimation[PLAYER_STATE.STATES_TOTAL.GetValue()];
    }

    public void Init(Context context, Tilemap map, int screenWidth, int screenHeight) {
        // Load sprites
        spriteArray[PLAYER_STATE.IDLE.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_idle),
                        screenWidth / 4 * 2, screenHeight / 5, true), 0, 0, 4, 2);
        spriteArray[PLAYER_STATE.MOVE.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_moving),
                        screenWidth / 4 * 6, screenHeight / 5, true), 0, 0, 4, 6);
        spriteArray[PLAYER_STATE.JUMP.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_jump),
                        screenWidth / 4, screenHeight / 5, true), 0, 0, 4, 1);
        spriteArray[PLAYER_STATE.MELEE_1.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_melee1),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[PLAYER_STATE.MELEE_2.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_melee2),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[PLAYER_STATE.MELEE_3.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_melee3),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[PLAYER_STATE.JUMP_ATTACK.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_jumpattack),
                        screenWidth / 4 * 3, screenHeight / 5, true), 0, 0, 4, 3);
        spriteArray[PLAYER_STATE.RANGED_ATTACK.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_attackranged),
                        screenWidth / 4 * 5, screenHeight / 5, true), 0, 0, 4, 5);
        spriteArray[PLAYER_STATE.HURT.GetValue()] = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                                (context.getResources(), R.drawable.player_hurt),
                        screenWidth / 4 * 2, screenHeight / 5, true), 0, 0, 4, 2);

        int spriteWidth = spriteArray[PLAYER_STATE.IDLE.GetValue()].getSpriteWidth();
        int spriteHeight = spriteArray[PLAYER_STATE.IDLE.GetValue()].getSpriteHeight();

        //AABBcollider = new Collider(new Vector2(-spriteWidth * 0.15f, 0), new Vector2(spriteWidth * 0.05f, spriteHeight * 0.6f));
        unflippedAABBCollider = new Collider(new Vector2(-spriteWidth * 0.15f, spriteHeight * 0.5f), new Vector2(spriteWidth * 0.05f, -spriteHeight * 0.05f));
        flippedAABBCollider = new Collider(new Vector2(-spriteWidth * 0.05f, spriteHeight * 0.5f), new Vector2(spriteWidth * 0.15f, -spriteHeight * 0.05f));

        AABBCollider = unflippedAABBCollider;

        SPEED = (int)(map.tileSize_X) * 5;
        gravity = map.tileSize_Y * 4.2f;
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

    // Collider
    public Collider GetCollider() { return AABBCollider; }

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

    public void Jump(Tilemap map) {
        if (!isInAir && this.attackState == PLAYER_STATE.IDLE)
        {
            isInAir = true;
            jumpSpeed = map.tileSize_Y * -5.f;
        }
    }

    public void UpdateJump(float deltaTime, Tilemap map) {
        // Jump Sprites
        if (attackState == PLAYER_STATE.IDLE)
            SetState(PLAYER_STATE.JUMP);
        //else
        //    SetState(PLAYER_STATE.JUMP_ATTACK);

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

    public void SetState(PLAYER_STATE state) { this.state = state; }

    public PLAYER_STATE GetAttackState() { return attackState; }

    public boolean GetFlipSprite() { return flipSprites; }

    public void SetFlipSprite(boolean flip)
    {
        flipSprites = flip;
        if (flip)
            AABBCollider = flippedAABBCollider;
        else
            AABBCollider = unflippedAABBCollider;
    }

    public void FlipSpriteAnimation()
    {
        spriteArray[state.value].SetFlipSprites(flipSprites);
    }

    public void SetStartMeleeAttack() {
        this.bStartedAttack = true;
        this.bFinishedFrame0 = false;
        this.bFinishedAttackAnimation = false;
        this.bAttackButtonPressed = false;

        if (attackState == PLAYER_STATE.IDLE || attackState == PLAYER_STATE.MELEE_3)
        {
            if (this.state == PLAYER_STATE.JUMP)
                attackState = PLAYER_STATE.JUMP_ATTACK;
            else
                attackState = PLAYER_STATE.MELEE_1;
            SetState(attackState);
        }
        else if (attackState == PLAYER_STATE.JUMP_ATTACK)
        {
            if (isInAir)
                attackState = PLAYER_STATE.JUMP_ATTACK;
            else
                attackState = PLAYER_STATE.MELEE_1;
            SetState(attackState);
        }
        else
        {
            attackState = PLAYER_STATE.values()[attackState.ordinal() + 1];
            SetState(attackState);
        }

        spriteArray[attackState.GetValue()].resetAnimation();
    }

    // Attack
    public void DoMeleeAttack(Gamepanelsurfaceview gameview) {
        if (bStartedAttack)
        {
            gameview.toast.show();
            gameview.soundmanager.PlaySFXSlash1();
            gameview.bossdragon.TakeDamage(1000);
            bStartedAttack = false;
        }

        else if (bFinishedFrame0 && spriteArray[attackState.GetValue()].getCurrentFrame() == 0) {
            bFinishedAttackAnimation = true;
        }
        else if (spriteArray[attackState.GetValue()].getCurrentFrame() == spriteArray[attackState.GetValue()].getFrame() - 1)
        {
            // at last frame, did the player press attack button
            if (gameview.AttackButton.isPressed())
                bAttackButtonPressed = true;
        }
        else if (spriteArray[attackState.GetValue()].getCurrentFrame() > 0) {
            bFinishedFrame0 = true;
        }

        if (bFinishedAttackAnimation)
        {
            if (bAttackButtonPressed)
                SetStartMeleeAttack();
            else
            {
                attackState = PLAYER_STATE.IDLE;
                SetState(PLAYER_STATE.IDLE);
            }
        }
    }

    public void SetStartRangedAttack()
    {
        this.bReleasedArrow = false;
        this.bShootArrow = false;
        this.bFinishedFrame0 = false;
        this.bFinishedAttackAnimation = false;
        this.bAttackButtonPressed = false;

        attackState = PLAYER_STATE.RANGED_ATTACK;
        SetState(PLAYER_STATE.RANGED_ATTACK);
    }

    public void DoRangedAttack(float dt, Gamepanelsurfaceview gameview)
    {
        if (gameview.RangedJoyStick.GetValue().x < 0.f)
        {
            SetFlipSprite(true);
        }
        else
        {
            SetFlipSprite(false);
        }

        if (spriteArray[PLAYER_STATE.RANGED_ATTACK.GetValue()].getCurrentFrame() > 1)
        {
            if (bReleasedArrow)
                spriteArray[PLAYER_STATE.RANGED_ATTACK.GetValue()].update(dt);
        }
        else if (spriteArray[attackState.GetValue()].getCurrentFrame() > 0)
        {
            bFinishedFrame0 = true;
            spriteArray[PLAYER_STATE.RANGED_ATTACK.GetValue()].update(dt);
        }
        else if (spriteArray[attackState.GetValue()].getCurrentFrame() == 0) {
            spriteArray[PLAYER_STATE.RANGED_ATTACK.GetValue()].update(dt);
            if (bFinishedFrame0)
                bFinishedAttackAnimation = true;
        }

        if (bShootArrow && !bReleasedArrow)
        {
            Projectile temp = new Projectile();
            temp.Init(gameview.bitmapList.get("Arrow"), gameview.Screenwidth, gameview.Screenheight);
            temp.SetPosition(new Vector2(gameview.player.GetPosition()));
            temp.SetVelocity(gameview.RangedJoyStick.GetValue().GetNormalized().Multiply(1000));

            //Matrix matrix = new Matrix();
            //matrix.postRotate(angle);
            //Bitmap.createBitmap(dt, 0, 0, source.getWidth(), source.getHeight(), matrix, true);


            Gameobject.goList.add(temp);

            spriteArray[PLAYER_STATE.RANGED_ATTACK.GetValue()].setCurrentFrame(3);

            bShootArrow = false;
            bReleasedArrow = true;
        }

        if (bFinishedAttackAnimation)
        {
            if (gameview.RangedJoyStick.isPressed() || gameview.RangedJoyStick.hold) {
                SetStartRangedAttack();
                spriteArray[PLAYER_STATE.RANGED_ATTACK.GetValue()].setCurrentFrame(1);
                bFinishedFrame0 = true;
            }
            else
            {
                attackState = PLAYER_STATE.IDLE;
                SetState(PLAYER_STATE.IDLE);
            }
        }
    }

    public void SetShootArrow(boolean shootArrow) {
        if (!bReleasedArrow)
            bShootArrow = shootArrow;
    }

}
