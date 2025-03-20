package com.devzine.inventory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracionImpresoraActivity extends AppCompatActivity {

    private EditText editTextIp;
    private Button btnGuardar;
    private Button btnTestConexion;
    private PrinterManager printerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_impresora);

        editTextIp = findViewById(R.id.editTextIp);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnTestConexion = findViewById(R.id.btnTestConexion);

        printerManager = new PrinterManager(this);

        // Cargar la configuración actual si existe
        if (printerManager.isPrinterConfigured()) {
            editTextIp.setText(printerManager.getPrinterIp());
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editTextIp.getText().toString().trim();
                if (!ip.isEmpty()) {
                    printerManager.setPrinterIp(ip);
                    Toast.makeText(ConfiguracionImpresoraActivity.this,
                            "Configuración guardada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConfiguracionImpresoraActivity.this,
                            "Por favor, ingrese una dirección IP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTestConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editTextIp.getText().toString().trim();
                if (!ip.isEmpty()) {
                    printerManager.setPrinterIp(ip);
                    btnTestConexion.setText("Probando...");
                    btnTestConexion.setEnabled(false);

                    printerManager.testConnection(new PrinterManager.PrinterConnectionCallback() {
                        @Override
                        public void onResult(boolean success, String message) {
                            btnTestConexion.setText("Probar Conexión");
                            btnTestConexion.setEnabled(true);
                            Toast.makeText(ConfiguracionImpresoraActivity.this,
                                    message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ConfiguracionImpresoraActivity.this,
                            "Por favor, ingrese una dirección IP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}