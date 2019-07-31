package com.manduannabelle.www.fooddiary;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Calendar;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for the date on the toolbar
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);

        // for tool bar to show
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // when card1 is clicked
        CardView card1 = findViewById(R.id.card1);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to singleMeal1Activity
                startActivity(new Intent(MainActivity.this, SingleMeal1Activity.class));
            }
        });

        loadCardBackground();
        loadCardTitle();
        loadCardTime();
    }

    private void loadCardBackground() {
        RelativeLayout card1background = findViewById(R.id.card1background);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String imgPath = sharedPreferences.getString("meal1_imgPath", "");
        if (!imgPath.isEmpty()) {
            try {
                File f = new File(imgPath, "profile.jpg");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                if (b != null) {
                    Drawable d = new BitmapDrawable(getResources(), b);
                    d.setColorFilter(getResources().getColor(R.color.darkgray), PorterDuff.Mode.DARKEN);
                    card1background.setBackground(d);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCardTitle() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_title = sharedPreferences.getString("meal1_title", "");
        if (!meal1_title.isEmpty()) {
            TextView card1text = findViewById(R.id.card1text);
            card1text.setTextColor(getResources().getColor(R.color.white));
            card1text.setText(meal1_title.toUpperCase());
        }
    }

    private void loadCardTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_time = sharedPreferences.getString("meal1_time", "");
        if (!meal1_time.isEmpty()) {
            TextView card1time = findViewById(R.id.card1time);
            card1time.setText(meal1_time);
        }
    }

    public static Bitmap rotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, source.getWidth(), source.getHeight(), true);
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Toast.makeText(this, "Share selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.theme:
                Toast.makeText(this, "Theme selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.language:
                Toast.makeText(this, "Language selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.feedback:
                Toast.makeText(this, "Feedback selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.rateus:
                Toast.makeText(this, "Rate us selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                Toast.makeText(this, "About selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
