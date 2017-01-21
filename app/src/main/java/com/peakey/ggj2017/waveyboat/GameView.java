package com.peakey.ggj2017.waveyboat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peakeyAdmin on 1/21/2017.
 */

public class GameView extends View {


    private static final float GRAVITY_ACCELERATION = 9.8f;
    private List<Crate> crates;
    private Context context;
    private GameLoopThread thdGameLoop;

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
        crates = new ArrayList<>();

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.woodencrate);
        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, false);

        crates.add(new Crate(bitmap, 0, 0, 5, 0, 0, 0));

        thdGameLoop = new GameLoopThread(this);
        Resume();
        thdGameLoop.start();
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
        //mWaveHelper.cancel();
    }


    public void Resume()
    {
        thdGameLoop.setRunning(true);
        //mWaveHelper.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawBackwave(canvas);
        drawBoat(canvas);
        drawFrontWave(canvas);
        drawCrates(canvas);
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


    private void drawCrates(Canvas canvas)
    {
        for(Iterator<Crate> i = crates.iterator(); i.hasNext(); ) {
            Crate item = i.next();
            item.draw(canvas);
        }
    }

}
