package com.codexmeraki.fastfare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopUp extends AppCompatActivity {

    private LinearLayout step1, step3;
    private RelativeLayout step2;
    private TabLayout.Tab tab1, tab2, tab3;
    private TabLayout tabs;
    private EditText amount;

    private OkHttpClient client = new OkHttpClient();
    private String uid;
    private SharedPreferences sp;


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

        sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        tab1 = tabs.getTabAt(0);
        tab2 = tabs.getTabAt(1);
        tab3 = tabs.getTabAt(2);

        step1 = findViewById(R.id.topup_lnrPartner);
        step2 = findViewById(R.id.topup_rltAmount);
        step3 = findViewById(R.id.topup_lnrReceipt);

        amount = findViewById(R.id.topup_eTxtAmount);
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

        Thread bgTopUp = new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody fbTopUp = new FormBody.Builder()
                        .add("uid", uid)
                        .add("partner", "7-Eleven (Cliqq)")
                        .add("amount", amount.getText().toString())
                        .build();

                Request reqTopUp = new Request.Builder()
                        .url("http://www.codexmeraki.ga/android/topUp.php")
                        .post(fbTopUp)
                        .build();

                try {
                    Response resTopUp = client.newCall(reqTopUp).execute();
                    String res = resTopUp.body().string();
                    Log.d("TopUpResponse", res);
                    JSONObject jsonRes = new JSONObject(res);
                    if(jsonRes.has("success")) {
                        //success
                    } else {
                        //error
                        Log.e("TopUp", "Something went wrong!\n"+res);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });

        bgTopUp.start();

    }
}