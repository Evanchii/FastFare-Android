package com.codexmeraki.fastfare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    LayoutInflater inflater;
    LinearLayout parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = findViewById(R.id.main_parentView);
        View childView = inflater.inflate(R.layout.activity_dashboard, null, false);

        parentView.addView(childView);
    }
}