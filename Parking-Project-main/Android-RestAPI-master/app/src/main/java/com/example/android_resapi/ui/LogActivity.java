package com.example.android_resapi.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetLog;

public class LogActivity extends AppCompatActivity {
    String getLogsURL;

    private TextView textView_Date1;
    private TextView textView_Date2;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final static String TAG = "AndroidAPITest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log);

        // API 호출을 위한 URL을 받아옴.
        Intent intent = getIntent();
        getLogsURL = intent.getStringExtra("getLogsURL");
        Log.i(TAG, "getLogsURL="+getLogsURL);

        // 시작 날짜 선택 버튼 설정
        Button startDateBtn = findViewById(R.id.start_date_button);
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DatePickerDialog로 날짜 선택
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        textView_Date1 = (TextView)findViewById(R.id.textView_date1);
                        textView_Date1.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth));
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2020, 12, 0);
                dialog.show();
            }
        });

        // 시작 시간 선택 버튼 설정
        Button startTimeBtn = findViewById(R.id.start_time_button);
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TimePickerDialog로 시간 선택
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView textView_Time1 = (TextView)findViewById(R.id.textView_time1);
                        textView_Time1.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                };

                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 0, 0, false);
                dialog.show();
            }
        });

        // 종료 날짜 선택 버튼 설정
        Button endDateBtn = findViewById(R.id.end_date_button);
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DatePickerDialog로 날짜 선택
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        textView_Date2 = (TextView)findViewById(R.id.textView_date2);
                        textView_Date2.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth));
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2020, 12, 0);
                dialog.show();
            }
        });

        // 종료 시간 선택 버튼 설정
        Button endTimeBtn = findViewById(R.id.end_time_button);
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TimePickerDialog로 시간 선택
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView textView_Time2 = (TextView)findViewById(R.id.textView_time2);
                        textView_Time2.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                };

                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 0, 0, false);
                dialog.show();
            }
        });

        // 로그 조회 버튼 설정
        Button start = findViewById(R.id.log_start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GetLog로 로그 조회
                new GetLog(LogActivity.this,getLogsURL).execute();
            }
        });
    }
}
