package com.example.taller2_gabrielg_juanmendez;

import androidx.fragment.app.FragmentActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.maps.OnMapReadyCallback;

public class MapaDisponibles extends FragmentActivity implements OnMapReadyCallback {

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

    private Marker myMarker;
    private Marker markerotherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_disponibles);

        mAuth = FirebaseAuth.getInstance();

        markerotherUser = null;
        myMarker = null;

        otherUserId = getIntent().getExtras().getString("otherUserID");
        userLat = getIntent().getExtras().getString("userLat");
        userLong = getIntent().getExtras().getString("userLong");

        availableUserLat = getIntent().getExtras().getString("availableUserLat");
        availableUserLong = getIntent().getExtras().getString("availableUserLong");
        userSearchName = getIntent().getExtras().getString("nombre");
        //user = new LatLng(Double.parseDouble(userLat), Double.parseDouble(userLong));
        //userSearch = new LatLng(Double.parseDouble(availableUserLat), Double.parseDouble(availableUserLong));
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
        // Add a marker in Sydney and move the camera
        //myMarker = mMap.addMarker(new MarkerOptions().position(user).title("Tu ubicación Actual")
        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        //mMap.addMarker(new MarkerOptions().position(userSearch).title("Ubicación de "+userSearchName));

    }
    private void setMyMarker(){
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                latitude = snapshot.child("latitude").getValue(Double.class);
                longitude = snapshot.child("longitude").getValue(Double.class);
                user = new LatLng(latitude,longitude);
                if(myMarker != null){
                    myMarker.remove();
                }
                myMarker = mMap.addMarker(new MarkerOptions().position(user).title("Tu ubicación Actual")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setOhterUserMarker(){
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(otherUserId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double latitude = snapshot.child("latitude").getValue(Double.class);
                double longitude = snapshot.child("longitude").getValue(Double.class);
                userSearch = new LatLng(latitude,longitude);
                if(markerotherUser != null){
                    markerotherUser.remove();
                }

                markerotherUser = mMap.addMarker(new MarkerOptions().position(userSearch).title("Ubicación de "+userSearchName));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userSearch));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
