package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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

    Paint testPaint = new Paint();
    //Bitmap bg;

    // Week 14 Accelerometer test
    private SensorManager sensor;
    float[] SensorVar = new float[3];
    private float[] values = {0 ,0, 0};

    private Bitmap ball;
    private Vector2 ballCoord = new Vector2();
    private long lastTime = System.currentTimeMillis();

    private Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

    int Screenwidth, Screenheight;
    float unitX;     // 1 unit in 16:9 resolution
    float unitY;     // 1 unit in 16:9 resolution

    GUIbutton BossDragonButton;
    GUIbutton BackButton;

    // Visuals
    Bitmap redPortal, bluePortal;
    Bitmap arch;
    Bitmap homeText;
    Spriteanimation bossdragon;
    Spriteanimation player;

    float rotAngle = 0.f;

    // Activity
    Activity activityTracker;   // use to track and then launch to the desired activity

    public Worldpanelview(Context context, Activity activity) {
        super(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;


        //bg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.worldmap), 1920, 1080, true);
        redPortal = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_red), Screenwidth / 2, Screenheight, true);
        bluePortal = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_blue), Screenwidth / 2, Screenheight, true);
        arch = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_arch), Screenwidth, Screenheight, true);

        homeText = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.portal_home), (int)(Screenwidth * 0.2f), Screenheight / 5, true);
        bossdragon = new Spriteanimation(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dragon_idle), Screenwidth, Screenheight / 4, true), 0, 0, 4, 4);

        player = new Spriteanimation(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.backview_run), Screenwidth / 2, Screenheight / 5, true), 0, 0, 4, 6);

        unitX = Screenwidth / 16;
        unitY = Screenheight / 9;

        // Week 14 Accelerometer
        sensor = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor.registerListener(this, sensor.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_NORMAL);
        ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.testball), Screenwidth / 6, Screenheight / 6, true);

        BackButton = new GUIbutton("Back");
        BossDragonButton = new GUIbutton("BossDragon");

        int buttonSize = (int) (0.7f * unitY * ((float) Screenwidth / (float) Screenheight));

        BackButton.SetButtonSize(buttonSize);
        BossDragonButton.SetButtonSize((int)(1.5f * buttonSize));
        BackButton.SetButtonPos((int) (unitX), (int)(0.15f * unitY));
        BossDragonButton.SetButtonPos((int) (3 * unitX), (int)(5 * unitY));

        BackButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.backbutton), BackButton.GetButtonSize(), BackButton.GetButtonSize(), true));
        BackButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.backbutton_pressed), BackButton.GetButtonSize(), BackButton.GetButtonSize(), true));

        BossDragonButton.SetBitMap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bossdragonbutton), BossDragonButton.GetButtonSize(), BossDragonButton.GetButtonSize(), true));
        BossDragonButton.SetBitMapPressed(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bossdragonbutton_pressed), BossDragonButton.GetButtonSize(), BossDragonButton.GetButtonSize(), true));

        // Activity stuff
        activityTracker = activity;
    }

    @Override
    public void onDraw(Canvas canvas) {

        testPaint.setStrokeWidth(3);
        testPaint.setColor(0xFFFF0055);

        //canvas.drawBitmap(bg, 0, 0, null);

        // render red portal
        canvas.save();
        rotAngle += 5;
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

        // ground
        canvas.drawRect(0, Screenheight, Screenwidth, Screenheight * 0.8f, testPaint);
        //canvas.drawCircle(120, 120, 30, testPaint);

        // home text
        canvas.drawBitmap(homeText, Screenwidth * 0.65f, Screenheight * 0.5f, null);

        // boss dragon
        bossdragon.setX((int)(Screenwidth * 0.27f));
        bossdragon.setY((int)(Screenheight * 0.6f));
        bossdragon.draw(canvas);

        // arch
        canvas.drawBitmap(arch, 0, 0, null);

        // player
        player.setX((int)(Screenwidth * 0.5f));
        player.setY((int)(Screenheight * 0.8f));
        player.draw(canvas);

        // Week 14 Accelerometer test ball
        canvas.drawBitmap(ball, ballCoord.x, ballCoord.y, null);

        // Buttons
        //if (BackButton.isPressed())
        //    canvas.drawBitmap(BackButton.GetBitMapPressed(), (int) (BackButton.GetPosition().x), (int) (BackButton.GetPosition().y), null);
        //else
        //    canvas.drawBitmap(BackButton.GetBitMap(), (int) (BackButton.GetPosition().x), (int) (BackButton.GetPosition().y), null);
//
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

                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    if (!BackButton.isPressed())
                    {
                        BackButton.SetPressed(true);

                        // Activate unique effects of button type
                        GoToMainMenu();
                    }
                }
                else if (dragonButtonPos.Subtract(touchPos).GetLength() < BackButton.GetButtonSize() / 2) {

                    if(vibrator.hasVibrator())
                    {
                        vibrator.vibrate(50);
                    }
                    if (!BossDragonButton.isPressed())
                    {
                        BossDragonButton.SetPressed(true);

                        // Activate unique effects of button type
                        GoToGamePage();
                    }
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
        float testX, testY;

        testX = ballCoord.x + (values[1] * ((System.currentTimeMillis() - lastTime) / 1000));
        testY = ballCoord.y + (values[0] * ((System.currentTimeMillis() - lastTime) / 1000));

        //// ball is going out of screen in x-axis
        //if (testX <= ball.getWidth() / 2 ||
        //        testX >= Screenwidth - ball.getWidth() / 2)
        //{
        //    // ball is within the screen in y-axis
        //    if (testY > ball.getHeight() / 2 &&
        //            testY < Screenheight - ball.getHeight() / 2) {
        //        ballCoord.y = testY;
        //    }
        //}
//
        //// ball is out of the screen in the y-axis
        //else if (testY <= ball.getHeight() / 2 ||
        //        testY >= Screenheight - ball.getHeight() / 2)
        //{
        //    // ball is within the screen in the x-axis
        //    if (testX > ball.getWidth() / 2 &&
        //            testX < Screenwidth - ball.getWidth() / 2)
        //    {
        //        ballCoord.x = testX;
        //    }
        //}

        //else
        //{   // move the ship in both axis independent of the frame
            ballCoord.x = testX;
            ballCoord.y = testY;
        //}
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
