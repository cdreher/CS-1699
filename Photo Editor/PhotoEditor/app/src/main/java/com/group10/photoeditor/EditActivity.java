package com.group10.photoeditor;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class EditActivity extends AppCompatActivity{

    private TextView textView;
    private ImageView imageView;
    private Button originalButton;
    private Button greyButton;
    private Button vividButton;
    private Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("Edit");
        image = (Image) getIntent().getSerializableExtra("Image");
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(image.getBitmap());
        textView = (TextView) findViewById(R.id.photoLabelTextView);
        textView.setText(image.getName());
        buttonSetup();
        int filter = image.getFilter();
        applyFilter(filter);
        switch (filter){
            case 0:
                setSelected(originalButton);
                break;
            case 1:
                setSelected(greyButton);
                break;
            case 2:
                setSelected(vividButton);
                break;
        }
        
    }

    public void buttonSetup() {
        originalButton = (Button) findViewById(R.id.originalButton);
        setUnselected(originalButton);
        //applyFilter(0);

        greyButton = (Button) findViewById(R.id.greyButton);
        setUnselected(greyButton);

        vividButton = (Button) findViewById(R.id.vividButton);
        setUnselected(vividButton);



        originalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelected(originalButton);
                applyFilter(0);
                setUnselected(greyButton);
                setUnselected(vividButton);
            }
        });

        greyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelected(greyButton);
                applyFilter(1);
                setUnselected(originalButton);
                setUnselected(vividButton);
            }
        });

        vividButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelected(vividButton);
                applyFilter(2);
                setUnselected(originalButton);
                setUnselected(greyButton);
            }
        });
    }

    public void setSelected(Button btn){
        btn.getBackground().setColorFilter(getResources().getColor(R.color.medium_gray), PorterDuff.Mode.MULTIPLY);
        btn.setTextColor(Color.WHITE);
    }

    public void setUnselected(Button btn){
        btn.getBackground().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.OVERLAY);
        btn.setTextColor(Color.BLACK);
    }

    public void applyFilter(int choice){
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

    public void next(View view){
        Intent i = new Intent(this, DetailsActivity.class);
        i.putExtra("Image", image);
        startActivity(i);
    }
}
