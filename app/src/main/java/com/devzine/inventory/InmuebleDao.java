package com.devzine.inventory;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InmuebleDao {

    @Insert
    void insertarInmueble(Inmueble inmueble);

    @Query("SELECT * FROM inmuebles WHERE area = :area")
    List<Inmueble> obtenerInmueblesPorArea(String area);

    @Delete
    void eliminarInmueble(Inmueble inmueble);
    @Update
    void actualizarInmueble(Inmueble inmueble);
}
