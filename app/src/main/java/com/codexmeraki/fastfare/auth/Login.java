package com.codexmeraki.fastfare.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
import com.codexmeraki.fastfare.App;
import com.codexmeraki.fastfare.MainActivity;
import com.codexmeraki.fastfare.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    final int INTERNET_REQUEST_CODE = 200;

    String msg = "";
    Toast popUp;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceHeight = displayMetrics.heightPixels;

        LinearLayout mainLayout = findViewById(R.id.login_linearlayout);

        mainLayout.setMinimumHeight(deviceHeight);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.INTERNET}, INTERNET_REQUEST_CODE);
        }

        popUp = Toast.makeText(Login.this, msg, Toast.LENGTH_LONG);
    }

    public void restLogin (String email, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(App.APP_URL + "/android/authenticate.php")
                .post(formBody)
                .build();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String debugResponse = response.body().string();
                    Log.d("Login",debugResponse);
                    JSONObject data = new JSONObject(debugResponse);
                    if(data.has("user")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("uid", data.get("user").toString());
                        edit.putString("type", data.get("type").toString());
                        edit.putString("discount", data.get("discount").toString());
                        edit.apply();

                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else {
                        msg = data.has("error") ? data.get("error").toString() : "Something went wrong";
                        Log.d("REST", msg);
                        popUp.setText(msg);
                        popUp.show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void btnSignUp(View view) {
        startActivity(new Intent(view.getContext(), Signup.class));
    }

    public void btnLogin(View view) {
        TextInputEditText email = findViewById(R.id.login_tietEmail);
        TextInputEditText password = findViewById(R.id.login_tietPassword);

        restLogin(email.getText().toString(), password.getText().toString());
    }

    public void btnForgot(View view) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewInput = inflater.inflate(R.layout.item_forgot_password,null,false);

        TextInputEditText email = viewInput.findViewById(R.id.fp_inputEmail);

        new AlertDialog.Builder(view.getContext())
                .setView(viewInput)
                .setTitle("Forgot Password")
                .setPositiveButton("Send", (dialog, i) -> {
                    Toast.makeText(view.getContext(), "Password Reset Sent!", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }).show();
    }
}