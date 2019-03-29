package com.example.theshoppingmileapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.theshoppingmileapp.R;

public class Splash extends Activity  {
    private static final long SPLASH_SCREEN_DELAY = 2000;

    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        StartAnimations();

        //Crea un controlador para iniciar la siguiente actividad después de un tiempo.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,Activity_Loguin.class);
                startActivity(intent);
                finish();
            }

        }, 5000);
    }

    //Comienza la animacion `para la pantalla de inicio

    private void StartAnimations() {

        // crear animacion
        AnimationSet animation = new AnimationSet(false);

        // cargar la animacion
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);

        //establecer la duracion de la animacion
        anim.setDuration(SPLASH_SCREEN_DELAY);

        //reestablecer la animacion
        anim.reset();

        // obtener el elemento en el que se lanza la animacion
        ImageView imag =  findViewById(R.id.location_2);

        // borrar la animacion
        imag.clearAnimation();

        // iniciar la animacion
        imag.startAnimation(anim);

        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim2.reset();
        anim2.setDuration(SPLASH_SCREEN_DELAY);

        // Establecer un desplazamiento para esperar a que comience.
        anim2.setStartOffset(1000);

        ImageView iv = findViewById(R.id.locatiion_1);
        iv.clearAnimation();
        iv.startAnimation(anim2);

        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim1.reset();
        ImageView l1 =  findViewById(R.id.location_3);

        l1.clearAnimation();
        l1.startAnimation(anim1);

        anim1.setDuration(SPLASH_SCREEN_DELAY);
        anim1.setStartOffset(1000);

        animation.addAnimation(anim);
        animation.addAnimation(anim2);
        animation.addAnimation(anim1);

    }

}
