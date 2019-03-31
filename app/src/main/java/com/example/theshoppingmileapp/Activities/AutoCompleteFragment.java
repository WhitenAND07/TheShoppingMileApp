package com.example.theshoppingmileapp.Activities;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import com.example.theshoppingmileapp.R;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

/**
 * Activity for testing Autocomplete (activity and fragment widgets, and programmatic).
 */
public class AutoCompleteFragment extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 23487;
    private PlacesClient placesClient;
    private TextView responseView;
    private FieldSelector fieldSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autocomplete_activity);

        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this);

        // Set up view objects
        responseView = findViewById(R.id.response);
        final Spinner typeFilterSpinner = findViewById(R.id.autocomplete_type_filter);
        typeFilterSpinner.setAdapter(
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, Arrays.asList(TypeFilter.values())));
        CheckBox useTypeFilterCheckBox = findViewById(R.id.autocomplete_use_type_filter);
        useTypeFilterCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        typeFilterSpinner.setEnabled(isChecked);
                    }
                });
        fieldSelector =
                new FieldSelector((CheckBox) findViewById(R.id.use_custom_fields),

                        (TextView) findViewById(R.id.custom_fields_list));

        // Setup Autocomplete Support Fragment
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        responseView.setText(
                                StringUtil.stringifyAutocompleteWidget(place, isDisplayRawResultsChecked()));
                    }

                    @Override
                    public void onError(Status status) {
                        responseView.setText(status.getStatusMessage());
                    }
                });
        findViewById(R.id.autocomplete_fragment_update_button)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                autocompleteSupportFragment.setPlaceFields(AutoCompleteFragment.this.getPlaceFields());
                                autocompleteSupportFragment.setText(AutoCompleteFragment.this.getQuery());
                                autocompleteSupportFragment.setHint(AutoCompleteFragment.this.getHint());
                                autocompleteSupportFragment.setCountry(AutoCompleteFragment.this.getCountry());
                                autocompleteSupportFragment.setLocationBias(AutoCompleteFragment.this.getLocationBias());
                                autocompleteSupportFragment.setLocationRestriction(AutoCompleteFragment.this.getLocationRestriction());
                                autocompleteSupportFragment.setTypeFilter(AutoCompleteFragment.this.getTypeFilter());
                            }
                        });

        // Set listeners for Autocomplete activity
        findViewById(R.id.autocomplete_activity_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AutoCompleteFragment.this.startAutocompleteActivity();
                    }
                });

        // Set listeners for programmatic Autocomplete
        findViewById(R.id.fetch_autocomplete_predictions_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AutoCompleteFragment.this.findAutocompletePredictions();
                    }
                });

        // UI initialization
        setLoading(false);
        typeFilterSpinner.setEnabled(false);
    }

    /**
     * Called when AutocompleteActivity finishes
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(intent);
                responseView.setText(
                        StringUtil.stringifyAutocompleteWidget(place, isDisplayRawResultsChecked()));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(intent);
                responseView.setText(status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        // Required because this class extends AppCompatActivity which extends FragmentActivity
        // which implements this method to pass onActivityResult calls to child fragments
        // (eg AutocompleteFragment).
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void startAutocompleteActivity() {
        Intent autocompleteIntent =
                new Autocomplete.IntentBuilder(getMode(), getPlaceFields())
                        .setInitialQuery(getQuery())
                        .setCountry(getCountry())
                        .setLocationBias(getLocationBias())
                        .setLocationRestriction(getLocationRestriction())
                        .setTypeFilter(getTypeFilter())
                        .build(AutoCompleteFragment.this);
        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void findAutocompletePredictions() {
        setLoading(true);

        FindAutocompletePredictionsRequest.Builder requestBuilder =
                FindAutocompletePredictionsRequest.builder()
                        .setQuery(getQuery())
                        .setCountry(getCountry())
                        .setLocationBias(getLocationBias())
                        .setLocationRestriction(getLocationRestriction())
                        .setTypeFilter(getTypeFilter());

        if (isUseSessionTokenChecked()) {
            requestBuilder.setSessionToken(AutocompleteSessionToken.newInstance());
        }

        Task<FindAutocompletePredictionsResponse> task =
                placesClient.findAutocompletePredictions(requestBuilder.build());

        task.addOnSuccessListener(
                new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse response) {
                        responseView.setText(StringUtil.stringify(response, AutoCompleteFragment.this.isDisplayRawResultsChecked()));
                    }
                });

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        responseView.setText(exception.getMessage());
                    }
                });

        task.addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> response) {
                AutoCompleteFragment.this.setLoading(false);
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

    private String getQuery() {
        return ((TextView) findViewById(R.id.autocomplete_query)).getText().toString();
    }

    private String getHint() {
        return ((TextView) findViewById(R.id.autocomplete_hint)).getText().toString();
    }

    private String getCountry() {
        return ((TextView) findViewById(R.id.autocomplete_country)).getText().toString();
    }

    @Nullable
    private LocationBias getLocationBias() {
        return getBounds(
                R.id.autocomplete_location_bias_south_west, R.id.autocomplete_location_bias_north_east);
    }

    @Nullable
    private LocationRestriction getLocationRestriction() {
        return getBounds(
                R.id.autocomplete_location_restriction_south_west,
                R.id.autocomplete_location_restriction_north_east);
    }

    @Nullable
    private RectangularBounds getBounds(int resIdSouthWest, int resIdNorthEast) {
        String southWest = ((TextView) findViewById(resIdSouthWest)).getText().toString();
        String northEast = ((TextView) findViewById(resIdNorthEast)).getText().toString();
        if (TextUtils.isEmpty(southWest) && TextUtils.isEmpty(northEast)) {
            return null;
        }

        LatLngBounds bounds = StringUtil.convertToLatLngBounds(southWest, northEast);
        if (bounds == null) {
            showErrorAlert(R.string.error_alert_message_invalid_bounds);
            return null;
        }

        return RectangularBounds.newInstance(bounds);
    }

    @Nullable
    private TypeFilter getTypeFilter() {
        Spinner typeFilter = findViewById(R.id.autocomplete_type_filter);
        return typeFilter.isEnabled() ? (TypeFilter) typeFilter.getSelectedItem() : null;
    }

    private AutocompleteActivityMode getMode() {
        boolean isOverlayMode =
                ((CheckBox) findViewById(R.id.autocomplete_activity_overlay_mode)).isChecked();
        return isOverlayMode ? AutocompleteActivityMode.OVERLAY : AutocompleteActivityMode.FULLSCREEN;
    }

    private boolean isDisplayRawResultsChecked() {
        return ((CheckBox) findViewById(R.id.display_raw_results)).isChecked();
    }

    private boolean isUseSessionTokenChecked() {
        return ((CheckBox) findViewById(R.id.autocomplete_use_session_token)).isChecked();
    }

    private void setLoading(boolean loading) {
        findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    private void showErrorAlert(@StringRes int messageResId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_alert_title)
                .setMessage(messageResId)
                .show();
    }
}