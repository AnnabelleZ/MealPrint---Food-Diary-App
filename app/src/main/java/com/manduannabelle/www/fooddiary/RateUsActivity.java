package com.manduannabelle.www.fooddiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class RateUsActivity extends AppCompatActivity {
    private EditText mEditTextTo;
    private EditText mEditTextSubject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rateus);

        ImageButton back = findViewById(R.id.rateus_back);
        back.setOnClickListener((View v) -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }
}
