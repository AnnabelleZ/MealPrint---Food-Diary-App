package com.manduannabelle.www.fooddiary;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    String currentDate;
    Calendar calendar;

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
        calendar = Calendar.getInstance();
        saveDate(TimeManager.dateFormatter(calendar));
        // load selected day
        loadDate();

        // for tool bar to show
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add onclick listener to cards
        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        CardView card4 = findViewById(R.id.card4);

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
                // go to singleMeal3Activity
                startActivity(new Intent(MainActivity.this, SingleMeal3Activity.class));
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to singleMeal4Activity
                startActivity(new Intent(MainActivity.this, SingleMeal4Activity.class));
            }
        });

        loadCardBackground();
        loadCardTitle();
        loadCardTime();

        ImageButton yesterday = findViewById(R.id.toolbar_nav_left);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToYesterday(yesterday);
            }
        });
        ImageButton tomorrow = findViewById(R.id.toolbar_nav_right);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTomorrow(tomorrow);
            }
        });
        updateRightButtonColor();
    }

    public void updateLeftButtonColor() {
        ImageButton yesterday = findViewById(R.id.toolbar_nav_left);
        if (!dateAfterMinDate(calendar.getTime())) {
            // set < button to gray
            yesterday.setBackground(getResources().getDrawable(R.drawable.ic_chevron_left_gray));
        } else {
            yesterday.setBackground(getResources().getDrawable(R.drawable.ic_chevron_left_white));
        }
    }

    public void updateRightButtonColor() {
        ImageButton tomorrow = findViewById(R.id.toolbar_nav_right);
        if (!dateBeforeMaxDate(calendar.getTime())) {
            // set > button to gray
            tomorrow.setBackground(getResources().getDrawable(R.drawable.ic_chevron_right_gray));
        } else {
            tomorrow.setBackground(getResources().getDrawable(R.drawable.ic_chevron_right_white));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCardBackground();
        loadCardTitle();
        loadCardTime();
    }



    public void goToYesterday(ImageButton yesterday) {
        if (dateAfterMinDate(calendar.getTime())) {
            calendar.add(Calendar.DATE, -1);
            saveDateStateAndReloadPage();
        }
    }

    public Boolean dateAfterMinDate(Date date) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, -6);
        Date minDate = today.getTime();
        return date.after(minDate);
    }

    public Boolean dateBeforeMaxDate(Date date) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, -1);
        Date maxDate = today.getTime();
        return date.before(maxDate);
    }

    public void goToTomorrow(ImageButton tomorrow) {
        if (dateBeforeMaxDate(calendar.getTime())) {
            calendar.add(Calendar.DATE, 1);
            saveDateStateAndReloadPage();
        }

    }

    public void saveDateStateAndReloadPage() {
        String currentDateString = TimeManager.dateFormatter(calendar);

        TextView date = (TextView) findViewById(R.id.text_view_date);
        date.setText(currentDateString);
        currentDate = currentDateString;
        saveDate(currentDateString);
        Toast.makeText(this, currentDate, Toast.LENGTH_SHORT).show();
        // reload the page
        loadCardBackground();
        loadCardTitle();
        loadCardTime();
        updateRightButtonColor();
        updateLeftButtonColor();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        saveDateStateAndReloadPage();
    }

    public void saveDate(String currentDateString) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("current_date", currentDateString);
        editor.commit();
    }

    public void loadDate() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        calendar = Calendar.getInstance();
        currentDate = sharedPreferences.getString("current_date", TimeManager.dateFormatter(calendar));

        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);
    }

    private void loadCardBackground() {
        ImageView card1background = findViewById(R.id.card1background);
        ImageView card2background = findViewById(R.id.card2background);
        ImageView card3background = findViewById(R.id.card3background);
        ImageView card4background = findViewById(R.id.card4background);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String imgPath1 = sharedPreferences.getString(currentDate + "_meal1_imgPath", "");
        String imgPath2 = sharedPreferences.getString(currentDate + "_meal2_imgPath", "");
        String imgPath3 = sharedPreferences.getString(currentDate + "_meal3_imgPath", "");
        String imgPath4 = sharedPreferences.getString(currentDate + "_meal4_imgPath", "");
        loadImageFromPath(card1background, imgPath1, currentDate.replace("/", "_") + "_meal1.jpg");
        loadImageFromPath(card2background, imgPath2, currentDate.replace("/", "_") + "_meal2.jpg");
        loadImageFromPath(card3background, imgPath3, currentDate.replace("/", "_") + "_meal3.jpg");
        loadImageFromPath(card4background, imgPath4, currentDate.replace("/", "_") + "_meal4.jpg");
    }

    private void loadImageFromPath(ImageView background, String imgPath, String name) {
        if (!imgPath.isEmpty()) {
            try {
                File f = new File(imgPath, name);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                if (b != null) {
                    Bitmap mutable = b.copy(Bitmap.Config.ARGB_8888, true);
                    background.setImageBitmap(ImageManager.darkenBitMap(mutable));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            background.setImageResource(android.R.color.transparent);
        }
    }

    private void loadCardTitle() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_title = sharedPreferences.getString(currentDate + "_meal1_title", "");
        String meal2_title = sharedPreferences.getString(currentDate + "_meal2_title", "");
        String meal3_title = sharedPreferences.getString(currentDate + "_meal3_title", "");
        String meal4_title = sharedPreferences.getString(currentDate + "_meal4_title", "");
        TextView card1text = findViewById(R.id.card1text);
        TextView card2text = findViewById(R.id.card2text);
        TextView card3text = findViewById(R.id.card3text);
        TextView card4text = findViewById(R.id.card4text);
        setTitle(card1text, meal1_title, getResources().getString(R.string.breakfast));
        setTitle(card2text, meal2_title, getResources().getString(R.string.lunch));
        setTitle(card3text, meal3_title, getResources().getString(R.string.dinner));
        setTitle(card4text, meal4_title, getResources().getString(R.string.snack));
    }

    private void setTitle(TextView view, String text, String hint) {
        if (!text.isEmpty()) {
            view.setTextColor(getResources().getColor(R.color.white));
            view.setText(text.toUpperCase());
        } else {
            if (view.getId() != R.id.card4text)
                view.setTextColor(getResources().getColor(R.color.colorDashboardDeep));
            view.setText(hint);
        }
    }

    private void loadCardTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_time = sharedPreferences.getString(currentDate + "_meal1_time", "");
        String meal2_time = sharedPreferences.getString(currentDate + "_meal2_time", "");
        String meal3_time = sharedPreferences.getString(currentDate + "_meal3_time", "");
        String meal4_time = sharedPreferences.getString(currentDate + "_meal4_time", "");
        TextView card1time = findViewById(R.id.card1time);
        TextView card2time = findViewById(R.id.card2time);
        TextView card3time = findViewById(R.id.card3time);
        TextView card4time = findViewById(R.id.card4time);
        setTime(card1time, meal1_time);
        setTime(card2time, meal2_time);
        setTime(card3time, meal3_time);
        setTime(card4time, meal4_time);
    }

    private void setTime(TextView view, String text) {
        if (!text.isEmpty()) {
            view.setText(text);
        } else {
            view.setText("");
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
