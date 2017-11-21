package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesRoutine;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsDateTime;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

public class MainActivity extends AppCompatActivity {

    // Static request variables
    private static final int REQUEST_NEW_ROUTINE    = 1;
    private static final int REQUEST_CHANGE_ROUTINE = 2;
    private static final int REQUEST_ROUTINE_LIST   = 3;

    // Routine Id from Shared Preferences
    private int routineInCourseId = 0;

    // Layout components
    private TextView originPlaceText;
    private TextView originDateTimeText;
    private TextView destinationPlaceText;
    private ListView listViewRoutines;
    private TextView noRoutinesRegistered;

    // Routine Array Adapter List
    private ArrayAdapter<Routine> adapterRoutineList;
    private List<Routine> routineList;

    // Routine instance
    private Routine routine;

    // Routine position on listView
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewRoutines = (ListView) findViewById(R.id.listViewMainRoutines);
        noRoutinesRegistered = (TextView) findViewById(R.id.textMainNoRoutine);

        listViewRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRoutine(position);
            }
        });

        fillRoutineList();
        getRoutineInCourse();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(routineInCourseId != 0) {
            menu.findItem(R.id.itemMenuMainActivityNew).setVisible(false);
            menu.findItem(R.id.itemMenuMainActivityRoutines).setEnabled(false);
        } else {
            menu.findItem(R.id.itemMenuMainActivityNew).setVisible(true);
            menu.findItem(R.id.itemMenuMainActivityRoutines).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemMenuMainActivityNew:
                addNewRoutine();
                return true;
            case R.id.itemMenuMainActivityPlaces:
                seePlacesList();
                return true;
            case R.id.itemMenuMainActivityRoutines:
                seeRoutinesList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == REQUEST_NEW_ROUTINE || requestCode == REQUEST_ROUTINE_LIST)
                && resultCode == Activity.RESULT_OK){
            getRoutineInCourse();
        } else if(requestCode == REQUEST_CHANGE_ROUTINE && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            int routineId = bundle.getInt(RegisterRoutineActivity.ID);

            routineList.remove(selectedPosition);
            routineList.add(selectedPosition, ServicesRoutine.getRoutineFromId(routineId, this));
            selectedPosition = -1;

            adapterRoutineList.notifyDataSetChanged();
        }
    }

    public void addNewRoutine() {
        Intent intent = new Intent(this, RegisterRoutineActivity.class);
        intent.putExtra(RegisterRoutineActivity.MODE, RegisterRoutineActivity.NEW);
        startActivityForResult(intent, REQUEST_NEW_ROUTINE);
    }

    public void seePlacesList() {
        Intent intent = new Intent(this, PlaceListActivity.class);
        startActivity(intent);
    }

    public void seeRoutinesList() {
        Intent intent = new Intent(this, RoutineListActivity.class);
        startActivityForResult(intent, REQUEST_ROUTINE_LIST);
    }

    private void changeRoutine(int position){

        selectedPosition = position;

        Routine routine = routineList.get(selectedPosition);

        Intent intent = new Intent(this, RegisterRoutineActivity.class);

        intent.putExtra(RegisterRoutineActivity.MODE, RegisterRoutineActivity.CHANGE);
        intent.putExtra(RegisterRoutineActivity.ID, routine.getId());

        startActivityForResult(intent, REQUEST_CHANGE_ROUTINE);
    }

    private void getRoutineInCourse() {
        // Get routine in course ID in shared preferences
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        routineInCourseId = sharedPref.getInt(getString(
                R.string.register_routine_txt_in_course), routineInCourseId);

        if(routineInCourseId == 0) return;

        // Get routine object from ID if exists
        routine = ServicesRoutine.getRoutineFromId(routineInCourseId, this);
        if(routine == null) return;

        // If routine object exists, change to transit layout in main
        setContentView(R.layout.activity_main_transit);

        // Get routine text views from transit layout
        originPlaceText      = (TextView) findViewById(R.id.textMainTransitFromInput);
        originDateTimeText   = (TextView) findViewById(R.id.textMainTransitFromDateTimeInput);
        destinationPlaceText = (TextView) findViewById(R.id.textMainTransitToInput);

        // Fill routine text views
        originPlaceText.setText(routine.getOriginPlace().toString());
        destinationPlaceText.setText(routine.getDestinationPlace().toString());
        originDateTimeText.setText(UtilsDateTime.convertDateTimeToString(routine.getStartDateTime()));

        // Disabling list-add routine buttons
        invalidateOptionsMenu();
    }

    public void cancelRoutine(View view) {

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {

                                    DatabaseHelper connection = DatabaseHelper
                                            .getInstance(MainActivity.this);

                                    int result = connection.getRoutineDAO()
                                            .deleteById(routineInCourseId);

                                    // Check if routine was removed
                                    if(result == 1) {
                                        resetActivityMain();
                                    } else
                                        Toast.makeText(MainActivity.this,
                                                R.string.txt_error_ormlite, Toast.LENGTH_SHORT)
                                                .show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_cancel_routine_modal), listener);

    }

    public void finishRoutine(View view) {

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {

                                    DatabaseHelper connection = DatabaseHelper
                                            .getInstance(MainActivity.this);

                                    if(routine == null) {
                                        routine = ServicesRoutine.getRoutineFromId(routineInCourseId, MainActivity.this);
                                        if(routine == null) break;
                                    }

                                    routine.setEndDateTime(Calendar.getInstance().getTime());

                                    int result = connection.getRoutineDAO().update(routine);

                                    // Check if routine was removed
                                    if(result == 1) {
                                        resetActivityMain();
                                    } else Toast.makeText(MainActivity.this,
                                            R.string.txt_error_ormlite, Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_finish_routine_modal), listener);

    }

    private void fillRoutineList() {
        routineList = null;

        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            routineList = connection.getRoutineDAO()
                    .queryBuilder()
                    .orderBy(Routine.ID, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(routineList.size() > 0) noRoutinesRegistered.setVisibility(View.GONE);

        adapterRoutineList = new ArrayAdapter<Routine>(this,
                android.R.layout.simple_list_item_1,
                routineList);

        listViewRoutines.setAdapter(adapterRoutineList);
    }

    private void resetActivityMain() {
        // Reset routineInCourseId and shared preferences
        routineInCourseId = 0;
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.register_routine_txt_in_course), routineInCourseId);
        editor.commit();

        // Reset menu and layout
        invalidateOptionsMenu();
        setContentView(R.layout.activity_main);

        // Load updated routine list
        listViewRoutines = (ListView) findViewById(R.id.listViewMainRoutines);
        fillRoutineList();
    }
}
