package com.devzine.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Temporizador para mostrar el Splash Screen durante 2.5 segundos
        new Handler().postDelayed(() -> {
            // Redirigir a MainActivity después del tiempo de espera
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cerrar la actividad actual para que no se pueda volver atrás
        }, 1500); //
    }
}