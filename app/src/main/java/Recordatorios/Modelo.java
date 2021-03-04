package Recordatorios;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Luis
 */
public class Modelo extends ArrayList implements Serializable {
    private ArrayList<Recordatorio> lista = new ArrayList<>();
    private Context context;

    public Modelo() {
    }

    public Modelo(Context context) {
        this.context = context;
        cargar();
    }

    public Recordatorio get(int index) {
        return lista.get(index);
    }

    @Override
    public int size() {
        return lista.size();
    }

    public void modificarRecordatorio(Recordatorio rec, int i) {
        lista.set(i, rec);
    }

    public void addRecordatorio(Recordatorio rec) {
        if (lista == null) {
            lista = new ArrayList<>();
        }
        lista.add(rec);
    }

    public void eliminarRecordatorio(int index0) {
        lista.remove(index0);
    }

    public Recordatorio getRecordatorio(int index) {
        return lista.get(index);
    }

    public void guardar() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        edit.putString("task list", json);
        edit.apply();
    }

    public void cargar() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sp.getString("task list", null);
        Type type = new TypeToken<ArrayList<Recordatorio>>() {
        }.getType();
        lista = gson.fromJson(json, type);
        if (lista == null) {
            lista = new ArrayList<>();
        }
    }

}
