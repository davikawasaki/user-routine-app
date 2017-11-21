package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;

public class RoutineListActivity extends AppCompatActivity {

    // Static request variables
    private static final int REQUEST_NEW_ROUTINE = 1;

    // Routine Id from Shared Preferences
    private int routineInCourseId = 0;

    private ListView listViewRoutines;

    // Routine Array Adapter List
    private ArrayAdapter<Routine> adapterRoutineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.list_routines_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        listViewRoutines = (ListView) findViewById(R.id.listViewMainRoutinesList);
        fillRoutineList();
        checkRoutineInCourse();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.routine_list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(routineInCourseId != 0) {
            menu.findItem(R.id.itemMenuRoutineListActivityNew).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            case R.id.itemMenuRoutineListActivityNew:
                addNewRoutine();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_ROUTINE && resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public void addNewRoutine() {
        Intent intent = new Intent(this, RegisterRoutineActivity.class);
        intent.putExtra(RegisterRoutineActivity.MODE, RegisterRoutineActivity.NEW);
        startActivityForResult(intent, REQUEST_NEW_ROUTINE);
    }

    private void checkRoutineInCourse() {
        // Get routine in course ID in shared preferences
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        routineInCourseId = sharedPref.getInt(getString(
                R.string.register_routine_txt_in_course), routineInCourseId);

        if(routineInCourseId != 0) {
            // Disabling add routine
            invalidateOptionsMenu();
        }
    }

    private void fillRoutineList() {
        List<Routine> list = null;

        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            list = connection.getRoutineDAO()
                    .queryBuilder()
                    .orderBy(Routine.ID, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        adapterRoutineList = new ArrayAdapter<Routine>(this,
                android.R.layout.simple_list_item_1,
                list);

        listViewRoutines.setAdapter(adapterRoutineList);
    }
}
