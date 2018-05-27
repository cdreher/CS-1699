package assignments.cs1699.assignment1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class PlayActivity extends AppCompatActivity {

    private ArrayList<String[]> dict;
    private int choice;
    private ListView myListView;
    private TextView myTextView;
    private ArrayList<String> definitionsList;
    private ArrayList<String> usedTerms;
    private ArrayAdapter adapter;
    private String[] word;
    private int score;
    private int progress;
    private int termCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);



        usedTerms = new ArrayList<String>();            //list of terms selected for previous questions
        myListView = (ListView) findViewById(R.id.definitionLView);
        myTextView = (TextView) findViewById(R.id.term);
        progress = 0;
        score = 0;

        word = getTerm();       //get selected term
        usedTerms.add(word[0]);     //add term to list of used terms


        //Check to see if selected answer is correct
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String myString = myListView.getItemAtPosition(i).toString();

                //Check to see if selected choice is correct
                if(word[1].equals(myString)){
                    score ++;
                    Toast.makeText(getApplicationContext(), "Correct Answer! Your score is: " + score + ".", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Wrong Answer!", Toast.LENGTH_SHORT).show();

                }

                //Change progress bar progress
                ProgressBar myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
                progress += 20;
                if(progress < 100) {        //if progress is not full, change term and definitions.
                    myProgressBar.setProgress(progress);
                    definitionsList = new ArrayList<String>();
                    adapter.notifyDataSetChanged();
                    myTextView.setText(null);

                    word = getTerm();       //get another term

                    int index = 0;

                    //Check to see if term has been used already
                    while(index < usedTerms.size()) {
                        if(word[0].equals(usedTerms.get(index))){       //if term has already been used, get another one
                            word = getTerm();
                            index = 0;
                        }
                        else{
                            index++;
                        }
                    }

                    usedTerms.add(word[0]);     //add term to list of used terms



                }
                else{
                    myProgressBar.setProgress(progress);
                    score *= 20;        //get percentage score
                    String timeString = DateFormat.getDateTimeInstance().format(new Date());        //get time of submission

                    //Print to new file
                    PrintStream output = null;
                    try {
                        output = new PrintStream(openFileOutput("scoreHistory.txt", Context.MODE_APPEND));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    output.println(timeString + "==>" + score);
                    output.close();

                    finish();
                }


            }
        });

        //Go back to main activity.
        if(progress == 100) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }



    public String[] getTerm() {
        Scanner scan = new Scanner(getResources().openRawResource(R.raw.terms));
        String line = null;
        termCount = 0;      //total word count
        dict = new ArrayList<String[]>();       //create new dictionary of terms
        definitionsList = new ArrayList<String>();       //create new def. list

        while (scan.hasNextLine()) {
            line = scan.nextLine();
            termCount++;
            String[] jawn = line.split(":");

            dict.add(jawn);
        }
        scan.close();

        //Open new terms file
        Scanner newTermScan = null;
        try {
            newTermScan = new Scanner(openFileInput("newTerms.txt"));
            while(newTermScan.hasNextLine()){
                line = newTermScan.nextLine();
                termCount++;
                String[] newJawn = line.split(":");
                dict.add(newJawn);
            }
            newTermScan.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }



        //Get random word
        Random r = new Random();
        choice = r.nextInt(dict.size());           //get random word from dictionary

        myTextView.setText(dict.get(choice)[0]);     //set text for TextView

        //Populate ListView
        definitionsList.add(dict.get(choice)[1]);
        String[] word = dict.get(choice);
        dict.remove(choice);
        for(int j = 1; j < 5; j++) {
            int x  = r.nextInt(dict.size());       //get another random word

            definitionsList.add(dict.get(x)[1]);
            dict.remove(x);

        }
        Collections.shuffle(definitionsList);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, definitionsList);
        myListView.setAdapter(adapter);

        return word;
    }

}
