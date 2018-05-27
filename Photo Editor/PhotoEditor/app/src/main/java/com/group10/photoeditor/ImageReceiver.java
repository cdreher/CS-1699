package com.group10.photoeditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class ImageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intentIn) {
        String bitmap = (String) intentIn.getExtras().get("Bitmap");
        String activity = (String) intentIn.getExtras().get("Activity");
        String fileName = (String) intentIn.getExtras().get("Filename");
        int iD = -1;
        if (intentIn.getExtras().get("ID") != null)
            iD = (Integer) intentIn.getExtras().get("ID");
        Intent logInIntent = new Intent(context, LogInActivity.class);
        logInIntent.putExtra("Activity", activity);
        logInIntent.putExtra("Filename", fileName);
        logInIntent.putExtra("Bitmap", bitmap);
        logInIntent.putExtra("ID", iD);
        context.startActivity(logInIntent);

    }


}
