package com.example.todoapp;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.todoapp.model.Item;

import java.util.Calendar;

public class NotifyWorker extends Worker {
    Context context;
    public NotifyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainActivity.notificationDialog(MainActivity.AddNotes.getContext());
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    @Override
    public void onStopped() {

        super.onStopped();
    }
}
