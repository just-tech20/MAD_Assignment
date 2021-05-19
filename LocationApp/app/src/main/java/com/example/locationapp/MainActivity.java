package com.example.locationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSION_CODE = 101;
    TextView locationText;
    Button getLocation;
    boolean gps_location;
    boolean network_location;
    String[] permissions_all={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    LocationManager locationManager;
    Location loc;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Getting Location...");

        locationText=findViewById(R.id.location);
        getLocation=findViewById(R.id.getlocation);

        getLocation.setOnClickListener(v -> {
            progressDialog.show();
            progressDialog.dismiss();
            getLocation();
        });
    }

    private void getLocation() {
        if(Build.VERSION.SDK_INT>=23){
            if(checkPermission()){
                getDeviceLocation();
            }
            else{
                requestPermission();
            }
        }
        else{
            getDeviceLocation();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,permissions_all,PERMISSION_CODE);
    }

    private boolean checkPermission() {
        for (String s : permissions_all) {
            int result = ContextCompat.checkSelfPermission(MainActivity.this, s);
            if (result == PackageManager.PERMISSION_GRANTED) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private void getDeviceLocation() {
        locationManager=(LocationManager)getSystemService(Service.LOCATION_SERVICE);
        gps_location=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_location=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!gps_location && !network_location){
            showSettingForLocation();
            getLastlocation();
        }
        else{
            getFinalLocation();
        }
    }

    private void getLastlocation() {
        if(locationManager!=null) {
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria,false);
                Location location=locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFinalLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFinalLocation() {
        try{
            if(gps_location){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*1,10,MainActivity.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null){
                        showResult(loc);
                    }
                }
            }

            if(network_location){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*1,10,MainActivity.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(loc!=null){
                        showResult(loc);
                    }
                }
            }

        }catch (SecurityException e){
            Toast.makeText(this, "Not Able to Find Location", Toast.LENGTH_SHORT).show();
        }

    }

    private void showResult(Location loc) {
        if(loc.getLatitude()==0 && loc.getLongitude()==0){
            getDeviceLocation();
        }
        else{
            progressDialog.dismiss();
            locationText.setText("Latitude : "+loc.getLatitude()+"     Longitude : "+loc.getLongitude());

        }

    }

    private void showSettingForLocation() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        al.setTitle("Location is Not Enabled!");
        al.setMessage("Enable Location ?");
        al.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
        al.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        al.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        showResult(location);
    }
}
