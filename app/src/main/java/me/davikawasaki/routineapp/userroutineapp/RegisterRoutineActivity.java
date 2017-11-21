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
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsDateTime;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

public class RegisterRoutineActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // Static request variables
    public static final String MODE   = "MODO";
    public static final String ID     = "ID";
    public static final int    NEW    = 1;
    public static final int    CHANGE = 2;

    // Layout components
    private TextView textRegisterRoutineTitle;
    private Spinner  spinnerOriginPlace;
    private Spinner  spinnerDestinationPlace;
    private Button   buttonOriginDateTime;
    private Button   buttonDestinationDateTime;
    private TextView textOriginDateTimeTitle;
    private TextView textOriginDateTime;
    private TextView textDestinationDateTimeTitle;
    private TextView textDestinationDateTime;
    private Button   buttonSaveRoutine;

    // Routine instance and places options
    private Routine routine;
    private List<Place> placeList;
    private Calendar date;

    // Date and time variables
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_routine);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.register_routine_txt_bar_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        textRegisterRoutineTitle     = (TextView) findViewById(R.id.textRegisterRoutineTitle);
        textOriginDateTimeTitle      = (TextView) findViewById(R.id.textRegisterRoutineToDateTitle);
        textDestinationDateTimeTitle = (TextView) findViewById(R.id.textRegisterRoutineFromDateTitle);
        buttonSaveRoutine            = (Button) findViewById(R.id.buttonRegisterRoutineSave);

        spinnerOriginPlace        = (Spinner) findViewById(R.id.spinnerRegisterRoutineFrom);
        spinnerDestinationPlace   = (Spinner) findViewById(R.id.spinnerRegisterRoutineTo);
        buttonOriginDateTime      = (Button) findViewById(R.id.buttonRegisterRoutineFromDate);
        buttonDestinationDateTime = (Button) findViewById(R.id.buttonRegisterRoutineToDate);
        textOriginDateTime        = (TextView) findViewById(R.id.textRegisterRoutineFromDateInput);
        textDestinationDateTime   = (TextView) findViewById(R.id.textRegisterRoutineToDateInput);

        // Set handlers for buttons, since they use the same selectDateTime method
        buttonOriginDateTime.setOnClickListener(buttonOriginDateTimeHandler);
        buttonDestinationDateTime.setOnClickListener(buttonDestinationDateTimeHandler);

        // Fill spinner with place options
        fillPlacesSpinner();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mode = bundle.getInt(MODE);

        if (mode == CHANGE) {

            int id = bundle.getInt(ID);

            try {
                DatabaseHelper connection = DatabaseHelper.getInstance(this);

                routine = connection.getRoutineDAO().queryForId(id);

                if(routine == null) return;

                // Update places objects from foreign key
                connection.getPlaceDAO().refresh(routine.getOriginPlace());
                connection.getPlaceDAO().refresh(routine.getDestinationPlace());

                // Select properly spinner option from originPlace name
                ArrayAdapter<Place> arraySpinnerOriginPlace = (ArrayAdapter<Place>)
                        spinnerOriginPlace.getAdapter();
                int originPos = ServicesPlace.getPositionFromArrayAdapter(
                        arraySpinnerOriginPlace, routine.getOriginPlace().toString());
                spinnerOriginPlace.setSelection(originPos);
                textOriginDateTime.setText(UtilsDateTime.convertDateTimeToString(
                        routine.getStartDateTime()));

                // Select properly spinner option from destinationPlace name
                ArrayAdapter<Place> arraySpinnerDestinationPlace = (ArrayAdapter<Place>)
                        spinnerDestinationPlace.getAdapter();
                int destinationPos = ServicesPlace.getPositionFromArrayAdapter(
                        arraySpinnerDestinationPlace, routine.getDestinationPlace().toString());
                spinnerDestinationPlace.setSelection(destinationPos);
                textDestinationDateTime.setText(UtilsDateTime.convertDateTimeToString(
                        routine.getEndDateTime()));

                // Copy routine origin and destination date time to originDate and destinationDate
                // This is done in case user only updates places and doesn't update dates
                originDate      = new Date(routine.getStartDateTime().getTime());
                destinationDate = new Date(routine.getEndDateTime().getTime());

            } catch (SQLException e) {
                e.printStackTrace();
            }

            actionBar.setTitle(R.string.update_routine_txt_bar_title);
            textRegisterRoutineTitle.setText(R.string.update_routine_txt_title);
            buttonSaveRoutine.setText(R.string.update_routine_txt_save_button);

        } else {
            routine = new Routine();

            // Hide Origin & Destination DateTime TextViews
            textOriginDateTimeTitle.setVisibility(View.GONE);
            textOriginDateTime.setVisibility(View.GONE);
            buttonOriginDateTime.setVisibility(View.GONE);
            textDestinationDateTimeTitle.setVisibility(View.GONE);
            textDestinationDateTime.setVisibility(View.GONE);
            buttonDestinationDateTime.setVisibility(View.GONE);
        }
    }

    View.OnClickListener buttonOriginDateTimeHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dateTimeButtonClick = 1;
            selectDateTime(view);
        }
    };

    View.OnClickListener buttonDestinationDateTimeHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dateTimeButtonClick = 2;
            selectDateTime(view);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home) finish();
        return true;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if(dateTimeButtonClick == 1) {
            originYear = year;
            originMonth = month;
            originDay = day;
        } else if(dateTimeButtonClick == 2) {
            destinationYear = year;
            destinationMonth = month;
            destinationDay = day;
        } else return;

        // After date set, set and show a timePickerFragment
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
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
        } else return;
    }

    public void closeActivity(View view) {
        finish();
    }

    /**
     * Fill spinner with place options.
     */
    private void fillPlacesSpinner() {

        placeList = null;

        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            placeList = connection.getPlaceDAO()
                    .queryBuilder()
                    .orderBy(Place.ID, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(placeList.size() > 1) {
            ArrayAdapter<Place> spinnerPlaceAdapter = new ArrayAdapter<Place>(this,
                    android.R.layout.simple_list_item_1,
                    placeList);

            spinnerOriginPlace.setAdapter(spinnerPlaceAdapter);
            spinnerDestinationPlace.setAdapter(spinnerPlaceAdapter);
        } else {
            Toast.makeText(this, R.string.register_routine_txt_empty_places, Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    /**
     * Save routine
     * @param view
     */
    public void save(View view) {
        Place originPlace = (Place) spinnerOriginPlace.getSelectedItem();
        Place destinationPlace = (Place) spinnerDestinationPlace.getSelectedItem();

        if(originPlace.getName() == destinationPlace.getName()) {
            UtilsGUI.modalError(this, R.string.register_routine_txt_origin_destination_equals);
            return;
        }

        if (mode == CHANGE) {

            try {

                DatabaseHelper connection = DatabaseHelper.getInstance(this);

                routine.setOriginPlace(originPlace);
                routine.setDestinationPlace(destinationPlace);
                routine.setName(originPlace + " > " + destinationPlace +
                        " - " + UtilsDateTime.convertDateToString(originDate));
                routine.setStartDateTime(originDate);
                routine.setEndDateTime(destinationDate);
                connection.getRoutineDAO().update(routine);

                Intent intent = new Intent();
                intent.putExtra(ID, routine.getId());

                setResult(Activity.RESULT_OK, intent);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Date originDateStart = Calendar.getInstance().getTime();

            try {

                DatabaseHelper connection = DatabaseHelper.getInstance(this);

                routine.setOriginPlace(originPlace);
                routine.setDestinationPlace(destinationPlace);
                routine.setName(originPlace + " > " + destinationPlace +
                        " - " + UtilsDateTime.convertDateToString(originDateStart));
                routine.setStartDateTime(originDateStart);
                connection.getRoutineDAO().create(routine);
                saveRoutineInCourse(routine);

                setResult(Activity.RESULT_OK);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void selectDateTime(View view) {
        System.out.println("Enter to open datePickerFragment");
        Log.d("MSG", "Enter to open datePickerFragment");
        Log.i("MSG", "Enter to open datePickerFragment");
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    private void saveRoutineInCourse(Routine routine) {
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.register_routine_txt_in_course), routine.getId());
        editor.commit();
    }

}
