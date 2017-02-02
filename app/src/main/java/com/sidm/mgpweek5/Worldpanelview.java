package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Foo on 30/1/2017.
 */

public class Worldpanelview extends View implements SensorEventListener {

    float dt;

    // Week 14 Accelerometer test
    private SensorManager sensor;
    float[] SensorVar = new float[3];
    private float[] values = {0 ,0, 0};

    private long lastTime = System.currentTimeMillis();

    private Vibratormanager vibrator;

    int Screenwidth, Screenheight;
    float unitX;     // 1 unit in 16:9 resolution
    float unitY;     // 1 unit in 16:9 resolution

    // Fonts
    Typeface font;
    int textSize;

    GUIbutton BossDragonButton;
    GUIbutton BackButton;

    // Visuals
    Bitmap redPortal, bluePortal;
    Bitmap arch, ground;
    Bitmap homeText;
    Spriteanimation bossdragon;
    Spriteanimation player;
    Vector2 playerPos = new Vector2();

    float rotAngle = 0.f;

    // Activity
    Activity activityTracker;   // use to track and then launch to the desired activity

    public Worldpanelview(Context context, Activity activity) {
        super(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;

        vibrator = new Vibratormanager(context);

        //bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.worldmap), 1920, 1080, true);
        redPortal = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_red), Screenwidth / 2, Screenheight, true);
        bluePortal = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_blue), Screenwidth / 2, Screenheight, true);
        arch = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_arch), Screenwidth, Screenheight, true);
        ground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_ground), Screenwidth, (int)(Screenheight * 0.1375f), true);

        homeText = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_home), (int)(Screenwidth * 0.2f), Screenheight / 5, true);
        bossdragon = new Spriteanimation(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dragon_idle), Screenwidth, Screenheight / 4, true), 0, 0, 4, 4);

        player = new Spriteanimation(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.backview_run), Screenwidth / 2, Screenheight / 5, true), 0, 0, 4, 6);

        unitX = Screenwidth / 16;
        unitY = Screenheight / 9;

        // Week 14 Accelerometer
        sensor = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor.registerListener(this, sensor.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_NORMAL);

        playerPos.Set(Screenwidth * 0.5f, Screenheight * 0.85f);

        playerPos.Set(Screenwidth * 0.5f, Screenheight * 0.8f);

        BackButton = new GUIbutton("Back");
        BossDragonButton = new GUIbutton("BossDragon");

        //int buttonSize = (int) (0.7f * unitY * ((float) Screenwidth / (float) Screenheight));
        int buttonSize = (int) (3 * unitY * ((float) Screenwidth / (float) Screenheight));

        BackButton.SetButtonSize(buttonSize);
        BossDragonButton.SetButtonSize(buttonSize);
        BackButton.SetButtonPos((int) (Screenwidth * 0.59f), (int)(Screenheight * 0.35f));
        BossDragonButton.SetButtonPos((int) (Screenwidth * 0.09f), (int)(Screenheight * 0.35f));

        BackButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.backbutton), BackButton.GetButtonSize(), BackButton.GetButtonSize(), true));
        BackButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.backbutton_pressed), BackButton.GetButtonSize(), BackButton.GetButtonSize(), true));

        BossDragonButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bossdragonbutton), BossDragonButton.GetButtonSize(), BossDragonButton.GetButtonSize(), true));
        BossDragonButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bossdragonbutton_pressed), BossDragonButton.GetButtonSize(), BossDragonButton.GetButtonSize(), true));

        // Activity stuff
        activityTracker = activity;

        // Font
        font = Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf");
        textSize = (int) (0.3 * unitX * ((float) Screenwidth / (float) Screenheight));
    }

    public void RenderTextOnScreen(Canvas canvas, String text, int posX, int posY, int textsize) {

        Paint paint = new Paint();

        paint.setARGB(255, 0, 0, 0);
        paint.setStrokeWidth(100);
        paint.setTextSize(textsize);
        paint.setTypeface(font);

        canvas.drawText(text, posX, posY, paint);
    }

    @Override
    public void onDraw(Canvas canvas) {

        // update values
        long currentTime = System.currentTimeMillis();
        dt = (currentTime - lastTime) / 1000.f;
        lastTime = currentTime;


        player.update(dt * 2);
        bossdragon.update(dt);
        rotAngle += 140 * dt;
        if (rotAngle > 360.f)
            rotAngle -= 360.f;

        //canvas.drawBitmap(bg, 0, 0, null);

        // render red portal
        canvas.save();
        canvas.translate(Screenwidth * 0.25f, Screenheight * 0.6f);
        canvas.rotate(rotAngle);
        canvas.translate(-Screenwidth * 0.25f, -Screenheight * 0.6f);
        canvas.drawBitmap(redPortal, 0, Screenheight * 0.1f, null);
        canvas.restore();

        canvas.save();
        canvas.translate(Screenwidth * 0.75f, Screenheight * 0.6f);
        canvas.rotate(90 + rotAngle);
        canvas.translate(-Screenwidth * 0.75f, -Screenheight * 0.6f);
        canvas.drawBitmap(bluePortal, Screenwidth * 0.5f, Screenheight * 0.1f, null);
        canvas.restore();

        // home text
        canvas.drawBitmap(homeText, Screenwidth * 0.65f, Screenheight * 0.5f, null);

        // boss dragon
        bossdragon.setX((int)(Screenwidth * 0.27f));
        bossdragon.setY((int)(Screenheight * 0.6f));
        bossdragon.draw(canvas);

        // arch
        canvas.drawBitmap(arch, 0, 0, null);

        // ground
        canvas.drawBitmap(ground, 0, Screenheight * 0.8625f, null);
        //canvas.drawCircle(120, 120, 30, testPaint);

        // player
        player.setX((int)playerPos.x);
        player.setY((int)playerPos.y);
        player.draw(canvas);

        // if player is at a portal, render guiding text
        if (playerPos.x < 0.375f * Screenwidth|| playerPos.x > 0.625f * Screenwidth)
        {
            RenderTextOnScreen(canvas, "Tap to Enter Portal", (int)(Screenwidth * 0.35f), (int) (Screenheight * 0.32f), textSize);
        }

        // Buttons
        //if (BackButton.isPressed())
        //    canvas.drawBitmap(BackButton.GetBitMapPressed(), (int) (BackButton.GetPosition().x), (int) (BackButton.GetPosition().y), null);
        //else
        //    canvas.drawBitmap(BackButton.GetBitMap(), (int) (BackButton.GetPosition().x), (int) (BackButton.GetPosition().y), null);

        //if (BossDragonButton.isPressed())
        //    canvas.drawBitmap(BossDragonButton.GetBitMapPressed(), (int) (BossDragonButton.GetPosition().x), (int) (BossDragonButton.GetPosition().y), null);
        //else
        //    canvas.drawBitmap(BossDragonButton.GetBitMap(), (int) (BossDragonButton.GetPosition().x), (int) (BossDragonButton.GetPosition().y), null);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        //Log.v("TEST", Integer.toString(event.getPointerCount()));
        switch(action & event.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {

                Vector2 touchPos = new Vector2(event.getX(), event.getY());

                Vector2 backButtonPos = new Vector2(BackButton.GetPosition());
                backButtonPos.x += BackButton.GetButtonSize() / 2;
                backButtonPos.y += BackButton.GetButtonSize() / 2;

                Vector2 dragonButtonPos = new Vector2(BossDragonButton.GetPosition());
                dragonButtonPos.x += BossDragonButton.GetButtonSize() / 2;
                dragonButtonPos.y += BossDragonButton.GetButtonSize() / 2;

                if(backButtonPos.Subtract(touchPos).GetLength() < BackButton.GetButtonSize() / 2) {

                    vibrator.Vibrate(50);
                    // Activate unique effects of button type
                    if (playerPos.x > 0.625f * Screenwidth /*&& playerPos.x < 0.875f * Screenwidth*/)
                        GoToMainMenu();
                }
                else if (dragonButtonPos.Subtract(touchPos).GetLength() < BackButton.GetButtonSize() / 2) {

                    vibrator.Vibrate(50);
                    // Activate unique effects of button type
                    if (/*playerPos.x > 0.125f * Screenwidth &&*/ playerPos.x < 0.375f * Screenwidth)
                        GoToGamePage();
                }

            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                //ProcessInputUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                //ProcessInputMove(event);
                break;
        }
        return true;
    }

    private void GoToGamePage()
    {
        // Release the memory
        sensor.unregisterListener(this);

        // Change the page
        Intent intent = new Intent();
        intent.setClass(getContext(), Gamepage.class);
        activityTracker.startActivity(intent);
    }

    private void GoToMainMenu()
    {
        // Release the memory
        sensor.unregisterListener(this);

        // Change the page
        Intent intent = new Intent();
        intent.setClass(getContext(), Mainmenu.class);
        activityTracker.startActivity(intent);
    }

    // Week 14 Accelerometer - method to implement and use
    public void SensorMove()
    {
        //float newX = playerPos.x + (values[1] * dt);

        float velX = 200.f * values[1];
        float newX = playerPos.x + (velX * dt);

        if (newX >= Screenwidth * 0.125f + player.getSpriteWidth() / 2 && newX <= Screenwidth * 0.875f - player.getSpriteWidth() / 2)
        {
            playerPos.x = newX;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        values = event.values;
        SensorMove();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do something here if sensor accuracy changes
    }

}
