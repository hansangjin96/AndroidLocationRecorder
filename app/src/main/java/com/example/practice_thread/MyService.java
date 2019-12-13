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

public class MyService extends Service implements LocationListener {
    //쓰레드
    private Thread myThread;
    private int mCount;

    //배열 선언
    double Latitude[] = new double[24];
    double Longitude[] = new double[24];
    int num=0;
    //location listener

    /*
    @Override
    public IBinder onBind(Intent intent) {
        //이 부분에서 intetn로 위도 경도 배열을 반환함
        return null;
    }*/

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

    public int test()
    {
        return 50;
    }

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    public void myServiceFunc(){
        //서비스에서 처리할 내용
        mCallback.recvData(Latitude[num],Longitude[num]);
        num++;
    }
    //서비스에서 액티비티 함수 호출은..
    //mCallback.recvData();
    //여기까지 콜백

    /* 쓰잘데기 없는거
    public class LocalBinder extends Binder {
        MyService getService()
        {
            return MyService.this;
        }
    }
    public double[] retlong(){
        return Longitude;
    }
    public double[] retlat(){
        return Latitude;
    }*/


    //location 끝
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationintent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationintent, 0);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Example service")
                .setContentText("알림창")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

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

                    for (int i = 0; i < Longitude.length; i++) {
                        try {
                            String locationProvider = LocationManager.GPS_PROVIDER;
                            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                            Longitude[i] = lastKnownLocation.getLongitude();
                            Latitude[i] = lastKnownLocation.getLatitude();
                            System.out.println("longtitude=" + Longitude[i] + ", latitude=" + Latitude[i]);
                            mCount++;
                            Thread.sleep(60000);
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
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(int i=0;i<Longitude.length;i++)
        {
            System.out.println(i+"배열에 저장되니? "+Longitude[i]);
        }

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