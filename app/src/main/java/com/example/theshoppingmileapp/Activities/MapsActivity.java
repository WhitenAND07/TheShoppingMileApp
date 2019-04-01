package com.example.theshoppingmileapp.Activities;

import com.example.theshoppingmileapp.dominio.PlacesShopping;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.theshoppingmileapp.R;

public class MapsActivity extends AppCompatActivity implements
        OnMarkerClickListener,
        OnInfoWindowClickListener,
        OnMarkerDragListener,
        OnInfoWindowLongClickListener,
        OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener, OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener{

    private static final LatLng HOTELUNO = new LatLng(42.507798, 1.520414);

    private static final LatLng BARUNO = new LatLng(42.507906, 1.521284);

    private static final LatLng BARDOS = new LatLng(42.508364, 1.521937);

    private static final LatLng HOTELDOS = new LatLng(42.506527, 1.518345);

    private static final LatLng CENTRO_COMERCIAL = new LatLng(42.507960, 1.523737);

    private static final LatLng PERUQUERIA = new LatLng(42.508046, 1.521699);

    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>();


    /**
     * Demonstrates customizing the info window and/or its contents.
     */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            if (marker.equals(cComercial)) {
                badge = R.drawable.centro_comercial;
                mMarkerRainbow.add(cComercial);
            } else if (marker.equals(hHotelUno)) {
                badge = R.drawable.hotel_1;
                mMarkerRainbow.add(hHotelUno);
            } else if (marker.equals(hHotelDos)) {
                badge = R.drawable.hotel_2;
                mMarkerRainbow.add(hHotelDos);
            } else if (marker.equals(bBarUno)) {
                badge = R.drawable.bar_1;
                mMarkerRainbow.add(bBarUno);
            } else if (marker.equals(bBarDos)) {
                badge = R.drawable.bar_2;
                mMarkerRainbow.add(bBarDos);
            } else if (marker.equals(pPerruqueria)) {
                badge = R.drawable.perruqueria_1;
                mMarkerRainbow.add(pPerruqueria);

            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = (view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = (view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 10) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }

    private GoogleMap mMap;

    private Marker bBarDos;

    private Marker hHotelDos;

    private Marker cComercial;

    private Marker hHotelUno;

    private Marker bBarUno;

    private Marker pPerruqueria;


    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;


    private TextView mTopText;

    private final Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);

        mTopText = findViewById(R.id.top_text);

        if (mLastSelectedMarker != null && mLastSelectedMarker.isInfoWindowShown()) {
            // Refresh the info window when the info window's content has changed.
            mLastSelectedMarker.showInfoWindow();
        }


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        new OnMapAndViewReadyListener(mapFragment, this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_establish) {
            Toast.makeText(this, "Not Implemented Yet! Keep Calm!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sales) {
            Intent intent = new Intent(this, PlacesShopping.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, PreferencesFragment.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());


        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        mMap.setContentDescription("Map with lots of markers.");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(BARDOS)
                .include(HOTELDOS)
                .include(HOTELUNO)
                .include(CENTRO_COMERCIAL)
                .include(BARUNO)
                .include(PERUQUERIA)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    private void addMarkersToMap() {
        // Uses a colored icon.
        cComercial = mMap.addMarker(new MarkerOptions()
                .position(CENTRO_COMERCIAL)
                .title("Pyrenees")
                .snippet("Rebajas del: 50% hombre")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Uses a custom icon with the info window popping out of the center of the icon.
        hHotelDos = mMap.addMarker(new MarkerOptions()
                .position(HOTELDOS)
                .title("Hotel Sant jordi")
                .snippet("Dos noches : 75€"));

        // Creates a draggable marker. Long press to drag.
        bBarUno = mMap.addMarker(new MarkerOptions()
                .position(BARUNO)
                .title("japonés")
                .snippet("Menú dia: 10€"));

        // Place four markers on top of each other with differing z-indexes.
        pPerruqueria = mMap.addMarker(new MarkerOptions()
                .position(PERUQUERIA)
                .title("Li-Joan")
                .snippet("Corte hombre: 15€"));


        // A few more markers for good measure.
        bBarDos = mMap.addMarker(new MarkerOptions()
                .position(BARDOS)
                .title("Cheese's Art")
                .snippet("Desayuno : 3,5€"));

        hHotelUno = mMap.addMarker(new MarkerOptions()
                .position(HOTELUNO)
                .title("Eurostars")
                .snippet("Una noche : 50€"));


    }


    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {

        for (Marker maker : mMarkerRainbow) {
            if (marker.equals(maker)) {
                // This causes the marker at Perth to bounce into position when it is clicked.
                final Handler handler = new Handler();
                final long start = SystemClock.uptimeMillis();
                final long duration = 1500;

                final Interpolator interpolator = new BounceInterpolator();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long elapsed = SystemClock.uptimeMillis() - start;
                        float t = Math.max(
                                1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                        marker.setAnchor(0.5f, 1.0f + 2 * t);

                        if (t > 0.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                        }
                    }
                });
            } else if (marker.equals(hHotelDos)) {
                // This causes the marker at Adelaide to change color and alpha.
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360));
                marker.setAlpha(mRandom.nextFloat());
            }

        }
        mLastSelectedMarker = marker;
        return false;

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        mTopText.setText("onMarkerDragStart");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mTopText.setText("onMarkerDragEnd");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mTopText.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
    }

}