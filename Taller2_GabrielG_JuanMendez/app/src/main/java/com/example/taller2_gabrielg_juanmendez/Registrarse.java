package com.example.taller2_gabrielg_juanmendez;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registrarse extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser mFUser;
    private FirebaseFirestore mFstore;
    private StorageReference mSotorageRef;

    public static final String PATH_USERS = "users/";

    private EditText mUser;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mApellido;
    private EditText mNumeroIdentificacion;
    private EditText mLatitud;
    private EditText mLongitud;
    private ImageView mImageView;
    private Uri imagenUri;

    private boolean imageInclude = false;

    int IMAGE_PICKER_REQUEST = 1;
    int REQUEST_IMAGE_CAPTURE = 2;

    private int STORAGE_PERMISSION_CODE = 1;
    private int CAMERA_PERMISSION_CODE = 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private double latitude;
    private double longitude;

    String [] location_permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 410;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mSotorageRef = FirebaseStorage.getInstance().getReference();

        Button buttonRegistrarse = (Button) findViewById(R.id.botonRegistrar);
        mUserName = (EditText)findViewById(R.id.campoNombre);
        mUser = (EditText)findViewById(R.id.campoEmail);
        mApellido = (EditText)findViewById(R.id.campoApellido);
        mPassword = (EditText)findViewById(R.id.campoContraseña);
        mNumeroIdentificacion = (EditText) findViewById(R.id.campoIdentificacion);
        mLatitud = (EditText)findViewById(R.id.campoLatitud);
        mLongitud = (EditText)findViewById(R.id.campoLongitud);
        mImageView = (ImageView)findViewById(R.id.imagenPerfil);

        //Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null){
                    mLatitud.setText("Latitude: " + String.valueOf(location.getLatitude()));
                    mLongitud.setText("Longitude: " + String.valueOf(location.getLongitude()));
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
                        mLatitud.setText(String.valueOf(location.getLatitude()));
                        mLongitud.setText(String.valueOf(location.getLongitude()));
                    }else{
                        Log.i("Location", "Location is null");
                        //Active location
                    }
                }
            });
        }else{
            ActivityCompat.requestPermissions(this, location_permissions, REQUEST_LOCATION);
        }


        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    signInUser(mUser.getText().toString(), mPassword.getText().toString());
                }
            }
        });


        Button botonTomarFoto = (Button) findViewById(R.id.botonTomarFoto);
        botonTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Registrarse.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(Activity_Registrarse.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    takeImage();
                }else{
                    requestCamaraPermission();
                }
            }
        });

        Button botonCambiarImagen = (Button) findViewById(R.id.botonSeleccionarImagen);
        botonCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission( Registrarse.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(Activity_Registrarse.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                }else{
                    requestStoragePermission();
                }

            }
        });


        Button buttonCancelar = (Button)findViewById(R.id.botonCancelar);
        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intentMainLogin = new Intent(v.getContext(), MainActivity.class);
                startActivity(intentMainLogin);
            }
        });

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
    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido para el acceso a su galeria")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Registrarse.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }
    private void requestCamaraPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setTitle("Este permiso es requerido para el acceso a su camarar")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Registrarse.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }

                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
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
                                mLatitud.setText(String.valueOf(location.getLatitude()));
                                mLongitud.setText(String.valueOf(location.getLongitude()));
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

    private void takeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        mImageView.setImageBitmap(selectedImage);
                        //mImageView.setImageURI(imageUri);
                        imageInclude = true;
                        imagenUri = imageUri;

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            case 2: {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    final Uri imageUri = getImageUri(getApplicationContext(),imageBitmap);
                    imageInclude = true;
                    mImageView.setImageBitmap(imageBitmap);
                    imagenUri = imageUri;
                }
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImageToFirebase(Uri imageUri){
        StorageReference fileRef = mSotorageRef.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mImageView);
                        //Toast.makeText(Activity_Registrarse.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registrarse.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean validateForm(){
        boolean valid = true;
        String user = mUser.getText().toString();
        String nombre = mUserName.getText().toString();
        String apellido = mApellido.getText().toString();
        String contraseña = mPassword.getText().toString();
        String identificacion = mNumeroIdentificacion.getText().toString();
        String latitud = mLatitud.getText().toString();
        String longitud = mLongitud.getText().toString();
        if(imageInclude == false){
            Toast.makeText(getBaseContext(), "Debe seleccionar una foto de perfil", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(TextUtils.isEmpty(user)){
            mUserName.setError("Required");
            valid = false;
        }else{
            mUserName.setError(null);
        }
        if(TextUtils.isEmpty(nombre)){
            mUser.setError("Required");
            valid = false;
        }else{
            mUser.setError(null);
        }
        if(TextUtils.isEmpty(contraseña)){
            mPassword.setError("Required");
            valid = false;
        }else{
            mPassword.setError(null);
        }
        return valid;
    }

    private void updateUI(FirebaseUser currentUser) throws ParseException {
        if(currentUser != null){
            currentUser = mAuth.getCurrentUser();

            if(validateForm()) {
                uploadImageToFirebase(imagenUri);
                Usuario usuario = new Usuario();

                usuario.setUsername(mUserName.getText().toString());
                usuario.setName(mUser.getText().toString());
                usuario.setApellido(mApellido.getText().toString());
                usuario.setContraseña(mPassword.getText().toString());
                usuario.setNumeroIdentificacion(Long.parseLong(mNumeroIdentificacion.getText().toString()));
                usuario.setLatitud(Double.parseDouble(mLatitud.getText().toString()));
                usuario.setLongitud(Double.parseDouble(mLongitud.getText().toString()));

                myRef = database.getReference(PATH_USERS + currentUser.getUid());

                String key = myRef.push().getKey();
                usuario.setUid(key);
                Log.d("UID_GET",key);
                myRef = database.getReference(PATH_USERS + key);
                myRef.setValue(usuario);
            }
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            Toast.makeText(getBaseContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
            intent.putExtra("user", currentUser.getEmail());
            loadUsersSuscripcion();
            startActivity(intent);
        }else{
            mUserName.setText("");
            mUser.setText("");
            mApellido.setText("");
            mPassword.setText("");
            mNumeroIdentificacion.setText("");
            mLatitud.setText("");
            mLongitud.setText("");
        }
    }

    private void signInUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("AUTH", "createUserWithEmail:onComplete"+task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null){
                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(mUserName.getText().toString());
                                user.updateProfile(upcrb.build());
                                try {
                                    updateUI(user);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(!task.isSuccessful()){
                            Toast.makeText(Registrarse.this, "Falló la autenticación" + task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("", task.getException().getMessage());
                        }
                    }
                });
    }
    public void loadUsersSuscripcion(){
        myRef = database.getReference(PATH_USERS);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnahpshot : dataSnapshot.getChildren()) {
                    Usuario usuario = singleSnahpshot.getValue(Usuario.class);

                    Log.i("Suscripcion Usuarios", "Encontró usuario: " + usuario.getName());
                    String name = usuario.getName();
                    String contraseña = usuario.getContraseña();
                    //Toast.makeText(getBaseContext(), name + " /" + contraseña, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Suscripcion Usuarios", "Error en la consulta", databaseError.toException());
            }
        });
    }
}