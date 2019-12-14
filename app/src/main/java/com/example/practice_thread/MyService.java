package com.example.practice_thread;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

import static com.example.practice_thread.MainActivity.CHANNEL_ID;

public class MyService extends Service implements LocationListener  {


    //쓰레드
    private Thread myThread;
    private int mCount;


    //블로그 gps 변수선언
    private  Context mContext = this;

    // GPS 사용여부
    boolean isGPSEnabled = false;

    // 네트워크 사용여부
    boolean isNetWorkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;


    Location location;
    double lat; // 위도
    double lon; // 경도

    private long startTime = -1;
    private Location beforeLocation;
    private Location curLocation;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 1;

    //private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;

    protected LocationManager locationManager;


    //여기까지 블로그 gps 변수선언

    //LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    //배열 선언
    double Latitude[] ={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    double Longitude[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    int num=0;
    //location listener

    //콜백부분 정의

    public class MainServiceBinder extends Binder {
        MyService getService() {
            return MyService.this; //현재 서비스를 반환.
        }
    }
    private final IBinder mBinder = new MainServiceBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
    public interface ICallback {
        public void recvData(); //액티비티에서 선언한 콜백 함수.
        public void recvData(double lat, double lon);

    }
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    public void myServiceFunc(){
        //서비스에서 처리할 내용
        if(num>=Latitude.length)
        {
            Toast.makeText(getApplicationContext(), "24 초과함 ", Toast.LENGTH_LONG).show();
        }
        else
        {
            mCallback.recvData(Latitude[num], Longitude[num]);
            num++;
        }
    }
    //서비스에서 액티비티 함수 호출은..
    //mCallback.recvData();
    //여기까지 콜백

    // 쓰잘데기 없는거
    public class LocalBinder extends Binder {
        MyService getService()
        {
            return MyService.this;
        }
    }


    //location 끝
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //알림시작
        Intent notificationintent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationintent, 0);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Example service")
                .setContentText("알림창")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        //gps
        //진짜시작.
        //스레드 생성해서 현재 위치를 지속적으로 갱신.
        if (myThread == null) {
            MainActivity.IsThreadIng = true;//쓰레드 실행중일때 백키 무효화
            Toast.makeText(getApplicationContext(), "Service is started", Toast.LENGTH_LONG).show();
            myThread = new Thread("My Thread") {
                @Override
                public void run() {

                    for (int i = 0; i < Longitude.length; i++) {
                        try {
                            //String locationProvider = LocationManager.NETWORK_PROVIDER;
                            //Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                            Location location = getLocation();
                            Longitude[i] = location.getLongitude();
                            Latitude[i] = location.getLatitude();
                            System.out.println("longtitude=" + Longitude[i] + ", latitude=" + Latitude[i]);
                            mCount++;
                            Thread.sleep(1000);//1200000=20분
                        } catch (InterruptedException e) {
                            break;
                        }
                        Log.d("My Service", "서비스동작중" + mCount);
                    }
                }
            };

            myThread.start();
        } else {
            MainActivity.IsThreadIng = false;//쓰레드 실행중이지 않을때 백키 사용가능!
            Toast.makeText(getApplicationContext(), "이미 서비스 실행중입니다.\n서비스 종료를 하신 후 눌러주세요.", Toast.LENGTH_LONG).show();

        }
        return super.onStartCommand(intent, flags, startId);
        //return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(getApplicationContext(), "Service is destroyed", Toast.LENGTH_LONG).show();

        if (myThread != null) {
            myThread.interrupt();
            myThread = null;
            mCount = 0;
        }
        else{
            Toast toast2 = Toast.makeText(this, "서비스가 동작중이지 않음", Toast.LENGTH_SHORT);
            toast2.show();
        }
    }

    public Location getLocation() {


        try {
            Criteria criteria = new Criteria();

            criteria.setAccuracy(Criteria.ACCURACY_FINE);     // 정확도
            criteria.setPowerRequirement(Criteria.POWER_LOW); // 전원소비량
            criteria.setAltitudeRequired(true);              // 고도
            criteria.setBearingRequired(false);              // 기본 정보, 방향, 방위
            criteria.setSpeedRequired(false);                // 속도
            criteria.setCostAllowed(true);                   // 위치정보 비용

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, true);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetWorkEnabled) {

            } else {
                if (isNetWorkEnabled) {
                    //locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lat = location.getLongitude();
                        }
                    }
                }
                this.isGetLocation = true;
                if (isGPSEnabled) {
                    if (location == null) {                  // LocationManager.GPS_PROVIDER
                        //locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } // end of try~catch
        return location;
    } // end of getLocation

    // GPS OFF
    public void stopUsingGPS() {
        if (locationManager != null) {
            //locationManager.removeUpdates(GpsInfo.this);
        } // end of if
    } // end of stopUsingGPS

    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        } // end of if
        return lat;
    } // end of getLatitude

    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        } // end of if
        return lon;
    } // end of getLatitude

    public boolean isGetLocation() {
        return this.isGetLocation;
    } // end of isGetLocation

    public void showSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS 사용유무");
        alertDialog.setMessage("GPS 사용해야됨, 설정 창 ㄱ?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            } // end of onClick
        }); // end of setPositiveButton

        alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            } // end of onClick
        }); // end of setNegativeButton
        alertDialog.show();
    } // end of showSettingAlert

    @Override
    public void onLocationChanged(Location location) {

        Log.d("----Start location----", location.toString());
        if (startTime == -1) {
            startTime = location.getTime();
        } // end of if
        Log.i("time", String.valueOf(location.getTime()));
        beforeLocation = getLocation();
        float distance[] = new float[1];
        Log.i("** Before Location", String.valueOf(beforeLocation.getLatitude()) + "!!!!" + String.valueOf(beforeLocation.getLongitude()));
        Log.i("&& Current Location", String.valueOf(location.getLatitude()) + "!!!!" + String.valueOf(location.getLongitude()));
        Location.distanceBetween(beforeLocation.getLatitude(), beforeLocation.getLongitude(), location.getLatitude(), location.getLongitude()
                , distance); // distance -> meter m/s

        float dis = distance[0];
//        Log.i("distance", distance.toString());
        Log.i("*** distance ", String.valueOf(dis));
        location.getSpeed();
        Log.i("Speed", String.valueOf(location.getSpeed()));
        long delay = location.getTime() - startTime;
        double speed = distance[0] / delay;
        double speedKMH = speed * 3600;  // m/s

        beforeLocation = location;
        Log.i("speed", String.valueOf(speedKMH));
    }

    /*
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/

}