package com.manduannabelle.www.fooddiary;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        ImageButton back = findViewById(R.id.about_back);
        back.setOnClickListener((View v) -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }
}
