package com.devzine.inventory;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Inmueble.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InmuebleDao inmuebleDao();

    private static volatile AppDatabase instancia;

    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, "inventario_db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instancia;
    }
}
