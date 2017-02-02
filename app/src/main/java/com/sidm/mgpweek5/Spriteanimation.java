package com.sidm.mgpweek5;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Created by Foo on 1/12/2016.
 */

public class Spriteanimation {

    private Bitmap bitmap; // the animation sequence
    private Bitmap flippedBitmap;   // reflected version of Bitmap
    private Rect sourceRect; // the rectangle to be drawn from the animation bitmap
    private int frame; // number of frames in animation
    private int currentFrame; // the current frame
    private long frameTicker; // the time of the last frame update
    private int framePeriod; // milliseconds between each frame (1000/fps)

    private int spriteWidth; // the width of the sprite to calculate the cut out rectangle
    private int spriteHeight; // the height of the sprite

    private boolean flipSprites;    // reflect the sprites

    private float frameTimerFloat;  // the timer between each frame
    private float framePeriodFloat; // frame period in seconds

    private int x; // the X coordinate of the object (top left of the image)
    private int y; // the Y coordinate of the object (top left of the image)

    public Spriteanimation(Bitmap bitmap, int x, int y, int fps, int frameCount) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        currentFrame = 0;
        frame = frameCount;
        spriteWidth = bitmap.getWidth() / frameCount;
        spriteHeight = bitmap.getHeight();

        flipSprites = false;

        sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
        framePeriod = 1000 / fps;
        frameTicker = 01;

        frameTimerFloat = 0.f;
        framePeriodFloat = (float)framePeriod / 1000;

        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                spriteWidth * frame, spriteHeight, matrix, false);

    }

    public Bitmap GetFlipVertical()
    {
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        return flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                spriteWidth, spriteHeight, matrix, false);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Rect getSourceRect() {
        return sourceRect;
    }

    public void setSourceRect(Rect sourceRect) {
        this.sourceRect = sourceRect;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void resetAnimation()
    {
        setCurrentFrame(0);
        frameTimerFloat = 0.f;
    };

    public int getFramePeriod() {
        return framePeriod;
    }

    public void setFramePeriod(int framePeriod) {
        this.framePeriod = framePeriod;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public void setSpriteWidth(int spriteWidth) {
        this.spriteWidth = spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public void setSpriteHeight(int spriteHeight) {
        this.spriteHeight = spriteHeight;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x - spriteWidth / 2;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y - spriteHeight / 2;
    }

    // using frameTicker
    public void update(long gameTime) {
        if (gameTime > frameTicker + framePeriod) {
            frameTicker = gameTime;
            // increment the frame
            currentFrame++;
            if (currentFrame >= frame) {
                currentFrame = 0;
            }
        }
        // define the rectangle to cut out sprite
        this.sourceRect.left = currentFrame * spriteWidth;
        this.sourceRect.right = this.sourceRect.left + spriteWidth;
    }

    // using spriteTimer
    public void update(float dt) {
        frameTimerFloat += dt;
        if (frameTimerFloat > framePeriodFloat) {
            frameTimerFloat = 0.f;
            // increment the frame
            currentFrame++;
            if (currentFrame >= frame) {
                currentFrame = 0;
            }
        }
    }

    public void draw(Canvas canvas) {
        // where to draw the sprite
        Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
        if (flipSprites)
        {
            // define the rectangle to cut out sprite
            int flippedFrame = (frame - currentFrame);
            this.sourceRect.left = (flippedFrame - 1) * spriteWidth;
            this.sourceRect.right = this.sourceRect.left + spriteWidth;

            canvas.drawBitmap(flippedBitmap, sourceRect, destRect, null);
        }
        else
        {
            // define the rectangle to cut out sprite
            this.sourceRect.left = currentFrame * spriteWidth;
            this.sourceRect.right = this.sourceRect.left + spriteWidth;

            canvas.drawBitmap(bitmap, sourceRect, destRect, null);
            //destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
        }

        //canvas.drawBitmap(bitmap, sourceRect, destRect, null);
    }

    public void recycleAnim(){
        bitmap.recycle();
    }

    public boolean GetFlipSprites() { return flipSprites; }
    public void SetFlipSprites(boolean flip) { flipSprites = flip; }
    public void SetFlipSprites(Bitmap flippedSprites) { flippedBitmap = flippedSprites; }

}