package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Foo on 3/2/2017.
 */

public class Helppanelview extends View {

    private Vibratormanager vibrator;
    private Soundmanager soundManager;

    private int helpState = 0;
    private boolean b_ChangeState = false;

    public int Screenwidth, Screenheight;

    private Bitmap bg;

    // Fonts
    Typeface font;
    int textSize;

    private float dt;
    private long lastTime = System.currentTimeMillis();

    // Activity
    Activity activityTracker;   // use to track and then launch to the desired activity

    // Buttons
    GUIbutton BackButton;
    GUIbutton NextPageButton;
    GUIbutton PrevPageButton;

    // HELP PAGE 1 VARIABLES
    Spriteanimation bossdragon;
    Bitmap missile;
    Bitmap shieldgenerator;

    // HELP PAGE 2 VARIABLES
    private Bitmap tile_ground;
    Tilemap map = new Tilemap();

    public Vector<Gameobject> golist = new Vector<Gameobject>();
    public Bitmap arrowProjectileBitmap;

    private final int ButtonCount = 8;
    private GUIbutton Buttons[];
    GUIbutton RightButton;
    GUIbutton LeftButton;
    GUIbutton JumpButton;
    GUIbutton AttackButton;
    GUIbutton SwitchButton;
    JoyStick RangedJoyStick;

    public Helppanelview(Context context, Activity activity) {
        super(context);

        activityTracker = activity;
        vibrator = new Vibratormanager(context);
        soundManager = new Soundmanager(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;

        // Tilemap for page 2
        map.Init(context, "HelpMap.csv");
        map.tileSize_X = Screenwidth / map.GetCols();
        map.tileSize_Y = Screenheight / map.GetRows();

        bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.menu), Screenwidth, Screenheight, true);
        arrowProjectileBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.projectile_turret),
                (int) (0.5f * map.tileSize_X), (int) (0.5f * map.tileSize_X), true);

        tile_ground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tile_ground),
                (int) (map.tileSize_X), (int) (map.tileSize_Y), true);

        // Font
        font = Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf");
        textSize = (int) (0.3 * map.tileSize_X * ((float) Screenwidth / (float) Screenheight));

        // Buttons
        InitButtons();

        Player.instance.Init(context, map, Screenwidth, Screenheight);
        Player.instance.SetPosition((map.GetCols() * 0.5f) * map.tileSize_X, (map.GetRows() - 3) * map.tileSize_Y);

        bossdragon = new Spriteanimation(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dragon_idle), Screenwidth, Screenheight / 4, true), 0, 0, 4, 4);
        bossdragon.setX((int)(3.f * map.tileSize_X));
        bossdragon.setY((int)(5.f * map.tileSize_Y));
        missile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.missile), Screenheight / 4, Screenheight / 4, true);
        shieldgenerator = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tower_shieldgenerator), Screenwidth / 4, Screenheight / 4, true);
    }

    public void InitButtons() {
        RightButton = new GUIbutton("Right");
        LeftButton = new GUIbutton("Left");
        AttackButton = new GUIbutton("Attack");
        JumpButton = new GUIbutton("Jump");
        SwitchButton = new GUIbutton("Switch");
        RangedJoyStick = new JoyStick("Ranged");

        BackButton = new GUIbutton("Back");
        NextPageButton = new GUIbutton("Next");
        PrevPageButton = new GUIbutton("Prev");

        int buttonSize = (int) (0.7f * map.tileSize_Y * ((float) Screenwidth / (float) Screenheight));

        RightButton.SetButtonSize(buttonSize);
        LeftButton.SetButtonSize(buttonSize);
        JumpButton.SetButtonSize(buttonSize);
        SwitchButton.SetButtonSize(buttonSize);
        AttackButton.SetButtonSize((int) (buttonSize * 1.45));
        RangedJoyStick.SetButtonSize((int) (buttonSize * 1.45));

        LeftButton.SetButtonPos((int) (map.tileSize_X), (int) (7.8f * map.tileSize_Y));
        RightButton.SetButtonPos((int) map.tileSize_X + (int) (Screenwidth * 0.02) + buttonSize, (int) (7.8f * map.tileSize_Y));
        JumpButton.SetButtonPos((int) ((16 - 3.5f) * map.tileSize_X), (int) (7.8f * map.tileSize_Y));
        AttackButton.SetButtonPos((int) ((16 - 2) * map.tileSize_X), (int) (7.3f * map.tileSize_Y));
        SwitchButton.SetButtonPos((int) (0.3f * map.tileSize_X), (int) (5.f * map.tileSize_Y));
        RangedJoyStick.SetButtonPos((int) ((16 - 2) * map.tileSize_X), (int) (7.f * map.tileSize_Y));

        BackButton.SetButtonSize(buttonSize);
        BackButton.SetButtonPos((int) map.tileSize_X, (int) (0.5f * map.tileSize_Y));
        NextPageButton.SetButtonSize(buttonSize);
        NextPageButton.SetButtonPos((int) (map.tileSize_X * 14.f), (int) (0.5f * map.tileSize_Y));
        PrevPageButton.SetButtonSize(buttonSize);
        PrevPageButton.SetButtonPos((int) (map.tileSize_X * 14.f) - (int)(1.2f * buttonSize), (int) (0.5f * map.tileSize_Y));

        LeftButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true));
        RightButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true));
        JumpButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true));
        AttackButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true));
        SwitchButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.switchbutton), SwitchButton.GetButtonSize(), SwitchButton.GetButtonSize(), true));
        RangedJoyStick.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.joy_bg), RangedJoyStick.GetButtonSize(), RangedJoyStick.GetButtonSize(), true));

        LeftButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton_pressed), LeftButton.GetButtonSize(), LeftButton.GetButtonSize(), true));
        RightButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton_pressed), RightButton.GetButtonSize(), RightButton.GetButtonSize(), true));
        JumpButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jumpbutton_pressed), JumpButton.GetButtonSize(), JumpButton.GetButtonSize(), true));
        AttackButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attackbutton_pressed), AttackButton.GetButtonSize(), AttackButton.GetButtonSize(), true));
        SwitchButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.switchbutton_pressed), SwitchButton.GetButtonSize(), SwitchButton.GetButtonSize(), true));
        RangedJoyStick.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.joy_bg_pressed), RangedJoyStick.GetButtonSize(), RangedJoyStick.GetButtonSize(), true));
        RangedJoyStick.bmFG = (Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.joy_fg), RangedJoyStick.GetButtonSize(), RangedJoyStick.GetButtonSize(), true));

        BackButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.backbutton), BackButton.GetButtonSize(), BackButton.GetButtonSize(), true));
        BackButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.backbutton_pressed), BackButton.GetButtonSize(), BackButton.GetButtonSize(), true));

        NextPageButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton), NextPageButton.GetButtonSize(), NextPageButton.GetButtonSize(), true));
        NextPageButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbutton_pressed), NextPageButton.GetButtonSize(), NextPageButton.GetButtonSize(), true));

        PrevPageButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton), PrevPageButton.GetButtonSize(), PrevPageButton.GetButtonSize(), true));
        PrevPageButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbutton_pressed), PrevPageButton.GetButtonSize(), PrevPageButton.GetButtonSize(), true));

        Buttons = new GUIbutton[ButtonCount];
        Buttons[0] = LeftButton;
        Buttons[1] = RightButton;
        Buttons[2] = JumpButton;
        Buttons[3] = AttackButton;
        Buttons[4] = SwitchButton;
        Buttons[5] = BackButton;
        Buttons[6] = NextPageButton;
        Buttons[7] = PrevPageButton;

        RangedJoyStick.active = false;

        // set player controls to false
        for (int i = 0; i < 5; ++i)
        {
            Buttons[i].active = false;
        }
        Buttons[7].active = false;  // set PrevPageButton to false
    }

    @Override
    public void onDraw(Canvas canvas) {

        //float FPS = 1 / dt;

        if (b_ChangeState) {
            ChangeState();
        }

        Update();

        // background
        canvas.drawBitmap(bg, 0, 0, null);

        String header = "";
        switch (helpState)
        {
            case 0:
                // Header
                header = "INSTRUCTIONS";
                RenderTextOnScreen(canvas, header, (int)(0.5f * Screenwidth - header.length() * textSize * 0.8f), (int)(textSize * 4.f), (int)(textSize * 2.5f), 255, 2, 0, 79);

                // boss dragon
                bossdragon.draw(canvas);

                // missile
                canvas.drawBitmap(missile, 7.f * map.tileSize_X, 4.f * map.tileSize_Y, null);

                // shield generator
                canvas.drawBitmap(shieldgenerator, 11.f * map.tileSize_X, 4.f * map.tileSize_Y, null);

                // Text
                RenderTextOnScreen(canvas, "Reduce the boss HP to 0", (int)(0.5f * map.tileSize_X), (int)(6.8f * map.tileSize_Y), textSize, 255, 255, 255, 255);
                RenderTextOnScreen(canvas, "Avoid or destroy", (int)(6.5f * map.tileSize_X), (int)(6.8f * map.tileSize_Y), textSize, 255, 255, 255, 255);
                RenderTextOnScreen(canvas, "missiles", (int)(6.6f * map.tileSize_X), (int)(6.8f * map.tileSize_Y + textSize), textSize, 255, 255, 255, 255);
                RenderTextOnScreen(canvas, "Shield generators prevent", (int)(10.4f * map.tileSize_X), (int)(6.8f * map.tileSize_Y), textSize, 255, 255, 255, 255);
                RenderTextOnScreen(canvas, "damage, destroy them", (int)(10.5f * map.tileSize_X), (int)(6.8f * map.tileSize_Y + textSize), textSize, 255, 255, 255, 255);

                // Buttons
                DrawButtons(canvas);
                canvas.drawBitmap(PrevPageButton.GetBitMapPressed(), (int) (PrevPageButton.GetPosition().x), (int) (PrevPageButton.GetPosition().y), null);

                break;

            case 1:
                DrawTilemap(canvas);
                DrawPlayer(canvas);

                for(int i = 0; i < golist.size(); ++i)
                {
                    Gameobject go = golist.get(i);
                    DrawGameobject(canvas, go);
                }

                // Header
                header = "CONTROLS";
                RenderTextOnScreen(canvas, header, (int)(0.5f * Screenwidth - header.length() * textSize * 0.8f), (int)(2.f * map.tileSize_Y), (int)(map.tileSize_Y), 255, 2, 0, 79);

                // Buttons
                DrawButtons(canvas);
                canvas.drawBitmap(NextPageButton.GetBitMapPressed(), (int) (NextPageButton.GetPosition().x), (int) (NextPageButton.GetPosition().y), null);

                //RenderTextOnScreen(canvas, "FPS " + FPS, (int) (1.5f * map.tileSize_X), (int) (0.4f * map.tileSize_Y), textSize);

                break;
        }

        invalidate();
    }

    private void Update()
    {
        long currentTime = System.currentTimeMillis();
        dt = (currentTime - lastTime) / 1000.f;
        lastTime = currentTime;

        ButtonUpdate(dt);

        switch (helpState)
        {
            case 0:
                bossdragon.update(dt);
                break;

            case 1:
                PlayerUpdate(dt);

                for (int i = 0; i < golist.size(); ++i)
                {
                    golist.get(i).Update(dt);
                }

                Iterator<Gameobject> goItr = golist.iterator();

                while(goItr.hasNext())
                {
                    Gameobject temp = goItr.next();
                    if(temp.toBeDestroyed)
                        goItr.remove();
                }
                break;
        }
    }

    // Update functions
    private void ButtonUpdate(float dt)
    {
        // Ranged Joystick
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

        // Switch Button
        if(SwitchButton.isPressed() && !SwitchButton.hold)
        {
            SwitchButton.hold = true;
            AttackButton.active = !AttackButton.active;
            if (AttackButton.isPressed())
                AttackButton.SetPressed(false);
            RangedJoyStick.active = !RangedJoyStick.active;
            if (RangedJoyStick.hold) {
                // force release
                RangedJoyStick.SetPressed(false);
                RangedJoyStick.hold = false;
                RangedJoyStick.PointerIndex = -1;
                Player.instance.SetShootArrow(true);
            }
        }else if(!SwitchButton.isPressed() && SwitchButton.hold)
        {
            SwitchButton.hold = false;
        }

        // Back Button
        if(BackButton.isPressed() && !BackButton.hold)
        {
            BackButton.hold = true;
            GoToMainMenu();
        }
    }

    private void PlayerUpdate(float dt)
    {
        if (Player.instance.GetAttackState() != Player.PLAYER_STATE.RANGED_ATTACK)
            Player.instance.spriteArray[Player.instance.GetState().GetValue()].update(dt);

        if (Player.instance.GetAttackState() == Player.PLAYER_STATE.RANGED_ATTACK) {
            Player.instance.DoRangedAttackAnimation(dt, this, soundManager);
        }
        else if (Player.instance.GetAttackState() != Player.PLAYER_STATE.IDLE) {
            Player.instance.DoMeleeAttackAnimation(this, soundManager);
        }
        // get key press
        if (Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE || Player.instance.GetAttackState() == Player.PLAYER_STATE.JUMP_ATTACK)
        {
            if (RightButton.isPressed()) {
                if (Player.instance.GetAttackState() != Player.PLAYER_STATE.JUMP_ATTACK)
                    Player.instance.SetState(Player.PLAYER_STATE.MOVE);

                Player.instance.MoveRight(dt, map);
                Player.instance.SetFlipSprite(false);
            } else if (LeftButton.isPressed()) {
                if (Player.instance.GetAttackState() != Player.PLAYER_STATE.JUMP_ATTACK)
                    Player.instance.SetState(Player.PLAYER_STATE.MOVE);
                Player.instance.MoveLeft(dt, map);
                Player.instance.SetFlipSprite(true);
            } else {    // player not pressing a moving button
                if (Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE) {
                    Player.instance.SetState(Player.PLAYER_STATE.IDLE);
                }
            }
        }

        if (JumpButton.isPressed()) {
            Player.instance.Jump(map, soundManager);
        }

        // update player movement
        Player.instance.CheckIsInAir(map);
        if (Player.instance.IsInAir()) {
            Player.instance.UpdateJump(dt, map);
        }

        Player.instance.FlipSpriteAnimation();
    }

    // Draw functions
    public void RenderTextOnScreen(Canvas canvas, String text, int posX, int posY, int textsize, int a, int r, int g, int b) {

        Paint paint = new Paint();

        paint.setARGB(a, r, g, b);
        paint.setStrokeWidth(100);
        paint.setTextSize(textsize);
        paint.setTypeface(font);

        canvas.drawText(text, posX, posY, paint);
    }

    private void DrawPlayer(Canvas canvas) {
        // player sprite
        Player.instance.spriteArray[Player.instance.GetState().GetValue()].setX((int) Player.instance.GetPosition().x);
        Player.instance.spriteArray[Player.instance.GetState().GetValue()].setY((int) Player.instance.GetPosition().y);
        Player.instance.spriteArray[Player.instance.GetState().GetValue()].draw(canvas);
    }

    private void DrawTilemap(Canvas canvas) {
        for (int y = 0; y < map.GetRows(); ++y) {
            for (int x = 0; x < map.GetCols(); ++x) {
                if (map.tilemap[y][x] == 1) {
                    canvas.drawBitmap(tile_ground, x * map.tileSize_X, y * map.tileSize_Y, null);
                }
            }
        }
    }

    private void DrawGameobject(Canvas canvas, Gameobject go)
    {
        go.spriteArray[0].setX((int) go.GetPosition().x);
        go.spriteArray[0].setY((int) go.GetPosition().y);
        go.spriteArray[0].draw(canvas);
    }

    private void DrawButtons(Canvas canvas) {

        for(int i = 0; i < ButtonCount; i++)
        {
            if(!Buttons[i].active)
                continue;

            if (Buttons[i].isPressed())
                canvas.drawBitmap(Buttons[i].GetBitMapPressed(), (int) (Buttons[i].GetPosition().x), (int) (Buttons[i].GetPosition().y), null);
            else
                canvas.drawBitmap(Buttons[i].GetBitMap(), (int) (Buttons[i].GetPosition().x), (int) (Buttons[i].GetPosition().y), null);
        }

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
                RangedJoyStick.SetTouchPos(CurrentTouchPos);
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
                    if (Buttons[i].GetName() == "Attack" && Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE) {
                        Player.instance.SetStartMeleeAttack();
                    }
                    else if (Buttons[i].GetName() == "Next" || Buttons[i].GetName() == "Prev")
                    {
                        b_ChangeState = true;
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
                RangedJoyStick.SetTouchPos(CurrentTouchPos);
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
                    if (Buttons[i].GetName() == "Attack" && Player.instance.GetAttackState() == Player.PLAYER_STATE.IDLE)
                    {
                        Player.instance.SetStartMeleeAttack();
                    }
                    else if (Buttons[i].GetName() == "Next" || Buttons[i].GetName() == "Prev")
                    {
                        b_ChangeState = true;
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

        if (helpState == 0)
        {
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
        }

        else if (helpState == 1)
        {
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
        }

        return true;
    }

    private void GoToMainMenu()
    {
        // Change the page
        Intent intent = new Intent();
        intent.setClass(getContext(), Mainmenu.class);
        activityTracker.startActivity(intent);
    }

    private void ChangeState()
    {
        if (helpState == 0) {
            helpState = 1;  // change to page 2

            // set player controls to active
            for (int count = 0; count < 5; ++count)
            {
                Buttons[count].active = true;
            }

            Buttons[6].active = false;  // set NextPageButton to inactive
            Buttons[6].PointerIndex = -1;
            Buttons[6].SetPressed(false);
            Buttons[7].active = true;   // set PrevPageButton to active
        }
        else if (helpState == 1) {
            helpState = 0;  // change to page 1

            // set player controls to inactive
            for (int count = 0; count < 5; ++count)
            {
                Buttons[count].active = false;
                Buttons[count].PointerIndex = -1;
                Buttons[count].SetPressed(false);
            }

            Buttons[6].active = true;  // set NextPageButton to active
            Buttons[7].active = false;   // set PrevPageButton to inactive
        }

        b_ChangeState = false;
    }

}
