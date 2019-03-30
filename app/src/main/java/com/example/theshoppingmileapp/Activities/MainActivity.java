package com.example.theshoppingmileapp.Activities;

import android.content.Intent;
import android.view.View;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.example.theshoppingmileapp.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int[] RADIUS_METERS = {
            R.string.distance_0,
            R.string.distance_1,
            R.string.distance_2,
            R.string.distance_3,
            R.string.distance_4,
            R.string.distance_5,
    };

    private static final int[] TYPE_OF_ESTABLISHMENT = {
            R.string.establish_all,
            R.string.establish_tobaco,
            R.string.establish_theater,
            R.string.establish_sports,
            R.string.establish_Shoes,
            R.string.establish_pharmacy,
            R.string.establish_parking,
            R.string.establish_park,
            R.string.establish_nightClub,
            R.string.establish_museum,
            R.string.establish_meal,
            R.string.establish_liquor,
            R.string.establish_mall,
            R.string.establish_jewery,
            R.string.establish_hotel,
            R.string.establish_florist,
            R.string.establish_electronic,
            R.string.establish_clothing,
            R.string.establish_cityHall,
            R.string.establish_cinema,
            R.string.establish_church,
            R.string.establish_bus,
            R.string.establish_bowling,
            R.string.establish_book,
            R.string.establish_beauty_Salon,
            R.string.establish_perfumery,
    };

    private MapUtils MapUtils = new MapUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapUtils);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapUtils.mEstablish_type_Spinner = (Spinner) findViewById(R.id.establish_type_Spinner);
        MapUtils.mEstablish_type_Spinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(TYPE_OF_ESTABLISHMENT)));

        MapUtils.mRadius_meters_Spinner = (Spinner) findViewById(R.id.radius_meters_Spinner);
        MapUtils.mRadius_meters_Spinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(RADIUS_METERS)));
    }
    private String[] getResourceStrings(int[] resourceIds) {
        String[] strings = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            strings[i] = getString(resourceIds[i]);
        }
        return strings;
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
            Toast.makeText(this, "Not Implemented Yet! Keep Calm!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_map) {
            Toast.makeText(this, "Not Implemented Yet! Keep Calm!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, PreferencesFragment.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
