package assignments.cs1699.assignment2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by collindreher on 3/26/18.
 */

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("New Term has been added.")
                .setContentText("Play now to find out if you know the new term!")
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_info);
        Intent i = new Intent(context, TriviaGameActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
        Toast.makeText(context, "Your term has been added.", Toast.LENGTH_SHORT).show();
    }
}
