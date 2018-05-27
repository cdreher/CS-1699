package com.group10.photoeditor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LookUpActivity extends AppCompatActivity {

    private ImageService service;
    private boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_up);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ImageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            ImageService.ImageBinder binder = (ImageService.ImageBinder) iBinder;
            service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    public void search(View v) {
        if (bound) {
            EditText et = (EditText) findViewById(R.id.filename);
            String filename = et.getText().toString().trim();
            Image image = service.getImage(filename);
            if (image != null) {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("Image", image);
                startActivity(intent);
            } else
                Toast.makeText(this, "Image not found!",
                        Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Please wait for Backend service to load",
                    Toast.LENGTH_LONG).show();
    }


}
