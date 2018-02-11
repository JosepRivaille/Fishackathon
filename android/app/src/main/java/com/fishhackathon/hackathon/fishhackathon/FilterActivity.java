package com.fishhackathon.hackathon.fishhackathon;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {
    private static final String TAG = FilterActivity.class.getSimpleName();
    public static final String KEY_DATE_FROM = "KEY_DATE_FROM";
    public static final String KEY_DATE_TO = "KEY_DATE_TO";
    private EditText inicioEditText;
    private EditText finEditText;
    private Button saveButton;
    private Button noSaveButton;

    private String fromDate;
    private String toDate;

    private Calendar calendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener fromDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            fromDate = sdf.format(calendar.getTime());
            inicioEditText.setText(fromDate);
        }
    };

    DatePickerDialog.OnDateSetListener toDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            toDate = sdf.format(calendar.getTime());
            finEditText.setText(toDate);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        setUpElements();
        setUpListeners();
    }

    private void setUpElements() {
        inicioEditText = findViewById(R.id.filter_inicio_edittext);
        finEditText = findViewById(R.id.filter_fin_edittext);

        saveButton = findViewById(R.id.filter_save_button);
        noSaveButton = findViewById(R.id.filter_no_save_button);
    }

    private void setUpListeners() {
        inicioEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(FilterActivity.this, fromDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        finEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(FilterActivity.this, toDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        noSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra(KEY_DATE_FROM, fromDate);
                intent.putExtra(KEY_DATE_TO, toDate);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
