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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListaInmueblesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InmuebleAdapter adapter;
    private List<Inmueble> listaInmuebles;
    private AppDatabase db;
    private InmuebleDao inmuebleDao;
    private FloatingActionButton fabAgregarInmueble;
    private MaterialToolbar topAppBar;
    // Definir el lanzador de actividad para recibir el resultado
    private final ActivityResultLauncher<Intent> agregarInmuebleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String nombre = data.getStringExtra("nombre");
                    int cantidad = data.getIntExtra("cantidad", 0);
                    double precio = data.getDoubleExtra("precio", 0.0);
                    String imagenUriStr = data.getStringExtra("imagenUri");
                    String area = data.getStringExtra("area");
                    String imagenUri = (imagenUriStr != null) ? imagenUriStr : "";
                    Inmueble nuevoInmueble = new Inmueble(nombre, cantidad, precio, imagenUri, area);
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
        fabAgregarInmueble = findViewById(R.id.fabAgregarInmueble); // Vincular el botón

        // Recibir el área seleccionada desde MainActivity
        String areaSeleccionada = getIntent().getStringExtra("AREA");
        // Mostrar el área en el título
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitle("Lista de inmuebles en " + areaSeleccionada);
        db = AppDatabase.getInstance(this);
        inmuebleDao = db.inmuebleDao();
        // Datos de ejemplo
        listaInmuebles = new ArrayList<>();

        // 🔹 Cargar inmuebles desde la base de datos en segundo plano
        new Thread(() -> {
            List<Inmueble> inmueblesGuardados = inmuebleDao.obtenerInmueblesPorArea(areaSeleccionada);
            listaInmuebles.clear();
            for (Inmueble inmueble : inmueblesGuardados) {
                // ✅ Si imagenUri es null, asignar un string vacío
                if (inmueble.getImagenUri() == null) {
                    inmueble.setImagenUri("");
                }
                listaInmuebles.add(inmueble);
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();


        // Configurar el adaptador
        adapter = new InmuebleAdapter(listaInmuebles, position -> {
            new Thread(() -> {
                inmuebleDao.eliminarInmueble(listaInmuebles.get(position));
                runOnUiThread(() -> {
                    listaInmuebles.remove(position);
                    adapter.notifyItemRemoved(position);
                });
            }).start();
        });

        recyclerView.setAdapter(adapter);

        // Configurar botón para agregar inmueble
        fabAgregarInmueble.setOnClickListener(v -> {
            Intent intent = new Intent(ListaInmueblesActivity.this, AgregarInmuebleActivity.class);
            intent.putExtra("AREA", areaSeleccionada);
            startActivityForResult(intent, 1);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String nombre = data.getStringExtra("nombre");
            int cantidad = data.getIntExtra("cantidad", 0);
            double precio = data.getDoubleExtra("precio", 0.0);
            String imagenUriStr = data.getStringExtra("imagenUri");
            String area = data.getStringExtra("area");

            // ✅ Verificar que imagenUri no sea null ni vacío antes de guardarlo
            String imagenUri = (imagenUriStr != null && !imagenUriStr.isEmpty()) ? imagenUriStr : "";
            Inmueble nuevoInmueble = new Inmueble(nombre, cantidad, precio, imagenUri, area);

            new Thread(() -> {
                inmuebleDao.insertarInmueble(nuevoInmueble);
                runOnUiThread(() -> {
                    listaInmuebles.add(nuevoInmueble);
                    adapter.notifyItemInserted(listaInmuebles.size() - 1);
                });
            }).start();
        }
    }
}
