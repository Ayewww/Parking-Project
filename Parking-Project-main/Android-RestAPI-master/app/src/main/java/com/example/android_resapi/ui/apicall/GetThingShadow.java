package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetThingShadow extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // GetThingShadow 클래스는 GetRequest를 상속받아 구현된 클래스입니다.
    // 디바이스의 Shadow 정보를 조회하고 화면에 표시하는 역할을 합니다.

    public GetThingShadow(Activity activity, String urlStr) {
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
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null)
            return;

        // JSON 문자열을 처리해 상태 정보를 가져오고 화면에 표시
        Map<String, String> state = getStateFromJSONString(jsonString);
        TextView reported_ledTV = activity.findViewById(R.id.reported_led);
        TextView reported_tempTV = activity.findViewById(R.id.reported_temp);
        reported_tempTV.setText(state.get("reported_time"));
        reported_ledTV.setText(state.get("reported_fee"));
    }

    // JSON 문자열에서 상태 정보를 추출
    protected Map<String, String> getStateFromJSONString(String jsonString) {
        Map<String, String> output = new HashMap<>();
        try {
            // 문자열에서 처음과 마지막의 큰 따옴표를 제거
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            // \\\"를 \"로 바꿈
            jsonString = jsonString.replace("\\\"", "\"");
            Log.i(TAG, "jsonString=" + jsonString);

            // JSON 문자열을 파싱
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            String tempValue = reported.getString("time");
            String ledValue = reported.getString("fee");
            output.put("reported_time", tempValue);
            output.put("reported_fee", ledValue);

            JSONObject desired = state.getJSONObject("desired");
            String desired_tempValue = desired.getString("time");
            String desired_ledValue = desired.getString("fee");
            output.put("desired_time", desired_tempValue);
            output.put("desired_fee", desired_ledValue);

        } catch (JSONException e) {
            // JSON 처리 중 예외 시 스택 추적을 출력
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }
}
