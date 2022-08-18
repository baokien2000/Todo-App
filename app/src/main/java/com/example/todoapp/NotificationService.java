package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
    Context context = (Context) this;
    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;


    @Override
    public IBinder onBind(Intent arg0) { return null; }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
//        WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder()
//                            .setInitialDelay(3, TimeUnit.SECONDS)
//                            .build();
//        WorkManager.getInstance(context).enqueue(uploadWorkRequest);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    notificationDialog(context);
//                }
//            }
//        }, 10000);   //5 seconds
//
//        Notification notification = new Notification();
//        startForeground(1, notification);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
//        stoptimertask();
        super.onDestroy();


    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 10000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            notificationDialog(context);
                        }
                    }
                });
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void notificationDialog(Context c) {
        NotificationManager notificationManager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Tutorialspoint")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("sample notification")
                .setContentText("This is sample notification")
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());
    }
}