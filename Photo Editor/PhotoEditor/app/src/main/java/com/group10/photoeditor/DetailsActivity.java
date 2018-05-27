package com.group10.photoeditor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {
    private static final String APP_PACKAGE_NAME = "group1.pittapi";
    private static final String BROADCAST_RECEIVER_CLASS_NAME = "group1.pittapi.SportsReceiver";
    private static final String ACTION_TRIGGER4 = "team_1.trigger_4";
    public enum team{
        PENN_STATE,
        BOSTON_COLLEGE,
        CLEMSON,
        FLORIDA_STATE,
        LOUISVILLE,
        NC_STATE,
        NOTRE_DAME,
        SYRACUSE,
        WAKE_FOREST,
        DUKE,
        GEORGIA_TECH,
        MIAMI,
        UNC,
        VIRGINIA,
        VIRGINIA_TECH,
        OTHER;
    }
    private EditText imageName;
    private TabLayout tabs;
    private RadioGroup radioGroup;
    private Image image;
    private String tag = "";
    private ImageService service;
    private boolean bound;
    public String sportingEvent = "";
    public int homeScore = 0;
    public int awayScore = 0;
    public String location = "";
    public String opponent = "";
    private int filter = 0;
    String oldName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        image = (Image) intent.getExtras().get("Image");

        imageName = (EditText) findViewById(R.id.nameText);
        EditText locationText = findViewById(R.id.locationText);
        if (image.getName() != null){
            imageName.setText(image.getName());
            locationText.setText(image.getLocation());
            selectRadioButton();
        }
        oldName = image.getName();
        filter = image.getFilter();
    }
    public void selectRadioButton(){
        if(image.getTag().equals("Sporting Event")){
            RadioButton sports = findViewById(R.id.sports);
            sports.setChecked(true);
        } else if(image.getTag().equals("Wedding")){
            RadioButton wedding = findViewById(R.id.wedding);
            wedding.setChecked(true);
        } else if(image.getTag().equals("Landmark")){
            RadioButton landmark = findViewById(R.id.landmark);
            landmark.setChecked(true);
        }else if(image.getTag().equals("Other")){
            RadioButton other = findViewById(R.id.other);
            other.setChecked(true);
        }
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.sports:
                if (checked)
                    tag = "Sporting Event";
                break;
            case R.id.wedding:
                if (checked)
                    tag = "Wedding";
                break;
            case R.id.landmark:
                if (checked)
                    tag = "Landmark";
                break;
            default:
                if (checked)
                    tag = "Other";
                break;
        }
    }
    public void saveImage(){
        if (!imageName.getText().toString().equals("")) {
            image = new Image(image.getBitmap(), imageName.getText().toString(), location, tag, sportingEvent, homeScore, awayScore);
            image.setFilter(filter);
            image.setOpponent(opponent);
            if (service.addImage(image))
                Toast.makeText(this, "Image has been saved!", Toast.LENGTH_LONG).show();
            else if (service.updateImage(oldName, image))
                Toast.makeText(this, "Image has been updated!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "ERROR: Image could not be saved!", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Image must be named!", Toast.LENGTH_LONG).show();
    }
    public void triggerOne(){
        String result = "";
        if(homeScore > awayScore)
            result = "WIN";
        else if(homeScore == awayScore)
            result = "DRAW";
        else
            result = "LOSE";
        SportEvent event = new SportEvent(sportingEvent.toUpperCase().replace(' ', '_'), opponent, result, ""+homeScore, ""+awayScore);
        String json = SportEvent.toJsonString(event);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(ACTION_TRIGGER4);
        intent.setComponent(new ComponentName(APP_PACKAGE_NAME, BROADCAST_RECEIVER_CLASS_NAME));
        intent.putExtra("info", json);

        sendBroadcast(intent);
    }
    public String setOpponent(String tempOpponent){
        String opponent = tempOpponent.trim().toUpperCase().replace(' ', '_');
        for(team t : team.values()){
            if(t.name().equals(opponent)){
                return t.name();
            }
        }
        return "OTHER";
    }
    public void save(View view){
        if(bound) {
            EditText loc = findViewById(R.id.locationText);
            location = loc.getText().toString();

            if (tag.equals("Sporting Event")) {
                //get info.
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final LayoutInflater inflater = getLayoutInflater();
                final View sportsView = inflater.inflate(R.layout.sports_dialog, null);
                final Button save = sportsView.findViewById(R.id.saveButton);
                builder.setView(sportsView);
                final Dialog dialog = builder.create();
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText event = sportsView.findViewById(R.id.event);
                        sportingEvent = event.getText().toString();
                        EditText opponentText = sportsView.findViewById(R.id.opponent);
                        opponent = setOpponent(opponentText.getText().toString());
                        EditText pittScore = sportsView.findViewById(R.id.pittscore);
                        homeScore = Integer.parseInt(pittScore.getText().toString().trim());
                        EditText other = sportsView.findViewById(R.id.otherscore);
                        awayScore = Integer.parseInt(other.getText().toString().trim());
                        dialog.dismiss();
                        saveImage();
                        triggerOne();
                    }
                });
                dialog.show();
            }
            else if(location.toUpperCase().equals("CATHY") || location.toUpperCase().equals("HILLMAN") || location.toUpperCase().equals("SENNOT"))
            {
                opponent = "";
                sportingEvent = "";
                homeScore = 0;
                awayScore = 0;
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                UserEnterEvent usrEnterEvent = new UserEnterEvent(ts,location);
                String json = UserEnterEvent.toJsonString(usrEnterEvent);

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.setAction(ACTION_TRIGGER4);
                intent.setComponent(new ComponentName(APP_PACKAGE_NAME, BROADCAST_RECEIVER_CLASS_NAME));
                intent.putExtra("info", json);
                saveImage();
                sendBroadcast(intent);
            }
            else {
                opponent = "";
                sportingEvent = "";
                homeScore = 0;
                awayScore = 0;
                saveImage();
            }
        } else {
            Toast.makeText(this, "Please wait for Backend service to load", Toast.LENGTH_LONG).show();
        }
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
