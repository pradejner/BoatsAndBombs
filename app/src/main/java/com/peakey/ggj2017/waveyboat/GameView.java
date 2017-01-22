package com.peakey.ggj2017.waveyboat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * @autor rhankins
 * @date 1/21/2017.
 */

public class GameView extends View implements SensorEventListener {
    SensorManager sensorManager = null;

    private List<Bomb> bombs;
    private Context context;
    private GameLoopThread thdGameLoop;

    private Bitmap bmpBomb;
    private Bitmap boat;

    private Boolean blnPlaneMovingRight = true;
    private float fltPlaneRate = 0.5f;
    private float fltBombLikelyHood = 0.99f;
    private Bitmap bmpPlane;
    private Bitmap bmpPlaneReverse;
    Rect rctPlaneSource;
    Rect rctPlaneDest;

    private Rect boatSourceRect;
    private Rect boatDestRect;

    private int boatPositionX;

    public GameView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }


    private void init() {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        bombs = new ArrayList<>();

        bmpBomb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.dynamite);
        bmpBomb = Bitmap.createScaledBitmap(bmpBomb, 60, 60, false);
        boat = BitmapFactory.decodeResource(context.getResources(), R.drawable.boat);

        bmpPlane = BitmapFactory.decodeResource(context.getResources(), R.mipmap.plane);
        bmpPlane = Bitmap.createScaledBitmap(bmpPlane, 100, 100, false);
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        bmpPlaneReverse = Bitmap.createBitmap(bmpPlane, 0, 0, bmpPlane.getWidth(), bmpPlane.getHeight(), m, false);
        bmpPlaneReverse.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        rctPlaneSource = new Rect(0, 0, bmpPlane.getWidth(), bmpPlane.getHeight());
        rctPlaneDest = new Rect(0, 0, bmpPlane.getWidth(), bmpPlane.getHeight());

        thdGameLoop = new GameLoopThread(this);
        resume();
        lastTime = System.currentTimeMillis();
        thdGameLoop.start();
    }


    private void BombSplashed(Bomb bomb) {
        MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), R.raw.short_explosion);
        mp.start();
        bombs.remove(bomb);
    }

    public void destroy() {
        boolean retry = true;
        thdGameLoop.setRunning(false);
        while (retry) {
            try {
                thdGameLoop.join();
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
    }

    public void pause() {
        thdGameLoop.setRunning(false);
    }


    public void resume() {
        thdGameLoop.setRunning(true);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_UI);
    }


    private long lastTime;


    @Override
    protected void onDraw(Canvas canvas) {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastTime;

        drawPlane(canvas, deltaTime);
        drawBoat(canvas, deltaTime);
        drawBombs(canvas, deltaTime);

        lastTime = currentTime;
    }

    public void stop() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }

    public void drawBoat(Canvas canvas, long deltaTime) {
        boatPositionX += fltTilt * ((float)deltaTime / 1000);
        if (boatPositionX < 0)
        {
            boatPositionX = 0;
        }
        else if (boatPositionX + boat.getWidth() > canvas.getWidth())
        {
            boatPositionX = canvas.getWidth() - boat.getWidth();
        }
        boatSourceRect = new Rect(0, 0, boat.getWidth(), boat.getHeight());
        boatDestRect = new Rect(boatPositionX, 500, boatPositionX + boat.getWidth(), 500 + boat.getHeight());

        canvas.drawBitmap(boat, boatSourceRect, boatDestRect, null);
    }

    private void drawPlane(Canvas canvas, long deltaTime) {
        Boolean blnCanBomb = true;
        if (blnPlaneMovingRight) {
            rctPlaneDest.left += fltPlaneRate * deltaTime;
            rctPlaneDest.right = rctPlaneDest.left + bmpPlane.getWidth();
            if (rctPlaneDest.left > canvas.getWidth()) {
                blnCanBomb = false;
                if (rctPlaneDest.left > (canvas.getWidth() + bmpPlane.getWidth())) {
                    blnPlaneMovingRight = false;
                }
            }
            canvas.drawBitmap(bmpPlane, rctPlaneSource, rctPlaneDest, null);
        }
        else {
            rctPlaneDest.left -= fltPlaneRate * deltaTime;
            rctPlaneDest.right = rctPlaneDest.left + bmpPlane.getWidth();
            if (rctPlaneDest.left < 0) {
                blnCanBomb = false;
                if (rctPlaneDest.left < -bmpPlane.getWidth()) {
                    blnPlaneMovingRight = true;
                }
            }
            canvas.drawBitmap(bmpPlaneReverse, rctPlaneSource, rctPlaneDest, null);
        }
        if (Math.random() > fltBombLikelyHood) {
            addBomb();
        }
    }


    private void addBomb() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager man = (WindowManager) this.context.getSystemService(context.WINDOW_SERVICE);
        Display display = man.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int MaxWidth = size.x - bmpBomb.getWidth();
        Bomb bomb = new Bomb(bmpBomb, rctPlaneDest.left, 0);
        bomb.addSplashedHandler(new Bomb.SplashedHandler() {
            @Override
            public void callback(Bomb crate) {
                BombSplashed(crate);
            }
        });
        bombs.add(bomb);
    }


    private void drawBombs(Canvas canvas, long deltaTime) {
        for (int i = bombs.size() - 1; i >= 0; i--) {
            Bomb item = bombs.get(i);
            item.draw(canvas, deltaTime);
        }
    }


    private float fltTilt = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    fltTilt = event.values[1] * 200;
                    break;
                case Sensor.TYPE_ORIENTATION:
                    break;

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
