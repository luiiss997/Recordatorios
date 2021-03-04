package com.example.recordatorios;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


public class NotificacionRec {
    private String titulo;
    private long when;
    private int ID;
    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager alarma;
    private Context context;

    public NotificacionRec(String titulo, int ID, long when, Context context) {
        this.titulo = titulo;
        this.when = when;
        this.ID = ID;
        this.context = context;
    }

    public NotificacionRec(Context context, int ID) {
        this.ID = ID;
        this.context = context;
    }

    public void crearAlarma() {
        intent = new Intent(context, ReceiberAlarm.class);
        intent.putExtra("T1", titulo);
        intent.putExtra("L1", when);
        intent.putExtra("ID", ID);
        pendingIntent = PendingIntent.getBroadcast(context, ID, intent, 0);
        alarma = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarma.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
    }

    public void cancelarAlarma() {
        try {
            intent = new Intent(context, ReceiberAlarm.class);
            pendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarma = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarma.cancel(pendingIntent);
            pendingIntent.cancel();
        }catch (Exception ex){
            ex.getCause();
        }
    }

    public void cancelarNotificacion(){
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch(NullPointerException error){
            error.getCause();
        }
    }

}





