package com.example.recordatorios;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PedirPermisos extends AppCompatActivity {

    private Activity activity;
    private Context context;

    private boolean permisos;

    private final int REQUEST_CAMERA = 255;
    private final int REQUEST_READ_STORAGE = 155;
    private final int REQUEST_WRITE_STORAGE = 55;


    public PedirPermisos(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            this.pedirPermisosCamara();
            this.permisosAlmacenamiento();
        } else {
            this.setPermisos(true);
        }
    }

    public void pedirPermisosCamara() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(context, "Los permisos de la cámara son necesarios para tomar fotos",
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.CAMERA
                }, 255);
            }
        } else {
            permisos = true;
        }
    }

    public void permisosAlmacenamiento() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Los permisos son necesarios para crear recordatorios",
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 255);
            }
        } else {
            permisos = true;
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Los permisos son necesarios para crear recordatorios",
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 255);
            }
        } else {
            permisos = true;
        }
    }

    public void permisosNotificaciones(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA:
            case REQUEST_READ_STORAGE:
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisos = true;

                } else {
                    Toast.makeText(activity, "El permiso fue denegado",
                            Toast.LENGTH_SHORT).show();
                    permisos = false;
                }
            }
        }
    }

    public boolean isPermisos() {
        return permisos;
    }

    public void setPermisos(boolean permisos) {
        this.permisos = permisos;
    }

}
