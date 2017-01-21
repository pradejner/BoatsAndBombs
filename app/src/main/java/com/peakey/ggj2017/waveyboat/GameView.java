package com.peakey.ggj2017.waveyboat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peakeyAdmin on 1/21/2017.
 */

public class GameView extends View {


    private static final float GRAVITY_ACCELERATION = 9.8f;
    private List<Bomb> bombs;
    private Context context;
    private GameLoopThread thdGameLoop;

    private Bitmap bmpBomb;

    private Boolean blnPlaneMovingRight = true;
    private float fltPlaneRate = 10;
    private float fltBombLikelyHood = 0.99f;
    private Bitmap bmpPlane;
    Rect rctPlaneSource;
    Rect rctPlaneDest;


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


    private void init()
    {
        bombs = new ArrayList<>();

        bmpBomb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.dynamite);
        bmpBomb = Bitmap.createScaledBitmap(bmpBomb, 60, 60, false);

        bmpPlane = BitmapFactory.decodeResource(context.getResources(), R.mipmap.plane);
        bmpPlane = Bitmap.createScaledBitmap(bmpPlane, 100, 100, false);
        rctPlaneSource = new Rect(0, 0, bmpPlane.getWidth(), bmpPlane.getHeight());
        rctPlaneDest = new Rect(0, 0, bmpPlane.getWidth(), bmpPlane.getHeight());

        thdGameLoop = new GameLoopThread(this);
        Resume();
        thdGameLoop.start();
    }


    private void BombSplashed(Bomb bomb)
    {
        //Log.d("CRATE", "NOW" );
        bombs.remove(bomb);
    }

    public void Destroy()
    {
        boolean retry = true;
        thdGameLoop.setRunning(false);
        while (retry) {
            try {
                thdGameLoop.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void Pause()
    {
        thdGameLoop.setRunning(false);
    }


    public void Resume()
    {
        thdGameLoop.setRunning(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPlane(canvas);
        drawBombs(canvas);
    }



    private void drawPlane(Canvas canvas)
    {
        Boolean blnCanBomb = true;
        if (blnPlaneMovingRight) {
            rctPlaneDest.left += fltPlaneRate;
            rctPlaneDest.right = rctPlaneDest.left + bmpPlane.getWidth();
            if (rctPlaneDest.left > canvas.getWidth())
            {
                blnCanBomb = false;
                if (rctPlaneDest.left > (canvas.getWidth() + bmpPlane.getWidth()))
                {
                    blnPlaneMovingRight = false;
                }
            }
        }
        else
        {
            rctPlaneDest.left -= fltPlaneRate;
            rctPlaneDest.right = rctPlaneDest.left - bmpPlane.getWidth();
            if (rctPlaneDest.left < 0)
            {
                blnCanBomb = false;
                if (rctPlaneDest.left < -bmpPlane.getWidth())
                {
                    blnPlaneMovingRight = true;
                }
            }
        }
        canvas.drawBitmap(bmpPlane, rctPlaneSource, rctPlaneDest, null);
        if (Math.random() > fltBombLikelyHood)
        {
            addBomb();
        }
    }


    private void addBomb()
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager man = (WindowManager)this.context.getSystemService(context.WINDOW_SERVICE);
        Display display = man.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int MaxWidth = size.x - bmpBomb.getWidth();
        Bomb bomb = new Bomb(bmpBomb, rctPlaneDest.left, 0);
        bomb.addSplashedHandler(new Bomb.SplashedHandler() {
            @Override
            public void callback(Bomb crate){
                BombSplashed(crate);
            }
        });
        bombs.add(bomb);
    }

    private void drawBombs(Canvas canvas)
    {
        for(int i = bombs.size() - 1; i >= 0; i-- ) {
            Bomb item = bombs.get(i);
            item.draw(canvas);
        }
    }

}
