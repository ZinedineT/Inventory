package com.devzine.inventory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class AgregarInmuebleActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgInmueble;
    private EditText edtNombre, edtCantidad, edtPrecio;
    private Uri imageUri;
    private String area;
    private Button btnSeleccionarImagen, btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_inmueble);

        imgInmueble = findViewById(R.id.imgInmueble);
        edtNombre = findViewById(R.id.edtNombre);
        edtCantidad = findViewById(R.id.edtCantidad);
        edtPrecio = findViewById(R.id.edtPrecio);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = findViewById(R.id.btnGuardar);

        // ✅ ALMACENAR EL ÁREA SELECCIONADA
        area = getIntent().getStringExtra("AREA");
        // Botón para seleccionar una imagen desde la galería
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        // Botón para guardar el inmueble
        btnGuardar.setOnClickListener(v -> guardarInmueble(area));
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgInmueble.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void guardarInmueble(String areaSeleccionada) {
        String nombre = edtNombre.getText().toString();
        String cantidadStr = edtCantidad.getText().toString();
        String precioStr = edtPrecio.getText().toString();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        double precio = Double.parseDouble(precioStr);

        Intent intent = new Intent();
        intent.putExtra("nombre", nombre);
        intent.putExtra("cantidad", cantidad);
        intent.putExtra("precio", precio);
        intent.putExtra("imagenUri", imageUri.toString());
        intent.putExtra("area", area); // AGREGAMOS EL ÁREA

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
