package com.example.junghyen.prototype1start_up;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        sendNotification(remoteMessage.getData().get("body"));
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, LogoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /*Request code*/, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.gach_icon)
                .setContentTitle("GACHI")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /*ID of notification */, notificationBuilder.build());
        set_alarm_badge();
    }

    public void set_alarm_badge(){
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        SharedPreferences badge = getSharedPreferences("badge", Activity.MODE_PRIVATE);
        SharedPreferences.Editor badge_editor = badge.edit();

        int badge_count = badge.getInt("badge_count",0);
        badge_count++;

        intent.putExtra("badge_count",badge_count);
        intent.putExtra("badge_count_package_name", getApplicationContext().getPackageName());
        intent.putExtra("badge_count_class_name",LogoActivity.class.getName());

        badge_editor.putInt("badge_count",badge_count);
        badge_editor.commit();

        sendBroadcast(intent);
    }
}