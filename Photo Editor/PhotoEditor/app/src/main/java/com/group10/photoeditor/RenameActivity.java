package com.group10.photoeditor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.ByteArrayOutputStream;

import static android.graphics.Color.WHITE;

public class RenameActivity extends AppCompatActivity {

    private EditText editPicName;
    private ImageService service;
    private boolean bound;
    private TabLayout tabLayout;
    private Image image;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);
        editPicName = findViewById(R.id.newPicNameEditText);
        fileName = (String)getIntent().getExtras().get("Filename");
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        myToolbar.setTitle("                  Rename Activity");
        myToolbar.setBackgroundColor(Color.parseColor("#673AB7"));
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setActionBar(myToolbar);


        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Edit"));
        tabLayout.addTab(tabLayout.newTab().setText("Rename"));
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ImageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0)
                {
                    edit(new View(getApplicationContext()));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition()==1)
                {

                }
            }
        });
    }

    public void renamePic(View v)
    {
        if(bound)
        {
            String newPicName = editPicName.getText().toString();

            if(image!=null)
            {
                image.setName(newPicName);
                if(service.updateImage(fileName,image))
                    Toast.makeText(this, "Image has been saved!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "ERROR: Image could not be saved!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "ERROR: Image could not be saved!", Toast.LENGTH_LONG).show();
            }

        }
        else
            Toast.makeText(this, "Please wait for Backend service to load",
                    Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    public void applyFilter(int choice, ImageView imageView){
        ColorMatrixColorFilter filter = null;
        if(choice == 0){
            imageView.setColorFilter(null);
        }
        else if (choice == 1){
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            filter = new ColorMatrixColorFilter(matrix);
            imageView.setColorFilter(filter);
        }
        else{
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(6);
            filter = new ColorMatrixColorFilter(matrix);
            imageView.setColorFilter(filter);
        }
        image.setFilter(choice);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            ImageService.ImageBinder binder = (ImageService.ImageBinder) iBinder;
            service = binder.getService();
            bound = true;
            image = service.getImage(fileName);
            if (image != null) {
                ImageView imageView = findViewById(R.id.photo);
                imageView.setImageBitmap(image.getBitmap());
                applyFilter(image.getFilter(),imageView);
            } else {
                Toast.makeText(getApplicationContext(), "Image not found!", Toast.LENGTH_LONG).show();
                Intent main = new Intent(RenameActivity.this, MainActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    public void edit(View view){
        if (image != null) {
            Intent i = new Intent(this, EditActivity.class);
            i.putExtra("Image", image);
            startActivity(i);
        } else
            Toast.makeText(this, "Please wait for Backend service to load",
                    Toast.LENGTH_LONG).show();
    }
}
