package me.davikawasaki.routineapp.userroutineapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by kawasaki on 19/11/17.
 */

/**
 * DatePicker Fragment class with DialogFragment inheritance.
 * Enable DatePickerDialog instance with attachment to any activity,
 * which will have to implement OnDateSetListener interface.
 * @see android.app.DialogFragment
 */
public class DatePickerFragment extends DialogFragment {

    // Activity instance for fragment attachment
    private Activity mActivity;
    // DatePicker onDateSet Listener instance
    private DatePickerDialog.OnDateSetListener mListener;

    /**
     * Attach DatePicker to activity passed.
     * Cast activity to onDateSet Listener, acting like a glue between activity and fragment.
     *
     * ClassCastException will be triggered if
     * OnDateSetListener interface isn't implemented in binded activity.
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        try {
            mListener = (DatePickerDialog.OnDateSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
        }
    }

    /**
     * Detach activity from fragment when finished.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * OnCreateDialog instantiate a new DatePickerDialog with current Date.
     * @param savedInstanceState
     * @return DatePickerDialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(mActivity, mListener, year, month, day);
    }

}
