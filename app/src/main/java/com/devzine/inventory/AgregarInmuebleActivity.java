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
    private static final int TAKE_PHOTO_REQUEST = 2; // Constante para la cámara

    private ImageView imgInmueble, imgCamara;
    private EditText edtNombre, edtCantidad, edtPrecio;
    private Uri imageUri;
    private String area;
    private Button btnSeleccionarImagen, btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_inmueble);

        imgInmueble = findViewById(R.id.imgInmueble);
        imgCamara = findViewById(R.id.imgCamara);
        edtNombre = findViewById(R.id.edtNombre);
        edtCantidad = findViewById(R.id.edtCantidad);
        edtPrecio = findViewById(R.id.edtPrecio);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = findViewById(R.id.btnGuardar);

        // ✅ ALMACENAR EL ÁREA SELECCIONADA
        area = getIntent().getStringExtra("AREA");
        // Botón para seleccionar una imagen desde la galería
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        // Icono de la cámara
        imgCamara.setOnClickListener(v -> abrirCamara()); // Listener para la cámara
        // Botón para guardar el inmueble
        btnGuardar.setOnClickListener(v -> guardarInmueble());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    getContentResolver().takePersistableUriPermission(
                            selectedImageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    imageUri = selectedImageUri; // Guardar la URI persistente
                    imgInmueble.setImageURI(imageUri);
                }
            } else if (requestCode == TAKE_PHOTO_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        imgInmueble.setImageBitmap(imageBitmap);
                        imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "Title", null));
                    }
                }
            }
        }
    }

    private void guardarInmueble() {
        if (area == null) {
            area = "Sin Área"; // 🔹 Si el área es null, asigna un valor por defecto
        }
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
        intent.putExtra("imagenUri", imageUri.toString()); // 🔹 Guardar URI como String
        intent.putExtra("area", area);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
