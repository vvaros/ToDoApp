package com.varos.todolist.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.varos.todolist.R;
import com.varos.todolist.activities.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.icon_notification)
                            .setContentTitle(intent.getStringExtra("title"))
                            .setContentText(intent.getStringExtra("description"))
                            .setSound(alarmSound);
            Intent resultIntent = new Intent(context, MainActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(context, intent.getIntExtra("id", 0), resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(intent.getIntExtra("id", 0), mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
