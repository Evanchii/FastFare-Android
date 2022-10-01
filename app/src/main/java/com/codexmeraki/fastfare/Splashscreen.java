package com.codexmeraki.fastfare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splashscreen extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splashscreen);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Handler handler = new Handler();
//        Intent intent = (mAuth.getCurrentUser() != null) ? new Intent(Splashscreen.this, Dashboard.class) : new Intent(Splashscreen.this, Login.class);
        Intent intent = sharedpreferences.contains("uid") ?
                new Intent(Splashscreen.this, MainActivity.class) :
                new Intent(Splashscreen.this, Login.class);
        handler.postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2500);
    }
}