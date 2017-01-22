package com.peakey.ggj2017.waveyboat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * @author rhankins
 * @ate 1/20/2017.
 */

public class Bomb
{

    public interface SplashedHandler{
        void callback(Bomb bomb);
    }


    private ArrayList<SplashedHandler> lstSplashedHandlers;

    private static final String TAG = Bomb.class.getSimpleName();
    private static final float GRAVITY_ACCELERATION = 9.8f; //M/S^2

    private Bitmap bitmap;		// the animation sequence
    private Rect sourceRect;	// the rectangle to be drawn from the animation bitmap

    private int spriteWidth;	// the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;	// the height of the sprite

    private float x;				// the X coordinate of the object (top left of the image)
    private float y;				// the Y coordinate of the object (top left of the image)

    private long lastTime;
    private float velocityY;


    public Bomb(Bitmap bitmap, float x, float y )
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        spriteWidth = bitmap.getWidth();
        spriteHeight = bitmap.getHeight();
        sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
        lastTime = System.currentTimeMillis();
        velocityY = 0;
        lstSplashedHandlers = new ArrayList<SplashedHandler>();
    }

    public void addSplashedHandler(SplashedHandler observer){
        lstSplashedHandlers.add(observer);
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

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    public float getVelocityY() { return velocityY; }


    public void draw(Canvas canvas, long deltaTime)
    {
        velocityY += (GRAVITY_ACCELERATION * ( (double)deltaTime / 1000 ) );

        //assume bombs are 1 meter
        y += velocityY;

        // where to draw the sprite
        Rect destRect = new Rect((int)x, (int)y, (int)x + spriteWidth, (int)y + spriteHeight);

        canvas.drawBitmap(bitmap, sourceRect, destRect, null);

        if (y > (canvas.getHeight() - (bitmap.getHeight() * 3)))
        {
            for (SplashedHandler splashHandler:lstSplashedHandlers){
                splashHandler.callback(this);
            }
        }
    }

}
