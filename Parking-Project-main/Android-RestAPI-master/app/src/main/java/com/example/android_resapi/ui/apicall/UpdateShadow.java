package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.android_resapi.httpconnection.PutRequest;

public class UpdateShadow extends PutRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // UpdateShadow는 PutRequest를 상속받아 구현된 클래스
    // 디바이스의 Shadow 정보를 업데이트하는 역할

    public UpdateShadow(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            // URL을 생성
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            // URL이 틀리면 액티비티를 종료
            e.printStackTrace();
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // 업데이트 결과를 토스트 메시지로 표시
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
    }
}
