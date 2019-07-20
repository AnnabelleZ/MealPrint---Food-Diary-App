package com.manduannabelle.www.fooddiary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class SingleMeal1Activity extends AppCompatActivity {
    //private LinearLayout card1background;
    private ImageView meal1image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemeal1);

        // for the date on the toolbar
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);

        //card1background = findViewById(R.id.card1background);
        meal1image = findViewById(R.id.meal1_image);

        Intent intent = new Intent(this, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);
        startActivityForResult(intent, 1213);

        // retake photo
        TextView retake = findViewById(R.id.toolbar_retake);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restart();
            }
        });
    }

    public void restart()
    {
        this.recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            //Drawable d = new BitmapDrawable(getResources(), selectedImage);
            //card1background.setBackground(d);
            meal1image.setImageBitmap(selectedImage);
            Matrix matrix = new Matrix();
            meal1image.setScaleType(ImageView.ScaleType.MATRIX);   //required
            matrix.postRotate((float) 90, meal1image.getDrawable().getBounds().width()/2, meal1image.getDrawable().getBounds().height()/2);
            meal1image.setImageMatrix(matrix);
        }
    }
}