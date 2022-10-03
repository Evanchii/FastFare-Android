package com.codexmeraki.fastfare.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.codexmeraki.fastfare.R;
import com.google.android.material.textview.MaterialTextView;

public class HowToUse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_green, getTheme())));
        View view = getLayoutInflater().inflate(R.layout.item_toolbar, null);
        ((TextView)view.findViewById(R.id.toolbar_title)).setText("How to Use");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        ab.setCustomView(view, layout);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(10);

        setContentView(R.layout.activity_how_to_use);

        ListView insList = findViewById(R.id.htu_list);
        insList.setOnItemClickListener((parent, view1, position, id) ->
            startActivity(new Intent(HowToUse.this, Instructions.class)
                    .putExtra("title", ((MaterialTextView) view1).getText().toString())
                    .putExtra("position", position))
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}