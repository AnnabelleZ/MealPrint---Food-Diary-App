package com.manduannabelle.www.fooddiary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    String currentDateFull;
    String currentDateShort;
    Calendar calendar;
    Dialog onItsWay;
    Button accept;
    ImageView closePopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onItsWay = new Dialog(this);

        ImageButton imgButton = findViewById(R.id.toolbar_calendar);
        imgButton.setOnClickListener((View v) -> {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
        });

        // for the date on the toolbar
        // reset the selected day in the SharedPreferences to the current date
        calendar = Calendar.getInstance();
        currentDateFull = TimeManager.dateFormatter(calendar);
        currentDateShort = TimeManager.dateFormatterShort(calendar);
        saveDate();
        // load selected day
        loadDate();

        // for tool bar to show
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add onclick listener to cards
        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        CardView card4 = findViewById(R.id.card4);

        card1.setOnClickListener((View v) ->
                startActivity(new Intent(MainActivity.this, SingleMeal1Activity.class)));
        card2.setOnClickListener((View v) ->
                startActivity(new Intent(MainActivity.this, SingleMeal2Activity.class)));
        card3.setOnClickListener((View v) ->
                startActivity(new Intent(MainActivity.this, SingleMeal3Activity.class)));
        card4.setOnClickListener((View v) ->
                startActivity(new Intent(MainActivity.this, SingleMeal4Activity.class)));

        setListenerChevronLeft();
        setListenerChevronRight();
        saveDateStateAndReloadPage();
        deleteOldPictures();
    }

    private void setListenerChevronLeft() {
        ImageButton yesterday = findViewById(R.id.toolbar_nav_left);
        yesterday.setOnClickListener((View v) -> goToYesterday());
    }

    private void setListenerChevronRight() {
        ImageButton tomorrow = findViewById(R.id.toolbar_nav_right);
        tomorrow.setOnClickListener((View v) -> goToTomorrow());
    }

    /**
     * sets onClickListener that shows the OnTheWay Popup for the button passed.
     * @param button the button to set the onClickListener for
     **/
    private void onItsWayPopUp(ImageButton button) {
        button.setOnClickListener((View v) ->{
            onItsWay.setContentView(R.layout.on_the_way_popup);
            closePopup = onItsWay.findViewById(R.id.close_on_the_way);
            accept = onItsWay.findViewById(R.id.accept_on_its_way);

            closePopup.setOnClickListener((View v1) -> onItsWay.dismiss());

            accept.setOnClickListener((View v2) -> onItsWay.dismiss());

            onItsWay.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            onItsWay.show();
        });
    }

    /**
     * updates the color of chevron_left button and the listener
     **/
    private void updateLeftButtonColor() {
        ImageButton yesterday = findViewById(R.id.toolbar_nav_left);
        if (!dateAfterMinDate(calendar.getTime())) {
            // set < button to gray
            yesterday.setBackground(getResources().getDrawable(R.drawable.ic_chevron_left_gray));
            onItsWayPopUp(yesterday);
        } else {
            yesterday.setBackground(getResources().getDrawable(R.drawable.ic_chevron_left_white));
            setListenerChevronLeft();
        }
    }

    /**
     * updates the color of chevron_right button and the listener
     **/
    private void updateRightButtonColor() {
        ImageButton tomorrow = findViewById(R.id.toolbar_nav_right);
        if (!dateBeforeMaxDate(calendar.getTime())) {
            // set > button to gray
            tomorrow.setBackground(getResources().getDrawable(R.drawable.ic_chevron_right_gray));
            onItsWayPopUp(tomorrow);
        } else {
            tomorrow.setBackground(getResources().getDrawable(R.drawable.ic_chevron_right_white));
            setListenerChevronRight();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadCardBackground();
        loadCardTitle();
        loadCardTime();
    }


    /**
     * if the date is within the date range then reload the page to show the content of yesterday
     **/
    private void goToYesterday() {
        if (dateAfterMinDate(calendar.getTime())) {
            calendar.add(Calendar.DATE, -1);
            saveDateStateAndReloadPage();
        }
    }

    /**
     * check if the date is after the minDate(7 days ago)
     * @param date the date which is checked
     **/
    private Boolean dateAfterMinDate(Date date) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, -6);
        Date minDate = today.getTime();
        return date.after(minDate);
    }

    /**
     * check if the date is before the maxDate(today)
     * @param date the date which is checked
     **/
    private Boolean dateBeforeMaxDate(Date date) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, -1);
        Date maxDate = today.getTime();
        return date.before(maxDate);
    }

    /**
     * if the date is within the date range then reload the page to show the content of tomorrow
     **/
    private void goToTomorrow() {
        if (dateBeforeMaxDate(calendar.getTime())) {
            calendar.add(Calendar.DATE, 1);
            saveDateStateAndReloadPage();
        }

    }

    /**
     * Save the date that's targeted by the user and update the page content based on the date
     **/
    private void saveDateStateAndReloadPage() {
        String currentDateString = TimeManager.dateFormatter(calendar);

        TextView date = findViewById(R.id.text_view_date);
        date.setText(currentDateString);
        currentDateFull = currentDateString;
        currentDateShort = TimeManager.dateFormatterShort(calendar);
        saveDate();
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

    /**
     * update the date targeted by the user in sharedPreferences
     **/
    private void saveDate() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("current_date", currentDateFull);
        editor.putString("current_date_short", currentDateShort);
        editor.apply();
    }

    /**
     * load the date targeted by the user from sharedPreferences to the global variables
     * update the text_view_date
     **/
    private void loadDate() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        calendar = Calendar.getInstance();
        currentDateFull = sharedPreferences.getString("current_date", TimeManager.dateFormatter(calendar));
        currentDateShort = sharedPreferences.getString("current_date", TimeManager.dateFormatterShort(calendar));

        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDateFull);
    }

    /**
     * load the images from the internal storage and update the background of cards
     **/
    private void loadCardBackground() {
        ImageView card1background = findViewById(R.id.card1background);
        ImageView card2background = findViewById(R.id.card2background);
        ImageView card3background = findViewById(R.id.card3background);
        ImageView card4background = findViewById(R.id.card4background);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String imgPath1 = sharedPreferences.getString(currentDateShort + "_meal1_imgPath", "");
        String imgPath2 = sharedPreferences.getString(currentDateShort + "_meal2_imgPath", "");
        String imgPath3 = sharedPreferences.getString(currentDateShort + "_meal3_imgPath", "");
        String imgPath4 = sharedPreferences.getString(currentDateShort + "_meal4_imgPath", "");
        loadImageFromPath(card1background, imgPath1, currentDateShort.replace("/", "_") + "_meal1.jpg");
        loadImageFromPath(card2background, imgPath2, currentDateShort.replace("/", "_") + "_meal2.jpg");
        loadImageFromPath(card3background, imgPath3, currentDateShort.replace("/", "_") + "_meal3.jpg");
        loadImageFromPath(card4background, imgPath4, currentDateShort.replace("/", "_") + "_meal4.jpg");
    }

    /**
     * load images from the internal storage and set as background of the cards
     * @param background the background whose image will be set to the loaded image
     * @param imgPath the path to load the image from
     * @param name the name of the image file
     **/
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

    /**
     * Delete the picture from 7 days ago from the internal storage as well as
     * the relevant data from the SharedPreferences
     **/
    private void deleteOldPictures() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File imgPath = cw.getDir("imageDir", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < 4; i++) {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DATE, -7);
            Format f = new SimpleDateFormat("M/d/yy", Locale.US);
            String date = f.format(today.getTime());
            File file = new File(imgPath,date.replace("/","_") + "_meal" + (i+1) + ".jpg");
            file.delete();

            editor.remove(date + "_meal" + (i+1) + "_title");
            editor.remove(date + "_meal" + (i+1) + "_note");
            editor.remove(date + "_meal" + (i+1) + "_time");
            editor.remove(date + "_meal" + (i+1) + "_imgPath");
            editor.remove(date + "_meal" + (i+1) + "_imgSet");
            editor.apply();
        }
    }

    /**
     * load card titles from the SharedPreferences and update the background of cards
     **/
    private void loadCardTitle() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_title = sharedPreferences.getString(currentDateShort + "_meal1_title", "Breakfast");
        String meal2_title = sharedPreferences.getString(currentDateShort + "_meal2_title", "Lunch");
        String meal3_title = sharedPreferences.getString(currentDateShort + "_meal3_title", "Dinner");
        String meal4_title = sharedPreferences.getString(currentDateShort + "_meal4_title", "Snacks");
        TextView card1text = findViewById(R.id.card1text);
        TextView card2text = findViewById(R.id.card2text);
        TextView card3text = findViewById(R.id.card3text);
        TextView card4text = findViewById(R.id.card4text);
        setTitle(1, card1text, meal1_title, getResources().getString(R.string.breakfast));
        setTitle(2, card2text, meal2_title, getResources().getString(R.string.lunch));
        setTitle(3, card3text, meal3_title, getResources().getString(R.string.dinner));
        setTitle(4, card4text, meal4_title, getResources().getString(R.string.snack));
    }

    public Boolean hasContent(int meal) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Boolean imgSet = sharedPreferences.getBoolean(currentDateShort + "_meal" + meal + "_imgSet", false);
        String note = sharedPreferences.getString(currentDateShort + "_meal" + meal + "_note", "");
        return !note.isEmpty() || imgSet;
    }

    /**
     * Update the color and text of card titles
     * @param view the TextView whose text to be updated
     * @param text the text to be used to update view
     * @param hint original hint text on the card
     **/
    private void setTitle(int meal, TextView view, String text, String hint) {
        if (hasContent(meal)) {
            view.setTextColor(getResources().getColor(R.color.white));
            view.setText(text.toUpperCase());
        } else {
            if (view.getId() != R.id.card4text)
                view.setTextColor(getResources().getColor(R.color.colorDashboardDeep));
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            sharedPreferences.edit().putString(currentDateShort + "_meal" + meal + "_title", "").apply();
            sharedPreferences.edit().putString(currentDateShort + "_meal" + meal + "_time", "").apply();
            view.setText(hint);
        }
    }

    /**
     * load the time the image of the meal was added from SharedPreferences and update
     * the TextView that shows the time with it
     **/
    private void loadCardTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String meal1_time = sharedPreferences.getString(currentDateShort + "_meal1_time", "");
        String meal2_time = sharedPreferences.getString(currentDateShort + "_meal2_time", "");
        String meal3_time = sharedPreferences.getString(currentDateShort + "_meal3_time", "");
        String meal4_time = sharedPreferences.getString(currentDateShort + "_meal4_time", "");
        TextView card1time = findViewById(R.id.card1time);
        TextView card2time = findViewById(R.id.card2time);
        TextView card3time = findViewById(R.id.card3time);
        TextView card4time = findViewById(R.id.card4time);
        setTime(1, card1time, meal1_time);
        setTime(2, card2time, meal2_time);
        setTime(3, card3time, meal3_time);
        setTime(4, card4time, meal4_time);
    }

    /**
     * updates the TextView that shows time on the card
     * @param view the TextView whose text to be updated
     * @param text the text to be used to update view
     **/
    private void setTime(int meal, TextView view, String text) {
        if (!text.isEmpty() && hasContent(meal)) {
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

    /**
     * Get the background images of the cards from the internal storage
     * @return an ArrayList of all the images in the form of Bitmaps
     **/
    private ArrayList<Bitmap> getImages() {
        ArrayList<Bitmap> parts = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        for (int i = 0; i < 4; i++) {
            String imgPath = sharedPreferences.getString(currentDateShort + "_meal" + (i+1) +"_imgPath", "");
            if (!imgPath.isEmpty()) {
                try {
                    File f = new File(imgPath, currentDateShort.replace("/", "_") + "_meal" + (i+1) + ".jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    if (b != null) {
                        Bitmap resized = Bitmap.createScaledBitmap(b, 400, 300, true);
                        parts.add(resized);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return parts;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                ArrayList<Bitmap> parts = getImages();
                String dateStr = TimeManager.dateFormatter(calendar);
                if (parts.size() == 0) {
                    Toast.makeText(this, getResources().getString(R.string.addmorephotoalarm), Toast.LENGTH_SHORT).show();
                } else {
                    shareImage(createCollage(parts, dateStr));
                }
                return true;
            /*case R.id.theme:
                Toast.makeText(this, "Theme selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.language:
                Toast.makeText(this, "Language selected", Toast.LENGTH_SHORT).show();
                return true;*/
            case R.id.feedback:
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                return true;
            case R.id.rateus:
                startActivity(new Intent(getApplicationContext(), RateUsActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * create the collage with the logo, the date targeted,
     * and background images of each card to share to other programs
     * @param parts an ArrayList of background images in the form of Bitmaps
     * @param dateStr string represents the currentDate full
     **/
    private Bitmap createCollage(ArrayList<Bitmap> parts, String dateStr) {
        Bitmap logo = ImageManager.getBitmapFromDrawable(getResources().getDrawable(R.mipmap.ic_toast_round));
        Bitmap logoResized = Bitmap.createScaledBitmap(logo, 50, 50, true);
        Bitmap date = ImageManager.textAsBitmap(dateStr, (float) logoResized.getHeight() / 2, getResources().getColor(R.color.colorDashboardDeep));
        Bitmap result = Bitmap.createBitmap(parts.get(0).getWidth(), parts.get(0).getHeight() * parts.size() + logoResized.getHeight() * 3/2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        canvas.drawColor(getResources().getColor(R.color.white));
        for (int i = 0; i < parts.size(); i++) {
            canvas.drawBitmap(parts.get(i), 0, (float) logoResized.getHeight() * 3/2 + parts.get(i).getHeight() * i, paint);
        }
        canvas.drawBitmap(logoResized, logoResized.getWidth(), (float) logoResized.getHeight() / 4, paint);
        canvas.drawBitmap(date, (float) logoResized.getWidth() * 5/2, (float) logoResized.getHeight() / 2, paint);
        return result;
    }

    /**
     * share the collage image to other programs
     * @param icon the collage image to share to other programs
     **/
    private void shareImage(Bitmap icon) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            if (uri != null) {
                outstream = getContentResolver().openOutputStream(uri);
                icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                if (outstream != null) outstream.close();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }

}
