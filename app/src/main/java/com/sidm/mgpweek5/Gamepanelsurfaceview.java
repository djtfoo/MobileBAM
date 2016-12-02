package com.sidm.mgpweek5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by Foo on 24/11/2016.
 */

public class Gamepanelsurfaceview extends SurfaceView implements SurfaceHolder.Callback {
    // Implement this interface to receive information about changes to the surface.

    private Gamethread myThread = null; // Thread to control the rendering

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

    // 4a) bitmap array to stores 4 images of the spaceship
    private Bitmap[] ship = new Bitmap[4];

    // 4b) Variable as an index to keep track of the spaceship images
    private short shipindex = 0;

    // 2 more variables to place my ship where it will be based on touch of screen
    private short mX = 200, mY = 200;

    // Variables for FPS
    public float FPS;
    float deltaTime;
    long dt;

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

        // 1e)load the image when this class is being instantiated
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.boss1_scene);
        scaledbg = Bitmap.createScaledBitmap(bg, Screenwidth, Screenheight, true);

        // 4c) Load the images of the spaceships
        //ship[0] = BitmapFactory.decodeResource(getResources(), R.drawable.ship2_1);
        //ship[1] = BitmapFactory.decodeResource(getResources(), R.drawable.ship2_2);
        //ship[2] = BitmapFactory.decodeResource(getResources(), R.drawable.ship2_3);
        //ship[3] = BitmapFactory.decodeResource(getResources(), R.drawable.ship2_4);

        ship[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_1),
                (int)(Screenwidth / 10), (int)(Screenheight / 10), true);
        ship[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_2),
                (int)(Screenwidth / 10), (int)(Screenheight / 10), true);
        ship[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_3),
                (int)(Screenwidth / 10), (int)(Screenheight / 10), true);
        ship[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_4),
                (int)(Screenwidth / 10), (int)(Screenheight / 10), true);

        // Load the sprite sheet
        flyincoins = new Spriteanimation(Bitmap.createScaledBitmap
                (BitmapFactory.decodeResource
                        (getResources(), R.drawable.flystar),
                        Screenwidth / 4, Screenheight / 10, true), 320, 64, 5, 5);

        // Create the game loop thread
        myThread = new Gamethread(getHolder(), this);

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);
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

    public void RenderGameplay(Canvas canvas) {
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }

        canvas.drawBitmap(scaledbg, bgX, bgY, null);    // 1st background image
        canvas.drawBitmap(scaledbg, bgX + Screenwidth, bgY, null);    // 2nd background image

        // 4d) Draw the spaceships
        canvas.drawBitmap(ship[shipindex], mX, mY, null);
        // location of the ship is based on touch

        // draw the stars
        flyincoins.draw(canvas);

        flyincoins.setX(coinX);
        flyincoins.setY(coinY);



        // Bonus) To print FPS on the screen
        RenderTextOnScreen(canvas, "FPS " + FPS, 130, 75, 50);

        if (moveship){
            RenderTextOnScreen(canvas, "TRIGGERED", 130, 75, 250);
        }
    }


    //Update method to update the game play
    public void update(float dt, float fps){
        FPS = fps;

        switch (GameState) {
            case 0: {
                // 3) Update the background to allow panning effect
                bgX -= 500 * dt;    // temp value to speed the panning
                if (bgX < -Screenwidth) {
                    bgX = 0;
                }

                // 4e) Update the spaceship images / shipIndex so that the animation will occur.
                ++shipindex;
                shipindex %= 3;

                // make sprite animate
                flyincoins.update(System.currentTimeMillis());
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

        short m_touchX = (short) event.getX();
        short m_touchY = (short) event.getY();

        int action = event.getAction(); // Check for the action of touch

        switch(action) {

            case MotionEvent.ACTION_DOWN:
                // To check finger touch x,y within image, i.e holding down on the image
                if (CheckCollision(
                        mX, mY, ship[shipindex].getWidth() / 2, ship[shipindex].getHeight() / 2,
                        m_touchX, m_touchY, 0, 0))
                    moveship = true;
                else
                    moveship = false;


                break;

            case MotionEvent.ACTION_MOVE:

                if (moveship == true)
                {
                    mX = (short) (m_touchX - ship[shipindex].getWidth() / 2);
                    mY = (short) (m_touchY - ship[shipindex].getHeight() / 2);

                    // Check Collision with coin
                    if (CheckCollision(
                            mX, mY, ship[shipindex].getWidth() / 2, ship[shipindex].getHeight() / 2,
                            coinX, coinY, flyincoins.getSpriteWidth() / 2, flyincoins.getSpriteHeight() / 2))
                    {
                        Random randomNum = new Random();
                        coinX = randomNum.nextInt(Screenwidth);
                        coinY = randomNum.nextInt(Screenheight);
                    }

                }
                break;
        }

        return true;
    }
}
