package com.manduannabelle.www.fooddiary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class SingleMeal2Activity extends UtilityActivity {
    public static final String EXTRA_PATH = "com.manduannabelle.www.fooddiary.EXTRA_PATH";
    public static final String EXTRA_TEXT = "com.manduannabelle.www.fooddiary.EXTRA_TEXT";
    public static final String SHARED_PREFS = "sharedPrefs";
    private ImageView meal2image;
    private Bitmap selectedImage;
    private String imgPath;
    private EditText editTitle;
    private EditText editNote;
    private String meal2_title = "";
    private String meal2_note = "";
    private Boolean imgSet;
    private TextView time;
    private String meal2_time;
    ImageManager imgManager = new ImageManager(SingleMeal2Activity.this);
    String currentDateFull;
    String currentDateShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemeal2);

        // to support hiding the keyboard when clicked outside
        setupUI(findViewById(R.id.meal2));

        editTitle = findViewById(R.id.meal2_title);
        editNote = findViewById(R.id.meal2_note);
        meal2image = findViewById(R.id.meal2_image);
        time = findViewById(R.id.meal2_time);

        // for the date on the toolbar
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();

        currentDateFull = sharedPreferences.getString("current_date", TimeManager.dateFormatter(calendar));
        currentDateShort = sharedPreferences.getString("current_date_short", TimeManager.dateFormatterShort(calendar));
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDateFull);

        TimeManager.setDefaultTime(time);
        loadImageIndicator();
        loadData();
        updateViews();
        takePhoto();
        retake();

        // go back
        ImageButton backButton = findViewById(R.id.toolbar_back);
        backButton.setOnClickListener((View v) -> backToMain());
        setMealTime();
    }

    public void setMealTime() {
        time.setOnClickListener(new View.OnClickListener() {
            int currentHour;
            int currentMinute;

            @Override
            public void onClick(View view) {
                TimeManager.timePickerImplementer(currentHour, currentMinute, SingleMeal2Activity.this, time);
            }
        });
    }

    public void loadImageIndicator() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        imgSet = sharedPreferences.getBoolean(currentDateShort + "_meal2_imgSet", false);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save data
        editor.putString(currentDateShort + "_meal2_title", editTitle.getText().toString());
        editor.putString(currentDateShort + "_meal2_note", editNote.getText().toString());
        editor.putString(currentDateShort + "_meal2_time", time.getText().toString());

        editor.apply();

        //Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void savePhoto() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (selectedImage != null)
            editor.putString(currentDateShort + "_meal2_imgPath", imgManager.saveImageToInternalStorage(selectedImage, 2, currentDateShort));
        editor.apply();

        //Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        meal2_title = sharedPreferences.getString(currentDateShort + "_meal2_title", getResources().getString(R.string.meal2title));
        meal2_note = sharedPreferences.getString(currentDateShort + "_meal2_note", "");
        imgPath = sharedPreferences.getString(currentDateShort + "_meal2_imgPath", "");
        meal2_time = sharedPreferences.getString(currentDateShort + "_meal2_time", "");
    }

    public void updateViews() {
        editTitle.setText(meal2_title);
        editNote.setText(meal2_note);
        if (imgSet)
            imgManager.loadImageFromStorage(imgPath, 2, meal2image, currentDateShort);
        time.setText(meal2_time);
    }

    public void backToMain() {
        if (meal2_title.isEmpty()) {
            editTitle.setText(getResources().getString(R.string.meal2title));
        }
        saveData();
        loadData();

        // switch from SingleMeal2Activity to MainActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        /*intent.putExtra(EXTRA_TEXT, meal2_title);

        intent.putExtra(EXTRA_PATH, imgPath);*/
        startActivity(intent);
    }

    /* ----------------------------------- PHOTO TAKING ------------------------------- */
    public void takePhoto() {
        // take photo
        if (!imgSet) {
            Intent intent = new Intent(this, ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
            startActivityForResult(intent, 1213);
        }
    }

    public void retake() {
        // retake photo
        TextView retake = findViewById(R.id.toolbar_retake);
        retake.setOnClickListener((View v) -> {
            imgSet = false;
            imgManager.saveImageIndicator(2, imgSet, currentDateShort);
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(currentDateShort + "_meal2_imgPath", null);
            selectedImage = null;
            editor.apply();
            imgPath = sharedPreferences.getString(currentDateShort + "_meal2_imgPath", "");
            meal2image.setImageResource(android.R.color.transparent);
            takePhoto();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            selectedImage = BitmapFactory.decodeFile(filePath);
            if (selectedImage.getHeight() > selectedImage.getWidth()) {
                selectedImage = ImageManager.rotateBitmap(selectedImage);
            }
            meal2image.setImageBitmap(selectedImage);
            TimeManager.setDefaultTime(time);
            imgSet = true;
            imgManager.saveImageIndicator(2, imgSet, currentDateShort);
            savePhoto();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }
}