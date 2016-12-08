package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Vibrator;

/**
 * Created by Foo on 24/11/2016.
 */

public class Gamepanelsurfaceview extends SurfaceView implements SurfaceHolder.Callback {
    // Implement this interface to receive information about changes to the surface.

    private Gamethread myThread = null; // Thread to control the rendering
    private Vibrator vibrator = (Vibrator)this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

    // Sprite animations
    private Spriteanimation flyincoins;
    private int coinX = 300, coinY = 300;

    protected boolean moveship = false;

    // 1a) Variables used for background rendering
    private Bitmap bg, scaledbg;    // bg = background; scaledbg = scaled version of bg
    // 1b) Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;

    // 1c) Variables for defining background start and end point
    private short bgX = 0, bgY = 0;

    // Used for tilemap rendering
    private Bitmap tile_ground;
    int textSize;

    // Variables for FPS
    public float FPS;
    float deltaTime;

    short m_touchX;
    short m_touchY;

    Vector2 pos = new Vector2();
    Tilemap map = new Tilemap();
    Player player = new Player();
    Bossdragon bossdragon = new Bossdragon();

    GUIbutton RightButton;
    GUIbutton LeftButton;
    GUIbutton JumpButton;
    GUIbutton AttackButton;
    boolean test;
    // Variable for Game State check
    private short GameState;

    //constructor for this GamePanelSurfaceView class
    public Gamepanelsurfaceview (Context context){

        // Context is the current state of the application/object
        super(context);

        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // 1d) Set information to get screen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;

        // Init tilemap
        map.Init(context, "MapTest.csv");
        map.tileSize_X = Screenwidth / map.GetCols();
        map.tileSize_Y = Screenheight / map.GetRows();

        // 1e)load the image when this class is being instantiated
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.boss1_scene);
        scaledbg = Bitmap.createScaledBitmap(bg, Screenwidth, Screenheight, true);

        tile_ground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tile_ground),
                (int)(map.tileSize_X), (int)(map.tileSize_Y), true);

        textSize = (int)(0.3 * map.tileSize_X * ((float)Screenwidth / (float)Screenheight));

        InitButtons();
        //Point1 = new TouchPoint();
        test = false;

        // Load the sprite sheet
        flyincoins = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                        (getResources(), R.drawable.flystar),
                        Screenwidth / 4, Screenheight / 10, true), 320, 64, 5, 5);

        // Init player and entities
        player.Init(context, Screenwidth, Screenheight);
        player.SetPosition(2 * map.tileSize_X, (map.GetRows() - 2) * map.tileSize_Y);
        bossdragon.Init(context, Screenwidth, Screenheight);
        bossdragon.SetPosition(Screenwidth / 2, Screenheight / 7 * 4);

        // Create the game loop thread
        myThread = new Gamethread(getHolder(), this);

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);
    }

    public void InitButtons()
    {
        RightButton = new GUIbutton();
        LeftButton = new GUIbutton();
        AttackButton = new GUIbutton();
        JumpButton = new GUIbutton();

        int buttonSize = (int)(0.7f * map.tileSize_Y * ((float)Screenwidth / (float)Screenheight));

        RightButton.SetButtonSize(buttonSize);
        LeftButton.SetButtonSize(buttonSize);
        JumpButton.SetButtonSize(buttonSize);
        AttackButton.SetButtonSize((int)(buttonSize * 1.45));

        LeftButton.SetButtonPos((int)(map.tileSize_X), (int)(7.8f * map.tileSize_Y));
        RightButton.SetButtonPos((int)(map.tileSize_X) + (int)(Screenwidth * 0.02) + buttonSize, (int)(7.8f * map.tileSize_Y));
        JumpButton.SetButtonPos((int)(12.5f * map.tileSize_X), (int)(7.8f * map.tileSize_Y));
        AttackButton.SetButtonPos((int)(14.f * map.tileSize_X), (int)(7.3f * map.tileSize_Y));

        LeftButton.SetBitMap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton));
        RightButton.SetBitMap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton));
        JumpButton.SetBitMap(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton));
        AttackButton.SetBitMap(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton));

        LeftButton.SetBitMapPressed(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton_pressed));
        RightButton.SetBitMapPressed(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton_pressed));
        JumpButton.SetBitMapPressed(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton_pressed));
        AttackButton.SetBitMapPressed(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton_pressed));
    }

    public void RenderButtons(Canvas canvas)
    {
        Bitmap scaledLeftButton;
        if(LeftButton.isPressed())
            scaledLeftButton = Bitmap.createScaledBitmap(LeftButton.GetBitMapPressed(), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true);
        else
            scaledLeftButton = Bitmap.createScaledBitmap(LeftButton.GetBitMap(), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true);
        canvas.drawBitmap(scaledLeftButton, (int)(LeftButton.GetPosition().x), (int)(LeftButton.GetPosition().y), null);

        Bitmap scaledRightButton;
        if(RightButton.isPressed())
            scaledRightButton = Bitmap.createScaledBitmap(RightButton.GetBitMapPressed(), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true);
        else
            scaledRightButton = Bitmap.createScaledBitmap(RightButton.GetBitMap(), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true);
        canvas.drawBitmap(scaledRightButton, (int)(RightButton.GetPosition().x), (int)(RightButton.GetPosition().y), null);

        Bitmap scaledAttackButton;
        if(AttackButton.isPressed())
            scaledAttackButton = Bitmap.createScaledBitmap(AttackButton.GetBitMapPressed(), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true);
        else
            scaledAttackButton = Bitmap.createScaledBitmap(AttackButton.GetBitMap(), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true);
        canvas.drawBitmap(scaledAttackButton, (int)(AttackButton.GetPosition().x), (int)(AttackButton.GetPosition().y), null);

        Bitmap scaledJumpButton;
        if(JumpButton.isPressed())
            scaledJumpButton = Bitmap.createScaledBitmap(JumpButton.GetBitMapPressed(), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true);
        else
            scaledJumpButton = Bitmap.createScaledBitmap(JumpButton.GetBitMap(), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true);
        canvas.drawBitmap(scaledJumpButton, (int)(JumpButton.GetPosition().x), (int)(JumpButton.GetPosition().y), null);
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder){
        // Create the thread
        if (!myThread.isAlive()){
            myThread = new Gamethread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        // Destroy the thread
        if (myThread.isAlive()){
            myThread.startRun(false);
        }
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    public void drawTilemap(Canvas canvas) {
        for (int y = 0; y < map.GetRows(); ++y) {
            for (int x = 0; x < map.GetCols(); ++x) {
                if (map.tilemap[y][x] == 1) {
                    canvas.drawBitmap(tile_ground, x * map.tileSize_X, y * map.tileSize_Y, null);
                }
            }
        }
    }

    public void RenderGameplay(Canvas canvas) {
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }

        canvas.drawBitmap(scaledbg, bgX, bgY, null);    // 1st background image
        canvas.drawBitmap(scaledbg, bgX + Screenwidth, bgY, null);    // 2nd background image


        // draw the stars
        flyincoins.draw(canvas);

        flyincoins.setX(coinX);
        flyincoins.setY(coinY);

        // draw the boss enemy
        bossdragon.spriteArray[bossdragon.GetState().GetValue()].setX((int)bossdragon.GetPosition().x);
        bossdragon.spriteArray[bossdragon.GetState().GetValue()].setY((int)bossdragon.GetPosition().y);
        bossdragon.spriteArray[bossdragon.GetState().GetValue()].draw(canvas);

        // draw the tilemap
        drawTilemap(canvas);

        // draw the player
        player.spriteArray[player.GetState().GetValue()].setX((int)player.GetPosition().x);
        player.spriteArray[player.GetState().GetValue()].setY((int)player.GetPosition().y);
        player.spriteArray[player.GetState().GetValue()].draw(canvas);

        RenderButtons(canvas);

        // Debug text
        RenderTextOnScreen(canvas, "FPS " + FPS, (int)(1.5f * map.tileSize_X), (int)(0.8f * map.tileSize_Y), textSize);
        //RenderTextOnScreen(canvas, "TP1 Initial: " + Point1.GetInitialPoint().ToString(), 130, 175, 50);
        //RenderTextOnScreen(canvas, "TP1 Current: " + Point1.GetCurrentPoint().ToString(), 130, 225, 50);
        if(test)
        RenderTextOnScreen(canvas, "TEST", 130, 375, textSize);
        if(RightButton.isPressed()) {
            RenderTextOnScreen(canvas, "Right Button Pressed", 130, 275, textSize);
        }
        if(LeftButton.isPressed()) {
            RenderTextOnScreen(canvas, "Left Button Pressed", 130, 325, textSize);
        }
        if(AttackButton.isPressed()) {
            RenderTextOnScreen(canvas, "Attack Button Pressed", 130, 375, textSize);
        }
        if(JumpButton.isPressed()) {
            RenderTextOnScreen(canvas, "Jump Button Pressed", 130, 425, textSize);
        }
        if (moveship){
            RenderTextOnScreen(canvas, "TRIGGERED", 130, 125, 50);
        }
    }


    //Update method to update the game play
    public void update(float dt, float fps){
        FPS = fps;
        this.deltaTime = dt;

        long dt_l = System.currentTimeMillis();

        switch (GameState) {
            case 0: {
                // 3) Update the background to allow panning effect
                bgX -= 200 * deltaTime;      // temp value to speed the panning
                if (bgX < -Screenwidth) {
                    bgX = 0;
                }

                // make sprite animate
                flyincoins.update(dt_l);

                player.spriteArray[player.GetState().GetValue()].update(dt_l);
                bossdragon.spriteArray[bossdragon.GetState().GetValue()].update(dt_l);


                if(RightButton.isPressed())
                {
                    player.MoveRight(deltaTime);
                }
                else if(LeftButton.isPressed())
                {
                    player.MoveLeft(deltaTime);
                }
            }
            break;
        }
    }

    // Rendering is done on Canvas
    public void doDraw(Canvas canvas){
        switch (GameState)
        {
            case 0:
                RenderGameplay(canvas);
                break;
        }
    }

    public void RenderTextOnScreen(Canvas canvas, String text, int posX, int posY, int textsize) {

        Paint paint = new Paint();

        paint.setARGB(255, 255, 255, 255);
        paint.setStrokeWidth(100);
        paint.setTextSize(textsize);

        canvas.drawText(text, posX, posY, paint);
    }

    // Collision Check - tbc
    boolean CheckCollision(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2)
    {
        if (x2 >= x1 && x2 <= x1 + w1) {
            if (y2 >= y1 && y2 <= y1 + h1)
                return true;
        }

        if (x2 + w2 >= x1 && x2 + w2 <= x1 + w1) {
            if (y2 >= y1 && y2 <= y1 + h1)
                return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
        // 5) In event of touch on screen, the spaceship will relocate to the point of touch
        short m_touchX = (short) event.getX();
        short m_touchY = (short) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mX = (short) (m_touchX - shipArr[shipArrIdx].getWidth() / 2);
            mY = (short) (m_touchY - shipArr[shipArrIdx].getHeight() / 2);
        }
        return super.onTouchEvent(event);
        */

        //int touch_1 = event.getPointerId(0);
        //int touch_2 = event.getPointerId(1);

        int action = event.getAction(); // Check for the action of touch

        switch(action) {

            case MotionEvent.ACTION_DOWN:
                //Log.v("DEBUG:","ACTION_DOWN");
                Vector2 m_touch = new Vector2(event.getX(), event.getY());
                Vector2 tempLeft = new Vector2(LeftButton.GetPosition());
                tempLeft.x += LeftButton.GetButtonSize() / 2;
                tempLeft.y += LeftButton.GetButtonSize() / 2;
                if(tempLeft.Subtract(m_touch).GetLength() < LeftButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    LeftButton.SetPressed(true);
                }
                Vector2 tempRight = new Vector2(RightButton.GetPosition());
                tempRight.x += RightButton.GetButtonSize() / 2;
                tempRight.y += RightButton.GetButtonSize() / 2;
                if(tempRight.Subtract(m_touch).GetLength() < RightButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    RightButton.SetPressed(true);
                }
                Vector2 tempAttack = new Vector2(AttackButton.GetPosition());
                tempAttack.x += AttackButton.GetButtonSize() / 2;
                tempAttack.y += AttackButton.GetButtonSize() / 2;
                if(tempAttack.Subtract(m_touch).GetLength() < AttackButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    AttackButton.SetPressed(true);
                }
                Vector2 tempJump = new Vector2(JumpButton.GetPosition());
                tempJump.x += JumpButton.GetButtonSize() / 2;
                tempJump.y += JumpButton.GetButtonSize() / 2;
                if(tempJump.Subtract(m_touch).GetLength() < JumpButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    JumpButton.SetPressed(true);
                }

            case MotionEvent.ACTION_POINTER_DOWN:
                //Log.v("DEBUG:","ACTION_POINTER_DOWN");
                boolean leftbutton = false;
                boolean rightbutton = false;
                boolean jumpbutton = false;
                boolean attackbutton = false;

                for(int idx = 0; idx < event.getPointerCount(); idx++)
                {
                    int pointerID = event.findPointerIndex(idx);
                    if(pointerID < 0)
                        continue;
                    Vector2 touch = new Vector2(event.getX(pointerID), event.getY(pointerID));
                    tempJump = new Vector2(JumpButton.GetPosition());
                    tempJump.x += JumpButton.GetButtonSize() / 2;
                    tempJump.y += JumpButton.GetButtonSize() / 2;
                    if (tempJump.Subtract(touch).GetLength() < JumpButton.GetButtonSize() / 2) {
                        if (jumpbutton == false) {
                            jumpbutton = true;
                            continue;
                        }
                    }

                    tempAttack = new Vector2(AttackButton.GetPosition());
                    tempAttack.x += AttackButton.GetButtonSize() / 2;
                    tempAttack.y += AttackButton.GetButtonSize() / 2;
                    if (tempAttack.Subtract(touch).GetLength() < AttackButton.GetButtonSize() / 2) {
                        if (attackbutton == false) {
                            attackbutton = true;
                            continue;
                            }
                    }

                    tempRight = new Vector2(RightButton.GetPosition());
                    tempRight.x += RightButton.GetButtonSize() / 2;
                    tempRight.y += RightButton.GetButtonSize() / 2;
                    if(tempRight.Subtract(touch).GetLength() < RightButton.GetButtonSize() / 2)
                    {
                        if(rightbutton == false)
                        {
                            rightbutton = true;
                            continue;
                        }
                    }

                    tempLeft = new Vector2(LeftButton.GetPosition());
                    tempLeft.x += LeftButton.GetButtonSize() / 2;
                    tempLeft.y += LeftButton.GetButtonSize() / 2;
                    if (tempLeft.Subtract(touch).GetLength() < LeftButton.GetButtonSize() / 2) {
                        if (leftbutton == false) {
                            leftbutton = true;
                            continue;
                        }
                    }

                }
                if(leftbutton && !LeftButton.isPressed())
                {
                    LeftButton.SetPressed(true);
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    //Log.v("APD", "Left Button Pressed");
                }else if(!leftbutton)
                {
                    LeftButton.SetPressed(false);
                }
                if(rightbutton && !RightButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    RightButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!rightbutton)
                {
                    RightButton.SetPressed(false);
                }
                if(attackbutton && !AttackButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    AttackButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!attackbutton)
                {
                    AttackButton.SetPressed(false);
                }
                if(jumpbutton && !JumpButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    JumpButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!jumpbutton)
                {
                    JumpButton.SetPressed(false);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //Log.v("DEBUG:","ACTION_MOVE");
                leftbutton = false;
                rightbutton = false;
                jumpbutton = false;
                attackbutton = false;

                for(int idx = 0; idx < event.getPointerCount(); idx++)
                {
                    int pointerID = event.findPointerIndex(idx);
                    if(pointerID < 0)
                        continue;
                    Vector2 touch = new Vector2(event.getX(pointerID), event.getY(pointerID));
                    tempJump = new Vector2(JumpButton.GetPosition());
                    tempJump.x += JumpButton.GetButtonSize() / 2;
                    tempJump.y += JumpButton.GetButtonSize() / 2;
                    if (tempJump.Subtract(touch).GetLength() < JumpButton.GetButtonSize() / 2) {
                        if (jumpbutton == false) {
                            jumpbutton = true;
                            continue;
                        }
                    }

                    tempAttack = new Vector2(AttackButton.GetPosition());
                    tempAttack.x += AttackButton.GetButtonSize() / 2;
                    tempAttack.y += AttackButton.GetButtonSize() / 2;
                    if (tempAttack.Subtract(touch).GetLength() < AttackButton.GetButtonSize() / 2) {
                        if (attackbutton == false) {
                            attackbutton = true;
                            continue;
                        }
                    }

                    tempRight = new Vector2(RightButton.GetPosition());
                    tempRight.x += RightButton.GetButtonSize() / 2;
                    tempRight.y += RightButton.GetButtonSize() / 2;
                    if(tempRight.Subtract(touch).GetLength() < RightButton.GetButtonSize() / 2)
                    {
                        if(rightbutton == false)
                        {
                            rightbutton = true;
                            continue;
                        }
                    }

                    tempLeft = new Vector2(LeftButton.GetPosition());
                    tempLeft.x += LeftButton.GetButtonSize() / 2;
                    tempLeft.y += LeftButton.GetButtonSize() / 2;
                    if (tempLeft.Subtract(touch).GetLength() < LeftButton.GetButtonSize() / 2) {
                        if (leftbutton == false) {
                            leftbutton = true;
                            continue;
                        }
                    }

                }
                if(leftbutton && !LeftButton.isPressed())
                {
                    LeftButton.SetPressed(true);
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    //Log.v("APD", "Left Button Pressed");
                }else if(!leftbutton)
                {
                    LeftButton.SetPressed(false);
                }
                if(rightbutton && !RightButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    RightButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!rightbutton)
                {
                    RightButton.SetPressed(false);
                }
                if(attackbutton && !AttackButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    AttackButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!attackbutton)
                {
                    AttackButton.SetPressed(false);
                }
                if(jumpbutton && !JumpButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(100);
                    }
                    JumpButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!jumpbutton)
                {
                    JumpButton.SetPressed(false);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //test = false;
                break;
            case MotionEvent.ACTION_UP:
                //Log.v("DEBUG:","ACTION_UP");
                test = false;
                moveship = false;
                RightButton.SetPressed(false);
                LeftButton.SetPressed(false);
                AttackButton.SetPressed(false);
                JumpButton.SetPressed(false);
                break;
        }

        return true;
    }
}
