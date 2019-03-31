package com.example.theshoppingmileapp.Activities;

import com.example.theshoppingmileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;


/**
 * Activity for testing {@link PlacesClient#fetchPlace(FetchPlaceRequest)}.
 */
public class PlacesAndPhotoActivity extends AppCompatActivity {

    private PlacesClient placesClient;
    private ImageView photoView;
    private TextView responseView;
    private FieldSelector fieldSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_and_photo_activity);

        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this);

        // Set up view objects
        responseView = findViewById(R.id.response);
        photoView = findViewById(R.id.photo);
        ((CheckBox) findViewById(R.id.fetch_photo_checkbox))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        PlacesAndPhotoActivity.this.setPhotoSizingEnabled(isChecked);
                    }
                });
        ((CheckBox) findViewById(R.id.use_custom_photo_reference))
                .setOnCheckedChangeListener(
                        new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                PlacesAndPhotoActivity.this.setCustomPhotoReferenceEnabled(isChecked);
                            }
                        });
        fieldSelector =
                new FieldSelector((CheckBox) findViewById(R.id.use_custom_fields),
                        (TextView) findViewById(R.id.custom_fields_list));

        // Set listeners for programmatic Fetch Place
        findViewById(R.id.fetch_place_and_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacesAndPhotoActivity.this.fetchPlace();
            }
        });

        // UI initialization
        setLoading(false);
        setPhotoSizingEnabled(false);
        setCustomPhotoReferenceEnabled(false);
    }

    /**
     * Fetches the {@link Place} specified via the UI and displays it. May also trigger {@link
     * #fetchPhoto(PhotoMetadata)} if set in the UI.
     */
    private void fetchPlace() {
        responseView.setText(null);
        photoView.setImageBitmap(null);
        dismissKeyboard((EditText) findViewById(R.id.place_id_field));

        final boolean isFetchPhotoChecked = isFetchPhotoChecked();
        List<Place.Field> placeFields = getPlaceFields();
        String customPhotoReference = getCustomPhotoReference();
        if (!validateInputs(isFetchPhotoChecked, placeFields, customPhotoReference)) {
            return;
        }

        setLoading(true);

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(getPlaceId(), placeFields);
        Task<FetchPlaceResponse> placeTask = placesClient.fetchPlace(request);

        placeTask.addOnSuccessListener(
                new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        responseView.setText(StringUtil.stringify(response, PlacesAndPhotoActivity.this.isDisplayRawResultsChecked()));
                        if (isFetchPhotoChecked) {
                            PlacesAndPhotoActivity.this.attemptFetchPhoto(response.getPlace());
                        }
                    }
                });

        placeTask.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        responseView.setText(exception.getMessage());
                    }
                });

        placeTask.addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FetchPlaceResponse> response) {
                PlacesAndPhotoActivity.this.setLoading(false);
            }
        });
    }

    private void attemptFetchPhoto(Place place) {
        List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();
        if (photoMetadatas != null && !photoMetadatas.isEmpty()) {
            fetchPhoto(photoMetadatas.get(0));
        }
    }

    /**
     * Fetches a Bitmap using the Places API and displays it.
     *
     * @param photoMetadata from a {@link Place} instance.
     */
    private void fetchPhoto(PhotoMetadata photoMetadata) {
        photoView.setImageBitmap(null);
        setLoading(true);

        String customPhotoReference = getCustomPhotoReference();
        if (!TextUtils.isEmpty(customPhotoReference)) {
            photoMetadata = PhotoMetadata.builder(customPhotoReference).build();
        }

        FetchPhotoRequest.Builder photoRequestBuilder = FetchPhotoRequest.builder(photoMetadata);

        Integer maxWidth = readIntFromTextView(R.id.photo_max_width);
        if (maxWidth != null) {
            photoRequestBuilder.setMaxWidth(maxWidth);
        }

        Integer maxHeight = readIntFromTextView(R.id.photo_max_height);
        if (maxHeight != null) {
            photoRequestBuilder.setMaxHeight(maxHeight);
        }

        Task<FetchPhotoResponse> photoTask = placesClient.fetchPhoto(photoRequestBuilder.build());

        photoTask.addOnSuccessListener(
                new OnSuccessListener<FetchPhotoResponse>() {
                    @Override
                    public void onSuccess(FetchPhotoResponse response) {
                        photoView.setImageBitmap(response.getBitmap());
                        StringUtil.prepend(responseView, StringUtil.stringify(response.getBitmap()));
                    }
                });

        photoTask.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        StringUtil.prepend(responseView, "Photo: " + exception.getMessage());
                    }
                });

        photoTask.addOnCompleteListener(new OnCompleteListener<FetchPhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<FetchPhotoResponse> response) {
                PlacesAndPhotoActivity.this.setLoading(false);
            }
        });
    }

    //////////////////////////
    // Helper methods below //
    //////////////////////////

    private void dismissKeyboard(EditText focusedEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(focusedEditText.getWindowToken(), 0);
    }

    private boolean validateInputs(
            boolean isFetchPhotoChecked, List<Field> placeFields, String customPhotoReference) {
        if (isFetchPhotoChecked) {
            if (!placeFields.contains(Field.PHOTO_METADATAS)) {
                responseView.setText(
                        "'Also fetch photo?' is selected, but PHOTO_METADATAS Place Field is not.");
                return false;
            }
        } else if (!TextUtils.isEmpty(customPhotoReference)) {
            responseView.setText(
                    "Using 'Custom photo reference', but 'Also fetch photo?' is not selected.");
            return false;
        }

        return true;
    }

    private String getPlaceId() {
        return ((TextView) findViewById(R.id.place_id_field)).getText().toString();
    }

    private List<Place.Field> getPlaceFields() {
        if (((CheckBox) findViewById(R.id.use_custom_fields)).isChecked()) {
            return fieldSelector.getSelectedFields();
        } else {
            return fieldSelector.getAllFields();
        }
    }

    private boolean isDisplayRawResultsChecked() {
        return ((CheckBox) findViewById(R.id.display_raw_results)).isChecked();
    }

    private boolean isFetchPhotoChecked() {
        return ((CheckBox) findViewById(R.id.fetch_photo_checkbox)).isChecked();
    }

    private String getCustomPhotoReference() {
        return ((TextView) findViewById(R.id.custom_photo_reference)).getText().toString();
    }

    private void setPhotoSizingEnabled(boolean enabled) {
        setEnabled(R.id.photo_max_width, enabled);
        setEnabled(R.id.photo_max_height, enabled);
    }

    private void setCustomPhotoReferenceEnabled(boolean enabled) {
        setEnabled(R.id.custom_photo_reference, enabled);
    }

    private void setEnabled(@IdRes int resId, boolean enabled) {
        TextView view = findViewById(resId);
        view.setEnabled(enabled);
        view.setText("");
    }

    @Nullable
    private Integer readIntFromTextView(@IdRes int resId) {
        Integer intValue = null;
        View view = findViewById(resId);

        if (view instanceof TextView) {
            CharSequence contents = ((TextView) view).getText();
            if (!TextUtils.isEmpty(contents)) {
                try {
                    intValue = Integer.parseInt(contents.toString());
                } catch (NumberFormatException e) {
                    showErrorAlert(R.string.error_alert_message_invalid_photo_size);
                }
            }
        }

        return intValue;
    }

    private void showErrorAlert(@StringRes int messageResId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_alert_title)
                .setMessage(messageResId)
                .show();
    }

    private void setLoading(boolean loading) {
        findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }
}
