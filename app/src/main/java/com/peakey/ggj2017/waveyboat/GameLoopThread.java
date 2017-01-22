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
            try {

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        view.invalidate();
                    }
                };
                mainHandler.post(myRunnable);

            }
            finally
            {

            }
        }
    }
}