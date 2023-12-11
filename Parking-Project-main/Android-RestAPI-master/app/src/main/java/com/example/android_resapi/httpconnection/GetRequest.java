package com.example.android_resapi.httpconnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// 안드로이드에서 AsyncTask를 사용해 GET 요청을 수행하는 추상 클래스
abstract public class GetRequest extends AsyncTask<String, Void, String> {
    final static String TAG = "AndroidAPITest";
    protected Activity activity; // 호출하는 액티비티에 대한 참조
    protected URL url; // GET 요청에 사용되는 URL

    // 호출하는 액티비티를 매개변수로 받는 생성자.
    public GetRequest(Activity activity) {
        this.activity = activity;
    }

    // 백그라운드 작업을 하는 AsyncTask 메서드
    @Override
    protected String doInBackground(String... strings) {
        StringBuffer output = new StringBuffer();

        try {
            if (url == null) {
                Log.e(TAG, "에러: URL이 null입니다. ");
                return null;
            }

            // 지정된 URL에 연결
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn == null) {
                Log.e(TAG, "HttpsURLConnection 에러");
                return null;
            }

            // 연결 매개변수를 설정
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);

            // 서버로부터 응답 코드를 가져옴
            int resCode = conn.getResponseCode();

            // 응답 코드가 HTTP_OK(200)인지 확인
            if (resCode != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "HttpsURLConnection 응답 코드: " + resCode);
                conn.disconnect();
                return null;
            }

            // 서버로부터의 응답을 읽음
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;

            // 각 응답 라인을 출력에 추가
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                output.append(line);
            }

            // 연결 해제
            reader.close();
            conn.disconnect();

        } catch (IOException ex) {
            // 예외 시
            Log.e(TAG, "응답 처리 중 예외 발생.", ex);
            ex.printStackTrace();
        }

        // 결과를 문자열로 반환
        return output.toString();
    }
}
