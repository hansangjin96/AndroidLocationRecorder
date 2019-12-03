package com.example.practice_thread;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

public class MyService extends Service implements LocationListener {
    //쓰레드
    private Thread myThread;
    private int mCount;

    double Latitude;
    double Longitude;
    //location listener

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //location 끝
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //gps
        /*
        mFusedLocationClient.getLastLocation().addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    Latitude = location.getLatitude();
                    Longitude =location.getLongitude();
                }
            }
        });*/
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //gps
        //진짜시작.
        //스레드 생성해서 현재 위치를 지속적으로 갱신.
        if (myThread == null) {
            Toast toast = Toast.makeText(this, "Service is started", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            myThread = new Thread("My Thread") {
                @Override
                public void run() {

                    for (int i = 0; i < 10; i++) {
                        try {
                            String locationProvider = LocationManager.GPS_PROVIDER;
                            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                            Longitude = lastKnownLocation.getLatitude();
                            Latitude = lastKnownLocation.getLatitude();
                            System.out.println("longtitude=" + Longitude + ", latitude=" + Longitude);
                            mCount++;
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        Log.d("My Service", "서비스동작중" + mCount);
                    }
                }
            };
            myThread.start();
        } else {
            Toast toast1 = Toast.makeText(this, "이미 서비스 실행중입니다.\n서비스 종료를 하신 후 눌러주세요.", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.TOP, 0, 0);
            toast1.show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast toast = Toast.makeText(this, "Service is destroyed", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();

        if (myThread != null) {
            myThread.interrupt();
            myThread = null;
            mCount = 0;
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }
}
