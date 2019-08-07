package com.manduannabelle.www.fooddiary;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeManager {

    protected static void setDefaultTime(TextView time) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);
        time.setText(formatTime(calendar, currentHour, currentMin));
    }

    protected static String formatTime(Calendar calendar, int hourOfDay, int min) {
        String amPm;
        if (hourOfDay >= 12 && hourOfDay < 24) {
            amPm = "PM";
        } else {
            amPm = "AM";
        }
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0) hour = 12;
        return hour + ":" + String.format(Locale.getDefault(),"%02d", min) + amPm;
    }

    protected static void timePickerImplementer(int currentHour, int currentMinute, Activity activity, final TextView time) {
        final Calendar calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.setText(TimeManager.formatTime(calendar, hourOfDay, minute));
            }
        }, currentHour, currentMinute, false);
        timePickerDialog.show();
    }

    protected static String dateFormatter(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");
        String dayOfWeek = simpleDateFormat.format(calendar.getTime());
        //String unformattedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        String dateShort = dateFormatterShort(calendar);
        return dayOfWeek + " " + dateShort.substring(0, dateShort.length() - 3);
    }

    protected static String dateFormatterShort(Calendar calendar) {
        Format f = new SimpleDateFormat("M/d/yy", Locale.US);
        String date = f.format(calendar.getTime());
        return date;
    }
}
