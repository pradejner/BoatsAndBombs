package com.peakey.ggj2017.waveyboat;

import android.app.Activity;
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
import android.media.Image;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

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
    private ImageView imgLives3;
    private ImageView imgLives2;
    private ImageView imgLives1;
    private TextView txtScore;

    private MediaPlayer mpExplode[];
    private MediaPlayer mpSplash[];
    private int intExplode;
    private int intSplash;
    private Bitmap bmpBomb;
    private Bitmap boat;

    private Boolean blnPlaneMovingRight = true;
    private float fltPlaneRate = 0.5f;
    private float fltBombLikelyHood = 0.90f;
    private Bitmap bmpPlane;
    private Bitmap bmpPlaneReverse;
    Rect rctPlaneSource;
    Rect rctPlaneDest;

    private Rect boatSourceRect;
    private Rect boatDestRect;

    private int Score = 0;
    private int Lives = 3;

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
        mpExplode = new MediaPlayer[10];
        mpSplash = new MediaPlayer[10];
        for (int i = 0; i < mpExplode.length; i++) {
            mpExplode[i] = MediaPlayer.create(context.getApplicationContext(), R.raw.short_explosion);
            mpExplode[i].setVolume(1f, 1f);
            mpSplash[i] = MediaPlayer.create(context.getApplicationContext(), R.raw.splash);
            mpSplash[i].setVolume(0.2f, 0.2f);
        }
        intExplode = -1;
        intSplash = -1;

        bombs = new ArrayList<>();

        DisplayMetrics dp = context.getResources().getDisplayMetrics();

        bmpBomb = BitmapFactory.decodeResource(context.getResources(), R.drawable.dynamite);
        bmpBomb = Bitmap.createScaledBitmap(bmpBomb, (dp.widthPixels / 35), (int)((dp.widthPixels / 35) * 2.15), false);

        //boat = BitmapFactory.decodeResource(context.getResources(), R.drawable.boat_normal);
        boat = BitmapFactory.decodeResource(context.getResources(), R.drawable.boat_jeyan);
        boat = Bitmap.createScaledBitmap(boat, (int)(dp.widthPixels / 5), (int)((dp.widthPixels / 5) * 0.82), false);

        bmpPlane = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane);
        bmpPlane = Bitmap.createScaledBitmap(bmpPlane, (dp.widthPixels / 10), (int)((dp.widthPixels / 10) * 1.178), false);
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

        Thread thd = new Thread(){
            @Override
            public void run()
            {
                try {
                    while (true)
                    {
                        Thread.sleep(100);
                        Score += 1;
                    }
                }
                catch (InterruptedException ex)
                {

                }
            }
        };
        thd.start();
    }


    public void Setup(Activity activity)
    {
        imgLives3 = (ImageView) activity.findViewById(R.id.lives3);
        imgLives2 = (ImageView) activity.findViewById(R.id.lives2);
        imgLives1 = (ImageView) activity.findViewById(R.id.lives1);
        txtScore = (TextView) activity.findViewById(R.id.score);

    }

    private void BombSplashed(Bomb bomb) {
        //MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), R.raw.splash);
        intSplash++;
        if (intSplash >= mpSplash.length)
        {
            intSplash = 0;
        }
        mpSplash[intSplash].start();
        bombs.remove(bomb);
        Score += 10;
    }


    private void BombHit(Bomb bomb) {

        //MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), R.raw.short_explosion);
        intExplode++;
        if (intExplode >= mpExplode.length)
        {
            intExplode = 0;
        }
        mpExplode[intExplode].start();

        bombs.remove(bomb);

        Lives -= 1;
        switch (Lives) {
            case 2:
                imgLives3.setImageResource(R.drawable.heart_dead);
                break;
            case 1:
                imgLives2.setImageResource(R.drawable.heart_dead);
                break;
            default:
                imgLives1.setImageResource(R.drawable.heart_dead);
                break;
        }

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

        if (txtScore != null) {
            txtScore.setText("Score: " + Score);
        }
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
        boatDestRect = new Rect(boatPositionX, canvas.getHeight() - (int)(boat.getHeight() * 1.4), boatPositionX + boat.getWidth(), canvas.getHeight() - (int)(boat.getHeight() * 1.4) + boat.getHeight());

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
        int intRight = boatPositionX + boat.getWidth();
        for (int i = bombs.size() - 1; i >= 0; i--) {
            Bomb item = bombs.get(i);
            item.draw(canvas, deltaTime);
            if (item.isHit(boatPositionX, canvas.getHeight() - (int)(boat.getHeight() * 1.4), intRight))
            {
                BombHit(item);
            }
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
