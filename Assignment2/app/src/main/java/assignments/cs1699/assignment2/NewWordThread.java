package assignments.cs1699.assignment2;

import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by collindreher on 3/26/18.
 */

public class NewWordThread extends Thread {
    String term;
    String def;
    DatabaseReference myRef;
    FirebaseDatabase database;
    SQLiteDatabase db;

    NewWordThread(String term, String def, SQLiteDatabase db){
        this.term = term;
        this.def = def;
        this.db = db;
    }

    public void run(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //add new word to firebase database
        DatabaseReference terms = myRef.child("terms");
        terms.child(term).setValue(def);

        //add new word to local SQLite database
        db.execSQL("INSERT INTO terms_table (term, definition) VALUES ('" + term
                + "', '" + def + "');");
    }
}
