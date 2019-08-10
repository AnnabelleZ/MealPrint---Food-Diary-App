package com.manduannabelle.www.fooddiary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/* code from https://stackoverflow.com/questions/10992411/how-to-add-pagelines-to-a-edittext-in-android */
public class LinedEditText extends android.support.v7.widget.AppCompatEditText
{
    private Rect mRect;
    private Paint mPaint;

    public LinedEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.colorDashboardDeep));
    }

    /**
     * This is called to draw the LinedEditText object
     * @param canvas The canvas on which the background is drawn.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        int height = getHeight();
        int curHeight = 0;
        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r);
        for (curHeight = baseline; curHeight < height;
             curHeight += getLineHeight())
        {
            canvas.drawLine(r.left, curHeight, r.right, curHeight, paint);
        }
        super.onDraw(canvas);
    }
}