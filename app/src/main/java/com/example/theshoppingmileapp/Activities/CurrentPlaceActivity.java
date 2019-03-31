package com.example.theshoppingmileapp.Activities;

import com.example.theshoppingmileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import android.support.annotation.RequiresPermission;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;

/**
 * Activity for testing {@link PlacesClient#findCurrentPlace(FindCurrentPlaceRequest)}.
 */
public class CurrentPlaceActivity extends AppCompatActivity {

    private PlacesClient placesClient;
    private TextView responseView;
    private FieldSelector fieldSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_place_activity);

        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this);

        // Set view objects
        List<Place.Field> placeFields =
                FieldSelector.getPlaceFields(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
        fieldSelector =
                new FieldSelector(
                        (CheckBox)findViewById(R.id.use_custom_fields),
                        (TextView) findViewById(R.id.custom_fields_list),
                        placeFields);
        responseView = findViewById(R.id.response);
        setLoading(false);

        // Set listeners for programmatic Find Current Place
        findViewById(R.id.find_current_place_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentPlaceActivity.this.findCurrentPlace();
            }
        });
    }

    /**
     * Fetches a list of {@link PlaceLikelihood} instances that represent the Places the user is
     * most
     * likely to be at currently.
     */
    private void findCurrentPlace() {
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                    this,
                    "Both ACCESS_WIFI_STATE & ACCESS_FINE_LOCATION permissions are required",
                    Toast.LENGTH_SHORT)
                    .show();
        }

        // Note that it is not possible to request a normal (non-dangerous) permission from
        // ActivityCompat.requestPermissions(), which is why the checkPermission() only checks if
        // ACCESS_FINE_LOCATION is granted. It is still possible to check whether a normal permission
        // is granted or not using ContextCompat.checkSelfPermission().
        if (checkPermission(ACCESS_FINE_LOCATION)) {
            findCurrentPlaceWithPermissions();
        }
    }

    /**
     * Fetches a list of {@link PlaceLikelihood} instances that represent the Places the user is
     * most
     * likely to be at currently.
     */
    @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE})
    private void findCurrentPlaceWithPermissions() {
        setLoading(true);

        FindCurrentPlaceRequest currentPlaceRequest =
                FindCurrentPlaceRequest.newInstance(getPlaceFields());
        Task<FindCurrentPlaceResponse> currentPlaceTask =
                placesClient.findCurrentPlace(currentPlaceRequest);

        currentPlaceTask.addOnSuccessListener(
                new OnSuccessListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onSuccess(FindCurrentPlaceResponse response) {
                        responseView.setText(StringUtil.stringify(response, CurrentPlaceActivity.this.isDisplayRawResultsChecked()));
                    }
                });

        currentPlaceTask.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        responseView.setText(exception.getMessage());
                    }
                });

        currentPlaceTask.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                CurrentPlaceActivity.this.setLoading(false);
            }
        });
    }

    //////////////////////////
    // Helper methods below //
    //////////////////////////

    private List<Place.Field> getPlaceFields() {
        if (((CheckBox) findViewById(R.id.use_custom_fields)).isChecked()) {
            return fieldSelector.getSelectedFields();
        } else {
            return fieldSelector.getAllFields();
        }
    }

    private boolean checkPermission(String permission) {
        boolean hasPermission =
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
        }
        return hasPermission;
    }

    private boolean isDisplayRawResultsChecked() {
        return ((CheckBox) findViewById(R.id.display_raw_results)).isChecked();
    }

    private void setLoading(boolean loading) {
        findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }
}
