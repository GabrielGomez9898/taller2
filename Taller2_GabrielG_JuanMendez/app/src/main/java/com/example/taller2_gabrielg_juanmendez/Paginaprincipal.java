package com.example.taller2_gabrielg_juanmendez;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Paginaprincipal extends FragmentActivity  implements OnMapReadyCallback  {
    private FirebaseAuth mAuth;
    private int STORAGE_PERMISSION_CODE = 1;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private FirebaseUser currentUser;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private double latitude;
    private double longitude;

    String [] location_permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 410;
    public static final String PATH_USERS = "users/";
    private GoogleMap mMap;
    double[] arreglo;
    double[] arreglo2;
    String[] nombre;
    SupportMapFragment mapFragment;
    LatLng LatLng1;
    LatLng LatLng2;
    LatLng LatLng3;
    LatLng LatLng4;
    LatLng LatLng5;
    LatLng Miubi;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paginaprincipal);
        initLocationsJson();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null){
                    //mLatitud.setText("Latitude: " + String.valueOf(location.getLatitude()));
                    //mLongitud.setText("Longitude: " + String.valueOf(location.getLongitude()));
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.i("perro", String.valueOf(latitude));
                    Log.i("perro", String.valueOf(longitude));
                    Miubi = new LatLng(latitude,longitude);
                    mMap.addMarker(new MarkerOptions().position(Miubi).title("Mi ubicacion"));
                    Log.i("NOTA ", "Se actualizo");
                }
            }

        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        Log.i("IMPRIMIR", String.valueOf(location.getLatitude()));
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.i("perro", String.valueOf(latitude));
                        Log.i("perro", String.valueOf(longitude));
                        Miubi = new LatLng(latitude,longitude);
                        mMap.addMarker(new MarkerOptions().position(Miubi).title("Mi ubicacion"));
                    }else{
                        Log.i("Location", "Location is null");
                        //Active location
                    }
                }
            });
        }else{
            ActivityCompat.requestPermissions(this, location_permissions, REQUEST_LOCATION);
        }
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
        LatLng2 = new LatLng(arreglo2[1], arreglo[1]);
        LatLng3 = new LatLng(arreglo2[2], arreglo[2]);
        LatLng4 = new LatLng(arreglo2[3], arreglo[3]);
        LatLng5 = new LatLng(arreglo2[4], arreglo[4]);
        //}
        mMap.addMarker(new MarkerOptions().position(LatLng1).title(nombre[0]));
        mMap.addMarker(new MarkerOptions().position(LatLng2).title(nombre[1]));
        mMap.addMarker(new MarkerOptions().position(LatLng3).title(nombre[2]));
        mMap.addMarker(new MarkerOptions().position(LatLng4).title(nombre[3]));
        mMap.addMarker(new MarkerOptions().position(LatLng5).title(nombre[4]));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addAllLocationRequests(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());*/
        return locationRequest;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisssions, @NonNull int [] grantResults){
        super.onRequestPermissionsResult(requestCode, permisssions, grantResults);
        switch(requestCode) {
            case 1:
                if (requestCode == STORAGE_PERMISSION_CODE) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("STORAGEPERMISSION", "Permiso Concedido");
                    } else {
                        Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
                    }
                }
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location permissions granted", Toast.LENGTH_SHORT).show();
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.i("IMPRIMIR", String.valueOf(location.getLatitude()));
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.i("perro", String.valueOf(latitude));
                                Log.i("perro", String.valueOf(longitude));
                                Miubi = new LatLng(latitude,longitude);
                                mMap.addMarker(new MarkerOptions().position(Miubi).title("Mi ubicacion"));
                            } else {
                                //Active Location
                            }
                        }
                    });
                }else {
                    Toast.makeText(this, "Location services were denied by the user", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void initLocationsJson() {
        JSONObject json = null;
        try {
            json = new JSONObject(loadJSONFromAsset());
            JSONArray locationsJsonArray = json.getJSONArray("locationsArray");
            //Log.i(TAG, "initLocationsJson: ");
            arreglo = new double[locationsJsonArray.length()];
            arreglo2 = new double[locationsJsonArray.length()];
            nombre = new String[locationsJsonArray.length()];
            for(int i = 0; i<locationsJsonArray.length(); i++)
            {
                JSONObject jsonObject = locationsJsonArray.getJSONObject(i);
                double longitud = jsonObject.getDouble("longitude");
                double latitud = jsonObject.getDouble("latitude");
                String nombres = jsonObject.getString("name");
                arreglo[i] = longitud;
                arreglo2[i] = latitud;
                nombre[i]= nombres;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity__navegation, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.menuDisponible){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if (itemClicked == R.id.menuListarUsuarios){
            Intent intent = new Intent( this, MainActivity.class);
            startActivity(intent);
        }else if(itemClicked == R.id.menuLogOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent( this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}