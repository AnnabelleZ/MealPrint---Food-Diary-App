package com.manduannabelle.www.fooddiary;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageManager {
    protected static final String SHARED_PREFS = "sharedPrefs";
    protected static Context context;

    protected ImageManager(Context context) {
        this.context = context;
    }

    /**
     * share the collage image to other programs
     * @param meal the number for the meal
     * @param imgSet implies if the image for the meal is set or not
     * @param currentDate a string represents the targeted date
     **/
    protected void saveImageIndicator(int meal, Boolean imgSet, String currentDate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(currentDate + "_meal" + meal + "_imgSet", imgSet);
        editor.apply();
    }

    /**
     * darkens the bitmap
     * @param bm the bitmap to be darken
     **/
    protected static Bitmap darkenBitMap(Bitmap bm) {

        Canvas canvas = new Canvas(bm);
        Paint p = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        p.setColorFilter(filter);
        canvas.drawBitmap(bm, new Matrix(), p);

        return bm;
    }

    /**
     * saves the image taken by camera or loaded from gallery to internal storage
     * @param bitmapImage the image to save
     * @param meal the number for the meal
     * @param currentDate a string represents the targeted date
     **/
    protected String saveImageToInternalStorage(Bitmap bitmapImage, int meal, String currentDate) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/FoodDiary/app_data/imageDir
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(dir, currentDate.replace("/", "_") + "_meal" + meal +".jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dir.getAbsolutePath();
    }

    /**
     * Loads an image from internal storage
     * @param path path to load the image from
     * @param meal the number of meal
     * @param mealImage the ImageView to set the image for
     * @param currentDate a string represents the targeted date
     **/
    protected void loadImageFromStorage(String path, int meal, ImageView mealImage, String currentDate) {
        try {
            File f = new File(path, currentDate.replace("/", "_") + "_meal" + meal + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            if (b != null)
                mealImage.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rotates the bitmap by 270 degree
     * @param source the bitmap to be rotated
     **/
    protected static Bitmap rotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, source.getWidth(), source.getHeight(), true);
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }

    /**
     * Creates a bitmap from a drawable
     * @param drawable the drawable to create bitmap with
     **/
    protected static Bitmap getBitmapFromDrawable(Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    /**
     * Creates text bitmap
     * @param text the text to create bitmap with
     * @param textSize size of the text
     * @param textColor color of the text
     **/
    protected static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}
