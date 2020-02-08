package com.manduannabelle.www.fooddiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.Calendar;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class SingleMeal3Activity extends UtilityActivity{
    public static final String EXTRA_PATH = "com.manduannabelle.www.fooddiary.EXTRA_PATH";
    public static final String EXTRA_TEXT = "com.manduannabelle.www.fooddiary.EXTRA_TEXT";
    public static final String SHARED_PREFS = "sharedPrefs";
    private ImageView meal3image;
    private Bitmap selectedImage;
    private String imgPath;
    private EditText editTitle;
    private EditText editNote;
    private String meal3_title = "";
    private String meal3_note = "";
    private Boolean imgSet;
    private TextView time;
    private String meal3_time;
    ImageManager imgManager = new ImageManager(SingleMeal3Activity.this);
    String currentDateFull;
    String currentDateShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemeal3);

        // to support hiding the keyboard when clicked outside
        setupUI(findViewById(R.id.meal3));

        editTitle = findViewById(R.id.meal3_title);
        editNote = findViewById(R.id.meal3_note);
        meal3image = findViewById(R.id.meal3_image);
        time = findViewById(R.id.meal3_time);

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
        remove();

        // go back
        ImageButton backButton = findViewById(R.id.toolbar_back);
        backButton.setOnClickListener((View v) ->backToMain());
        setMealTime();
    }


    public void setMealTime() {
        time.setOnClickListener(new View.OnClickListener() {
            int currentHour;
            int currentMinute;

            @Override
            public void onClick(View view) {
                TimeManager.timePickerImplementer(currentHour, currentMinute, SingleMeal3Activity.this, time);
            }
        });
    }

    public void loadImageIndicator() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        imgSet = sharedPreferences.getBoolean(currentDateShort + "_meal3_imgSet", false);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save data
        editor.putString(currentDateShort + "_meal3_title", editTitle.getText().toString());
        editor.putString(currentDateShort + "_meal3_note", editNote.getText().toString());
        editor.putString(currentDateShort + "_meal3_time", time.getText().toString());

        editor.apply();

        //Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void savePhoto() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (selectedImage != null)
            editor.putString(currentDateShort + "_meal3_imgPath", imgManager.saveImageToInternalStorage(selectedImage, 3, currentDateShort));
        editor.apply();

        //Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        meal3_title = sharedPreferences.getString(currentDateShort + "_meal3_title", getResources().getString(R.string.meal3title));
        meal3_note = sharedPreferences.getString(currentDateShort + "_meal3_note", "");
        imgPath = sharedPreferences.getString(currentDateShort + "_meal3_imgPath", "");
        meal3_time = sharedPreferences.getString(currentDateShort + "_meal3_time", "");
    }

    public void updateViews() {
        editTitle.setText(meal3_title);
        editNote.setText(meal3_note);
        if (imgSet)
            imgManager.loadImageFromStorage(imgPath, 3, meal3image, currentDateShort);
        time.setText(meal3_time);
    }

    public void backToMain() {
        if (editTitle.getText().toString().isEmpty()) {
            editTitle.setText(getResources().getString(R.string.meal3title));
        }
        // set default time
        if (time.getText().toString().isEmpty()) {
            TimeManager.setDefaultTime(time);
        }
        saveData();
        loadData();

        // switch from SingleMeal3Activity to MainActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void clearPage() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(currentDateShort + "_meal3_title", "");
        editor.putString(currentDateShort + "_meal3_note", "");
        editor.remove(currentDateShort + "_meal3_imgPath");
        editor.putString(currentDateShort + "_meal3_time", "");
        imgSet = false;
        imgManager.saveImageIndicator(3, imgSet, currentDateShort);
        editor.apply();
        loadData();
        updateViews();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void remove() {
        ImageButton removeBtn = findViewById(R.id.toolbar_delete);
        removeBtn.setOnClickListener((View v) -> {
            AlertDialog.Builder dial = new AlertDialog.Builder(SingleMeal3Activity.this);
            dial.setMessage(getResources().getString(R.string.removeSure)).setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clearPage();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog alert = dial.create();
            alert.setTitle(getResources().getString(R.string.remove));
            alert.show();
        });
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
            imgManager.saveImageIndicator(3, imgSet, currentDateShort);
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(currentDateShort + "_meal3_imgPath", null);
            selectedImage = null;
            editor.apply();
            imgPath = sharedPreferences.getString(currentDateShort + "_meal3_imgPath", "");
            meal3image.setImageResource(android.R.color.transparent);
            // set time to be default time
            time.setText("");
            TimeManager.setDefaultTime(time);
            sharedPreferences.edit().putString(currentDateShort + "_meal3_time", time.getText().toString()).apply();
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
            meal3image.setImageBitmap(selectedImage);
            TimeManager.setDefaultTime(time);
            time.setVisibility(View.VISIBLE);
            imgSet = true;
            imgManager.saveImageIndicator(3, imgSet, currentDateShort);
            savePhoto();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }
}