package com.example.melih.a14011077_digitalnotebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent){
        String title=intent.getExtras().getString("title");
        MyNotification ntf=new MyNotification();
        ntf.sendNotification(context,title,"Time is up for this task");
    }
}
