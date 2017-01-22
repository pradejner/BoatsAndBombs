package com.peakey.ggj2017.waveyboat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends Activity {

    private Button playAgain;
    private Button mainMenu;
    private TextView highScore;
    private TextView currentScore;
    private static SharedPreferences highScorePref;
    public static final String HIGH_SCORES = "HighScores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.game_over);

        playAgain = (Button) findViewById(R.id.resetButton);
        mainMenu = (Button) findViewById(R.id.mainMenuButton);
        highScore = (TextView) findViewById(R.id.high_score);
        currentScore = (TextView) findViewById(R.id.current_score);

        String scores = highScorePref.getString("highScores", "");

        highScore.setText("HighScore: " + scores);
        Intent intent = getIntent();
        currentScore.setText("Current Score: " + intent.getStringExtra("score"));

//        highScore = getSharedPreferences(HIGH_SCORES, 0);//gethighscores
//        //initialize components
//        final SharedPreferences.Editor scoreEdit = highScore.edit();
//        final TextView textView2 = (TextView) findViewById(R.id.textView2);
//        final SharedPreferences scorePrefs = getSharedPreferences("High Scores", 0);
//        //split strings, loop through them appending a newline char
//        String[] savedScores = scorePrefs.getString("highScores", "").split(",");
//        StringBuilder buildScore = new StringBuilder("");
//        for (String score : savedScores) {
//            buildScore.append(score + "\n");
//        }
        //highScore.setText(buildScore.toString());//set the text
        //reset button listener onclick clears sharedpref and resets textview2 text

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(getApplicationContext(), WaveyBoatActivity.class);
                startActivity(gameIntent);
            }
        });

        mainMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent titleIntent = new Intent(getApplicationContext(), TitlePage.class);
                startActivity(titleIntent);
            }
        });
    }
}