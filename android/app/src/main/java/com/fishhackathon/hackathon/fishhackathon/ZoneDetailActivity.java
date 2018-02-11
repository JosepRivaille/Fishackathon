package com.fishhackathon.hackathon.fishhackathon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class ZoneDetailActivity extends AppCompatActivity {
    private static final String TAG = ZoneDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        setUpElements();
        setUpListeners();


    }

    private void setUpElements() {
    }

    private void setUpListeners() {
    }
}
