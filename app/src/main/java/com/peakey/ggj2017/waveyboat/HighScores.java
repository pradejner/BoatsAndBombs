package com.peakey.ggj2017.waveyboat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HighScores extends Activity {

    private Button resetButton;
    private SharedPreferences highScore;
    public static final String HIGH_SCORES = "HighScores";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_scores);

        highScore = getSharedPreferences(HIGH_SCORES, 0);//gethighscores
        //initialize components
        final SharedPreferences.Editor scoreEdit = highScore.edit();
        final TextView textView2=(TextView) findViewById(R.id.textView2);
        final SharedPreferences scorePrefs = getSharedPreferences(HIGH_SCORES, 0);
        //split strings, loop through them appending a newline char
        String savedScore = scorePrefs.getString("highScores", "");

        textView2.setText(savedScore);//set the text
        //reset button listener onclick clears sharedpref and resets textview2 text
        resetButton=(Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreEdit.clear();
                scoreEdit.commit();
                textView2.setText("");
            }
        });
    }
}