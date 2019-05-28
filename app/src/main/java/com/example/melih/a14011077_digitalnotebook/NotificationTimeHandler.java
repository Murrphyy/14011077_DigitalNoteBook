package com.example.melih.a14011077_digitalnotebook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class NotificationTimeHandler {
    public NotificationTimeHandler(){

    }

    public void setTimetoNotify(Context context, Calendar date,Note note){
        Intent notifyIntent = new Intent(context,MyReceiver.class);
        notifyIntent.putExtra("title",note.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context, 0, notifyIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,  date.getTimeInMillis(), pendingIntent);
    }
}
