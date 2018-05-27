package com.example.saiko.testingtool;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private final String ACTION = "com.group10.photoeditor";
    private final String DESTINATION_PACKAGE = "com.group10.photoeditor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button rand = findViewById(R.id.rand);
        rand.setBackgroundColor(getResources().getColor(R.color.unselected));
        rand.setTextColor(getResources().getColor(R.color.selected));
        Button rename = findViewById(R.id.rename);
        rename.setBackgroundColor(getResources().getColor(R.color.unselected));
        rename.setTextColor(getResources().getColor(R.color.selected));
        Button lookup = findViewById(R.id.lookup);
        lookup.setBackgroundColor(getResources().getColor(R.color.unselected));
        lookup.setTextColor(getResources().getColor(R.color.selected));
        Button edit = findViewById(R.id.edit);
        edit.setBackgroundColor(getResources().getColor(R.color.unselected));
        edit.setTextColor(getResources().getColor(R.color.selected));
    }

    public void random(View view){
        Button rand = findViewById(R.id.rand);
        rand.setBackgroundColor(getResources().getColor(R.color.selected));
        rand.setTextColor(getResources().getColor(R.color.textColor));
        Intent intent = new Intent(ACTION);
        intent.setPackage(DESTINATION_PACKAGE);
        intent.putExtra("Activity", "RandomActivity");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    public void rename(View view){
        Button rename = findViewById(R.id.rename);
        rename.setBackgroundColor(getResources().getColor(R.color.selected));
        rename.setTextColor(getResources().getColor(R.color.textColor));
        Drawable d = getDrawable(R.drawable.image);
        Intent intent = new Intent(ACTION);
        intent.setPackage(DESTINATION_PACKAGE);
        intent.putExtra("Activity", "RenameActivity");
        intent.putExtra("Filename", "image");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    public void lookUp(View view){
        Button lookup = findViewById(R.id.lookup);
        lookup.setBackgroundColor(getResources().getColor(R.color.selected));
        lookup.setTextColor(getResources().getColor(R.color.textColor));
        Button rand = findViewById(R.id.rand);
        rand.setBackgroundColor(getResources().getColor(R.color.selected));
        rand.setTextColor(getResources().getColor(R.color.textColor));
        Intent intent = new Intent(ACTION);
        intent.setPackage(DESTINATION_PACKAGE);
        intent.putExtra("Activity", "LookupActivity");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    public void edit(View view){
        Button edit = findViewById(R.id.edit);
        edit.setBackgroundColor(getResources().getColor(R.color.selected));
        edit.setTextColor(getResources().getColor(R.color.textColor));
        Drawable d = getDrawable(R.drawable.image);
        Bitmap _bitmap = ((BitmapDrawable)d).getBitmap();
        ContentValues values = new ContentValues();
        values.put("bitmap", BitMapToString(_bitmap));
        Uri uri = getContentResolver().insert(
                Uri.parse("content://com.group10.photoeditor.ImageProvider/images"), values);
        if (uri != null) {
            Intent intent = new Intent(ACTION);
            intent.setPackage(DESTINATION_PACKAGE);
            intent.putExtra("Activity", "EditActivity");
            intent.putExtra("ID", Integer.parseInt(uri.getLastPathSegment()));
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(intent);
        } else Toast.makeText(this, "Content Provider returned null", Toast.LENGTH_LONG).show();
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
