package com.abbvmk.sathi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.screens.SplashScreen.Splash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class FirebaseMessageReceiver
        extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e("NOTIFICATION TOKEN", token);
        AuthHelper.saveNotificationToken(token);
        FirebaseMessaging
                .getInstance()
                .subscribeToTopic("notice")
                .addOnCompleteListener(task -> {
        });
    }

    @Override
    public void
    onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        Map<String, String> payload = remoteMessage.getData();
        if (payload.size() > 0) {
            createNotification(payload.get("title"), payload.get("message"));
        }
    }

    public void createNotification(String title, String message) {
        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, Splash.class);
        intent.putExtra("notice_notification", true);

        String channel_id = "default";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent
                = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setGroup(channel_id)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(channel_id, getString(R.string.title), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(channel_id, (int) (Math.random() * 100), builder.build());
    }
}
