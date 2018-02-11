package com.fishhackathon.hackathon.fishhackathon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fishhackathon.hackathon.fishhackathon.models.Zone;

import java.io.Serializable;

public class ZoneDetailActivity extends AppCompatActivity {
    private static final String TAG = ZoneDetailActivity.class.getSimpleName();
    public static final String KEY_FOR_ZONE = "KEY_FOR_ZONES";
    private Zone currentZone;
    private TextView codeTextView;
    private TextView levelTextView;
    private TextView oceanTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        setUpElements();
        setUpListeners();

        currentZone = (Zone) getIntent().getSerializableExtra(KEY_FOR_ZONE);

        codeTextView.setText(currentZone.getCode());
        levelTextView.setText(currentZone.getLevel());
        oceanTextView.setText(currentZone.getOcean());
    }

    private void setUpElements() {
        codeTextView = (TextView) findViewById(R.id.zone_code_textview);
        levelTextView = (TextView) findViewById(R.id.zone_nivel_textview);
        oceanTextView = (TextView) findViewById(R.id.zone_oceano_textview);
    }

    private void setUpListeners() {
    }
}
