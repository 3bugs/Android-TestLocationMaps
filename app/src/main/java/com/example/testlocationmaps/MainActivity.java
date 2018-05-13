package com.example.testlocationmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

import static com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED;
import static com.google.android.gms.common.ConnectionResult.SERVICE_MISSING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_GOOGLE_PLAY_UPDATE = 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private TextView mTestTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTestTextView = findViewById(R.id.test_text_view);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION_PERMISSION
            );
        } else { // Permission granted.
            main();
            /*if (checkGooglePlayServicesReady()) {

            }*/
        }
    }

    @SuppressLint("MissingPermission")
    private void main() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            String msg = String.format(
                                    Locale.getDefault(),
                                    "Latitude: %s\nLongitude: %s",
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            mTestTextView.setText(msg);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String msg = "Get current location failed!";
                        mTestTextView.setText(msg);
                    }
                });
    }

    private boolean checkGooglePlayServicesReady() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case SUCCESS:
                return true;
            case SERVICE_MISSING:
            case SERVICE_VERSION_UPDATE_REQUIRED:
            case SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(
                        this, resultCode, REQUEST_GOOGLE_PLAY_UPDATE
                ).show();
                return false;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_PLAY_UPDATE) {
            if (resultCode == RESULT_OK) {
                // todo:
            } else {
                Toast.makeText(this, "Error updating Google Play Services.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error updating Google Play Services.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    main();
                } else {
                    // permission denied
                    String msg = "Permission denied by the user. App can't continue.";
                    mTestTextView.setText(msg);
                }
            }
        }
    }
}
