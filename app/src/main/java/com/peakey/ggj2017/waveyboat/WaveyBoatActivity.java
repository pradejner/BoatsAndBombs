package com.peakey.ggj2017.waveyboat;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class WaveyBoatActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager = null;

    private WaveView backgroundWaveView;
    private WaveView frontWaveView;

    private WaveHelper backgroundWaveHelper;
    private WaveHelper frontWaveHelper;

    private ImageView boatImage;
    private GameView game;

    private int borderColor = Color.parseColor("#44FFFFFF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.wavey_boat_activity);

        backgroundWaveView = (WaveView) findViewById(R.id.background_wave);
        frontWaveView = (WaveView) findViewById(R.id.front_wave);
        boatImage = (ImageView) findViewById(R.id.boat_image);
        game = (GameView) findViewById(R.id.game);

        boatImage.setImageResource(R.drawable.boat);

        setAttributesOfWave(backgroundWaveView);
        setAttributesOfWave(frontWaveView);

        backgroundWaveView.setWaveColor(Color.parseColor("#013865"), Color.parseColor("#026ABF"));
        frontWaveView.setWaveColor(Color.parseColor("#259BFC"), Color.parseColor("#4AACFD"));

        backgroundWaveView.setWaveLengthRatio(.2f);
        frontWaveView.setWaveLengthRatio(.2f);

        backgroundWaveHelper = new WaveHelper(backgroundWaveView);
        frontWaveHelper = new WaveHelper(frontWaveView);

        frontWaveView.bringToFront();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (game != null) {
            game.Destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        backgroundWaveHelper.cancel();
        frontWaveHelper.cancel();
        if (game != null) {
            game.Pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        backgroundWaveHelper.start();
        frontWaveHelper.start();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }

    private void setAttributesOfWave(WaveView waveView) {
        waveView.setBorder(0, borderColor);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
        if (game != null) {
            game.Resume();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
//            switch (event.sensor.getType()) {
//                case Sensor.TYPE_ACCELEROMETER:
//                    //boat.setRotation(boat.getRotation() + 1);
//                    boatImage.setRotation(event.values[1] * 5);
//                    break;
//                case Sensor.TYPE_ORIENTATION:
//                    break;
//
//            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}