package com.example.taller2_gabrielg_juanmendez;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Paginaprincipal extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double[] arreglo;
    double[] arreglo2;
    SupportMapFragment mapFragment;
    LatLng LatLng1;
    LatLng LatLng2;
    LatLng LatLng3;
    LatLng LatLng4;
    LatLng LatLng5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paginaprincipal);
        initLocationsJson();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double latitudeInicial = 4.6584796;
        double longitudeInicial = -74.0934579;
        LatLng mapaInicial = new LatLng(latitudeInicial, longitudeInicial);
        mMap.moveCamera(CameraUpdateFactory.newLatLng( mapaInicial));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //for(int i = 0; i < arreglo.length; i++)
        //{
            LatLng1 = new LatLng(arreglo2[0], arreglo[0]);
        //}
        mMap.addMarker(new MarkerOptions().position(LatLng1).title("Simoncho"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
    private void initLocationsJson() {
        JSONObject json = null;
        try {
            json = new JSONObject(loadJSONFromAsset());
            JSONArray locationsJsonArray = json.getJSONArray("locationsArray");
            //Log.i(TAG, "initLocationsJson: ");
            arreglo = new double[locationsJsonArray.length()];
            arreglo2 = new double[locationsJsonArray.length()];

            for(int i = 0; i<locationsJsonArray.length(); i++)
            {
                JSONObject jsonObject = locationsJsonArray.getJSONObject(i);
                double longitud = jsonObject.getDouble("longitude");
                double latitud = jsonObject.getDouble("latitude");
                arreglo[i] = longitud;
                arreglo2[i] = latitud;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String loadJSONFromAsset() {  String json = null;
        try {
            InputStream is = this.getAssets().open("locations.json");  int size = is.available();
            byte[] buffer = new byte[size];  is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();  return null;
        }
        return json;
    }

}