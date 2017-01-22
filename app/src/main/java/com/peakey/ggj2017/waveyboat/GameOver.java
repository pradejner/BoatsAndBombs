package com.peakey.ggj2017.waveyboat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends Activity {

    private Button playAgain;
    private TextView highScore;
    private TextView currentScore;
    private SharedPreferences highScoreData;
    public static final String HIGH_SCORES = "HighScores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        playAgain = (Button) findViewById(R.id.resetButton);
        highScore = (TextView) findViewById(R.id.high_score);
        currentScore = (TextView) findViewById(R.id.current_score);

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
    }

}