package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;
import com.example.android_resapi.ui.DeviceActivity;

public class GetThings extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;

    // GetThings는 GetRequest를 상속받아 구현된 클래스
    // API에서 디바이스 목록을 가져오는 역할

    public GetThings(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            // URL을 생성
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            // URL이 틀리면 액티비티 종료
            Toast.makeText(activity, "URL is invalid:" + urlStr, Toast.LENGTH_SHORT).show();
            activity.finish();
            e.printStackTrace();
        }
        // 화면에 "조회중..." 표시
        TextView message = activity.findViewById(R.id.message);
        message.setText("조회중...");
    }

    @Override
    protected void onPostExecute(String jsonString) {
        // 조회 결과를 화면에 띄움
        TextView message = activity.findViewById(R.id.message);
        if (jsonString == null || jsonString.equals("")) {
            message.setText("디바이스 없음");
            return;
        }
        message.setText("");

        //arrayList의 데이터를 배열로 변환해 사용하는 새로운 ArrayAdapter를 생성
        ArrayList<Thing> arrayList = getArrayListFromJSONString(jsonString);

        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.txtList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);

        // 리스트뷰 아이템 클릭 시 이벤트 처리
        txtList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Thing thing = (Thing) adapterView.getAdapter().getItem(i);
                // 선택한 디바이스의 세부 정보를 표시하는 액티비티로 이동
                Intent intent = new Intent(activity, DeviceActivity.class);
                intent.putExtra("thingShadowURL", urlStr + "/" + thing.name);
                activity.startActivity(intent);
            }
        });
    }

    // JSON 문자열을 ArrayList<Thing>으로 바꿈
    protected ArrayList<Thing> getArrayListFromJSONString(String jsonString) {
        ArrayList<Thing> output = new ArrayList();
        try {
            // 문자열에서 처음과 마지막의 큰 따옴표를 제거
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            // \\\"를 \"로 바꿈
            jsonString = jsonString.replace("\\\"", "\"");

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("things");

            for (int i = 0; i < jsonArray.length(); i++) {
                // JSON 배열에서 필요한 데이터를 추출해 Thing 객체를 생성
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                Thing thing = new Thing(jsonObject.getString("thingName"),
                        jsonObject.getString("thingArn"));

                output.add(thing);
            }

        } catch (JSONException e) {
            // JSON 처리 중 예외 시 스택 추적을 출력
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }

    // 디바이스 정보를 담는 Thing 클래스
    class Thing {
        String name;
        String arn;
        HashMap<String, String> tags;

        public Thing(String name, String arn) {
            this.name = name;
            this.arn = arn;
            tags = new HashMap<String, String>();
        }

        public String toString() {
            // 디바이스 정보
            return String.format("이름 = %s \nARN = %s \n", name, arn);
        }
    }
}
