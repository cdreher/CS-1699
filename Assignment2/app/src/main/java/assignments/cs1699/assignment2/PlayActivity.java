package assignments.cs1699.assignment2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.Sampler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class PlayActivity extends AppCompatActivity{

    private ArrayList<String[]> dict;
    private int choice;
    private ListView myListView;
    private TextView myTextView;
    private ArrayList<String> definitionsList;
    private ArrayList<String> usedTerms;
    private ArrayAdapter adapter;
    private String[] word;
    private int player_score;
    private int progress;
    private int termCount;
    private boolean isTextToSpeech;
    private boolean myTTSisReady;
    private TextToSpeech tts;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String[] tempTerm;
    private ArrayList<String[]> tempArr;
    private SQLiteDatabase db;
    private SQLiteDatabase topTendb;
    private GoogleSignInAccount account;
    private FirebaseAuth mAuth;
    private long bestScore;
    private String timeString;
    private ArrayList<String[]> topTenScoresList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //get google account that is signed in
        mAuth = FirebaseAuth.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(this);

        usedTerms = new ArrayList<String>();            //list of terms selected for previous questions
        myListView = (ListView) findViewById(R.id.definitionLView);
        myTextView = (TextView) findViewById(R.id.term);
        progress = 0;
        player_score = 0;

        db = openOrCreateDatabase("Terms_and_Defs", MODE_PRIVATE, null);
        String query = "CREATE TABLE IF NOT EXISTS terms_table ( "
                + " id INTEGER PRIMARY KEY, "
                + " term TEXT NOT NULL, "
                + " definition TEXT NOT NULL "
                + ");";
        db.execSQL(query);

        topTendb = openOrCreateDatabase("topTenScores.db", MODE_PRIVATE, null);
        String query2 = "CREATE TABLE IF NOT EXISTS top_scores_table ( "
                + " id INTEGER PRIMARY KEY, "
                + " email TEXT NOT NULL, "
                + " score INTEGER NOT NULL "
                + ");";
        topTendb.execSQL(query2);

        word = getTerm();       //get selected term
        usedTerms.add(word[0]);     //add term to list of used terms

        Intent i = getIntent();
        isTextToSpeech = i.getExtras().getBoolean("isTextToSpeech");

        if(isTextToSpeech){
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                    }
                    myTTSisReady = true;

                    if(myTTSisReady){
                        tts.speak(word[0], TextToSpeech.QUEUE_ADD, null);
//                        Toast.makeText(getApplicationContext(), "Hello, world", Toast.LENGTH_SHORT).show();
                        for(int j = 0; j < definitionsList.size(); j++){
                            tts.speak(definitionsList.get(j), TextToSpeech.QUEUE_ADD, null);
                        }
                    }

                }
            });
        }

        //Check to see if selected answer is correct
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String myString = myListView.getItemAtPosition(i).toString();

                //Check to see if selected choice is correct
                if(word[1].equals(myString)){
                    player_score ++;
                    Toast.makeText(getApplicationContext(), "Correct Answer! Your score is: " + player_score + ".", Toast.LENGTH_SHORT).show();
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

                    //stop text-to-speech service when question is changed
                    if(tts != null && tts.isSpeaking()){
                        tts.stop();
                    }

                    if(isTextToSpeech){
                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status != TextToSpeech.ERROR) {
                                    tts.setLanguage(Locale.US);
                                }
                                myTTSisReady = true;

                                if(myTTSisReady){
                                    tts.speak(word[0], TextToSpeech.QUEUE_ADD, null);
                                    for(int j = 0; j < definitionsList.size(); j++){
                                        tts.speak(definitionsList.get(j), TextToSpeech.QUEUE_ADD, null);
                                    }
                                }

                            }
                        });
                    }

                }
                else{
                    myProgressBar.setProgress(progress);
                    player_score *= 20;        //get percentage score
                    timeString = DateFormat.getDateTimeInstance().format(new Date());        //get time of submission

                    //set values for user
                    DatabaseReference scoreHistory_child = myRef.child("users/" + account.getId() + "/scoreHistory");
                    scoreHistory_child.child(timeString).setValue(player_score);
                    scoreHistory_child.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DatabaseReference user = myRef.child("users");
                            user.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(DataSnapshot data) {
                                    Log.e("tesT", "this is test");
                                    topTendb.execSQL("DELETE FROM top_scores_table");
                                    for(DataSnapshot dsp : data.getChildren()){
                                        for(DataSnapshot score : dsp.child("scoreHistory").getChildren()){
                                            topTendb.execSQL("INSERT INTO top_scores_table (email, score) VALUES ('" + String.valueOf(dsp.child("email").getValue())
                                                    + "', '" + String.valueOf(score.getValue()) + "');");
                                        }
                                    }
                                }
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    DatabaseReference bestScore_child = myRef.child("users/" + account.getId() + "/bestScore");
                    bestScore_child.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot data) {
                            if(data.getValue().equals("")){
                                bestScore = 0;
                            }
                            else{
                                bestScore = (long)(data.getValue());
                            }
                            Log.e("bestScore", "" + bestScore);

                            if(player_score > bestScore){
                                Log.e("tag", "hello");
                                changeBestScore();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    topTenScoresList = new ArrayList<String[]>();
                    Cursor cr = topTendb.rawQuery(
                            "SELECT email, score FROM top_scores_table ORDER BY score DESC LIMIT 10", null);
                    if (cr.moveToFirst()) {
                        do {
                            String[] player = new String[2];
                            player[0] = cr.getString(cr.getColumnIndex("email"));
                            player[1] = cr.getString(cr.getColumnIndex("score"));
                            topTenScoresList.add(player);
                        } while (cr.moveToNext());
                        cr.close();
                    }
                    for(int j = 0; j < 10; j++){
                        myRef.child("topTenPlayers").child(""+ (j+1)).child("email").setValue(topTenScoresList.get(j)[0]);
                        myRef.child("topTenPlayers").child(""+ (j+1)).child("score").setValue(topTenScoresList.get(j)[1]);
                    }

                    Notification.Builder builder = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Top Ten Players Updated")
                            .setContentText("Check out the new top 10 scores now!")
                            .setAutoCancel(true)
                            .setSmallIcon(android.R.drawable.star_big_on);
                    Intent intent = new Intent(getApplicationContext(), TopTenActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    builder.setContentIntent(pendingIntent);
                    Notification notification = builder.build();

                    NotificationManager manager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(0, notification);

                    if(tts != null && tts.isSpeaking()){
                        tts.stop();
                    }
                    finish();
                }
            }
        });

//        //Go back to main activity.
//        if(progress == 100) {
//            Intent intent = new Intent(this, TriviaGameActivity.class);
//            startActivity(intent);
//        }
    }

    //Get word
    public String[] getTerm() {
        termCount = 0;      //total word count
        dict = new ArrayList<String[]>();       //create new dictionary of terms
        definitionsList = new ArrayList<String>();       //create new def. list

        dict = getData();

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

    //get data from firebase realtime DB
    public ArrayList<String[]> getData() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        tempArr = new ArrayList<String[]>();


        DatabaseReference newChild = myRef.child("terms");
        newChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                db.execSQL("DELETE FROM terms_table ");
                for (DataSnapshot dsp : data.getChildren()) {
                    db.execSQL("INSERT INTO terms_table (term, definition) VALUES ('" + String.valueOf(dsp.getKey())
                            + "', '" + String.valueOf(dsp.getValue()) + "');");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Cursor cr = db.rawQuery(
                "SELECT term, definition FROM terms_table", null);
        Log.i("test", "count " + cr.getCount());
        if (cr.moveToFirst()) {
            do {
                tempTerm = new String[2];
                tempTerm[0] = cr.getString(cr.getColumnIndex("term"));
                tempTerm[1] = cr.getString(cr.getColumnIndex("definition"));
                tempArr.add(tempTerm);
                termCount++;
            } while (cr.moveToNext());
            cr.close();
        }
        return tempArr;
    }

    //Change user's best score if necessary
    public void changeBestScore(){
        DatabaseReference jawn = myRef.child("users/" + account.getId());
        jawn.child("bestScore").setValue(player_score);
    }

}
