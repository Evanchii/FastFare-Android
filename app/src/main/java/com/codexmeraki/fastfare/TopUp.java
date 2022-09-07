package com.codexmeraki.fastfare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class TopUp extends AppCompatActivity {

    LinearLayout step1, step3;
    RelativeLayout step2;
    TabLayout.Tab tab1, tab2, tab3;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Top Up");
        setContentView(R.layout.activity_top_up);

        tabs = findViewById(R.id.topup_tabs);

        LinearLayout tabStrip = ((LinearLayout)tabs.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> true);
        }

        tab1 = tabs.getTabAt(0);
        tab2 = tabs.getTabAt(1);
        tab3 = tabs.getTabAt(2);

        step1 = findViewById(R.id.topup_lnrPartner);
        step2 = findViewById(R.id.topup_rltAmount);
        step3 = findViewById(R.id.topup_lnrReceipt);
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

    public void selectPartner(View view) {
        tab2.select();
        step1.setVisibility(View.GONE);
        step2.setVisibility(View.VISIBLE);
    }

    public void btnReturn (View view) {
        tab1.select();
        step2.setVisibility(View.GONE);
        step1.setVisibility(View.VISIBLE);
    }

    public void btnPay (View view) {
        tab3.select();
        step2.setVisibility(View.GONE);
        step3.setVisibility(View.VISIBLE);
    }

    public void btnSave (View view) {
        Toast.makeText(view.getContext(), "Barcode Saved!", Toast.LENGTH_LONG);
        finish();
    }
}