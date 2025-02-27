package com.devzine.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private Button btnAgregarInmueble; // Botón para agregar inmueble

    // Definir el lanzador de actividad para recibir el resultado
    private final ActivityResultLauncher<Intent> agregarInmuebleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String nombre = data.getStringExtra("nombre");
                    int cantidad = data.getIntExtra("cantidad", 0);
                    double precio = data.getDoubleExtra("precio", 0.0);
                    String imagenUriStr = data.getStringExtra("imagenUri");
                    Uri imagenUri = (imagenUriStr != null) ? Uri.parse(imagenUriStr) : null;

                    // Agregar el nuevo inmueble a la lista
                    Inmueble nuevoInmueble = new Inmueble(nombre, cantidad, precio, imagenUri);
                    listaInmuebles.add(nuevoInmueble);
                    adapter.notifyItemInserted(listaInmuebles.size() - 1);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_inmuebles);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnAgregarInmueble = findViewById(R.id.btnAgregarInmueble); // Vincular el botón

        // Datos de ejemplo
        listaInmuebles = new ArrayList<>();
        listaInmuebles.add(new Inmueble("Casa en Lima", 1, 250000, null));
        listaInmuebles.add(new Inmueble("Departamento en Arequipa", 1, 180000, null));

        // Configurar el adaptador
        adapter = new InmuebleAdapter(listaInmuebles, position -> {
            listaInmuebles.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Inmueble eliminado", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        // Configurar botón para agregar inmueble
        btnAgregarInmueble.setOnClickListener(v -> {
            Intent intent = new Intent(ListaInmueblesActivity.this, AgregarInmuebleActivity.class);
            agregarInmuebleLauncher.launch(intent); // Usar ActivityResultLauncher
        });
    }
}
