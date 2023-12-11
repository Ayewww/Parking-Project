package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetLog extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // GetLog는 GetRequest를 상속받는 클래스
    // API에서 로그 데이터를 가져오는 역할

    public GetLog(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            // 화면에서 날짜와 시간을 읽어 API에 전달할 매개변수를 생성
            TextView textView_Date1 = activity.findViewById(R.id.textView_date1);
            TextView textView_Time1 = activity.findViewById(R.id.textView_time1);
            TextView textView_Date2 = activity.findViewById(R.id.textView_date2);
            TextView textView_Time2 = activity.findViewById(R.id.textView_time2);

            String params = String.format("?from=%s:00&to=%s:00", textView_Date1.getText().toString() + textView_Time1.getText().toString(),
                    textView_Date2.getText().toString() + textView_Time2.getText().toString());

            Log.i(TAG, "urlStr=" + urlStr + params);
            // URL을 생성
            url = new URL(urlStr + params);

        } catch (MalformedURLException e) {
            // URL이 틀리면 예외 처리
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        // 화면에 "조회중..." 표시
        TextView message = activity.findViewById(R.id.message2);
        message.setText("조회중...");
    }

    @Override
    protected void onPostExecute(String jsonString) {
        // 조회 결과를 화면에 띄움
        TextView message = activity.findViewById(R.id.message2);
        if (jsonString == null) {
            message.setText("로그 없음");
            return;
        }
        message.setText("");

        //arrayList의 데이터를 배열로 변환해 사용하는 새로운 ArrayAdapter를 생성
        ArrayList<Tag> arrayList = getArrayListFromJSONString(jsonString);

        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.logList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);
    }

    // JSON 문자열을 ArrayList<Tag>로 바꿈
    protected ArrayList<Tag> getArrayListFromJSONString(String jsonString) {
        ArrayList<Tag> output = new ArrayList();
        try {
            // 문자열에서 처음과 마지막의 큰 따옴표를 제거
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            // \\\"를 \"로 바꿈
            jsonString = jsonString.replace("\\\"", "\"");

            Log.i(TAG, "jsonString=" + jsonString);

            // JSON 문자열을 파싱
            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                // JSON 배열에서 필요한 데이터를 추출해 Tag 객체를 생성(fee, timestamp)
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                Tag thing = new Tag(jsonObject.getString("fee"),
                        jsonObject.getString("timestamp"));

                output.add(thing);
            }

        } catch (JSONException e) {
            // JSON 처리 중 예외 시 스택 추적을 출력
            e.printStackTrace();
        }
        return output;
    }

    // 로그 데이터를 담는 Tag 클래스.
    class Tag {

        String fee;
        String timestamp;

        public Tag(String fe, String timest) {
            fee = fe;
            timestamp = timest;
        }

        public String toString() {
            // 로그 데이터.
            return String.format("[%s]  fee: %s", timestamp, fee);
        }
    }
}
