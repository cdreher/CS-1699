package assignments.cs1699.assignment2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.PrintStream;


/* !!   DISCLAIMER   !!


//THIS ACTIVITY IS NOT USED IN ASSIGNMENT 2 --> USED FOR ONLY REFERENCING!!!


*/

public class AddWordActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("Title...");
        Button add = (Button) dialog.findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                EditText term = (EditText) dialog.findViewById(R.id.termText);
//                EditText definition = (EditText) dialog.findViewById(R.id.defText);
//                String termString = term.getText().toString();
//                String defString = definition.getText().toString();
//
//                DatabaseReference terms = myRef.child("terms");
//                terms.child(termString).setValue(defString);

                Toast.makeText(getApplicationContext(), "Your term has been added.", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
        dialog.show();





    }
}
