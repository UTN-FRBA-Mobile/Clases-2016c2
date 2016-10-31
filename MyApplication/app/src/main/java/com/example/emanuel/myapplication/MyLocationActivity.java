package com.example.emanuel.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by emanuel on 24/10/16.
 */

public class MyLocationActivity extends AppCompatActivity implements LocationListener {

    private static final int GS_CONN_ERROR = 4317;
    private static final int LOC_PERM = 9930;

    private GoogleApiClient googleApiClient;
    private Location location = null;
    private Handler handler = new Handler();
    private Runnable timeoutCallback = new Runnable() {
        @Override
        public void run() {
            if (location == null) {
                showNoLocation();
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, MyLocationActivity.this);
                requestLocationButton.setEnabled(true);
            }
        }
    };


    private View requestLocationButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_location_activity);
        requestLocationButton = findViewById(R.id.requestLocation);
        requestLocationButton.setEnabled(false);
        requestLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });
        connectGoogleApi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timeoutCallback);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
    }

    private void connectGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        requestLocationButton.setEnabled(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        requestLocationButton.setEnabled(false);
                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        try {
                            connectionResult.startResolutionForResult(MyLocationActivity.this, GS_CONN_ERROR);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }).build();
        googleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GS_CONN_ERROR:
                if (googleApiClient != null) {
                    googleApiClient.connect();
                }
                break;
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, LOC_PERM);
            return;
        }
        LocationAvailability availability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (availability == null || !availability.isLocationAvailable()) {
            showLocationDisabled();
            return;
        }
        requestLocationButton.setEnabled(false);
        location = null;
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
        handler.postDelayed(timeoutCallback, 30000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOC_PERM:
                boolean locationGranted = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i]) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        locationGranted = true;
                    }
                }
                if (locationGranted) {
                    requestLocation();
                }
                else {
                    showLocationRequired();
                }
                break;
        }
    }

    private void showLocationRequired() {
        Toast.makeText(this, R.string.locationRequired, Toast.LENGTH_LONG).show();
    }

    private void showLocationDisabled() {
        Toast.makeText(this, R.string.locationServicesDisabled, Toast.LENGTH_LONG).show();
    }

    private void showNoLocation() {
        Toast.makeText(this, R.string.noLocation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        requestLocationButton.setEnabled(true);
        Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();
        handler.removeCallbacks(timeoutCallback);
    }
}
