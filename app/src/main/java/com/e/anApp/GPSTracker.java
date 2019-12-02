package com.e.anApp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;


public class GPSTracker extends Service implements LocationListener {
    private final Context myContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetGPSInfo = false;
    Location myLocation;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //meters
    private static final long MIN_TIME_UPDATES = 30000; // 0.5 minutes
    protected LocationManager myLocationManager;
    public GPSTracker(Context context) {
        myContext = context;
        getLocation();
    }
    public Location getLocation() {
        try {
            myLocationManager = (LocationManager)
                    myContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled =
                    myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled =
                    myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnabled && !isNetworkEnabled) {
                // do nothing
            }
            else {
                this.canGetGPSInfo = true;
                if(isNetworkEnabled) {

                    myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if(myLocation != null) {
                        latitude = myLocation.getLatitude();
                        longitude = myLocation.getLongitude();
                    }
                }
                if(isGPSEnabled) {
                    if(myLocation == null) {

                        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if(myLocationManager != null) {
                            myLocation =
                                    myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(myLocation != null) {
                                latitude = myLocation.getLatitude();
                                longitude = myLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myLocation;
    }
    public void stopUsingGPS() {
        if(myLocation != null) {
            myLocationManager.removeUpdates(GPSTracker.this);
        }
    }
    public double getLatitude() {
        if(myLocation != null) {
            latitude = myLocation.getLatitude();
        }
        return latitude;
    }
    public double getLongitude() {
        if(myLocation != null) {
            longitude = myLocation.getLongitude();
        }
        return longitude;
    }
    public boolean canGetLocation() {
        return this.canGetGPSInfo;
    }
    public void showSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(myContext);
        alertDialog.setTitle("GPS 는 설정중입니다.");
        alertDialog.setMessage("GPS 는 활성화되지 않았습니다. 설정 메뉴로 이동할까요?");
                alertDialog.setPositiveButton("설정", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                myContext.startActivity(intent);
                            }
                        });
        alertDialog.setNegativeButton("취소", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }
    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            double latitude= location.getLatitude();
            double longitude = location.getLongitude();
            Toast.makeText(myContext, "onLocationChanged is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderEnabled(String s) {
    }
    @Override
    public void onProviderDisabled(String s) {
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}