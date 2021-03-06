package com.example.taller2_gabrielg_juanmendez;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String userLat;
    private String userLong;
    private String availableUserLat;
    private String availableUserLong;
    private String userSearchName;
    private LatLng user;
    private LatLng userSearch;
    private String otherUserId;
    public double latitude;
    public double longitude;
    public double latitude1;
    public double longitude1;
    private Marker myMarker;
    private Marker markerotherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);

        mAuth = FirebaseAuth.getInstance();

        markerotherUser = null;
        myMarker = null;

        otherUserId = getIntent().getExtras().getString("otherUserID");

        userLat = getIntent().getExtras().getString("userLat");
        userLong = getIntent().getExtras().getString("userLong");
        availableUserLat = getIntent().getExtras().getString("availableUserLat");
        availableUserLong = getIntent().getExtras().getString("availableUserLong");
        userSearchName = getIntent().getExtras().getString("nombre");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapUser);
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

        setMyMarker();
        setOhterUserMarker();
        //actualizarDistancia();
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private void setMyMarker(){
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                latitude = snapshot.child("latitud").getValue(Double.class);
                longitude = snapshot.child("longitud").getValue(Double.class);
                user = new LatLng(latitude,longitude);
                if(myMarker != null){
                    myMarker.remove();
                }
                myMarker = mMap.addMarker(new MarkerOptions().position(user).title("Tu ubicaci??n Actual")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                //Mostrar distanci


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /*public void actualizarDistancia(){
        double distancia = distancia(latitude,longitude,latitude1,longitude1);
        String resultado = String.valueOf(distancia);
        Toast.makeText(getBaseContext(), resultado, Toast.LENGTH_SHORT).show();
    }*/
    public double distancia(double lat1, double long1, double lat2, double long2) {

        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = 6.371 * c;
        return Math.round(result*100.0)/100.0;
    }

    private void setOhterUserMarker(){
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(otherUserId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                latitude1 = snapshot.child("latitud").getValue(Double.class);
                longitude1 = snapshot.child("longitud").getValue(Double.class);
                userSearch = new LatLng(latitude1,longitude1);
                if(markerotherUser != null){
                    markerotherUser.remove();
                }
                //Mostrar distancia

                double distancia = distancia(latitude,longitude,latitude1,longitude1);
                String resultado = String.valueOf(distancia);
                Toast.makeText(getBaseContext(), resultado, Toast.LENGTH_SHORT).show();

                markerotherUser = mMap.addMarker(new MarkerOptions().position(userSearch).title("Ubicaci??n de "+userSearchName));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userSearch));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}