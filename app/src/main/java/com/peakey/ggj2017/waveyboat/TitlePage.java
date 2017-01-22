package com.peakey.ggj2017.waveyboat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class TitlePage extends Activity {

    private Button score;
    private Button game;
    private Button credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        GameView.loadGameView(getApplicationContext());
        setContentView(R.layout.title_page);
        game = (Button) findViewById(R.id.start_button);
        //game button listener onclick creates intent and goes to game activity
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(getApplicationContext(), WaveyBoatActivity.class);
                startActivity(gameIntent);
            }
        });
        score = (Button) findViewById(R.id.scores_button);
        //highscore button listener onclick creates intent and goes to highscore activity
        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scoreIntent = new Intent(getApplicationContext(), HighScores.class);
                startActivity(scoreIntent);
            }
        });

        credits = (Button) findViewById(R.id.credits_button);

        credits.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent creditsIntent = new Intent(getApplicationContext(), Credits.class);
                startActivity(creditsIntent);
            }
        });
    }
}
