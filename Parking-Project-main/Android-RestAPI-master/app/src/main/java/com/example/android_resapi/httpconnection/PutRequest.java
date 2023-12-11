package com.example.android_resapi.httpconnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// Android에서 JSON 데이터를 사용해 PUT 요청하는 AsyncTask 클래스
public class PutRequest extends AsyncTask<JSONObject, Void, String> {
    protected Activity activity; // 호출하는 액티비티에 대한 참조
    protected URL url; // PUT 요청에 사용되는 URL

    // 호출하는 액티비티를 매개변수로 받는 생성자
    public PutRequest(Activity activity) {
        this.activity = activity;
    }

    // 백그라운드 작업을 하는 AsyncTask 메서드
    @Override
    protected String doInBackground(JSONObject... postDataParams) {

        try {
            // PUT 요청을 위해 지정된 URL에 연결
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // 연결에서 출력 스트림을 가져옴
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            // JSON 매개변수를 문자열로 변환하고 출력 스트림에 작성
            String str = postDataParams[0].toString();
            Log.e("params", "Post String = " + str);
            writer.write(str);

            writer.flush();
            writer.close();
            os.close();

            // 서버로부터 응답 코드를 가져옴
            int responseCode = conn.getResponseCode();

            // 응답 코드가 HTTP_OK(200)인지 확인
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // 서버의 응답을 읽음
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                // 각 응답 라인을 StringBuffer에 추가
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                // 응답 코드가 HTTP_OK가 아닐 때
                return new String("서버 오류: " + responseCode);
            }
        } catch (Exception e) {
            // 예외 시
            e.printStackTrace();
        }
        return null;
    }
}
