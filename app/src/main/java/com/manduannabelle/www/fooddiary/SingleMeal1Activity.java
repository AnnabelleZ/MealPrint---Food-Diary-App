package com.manduannabelle.www.fooddiary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class SingleMeal1Activity extends AppCompatActivity {
    public static final String EXTRA_DRAWABLE = "com.manduannabelle.www.fooddiary.EXTRA_DRAWABLE";
    public static final String EXTRA_TEXT = "com.manduannabelle.www.fooddiary.EXTRA_TEXT";
    private ImageView meal1image;
    private Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemeal1);

        // for the date on the toolbar
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);

        // retake photo
        TextView retake = findViewById(R.id.toolbar_retake);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restart();
            }
        });

        // take photo
        meal1image = findViewById(R.id.meal1_image);
        if (!(BooleanValues.meal1ImageSet || BooleanValues.meal1TitleSet)) {
            Intent intent = new Intent(this, ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
            startActivityForResult(intent, 1213);
        }

        // go back
        ImageButton backButton = findViewById(R.id.toolbar_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain();
            }
        });
    }

    public void backToMain() {
        // send card1 title
        EditText editTitle = (EditText) findViewById(R.id.meal1_title_editview);
        String title = editTitle.getText().toString();
        if (!title.isEmpty()) {
            BooleanValues.meal1TitleSet = true;
        }

        // switch from SingleMeal1Activity to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        if (BooleanValues.meal1TitleSet) {
            intent.putExtra(EXTRA_TEXT, title);
        }
        if (BooleanValues.meal1ImageSet) {
            // send card1 background
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 80, bs);
            intent.putExtra(EXTRA_DRAWABLE, bs.toByteArray());
        }
        startActivity(intent);
    }

    public void restart() {
        BooleanValues.meal1ImageSet = false;
        BooleanValues.meal1TitleSet = false;
        this.recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            selectedImage = BitmapFactory.decodeFile(filePath);
            //Drawable d = new BitmapDrawable(getResources(), selectedImage);
            //card1background.setBackground(d);
            meal1image.setImageBitmap(selectedImage);
            Matrix matrix = new Matrix();
            meal1image.setScaleType(ImageView.ScaleType.MATRIX);   //required
            matrix.postRotate((float) -90, meal1image.getDrawable().getBounds().width()/2, meal1image.getDrawable().getBounds().height()/2);
            meal1image.setImageMatrix(matrix);
            BooleanValues.meal1ImageSet = true;
            //card1background.setBackground(meal1image.getDrawable());
        }
    }
}