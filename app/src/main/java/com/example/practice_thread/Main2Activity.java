package com.example.practice_thread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    Button bt5,bt6,bt7,bt8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bt5 = findViewById(R.id.button5);//위치 추적
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        bt6 = findViewById(R.id.button6);//카카오톡
        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String msg = "사랑해";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "오빠 믿지 알림");
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                intent.setPackage("com.kakao.talk");
                startActivity(intent);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "카카오톡 없음", Toast.LENGTH_LONG).show();
                }


                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        bt7 = findViewById(R.id.button7);//Dday
        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        bt8 = findViewById(R.id.button8);//호텔예약
        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

}
