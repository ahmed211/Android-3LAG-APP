package com.example.ahmed.a3lag;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mouaz on 2017-05-03.
 */

public class MyService extends android.app.Service
{

    DataBase db;
    ArrayList<String> id, time, name;
    Context context;
    public MyService(){}
    DoaData doa;
    int index, notfid=0;
    NotificationCompat.Builder notification;

    public MyService(Context context){
        this.context = context;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId)
    {
        doa = new DoaData();
        db= new DataBase(this);
        id = new ArrayList<>();
        time = new ArrayList<>();
        name = new ArrayList<>();
        notification = new NotificationCompat.Builder(getApplicationContext());
        notification.setAutoCancel(true);

        getDoaData();
        check();

        return super.onStartCommand(intent, flags, startId);
    }

    public void check()
    {
        for(int i = 0; i < id.size(); i++)
        {
            String curent = time.get(i);

            java.util.Calendar c = java.util.Calendar.getInstance();
            SimpleDateFormat sdf3 = new SimpleDateFormat("HH", Locale.ENGLISH);
            int t  = Integer.parseInt(sdf3.format(c.getTime()));
            SimpleDateFormat sdf4 = new SimpleDateFormat("mm", Locale.ENGLISH);
            int yy  = Integer.parseInt(sdf4.format(c.getTime()));
            String currentTime = t + ":" + yy;

            if(currentTime.equals(curent))
            {
                index=i;
                createNotification();
                startAlert();
            }
        }
        id.clear();
        time.clear();
    }

    public void getDoaData() {
        Cursor res = db.getDoa();
        if (res.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "NOTHING FOUND", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {

            for(int i=5; i<17; i++)
            {
                if(res.getString(i)!=null)
                {
                    id.add(res.getString(0));
                    time.add(res.getString(i));
                    name.add(res.getString(2));

                }

            }
        }
    }


    public void startAlert()
    {

        Intent intent = new Intent(getApplicationContext(), MyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 234324243, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()  , pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);

    }

    public void createNotification()
    {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        notification.setSmallIcon(R.drawable.ic_stat_name)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(name.get(index))
                .setSound(soundUri)
                .setContentText("This the Time you should take Ur Drug");

        Intent intent = new Intent(getApplicationContext(), DoaDetails.class);
        intent.putExtra("id", id.get(index));
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(index, notification.build());

//        Intent i = new Intent(getApplicationContext(), DoaDetails.class);
//        i.putExtra("id", id.get(index));
//
//        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, i , PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//
//        Notification noti = new Notification.Builder(getApplicationContext())
//                .setContentTitle(name.get(index))
//                .setContentText("This the Time you should take Ur Drug")
//                .setSmallIcon(R.drawable.ic_stat_name).setContentIntent(pIntent)
//                .setAutoCancel(true)
//                .setSound(soundUri).build();
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        //noti.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        v.vibrate(500);
//        notificationManager.notify(index, noti);
    }



}
