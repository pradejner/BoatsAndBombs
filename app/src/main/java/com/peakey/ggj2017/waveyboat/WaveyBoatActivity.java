package com.peakey.ggj2017.waveyboat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WaveyBoatActivity extends AppCompatActivity {

    private WaveHelper mWaveHelper;

    private int mBorderColor = Color.parseColor("#44FFFFFF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wavey_boat_activity);
        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(0, mBorderColor);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);

        mWaveHelper = new WaveHelper(waveView);

        waveView.setWaveColor(
                Color.parseColor("#88b8f1ed"),
                Color.parseColor("#b8f1ed"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaveHelper.start();
    }
}