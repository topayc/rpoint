package com.tophappyworld.returnpapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tophappyworld.returnpapp.session.ReturnPSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static String TAG = "MyFirebaseMessagingService";
    public static int notiId = 1;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "title: " + remoteMessage.getData().get("title"));
            Log.d(TAG, "content: " + remoteMessage.getData().get("content"));
            handleMessage(remoteMessage.getData());
        }
    }

    private void handleMessage(Map<String,String> data) {
        Intent intent = new Intent(this, ReturnPMainActivity.class);
        intent.putExtra("pushCode" , data.get("pushCode"));
        intent.putExtra("pinNumber" , data.get("pinNumber"));
        intent.putExtra("myGiftCardNo" , data.get("myGiftCardNo"));
        intent.putExtra("title" , data.get("title"));
        intent.putExtra("giftCardIssueNo" , data.get("giftCardIssueNo"));
        intent.putExtra("link" , data.get("link"));
        intent.putExtra("content" , data.get("content"));

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >=  26 ){
            NotificationChannel mChannel = new NotificationChannel("android_gift_noti","android_gift_noti" , NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder = new NotificationCompat.Builder(this, mChannel.getId());
        }else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder
                .setSmallIcon(R.mipmap.new_ic_launcher)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("content"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notiId++, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        Log.d("ReturnPMainActivity", "New Token: " + s);
        JSONObject json = new JSONObject();
        try {
            json.put("token", s);
            json.put("os", "android");
            ReturnPMainActivity mainAct =  (ReturnPMainActivity)((ReturnpAppliaction)getApplication()).getMainActivity();

            mainAct.getSession().setData(ReturnPSession.PREF_MUST_PHSU_TOKEN_SEND, "Y");
            mainAct.getSession().setData(ReturnPSession.PREF_PUSH_TOKEN, s);
            mainAct.getSession().setData(ReturnPSession.PREF_OS, "android");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
