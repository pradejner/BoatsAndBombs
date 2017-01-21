package com.peakey.ggj2017.waveyboat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class WaveyBoatActivity extends AppCompatActivity {
    private WaveView backgroundWaveView;
    private WaveView middleWaveView;
    private WaveView frontWaveView;

    private WaveHelper backgroundWaveHelper;
    private WaveHelper middleWaveHelper;
    private WaveHelper frontWaveHelper;

    private ImageView boatImage;

    private int borderColor = Color.parseColor("#44FFFFFF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wavey_boat_activity);

        backgroundWaveView = (WaveView) findViewById(R.id.background_wave);
        middleWaveView = (WaveView) findViewById(R.id.middle_wave);
        frontWaveView = (WaveView) findViewById(R.id.front_wave);
        boatImage = (ImageView) findViewById(R.id.boat_image);

        boatImage.setImageResource(R.mipmap.boat);

        setAttributesOfWave(backgroundWaveView);
        setAttributesOfWave(middleWaveView);
        setAttributesOfWave(frontWaveView);

        backgroundWaveView.setWaveColor(Color.parseColor("#013865"), Color.parseColor("#05FF00FF"));
        middleWaveView.setWaveColor(Color.parseColor("#026ABF"), Color.parseColor("#05FF00FF"));
        frontWaveView.setWaveColor(Color.parseColor("#259BFC"), Color.parseColor("#05FF00FF"));

        backgroundWaveView.setWaveLengthRatio(.25f);
        middleWaveView.setWaveLengthRatio(.2f);
        frontWaveView.setWaveLengthRatio(.15f);

        middleWaveView.bringToFront();
        frontWaveView.bringToFront();

        backgroundWaveHelper = new WaveHelper(backgroundWaveView);
        middleWaveHelper = new WaveHelper(middleWaveView);
        frontWaveHelper = new WaveHelper(frontWaveView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        backgroundWaveHelper.cancel();
        middleWaveHelper.cancel();
        frontWaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        backgroundWaveHelper.start();
        middleWaveHelper.start();
        frontWaveHelper.start();
    }

    private void setAttributesOfWave(WaveView waveView) {
        waveView.setBorder(0, borderColor);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
    }
}