package com.peakey.ggj2017.waveyboat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Double2;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by peakeyAdmin on 1/20/2017.
 */

public class Crate
{

    public interface SplashedHandler{
        void callback(Crate crate);
    }


    private ArrayList<SplashedHandler> lstSplashedHandlers;

    private static final String TAG = Crate.class.getSimpleName();
    private static final float GRAVITY_ACCELERATION = 9.8f; //M/S^2

    private Bitmap bitmap;		// the animation sequence
    private Rect sourceRect;	// the rectangle to be drawn from the animation bitmap

    private int spriteWidth;	// the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;	// the height of the sprite

    private float x;				// the X coordinate of the object (top left of the image)
    private float y;				// the Y coordinate of the object (top left of the image)

    private float fltGravityForce;
    private boolean blnOnBoat;
    private float fltCoefficientOfFriction;
    private float fltFriction;
    private float fltMass;
    private float fltAngle;
    private long lastTime;
    private float velocityX;
    private float velocityY;


    public Crate(Bitmap bitmap, float x, float y, float Mass, float CoefficientOfFriction, float Friction, float Angle)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        spriteWidth = bitmap.getWidth();
        spriteHeight = bitmap.getHeight();
        sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
        fltMass = Mass;
        fltGravityForce = fltMass * GRAVITY_ACCELERATION;
        fltCoefficientOfFriction = CoefficientOfFriction;
        fltFriction = Friction;
        fltAngle = Angle;
        lastTime = System.currentTimeMillis();
        velocityY = 0;
        velocityX = 0;
        blnOnBoat = true;
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


    public void draw(Canvas canvas)
    {
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - lastTime;

        blnOnBoat = false;
        if (blnOnBoat) {
            float totalForce = ((long) Math.cos(fltAngle) / fltGravityForce) - fltCoefficientOfFriction;
            float totalAcceleration = fltMass / totalForce;
            float accelerationX = totalAcceleration * (long) Math.sin(fltAngle);
            float accelerationY = totalAcceleration * (long) Math.cos(fltAngle);

            velocityX += ((accelerationY / fltMass) * difference) / 1000;
            velocityY += ((accelerationX / fltMass) * difference) / 1000;
        }
        else{
            velocityY += (GRAVITY_ACCELERATION * ( (double)difference / 1000 ) );

        }

//        Log.d("CRATE", Double.toString( velocityY) );

        //assume boxes are 1 meter squares
        y += velocityY;

        // where to draw the sprite
        Rect destRect = new Rect((int)x, (int)y, (int)x + spriteWidth, (int)y + spriteHeight);
        canvas.drawBitmap(bitmap, sourceRect, destRect, null);


        if (y > canvas.getHeight())
        {
            for (SplashedHandler splashHandler:lstSplashedHandlers){
                splashHandler.callback(this);
            }
        }
        lastTime = currentTime;
    }

}
