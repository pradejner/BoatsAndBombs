package com.peakey.ggj2017.waveyboat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static com.peakey.ggj2017.waveyboat.HighScores.HIGH_SCORES;

/**
 * @autor rhankins
 * @date 1/21/2017.
 */

public class GameView extends SurfaceView implements SensorEventListener {
    private static final float PLANE_INIT_RATE = 0.5f;  // currently unchanged during game play
    private static final float BOMB_INIT_LIKELYHOOD = 0.99f;
    private static final float BOMB_INIT_ACCELERATION = 10f;
    private static final float BOMB_INCREASE_LIKELYHOOD = 0.0001f;
    private static final float BOMB_INCREASE_ACCELERATION = 0.001f;

    private static Resources resources;

    private List<Bomb> bombs;
    private static Context context;
    private GameLoopThread thdGameDrawLoop;
    private Thread thdGameLoop;
    private ImageView imgLives3;
    private ImageView imgLives2;
    private ImageView imgLives1;
    private TextView txtScore;
    private Activity refActivity;
    private static SensorManager sensorManager = null;

    private static MediaPlayer mpBackground;
    private static MediaPlayer mpExplode[];
    private static MediaPlayer mpSplash[];
    private static int intExplode;
    private static int intSplash;
    private static Bitmap bmpBackground;
    private static Bitmap bmpBomb;
    private static Bitmap bmpBoat;
    private static Bitmap bmpPlane;
    private static Bitmap bmpPlaneReverse;

    private long currentTime;
    private long deltaTime;
    private static SharedPreferences highScore;

    private Boolean blnPlaneMovingRight = true;
    private float fltPlaneRate = PLANE_INIT_RATE;
    private float fltBombLikelyHood = BOMB_INIT_LIKELYHOOD;
    private float fltBombAcceleration = BOMB_INIT_ACCELERATION;

    private static Rect rctPlaneSource;
    private static Rect rctPlaneDest;
    private static Rect boatSourceRect;
    private static Rect boatDestRect;

    private Boolean gameRunning = false;
    private int score = 0;
    private int lives = 3;

    private int boatPositionX;
    private static DisplayMetrics displayMetrics;
    private float fltTilt = 0;
    private long lastTime;

    private static WindowManager windowManager;
    private static Display display;
    private static Point size;

    private Canvas canvas;
    private int canvasWidth;
    private int canvasHeight;

    private static boolean resourcesReady = false;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        while (!resourcesReady) {
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {

            }
        }

        bombs = new ArrayList<>();

        thdGameDrawLoop = new GameLoopThread(this);
        resume();
        lastTime = 0;
        thdGameDrawLoop.start();

        thdGameLoop = new Thread() {
            @Override
            public void run() {
                try {
                    gameRunning = true;

                    while (gameRunning) {
                        Thread.sleep(100);
                        score += 1;
                        fltBombLikelyHood -= BOMB_INCREASE_LIKELYHOOD;
                        fltBombAcceleration += BOMB_INCREASE_ACCELERATION;
                    }
                }
                catch (InterruptedException ex) {

                }
            }
        };
    }

    public static void loadGameView(Context c) {
        try {
            context = c;
            resources = context.getResources();

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    if (resourcesReady)
                    {
                        return null;
                    }
                    sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
                    windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
                    display = windowManager.getDefaultDisplay();
                    size = new Point();

                    mpBackground = MediaPlayer.create(context, R.raw.background_music);
                    mpBackground.setLooping(true);
                    mpBackground.start();
                    //try { mpBackground.prepare(); } catch (IOException ex) { }
                    highScore = context.getSharedPreferences(HIGH_SCORES, 0);
                    sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
                    mpExplode = new MediaPlayer[10];
                    mpSplash = new MediaPlayer[10];
                    for (int i = 0; i < mpExplode.length; i++) {
                        mpExplode[i] = MediaPlayer.create(context, R.raw.short_explosion);
                        mpExplode[i].setVolume(1f, 1f);
                        //try { mpExplode[i].prepare(); } catch (IOException ex) { }
                        mpSplash[i] = MediaPlayer.create(context, R.raw.splash);
                        mpSplash[i].setVolume(0.2f, 0.2f);
                        //try { mpSplash[i].prepare(); } catch (IOException ex) { }
                    }
                    intExplode = -1;
                    intSplash = -1;


                    displayMetrics = resources.getDisplayMetrics();

                    bmpBackground = BitmapFactory.decodeResource(resources, R.drawable.background);
                    //bmpBoat = BitmapFactory.decodeResource(resources, R.drawable.boat_jeyan);
                    bmpBoat = BitmapFactory.decodeResource(resources, R.drawable.boat_normal);
                    bmpBomb = BitmapFactory.decodeResource(resources, R.drawable.dynamite);
                    bmpPlane = BitmapFactory.decodeResource(resources, R.drawable.plane);

                    bmpBackground = Bitmap.createScaledBitmap(bmpBackground, displayMetrics.widthPixels, displayMetrics.heightPixels, false);
                    bmpBoat = Bitmap.createScaledBitmap(bmpBoat, (displayMetrics.widthPixels / 5), (int) ((displayMetrics.widthPixels / 5) * 0.82), false);
                    bmpBomb = Bitmap.createScaledBitmap(bmpBomb, (displayMetrics.widthPixels / 35), (int) ((displayMetrics.widthPixels / 35) * 2.15), false);
                    bmpPlane = Bitmap.createScaledBitmap(bmpPlane, (displayMetrics.widthPixels / 10), (int) ((displayMetrics.widthPixels / 10) * 1.178), false);

                    Matrix m = new Matrix();
                    m.preScale(-1, 1);

                    int planeWidth = bmpPlane.getWidth();
                    int planeHeight = bmpPlane.getHeight();
                    bmpPlaneReverse = Bitmap.createBitmap(bmpPlane, 0, 0, planeWidth, planeHeight, m, false);
                    bmpPlaneReverse.setDensity(DisplayMetrics.DENSITY_DEFAULT);

                    rctPlaneSource = new Rect(0, 0, planeWidth, planeHeight);
                    rctPlaneDest = new Rect(0, 0, planeWidth, planeHeight);

                    boatSourceRect = new Rect(0, 0, bmpBoat.getWidth(), bmpBoat.getHeight());

                    resourcesReady = true;
                    return null;
                }
            }.execute();
        }
        catch (final Exception e) {
            Log.e("Error loadGameView: {}", e.toString());
        }
        finally {

        }
    }

    public void applyActivity(Activity activity) {
        refActivity = activity;
        imgLives3 = (ImageView) activity.findViewById(R.id.lives3);
        imgLives2 = (ImageView) activity.findViewById(R.id.lives2);
        imgLives1 = (ImageView) activity.findViewById(R.id.lives1);
        txtScore = (TextView) activity.findViewById(R.id.score);
    }

    private void bombSplashed(Bomb bomb) {
        intSplash++;
        if (intSplash >= mpSplash.length) {
            intSplash = 0;
        }
        mpSplash[intSplash].start();
        bombs.remove(bomb);
        score += 10;
    }

    private void bombHit(Bomb bomb) {
        intExplode++;
        if (intExplode >= mpExplode.length) {
            intExplode = 0;
        }
        mpExplode[intExplode].start();

        bombs.remove(bomb);

        lives -= 1;
        switch (lives) {
            case 2:
                refActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgLives3.setImageResource(R.drawable.heart_dead);
                    }
                });
                break;
            case 1:
                refActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgLives2.setImageResource(R.drawable.heart_dead);
                    }
                });
                break;
            default:
                refActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgLives1.setImageResource(R.drawable.heart_dead);
                        GameOver();
                        SharedPreferences.Editor scoreEdit = highScore.edit();
                        String scores = highScore.getString("highScores", "");
                        if (scores.length() > 0) {
                            if (score > Integer.parseInt(scores)) {
                                scoreEdit.remove("highScores");
                                scoreEdit.putString("highScores", "" + score);
                                scoreEdit.commit();
                            }
                        }
                        else {
                            scoreEdit.putString("highScores", "" + score);
                            scoreEdit.commit();
                        }

                        Intent gameIntent = new Intent(getContext(), GameOver.class);
                        gameIntent.putExtra("score", Integer.toString(score));
                        getContext().startActivity(gameIntent);
                    }
                });

                break;
        }
    }

    private void GameOver() {
        pause();
        stop();
    }

    public void destroy() {
        boolean retry = true;
        thdGameDrawLoop.setRunning(false);
        while (retry) {
            try {
                thdGameDrawLoop.join();
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
    }

    public void pause() {
        gameRunning = false;
        try {
            thdGameLoop.join(100);
        }
        catch (Exception ex) {
        }
        thdGameDrawLoop.setRunning(false);
        //mpBackground.stop();
        lastTime = 0;
    }

    public void resume() {
        thdGameDrawLoop.setRunning(true);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    public void Draw(Canvas canvas) {
        if (null != canvas) {
            this.canvas = canvas;
            canvasWidth = canvas.getWidth();
            canvasHeight = canvas.getHeight();

            //must do these here AFTER we have a canvas
            boatDestRect = new Rect(boatPositionX, canvasHeight - (int) (bmpBoat.getHeight() * 1.4), boatPositionX + bmpBoat.getWidth(), canvasHeight - (int) (bmpBoat.getHeight() * 1.4) + bmpBoat.getHeight());
        }
        if (lastTime == 0) {   //everything is running now, games should actually start

            refActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (thdGameLoop.isAlive())
                    {
                        gameRunning = false;
                        try {
                            thdGameLoop.join(100);
                        } catch (InterruptedException ex) {

                        }
                    }
                    try {
                        thdGameLoop.start();
                    } catch (Exception ex) {

                    }
                }
            });
            lastTime = System.currentTimeMillis();
        }
        currentTime = System.currentTimeMillis();
        deltaTime = currentTime - lastTime;

        drawBackground();
        drawPlane(deltaTime);
        drawBoat(deltaTime);
        drawBombs(deltaTime);

        if (txtScore != null) {
            refActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtScore.setText("Score: " + score);
                }
            });
        }
        lastTime = currentTime;
    }

    public void stop() {
        pause();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }

    public void drawBackground() {
        Rect bgSourceRect = new Rect(0, 0, bmpBackground.getWidth(), bmpBackground.getHeight());
        canvas.drawBitmap(bmpBackground, bgSourceRect, bgSourceRect, null);
    }

    public void drawBoat(long deltaTime) {
        boatPositionX += fltTilt * ((float) deltaTime / 1000);
        if (boatPositionX < 0) {
            boatPositionX = 0;
        }
        else if (boatPositionX + bmpBoat.getWidth() > canvasWidth) {
            boatPositionX = canvasWidth - bmpBoat.getWidth();
        }
        //boatSourceRect = new Rect(0, 0, bmpBoat.getWidth(), bmpBoat.getHeight());
        //boatDestRect = new Rect(boatPositionX, canvasHeight - (int) (bmpBoat.getHeight() * 1.4), boatPositionX + bmpBoat.getWidth(), canvasHeight - (int) (bmpBoat.getHeight() * 1.4) + bmpBoat.getHeight());
        boatDestRect.left = boatPositionX;
        boatDestRect.right = boatPositionX + bmpBoat.getWidth();

        canvas.drawBitmap(bmpBoat, boatSourceRect, boatDestRect, null);
    }

    private void drawPlane(long deltaTime) {
        if (blnPlaneMovingRight) {
            rctPlaneDest.left += fltPlaneRate * deltaTime;
            rctPlaneDest.right = rctPlaneDest.left + bmpPlane.getWidth();
            if (rctPlaneDest.left > canvasWidth) {
                if (rctPlaneDest.left > (canvasWidth + bmpPlane.getWidth())) {
                    blnPlaneMovingRight = false;
                }
            }
            canvas.drawBitmap(bmpPlane, rctPlaneSource, rctPlaneDest, null);
        }
        else {
            rctPlaneDest.left -= fltPlaneRate * deltaTime;
            rctPlaneDest.right = rctPlaneDest.left + bmpPlane.getWidth();
            if (rctPlaneDest.left < 0) {
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
        display.getSize(size);
        int MaxWidth = size.x - bmpBomb.getWidth();
        Bomb bomb = new Bomb(bmpBomb, rctPlaneDest.left, 0, fltBombAcceleration);
        bomb.addSplashedHandler(new Bomb.SplashedHandler() {
            @Override
            public void callback(Bomb crate) {
                bombSplashed(crate);
            }
        });
        bombs.add(bomb);
    }

    private void drawBombs(long deltaTime) {
        int intRight = boatPositionX + bmpBoat.getWidth();
        for (int i = bombs.size() - 1; i >= 0; i--) {
            Bomb item = bombs.get(i);
            item.draw(canvas, deltaTime);
            if (item.isHit(boatPositionX, boatDestRect.top + (bmpBoat.getHeight() / 3), intRight)) {
                bombHit(item);
            }
        }
    }

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
