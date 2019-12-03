package com.example.practice_thread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //gps부분
    /*
    Button myButton;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String myPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    */


    MyService myService;
    //gps전용 끝

    //googleMap 부분
    private FragmentManager fragmentManager;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //gpssssssssssssssssssssssssssssssssssssssssssssss
        /*
        try {
            if (ActivityCompat.checkSelfPermission(this, myPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{myPermission}, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myButton = (Button) findViewById(R.id.button3);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService = new MyService(MainActivity.this);
                if (myService.canGetLocation()) {
                    double latitude = myService.getLatitude();
                    double longitude = myService.getLongitude();
                    Toast.makeText(getApplicationContext(), "당신의 위치는 경도: " + latitude + " " + "위도: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    myService.showSettingAlert();
                }
            }
        });
         */
        //gpssssssssssssssssssssssssssssssssssssssssssssss끝
        //GoogleMap
        fragmentManager = getFragmentManager();

        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(37.558291, 127.000190);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("동국대학교");
        markerOptions.snippet("전철역");
        markerOptions.position(location);
        googleMap.addMarker(markerOptions);

        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

    }
    //GoogleMap Fragment 끝.

    private static final int REQUEST_CODE_PERMISSIONS = 1000;

    public void gpspermissionCheck(View view){
        //위치받아오는부분
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED)
        ){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;
        }
        else{
            System.out.println("권한체크완료.");
        }
        //위치받아오는부분 끝
    }
    //쓰레드 연결하기 버튼 클릭시
    public void startService(View view) {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_CODE_PERMISSIONS:
                if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "권한체크 거부 됨", Toast.LENGTH_SHORT).show();

        }
    }

    public void stopService(View view) {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }
    //쓰레드 연결하기 버튼 클릭시 끝

}
