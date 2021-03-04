package Recordatorios;

public class Paquete {

    private String nombre = "";
    private String fichero = "";
    private String address = "";
    private double latitud = 0.0;
    private double longitud = 0.0;

    public Paquete() {
    }

    public Paquete(String nombre, String fichero, String address, double latitud, double longitud) {
        this.nombre = nombre;
        this.fichero = fichero;
        this.address = address;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFichero() {
        return fichero;
    }

    public void setFichero(String fichero) {
        this.fichero = fichero;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
