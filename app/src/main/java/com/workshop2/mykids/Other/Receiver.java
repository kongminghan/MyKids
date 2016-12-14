package com.workshop2.mykids.other;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.workshop2.mykids.MainActivity;
import com.workshop2.mykids.R;

import java.util.Calendar;

import static android.app.Notification.PRIORITY_HIGH;

/**
 * Created by MingHan on 5/11/2016.
 */

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // TODO Auto-generated method stub
//        Toast.makeText(context, "WORKING", Toast.LENGTH_LONG).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.calendara)
                                .setColor(context.getResources().getColor(R.color.colorPrimary))
                                .setPriority(PRIORITY_HIGH)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSound(notification)
                                .setContentTitle("Vaccine reminder")
                                .setContentText("It's time to get your kid for "+intent.getStringExtra("title"))
                                .setAutoCancel(true);

// Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(context, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify((int)Calendar.getInstance().getTimeInMillis(), mBuilder.build());
            }
        }).start();
    }

}