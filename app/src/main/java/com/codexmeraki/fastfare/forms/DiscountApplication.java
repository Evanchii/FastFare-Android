package com.codexmeraki.fastfare.forms;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codexmeraki.fastfare.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscountApplication extends AppCompatActivity {

    final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    ImageView iv;

    File id, registration;
    SharedPreferences sp;
    OkHttpClient client;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_green, getTheme())));
        View view = getLayoutInflater().inflate(R.layout.item_toolbar, null);
        ((TextView)view.findViewById(R.id.toolbar_title)).setText("Discount Application");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        ab.setCustomView(view, layout);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(10);
        setContentView(R.layout.activity_discount_application);
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

    public void submitApplication(View view) {
        Thread submit = new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody rbApplication = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id", id.getName(),
                                RequestBody.create(id, MediaType.parse("image/png")))
                        .addFormDataPart("registration", id.getName(),
                                RequestBody.create(id, MediaType.parse("image/png")))
                        .build();

                Request request = new Request.Builder()
                        .url("htpp://codexmeraki.ga/android/apply.php")
                        .post(rbApplication)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String strResponse = response.body().string();
                    JSONObject data = new JSONObject(strResponse);
                    if(data.has("code") && data.get("code") == "2") {
                        Log.d("DriverApp", "An error has occurred.");
                        for(int x = 0; x > data.length()-1; x++) {
                            Log.d("DriverApp", data.get("log-"+x).toString());
                        }
                    } else {
                        Log.d("DriverApp", "Application has been submitted");
                        sp.edit().putString("type", "1").apply();
                        finish();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        submit.start();
    }

    public void uploadPhoto(View view) {
        String target = view.getContentDescription().toString();
        switch(target) {
            case "imageView8":
                iv = findViewById(R.id.imageView8);
                break;
            case "imageView9":
                iv = findViewById(R.id.imageView9);
                break;
            default:
                iv = null;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DiscountApplication.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
            return;
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if (data != null){
                        Uri selectedImageUri = data.getData();
                        if (selectedImageUri != null){
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                iv.setImageBitmap(bitmap);
                            }catch (Exception e){
                                Toast.makeText(DiscountApplication.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

    private String getPathFormatUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri,null,null,null,null);
        if (cursor == null){
            filePath = contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }
}