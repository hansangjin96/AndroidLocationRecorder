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
import android.view.KeyEvent;
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
    //notification이랑 백키 무효화
    public static final String CHANNEL_ID = "exampleNotification";
    public static boolean IsThreadIng= false;

    //gps부분

    //변수 설정
    private GoogleMap mMap;
    double lat[] = new double[24];
    double lon[] = new double[24];
    int arrayLen = 0;
    Button myButton, myButton2, myButton3, myButton4;
    boolean isService = false; // 서비스 중인 확인용
    int service_check = 0;

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
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isService = false;
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
            if(lat[arrayLen] !=0 && lon[arrayLen]!=0) {
                Toast.makeText(getApplicationContext(), arrayLen+"번째 recv data : " + lat[arrayLen] + " " + lon[arrayLen], Toast.LENGTH_LONG).show();
                init(latitude, longitude);
                arrayLen++;
            }
            else if (arrayLen>=lat.length)
            {
                Toast.makeText(getApplicationContext(), "24 초과함 ", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), arrayLen+"번째 recv data : " + "0값이 들어감 \n 서비스 종료됨", Toast.LENGTH_LONG).show();
            }

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

        //notification 생성
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
                if(service_check==0)
                {
                    Toast.makeText(getApplicationContext(), "아직 서비스 시작 안함", Toast.LENGTH_LONG).show();
                }
                else {
                    mService.myServiceFunc();
                }
            }
        });
    }
    //GoogleMap Fragment 끝.

    public void start() {

        LatLng location = new LatLng(37.559085, 126.998501);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("시작위치");
        markerOptions.snippet("요기");
        markerOptions.position(location);
        mMap.addMarker(markerOptions);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }

    public void init(double lati, double longi) {

        LatLng location = new LatLng(lati, longi);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("위치" + arrayLen);
        markerOptions.snippet("전철역");
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

    public void startService(View view) {
        IsThreadIng = true;
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
        service_check+=10;
        arrayLen=0;
    }

    public void stopService(View view) {
        IsThreadIng = false;
        if (!isService) {
            Toast.makeText(getApplicationContext(),
                    "서비스중이 아닙니다, 종료 할 수 없음",
                    Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
            unbindService(mConnection); // 서비스 종료
            isService=!isService;
        }
    }
    //쓰레드 연결하기 버튼 클릭시 끝
    MyService myService;
    //notification생성
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Example Service Channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    //백키 무효화
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (IsThreadIng == true&&event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // handle back press
            // if (event.getAction() == KeyEvent.ACTION_DOWN)
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}