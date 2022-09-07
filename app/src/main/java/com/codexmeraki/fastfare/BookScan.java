package com.codexmeraki.fastfare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.ScanMode;

public class BookScan extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 101;
    private CodeScanner codeScanner;
    private Button cancel, pay;
    private TextView dName, pNumber, initStatus;
    private ScrollView details;
    private String driverUid, Username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Book: Scan QR Code");
        setContentView(R.layout.activity_book_scan);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BookScan.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        cancel = (Button) findViewById(R.id.scan_cancel);
        cancel.setOnClickListener(v -> finish());

        pay = (Button) findViewById(R.id.scan_confirm);
        pay.setOnClickListener(v -> {
//            TODO: Open next frame
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BookScan.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                return;
            } else startActivity(new Intent(BookScan.this, BookDestination.class));
            startActivity(new Intent(BookScan.this, BookDestination.class));
            finish();
        });

        codeScanner();
    }

    private void codeScanner() {
        CodeScannerView scannerView = findViewById(R.id.qrscan_cam);
        codeScanner = new CodeScanner(this, scannerView);
        Toast.makeText(this, "Camera Created", Toast.LENGTH_LONG).show();


        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);

        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);

        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            //Insert Detection System
            initStatus = (TextView) findViewById(R.id.scan_initStatus);
            dName = (TextView) findViewById(R.id.scan_txtDriverName);
            pNumber = (TextView) findViewById(R.id.scan_txtPlateNumber);

            details = (ScrollView) findViewById(R.id.scr_scan);

            initStatus.setVisibility(View.GONE);
            dName.setText(result.getText());
            pNumber.setText(result.getText());
            details.setVisibility(View.VISIBLE);

            pay.setVisibility(View.VISIBLE);
        }));

        codeScanner.setErrorCallback(thrown -> Log.d("QR", "An Error Occurred\n"+thrown.getMessage()));

        scannerView.setOnClickListener(view -> codeScanner.startPreview());
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

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}