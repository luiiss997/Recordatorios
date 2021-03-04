package Recordatorios;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.io.Serializable;

public class Identificador implements Serializable {
    private int identificador = 0;
    private Context context;

    public Identificador(Context context) {
        this.context = context;
        cargar();
    }

    public int newIdentificador() {
        identificador++;
        guardar();
        return identificador;
    }

    public void guardar() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("ID list", identificador);
        editor.commit();
    }

    public void cargar() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        identificador = sp.getInt("ID list", -1);
    }
}
