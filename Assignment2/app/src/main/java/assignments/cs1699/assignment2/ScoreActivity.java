package assignments.cs1699.assignment2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ScoreActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private GoogleSignInAccount account;
    private FirebaseAuth mAuth;
    private ArrayList<String> scoresList;
    private long highScore;
    private TextView myTextView;
    private ListView myListView;
    private ArrayAdapter adapter;
    private String timeStamp;
    private long score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //get google account that is signed in
        mAuth = FirebaseAuth.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(this);

        scoresList = new ArrayList<String>();     //list of scores
        highScore = 0;

        //Get score history of user from firebase realtime DB
        DatabaseReference scores = myRef.child("users/" + account.getId() + "/scoreHistory");
        scores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                for(DataSnapshot dsp : data.getChildren()){
                    timeStamp = dsp.getKey();
                    score = (long)(dsp.getValue());
                    scoresList.add(timeStamp + "\t\t\t" + score + "%");
                }

                //Populate scores ListView
                myListView = (ListView) findViewById(R.id.scoresLView);
                adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, scoresList);
                myListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //get the best score of the user from firebase
        DatabaseReference bestScore = myRef.child("users/" + account.getId() + "/bestScore");
        bestScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                highScore = (long)(data.getValue());

                //Set high score TextView
                myTextView = (TextView) findViewById(R.id.highScore);
                myTextView.setText("Highest score: \t\t\t" + highScore + "%");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void goBack (View view) {
        finish();
    }
}
