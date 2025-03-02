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
import android.widget.TextView;


public class ListaInmueblesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InmuebleAdapter adapter;
    private List<Inmueble> listaInmuebles;
    private Button btnAgregarInmueble; // Botón para agregar inmueble
    private AppDatabase db;
    private InmuebleDao inmuebleDao;

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
        btnAgregarInmueble = findViewById(R.id.btnAgregarInmueble); // Vincular el botón

        // Recibir el área seleccionada desde MainActivity
        String areaSeleccionada = getIntent().getStringExtra("AREA");
        // Mostrar el área en el título
        TextView txtTitulo = findViewById(R.id.txtTitulo);
        txtTitulo.setText("Inmuebles en " + areaSeleccionada);
        db = AppDatabase.getInstance(this);
        inmuebleDao = db.inmuebleDao();
        // Datos de ejemplo
        listaInmuebles = new ArrayList<>();

        // 🔹 Cargar inmuebles desde la base de datos en segundo plano
        new Thread(() -> {
            List<Inmueble> inmueblesGuardados = inmuebleDao.obtenerInmueblesPorArea(areaSeleccionada);

            System.out.println("📌 Cargando inmuebles de " + areaSeleccionada + " desde Room:");
            listaInmuebles.clear();
            for (Inmueble inmueble : inmueblesGuardados) {
                // ✅ Si imagenUri es null, asignar un string vacío
                if (inmueble.getImagenUri() == null) {
                    inmueble.setImagenUri("");
                }
                System.out.println("Nombre: " + inmueble.getNombre());
                System.out.println("Cantidad: " + inmueble.getCantidad());
                System.out.println("Precio: " + inmueble.getPrecio());
                System.out.println("Imagen URI: " + inmueble.getImagenUri());
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
        btnAgregarInmueble.setOnClickListener(v -> {
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

            // 🔹 Imprimir datos en Logcat
            System.out.println("📌 Guardando en Room: ");
            System.out.println("Nombre: " + nombre);
            System.out.println("Cantidad: " + cantidad);
            System.out.println("Precio: " + precio);
            System.out.println("Imagen URI: " + imagenUriStr);
            System.out.println("Área: " + area);

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
