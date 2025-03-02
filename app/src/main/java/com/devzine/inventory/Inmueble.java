package com.devzine.inventory;

import android.net.Uri;

public class Inmueble {
    private String nombre;
    private int cantidad;
    private double precio;
    private Uri imagenUri;
    private String area; // NUEVO: Campo para almacenar el área

    public Inmueble(String nombre, int cantidad, double precio, Uri imagenUri, String area) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.imagenUri = imagenUri;
        this.area = area;
    }
    public String getNombre() {return nombre;}
    public int getCantidad() {return cantidad;}
    public double getPrecio() {return precio;}
    public Uri getImagenUri() {return imagenUri;}
    public String getArea() { return area; } // NUEVO: Getter del área
}
