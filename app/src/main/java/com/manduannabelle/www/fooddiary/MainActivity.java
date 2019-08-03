package com.manduannabelle.www.fooddiary;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imgButton = (ImageButton) findViewById(R.id.toolbar_calendar);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        // for the date on the toolbar
        // reset the selected day in the SharedPreferences to the current date
        Calendar calendar = Calendar.getInstance();
        saveDate(DateFormat.getDateInstance().format(calendar.getTime()));
        // load selected day
        loadDate();

        // for tool bar to show
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add onclick listener to cards
        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to singleMeal1Activity
                startActivity(new Intent(MainActivity.this, SingleMeal1Activity.class));
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to singleMeal2Activity
                startActivity(new Intent(MainActivity.this, SingleMeal2Activity.class));
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to singleMeal2Activity
                startActivity(new Intent(MainActivity.this, SingleMeal3Activity.class));
            }
        });

        loadCardBackground();
        loadCardTitle();
        loadCardTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCardBackground();
        loadCardTitle();
        loadCardTime();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());

        TextView date = (TextView) findViewById(R.id.text_view_date);
        date.setText(currentDateString);
        saveDate(currentDateString);
    }

    public void saveDate(String currentDate) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("current_date", currentDate);
        editor.commit();
    }

    public void loadDate() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        String currentDate = sharedPreferences.getString("current_date", DateFormat.getDateInstance().format(calendar.getTime()));
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);
    }

    private void loadCardBackground() {
        ImageView card1background = findViewById(R.id.card1background);
        ImageView card2background = findViewById(R.id.card2background);
        ImageView card3background = findViewById(R.id.card3background);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String imgPath1 = sharedPreferences.getString("meal1_imgPath", "");
        String imgPath2 = sharedPreferences.getString("meal2_imgPath", "");
        String imgPath3 = sharedPreferences.getString("meal3_imgPath", "");
        loadImageFromPath(card1background, imgPath1, "meal1.jpg");
        loadImageFromPath(card2background, imgPath2, "meal2.jpg");
        loadImageFromPath(card3background, imgPath3, "meal3.jpg");
    }

    private void loadImageFromPath(ImageView background, String imgPath, String name) {
        if (!imgPath.isEmpty()) {
            try {
                File f = new File(imgPath, name);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                if (b != null) {
                    //Drawable d = new BitmapDrawable(getResources(), b);
                    //d.setColorFilter(getResources().getColor(R.color.darkgray), PorterDuff.Mode.DARKEN);
                    //background.setBackground(d);
                    Bitmap mutable = b.copy(Bitmap.Config.ARGB_8888, true);
                    background.setImageBitmap(ImageManager.darkenBitMap(mutable));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCardTitle() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_title = sharedPreferences.getString("meal1_title", "");
        String meal2_title = sharedPreferences.getString("meal2_title", "");
        String meal3_title = sharedPreferences.getString("meal3_title", "");
        TextView card1text = findViewById(R.id.card1text);
        TextView card2text = findViewById(R.id.card2text);
        TextView card3text = findViewById(R.id.card3text);
        setTitle(card1text, meal1_title);
        setTitle(card2text, meal2_title);
        setTitle(card3text, meal3_title);
    }

    private void setTitle(TextView view, String text) {
        if (!text.isEmpty()) {
            view.setTextColor(getResources().getColor(R.color.white));
            view.setText(text.toUpperCase());
        }
    }

    private void loadCardTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_time = sharedPreferences.getString("meal1_time", "");
        String meal2_time = sharedPreferences.getString("meal2_time", "");
        String meal3_time = sharedPreferences.getString("meal3_time", "");
        TextView card1time = findViewById(R.id.card1time);
        TextView card2time = findViewById(R.id.card2time);
        TextView card3time = findViewById(R.id.card3time);
        setTime(card1time, meal1_time);
        setTime(card2time, meal2_time);
        setTime(card3time, meal3_time);
    }

    private void setTime(TextView view, String text) {
        if (!text.isEmpty()) {
            view.setText(text);
        }
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
