package com.example.taller2_gabrielg_juanmendez;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonRegistrarse = (Button)findViewById(R.id.botonRegistrate);
        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistrate = new Intent(v.getContext(), MainActivity.class);
                startActivity(intentRegistrate);
            }
        });
    }
}