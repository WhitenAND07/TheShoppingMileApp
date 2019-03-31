package com.example.theshoppingmileapp.Activities;

import com.example.theshoppingmileapp.R;
import com.google.android.libraries.places.api.Places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_main);

        String apiKey = getString(R.string.places_api_key);

        if (apiKey.equals("")) {
            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        Button btnAutoComplete = findViewById(R.id.autocomplete_button);
        Button btnPlaceAndPhoto = findViewById(R.id.place_and_photo_button);
        Button btnCurrentPlace= findViewById(R.id.current_place_button);
        btnAutoComplete.setOnClickListener(this);
        btnPlaceAndPhoto.setOnClickListener(this);
        btnCurrentPlace.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.autocomplete_button:
                Intent intent = new Intent(this, AutoCompleteFragment.class);
                startActivity(intent);
                finish();
                break;
            case R.id.place_and_photo_button:
                Intent intent1 = new Intent(this, PlacesAndPhotoActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.current_place_button:
                Intent intent2 = new Intent(this, CurrentPlaceActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
    }

}