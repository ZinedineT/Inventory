package com.devzine.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView txtBienvenida = findViewById(R.id.txtBienvenida);

        // Animación para el logo
        Animation scaleFade = AnimationUtils.loadAnimation(this, R.anim.scale_fade);
        logo.startAnimation(scaleFade);

        //Animacion para el texto
        txtBienvenida.startAnimation(scaleFade);

        // Temporizador para mostrar el Splash Screen durante 2 segundos
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}