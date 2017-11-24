package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.fragments.DatePickerFragment;
import me.davikawasaki.routineapp.userroutineapp.fragments.TimePickerFragment;
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesPlace;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesRoutine;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsDateTime;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

/**
 * Register/Update Routine Activity.
 * Routine activity with registration and update. Implements DatePicker and TimePicker Dialogs.
 * @see android.support.v7.app.AppCompatActivity
 * @see DatePickerDialog
 */
public class RegisterRoutineActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /*********************************************************************/
    /***************************** VARIABLES *****************************/
    /*********************************************************************/

    // Static request variables
    public static final String MODE   = "MODO";
    public static final String ID     = "ID";
    public static final int    NEW    = 1;
    public static final int    CHANGE = 2;

    // Registration/Update routine layout components
    private TextView textRegisterRoutineTitle;
    private Spinner  spinnerOriginPlace;
    private Spinner  spinnerDestinationPlace;
    private Button   buttonSaveRoutine;

    // Update exclusive routine layout components
    private Button   buttonOriginDateTime;
    private Button   buttonDestinationDateTime;
    private TextView textOriginDateTimeTitle;
    private TextView textOriginDateTime;
    private TextView textDestinationDateTimeTitle;
    private TextView textDestinationDateTime;

    // Routine instance and places options
    private Routine routine;
    private List<Place> placeList;

    // Update Routine Date and time variables
    private int originYear;
    private int originMonth;
    private int originDay;
    private int destinationYear;
    private int destinationMonth;
    private int destinationDay;
    private Date originDate;
    private Date destinationDate;

    // Button click and intent mode flags
    private int dateTimeButtonClick = 0;
    private int mode;

    /*********************************************************************/
    /************************* ACTIVITY LISTENERS ************************/
    /*********************************************************************/

    /**
     * On RegisterRoutineActivity instantiation implements sequence of scripts.
     * 1. Set content view with registerPlaceType layout;
     * 2. Get actionBar and set its title and a back button with ic_back_white drawable;
     * 3. Get textView, editText, spinner and button elements from id;
     * 4. Set handlers for buttons, since they use the same selectDateTime method;
     * 5. Fill spinners with place options;
     * 6. Get activity mode from incoming intent;
     * 7. Edit mode: get routine by ID from database inside intent bundle. If routine
     *               isn't returned, close activity;
     * 8. Edit mode: select properly spinner option from originPlace name position;
     * 9. Edit mode: select properly spinner option from destinationPlace name position;
     * 10. Edit mode: copy routine origin and destination date time to originDate and destinationDate.
     *                This is done in case user only updates places and doesn't update dates;
     * 11. Edit mode: Update action bar title, button text and main title;
     * 12. New mode: instantiate a new routine and hide origin/destination DateTime textViews.
     * @param savedInstanceState
     * @see ServicesRoutine
     * @see ServicesPlace
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Step 1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_routine);

        // Step 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.register_routine_txt_bar_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        // Step 3
        textRegisterRoutineTitle     = (TextView) findViewById(R.id.textRegisterRoutineTitle);
        textOriginDateTimeTitle      = (TextView) findViewById(R.id.textRegisterRoutineToDateTitle);
        textDestinationDateTimeTitle = (TextView) findViewById(R.id.textRegisterRoutineFromDateTitle);
        buttonSaveRoutine            = (Button)   findViewById(R.id.buttonRegisterRoutineSave);
        spinnerOriginPlace           = (Spinner)  findViewById(R.id.spinnerRegisterRoutineFrom);
        spinnerDestinationPlace      = (Spinner)  findViewById(R.id.spinnerRegisterRoutineTo);
        buttonOriginDateTime         = (Button)   findViewById(R.id.buttonRegisterRoutineFromDate);
        buttonDestinationDateTime    = (Button)   findViewById(R.id.buttonRegisterRoutineToDate);
        textOriginDateTime           = (TextView) findViewById(R.id.textRegisterRoutineFromDateInput);
        textDestinationDateTime      = (TextView) findViewById(R.id.textRegisterRoutineToDateInput);

        // Step 4
        buttonOriginDateTime.setOnClickListener(buttonOriginDateTimeHandler);
        buttonDestinationDateTime.setOnClickListener(buttonDestinationDateTimeHandler);

        // Step 5
        fillPlacesSpinner();

        // Step 6
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mode = bundle.getInt(MODE);

        if (mode == CHANGE) {

            // Step 7
            routine = ServicesRoutine.getRoutineFromId(bundle.getInt(ID), this);
            if(routine == null) {
                finish();
                return;
            }

            // Step 8
            ArrayAdapter<Place> arraySpinnerOriginPlace = (ArrayAdapter<Place>)
                    spinnerOriginPlace.getAdapter();
            int originPos = ServicesPlace.getPositionFromArrayAdapter(
                    arraySpinnerOriginPlace, routine.getOriginPlace().toString());
            spinnerOriginPlace.setSelection(originPos);
            textOriginDateTime.setText(UtilsDateTime.convertDateTimeToString(
                    routine.getStartDateTime()));

            // Step 9
            ArrayAdapter<Place> arraySpinnerDestinationPlace = (ArrayAdapter<Place>)
                    spinnerDestinationPlace.getAdapter();
            int destinationPos = ServicesPlace.getPositionFromArrayAdapter(
                    arraySpinnerDestinationPlace, routine.getDestinationPlace().toString());
            spinnerDestinationPlace.setSelection(destinationPos);
            textDestinationDateTime.setText(UtilsDateTime.convertDateTimeToString(
                    routine.getEndDateTime()));

            // Step 10
            originDate      = new Date(routine.getStartDateTime().getTime());
            destinationDate = new Date(routine.getEndDateTime().getTime());

            // Step 11
            actionBar.setTitle(R.string.update_routine_txt_bar_title);
            textRegisterRoutineTitle.setText(R.string.update_routine_txt_title);
            buttonSaveRoutine.setText(R.string.update_routine_txt_save_button);
        } else {
            // Step 12
            routine = new Routine();
            textOriginDateTimeTitle.setVisibility(View.GONE);
            textOriginDateTime.setVisibility(View.GONE);
            buttonOriginDateTime.setVisibility(View.GONE);
            textDestinationDateTimeTitle.setVisibility(View.GONE);
            textDestinationDateTime.setVisibility(View.GONE);
            buttonDestinationDateTime.setVisibility(View.GONE);
        }
    }

    /**
     * View onClickListener for originDateTime button handler.
     * Listen to onClick cycle of button with an instance of onClickListener.
     */
    View.OnClickListener buttonOriginDateTimeHandler = new View.OnClickListener() {

        /**
         * onClick listener for originDateTime button.
         * Set dateTimeButtonClick flag and open dateTimePicker.
         * @param view
         */
        @Override
        public void onClick(View view) {
            dateTimeButtonClick = 1;
            selectDateTime(view);
        }

    };

    /**
     * View onClickListener for destinationDateTime button handler.
     * Listen to onClick cycle of button with an instance of onClickListener.
     */
    View.OnClickListener buttonDestinationDateTimeHandler = new View.OnClickListener() {

        /**
         * onClick listener for destinationDateTime button.
         * Set dateTimeButtonClick flag and open dateTimePicker.
         * @param view
         */
        @Override
        public void onClick(View view) {
            dateTimeButtonClick = 2;
            selectDateTime(view);
        }

    };

    /**
     * Date Listener for setted date from DatePicker.
     * 1. Checks if dateTimeButtonClick flag was from origin or destination and set year/month/day
     *    variables for origin or destination;
     * 2. After date variables setted, instantiate and show a timePickerFragment.
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Step 1
        if(dateTimeButtonClick == 1) {
            originYear = year;
            originMonth = month;
            originDay = day;
        } else if(dateTimeButtonClick == 2) {
            destinationYear = year;
            destinationMonth = month;
            destinationDay = day;
        } else return;

        // Step 2
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * Time Listener for setted time from TimePicker.
     * 1. Checks if dateTimeButtonClick flag was from origin or destination, set originDate
     *    and textOriginDateTime;
     * 2. Otherwise do return method.
     * @param timePicker
     * @param hours
     * @param minutes
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        // Step 1
        if(dateTimeButtonClick == 1) {
            originDate = UtilsDateTime.setDateTime(originYear, originMonth, originDay,
                    hours, minutes);
            textOriginDateTime.setText(UtilsDateTime.convertDateTimeToString(originDate));

            // Reset dateTimeButtonClick
            dateTimeButtonClick = 0;
        } else if(dateTimeButtonClick == 2) {
            destinationDate = UtilsDateTime.setDateTime(destinationYear, destinationMonth,
                    destinationDay, hours, minutes);
            textDestinationDateTime.setText(UtilsDateTime.convertDateTimeToString(destinationDate));

            // Reset dateTimeButtonClick
            dateTimeButtonClick = 0;
        }
        // Step 2
        else return;
    }

    /*********************************************************************/
    /********************* MENU/ACTION BAR LISTENERS *********************/
    /*********************************************************************/

    /**
     * Listener to menu options item selected.
     * Android Home Button: finish activity.
     * @param item
     * @return selectedStatus
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) finish();
        return true;
    }

    /*********************************************************************/
    /************************ BUTTON CLICK METHODS ***********************/
    /*********************************************************************/

    /**
     * Button call to cancel/close an activity.
     * @param view
     */
    public void closeActivity(View view) {
        finish();
    }

    /**
     * Save/update routine.
     * 1. Get originPlace and destinationPlace from spinners;
     * 2. Check origin & destination places equality, returning a modal error and canceling method;
     * 3. Edit mode: set routine object with collected updated fields;
     * 4. Edit mode: if updated routine is saved into database, insert ID into intent bundle
     *               and finish activity with success;
     * 5. Edit mode: if updated routine isn't saved into database, emit a modal error;
     * 6. New mode: instantiate a new current date to originDateTime;
     * 7. New mode: set routine object with collected fields;
     * 8. New mode: if new routine is saved into database, save current routine to sharedPreferences
     *              and finish activity with success;
     * 9. New mode: if new routine isn't saved into database, emit a modal error.
     * @param view
     * @see ServicesRoutine
     */
    public void save(View view) {

        // Step 1
        Place originPlace      = (Place) spinnerOriginPlace.getSelectedItem();
        Place destinationPlace = (Place) spinnerDestinationPlace.getSelectedItem();

        // Step 2
        if(originPlace.getName() == destinationPlace.getName()) {
            UtilsGUI.modalError(this, R.string.register_routine_txt_origin_destination_equals);
            return;
        }

        if (mode == CHANGE) {
            // Step 3
            routine.setOriginPlace(originPlace);
            routine.setDestinationPlace(destinationPlace);
            routine.setName(originPlace + " > " + destinationPlace +
                    " - " + UtilsDateTime.convertDateToString(originDate));
            routine.setStartDateTime(originDate);
            routine.setEndDateTime(destinationDate);

            // Step 4
            if(ServicesRoutine.updateRoutine(routine, this)) {
                Intent intent = new Intent();
                intent.putExtra(ID, routine.getId());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            // Step 5
            else Toast.makeText(this, R.string.txt_error_msg_update_routine_toast, Toast.LENGTH_SHORT).show();

        } else {
            // Step 6
            Date originDateStart = Calendar.getInstance().getTime();

            // Step 7
            routine.setOriginPlace(originPlace);
            routine.setDestinationPlace(destinationPlace);
            routine.setName(originPlace + " > " + destinationPlace +
                    " - " + UtilsDateTime.convertDateToString(originDateStart));
            routine.setStartDateTime(originDateStart);

            // Step 8
            if(ServicesRoutine.createRoutine(routine, this)) {
                saveRoutineInCourse(routine);
                setResult(Activity.RESULT_OK);
                finish();
            }
            // Step 9
            else Toast.makeText(this, R.string.txt_error_msg_register_routine_toast, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Instantiate and show a DatePickerFragment.
     * @param view
     */
    public void selectDateTime(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    /*********************************************************************/
    /************** PLACE TYPE SPINNER MANIPULATION METHODS **************/
    /*********************************************************************/

    /**
     * Fill spinners with place options collected from database records.
     * 1. Get place list records from database;
     * 2. If place list is returned properly, insert place list into spinner adapters;
     * 3. If an error is emitted from database, emit a modal error and finish activity;
     * @see ServicesPlace
     */
    private void fillPlacesSpinner() {

        // Step 1
        placeList = null;
        placeList = ServicesPlace.getPlaceList(this);

        // Step 2
        if(placeList.size() > 1) {
            ArrayAdapter<Place> spinnerPlaceAdapter = new ArrayAdapter<Place>(this,
                    android.R.layout.simple_list_item_1,
                    placeList);
            spinnerOriginPlace.setAdapter(spinnerPlaceAdapter);
            spinnerDestinationPlace.setAdapter(spinnerPlaceAdapter);
        }
        // Step 3
        else {
            Toast.makeText(this, R.string.register_routine_txt_empty_places, Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    }

    /*********************************************************************/
    /***************** ROUTINE IN COURSE LAYOUT METHODS ******************/
    /*********************************************************************/

    /**
     * Get database saved routine and save into sharedPreferences.
     * @param routine
     */
    private void saveRoutineInCourse(Routine routine) {
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.register_routine_txt_in_course), routine.getId());
        editor.commit();
    }

}
