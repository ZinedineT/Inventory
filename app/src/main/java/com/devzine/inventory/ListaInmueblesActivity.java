package com.devzine.inventory;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListaInmueblesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InmuebleAdapter adapter;
    private List<Inmueble> listaInmuebles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_inmuebles);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Datos de ejemplo
        listaInmuebles = new ArrayList<>();
        listaInmuebles.add(new Inmueble("Casa en Lima", "Av. Principal 123", 250000));
        listaInmuebles.add(new Inmueble("Departamento en Arequipa", "Calle Secundaria 456", 180000));

        // Configurar el adaptador
        adapter = new InmuebleAdapter(listaInmuebles, position -> {
            listaInmuebles.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Inmueble eliminado", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
    }
}
