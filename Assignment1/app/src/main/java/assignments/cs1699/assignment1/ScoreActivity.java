package assignments.cs1699.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Scanner;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        ArrayList<String> scoresList = new ArrayList<String>();     //list of scores
        int highScore = 0;
        Scanner scan = null;
        String line = null;

        //Read all scores from scoreHistory file
        try{
            scan = new Scanner(openFileInput("scoreHistory.txt"));
            while(scan.hasNextLine()){
                line = scan.nextLine();
                String[] score = line.split("==>");
                if(Integer.parseInt(score[1]) >= highScore){        //get new high score
                    highScore = Integer.parseInt(score[1]);
                }
                scoresList.add(score[0] + "\t\t\t" + score[1] + "%");
            }
            scan.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Set high score TextView
        TextView myTextView = (TextView) findViewById(R.id.highScore);
        myTextView.setText("Highest score: \t\t\t" + highScore + "%");

        //Populate scores ListView
        ListView myListView = (ListView) findViewById(R.id.scoresLView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, scoresList);
        myListView.setAdapter(adapter);
    }

    public void goBack (View view) {
        finish();
    }
}
