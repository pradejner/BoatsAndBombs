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

        while (running) {
            try {
                Canvas c = null;
                try {
                    c = view.getHolder().lockCanvas();
                    if (c == null)
                    {
                        continue;
                    }
                    synchronized (view.getHolder()) {
                        view.draw(c);
                    }
                } finally {
                    if (c != null) {
                        view.getHolder().unlockCanvasAndPost(c);
                    }
                }
            }
            finally
            {

            }
        }
    }
}