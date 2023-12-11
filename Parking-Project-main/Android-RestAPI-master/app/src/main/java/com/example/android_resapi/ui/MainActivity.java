package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_resapi.R;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "AndroidAPITest";
    EditText listThingsURL, thingShadowURL, getLogsURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 사물 목록 조회 버튼 설정
        Button listThingsBtn = findViewById(R.id.listThingsBtn);
        listThingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // API 호출을 위한 URL
                String urlstr = "https://vm3hfg6d37.execute-api.ap-northeast-2.amazonaws.com/prod/devices";
                Log.i(TAG, "listThingsURL=" + urlstr);

                // URL이 맞는건지 확인하고 ListThingsActivity로 전환
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(MainActivity.this, "사물목록 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ListThingsActivity.class);
                intent.putExtra("listThingsURL", urlstr);
                startActivity(intent);
            }
        });

        // 사물 상태 조회/변경 버튼 설정
        Button thingShadowBtn = findViewById(R.id.thingShadowBtn);
        thingShadowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // API 호출을 위한 URL
                String urlstr = "https://vm3hfg6d37.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMKRWiFi1010";

                // URL이 맞는건지 확인하고 ListThingsActivity로 전환
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(MainActivity.this, "사물상태 조회/변경 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                intent.putExtra("thingShadowURL", urlstr);
                startActivity(intent);
            }
        });

        // 사물 로그 조회 버튼 설정
        Button listLogsBtn = findViewById(R.id.listLogsBtn);
        listLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // API 호출을 위한 URL
                String urlstr = "https://vm3hfg6d37.execute-api.ap-northeast-2.amazonaws.com/prod/devices/MyMKRWiFi1010/log";

                // URL이 맞는건지 확인하고 ListThingsActivity로 전환
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(MainActivity.this, "사물로그 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                intent.putExtra("getLogsURL", urlstr);
                startActivity(intent);
            }
        });
    }
}
