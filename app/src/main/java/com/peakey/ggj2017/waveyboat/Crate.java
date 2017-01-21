package com.peakey.ggj2017.waveyboat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by peakeyAdmin on 1/20/2017.
 */

public class Crate
{


    private static final String TAG = Crate.class.getSimpleName();
    private static final float GRAVITY = 9.8f;

    private Bitmap bitmap;		// the animation sequence
    private Rect sourceRect;	// the rectangle to be drawn from the animation bitmap

    private int spriteWidth;	// the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;	// the height of the sprite

    private int x;				// the X coordinate of the object (top left of the image)
    private int y;				// the Y coordinate of the object (top left of the image)
    private float fltCoefficientOfFriction;
    private float fltFriction;
    private float fltMass;
    private float fltAngle;
    private long lastTime;
    private long velocityX;
    private long velocityY;


    public Crate(Bitmap bitmap, int x, int y, float Mass, float CoefficientOfFriction, float Friction, float Angle)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        spriteWidth = bitmap.getWidth();
        spriteHeight = bitmap.getHeight();
        sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
        fltMass = Mass;
        fltCoefficientOfFriction = CoefficientOfFriction;
        fltFriction = Friction;
        fltAngle = Angle;
        lastTime = System.currentTimeMillis();
        velocityY = 0;
        velocityX = 0;
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

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }


    public float getAngle()
    {
        return fltAngle;
    }
    public void setAngle(float angle)
    {
        fltAngle = angle;
    }

    public float getVelocityX()
    {
        return velocityX;
    }

    public float getVelocityY()
    {
        return velocityY;
    }

    public void update()
    {

        // define the rectangle to cut out sprite
        this.sourceRect.left = 1 * spriteWidth;
        this.sourceRect.right = this.sourceRect.left + spriteWidth;
    }

    public void draw(Canvas canvas)
    {
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - lastTime;

        float totalForce = ((long)Math.cos(fltAngle) / GRAVITY) - fltCoefficientOfFriction;
        float forceY = totalForce * (long)Math.cos(fltAngle);
        float forceX = totalForce * (long)Math.sin(fltAngle);

        velocityX += (forceX / fltMass) * difference;
        velocityY += (forceY / fltMass) * difference;



        // where to draw the sprite
        Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
        canvas.drawBitmap(bitmap, sourceRect, destRect, null);
        canvas.drawBitmap(bitmap, 20, 150, null);
        Paint paint = new Paint();
        paint.setARGB(50, 0, 255, 0);
        canvas.drawRect(20 + (1 * destRect.width()), 150, 20 + (1 * destRect.width()) + destRect.width(), 150 + destRect.height(),  paint);

        lastTime = currentTime;
    }

}
