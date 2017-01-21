package com.peakey.ggj2017.waveyboat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
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
    private Bitmap bmpCrate;

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

        bmpCrate = BitmapFactory.decodeResource(context.getResources(), R.mipmap.dynamite);
        bmpCrate = Bitmap.createScaledBitmap(bmpCrate, 60, 60, false);

        Bomb crate = new Bomb(bmpCrate, 0, 0);
        crate.addSplashedHandler(new Bomb.SplashedHandler() {
            @Override
            public void callback(Bomb bomb){
                BombSplashed(bomb);
            }
        });
        bombs.add(crate);

        crate = new Bomb(bmpCrate, 300, 0);
        crate.addSplashedHandler(new Bomb.SplashedHandler() {
            @Override
            public void callback(Bomb bomb){
                BombSplashed(bomb);
            }
        });
        bombs.add(crate);

        thdGameLoop = new GameLoopThread(this);
        Resume();
        thdGameLoop.start();
    }


    private void BombSplashed(Bomb bomb)
    {
        //Log.d("CRATE", "NOW" );
        bombs.remove(bomb);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager man = (WindowManager)this.context.getSystemService(context.WINDOW_SERVICE);
        Display display = man.getDefaultDisplay();
         Point size = new Point();
         display.getSize(size);
        int MaxWidth = size.x - bmpCrate.getWidth();
        bomb = new Bomb(bmpCrate, (int)(Math.random() * MaxWidth), 0);
        bomb.addSplashedHandler(new Bomb.SplashedHandler() {
            @Override
            public void callback(Bomb crate){
                BombSplashed(crate);
            }
        });
        bombs.add(bomb);
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
        drawBackwave(canvas);
        drawBoat(canvas);
        drawFrontWave(canvas);
        drawBombs(canvas);
    }


    private void drawBackwave(Canvas canvas)
    {

    }


    private void drawBoat(Canvas canvas)
    {

    }


    private void drawFrontWave(Canvas canvas)
    {

    }


    private void drawBombs(Canvas canvas)
    {
        for(int i = bombs.size() - 1; i >= 0; i-- ) {
            Bomb item = bombs.get(i);
            item.draw(canvas);
        }
    }

}
