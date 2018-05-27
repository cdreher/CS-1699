package assignments.cs1699.assignment1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.PrintStream;

public class AddWordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

    }

    public void addWord(View view){
        //Get text
        EditText term = (EditText) findViewById(R.id.termText);
        EditText definition = (EditText) findViewById(R.id.defText);
        String termString = term.getText().toString();
        String defString = definition.getText().toString();

        //Print to new file
        PrintStream output = null;
        try {
            output = new PrintStream(openFileOutput("newTerms.txt", Context.MODE_APPEND));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        output.println(termString + ":" + defString);
        output.close();

        Toast.makeText(getApplicationContext(), "Your term has been added.", Toast.LENGTH_SHORT).show();

        finish();
    }
}
