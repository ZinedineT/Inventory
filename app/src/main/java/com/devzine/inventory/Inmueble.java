package com.devzine.inventory;

import android.net.Uri;

public class Inmueble {
    private String nombre;
    private int cantidad;
    private double precio;
    private Uri imagenUri;

    public Inmueble(String nombre, int cantidad, double precio, Uri imagenUri) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.imagenUri = null;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public Uri getImagenUri() {
        return imagenUri;
    }
}
