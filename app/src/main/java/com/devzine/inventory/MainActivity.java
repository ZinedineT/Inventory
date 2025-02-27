package com.devzine.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnGerencia = findViewById(R.id.btnGerencia);
        Button btnAdministracion = findViewById(R.id.btnAdministracion);
        Button btnTecnologia = findViewById(R.id.btnTecnologia);
        Button btnMarketing = findViewById(R.id.btnMarketing);

        btnGerencia.setOnClickListener(view -> abrirLista("Gerencia"));
        btnAdministracion.setOnClickListener(view -> abrirLista("Administración"));
        btnTecnologia.setOnClickListener(view -> abrirLista("Tecnología"));
        btnMarketing.setOnClickListener(view -> abrirLista("Marketing"));
    }

    private void abrirLista(String area) {
        Intent intent = new Intent(this, ListaInmueblesActivity.class);
        intent.putExtra("AREA", area);
        startActivity(intent);
    }
}
