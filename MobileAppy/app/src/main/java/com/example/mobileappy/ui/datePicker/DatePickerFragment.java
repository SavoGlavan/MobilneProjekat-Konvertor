package com.example.mobileappy.ui.datePicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog dpd;
    public void setMinDate(long d){
        dpd.getDatePicker().setMinDate(d);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    public static DatePickerFragment newInstance(long num, String type) {

        Bundle args = new Bundle();
        args.putLong("date",num);
        args.putString("type",type);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle mArgs=getArguments();
        String mType=mArgs.getString("type");
        long mDate=mArgs.getLong("date");

        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        dpd=new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener)getTargetFragment(),year,month,day);
        //dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        if(mType.equals("end")){
        //dpd.getDatePicker().setMaxDate(mDate);
        dpd.getDatePicker().setMinDate(mDate);
        dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        }
        if(mType.equals("start")){
            //dpd.getDatePicker().setMaxDate(mDate);
            dpd.getDatePicker().setMaxDate(mDate);
        }
        if(mType.equals("conversion")){
            dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        }
        return dpd;
    }
}
