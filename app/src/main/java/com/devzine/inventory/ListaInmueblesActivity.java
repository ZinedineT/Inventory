package com.devzine.inventory;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileOutputStream;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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
                    int codigo = data.getIntExtra("codigo", 0);
                    int cantidad = data.getIntExtra("cantidad", 0);
                    double precio = data.getDoubleExtra("precio", 0.0);
                    String imagenUriStr = data.getStringExtra("imagenUri");
                    String area = data.getStringExtra("area");
                    String imagenUri = (imagenUriStr != null) ? imagenUriStr : "";
                    Inmueble nuevoInmueble = new Inmueble(nombre,codigo, cantidad, precio, imagenUri, area);
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
                // Obtener referencia al EditText de búsqueda
                EditText searchEditText = findViewById(R.id.searchEditText);
                // Agregar TextWatcher para la búsqueda en tiempo real
                searchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.filter(s.toString());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            });
        }).start();
        // Configurar botón para agregar inmueble
        fabAgregarInmueble.setOnClickListener(v -> {
            Intent intent = new Intent(ListaInmueblesActivity.this, AgregarInmuebleActivity.class);
            intent.putExtra("AREA", areaSeleccionada);
            startActivityForResult(intent, 1);
        });
        FloatingActionButton fabGenerarReportePdf = findViewById(R.id.fabGenerarReportePdf);
        fabGenerarReportePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarReportePdf();
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) { // Agregar nuevo inmueble
                String nombre = data.getStringExtra("nombre");
                int codigo = data.getIntExtra("codigo", 0);
                int cantidad = data.getIntExtra("cantidad", 0);
                double precio = data.getDoubleExtra("precio", 0.0);
                String imagenUriStr = data.getStringExtra("imagenUri");
                String area = data.getStringExtra("area");

                Inmueble nuevoInmueble = new Inmueble(nombre,codigo , cantidad, precio, imagenUriStr, area);
                new Thread(() -> {
                    inmuebleDao.insertarInmueble(nuevoInmueble);
                    runOnUiThread(() -> {
                        listaInmuebles.add(nuevoInmueble);
                        // Actualizar la lista original en el adaptador
                        adapter.actualizarListaOriginal(listaInmuebles);
                        adapter.notifyItemInserted(listaInmuebles.size() - 1);
                        adapter.notifyDataSetChanged();
                    });
                }).start();
            } else if (requestCode == 2) { //Esto es para la edicion del inmueble
                int idInmueble = data.getIntExtra("ID_INMUEBLE", -1);
                String nombre = data.getStringExtra("nombre");
                int codigo = data.getIntExtra("codigo", 0);
                int cantidad = data.getIntExtra("cantidad", 0);
                double precio = data.getDoubleExtra("precio", 0.0);
                String imagenUriStr = data.getStringExtra("imagenUri");
                String area = data.getStringExtra("area");

                Inmueble inmuebleEditado = new Inmueble(nombre, codigo, cantidad, precio, imagenUriStr, area);
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
    private void generarReportePdf() {
        try {
            // 1. Crear el documento PDF
            Document documento = new Document();

            String fileName = "Reporte_inmuebles.pdf";
            Uri collection;
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else {
                File documentsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                File file = new File(documentsFolder, fileName);
                collection = Uri.fromFile(file);
            }

            Uri fileUri = getContentResolver().insert(collection, values);

            if (fileUri != null) {
                FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(fileUri);
                PdfWriter.getInstance(documento, outputStream);
                documento.open();

                // 2. Agregar contenido al PDF (título, información de los inmuebles)
                Paragraph titulo = new Paragraph("Reporte de Inmuebles", new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD));
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);
                // Agregar un espacio en blanco después del título
                documento.add(new Paragraph(" "));
                // Crear una tabla para los inmuebles
                PdfPTable table = new PdfPTable(5); // 5 columnas: Nombre, Código, Cantidad, Precio, Área
                table.setWidthPercentage(100);
                // Agregar encabezados de columna
                table.addCell("NOMBRE");
                table.addCell("CÓDIGO");
                table.addCell("CANTIDAD");
                table.addCell("PRECIO");
                table.addCell("ÁREA");
                // Agregar datos de los inmuebles a la tabla
                for (Inmueble inmueble : listaInmuebles) {
                    table.addCell(inmueble.getNombre());
                    table.addCell(String.valueOf(inmueble.getCodigo()));
                    table.addCell(String.valueOf(inmueble.getCantidad()));
                    table.addCell(String.valueOf(inmueble.getPrecio()));
                    table.addCell(inmueble.getArea());
                }
                documento.add(table);
                // Agregar un espacio en blanco antes del pie de página
                documento.add(new Paragraph(" "));
                // Agregar pie de página con fecha y hora
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String fechaHora = dateFormat.format(new Date());
                Paragraph piePagina = new Paragraph("Generado el " + fechaHora, new Font(Font.FontFamily.HELVETICA, 10));
                piePagina.setAlignment(Element.ALIGN_CENTER);
                documento.add(piePagina);
                // 3. Cerrar el documento
                documento.close();
                outputStream.close();

                Toast.makeText(this, "Reporte PDF generado con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear el archivo", Toast.LENGTH_SHORT).show();
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar el reporte PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
