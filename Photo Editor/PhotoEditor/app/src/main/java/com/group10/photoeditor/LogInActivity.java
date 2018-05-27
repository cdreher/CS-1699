package com.group10.photoeditor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class LogInActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 2;
    private final int SIGN_OUT = 3;
    List<AuthUI.IdpConfig> providers;
    private String activity;
    private String bitmap;
    private String fileName;
    private FirebaseUser user;
    private String userID;
    private SQLiteDatabase db;
    private String bitmapFromDatabase;
    private int iD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        bitmap = (String) getIntent().getExtras().get("Bitmap");
        activity = (String) getIntent().getExtras().get("Activity");
        fileName = (String) getIntent().getExtras().get("Filename");
        if (getIntent().getExtras().get("ID") != null)
            iD = (Integer) getIntent().getExtras().get("ID");
    }

    private void signIn() {
        if (user == null) {
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            Toast.makeText(LogInActivity.this, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();
                ImageService.userID = userID;
                Toast.makeText(LogInActivity.this, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                startIntent();
            } else
                // Sign in failed, check response for error code
                Toast.makeText(LogInActivity.this, "Unauthorized User!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == SIGN_OUT) {
            if (resultCode == RESULT_OK) {
                user = null;
                userID = null;
                ImageService.userID = null;
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(LogInActivity.this, "User has been Signed Out", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void startIntent() {
        if (activity.equals("RandomActivity")) {
            Intent intent = new Intent(this, RandomActivity.class);
            startActivity(intent);
        } else if (activity.equals("RenameActivity")) {
            Intent intent = new Intent(this, RenameActivity.class);
            intent.putExtra("Filename", fileName);
            startActivity(intent);
        } else if (activity.equals("EditActivity")) {
            Intent intent = new Intent(this, EditActivity.class);

            db = openOrCreateDatabase("Group10_Image_Table", MODE_PRIVATE, null);
            Cursor cr = db.rawQuery("SELECT _id, bitmap FROM images WHERE _id=" + iD, null);
            if (cr.moveToFirst()) {
                do {
                    bitmapFromDatabase = cr.getString(cr.getColumnIndex("bitmap")); // Use iD to get this string from database
                } while (cr.moveToNext());
                cr.close();
            }

            intent.putExtra("Image",
                    new Image(StringToBitMap(bitmapFromDatabase), fileName,
                            "", "", "", 0, 0));
            startActivity(intent);
        } else if (activity.equals("LookupActivity")) {
            Intent intent = new Intent(this, LookUpActivity.class);
            startActivity(intent);
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
