package com.sidm.mgpweek5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Foo on 24/11/2016.
 */

public class Gamepanelsurfaceview extends SurfaceView implements SurfaceHolder.Callback {
    // Implement this interface to receive information about changes to the surface.

    private Gamethread myThread = null; // Thread to control the rendering
    private Vibrator vibrator = (Vibrator)this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

    // 1a) Variables used for background rendering
    private Bitmap bg, scaledbg;    // bg = background; scaledbg = scaled version of bg
    // 1b) Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;

    private Bitmap playerProfile, bossProfile;

    // 1c) Variables for defining background start and end point
    private short bgX = 0, bgY = 0;

    // Used for tilemap rendering
    private Bitmap tile_ground;
    int textSize;

    // Fonts
    Typeface font;

    // init for start png
    private Bitmap star;
    int numstar = 3;

    // Activity
    Activity activityTracker;   // use to track and then launch to the desired activity

    // Variables for FPS
    public float FPS;
    float deltaTime;

    Vector2 pos = new Vector2();
    Tilemap map = new Tilemap();
    Player player = new Player();
    Bossdragon bossdragon = new Bossdragon();

    GUIbutton RightButton;
    GUIbutton LeftButton;
    GUIbutton JumpButton;
    GUIbutton AttackButton;
    GUIbutton PauseButton;

    GUIbutton primaryButton;

    // Pause
    private boolean isPaused = false;
    private Object PauseB1;
    private Object PauseB2;
    Bitmap pauseBitmap;

    // Variable for Game State check
    private short GameState;

    // Week 13 Toast
    CharSequence text;
    int toastTime;
    Toast toast;

    // Week 13 Alert Dialog
    public boolean showAlert = true;

    AlertDialog.Builder alert = null;
    private Alert AlertObj;

    // Week 13 Shared Preferences
    SharedPreferences SharedPrefname;
    SharedPreferences.Editor editName;
    String Playername;

    SharedPreferences SharedPrefscore;
    SharedPreferences.Editor editScore;
    int highScore;

    //constructor for this GamePanelSurfaceView class
    public Gamepanelsurfaceview (Context context, Activity activity){

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

        playerProfile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_profile),
                (int)(map.tileSize_X), (int)(map.tileSize_X), true);
        bossProfile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dragon_profile),
                (int)(map.tileSize_X), (int)(map.tileSize_X), true);

        textSize = (int)(0.3 * map.tileSize_X * ((float)Screenwidth / (float)Screenheight));

        InitButtons();
        //Point1 = new TouchPoint();

        // Init player and entities
        player.Init(context, Screenwidth, Screenheight);
        player.SetPosition(2 * map.tileSize_X, (map.GetRows() - 2) * map.tileSize_Y);
        bossdragon.Init(context, Screenwidth, Screenheight);
        bossdragon.SetPosition(Screenwidth / 2, Screenheight / 7 * 4);

        // Create the game loop thread
        myThread = new Gamethread(getHolder(), this);

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);

        // Font
        font = Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf");

        // Activity stuff
        activityTracker = activity;
        //// HOW TO CHANGE SCENES
        //Intent intent = new Intent();
        //intent.setClass(getContext(), Mainmenu.class);
        //activityTracker.startActivity(intent);

        // Week 13 Toast
        Toastmessage(context);

        // Week 13 Shared Preferences
        SharedPrefname = getContext().getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        editName = SharedPrefname.edit();
        Playername = "Player1";
        Playername = SharedPrefname.getString("PlayerUSERID", "DEFAULT");

        SharedPrefscore = getContext().getSharedPreferences("PlayerUSERSCORE", Context.MODE_PRIVATE);
        editScore = SharedPrefscore.edit();
        highScore = 0;
        highScore = SharedPrefscore.getInt("PlayerUSERSCORE", 0);

        // Week 13 Alert Dialog
        AlertObj = new Alert(this);
        alert = new AlertDialog.Builder(getContext());

        // Allow players to input their name
        final EditText input = new EditText(getContext());

        // Define the input method where 'enter' key is disabled
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Define max of 20 characters to be entered for 'Name' field
        int maxLength = 20;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        input.setFilters(FilterArray);

        // Setup the alert dialog
        alert.setMessage("Game over! So how?");
        alert.setCancelable(false);
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                Playername = input.getText().toString();
                editScore.putString("PlayerUSERID", Playername);
                editName.commit();

                Intent intent = new Intent();
                intent.setClass(getContext(), Mainmenu.class);
                activityTracker.startActivity(intent);
            }
        });

        // Week 9 Pause
        PauseB1 = new Object(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.pause)),
                (int)(Screenwidth/15), (int)(Screenheight/10), true), Screenwidth / 2, 20);
        PauseB2 = new Object(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.pause1)),
                (int)(Screenwidth/15), (int)(Screenheight/10), true), Screenwidth / 2, 20);
    }

    // Week 13 Toast
    public void Toastmessage(Context context)
    {
        text = "Attack!";
        toastTime = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, toastTime);
    }

    // Week 9 Pause
    public void PauseButtonPressed()
    {
        if (isPaused)   // game is currently paused
        {
            isPaused = false;
            pauseBitmap = PauseButton.GetBitMap();
            myThread.unPause();
        }
        else
        {
            isPaused = true;
            pauseBitmap = PauseButton.GetBitMapPressed();
            myThread.pause();
        }
    }

    public void InitButtons()
    {
        RightButton = new GUIbutton();
        LeftButton = new GUIbutton();
        AttackButton = new GUIbutton();
        JumpButton = new GUIbutton();
        PauseButton = new GUIbutton();
        primaryButton = null;

        int buttonSize = (int)(0.7f * map.tileSize_Y * ((float)Screenwidth / (float)Screenheight));

        RightButton.SetButtonSize(buttonSize);
        LeftButton.SetButtonSize(buttonSize);
        JumpButton.SetButtonSize(buttonSize);
        AttackButton.SetButtonSize((int)(buttonSize * 1.45));
        PauseButton.SetButtonSize(buttonSize);

        LeftButton.SetButtonPos((int)(map.tileSize_X), (int)(7.8f * map.tileSize_Y));
        RightButton.SetButtonPos((int)(map.tileSize_X) + (int)(Screenwidth * 0.02) + buttonSize, (int)(7.8f * map.tileSize_Y));
        JumpButton.SetButtonPos((int)((map.GetCols() - 3.5f) * map.tileSize_X), (int)(7.8f * map.tileSize_Y));
        AttackButton.SetButtonPos((int)((map.GetCols() - 2) * map.tileSize_X), (int)(7.3f * map.tileSize_Y));
        PauseButton.SetButtonPos(Screenwidth / 2, 20);

        LeftButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true));
        RightButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true));
        JumpButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true));
        AttackButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true));
        PauseButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause), PauseButton.GetButtonSize(), PauseButton.GetButtonSize(), true));

        LeftButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton_pressed), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true));
        RightButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton_pressed), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true));
        JumpButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton_pressed), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true));
        AttackButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton_pressed), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true));
        PauseButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause1), PauseButton.GetButtonSize(), PauseButton.GetButtonSize(), true));

        // Init pause Bitmap
        pauseBitmap = PauseButton.GetBitMapPressed();
    }

    public void RenderButtons(Canvas canvas)
    {
        if(LeftButton.isPressed())
            canvas.drawBitmap(LeftButton.GetBitMapPressed(), (int)(LeftButton.GetPosition().x), (int)(LeftButton.GetPosition().y), null);
        else
            canvas.drawBitmap(LeftButton.GetBitMap(), (int)(LeftButton.GetPosition().x), (int)(LeftButton.GetPosition().y), null);

        if(RightButton.isPressed())
            canvas.drawBitmap(RightButton.GetBitMapPressed(), (int)(RightButton.GetPosition().x), (int)(RightButton.GetPosition().y), null);
        else
            canvas.drawBitmap(RightButton.GetBitMap(), (int)(RightButton.GetPosition().x), (int)(RightButton.GetPosition().y), null);

        if(AttackButton.isPressed())
            canvas.drawBitmap(AttackButton.GetBitMapPressed(), (int)(AttackButton.GetPosition().x), (int)(AttackButton.GetPosition().y), null);
        else
            canvas.drawBitmap(AttackButton.GetBitMap(), (int)(AttackButton.GetPosition().x), (int)(AttackButton.GetPosition().y), null);

        if(JumpButton.isPressed())
            canvas.drawBitmap(JumpButton.GetBitMapPressed(), (int)(JumpButton.GetPosition().x), (int)(JumpButton.GetPosition().y), null);
        else
            canvas.drawBitmap(JumpButton.GetBitMap(), (int)(JumpButton.GetPosition().x), (int)(JumpButton.GetPosition().y), null);
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

    public void RenderPlayerHealthBar(Canvas canvas) {
        Paint paint = new Paint();

        float boxWidth = Screenwidth / 3;
        float boxHeight = Screenheight / 20;
        float boxStroke = boxHeight / 10.f;

        // box
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(Screenwidth / 20 + 5, Screenheight / 20 - 5, 4 * Screenwidth/20, 2 * Screenheight/25, paint);

        int hpRatio = player.GetHP();

        // Fill the rectangle
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(Screenwidth/20 + 8, Screenheight / 20, 4 * Screenwidth/20, 2 * Screenheight/25, paint);
    }

    public void RenderGameplay(Canvas canvas) {
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }

        canvas.drawBitmap(scaledbg, bgX, bgY, null);    // 1st background image
        canvas.drawBitmap(scaledbg, bgX + Screenwidth, bgY, null);    // 2nd background image

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

        // HUD
        RenderPause(canvas);
        RenderButtons(canvas);
        canvas.drawBitmap(playerProfile, 0.5f * map.tileSize_X, 0.5f * map.tileSize_Y, null);
        RenderPlayerHealthBar(canvas);
        canvas.drawBitmap(bossProfile, (map.GetCols() - 1.5f) * map.tileSize_X, 0.5f * map.tileSize_Y, null);

        // Debug text
        RenderTextOnScreen(canvas, "FPS " + FPS, (int)(1.5f * map.tileSize_X), (int)(0.4f * map.tileSize_Y), textSize);
        //RenderTextOnScreen(canvas, "TP1 Initial: " + Point1.GetInitialPoint().ToString(), 130, 175, 50);
        //RenderTextOnScreen(canvas, "TP1 Current: " + Point1.GetCurrentPoint().ToString(), 130, 225, 50);

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
                player.spriteArray[player.GetState().GetValue()].update(dt_l);
                bossdragon.spriteArray[bossdragon.GetState().GetValue()].update(dt_l);

                // get key press
                if(RightButton.isPressed())
                {
                    player.SetState(Player.PLAYER_STATE.MOVE);
                    player.MoveRight(deltaTime, map);
                }
                else if(LeftButton.isPressed())
                {
                    player.SetState(Player.PLAYER_STATE.MOVE);
                    player.MoveLeft(deltaTime, map);
                }
                else {
                    player.SetState(Player.PLAYER_STATE.IDLE);
                }

                if (JumpButton.isPressed()) {
                    player.Jump();
                }

                if (AttackButton.isPressed()) {
                    // Init attack things here
                    toast.show();
                }

                // update player movement
                player.CheckIsInAir(map);
                if (player.IsInAir()) {
                    player.SetState(Player.PLAYER_STATE.JUMP);
                    player.UpdateJump(deltaTime, map);
                }

                // Trigger Alert box
                if (showAlert) {
                    //AlertObj.RunAlert();
                    showAlert = false;
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
        paint.setTypeface(font);

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

    public void RenderPause(Canvas canvas) {
        // Draw Pause button
        //if (isPaused) {
        //    canvas.drawBitmap(pauseBitmap, (int)(PauseButton.GetPosition().x), (int)(PauseButton.GetPosition().y), null);
        //}
        //else {
        //    canvas.drawBitmap(pauseBitmap, (int)(PauseButton.GetPosition().x), (int)(PauseButton.GetPosition().y), null);
        //}

        canvas.drawBitmap(pauseBitmap, (int)(PauseButton.GetPosition().x), (int)(PauseButton.GetPosition().y), null);

        if (isPaused) {
            RenderTextOnScreen(canvas, "Game Paused", (int) (3.5f * map.tileSize_X), (int) (0.4f * map.tileSize_Y), textSize);
        }
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
                        vibrator.vibrate(50);
                    }
                    LeftButton.SetPressed(true);
                    primaryButton = LeftButton;
                    Log.v("Primary Button", "Left Button");
                }
                Vector2 tempRight = new Vector2(RightButton.GetPosition());
                tempRight.x += RightButton.GetButtonSize() / 2;
                tempRight.y += RightButton.GetButtonSize() / 2;
                if(tempRight.Subtract(m_touch).GetLength() < RightButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    RightButton.SetPressed(true);
                    primaryButton = RightButton;
                    Log.v("Primary Button", "Right Button");
                }
                Vector2 tempAttack = new Vector2(AttackButton.GetPosition());
                tempAttack.x += AttackButton.GetButtonSize() / 2;
                tempAttack.y += AttackButton.GetButtonSize() / 2;
                if(tempAttack.Subtract(m_touch).GetLength() < AttackButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    AttackButton.SetPressed(true);
                    primaryButton = AttackButton;
                    Log.v("Primary Button", "Attack Button");
                }
                Vector2 tempJump = new Vector2(JumpButton.GetPosition());
                tempJump.x += JumpButton.GetButtonSize() / 2;
                tempJump.y += JumpButton.GetButtonSize() / 2;
                if(tempJump.Subtract(m_touch).GetLength() < JumpButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    JumpButton.SetPressed(true);
                    primaryButton = JumpButton;
                    Log.v("Primary Button", "Jump Button");
                }
                Vector2 tempPause = new Vector2(PauseButton.GetPosition());
                tempPause.x += PauseButton.GetButtonSize() / 2;
                tempPause.y += PauseButton.GetButtonSize() / 2;
                if(tempPause.Subtract(m_touch).GetLength() < PauseButton.GetButtonSize() / 2)
                {
                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    PauseButton.SetPressed(true);
                    PauseButtonPressed();   // pausing/unpausing action

                    primaryButton = PauseButton;
                    Log.v("Primary Button", "Pause Button");
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //Log.v("DEBUG:","ACTION_POINTER_DOWN");
                boolean leftbutton = false;
                boolean rightbutton = false;
                boolean jumpbutton = false;
                boolean attackbutton = false;
                boolean pausebutton = false;

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

                    tempPause = new Vector2(PauseButton.GetPosition());
                    tempPause.x += PauseButton.GetButtonSize() / 2;
                    tempPause.y += PauseButton.GetButtonSize() / 2;
                    if (tempPause.Subtract(touch).GetLength() < PauseButton.GetButtonSize() / 2) {
                        if (pausebutton == false) {
                           pausebutton = true;
                            continue;
                        }
                    }

                }
                if(leftbutton && !LeftButton.isPressed())
                {
                    LeftButton.SetPressed(true);
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
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
                        vibrator.vibrate(50);
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
                        vibrator.vibrate(50);
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
                        vibrator.vibrate(50);
                    }
                    JumpButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!jumpbutton)
                {
                    JumpButton.SetPressed(false);
                }
                if(pausebutton && !PauseButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    PauseButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!pausebutton)
                {
                    PauseButton.SetPressed(false);
                    PauseButtonPressed();   // pausing/unpausing action
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //Log.v("DEBUG:","ACTION_MOVE");
                leftbutton = false;
                rightbutton = false;
                jumpbutton = false;
                attackbutton = false;
                pausebutton = false;

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

                    tempPause = new Vector2(PauseButton.GetPosition());
                    tempPause.x += PauseButton.GetButtonSize() / 2;
                    tempPause.y += PauseButton.GetButtonSize() / 2;
                    if (tempPause.Subtract(touch).GetLength() < PauseButton.GetButtonSize() / 2) {
                        if (pausebutton == false) {
                            pausebutton = true;
                            continue;
                        }
                    }

                }
                if(leftbutton && !LeftButton.isPressed())
                {
                    LeftButton.SetPressed(true);
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
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
                        vibrator.vibrate(50);
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
                        vibrator.vibrate(50);
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
                        vibrator.vibrate(50);
                    }
                    JumpButton.SetPressed(true);
                    //Log.v("APD", "Right Button Pressed");
                }else if(!jumpbutton)
                {
                    JumpButton.SetPressed(false);
                }
                if (pausebutton && !PauseButton.isPressed())
                {
                    if (vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    PauseButton.SetPressed(true);
                } else if (!pausebutton)
                {
                    PauseButton.SetPressed(false);
                    PauseButtonPressed();   // pausing/unpausing action
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //test = false;
                break;
            case MotionEvent.ACTION_UP:
                //Log.v("DEBUG:","ACTION_UP");
                if(primaryButton != null)
                {
                    primaryButton.SetPressed(false);
                    primaryButton = null;
                }
                //RightButton.SetPressed(false);
                //LeftButton.SetPressed(false);
                //AttackButton.SetPressed(false);
                //JumpButton.SetPressed(false);
                break;
        }

        return true;
    }
}
