package com.example.taller2_gabrielg_juanmendez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText mUser;
    private  EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Button buttonRegistrarse = (Button)findViewById(R.id.botonRegistrate);
        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistrate = new Intent(v.getContext(), Registrarse.class);
                startActivity(intentRegistrate);
            }
        });
        mUser = (EditText)findViewById(R.id.campoEmailLogin);
        mPassword = (EditText)findViewById(R.id.campoContraseñaLogin);
        Button botonlogin = (Button)findViewById(R.id.botonLogin);
        botonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser(mUser.getText().toString(), mPassword.getText().toString());
                Intent intent = new Intent(v.getContext(), Paginaprincipal.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            Intent intent = new Intent(getBaseContext(), Paginaprincipal.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            mUser.setText("");
            mPassword.setText("");
        }
    }
    private boolean validateForm(){
        boolean valid = true;
        String email = mUser.getText().toString();
        if(TextUtils.isEmpty(email)){
            mUser.setError("Required.");
            valid = false;
        } else if(!isEmailValid(email)){
            mUser.setError("Ingrese un correo válido");
        }else {
            mUser.setError(null);
        }
        String password = mPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Required.");
            valid = false;
        }else{
            mPassword.setError(null);
        }
        return valid;
    }
    private boolean isEmailValid(String email){
        if(!email.contains("@") || !email.contains(".") || email.length() < 5)
            return false;
        return true;
    }
    private void logInUser(String email, String password){
        if(validateForm()){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("AUTH", "logInWithEmail:success"+ task.isSuccessful());
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
                            if(!task.isSuccessful()) {
                                Log.w("AUTH", "logInWithEmail:failed", task.getException());
                                Toast.makeText(MainActivity.this, "Inicio de sesión fallido.",
                                        Toast.LENGTH_SHORT);
                                updateUI(null);
                            }
                        }
                    });
        }
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