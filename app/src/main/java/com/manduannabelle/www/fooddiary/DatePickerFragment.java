package com.manduannabelle.www.fooddiary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // set limit to only last week!
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add( Calendar.DAY_OF_MONTH, -6 ); // Subtract 6 months
        long minDate = c.getTime().getTime(); // Twice!
        //return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        pickerDialog.getDatePicker().setMaxDate(today.getTime());
        pickerDialog.getDatePicker().setMinDate(minDate);
        return pickerDialog;
    }
}
