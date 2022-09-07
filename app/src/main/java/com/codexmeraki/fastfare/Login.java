package com.codexmeraki.fastfare;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

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
    }

    public void btnSignUp(View view) {
        startActivity(new Intent(view.getContext(), Signup.class));
    }

    public void btnLogin(View view) {
        startActivity(new Intent(view.getContext(), Dashboard.class));
        finish();
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