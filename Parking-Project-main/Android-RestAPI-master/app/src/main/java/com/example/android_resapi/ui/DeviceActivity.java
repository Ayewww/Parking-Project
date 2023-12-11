package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThingShadow;
import com.example.android_resapi.ui.apicall.UpdateShadow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends AppCompatActivity {
    String urlStr;
    final static String TAG = "AndroidAPITest";
    Timer timer;
    Button startGetBtn;
    Button stopGetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Intent intent = getIntent();
        urlStr = intent.getStringExtra("thingShadowURL");

        // 시작 버튼 초기화
        startGetBtn = findViewById(R.id.startGetBtn);
        startGetBtn.setEnabled(true);
        startGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 주기적으로 Thing Shadow 정보를 가져오는 타이머를 킴
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       new GetThingShadow(DeviceActivity.this, urlStr).execute();
                                   }
                               },
                        0, 2000);

                startGetBtn.setEnabled(false);
                stopGetBtn.setEnabled(true);
            }
        });

        // 정지 버튼 초기화
        stopGetBtn = findViewById(R.id.stopGetBtn);
        stopGetBtn.setEnabled(false);
        stopGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 타이머가 있으면 정지
                if (timer != null)
                    timer.cancel();
                // 화면에 표시된 값을 초기화
                clearTextView();
                startGetBtn.setEnabled(true);
                stopGetBtn.setEnabled(false);
            }
        });

        // 업데이트 버튼 초기화
        Button updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 업데이트할 상태 정보를 생성
                JSONObject payload = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();

                    //"light" 태그 추가, 버튼 눌리면 light를 0으로 만듦
                    JSONObject tag1 = new JSONObject();
                    tag1.put("tagName", "light");
                    tag1.put("tagValue", 0);

                    jsonArray.put(tag1);

                    // 생성된 태그를 payload에 추가
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException");
                }
                Log.i(TAG, "payload=" + payload);

                // payload가 존재한다면 Thing Shadow를 업데이트
                if (payload.length() > 0)
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                else
                    Toast.makeText(DeviceActivity.this, "변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 화면에 표시된 값을 초기화
    private void clearTextView() {
        TextView reported_ledTV = findViewById(R.id.reported_led);
        TextView reported_tempTV = findViewById(R.id.reported_temp);
        reported_tempTV.setText("");
        reported_ledTV.setText("");
    }
}
