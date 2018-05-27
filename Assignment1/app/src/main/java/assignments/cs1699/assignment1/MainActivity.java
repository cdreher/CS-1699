package assignments.cs1699.assignment1;


import android.app.ActivityManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity{

    private MediaPlayer mp;
    private Switch backgroundMusic;
    private boolean dontPlayMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        backgroundMusic = (Switch) findViewById(R.id.musicSwitch);
        mp = MediaPlayer.create(this, R.raw.background);
        dontPlayMusic = false;


    }

    @Override
    protected void onStart(){
        super.onStart();
        if(!dontPlayMusic)
            mp.start();

        //PAUSE OR PLAY BACKGROUND MUSIC
        backgroundMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean switched = backgroundMusic.isChecked();
                if(!switched){
                    if(mp.isPlaying()){
                        dontPlayMusic = true;
                        mp.pause();
                    }
                }

                else {
                    if(!mp.isPlaying()) {
                        mp.start();
                        dontPlayMusic = false;
                    }

                }
            }
        });


        //***** UNCOMMENT CODE BELOW TO DELETE scoreHistory.txt AND newTerms.txt FROM INTERNAL STORAGE ***** //

//        File dir = getFilesDir();
//        File f = new File(dir,"scoreHistory.txt");
//        boolean thing = f.delete();
//        Toast.makeText(getApplicationContext(), "score hist deleted", Toast.LENGTH_LONG).show();
//
//        File dir2 = getFilesDir();
//        File f2 = new File(dir2,"newTerms.txt");
//        boolean thing2 = f2.delete();
//        Toast.makeText(getApplicationContext(), "new terms deleted", Toast.LENGTH_LONG).show();

    }

    //WHEN BACK BUTTON IS CLICKED
    @Override
    protected void onStop(){
        super.onStop();
        if(!foregrounded()){
            mp.pause();
        }
    }

    //USE THIS FOR WHEN HOME BUTTON IS CLICKED -- START MP AGAIN
    @Override
    protected void onRestart(){
        super.onRestart();
        if(!dontPlayMusic)
            mp.start();
    }

    //USE THIS FOR WHEN BACK BUTTON IS CLICKED
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mp.stop();
    }



    //<-- OPEN OTHER ACTIVITES -->

    public void playGame(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    public void addWord(View view) {
        Intent intent = new Intent(this, AddWordActivity.class);
        startActivity(intent);
    }

    public void scoreHistory(View view) {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }



    //USED TO DETERMINE IF APP IS IN FOREGROUND OR NOT
    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
    }


}
