package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesRoutine;

public class RoutineListActivity extends AppCompatActivity {

    // Static request variables
    public static final int REQUEST_NEW_ROUTINE    = 1;
    public static final int REQUEST_CHANGE_ROUTINE = 2;
    public static final int REQUEST_ROUTINE_LIST   = 3;

    // Routine Id from Shared Preferences
    private int routineInCourseId = 0;

    private ListView listViewRoutines;

    // Routine Array Adapter List
    private ArrayAdapter<Routine> adapterRoutineList;
    private List<Routine> routineList;

    // Routine position on listView
    private int selectedPosition = -1;

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

        listViewRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRoutine(position);
            }
        });

        listViewRoutines.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewRoutines.setMultiChoiceModeListener(onMultiChoiceModeListener());

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

    public AbsListView.MultiChoiceModeListener onMultiChoiceModeListener() {
        return new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {

                boolean selected = listViewRoutines.isItemChecked(position);

                View view = listViewRoutines.getChildAt(position);

                if (selected) view.setBackgroundColor(Color.LTGRAY);
                else view.setBackgroundColor(Color.TRANSPARENT);

                int totalSelected = listViewRoutines.getCheckedItemCount();

                if (totalSelected > 0) {

                    mode.setTitle(getResources().getQuantityString(R.plurals.selected,
                            totalSelected,
                            totalSelected));
                }

                mode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.main_multiple_selected_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                // Hide edit button for two or more selected items
                if (listViewRoutines.getCheckedItemCount() > 1) menu.getItem(0).setVisible(false);
                else menu.getItem(0).setVisible(true);
                return true;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {

                switch(item.getItemId()){
                    case R.id.itemMenuMainSelectedActivityEdit:
                        for (int position = listViewRoutines.getChildCount(); position >= 0; position--){
                            if (listViewRoutines.isItemChecked(position)){
                                changeRoutine(position);
                            }
                        }
                        mode.finish();
                        return true;

                    case R.id.itemMenuMainSelectedActivityRemove:
                        removeRoutines();
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                for (int position = 0; position < listViewRoutines.getChildCount(); position++){
                    View view = listViewRoutines.getChildAt(position);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_ROUTINE && resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK);
            finish();
        } else if (requestCode == REQUEST_CHANGE_ROUTINE && resultCode == Activity.RESULT_OK){
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

    private void changeRoutine(int position){

        selectedPosition = position;

        Routine routine = routineList.get(selectedPosition);

        Intent intent = new Intent(this, RegisterRoutineActivity.class);

        intent.putExtra(RegisterRoutineActivity.MODE, RegisterRoutineActivity.CHANGE);
        intent.putExtra(RegisterRoutineActivity.ID, routine.getId());

        startActivityForResult(intent, REQUEST_CHANGE_ROUTINE);
    }

    private void removeRoutines() {
        List<Routine> removeRoutineList = new ArrayList<>();
        for (int position = listViewRoutines.getChildCount(); position >= 0; position--){
            if (listViewRoutines.isItemChecked(position)){
                removeRoutineList.add(routineList.get(position));
            }
        }
        // Database request to remove
        ServicesRoutine.removeRoutines(removeRoutineList, this);
        // Remove items from routineList,
        // setting a new adapter according to new instance of Array List
        routineList = new ArrayList<>(ServicesRoutine.
                iterateAndRemoveFromList(routineList,removeRoutineList));
        adapterRoutineList = new ArrayAdapter<Routine>(this,
                android.R.layout.simple_list_item_1,
                routineList);
        listViewRoutines.setAdapter(adapterRoutineList);

        adapterRoutineList.notifyDataSetChanged();
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
        routineList = null;

        routineList = ServicesRoutine.getRoutineList(this);

        if(routineList == null) routineList = new ArrayList<Routine>();

        adapterRoutineList = new ArrayAdapter<Routine>(this,
                android.R.layout.simple_list_item_1,
                routineList);

        listViewRoutines.setAdapter(adapterRoutineList);
    }

    private void resetActivityRoutineList() {
        // Load updated routine list
        listViewRoutines = (ListView) findViewById(R.id.listViewMainRoutines);
        fillRoutineList();

        // Reset click listener and choice modes
        listViewRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRoutine(position);
            }
        });
        listViewRoutines.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewRoutines.setMultiChoiceModeListener(onMultiChoiceModeListener());

        adapterRoutineList.notifyDataSetChanged();
    }
}
