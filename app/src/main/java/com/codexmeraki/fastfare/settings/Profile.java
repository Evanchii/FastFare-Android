package com.codexmeraki.fastfare.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codexmeraki.fastfare.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Profile extends AppCompatActivity {

    private TextInputLayout fname, mname, lname, bday, address, cno;
    private TextView fullname, email;

    private SharedPreferences sp;
    private String uid;
    private OkHttpClient client = new OkHttpClient();
    private Toast popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_green, getTheme())));
        View view = getLayoutInflater().inflate(R.layout.item_toolbar, null);
        ((TextView)view.findViewById(R.id.toolbar_title)).setText("User Profile");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        ab.setCustomView(view, layout);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(0);

        setContentView(R.layout.activity_profile);

        fname = findViewById(R.id.profile_tilFName);
        mname = findViewById(R.id.profile_tilMName);
        lname = findViewById(R.id.profile_tilLName);
        bday = findViewById(R.id.profile_tilBDay);
        address = findViewById(R.id.profile_tilAddress);
        cno = findViewById(R.id.profile_tilCNo);

        fullname = findViewById(R.id.profile_txtName);
        email = findViewById(R.id.profile_txtEmail);

        sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        popUp = Toast.makeText(this, "", Toast.LENGTH_LONG);

        fetchAllData();
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
    
    private void fetchAllData() {
        final Thread bgFetchProfile = new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                FormBody fbProf = new FormBody.Builder()
                        .add("uid", uid)
                        .build();

                Request reqProf = new Request.Builder()
                        .url("http://codexmeraki.ga/android/fetchProfile.php")
                        .post(fbProf)
                        .build();

                try {
                    Response resProf = client.newCall(reqProf).execute();
                    JSONObject jsonProf = new JSONObject(resProf.body().string());
                    if(jsonProf.has("data")) {
                        JSONObject data = (JSONObject) jsonProf.get("data");
                        runOnUiThread(() -> {
                            try {
                                fullname.setText(data.getString("firstname")
                                        + " " + data.getString("middlename")
                                        + " " + data.getString("lastname"));
                                email.setText(data.getString("email"));

                                fname.getEditText().setText(data.getString("firstname"));
                                mname.getEditText().setText(data.getString("middlename"));
                                lname.getEditText().setText(data.getString("lastname"));
                                bday.getEditText().setText(data.getString("birthday"));
                                address.getEditText().setText(data.getString("address"));
                                cno.getEditText().setText(data.getString("contact"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        String err = jsonProf.has("error") ? jsonProf.getString("error") : "An error has occurred";
                        popUp.setText(err);
                        popUp.show();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bgFetchProfile.start();
    }

    public void updateProfile(View view) {
        final Thread updateData = new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody fbUpdate = new FormBody.Builder()
                        .add("uid", uid)
                        .add("fname", fname.getEditText().getText().toString())
                        .add("mname", mname.getEditText().getText().toString())
                        .add("lname", lname.getEditText().getText().toString())
                        .add("contact", cno.getEditText().getText().toString())
                        .add("address", address.getEditText().getText().toString())
                        .add("bday", bday.getEditText().getText().toString())
                        .build();

                Request reqUpdate = new Request.Builder()
                        .url("http://codexmeraki.ga/android/updateProfile.php")
                        .post(fbUpdate)
                        .build();

                try {
                    Response resUpdate = client.newCall(reqUpdate).execute();
                    String response = resUpdate.body().string();
                    Log.d("Response", response);
                    JSONObject data = new JSONObject(response);
                    if(data.has("success")) {
                        popUp.setText("Profile updated!");
                        finish();
                    } else {
                        String err = data.has("error") ? data.getString("error") : "An error has occurred";
                        popUp.setText(err);
                        popUp.show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        updateData.start();
    }
}