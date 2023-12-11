package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThings;

public class ListThingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_things);

        // API 호출을 위한 URL을 받아옴
        Intent intent = getIntent();
        String url = intent.getStringExtra("listThingsURL");

        // GetThings로 디바이스 목록을 가져옴
        new GetThings(ListThingsActivity.this, url).execute();
    }
}
