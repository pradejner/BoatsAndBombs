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
    private float fltAcceleration;

    private Bitmap bitmap;		// the animation sequence
    private Rect sourceRect;	// the rectangle to be drawn from the animation bitmap

    private int spriteWidth;	// the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;	// the height of the sprite

    private float x;				// the X coordinate of the object (top left of the image)
    private float y;				// the Y coordinate of the object (top left of the image)

    private long lastTime;
    private float velocityY;


    public Bomb(Bitmap bitmap, float x, float y, float Acceleration )
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        fltAcceleration = Acceleration;
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


    public Boolean isHit(int intLeft, int intTop, int intRight)
    {
        return !((intTop > (y + bitmap.getHeight())) || ((x + bitmap.getWidth()) < intLeft) || (x > intRight) );
    }

    public void draw(Canvas canvas, long deltaTime)
    {
        velocityY += (fltAcceleration * ( (double)deltaTime / 1000 ) );

        y += velocityY;

        // where to draw the sprite
        Rect destRect = new Rect((int)x, (int)y, (int)x + spriteWidth, (int)y + spriteHeight);

        canvas.drawBitmap(bitmap, sourceRect, destRect, null);

        if (y > (canvas.getHeight() - (bitmap.getHeight() * 2)))
        {
            for (SplashedHandler splashHandler:lstSplashedHandlers){
                splashHandler.callback(this);
            }
        }
    }

}
