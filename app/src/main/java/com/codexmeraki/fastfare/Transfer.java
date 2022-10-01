package com.codexmeraki.fastfare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Transfer extends AppCompatActivity {

    private TextView balance;
    private TextInputLayout parentRecipient;
    private TextInputEditText recipient, amount;

    private String uid;
    private SharedPreferences sp;
    private OkHttpClient client = new OkHttpClient();
    private Toast popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Balance Transfer");
        setContentView(R.layout.activity_transfer);

        balance = findViewById(R.id.transfer_bal);
        parentRecipient = findViewById(R.id.transfer_tilRecipient);
        recipient = findViewById(R.id.transfer_tietRecipient);
        amount = findViewById(R.id.transfer_tietAmount);

        sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        popUp = Toast.makeText(this, "", Toast.LENGTH_LONG);

        RequestBody formBody = new FormBody.Builder()
                .add("uid", uid)
                .build();

        Request request = new Request.Builder()
                .url("http://www.codexmeraki.ga/android/fetchBalance.php")
                .post(formBody)
                .build();

        @SuppressLint("SetTextI18n") final Thread thread = new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                JSONObject dataBal = new JSONObject(response.body().string());
                Log.d("Keys", String.valueOf(dataBal.keys()));

                if(dataBal.has("balance")) {
                    this.runOnUiThread(() -> {
                        try {
                            Locale locale = new Locale("en", "PH");
                            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

                            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
                            decimalFormatSymbols.setCurrencySymbol("");
                            ((DecimalFormat) currencyFormatter).setDecimalFormatSymbols(decimalFormatSymbols);

                            Log.d("RESTBalance", dataBal.getString("balance"));
                            balance.setText("Available Balance: "
                                    + currencyFormatter.format(Double.valueOf(dataBal.getString("balance"))).trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                } else {
                    String msg = dataBal.has("error") ? dataBal.get("error").toString() : "Something went wrong";
                    Log.e("Balance", msg);
                    popUp.setText(msg);
                    popUp.show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
        thread.start();
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

    public void transferBalance(View view) {
        Thread payUser = new Thread(() -> {
            RequestBody fbPay = new FormBody.Builder()
                    .add("sender", uid)
                    .add("reciever", recipient.getText().toString())
                    .add("amount", amount.getText().toString())
                    .build();

            Request reqPay = new Request.Builder()
                    .url("http://www.codexmeraki.ga/android/fetchBalance.php")
                    .post(fbPay)
                    .build();

            try {
                Response resPay = client.newCall(reqPay).execute();
                JSONObject jsonPay = new JSONObject(resPay.body().string());
                if(jsonPay.has("success")) {
                    popUp.setText(jsonPay.getString("success"));
                    popUp.show();
                    finish();
                } else {
                    String err = jsonPay.has("error") ? jsonPay.getString("error") : "An error has occurred.";
                    popUp.setText(err);
                    popUp.show();

                    this.runOnUiThread(() -> {
                        parentRecipient.setError(err);
                        parentRecipient.setErrorEnabled(true);
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });

        payUser.start();
    }
}