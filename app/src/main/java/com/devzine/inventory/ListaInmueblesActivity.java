package com.devzine.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
        fabAgregarInmueble = findViewById(R.id.fabAgregarInmueble);

        // Recibir el área seleccionada desde MainActivity
        String areaSeleccionada = getIntent().getStringExtra("AREA");
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setTitle("Lista de inmuebles en " + areaSeleccionada);

        // Inicializar la base de datos
        db = AppDatabase.getInstance(this);
        inmuebleDao = db.inmuebleDao();

        // Inicializar la lista de inmuebles
        listaInmuebles = new ArrayList<>();

        // Cargar inmuebles desde la base de datos en segundo plano
        new Thread(() -> {
            List<Inmueble> inmueblesGuardados = inmuebleDao.obtenerInmueblesPorArea(areaSeleccionada);
            listaInmuebles.clear();
            listaInmuebles.addAll(inmueblesGuardados);

            // Verificar si los inmuebles se cargaron correctamente
            Log.d("ListaInmuebles", "Inmuebles cargados: " + listaInmuebles.size());

            runOnUiThread(() -> {
                // Inicializar el adaptador con la lista de inmuebles
                adapter = new InmuebleAdapter(listaInmuebles, position -> {
                    new Thread(() -> {
                        inmuebleDao.eliminarInmueble(listaInmuebles.get(position));
                        runOnUiThread(() -> {
                            listaInmuebles.remove(position);
                            adapter.notifyItemRemoved(position);
                        });
                    }).start();
                });

                // Asignar el adaptador al RecyclerView
                recyclerView.setAdapter(adapter);
            });
        }).start();

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

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) { // Agregar nuevo inmueble
                String nombre = data.getStringExtra("nombre");
                int cantidad = data.getIntExtra("cantidad", 0);
                double precio = data.getDoubleExtra("precio", 0.0);
                String imagenUriStr = data.getStringExtra("imagenUri");
                String area = data.getStringExtra("area");

                Inmueble nuevoInmueble = new Inmueble(nombre, cantidad, precio, imagenUriStr, area);

                new Thread(() -> {
                    inmuebleDao.insertarInmueble(nuevoInmueble);
                    runOnUiThread(() -> {
                        listaInmuebles.add(nuevoInmueble);
                        adapter.notifyItemInserted(listaInmuebles.size() - 1);

                        // Log para verificar que el inmueble se insertó correctamente
                        Log.d("ListaInmuebles", "Inmueble insertado: " + nuevoInmueble.getNombre() + ", Área: " + nuevoInmueble.getArea());
                    });
                }).start();
            } else if (requestCode == 2) { // Editar inmueble
                int idInmueble = data.getIntExtra("ID_INMUEBLE", -1);
                String nombre = data.getStringExtra("nombre");
                int cantidad = data.getIntExtra("cantidad", 0);
                double precio = data.getDoubleExtra("precio", 0.0);
                String imagenUriStr = data.getStringExtra("imagenUri");
                String area = data.getStringExtra("area");

                Inmueble inmuebleEditado = new Inmueble(nombre, cantidad, precio, imagenUriStr, area);
                inmuebleEditado.setId(idInmueble);

                new Thread(() -> {
                    inmuebleDao.actualizarInmueble(inmuebleEditado);
                    runOnUiThread(() -> {
                        // Buscar el inmueble en la lista por su ID
                        for (int i = 0; i < listaInmuebles.size(); i++) {
                            if (listaInmuebles.get(i).getId() == idInmueble) {
                                // Actualizar el inmueble en la lista
                                listaInmuebles.set(i, inmuebleEditado);
                                // Notificar al adaptador que el ítem ha cambiado
                                adapter.notifyItemChanged(i);
                                break;
                            }
                        }
                    });
                }).start();
            }
        }
    }
}
