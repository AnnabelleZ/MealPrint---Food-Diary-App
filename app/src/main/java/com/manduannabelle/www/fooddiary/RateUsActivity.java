package com.manduannabelle.www.fooddiary;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

        Button rateOnGoogle = findViewById(R.id.gotogoogleplay);
        rateOnGoogle.setOnClickListener((View v) -> rateMe(v));

        Button rateOnAmazon = findViewById(R.id.gotoamazonappstore);
        rateOnAmazon.setOnClickListener((View v) -> rateMeAmazon(v));
    }

    public void rateMe(View v) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void rateMeAmazon(View v) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + getPackageName())));
        }
    }
}
