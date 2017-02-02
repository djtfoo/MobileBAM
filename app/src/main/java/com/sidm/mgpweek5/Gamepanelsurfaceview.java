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
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Vibrator;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Foo on 24/11/2016.
 */

public class Gamepanelsurfaceview extends SurfaceView implements SurfaceHolder.Callback
{
    // Implement this interface to receive information about changes to the surface.

    public static Gamepanelsurfaceview instance = null;

    private boolean debugInfo = false;

    private Gamethread myThread = null; // Thread to control the rendering
    private Vibratormanager vibrator;

    // 1a) Variables used for background rendering
    private Bitmap scaledbg;    //scaledbg = scaled version of bg
    // 1b) Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;

    private Bitmap playerProfile, bossProfile;

    public HashMap<String, Bitmap> bitmapList = new HashMap<String, Bitmap>();     // for instantiating objects during runtime

    // 1c) Variables for defining background start and end point
    private short bgX = 0, bgY = 0;

    // Used for tilemap rendering
    private Bitmap tile_ground;
    int textSize;

    // Fonts
    Typeface font;

    // Activity
    Activity activityTracker;   // use to track and then launch to the desired activity

    // Variables for FPS
    public float FPS;
    float deltaTime;

    Vector2 pos = new Vector2();
    Tilemap map = new Tilemap();
    Bossdragon bossdragon = new Bossdragon();

    TowerShieldgenerator testsg = new TowerShieldgenerator(100);

    private final int ButtonCount = 6;
    private GUIbutton Buttons[];
    GUIbutton RightButton;
    GUIbutton LeftButton;
    GUIbutton JumpButton;
    GUIbutton AttackButton;
    GUIbutton PauseButton;
    GUIbutton SwitchButton;
    JoyStick RangedJoyStick;
    GUIbutton primaryButton;

    // Pause
    private boolean isPaused = false;

    // Variable for Game State check
    private short GameState = 0;

    // Week 9 Sound
    Soundmanager soundmanager;

    // Week 13 Alert Dialog
    public boolean showAlert = false;
    private boolean hasShownAlert = false;
    private boolean winGame = false;

    AlertDialog.Builder alert = null;
    private Alert AlertObj;

    // Week 13 Shared Preferences
    SharedPreferences SharedPrefname;
    SharedPreferences.Editor editName;
    String Playername;

    //constructor for this GamePanelSurfaceView class
    public Gamepanelsurfaceview(Context context, Activity activity) {

        // Context is the current state of the application/object
        super(context);

        if(instance == null)
            instance = this;
        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        vibrator = new Vibratormanager(context);

        // 1d) Set information to get screen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;

        // Init tilemap
        map.Init(context, "MapTest.csv");
        map.tileSize_X = Screenwidth / map.GetCols();
        map.tileSize_Y = Screenheight / map.GetRows();

        // 1e)load the image when this class is being instantiated
        scaledbg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.boss1_scene), Screenwidth, Screenheight, true);

        tile_ground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tile_ground),
                (int) (map.tileSize_X), (int) (map.tileSize_Y), true);

        playerProfile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_profile),
                (int) (map.tileSize_X), (int) (map.tileSize_X), true);
        bossProfile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dragon_profile),
                (int) (map.tileSize_X), (int) (map.tileSize_X), true);

        textSize = (int) (0.3 * map.tileSize_X * ((float) Screenwidth / (float) Screenheight));

        //bitmapList.put("Arrow", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.projectile_arrow),
        //        (int) (0.5f * map.tileSize_X), (int) (0.5f * map.tileSize_X), true));
        bitmapList.put("Turret", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.projectile_turret),
                (int) (0.5f * map.tileSize_X), (int) (0.5f * map.tileSize_X), true));
        bitmapList.put("Arrow", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.projectile_turret),
                (int) (0.5f * map.tileSize_X), (int) (0.5f * map.tileSize_X), true));

        bitmapList.put("Missile", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.missile),
                (int) (2f * map.tileSize_X), (int) (2f * map.tileSize_X), true));

        bitmapList.put("explosion", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.explosion),
                (int) (2f * map.tileSize_X) * 4, (int) (2f * map.tileSize_X), true));

        InitButtons();

        // Init Player and GameObjects/Entities
        Player.instance.Init(context, map, Screenwidth, Screenheight);
        Player.instance.SetPosition(2 * map.tileSize_X, (map.GetRows() - 2) * map.tileSize_Y);
        bossdragon.Init(context, Screenwidth, Screenheight);
        bossdragon.SetPosition(Screenwidth / 2, Screenheight / 7 * 4);
        Gameobject.goList.add(bossdragon);

        testsg.Init(context, Screenwidth, Screenheight);
        testsg.SetPosition(3 * map.tileSize_X, (map.GetRows() - 6) * map.tileSize_Y);
        Gameobject.goList.add(testsg);

        testsg = new TowerShieldgenerator(100);
        testsg.Init(context, Screenwidth, Screenheight);
        testsg.SetPosition(14 * map.tileSize_X, (map.GetRows() - 5) * map.tileSize_Y);
        Gameobject.goList.add(testsg);
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

        // Week 9 Sound
        soundmanager = new Soundmanager(context);

        // Week 13 Shared Preferences
        SharedPrefname = getContext().getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        editName = SharedPrefname.edit();
        Playername = "Player1";
        Playername = SharedPrefname.getString("PlayerUSERID", "DEFAULT");

        // Week 13 Alert Dialog
        AlertObj = new Alert(this);
        alert = new AlertDialog.Builder(getContext());
    }

    // Week 9 Pause
    public void PauseButtonPressed() {
        if (isPaused)   // game is currently paused
        {
            isPaused = false;
            GameState = 0;  // the gameplay state
            //myThread.unPause();
        }
        else {
            isPaused = true;
            GameState = 1;  // the pause state
            //myThread.pause();
        }
    }

    public void InitButtons() {
        RightButton = new GUIbutton("Right");
        LeftButton = new GUIbutton("Left");
        AttackButton = new GUIbutton("Attack");
        JumpButton = new GUIbutton("Jump");
        PauseButton = new GUIbutton("Pause");
        SwitchButton = new GUIbutton("Switch");
        RangedJoyStick = new JoyStick("Ranged");
        primaryButton = null;

        int buttonSize = (int) (0.7f * map.tileSize_Y * ((float) Screenwidth / (float) Screenheight));

        RightButton.SetButtonSize(buttonSize);
        LeftButton.SetButtonSize(buttonSize);
        JumpButton.SetButtonSize(buttonSize);
        SwitchButton.SetButtonSize(buttonSize);
        AttackButton.SetButtonSize((int) (buttonSize * 1.45));
        PauseButton.SetButtonSize((int)(0.9f * map.tileSize_X));
        RangedJoyStick.SetButtonSize((int) (buttonSize * 1.45));

        LeftButton.SetButtonPos((int) (map.tileSize_X), (int) (7.8f * map.tileSize_Y));
        RightButton.SetButtonPos((int) (map.tileSize_X) + (int) (Screenwidth * 0.02) + buttonSize, (int) (7.8f * map.tileSize_Y));
        JumpButton.SetButtonPos((int) ((map.GetCols() - 3.5f) * map.tileSize_X), (int) (7.8f * map.tileSize_Y));
        AttackButton.SetButtonPos((int) ((map.GetCols() - 2) * map.tileSize_X), (int) (7.3f * map.tileSize_Y));
        PauseButton.SetButtonPos((int) ((map.GetCols() / 2 - 0.45f) * map.tileSize_X), (int) (0.2f * map.tileSize_Y));
        SwitchButton.SetButtonPos((int) (0.3f * map.tileSize_X), (int) (5.f * map.tileSize_Y));
        //RangedJoyStick.SetButtonPos((int) ((map.GetCols() - 4.f) * map.tileSize_X), (int) (5.5f *map.tileSize_Y));
        RangedJoyStick.SetButtonPos((int) ((map.GetCols() - 2) * map.tileSize_X), (int) (7.f * map.tileSize_Y));

        LeftButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true));
        RightButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true));
        JumpButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true));
        AttackButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true));
        PauseButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause), PauseButton.GetButtonSize(), PauseButton.GetButtonSize(), true));
        SwitchButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.switchbutton), SwitchButton.GetButtonSize(), SwitchButton.GetButtonSize(), true));
        RangedJoyStick.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.joy_bg), RangedJoyStick.GetButtonSize(), RangedJoyStick.GetButtonSize(), true));

        LeftButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton_pressed), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true));
        RightButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton_pressed), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true));
        JumpButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton_pressed), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true));
        AttackButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton_pressed), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true));
        PauseButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause1), PauseButton.GetButtonSize(), PauseButton.GetButtonSize(), true));
        SwitchButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.switchbutton_pressed), SwitchButton.GetButtonSize(), SwitchButton.GetButtonSize(), true));
        RangedJoyStick.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.joy_bg_pressed), RangedJoyStick.GetButtonSize(), RangedJoyStick.GetButtonSize(), true));

        RangedJoyStick.bmFG = (Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.joy_fg), RangedJoyStick.GetButtonSize(), RangedJoyStick.GetButtonSize(), true));

        Buttons = new GUIbutton[6];
        Buttons[0] = LeftButton;
        Buttons[1] = RightButton;
        Buttons[2] = JumpButton;
        Buttons[3] = AttackButton;
        Buttons[4] = PauseButton;
        Buttons[5] = SwitchButton;

        RangedJoyStick.active = false;
    }

    public void RenderButtons(Canvas canvas) {

        for(int i = 0; i < ButtonCount; i++)
        {
            if(!Buttons[i].active)
                continue;

            if (!isPaused && Buttons[i].isPressed())
                canvas.drawBitmap(Buttons[i].GetBitMapPressed(), (int) (Buttons[i].GetPosition().x), (int) (Buttons[i].GetPosition().y), null);
            else
                canvas.drawBitmap(Buttons[i].GetBitMap(), (int) (Buttons[i].GetPosition().x), (int) (Buttons[i].GetPosition().y), null);
        }

        if (isPaused)
            canvas.drawBitmap(PauseButton.GetBitMapPressed(), (int) (PauseButton.GetPosition().x), (int) (PauseButton.GetPosition().y), null);
        else
            canvas.drawBitmap(PauseButton.GetBitMap(), (int) (PauseButton.GetPosition().x), (int) (PauseButton.GetPosition().y), null);

        if(RangedJoyStick.active)
        {
            if(RangedJoyStick.isPressed())
            {
                canvas.drawBitmap(RangedJoyStick.GetBitMapPressed(), (int) (RangedJoyStick.GetPosition().x), (int) (RangedJoyStick.GetPosition().y), null);
                canvas.drawBitmap(RangedJoyStick.bmFG, (int) (RangedJoyStick.GetTouchPos().x), (int) (RangedJoyStick.GetTouchPos().y), null);
            }
            else
                canvas.drawBitmap(RangedJoyStick.GetBitMap(), (int) (RangedJoyStick.GetPosition().x), (int) (RangedJoyStick.GetPosition().y), null);
        }
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder) {
        // Create the thread
        if (!myThread.isAlive()) {
            myThread = new Gamethread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
        }

        soundmanager.PlayBGM();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Destroy the thread
        if (myThread.isAlive()) {
            myThread.startRun(false);
            soundmanager.StopBGM();
            soundmanager.Exit();
        }
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

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

        // box
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(1.55f * map.tileSize_X, 0.5f * map.tileSize_Y, ((map.GetCols() / 2) - 0.5f) * map.tileSize_X, 0.9f * map.tileSize_Y, paint);

        float hpRatio = (float) Player.instance.GetHP() / (float) Player.instance.GetMaxHP();
        float hpBarStart = 1.55f * map.tileSize_X;
        float hpBarLength = ((map.GetCols() / 2) - 0.55f) * map.tileSize_X - hpBarStart;

        // Fill the rectangle
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(hpBarStart, 0.55f * map.tileSize_Y, hpRatio * hpBarLength + hpBarStart, 0.875f * map.tileSize_Y, paint);
    }

    public void RenderBossHealthBar(Canvas canvas) {
        Paint paint = new Paint();

        // box
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(((map.GetCols() / 2) + 0.5f) * map.tileSize_X, 0.5f * map.tileSize_Y, (map.GetCols() - 1.55f) * map.tileSize_X, 0.9f * map.tileSize_Y, paint);

        float hpRatio = (float) bossdragon.GetHP() / (float) bossdragon.GetMaxHP();
        float hpBarStart = (map.GetCols() - 1.55f) * map.tileSize_X;
        float hpBarLength = hpBarStart - ((map.GetCols() / 2) + 0.55f) * map.tileSize_X;

        // Fill the rectangle
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(hpBarStart - hpRatio * hpBarLength, 0.55f * map.tileSize_Y, hpBarStart, 0.875f * map.tileSize_Y, paint);
    }

    public void RenderGameplay(Canvas canvas) {
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }

        canvas.drawBitmap(scaledbg, bgX, bgY, null);    // 1st background image
        //canvas.drawBitmap(scaledbg, bgX + Screenwidth, bgY, null);    // 2nd background image

        // draw the boss enemy
        bossdragon.spriteArray[bossdragon.GetState().GetValue()].setX((int) bossdragon.GetPosition().x);
        bossdragon.spriteArray[bossdragon.GetState().GetValue()].setY((int) bossdragon.GetPosition().y);

        for(int i = 0; i < Gameobject.missileList.size(); ++i)
        {
            Missile go = (Missile)Gameobject.missileList.get(i);
            if(go.state == Missile.MISSILE_STATE.LAUNCH)
            DrawGameobject(canvas, go);
        }

        bossdragon.spriteArray[bossdragon.GetState().GetValue()].draw(canvas);

        // draw the tilemap
        drawTilemap(canvas);

        // draw the Gameobjects
        for (int i = 0; i < Gameobject.goList.size(); ++i)
        {
            Gameobject go = Gameobject.goList.get(i);
            if(go.type == "boss" || go.type == "missile")
                continue;
            DrawGameobject(canvas, go);
        }

        for(int i =0; i < Gameobject.missileList.size(); ++i)
        {
            Missile go = (Missile)Gameobject.missileList.get(i);
            if(go.state == Missile.MISSILE_STATE.TRACKING)
                DrawGameobject(canvas, go);
        }

        if (debugInfo || (!debugInfo && bossdragon.shielded))
        {
            for(int i = 0; i < 1; i++)
            {
                Paint paint = new Paint();

                // go AABB
                Vector2 min = bossdragon.HitBoxes[i].AABBCollider.GetMinAABB();
                Vector2 max = bossdragon.HitBoxes[i].AABBCollider.GetMaxAABB();
                Vector2 goPos = bossdragon.HitBoxes[i].GetPosition();

                paint.setColor(Color.RED);
                paint.setStrokeWidth(5);
                paint.setStyle(Paint.Style.STROKE);
                // sequence is minX, maxY, maxX, minY, where min point is the top left corner
                canvas.drawRect(goPos.x + min.x, goPos.y + max.y,
                        goPos.x + max.x, goPos.y + min.y, paint);
            }
        }

        // draw the player
        DrawPlayer(canvas);

        for (int i = 0; i < Gameobject.particleList.size(); ++i)
        {
            Gameobject go = Gameobject.particleList.get(i);
            DrawGameobject(canvas, go);
        }

        // HUD
        RenderButtons(canvas);
        // Player
        canvas.drawBitmap(playerProfile, 0.5f * map.tileSize_X, 0.5f * map.tileSize_Y, null);
        RenderPlayerHealthBar(canvas);
        // Boss
        canvas.drawBitmap(bossProfile, (map.GetCols() - 1.5f) * map.tileSize_X, 0.5f * map.tileSize_Y, null);
        RenderBossHealthBar(canvas);

        if(RangedJoyStick.isPressed())
        {
            Vector2 Test = RangedJoyStick.GetValue().Multiply(5000);
            Test = Test.Add(Player.instance.GetPosition());
            Log.v("Value", "Line Pos: " + RangedJoyStick.GetValue().ToString() + " Player Pos: " + Player.instance.GetPosition().ToString());
            Paint line = new Paint();
            line.setARGB(100, 255, 0, 0);
            line.setStyle(Paint.Style.STROKE);
            line.setStrokeWidth(10);
            canvas.drawLine(Player.instance.GetPosition().x, Player.instance.GetPosition().y, Test.x, Test.y, line);
        }

        // DEBUG TEXT
        if (debugInfo)
        {
            RenderTextOnScreen(canvas, "FPS " + FPS, (int) (1.5f * map.tileSize_X), (int) (0.4f * map.tileSize_Y), textSize);
            //RenderTextOnScreen(canvas, "TP1 Initial: " + Point1.GetInitialPoint().ToString(), 130, 175, 50);
            //RenderTextOnScreen(canvas, "TP1 Current: " + Point1.GetCurrentPoint().ToString(), 130, 225, 50);

            if (!isPaused)
            {
                if (RightButton.isPressed()) {
                    RenderTextOnScreen(canvas, "Right Button Pressed", 130, 275, textSize);
                }
                if (LeftButton.isPressed()) {
                    RenderTextOnScreen(canvas, "Left Button Pressed", 130, 325, textSize);
                }
                if (AttackButton.isPressed()) {
                    RenderTextOnScreen(canvas, "Attack Button Pressed", 130, 375, textSize);
                }
                if (JumpButton.isPressed()) {
                    RenderTextOnScreen(canvas, "Jump Button Pressed", 130, 425, textSize);
                }
            }
        }

    }

    // Draw functions
    private void DrawPlayer(Canvas canvas)
    {
        // player sprite
        Player.instance.spriteArray[Player.instance.GetState().GetValue()].setX((int) Player.instance.GetPosition().x);
        Player.instance.spriteArray[Player.instance.GetState().GetValue()].setY((int) Player.instance.GetPosition().y);
        Player.instance.spriteArray[Player.instance.GetState().GetValue()].draw(canvas);

        if(debugInfo)
        {
            // player AABB
            Vector2 min = Player.instance.GetCollider().GetMinAABB();
            Vector2 max = Player.instance.GetCollider().GetMaxAABB();
            Vector2 playerPos = Player.instance.GetPosition();

            Paint paint = new Paint();

            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);

            // sequence is minX, maxY, maxX, minY, where min point is the top left corner
            canvas.drawRect(playerPos.x + min.x, playerPos.y + max.y,
                    playerPos.x + max.x, playerPos.y + min.y, paint);
        }

        // player's sword AABB
    }

    private void DrawGameobject(Canvas canvas, Gameobject go)
    {
        go.spriteArray[0].setX((int) go.GetPosition().x);
        go.spriteArray[0].setY((int) go.GetPosition().y);
        go.spriteArray[0].draw(canvas);

        Paint paint = new Paint();

        if(debugInfo) {
            // go AABB
            Vector2 min = go.GetCollider().GetMinAABB();
            Vector2 max = go.GetCollider().GetMaxAABB();
            Vector2 goPos = go.GetPosition();

            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            // sequence is minX, maxY, maxX, minY, where min point is the top left corner
            canvas.drawRect(goPos.x + min.x, goPos.y + max.y,
                    goPos.x + max.x, goPos.y + min.y, paint);
        }

        if(go.type == "shieldgenerator")
        {
            Paint line = new Paint();
            line.setARGB(255, 150, 150, 255);
            line.setStyle(Paint.Style.STROKE);
            line.setStrokeWidth(10);
            for(int i = 0; i < Gameobject.missileList.size(); ++i)
            {
                Missile temp = Gameobject.missileList.get(i);
                if( temp.position.Subtract(go.position).GetLength() < map.tileSize_X * 4 )
                {
                    temp.shielded = true;
                    canvas.drawLine(go.position.x, go.position.y, temp.position.x, temp.position.y, line);
                    canvas.drawCircle(temp.position.x, temp.position.y, temp.GetCollider().GetMaxAABB().y, line);
                }else
                {
                    temp.shielded = false;
                }
            }

            if( bossdragon.HitBoxes[0].position.Subtract(go.position).GetLength() < map.tileSize_X * 4 )
            {
                bossdragon.shielded = true;
                canvas.drawLine(go.position.x, go.position.y, bossdragon.HitBoxes[0].position.x, bossdragon.HitBoxes[0].position.y, line);
                canvas.drawCircle(bossdragon.HitBoxes[0].position.x, bossdragon.HitBoxes[0].position.y, bossdragon.HitBoxes[0].GetCollider().GetMaxAABB().y, line);
            }else
            {
                bossdragon.shielded = false;
            }
        }
    }


    private boolean Collided(Gameobject go1, Gameobject go2)
    {
        if(go1.GetCollider().GetMinAABB().x + go1.position.x < go2.GetCollider().GetMaxAABB().x + go2.position.x &&
                go1.GetCollider().GetMaxAABB().x + go1.position.x > go2.GetCollider().GetMinAABB().x + go2.position.x &&
                go1.GetCollider().GetMinAABB().y + go1.position.y < go2.GetCollider().GetMaxAABB().y + go2.position.y &&
                go1.GetCollider().GetMaxAABB().y + go1.position.y > go2.GetCollider().GetMinAABB().y + go2.position.y)
        {
            return true;
        }

        return false;
    }

    private boolean CollidedWithPlayer(Gameobject go)
    {
        if(go.GetCollider().GetMinAABB().x + go.position.x < Player.instance.GetCollider().GetMaxAABB().x + Player.instance.GetPosition().x &&
                go.GetCollider().GetMaxAABB().x + go.position.x > Player.instance.GetCollider().GetMinAABB().x + Player.instance.GetPosition().x &&
                go.GetCollider().GetMinAABB().y + go.position.y < Player.instance.GetCollider().GetMaxAABB().y + Player.instance.GetPosition().y &&
                go.GetCollider().GetMaxAABB().y + go.position.y > Player.instance.GetCollider().GetMinAABB().y + Player.instance.GetPosition().y)
        {
            return true;
        }

        return false;
    }

    Vector<Vector2> ToBeCreated =  new Vector<Vector2>();

    private void CheckForCollision()
    {
        for(int i = 0; i < Gameobject.goList.size(); i++)
        {
            Gameobject go1 = Gameobject.goList.get(i);
            if(!go1.hasCollider)
                continue;
            for(int j = 0; j < Gameobject.goList.size(); j++)
            {
                Gameobject go2 = Gameobject.goList.get(j);
                if(!go2.hasCollider)
                    continue;
                if(go1 == go2 || go2.toBeDestroyed || go1.toBeDestroyed)
                    continue;

                if(go1.type == "boss" || go2.type == "boss")
                {
                    Gameobject goA, goB;
                    if(go1.type == "boss") {
                        goA = go1;
                        goB = go2;
                    }
                    else {
                        goA = go2;
                        goB = go1;
                    }
                    Bossdragon temp = (Bossdragon)goA;
                    for(int k = 0; k < 1; k++)
                    {
                        if (Collided(bossdragon.HitBoxes[k], goB))
                        {
                            if(goB.type == "projectile")
                            {
                                if(!bossdragon.shielded)
                                bossdragon.SetHP(temp.GetHP() - ((Projectile)goB).damage);
                                ToBeCreated.add(goB.position);
                                goB.toBeDestroyed = true;
                            }
                            break;
                        }
                    }
                }else if(Collided(go1, go2))
                {
                    if(go1.type == "missile" && go2.type == "missile")
                        continue;

                    if(go1.type == "shieldgenerator" || go2.type == "shieldgenerator")
                    {
                        Gameobject goA, goB;
                        if (go1.type == "shieldgenerator") {
                            goA = go1;
                            goB = go2;
                        } else {
                            goA = go2;
                            goB = go1;
                        }

                        if(goB.type == "missile")
                            continue;
                    }

                    if(!go1.shielded)
                    {
                        ((Tower)go1).SetHP(((Tower)go1).GetHP() - ((Projectile)go2).damage);
                        if(((Tower)go1).GetHP() <= 0)
                            go1.toBeDestroyed = true;
                        ToBeCreated.add(go1.position);

                    }
                    if(!go2.shielded)
                    {
                        go2.toBeDestroyed = true;
                        ToBeCreated.add(go2.position);
                    }
                }
            }
            if(!go1.toBeDestroyed)
            {
                if(CollidedWithPlayer(go1))
                {
                    if(go1.type == "missile")
                    {
                        if( ((Missile)go1).state == Missile.MISSILE_STATE.LAUNCH)
                            continue;
                        Player.instance.SetHP(Player.instance.GetHP() - ((Projectile)go1).damage);
                        ToBeCreated.add(go1.position);
                        go1.toBeDestroyed = true;
                        ToBeCreated.add(go1.position);
                    }
                }
            }
        }

        // Spawn stuff here lmao
        if (ToBeCreated.size() > 0)
            soundmanager.PlaySFXExplosion();

        while(ToBeCreated.size() > 0)
        {
            Particle temp = new Particle();
            temp.Init();
            temp.position = ToBeCreated.lastElement();
            Gameobject.particleList.add(temp);
            ToBeCreated.remove(ToBeCreated.lastElement());
        }

    }

    private void EntityUpdate(float dt, long dt_l)
    {
        BossUpdate(dt, dt_l);
        for (int i = 0; i < Gameobject.goList.size(); ++i)
        {
            Gameobject.goList.get(i).Update(dt);
        }

        for (int i =0 ;i < Gameobject.particleList.size(); ++i)
        {
            Gameobject.particleList.get(i).Update(dt);
        }

        CheckForCollision();

        Iterator<Gameobject> goItr = Gameobject.goList.iterator();

        while(goItr.hasNext())
        {
            Gameobject temp = goItr.next();
            if(temp.toBeDestroyed)
                goItr.remove();
        }

        Iterator<Missile> missileItr = Gameobject.missileList.iterator();

        while(missileItr.hasNext())
        {
            Missile temp = missileItr.next();
            if(temp.toBeDestroyed)
                missileItr.remove();
        }

        Iterator<Particle> particleItr = Gameobject.particleList.iterator();

        while(particleItr.hasNext())
        {
            Particle temp = particleItr.next();
            if(temp.toBeDestroyed)
                particleItr.remove();
        }

    }


    private float BossMissileAttackTimer = 0f;

    private void BossUpdate(float dt, long gameTime)
    {
        //bossdragon.spriteArray[bossdragon.GetState().GetValue()].update(gameTime);
        BossMissileAttackTimer += dt;

        if(BossMissileAttackTimer > 5f)
        {
            if(Gameobject.missileList.size() < 5)
            bossdragon.SpawnMissiles();
            BossMissileAttackTimer = 0f;
            soundmanager.PlaySFXMissileLaunch();
        }


        if (bossdragon.IsDead()) {
            showAlert = true;
            winGame = true;
        }
    }


    //Update method to update the game play
    public void update(float dt, float fps) {
        this.deltaTime = dt;
        FPS = 1 / dt;
        //Log.v("FPS", Float.toString(FPS));
        long dt_l = System.currentTimeMillis();

        if (dt > 1f)
        {
            dt = 0.01333f;
        }

       switch (GameState) {
            case 0: {
                // update sprite - make sprite animate
                //Player.instance.spriteArray[Player.instance.GetState().GetValue()].update(dt_l);
                if (Player.instance.GetAttackState() != Player.PLAYER_STATE.RANGED_ATTACK)
                    Player.instance.spriteArray[Player.instance.GetState().GetValue()].update(dt);

                EntityUpdate(dt, dt_l);

                if (Player.instance.IsDead())
                {
                    showAlert = true;
                    winGame = false;
                }

                // Do attack things here
                if (Player.instance.GetAttackState() == Player.PLAYER_STATE.RANGED_ATTACK) {
                    Player.instance.DoRangedAttack(dt, this);
                }
                else if (Player.instance.GetAttackState() != Player.PLAYER_STATE.IDLE) {
                    Player.instance.DoMeleeAttack(this);
                }
                // get key press
                if (Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE || Player.instance.GetAttackState() == Player.PLAYER_STATE.JUMP_ATTACK)
                {
                    if (RightButton.isPressed()) {
                        if (Player.instance.GetAttackState() != Player.PLAYER_STATE.JUMP_ATTACK)
                            Player.instance.SetState(Player.PLAYER_STATE.MOVE);

                        Player.instance.MoveRight(deltaTime, map);
                        Player.instance.SetFlipSprite(false);
                    } else if (LeftButton.isPressed()) {
                        if (Player.instance.GetAttackState() != Player.PLAYER_STATE.JUMP_ATTACK)
                            Player.instance.SetState(Player.PLAYER_STATE.MOVE);
                        Player.instance.MoveLeft(deltaTime, map);
                        Player.instance.SetFlipSprite(true);
                    } else {    // player not pressing a moving button
                        if (Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE) {
                            Player.instance.SetState(Player.PLAYER_STATE.IDLE);
                        }
                    }
                }

                if (JumpButton.isPressed()) {
                    Player.instance.Jump(map, soundmanager);
                }

                if(SwitchButton.isPressed() && !SwitchButton.hold)
                {
                    SwitchButton.hold = true;
                    AttackButton.active = !AttackButton.active;
                    //JumpButton.active = !JumpButton.active;
                    RangedJoyStick.active = !RangedJoyStick.active;
                }else if(!SwitchButton.isPressed() && SwitchButton.hold)
                {
                    SwitchButton.hold = false;
                }

                if(RangedJoyStick.isPressed() && !RangedJoyStick.hold)
                {
                    RangedJoyStick.hold = true;
                    if (Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE)
                        Player.instance.SetStartRangedAttack();
                }else if(!RangedJoyStick.isPressed() && RangedJoyStick.hold)
                {
                    //On Release
                    RangedJoyStick.hold = false;
                    Player.instance.SetShootArrow(true);
                }

                // update player movement
                Player.instance.CheckIsInAir(map);
                if (Player.instance.IsInAir()) {
                    Player.instance.UpdateJump(deltaTime, map);
                }

                Player.instance.FlipSpriteAnimation();

                // Trigger Alert box
                if (!hasShownAlert && showAlert) {

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
                    if (winGame)
                    {
                        alert.setTitle("You Win!");
                        alert.setMessage("Record your name");
                        alert.setCancelable(false);
                        alert.setIcon(R.drawable.icon_player_small);
                        alert.setView(input);

                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                Playername = input.getText().toString();
                                editName.putString("PlayerUSERID", Playername);
                                editName.commit();

                                Intent intent = new Intent();
                                intent.setClass(getContext(), Legacypage.class);
                                activityTracker.startActivity(intent);
                            }
                        });
                    }
                    else
                    {
                        alert.setTitle("You Lose...");
                        alert.setMessage("Return to Level Select");
                        alert.setCancelable(false);
                        alert.setIcon(R.drawable.icon_dragon_small);

                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                Intent intent = new Intent();
                                intent.setClass(getContext(), Worldmappage.class);
                                activityTracker.startActivity(intent);
                            }
                        });
                    }

                    AlertObj.RunAlert();
                    showAlert = false;
                    hasShownAlert = true;
                }

                break;
            }
           case 1:

               break;
        }
    }

    // Rendering is done on Canvas
    public void doDraw(Canvas canvas) {
        switch (GameState) {
            case 0:
                RenderGameplay(canvas);
                break;

            case 1:
                RenderGameplay(canvas);
                RenderPause(canvas);
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

    public void RenderPause(Canvas canvas)
    {
        RenderTextOnScreen(canvas, "Game Paused", (int) ((map.GetCols() / 2 + 1) * map.tileSize_X), (int) (0.4f * map.tileSize_Y), textSize);
    }


    private boolean CheckButtonPressed(Vector2 m_touch, GUIbutton button)
    {
            Vector2 buttonPos = new Vector2(button.GetPosition());
            buttonPos.x += button.GetButtonSize() / 2;
            buttonPos.y += button.GetButtonSize() / 2;
            if(buttonPos.Subtract(m_touch).GetLength() < button.GetButtonSize() / 2) {
                return true;
                //Log.v("Input", "Button Pressed!");
            }

        return false;
    }

    private void ProcessInputDown(MotionEvent event)
    {
        //Log.v("STATE", "INPUT DOWN");
        int PointerID = event.getActionIndex();
        if(PointerID < 0)
            return;

        if (event.getPointerCount() >= 5)
            debugInfo = !debugInfo;

        Vector2 CurrentTouchPos = new Vector2(
                event.getX(PointerID),
                event.getY(PointerID)
        );

        if(RangedJoyStick.active && !RangedJoyStick.isPressed() && RangedJoyStick.PointerIndex == -1)
        {
            if(CheckButtonPressed(CurrentTouchPos, RangedJoyStick))
            {
                RangedJoyStick.SetPressed(true);
                RangedJoyStick.PointerIndex = PointerID;
                return;
            }
        }else if(RangedJoyStick.PointerIndex == PointerID)
        {
            RangedJoyStick.SetTouchPos(CurrentTouchPos);
            return;
        }

        for (int i = 0; i < ButtonCount; i++) {
            if(!Buttons[i].active || Buttons[i].isPressed())
                continue;
            if (CheckButtonPressed(new Vector2(event.getX(PointerID), event.getY(PointerID)), Buttons[i]))
            {
                vibrator.Vibrate(50);

                if (!Buttons[i].isPressed())
                {
                    Buttons[i].SetPressed(true);

                    // Activate unique effects of button type
                    if (Buttons[i].GetName() == "Attack" && Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE)
                    {
                        Player.instance.SetStartMeleeAttack();
                    }
                    else if (Buttons[i].GetName() == "Pause")
                    {
                        PauseButtonPressed();
                    }
                }

                Buttons[i].PointerIndex = PointerID;
            }
        }
    }

    private void ProcessInputUp(MotionEvent event)
    {
       //Log.v("STATE", "INPUT UP");
        int PointerID = event.getActionIndex();
        if(PointerID < 0)
            return;
        //Log.v("STATE", Integer.toString(PointerID));

        if(RangedJoyStick.PointerIndex == PointerID)
        {
            RangedJoyStick.PointerIndex = -1;
            RangedJoyStick.SetPressed(false);
            return;
        }

        for (int i = 0; i < ButtonCount; i++) {
            //Log.v("Buttons", Integer.toString(Buttons[i].PointerIndex));
            if (Buttons[i].PointerIndex == PointerID)
            {
                Buttons[i].PointerIndex = -1;
                Buttons[i].SetPressed(false);
            }
        }
    }

    private boolean CheckIfLineIntersect(Vector2 lineStart, Vector2 lineEnd, Vector2 CirclePos, float radius)
    {
        //parameters: ax ay bx by cx cy r
        lineStart.x -= CirclePos.x;
        lineStart.y -= CirclePos.y;
        lineEnd.x -= CirclePos.x;
        lineEnd.y -= CirclePos.y;
        float a = (lineStart.x * lineStart.x) + (lineStart.y * lineStart.y) - (radius * radius);
        float b = 2*(lineStart.x*(lineEnd.x - lineStart.x) + lineStart.y*(lineEnd.y - lineStart.y));
        float c = (lineEnd.x - lineStart.x)*(lineEnd.x - lineStart.x) + (lineEnd.y - lineStart.x)* (lineEnd.y - lineStart.x);
        float disc = (b*b) - 4*a*c;
        if (disc >= 0) return true;

        return false;
        //if(disc <= 0) return false;
        //float sqrtdisc = (float)Math.sqrt(disc);
        //float t1 = (-b + sqrtdisc)/(2*a);
        //float t2 = (-b - sqrtdisc)/(2*a);
        //if(0 < t1 && t1 < 1 && 0 < t2 && t2 < 1) return true;
        //return false;
    }

    private void ProcessInputMove(MotionEvent event)
    {
        //Log.v("STATE", "INPUT DRAG");
        int pointerID = event.getActionIndex();
        if(pointerID < 0)
            return;
        Vector2 CurrentTouchPos = new Vector2(
                event.getX(pointerID),
                event.getY(pointerID)
        );


        if(RangedJoyStick.active && !RangedJoyStick.isPressed() && RangedJoyStick.PointerIndex == -1)
        {
            if(CheckButtonPressed(CurrentTouchPos, RangedJoyStick))
            {
                RangedJoyStick.SetPressed(true);
                RangedJoyStick.PointerIndex = pointerID;
            }
        }else if(RangedJoyStick.PointerIndex == pointerID)
        {
            RangedJoyStick.SetTouchPos(CurrentTouchPos);
            return;
        }

        for(int i = 0; i < ButtonCount; i++)
        {
            if(!Buttons[i].active)
                continue;
            if(CheckButtonPressed(CurrentTouchPos, Buttons[i]))
            {
                if (!Buttons[i].isPressed())
                {
                    Buttons[i].SetPressed(true);

                    // Activate unique effects of button type
                    if (Buttons[i].GetName() == "Attack")
                    {
                        Player.instance.SetStartMeleeAttack();
                    }
                }

                Buttons[i].PointerIndex = pointerID;
            }
            else
            {
                if(Buttons[i].PointerIndex == pointerID)
                {
                    Buttons[i].PointerIndex = -1;
                    Buttons[i].SetPressed(false);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        //Log.v("TEST", Integer.toString(event.getPointerCount()));
        switch(action & event.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                ProcessInputDown(event);
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                ProcessInputUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                ProcessInputMove(event);
                break;
        }
        return true;
    }

}
