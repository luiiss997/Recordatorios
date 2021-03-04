package com.example.recordatorios.Usuario;


public class UserSettings {
    private int tono;
    private boolean vibracion;
    private boolean formato;

    public UserSettings() {
    }

    public UserSettings(int tono, boolean vibracion, boolean formato) {
        this.tono = tono;
        this.vibracion = vibracion;
        this.formato = formato;
    }

    public int getTono() {
        return tono;
    }

    public void setTono(int tono) {
        this.tono = tono;
    }

    public boolean isVibracion() {
        return vibracion;
    }

    public void setVibracion(boolean vibracion) {
        this.vibracion = vibracion;
    }

    public boolean isFormato() {
        return formato;
    }

    public void setFormato(boolean formato) {
        this.formato = formato;
    }
}
