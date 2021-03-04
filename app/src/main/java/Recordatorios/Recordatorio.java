package Recordatorios;

import android.graphics.Color;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Luis
 */
public class Recordatorio {
    private String nombre;
    private String notas;
    private int recordar;
    private Calendar fecha = Calendar.getInstance();
    private int colorR = Color.WHITE;
    private ArrayList<String> imagenes = new ArrayList<>();
    private Paquete paquete;
    private int identificador;
    private boolean realizado = false;

    public Recordatorio() {
    }

    public Recordatorio(String nombre, String notas, Calendar c, int recordar, ArrayList imagenes,
                        Paquete paquete) {
        fecha.clear();
        this.nombre = nombre;
        this.notas = notas;
        this.fecha = c;
        this.recordar = recordar;
        this.imagenes = imagenes;
        this.paquete = paquete;
    }

    public String getAlarma(boolean format) {
        int year, day, mounth, hour, minute;
        year = fecha.get(Calendar.YEAR);
        mounth = fecha.get(Calendar.MONTH);
        day = fecha.get(Calendar.DAY_OF_MONTH);
        hour = fecha.get(Calendar.HOUR);
        minute = fecha.get(Calendar.MINUTE);
        String minuutex = "";
        if (minute < 10) {
            minuutex = "0" + minute;
        } else {
            minuutex = Integer.toString(minute);
        }
        mounth += 1;

        int dayOfWeek = fecha.get(Calendar.DAY_OF_WEEK);
        String weekday =
                new DateFormatSymbols().getShortWeekdays()[dayOfWeek];

        String st = weekday + ", " + day + "/" + mounth + "/" + year + ", ";

        if (format){
            int hourFull= fecha.get(Calendar.HOUR_OF_DAY);
            st += hourFull + ":" + minuutex;
        }else{
            String aa = "p.m.";
            if (fecha.get(Calendar.AM_PM) == 0) {
                aa = "a.m.";
            }
            st += hour + ":" + minuutex + " " + aa;
        }

        return st;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Calendar getFecha() {
        return fecha;
    }

    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    public int getRecordar() {
        return recordar;
    }

    public void setRecordar(int recordar) {
        this.recordar = recordar;
    }

    public int getColorR() {
        return colorR;
    }

    public void setColorR(int colorR) {
        this.colorR = colorR;
    }

    public ArrayList<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(ArrayList<String> imagenes) {
        this.imagenes = imagenes;
    }

    public Paquete getPaquete() {
        return paquete;
    }

    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }

    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public boolean isRealizado() {
        return realizado;
    }

    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }
}
