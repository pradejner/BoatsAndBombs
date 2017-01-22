package com.peakey.ggj2017.waveyboat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class WaveyBoatActivity extends Activity {
    private WaveView backgroundWaveView;
    private WaveView frontWaveView;

    private WaveHelper backgroundWaveHelper;
    private WaveHelper frontWaveHelper;
    private LinearLayout bottomBar;

    private GameView game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.wavey_boat_activity);

        backgroundWaveView = (WaveView) findViewById(R.id.background_wave);
        frontWaveView = (WaveView) findViewById(R.id.front_wave);
        game = (GameView) findViewById(R.id.game);
        SurfaceHolder sfhGame = game.getHolder();
        sfhGame.setFormat(PixelFormat.TRANSLUCENT);

        bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);

        setAttributesOfWave(backgroundWaveView);
        setAttributesOfWave(frontWaveView);

        backgroundWaveView.setWaveColor(Color.parseColor("#013865"), Color.parseColor("#025191"));
        frontWaveView.setWaveColor(Color.parseColor("#259BFC"), Color.parseColor("#4AACFD"));

        backgroundWaveView.setWaveLengthRatio(.2f);
        frontWaveView.setWaveLengthRatio(.3f);

        backgroundWaveHelper = new WaveHelper(backgroundWaveView);
        frontWaveHelper = new WaveHelper(frontWaveView);

        game = (GameView) findViewById(R.id.game);
        game.applyActivity(this);
        frontWaveView.bringToFront();
        bottomBar.bringToFront();
    }

    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        if (game != null) {
            game.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        backgroundWaveHelper.cancel();
        frontWaveHelper.cancel();
        if (game != null) {
            game.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        backgroundWaveHelper.start();
        frontWaveHelper.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != game) {
            game.stop();
        }
    }

    private void setAttributesOfWave(WaveView waveView) {
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
        if (game != null) {
            game.resume();
        }
    }
}