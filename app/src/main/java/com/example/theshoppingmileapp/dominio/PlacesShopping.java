package com.example.theshoppingmileapp.dominio;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.theshoppingmileapp.R;
import com.example.theshoppingmileapp.adapters.Myadapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlacesShopping extends AppCompatActivity {

    private static final String URL_DATA = "https://api.myjson.com/bins/nae2i";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListPlace> listPlaces;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_shopping);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listPlaces = new ArrayList<>();
        loadRecyclerViewData();





    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loadding Data..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,

                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("Places");

                            for(int i = 0; i < array.length(); i++){
                                JSONObject o = array.getJSONObject(i);
                                ListPlace listPlace = new ListPlace(
                                        o.getString("Nombre"),
                                        o.getString("Oferta")
                                );
                                listPlaces.add(listPlace);
                            }
                            adapter = new Myadapter(listPlaces, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.getAdapter();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
