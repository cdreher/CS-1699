package assignments.cs1699.assignment2;


import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintStream;
import java.lang.Object;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TriviaGameActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private Switch backgroundMusic;
    private Switch textToSpeech;
    private boolean isTextToSpeech;
    private EditText term;
    private EditText definition;
    private SQLiteDatabase db;
    private GoogleApiClient google;
    private FirebaseAuth mAuth;
    private boolean isSignedIn;
    private Button signOutButton;
    private TextView welcomeTV;
    private ImageView photo;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private GoogleSignInAccount account;
    private Dialog dialog;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game);

        // request the user's ID, email address, and basic profile
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("53587527544-af906e7equajbo6777566vn32spgjsak.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // build API client with access to Sign-In API and options abo
        google = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .addConnectionCallbacks(this)
                .build();

        textToSpeech = (Switch) findViewById(R.id.textToSpeechSwitch);

        signOutButton = (Button) findViewById(R.id.signOutButton);

        Intent i = getIntent();
        isSignedIn = i.getExtras().getBoolean("userSignedIn");
        if(isSignedIn){
            signOutButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart(){

        //get google account that is signed in
        mAuth = FirebaseAuth.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        user = mAuth.getCurrentUser();

        welcomeTV = (TextView) findViewById(R.id.welcomeTextView);
        welcomeTV.setText("Welcome, " + account.getGivenName());

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        DatabaseReference newChild = myRef.child("users");
        newChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                for (DataSnapshot dsp : data.getChildren()) {
                    //get correct account
                    if(user.getEmail().equals(String.valueOf(dsp.child("email").getValue()))){
                        photo = (ImageView) findViewById(R.id.camera_image);
                        String str = String.valueOf(dsp.child("profilePic").getValue());
                        photo.setImageBitmap(StringToBitMap(str));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        super.onStart();


        textToSpeech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) //Line A
            {
                if(isChecked)
                    isTextToSpeech = true;
                else
                    isTextToSpeech = false;
            }
        });

    }

    //WHEN BACK BUTTON IS CLICKED
    @Override
    protected void onStop(){
        Log.e("tag", "activity has been stopped");
        super.onStop();
    }

    //USE THIS FOR WHEN HOME BUTTON IS CLICKED -- START MP AGAIN
    @Override
    protected void onRestart(){
        super.onRestart();
    }

    //USE THIS FOR WHEN BACK BUTTON IS CLICKED
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    //<-- OPEN OTHER ACTIVITES -->

    public void playGame(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("isTextToSpeech", isTextToSpeech);
        startActivity(intent);
    }

    public void addWord(View view) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("Please add a word...");
        dialog.show();

        Button add = (Button) dialog.findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = openOrCreateDatabase("Terms_and_Defs", MODE_PRIVATE, null);
                String query = "CREATE TABLE IF NOT EXISTS terms_table ( "
                        + " id INTEGER PRIMARY KEY, "
                        + " term TEXT NOT NULL, "
                        + " definition TEXT NOT NULL "
                        + ");";
                db.execSQL(query);

                EditText term = (EditText) dialog.findViewById(R.id.termText);
                EditText definition = (EditText) dialog.findViewById(R.id.defText);
                String termString = term.getText().toString();
                String defString = definition.getText().toString();

                //Sync local SQLite db on separate thread.
                NewWordThread thread = new NewWordThread(termString, defString, db);
                thread.start();

                //call broadcast receiver to notify user.
                broadcastIntent(view);
            }
        });
    }

    // broadcast a custom intent.
    public void broadcastIntent(View view){
        dialog.dismiss();

        Intent intent = new Intent();
        intent.setAction("assignments.cs1699.assignment2.CUSTOM_INTENT");
        sendBroadcast(intent);
    }

    //Open score history for user
    public void scoreHistory(View view) {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }

    //Open top ten players scores
    public void openTopTen(View v){
        Intent i = new Intent(this, TopTenActivity.class);
        startActivity(i);
    }

    //upload terms to DB from terms.txt
    //THIS WILL ONLY BE USED WHEN TERMS ARE CLEANED OUT OF DB --> BUTTON IS DISABLED AND INVISIBLE USUALLY
    public void uploadTerms(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference termsChild = myRef.child("terms");

        Scanner scan = new Scanner(getResources().openRawResource(R.raw.terms));
        String line = null;

        while (scan.hasNextLine()) {
            line = scan.nextLine();
            String[] jawn = line.split(":");
            termsChild.child(jawn[0]).setValue(jawn[1]);
        }
        scan.close();
    }

    //sign user out
    public void signOut(View v){
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(google).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.e("sign out", "sign out was: " + status);
                    }
                });
        Toast.makeText(getApplicationContext(), "Sign out complete!", Toast.LENGTH_SHORT).show();

        finish();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    //USED TO DETERMINE IF APP IS IN FOREGROUND OR NOT
    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
    }

    public void onConnectionFailed(ConnectionResult connectionResult){}
    public void onConnected(Bundle bundle) {}
    public void onConnectionSuspended(int i) {}

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
