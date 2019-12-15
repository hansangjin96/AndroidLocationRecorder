package com.example.practice_thread;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Main3Activity extends AppCompatActivity {
    TextView textDday;
    TextView textResult;
    TextView textToday;

    int tYear;
    int tMonth;
    int tDay;

    int dYear=0;
    int dMonth=0;
    int dDay=0;

    long dday;
    long today;
    long result;

    int resultValue=0;
    Calendar calendar;  //Today
    Calendar calendar2;  //D-Day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toast.makeText(getApplicationContext(), "사귄 날짜를 입력하세요.", Toast.LENGTH_LONG).show();

        textToday=(TextView) findViewById(R.id.textToday);
        textDday=(TextView) findViewById(R.id.textDday);
        textResult=(TextView) findViewById(R.id.textResult);

        /* 오늘 날짜 구하기 */
        calendar=Calendar.getInstance();
        tYear=calendar.get(Calendar.YEAR);
        tMonth=calendar.get(Calendar.MONTH);
        tDay=calendar.get(Calendar.DAY_OF_MONTH);

        textToday.setText(String.format("오늘 날짜 : %d.%d.%d", tYear, tMonth+1, tDay));  //오늘 날짜 출력
        new DatePickerDialog(Main3Activity.this, mDateSetListener, tYear, tMonth, tDay).show();

        /* 선택 날짜 구하기 */
        calendar2=Calendar.getInstance();
        dYear=calendar2.get(Calendar.YEAR);
        dMonth=calendar2.get(Calendar.MONTH);
        dDay=calendar2.get(Calendar.DAY_OF_MONTH);





    }
    DatePickerDialog.OnDateSetListener mDateSetListener=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dYear=year;
            dMonth=monthOfYear;
            dDay=dayOfMonth;


            calendar2.set(Calendar.YEAR, dYear);
            calendar2.set(Calendar.MONTH, dMonth);
            calendar2.set(Calendar.DATE, dDay);

            today=calendar.getTimeInMillis()/(24*60*60*1000);
            dday=calendar2.getTimeInMillis()/(24*60*60*1000);
            result=today-dday;
            resultValue=(int)result;

            UpdateDday();
        }
    };

    void UpdateDday(){
        textDday.setText(String.format("사귄 날짜 : %d.%d.%d", dYear, dMonth+1, dDay));  //선택 날짜 출력


        if(resultValue==0){
            textResult.setText("오늘일정");
        }
        else
        {
            textResult.setText("우리 함께한지 "+String.format("D+%d", resultValue+1));
        }
    }

}