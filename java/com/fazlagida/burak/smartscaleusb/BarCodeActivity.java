package com.fazlagida.burak.smartscaleusb;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fazlagida.burak.smartscaleusb.Api.ApiClient;
import com.fazlagida.burak.smartscaleusb.Api.ApiService;
import com.fazlagida.burak.smartscaleusb.model.Barcode;
import com.fazlagida.burak.smartscaleusb.model.Flavour;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarCodeActivity extends AppCompatActivity {

    private Button barcodeScanner;
    ApiService service;
    private ProgressDialog pDialog;
    SharedPref pref;

    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);

        service = ApiClient.getInstance(this).create(ApiService.class);
        pref = SharedPref.getPreferences(this);
        initpDialog();

        barcodeScanner = findViewById(R.id.barscanner);
        barcodeScanner.setOnClickListener(view -> {
            qrScanner();
        }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

    }

    public void qrScanner() {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Barcode");
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setBeepEnabled(true);
        integrator.setCameraId(1);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            barcodeScanner.setEnabled(false);
            IntentResult resultQR = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
             if (resultQR.getContents() != null) {
                String barcode = resultQR.getContents();
                sendQR(barcode);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Cancel. No data received !!", Toast.LENGTH_SHORT).show();
                 barcodeScanner.setEnabled(true);
            }
        }
    }

    public void sendQR(String code) {
        showpDialog();
        service.barcode(code).enqueue(new Callback<Barcode>() {
            @Override
            public void onResponse(Call<Barcode> call, Response<Barcode> response) {

                Barcode bc = response.body();

                if (response.isSuccessful() && bc.getStatus().equals("success")) {
//                Toast.makeText(BarCodeActivity.this, "Choose Your Flavour. Thank You !!", Toast.LENGTH_SHORT).show();
                    pref.setIntData("plate",Integer.parseInt(bc.getQty()));
                    pref.setStringData("barcode",code);
                    startActivity(new Intent(BarCodeActivity.this,FlavourActivity.class));
                    hidepDialog();
                    finish();
                } else {
                    hidepDialog();
                    barcodeScanner.setEnabled(true);
                    Toast.makeText(BarCodeActivity.this, "QR Doesn't Exists. Thank You !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Barcode> call, Throwable t) {
                Toast.makeText(BarCodeActivity.this, "NETWORK/SERVER ERROR", Toast.LENGTH_SHORT).show();
                hidepDialog();
                barcodeScanner.setEnabled(true);
            }
        });
    }

    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Camera and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(
                        BarCodeActivity.this,
                        permissions,
                        MY_PERMISSIONS_REQUEST_CODE
                ));
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        } else {
            // Do something, when permissions are already granted
//            Toast.makeText(this,"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // Permissions are granted
                } else {
                    // Permissions are denied
                    Toast.makeText(this, "Permissions are required to use the system.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    protected void initpDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Validating Your QR Code...");
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    protected void hidepDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        // no backpress happen
    }

}
