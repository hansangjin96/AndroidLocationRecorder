package com.example.practice_thread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
    public static final String CHANNEL_ID = "exampleNotification";
    //gps부분
    /*
    Button myButton;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String myPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    */

    //변수 설정
    private GoogleMap mMap;
    double lat[] = new double[24];
    double lon[] = new double[24];
    int arrayLen=0;
    Button myButton,myButton2,myButton3,myButton4;

    //gps전용 끝

    //googleMap 부분
    private FragmentManager fragmentManager;
    private MapFragment mapFragment;

    // 콜백하는거
    //액티비티에서 선언.
    private MyService mService;
    //서비스 커넥션 선언.
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MainServiceBinder binder = (MyService.MainServiceBinder) iBinder;
            mService = binder.getService(); //서비스 받아옴
            mService.registerCallback(mCallback); //콜백 등록
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    //서비스에서 아래의 콜백 함수를 호출하며, 콜백 함수에서는 액티비티에서 처리할 내용 입력
    private MyService.ICallback mCallback = new MyService.ICallback() {

        @Override
        public void recvData() {
            //처리할 일들..
        }

        public void recvData(double latitude, double longitude) {
            lat[arrayLen] = latitude;
            lon[arrayLen] = longitude;
            init(latitude, longitude);
            Toast.makeText(getApplicationContext(), "recv data : " + lat[arrayLen] + " " +lon[arrayLen], Toast.LENGTH_LONG).show();
            arrayLen++;
        }
    };
    //서비스 시작.
    /* 여기가 스타트서비스?
    public void startServiceMethod(View v){
        Intent Service = new Intent(this, MyService.class);
        bindService(Service, mConnection, Context.BIND_AUTO_CREATE);
    }*/
    //액티비티에서 서비스 함수 호출
    //mService.myServiceFunc();



    //서비스 끝


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        //GoogleMap
        fragmentManager = getFragmentManager();

        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        start();

        myButton4 = (Button) findViewById(R.id.button4);
        myButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mCallback.recvData();
                mService.myServiceFunc();
                //Toast.makeText(getApplicationContext(),"되는거니" +lat[arrayLen], Toast.LENGTH_LONG).show();
                //System.out.println(arrayLen);
            }
        });
    }
    //GoogleMap Fragment 끝.

    public void start()
    {
        LatLng location = new LatLng(37.56150314578369, 126.99875640600861);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("동국대학교");
        markerOptions.snippet("전철역");
        markerOptions.position(location);
        mMap.addMarker(markerOptions);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }

    public void init(double lati,double longi) {

        LatLng location = new LatLng(lati, longi);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("위치"+arrayLen);
        markerOptions.snippet("전철역");//???
        markerOptions.position(location);
        mMap.addMarker(markerOptions);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }

    private static final int REQUEST_CODE_PERMISSIONS = 1000;

    public void gpspermissionCheck(View view) {
        //위치받아오는부분
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;
        } else {
            System.out.println("권한체크완료.");
        }
        //위치받아오는부분 끝
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "권한체크 거부 됨", Toast.LENGTH_SHORT).show();
        }
    }

    public void startService(View view){
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
        Toast.makeText(getApplicationContext(),"눌리긴 하니", Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Example Service Channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public void stopService(View view) {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);

        unbindService(mConnection);
    }
    //쓰레드 연결하기 버튼 클릭시 끝
}