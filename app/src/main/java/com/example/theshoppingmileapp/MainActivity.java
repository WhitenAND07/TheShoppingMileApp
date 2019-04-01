package com.example.theshoppingmileapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Toolbar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(appbar);

        TextView textView = (TextView) findViewById(R.id.text);
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.config_option:
                Intent intent1 = new Intent(this, PreferencesFragment.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
