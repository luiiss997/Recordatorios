package com.example.recordatorios;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.dynamic.IFragmentWrapper;

import Recordatorios.Modelo;
import Recordatorios.Recordatorio;

public class ReceiberAlarm extends BroadcastReceiver {
    private int ID;
    private String titulo;
    private long when;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        this.context = context;

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            reprogramReminders();
        } else {
            try {
                Bundle datos = intent.getExtras();
                this.ID = datos.getInt("ID");
                this.titulo = datos.getString("T1");
                this.when = datos.getLong("L1");

                createNotification();
            } catch (Exception ignore) {
                ignore.getCause();
            }
        }
    }

    public void createNotification() {
        String defaultChannel = "chnelrec";
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_notifications_active);

        Intent notificationIntent = new Intent(context, Activity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, defaultChannel)
                .setWhen(when)
                .setContentTitle(titulo)
                .setChannelId(defaultChannel)
                .setContentText("Toca para ver el recordatorio")
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.mipmap.recor_lauch)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibrate=pref.getBoolean("vibrate", true);

        if (!vibrate){
            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        }

        Intent resultIntent = new Intent(context, VerRecordatorio.class);
        resultIntent.putExtra("RecordatorioID", getPosition());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(VerRecordatorio.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(ID, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify((int) when, notificationBuilder.build());


        PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? power.isInteractive() : power.isScreenOn();

        if (!isScreenOn) { //¿La pantalla esta apagada?
            //La pantalla esta apagada!, se enciende.
            PowerManager.WakeLock wl = power.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.PARTIAL_WAKE_LOCK , "myApp:notificationLock");
            wl.acquire(3000);
            wl.release();
        }else{
            //La pantalla esta encendida!
        }
    }

    public int getPosition() {
        Modelo m = new Modelo(context);
        int pos = 0;
        for (int i = 0; i <= m.size(); i++) {
            if (m.get(i).getIdentificador() == ID) {
                pos = i;
                break;
            }
        }
        m.get(pos).setRealizado(true);
        m.guardar();
        return pos;
    }

    public void reprogramReminders() {
        Modelo lista = new Modelo(context);
        NotificacionRec notificacion;
        Recordatorio recordatorio;

        for (int i = 0; i <= lista.size(); i++) {
            if (!lista.getRecordatorio(i).isRealizado()) {
                recordatorio = lista.get(i);
                notificacion = new NotificacionRec(recordatorio.getNombre(), recordatorio.getIdentificador(),
                        recordatorio.getFecha().getTimeInMillis(), context);
                notificacion.crearAlarma();
            }
        }
    }

}
