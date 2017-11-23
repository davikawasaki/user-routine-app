package me.davikawasaki.routineapp.userroutineapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by kawasaki on 19/11/17.
 */

/**
 * TimePicker Fragment class with DialogFragment inheritance.
 * Enable TimePickerDialog instance with attachment to any activity,
 * which will have to implement OnDateSetListener interface.
 * @see android.app.DialogFragment
 */
public class TimePickerFragment extends DialogFragment {

    // Activity instance for fragment attachment
    private Activity mActivity;
    // TimePicker onTimeSet Listener instance
    private TimePickerDialog.OnTimeSetListener mListener;

    /**
     * Attach TimePicker to activity passed.
     * Cast activity to onTimeSet Listener, acting like a glue between activity and fragment.
     *
     * ClassCastException will be triggered if
     * OnTimeSetListener interface isn't implemented in binded activity.
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        try {
            mListener = (TimePickerDialog.OnTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
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
     * OnCreateDialog instantiate a new TimePickerDialog with current Date.
     * Check if user preference is set to 24-hour format to create a TimePickerDialog.
     * @param savedInstanceState
     * @return TimePickerDialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(mActivity, mListener, hour, minute,
                DateFormat.is24HourFormat(mActivity));
    }

}
