package com.peakey.ggj2017.waveyboat;

import android.graphics.Canvas;
import android.os.Handler;

public class GameLoopThread extends Thread {
    private GameView view;
    private boolean running = false;

    public GameLoopThread(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        Handler mainHandler = new Handler(view.getContext().getMainLooper());
        while (running) {
            Canvas c = null;
            try {

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        view.invalidate();
                    }
                };
                mainHandler.post(myRunnable);

//                c = view.getHolder().lockCanvas();
//                if (c == null)
//                {
//                    Thread.sleep(50);
//                    continue;
//                }
//                synchronized (view.getHolder()) {
//                    view.onDraw(c);
//                }
//            } catch (InterruptedException ex) {
            }
            finally
            {
//                if (c != null) {
//                    view.getHolder().unlockCanvasAndPost(c);
//                }
            }
        }
    }
}