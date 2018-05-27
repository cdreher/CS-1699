package assignments.cs1699.assignment2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TopTenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private GoogleSignInAccount account;
    private ListView myListView;
    private ArrayAdapter adapter;
    private ArrayList<String> list;
    private String[] player;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //get google account that is signed in
        mAuth = FirebaseAuth.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(this);

        list = new ArrayList<String>();
        count = 1;

        //get and display top ten players from firebase db
        DatabaseReference topTen = myRef.child("topTenPlayers");
        topTen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    player = new String[2];
                    player[0] = String.valueOf(dsp.child("email").getValue());
                    player[1] = String.valueOf(dsp.child("score").getValue());
                    list.add(count + ".\t\t" + player[0] + "\t\t\t" + player[1] + "%");
                    count++;
                }
                //Populate scores ListView
                myListView = (ListView) findViewById(R.id.topTenListView);
                adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                myListView.setAdapter(adapter);
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
