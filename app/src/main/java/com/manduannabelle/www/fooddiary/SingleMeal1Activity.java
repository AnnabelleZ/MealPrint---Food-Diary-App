package com.manduannabelle.www.fooddiary;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.ConcurrentModificationException;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class SingleMeal1Activity extends AppCompatActivity {
    //public static final String EXTRA_DRAWABLE = "com.manduannabelle.www.fooddiary.EXTRA_DRAWABLE";
    public static final String EXTRA_PATH = "com.manduannabelle.www.fooddiary.EXTRA_PATH";
    public static final String EXTRA_TEXT = "com.manduannabelle.www.fooddiary.EXTRA_TEXT";
    public static final String SHARED_PREFS = "sharedPrefs";
    private ImageView meal1image;
    private Bitmap selectedImage;
    private String imgPath;
    private EditText editTitle;
    private EditText editNote;
    private String meal1_title = "";
    private String meal1_note = "";
    private Boolean imgSet;
    private TextView time;
    private String meal1_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemeal1);

        editTitle = findViewById(R.id.meal1_title);
        editNote = findViewById(R.id.meal1_note);

        // for the date on the toolbar
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);

        meal1image = findViewById(R.id.meal1_image);
        time = findViewById(R.id.meal1_time);
        loadImageIndicator();
        loadData();
        updateViews();
        takePhoto();
        retake();
        // go back
        ImageButton backButton = findViewById(R.id.toolbar_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain();
            }
        });
        setMealTime();
        if (imgSet) {
            time.setVisibility(View.VISIBLE);
        }
    }

    public void setDefaultTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);
        time.setText(formatTime(calendar, currentHour, currentMin));
    }

    public String formatTime(Calendar calendar, int hourOfDay, int min) {
        String amPm;
        if (hourOfDay >= 12 && hourOfDay < 24) {
            amPm = "PM";
        } else {
            amPm = "AM";
        }
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0) hour = 12;
        return hour + ":" + String.format("%02d", min) + amPm;
    }

    public void setMealTime() {
        time.setOnClickListener(new View.OnClickListener() {
            int currentHour;
            int currentMinute;

            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(SingleMeal1Activity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        /*String amPm;
                        if (hourOfDay > 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        int hour = calendar.get(Calendar.HOUR);
                        if (hour == 0) hour = 12;*/
                        time.setText(formatTime(calendar, hourOfDay, minute));
                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }
        });
    }

    public void takePhoto() {
        // take photo
        if (!imgSet) {
            Intent intent = new Intent(this, ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
            startActivityForResult(intent, 1213);
            imgSet = true;
            saveImageIndicator();
        }
    }

    public void saveImageIndicator() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("meal1_imgSet", imgSet);
        editor.apply();
    }

    public void loadImageIndicator() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        imgSet = sharedPreferences.getBoolean("meal1_imgSet", false);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save data
        editor.putString("meal1_title", editTitle.getText().toString());
        editor.putString("meal1_note", editNote.getText().toString());
        if (selectedImage != null)
            editor.putString("meal1_imgPath", saveImageToInternalStorage(MainActivity.rotateBitmap(selectedImage)));
        editor.putString("meal1_time", time.getText().toString());

        editor.apply();

        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        meal1_title = sharedPreferences.getString("meal1_title", "Breakfast");
        meal1_note = sharedPreferences.getString("meal1_note", "");
        imgPath = sharedPreferences.getString("meal1_imgPath", "");
        meal1_time = sharedPreferences.getString("meal1_time", "");
    }

    public void updateViews() {
        editTitle.setText(meal1_title);
        editNote.setText(meal1_note);
        if (imgSet)
            loadImageFromStorage(imgPath);
        time.setText(meal1_time);
    }


    private String saveImageToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/FoodDiary/app_data/imageDir
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(dir, "profile.jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dir.getAbsolutePath();
    }

    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            if (b != null)
                meal1image.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void backToMain() {
        saveData();
        loadData();

        // switch from SingleMeal1Activity to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_TEXT, meal1_title);

        intent.putExtra(EXTRA_PATH, imgPath);
        startActivity(intent);
    }

    public void retake() {
        // retake photo
        TextView retake = findViewById(R.id.toolbar_retake);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgSet = false;
                saveImageIndicator();
                takePhoto();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            selectedImage = BitmapFactory.decodeFile(filePath);
            meal1image.setImageBitmap(MainActivity.rotateBitmap(selectedImage));
            setDefaultTime();
            time.setVisibility(View.VISIBLE);
        }
    }
}