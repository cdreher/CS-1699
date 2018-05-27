package assignments.cs1699.assignment2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private GoogleApiClient google;
    private Intent signInIntent;
    private boolean isSignedIn;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private final int REQ_CODE_TAKE_PICTURE = 30210; // 1-65535
    private Bitmap bmp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

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

        // Build a GoogleSignInClient with the options specified by options.
        mGoogleSignInClient = GoogleSignIn.getClient(this, options);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            Toast.makeText(getApplicationContext(), "Already signed in, welcome back!", Toast.LENGTH_SHORT).show();
            Log.e("account","account email is " + account.getEmail());
            //firebaseAuthWithGoogle(account);
            Intent intent = new Intent(getApplicationContext(), TriviaGameActivity.class);
            intent.putExtra("userSignedIn", true);
            startActivity(intent);
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        //Toast.makeText(getApplicationContext(), "app started", Toast.LENGTH_SHORT).show();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.e("currentUser","current user email is " + account.getEmail());
        }
        else{
            Log.e("currentUser","current user email is null");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 0) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        if (requestCode == REQ_CODE_TAKE_PICTURE
                && resultCode == RESULT_OK) {
            bmp = (Bitmap) data.getExtras().get("data");
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();

            //set values for user
            DatabaseReference user_child = myRef.child("users");
            user_child.child(account.getId()).child("name").setValue(account.getDisplayName());
            user_child.child(account.getId()).child("email").setValue(account.getEmail());
            user_child.child(account.getId()).child("profilePic").setValue(BitMapToString(bmp));
            user_child.child(account.getId()).child("bestScore").setValue("");
            user_child.child(account.getId()).child("scoreHistory").setValue("");



            Log.e("pic", "pic taken");
            Intent intent = new Intent(getApplicationContext(), TriviaGameActivity.class);
            intent.putExtra("userSignedIn", true);
            startActivity(intent);
        }
    }

    //Google sign in
    private void signIn() {
        signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.e("signInResult", "user signed in safely.");
            Log.e("test", "account email: " + account.getEmail());

            if(account != null){
                firebaseAuthWithGoogle(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("signInResult", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("firebase", "firebaseAuthWithGoogle:" + acct.getId());

        account = acct;

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("tag", "signInWithCredential:success");
                            //See if user is new user
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference();

                            DatabaseReference newChild = myRef.child("users");
                            newChild.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot data) {
                                    Log.e("test", "MainActivity onDataChange entered");
                                    int count = 0;
                                    for (DataSnapshot dsp : data.getChildren()) {
                                        //check if user is in firebase
                                        if(account.getEmail().equals(String.valueOf(dsp.child("email").getValue()))){
                                            Intent intent = new Intent(getApplicationContext(), TriviaGameActivity.class);
                                            intent.putExtra("userSignedIn", true);
                                            startActivity(intent);
                                        }
                                        else{
                                            count++;
                                        }
                                    }
                                    //if user is not in firebase already
                                    if(count == data.getChildrenCount()){
                                        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("tag", "signInWithCredential:failure", task.getException());
                        }
                    }
                });

    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public void onConnectionFailed(ConnectionResult connectionResult){}
    public void onConnected(Bundle bundle) {}
    public void onConnectionSuspended(int i) {}
}
