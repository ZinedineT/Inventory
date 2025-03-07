package com.devzine.inventory;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "inmuebles")
public class Inmueble {
    @PrimaryKey(autoGenerate = true)
    private int id; // 🔹 ID único para cada inmueble
    private String nombre;
    private int codigo;
    private int cantidad;
    private double precio;
    private String imagenUri;
    private String area; // NUEVO: Campo para almacenar el área

    public Inmueble(String nombre,int codigo, int cantidad, double precio, String imagenUri, String area) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.imagenUri = imagenUri;
        this.area = area;
    }
    // 🔹 Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() {return nombre;}
    public int getCodigo() {return codigo;}
    public int getCantidad() {return cantidad;}
    public double getPrecio() {return precio;}
    public String getImagenUri() {return imagenUri;}
    public String getArea() { return area; } // NUEVO: Getter del área
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCodigo(int codigo) {this.codigo = codigo;}
    public void setCantidad(int cantidad) { this.cantidad = cantidad;}
    public void setPrecio(double precio) { this.precio = precio; }
    public void setImagenUri(String imagenUri) { this.imagenUri = imagenUri; }
    public void setArea(String area) { this.area = area; }
}
