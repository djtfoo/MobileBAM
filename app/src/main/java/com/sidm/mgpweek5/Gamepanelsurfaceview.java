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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
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

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Foo on 24/11/2016.
 */

public class Gamepanelsurfaceview extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
    // Implement this interface to receive information about changes to the surface.

    private Gamethread myThread = null; // Thread to control the rendering
    private Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

    // 1a) Variables used for background rendering
    private Bitmap bg, scaledbg;    // bg = background; scaledbg = scaled version of bg
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

    // Week 13 Toast
    CharSequence text;
    int toastTime;
    Toast toast;

    // Week 13 Alert Dialog
    public boolean showAlert = false;
    private boolean hasShownAlert = false;

    AlertDialog.Builder alert = null;
    private Alert AlertObj;

    // Week 13 Shared Preferences
    SharedPreferences SharedPrefname;
    SharedPreferences.Editor editName;
    String Playername;

    // Week 14 Accelerometer test
    private SensorManager sensor;
    float[] SensorVar = new float[3];
    private float[] values = {0 ,0, 0};

    private Bitmap ball;
    private Vector2 ballCoord = new Vector2();
    private long lastTime = System.currentTimeMillis();

    //constructor for this GamePanelSurfaceView class
    public Gamepanelsurfaceview(Context context, Activity activity) {

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

        InitButtons();
        //Point1 = new TouchPoint();

        // Init Player and GameObjects/Entities
        player.Init(context, map, Screenwidth, Screenheight);
        player.SetPosition(2 * map.tileSize_X, (map.GetRows() - 2) * map.tileSize_Y);
        bossdragon.Init(context, Screenwidth, Screenheight);
        bossdragon.SetPosition(Screenwidth / 2, Screenheight / 7 * 4);

        //Gameobject.goList.add(bossdragon);

        testsg.Init(context, Screenwidth, Screenheight);
        testsg.SetPosition(14 * map.tileSize_X, (map.GetRows() - 2) * map.tileSize_Y);
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

        // Week 13 Toast
        Toastmessage(context);

        // Week 14 Accelerometer
        sensor = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor.registerListener(this, sensor.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_NORMAL);
        ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.testball), Screenwidth / 6, Screenheight / 6, true);

        // Week 13 Shared Preferences
        SharedPrefname = getContext().getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        editName = SharedPrefname.edit();
        Playername = "Player1";
        Playername = SharedPrefname.getString("PlayerUSERID", "DEFAULT");

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
        alert.setTitle("You Win!");
        alert.setMessage("Record your name");
        alert.setCancelable(false);
        alert.setIcon(R.drawable.icon_dragon_small);
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

    // Week 13 Toast
    public void Toastmessage(Context context) {
        text = "Attack!";
        toastTime = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, toastTime);
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
        RangedJoyStick.SetButtonSize(buttonSize * 3);

        LeftButton.SetButtonPos((int) (map.tileSize_X), (int) (7.8f * map.tileSize_Y));
        RightButton.SetButtonPos((int) (map.tileSize_X) + (int) (Screenwidth * 0.02) + buttonSize, (int) (7.8f * map.tileSize_Y));
        JumpButton.SetButtonPos((int) ((map.GetCols() - 3.5f) * map.tileSize_X), (int) (7.8f * map.tileSize_Y));
        AttackButton.SetButtonPos((int) ((map.GetCols() - 2) * map.tileSize_X), (int) (7.3f * map.tileSize_Y));
        PauseButton.SetButtonPos((int) ((map.GetCols() / 2 - 0.45f) * map.tileSize_X), (int) (0.2f * map.tileSize_Y));
        SwitchButton.SetButtonPos((int) (0.3f * map.tileSize_X), (int) (5.f * map.tileSize_Y));
        RangedJoyStick.SetButtonPos((int) ((map.GetCols() - 4.f) * map.tileSize_X), (int) (5.5f *map.tileSize_Y));


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

        // Release the memory
        sensor.unregisterListener(this);
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

        float hpRatio = (float) player.GetHP() / (float) player.GetMaxHP();
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
        bossdragon.spriteArray[bossdragon.GetState().GetValue()].draw(canvas);

        // draw the tilemap
        drawTilemap(canvas);

        // draw the Gameobjects
        for (int i = 0; i < Gameobject.goList.size(); ++i)
        {
            Gameobject go = Gameobject.goList.get(i);
            DrawGameobject(canvas, go);
        }

        // draw the player
        DrawPlayer(canvas);

        // HUD
        RenderButtons(canvas);
        // Player
        canvas.drawBitmap(playerProfile, 0.5f * map.tileSize_X, 0.5f * map.tileSize_Y, null);
        RenderPlayerHealthBar(canvas);
        // Boss
        canvas.drawBitmap(bossProfile, (map.GetCols() - 1.5f) * map.tileSize_X, 0.5f * map.tileSize_Y, null);
        RenderBossHealthBar(canvas);

        // Debug text
        RenderTextOnScreen(canvas, "FPS " + FPS, (int) (1.5f * map.tileSize_X), (int) (0.4f * map.tileSize_Y), textSize);
        //RenderTextOnScreen(canvas, "TP1 Initial: " + Point1.GetInitialPoint().ToString(), 130, 175, 50);
        //RenderTextOnScreen(canvas, "TP1 Current: " + Point1.GetCurrentPoint().ToString(), 130, 225, 50);

        if(RangedJoyStick.isPressed())
        {
            Vector2 Test = RangedJoyStick.GetValue().Multiply(1500);
            Test = Test.Add(player.GetPosition());
            Log.v("Value", "Line Pos: " + RangedJoyStick.GetValue().ToString() + " Player Pos: " + player.GetPosition().ToString());
            Paint line = new Paint();
            line.setARGB(100, 255, 0, 0);
            line.setStyle(Paint.Style.STROKE);
            line.setStrokeWidth(10);
            canvas.drawLine(player.GetPosition().x, player.GetPosition().y, Test.x, Test.y, line);
        }

        // DEBUG TEXT
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

        // Week 14 Accelerometer test ball
        canvas.drawBitmap(ball, ballCoord.x, ballCoord.y, null);
    }

    // Draw functions
    private void DrawPlayer(Canvas canvas)
    {
        // player sprite
        player.spriteArray[player.GetState().GetValue()].setX((int) player.GetPosition().x);
        player.spriteArray[player.GetState().GetValue()].setY((int) player.GetPosition().y);
        player.spriteArray[player.GetState().GetValue()].draw(canvas);

        // player AABB
        Vector2 min = player.GetCollider().GetMinAABB();
        Vector2 max = player.GetCollider().GetMaxAABB();
        Vector2 playerPos = player.GetPosition();

        Paint paint = new Paint();

        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        // sequence is minX, maxY, maxX, minY, where min point is the top left corner
        canvas.drawRect(playerPos.x + min.x, playerPos.y + max.y,
                playerPos.x + max.x, playerPos.y + min.y, paint);

        // player's sword AABB
    }

    private void DrawGameobject(Canvas canvas, Gameobject go)
    {
        go.spriteArray[0].setX((int) go.GetPosition().x);
        go.spriteArray[0].setY((int) go.GetPosition().y);
        go.spriteArray[0].draw(canvas);

        Paint paint = new Paint();

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

    //Update method to update the game play
    public void update(float dt, float fps) {
        this.deltaTime = dt;
        FPS = 1 / dt;
        //Log.v("FPS", Float.toString(FPS));
        long dt_l = System.currentTimeMillis();

       switch (GameState) {
            case 0: {
                // 3) Update the background to allow panning effect
                //bgX -= 200 * deltaTime;      // temp value to speed the panning
                //if (bgX < -Screenwidth) {
                //    bgX = 0;
                //}

                // update sprite - make sprite animate
                //player.spriteArray[player.GetState().GetValue()].update(dt_l);
                if (player.GetAttackState() != Player.PLAYER_STATE.RANGED_ATTACK)
                    player.spriteArray[player.GetState().GetValue()].update(dt);
                bossdragon.spriteArray[bossdragon.GetState().GetValue()].update(dt_l);

                for (int i = 0; i < Gameobject.goList.size(); ++i)
                {
                    Gameobject.goList.get(i).Update(dt);
                }

                // Do attack things here
                if (player.GetAttackState() == Player.PLAYER_STATE.RANGED_ATTACK) {
                    player.DoRangedAttack(dt, this);
                }
                else if (player.GetAttackState() != Player.PLAYER_STATE.IDLE) {
                    player.DoMeleeAttack(this);
                }
                // get key press
                if (player.GetAttackState() == Player.PLAYER_STATE.IDLE || player.GetAttackState() == Player.PLAYER_STATE.JUMP_ATTACK)
                {
                    if (RightButton.isPressed()) {
                        if (player.GetAttackState() != Player.PLAYER_STATE.JUMP_ATTACK)
                            player.SetState(Player.PLAYER_STATE.MOVE);

                        player.MoveRight(deltaTime, map);
                        player.SetFlipSprite(false);
                    } else if (LeftButton.isPressed()) {
                        if (player.GetAttackState() != Player.PLAYER_STATE.JUMP_ATTACK)
                            player.SetState(Player.PLAYER_STATE.MOVE);
                        player.MoveLeft(deltaTime, map);
                        player.SetFlipSprite(true);
                    } else {    // player not pressing a moving button
                        if (player.GetAttackState() == Player.PLAYER_STATE.IDLE) {
                            player.SetState(Player.PLAYER_STATE.IDLE);
                        }
                    }
                }

                if (JumpButton.isPressed()) {
                    player.Jump(map);
                }

                if(SwitchButton.isPressed() && !SwitchButton.hold)
                {
                    SwitchButton.hold = true;
                    AttackButton.active = !AttackButton.active;
                    JumpButton.active = !JumpButton.active;
                    RangedJoyStick.active = !RangedJoyStick.active;
                }else if(!SwitchButton.isPressed() && SwitchButton.hold)
                {
                    SwitchButton.hold = false;
                }

                if(RangedJoyStick.isPressed() && !RangedJoyStick.hold)
                {
                    RangedJoyStick.hold = true;
                    if (player.GetAttackState() == Player.PLAYER_STATE.IDLE)
                        player.SetStartRangedAttack();
                }else if(!RangedJoyStick.isPressed() && RangedJoyStick.hold)
                {
                    //On Release
                    RangedJoyStick.hold = false;
                    player.SetShootArrow(true);
                }

                // update player movement
                player.CheckIsInAir(map);
                if (player.IsInAir()) {
                    player.UpdateJump(deltaTime, map);
                }

                player.FlipSpriteAnimation();


                if (bossdragon.IsDead()) {
                    showAlert = true;
                }

                // Trigger Alert box
                if (!hasShownAlert && showAlert) {
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
                if(vibrator.hasVibrator())
                {
                    vibrator.vibrate(50);
                }
                if (!Buttons[i].isPressed())
                {
                    Buttons[i].SetPressed(true);

                    // Activate unique effects of button type
                    if (Buttons[i].GetName() == "Attack" && player.GetAttackState() == Player.PLAYER_STATE.IDLE)
                    {
                        player.SetStartMeleeAttack();
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
                        player.SetStartMeleeAttack();
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

    // Week 14 Accelerometer - method to implement and use
    public void SensorMove()
    {
        float testX, testY;

        testX = ballCoord.x + (values[1] * ((System.currentTimeMillis() - lastTime) / 1000));
        testY = ballCoord.y + (values[0] * ((System.currentTimeMillis() - lastTime) / 1000));

        // ball is going out of screen in x-axis
        if (testX <= ball.getWidth() / 2 ||
                testX >= Screenwidth - ball.getWidth() / 2)
        {
            // ball is within the screen in y-axis
            if (testY > ball.getHeight() / 2 &&
                    testY < Screenheight - ball.getHeight() / 2) {
                ballCoord.y = testY;
            }
        }

        // ball is out of the screen in the y-axis
        else if (testY <= ball.getHeight() / 2 ||
                testY >= Screenheight - ball.getHeight() / 2)
        {
            // ball is within the screen in the x-axis
            if (testX > ball.getWidth() / 2 &&
                    testX < Screenwidth - ball.getWidth() / 2)
            {
                ballCoord.x = testX;
            }
        }

        else
        {   // move the ship in both axis independent of the frame
            ballCoord.x = testX;
            ballCoord.y = testY;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        values = event.values;
        SensorMove();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
