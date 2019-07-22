package com.manduannabelle.www.fooddiary;

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
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.Calendar;
import android.content.Intent;

public class MainActivity extends AppCompatActivity{

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

        // set card1 title
        Intent intent = getIntent();
        if (intent.hasExtra(SingleMeal1Activity.EXTRA_TEXT)) {
            TextView card1text = findViewById(R.id.card1text);
            card1text.setTextColor(getResources().getColor(R.color.white));
            String text = intent.getStringExtra(SingleMeal1Activity.EXTRA_TEXT);
            card1text.setText(text);
            card1text.setText(text.toUpperCase());
        }
        // set card1 background
        if (intent.hasExtra(SingleMeal1Activity.EXTRA_DRAWABLE)) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    intent.getByteArrayExtra(SingleMeal1Activity.EXTRA_DRAWABLE), 0,
                    intent.getByteArrayExtra(SingleMeal1Activity.EXTRA_DRAWABLE).length);
            Drawable d = new BitmapDrawable(getResources(), rotateBitmap(bitmap));
            LinearLayout card1background = findViewById(R.id.card1background);
            d.setColorFilter(getResources().getColor(R.color.darkgray), PorterDuff.Mode.DARKEN);
            card1background.setBackground(d);
        }
    }

    public static Bitmap rotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, source.getWidth(),source.getHeight(), true);
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

    /*public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_addphoto);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.takephoto:
                Toast.makeText(this, "Take photo clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.choosefromgallery:
                Toast.makeText(this, "Choose from gallery clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }*/
}
