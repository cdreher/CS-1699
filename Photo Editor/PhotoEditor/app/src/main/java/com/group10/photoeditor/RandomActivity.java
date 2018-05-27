package com.group10.photoeditor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class RandomActivity extends AppCompatActivity {

    private ImageService service;
    private boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
    }

    public void onClick(View view) {
        if (bound) {
            Image image = service.getRandomImage();
            if (image != null) {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("Image", image);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No images found! Default being used",
                        Toast.LENGTH_LONG).show();
                Drawable d = getDrawable(R.drawable.image);
                Bitmap _bitmap = ((BitmapDrawable)d).getBitmap();
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("Image",
                        new Image(_bitmap, "image", "", "", "", 0, 0));
                startActivity(intent);
            }
        } else
            Toast.makeText(this, "Please wait for Backend service to load",
                    Toast.LENGTH_LONG).show();
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
}
