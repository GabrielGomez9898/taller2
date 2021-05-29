package com.example.taller2_gabrielg_juanmendez;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Paginaprincipal extends AppCompatActivity  implements OnMapReadyCallback  {

    //notificaciones
    private static final int NOTIFICATION_CODE = 200;
    private static final String NOTIFICATION_CHANNEL = "NOTIFICATION";
    private String currentUserId = "";
    private boolean initialState = true;
    ArrayList<Usuario> users = new ArrayList<Usuario>();

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
    //NOTIFICACIONES
    private static final int REQUEST_LOCATION_ACCESS_CODE = 100;
    private static final int LOCATION_ACCESS_CODE = 101;


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
    @RequiresApi(api = Build.VERSION_CODES.M)
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

        // Configurations

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users");
        createNotificationChannel();

        // Create listener for location in users ...
        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Save initial state of DB
                Log.i("FUNCION", "ENTRO AL LISTENER");
                if (initialState) {
                    initialState = false;
                    for (DataSnapshot singleUser : snapshot.getChildren()) {
                        Usuario user = singleUser.getValue( Usuario.class );
                        users.add(user);
                    }
                    Log.i("STATE_I", "Entered to initialize the users in the database");
                    return;
                }
                Log.i("IMPRESION", "ENTRO DESPUES DEL IF");
                // Create notification if changed user is available now
                short shouldStartLocationActivity = shouldCreateNotification(snapshot);
                Log.i("STATE:", "INDEX ... " + String.valueOf(shouldStartLocationActivity));
                if (shouldStartLocationActivity != -1) {
                    Log.i("STATE", "USER CHANGED ITS STATUS");
                    short index = shouldStartLocationActivity;
                    createNotificaion(index);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("STATE", "Notification:Error");
            }
        };
        myRef.addValueEventListener(usersListener); // Assign the listener to a database reference


    }
    /*-------------------------------------------NOTIFICACIONES-------------------------------------------*/
    private void createNotificaion(int index) {

        Log.i("SUPERTAG","ENTRO A CREAR LA NOTIFICACION");
        // Create an explicit intent for an Activity in your app
        Intent showUserLocation = new Intent(this, UserMapsActivity.class);
        showUserLocation.putExtra("otherUserID", users.get(index).getUid());
        showUserLocation.putExtra("availableUserLat", users.get(index).getLatitud());
        showUserLocation.putExtra("availableUserLong", users.get(index).getLongitud());
        showUserLocation.putExtra("nombre",users.get(index).getName());

        showUserLocation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, showUserLocation, 0);

        String notificationMessage = users.get(index).getName() + " ahora se encuentra disponible";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL);
        notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        notificationBuilder.setContentTitle("NOTIFICACION DE USUARIO");
        notificationBuilder.setColor(Color.BLUE);
        notificationBuilder.setContentText(notificationMessage);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                // Set the intent that will fire when the user taps the notification
        notificationBuilder.setContentIntent(pendingIntent);
        //notificationBuilder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0,notificationBuilder.build());

        /*
         NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL);
         builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
         builder.setContentTitle("NOTIFICACION DE USUARIO");
         builder.setContentText("nuevo usuario activado");
         builder.setColor(Color.BLUE);
         builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

         NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(0,builder.build());*/

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATION";
            String description = "NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    /**
     * Function that decides if the a notification should be shown to the user
     * @param snapshot, contains the users of the DB
     * @return index of the user that is now available, or -1 if a user changed its state to unavailable
     */
    private short shouldCreateNotification(DataSnapshot snapshot) {
        short changedUser = -1;
        ArrayList<Usuario> changedUsers = new ArrayList<Usuario>();

        // Fill changed users array
        for (DataSnapshot singleUser : snapshot.getChildren()) {
            Usuario user = singleUser.getValue( Usuario.class );
            changedUsers.add( user );
        }
        // If a new user was registered
        if (changedUsers.size() != users.size()) {
            uploadUsersArray(changedUsers);
            return (short) ((short) changedUsers.size() - 1);
        }

        // Check for users that have changed its status to available
        for (int i = 0; i < changedUsers.size(); i++) {
            // User have change its status to available
            if (!changedUsers.get( i ).getDisponible() == ( users.get(i).getDisponible() ) && changedUsers.get( i ).getDisponible() == true) {
                changedUser = (short) i;
            }
        }

        uploadUsersArray(changedUsers);

        return changedUser;
    }

    private void uploadUsersArray(ArrayList<Usuario> newUsersArray) {
        // If a user registered
        if (newUsersArray.size() != users.size()) {
            users.add(newUsersArray.get( newUsersArray.size() - 1 ));
            return;
        }
        // If a user state changed
        for (int i = 0; i < newUsersArray.size(); i++) {
            users.get(i).setDisponible(newUsersArray.get(i).getDisponible());
        }
    }



    /*-------------------------------------------FIN NOTIFICACIONES-------------------------------------------*/

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
            mAuth = FirebaseAuth.getInstance();
            myRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
            Log.i("REF", String.valueOf(myRef.toString()));
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.i("GABRIEL", String.valueOf(snapshot.child("disponible")));
                    boolean estado = snapshot.child("disponible").getValue(Boolean.class);

                    if (estado == true) {
                        Toast.makeText(getBaseContext(), "Disponibilidad desactivada", Toast.LENGTH_SHORT).show();
                        pararLocation();
                        myRef.child("disponible").setValue(false);
                    } else {
                        Toast.makeText(getBaseContext(), "Disponibilidad activada", Toast.LENGTH_SHORT).show();
                        empezarLocation();
                        myRef.child("disponible").setValue(true);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            myRef.addListenerForSingleValueEvent(postListener);
        }else if (itemClicked == R.id.menuListarUsuarios){
            mAuth = FirebaseAuth.getInstance();
            myRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
            final boolean[] aux = new boolean[1];
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean dispo = snapshot.child("disponible").getValue(Boolean.class);
                    if (dispo == true) {

                        Intent userList = new Intent(getApplicationContext(), UsuariosDisponibles.class);
                        startActivity(userList);
                    } else {
                        Toast.makeText(getBaseContext(), "Usarios visualizados disponibles", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            myRef.addListenerForSingleValueEvent(postListener);
        }else if(itemClicked == R.id.menuLogOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent( this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationController.class.getName().equals(service.service.getClassName())){
                    if(service.foreground)
                        return true;
                }
            }
            return false;
        }
        return false;
    }

    private void empezarLocation(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationController.class);
            intent.setAction(Constantes.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stated", Toast.LENGTH_SHORT).show();
        }
    }

    private void pararLocation(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationController.class);
            intent.setAction(Constantes.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

}