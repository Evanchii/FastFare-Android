package com.codexmeraki.fastfare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splashscreen);

        Handler handler = new Handler();
//        Intent intent = (mAuth.getCurrentUser() != null) ? new Intent(Splashscreen.this, Dashboard.class) : new Intent(Splashscreen.this, Login.class);
        Intent intent = new Intent(Splashscreen.this, MainActivity.class);
        handler.postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2500);
    }
}