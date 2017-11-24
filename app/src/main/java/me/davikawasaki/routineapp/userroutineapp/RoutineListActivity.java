package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
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
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

public class RoutineListActivity extends AppCompatActivity {

    /*********************************************************************/
    /***************************** VARIABLES *****************************/
    /*********************************************************************/

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

    /*********************************************************************/
    /************************* ACTIVITY LISTENERS ************************/
    /*********************************************************************/

    /**
     * On RoutineListActivity instantiation implements sequence of scripts.
     * 1. Set content view with last routines layout;
     * 2. Get actionBar and set its title and a back button with ic_back_white drawable;
     * 3. Get listViewRoutines and insert a itemClickListener for each option;
     * 4. Set multiple choice action bar for listViewRoutines selected item(s);
     * 5. Fill Last Routines List;
     * 6. If a routine is in course, disable menu to add a new routine.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Step 1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        // Step 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.list_routines_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        // Step 3
        listViewRoutines = (ListView) findViewById(R.id.listViewMainRoutinesList);
        listViewRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRoutine(position);
            }
        });

        // Step 4
        listViewRoutines.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewRoutines.setMultiChoiceModeListener(onMultiChoiceModeListener());

        // Step 5
        fillRoutineList();

        // Step 6
        checkRoutineInCourse();
    }

    /**
     * Listener for other activity result incomes.
     * 1. If result is from a success new routine, finish activity and delegate routineInCourse
     *    to mainActivity;
     * 2. If result is from a success changed routine, recover updated ID and change it in listView;
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Step 1
        if (requestCode == REQUEST_NEW_ROUTINE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
        // Step 2
        else if (requestCode == REQUEST_CHANGE_ROUTINE && resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            int routineId = bundle.getInt(RegisterRoutineActivity.ID);

            routineList.remove(selectedPosition);
            routineList.add(selectedPosition, ServicesRoutine.getRoutineFromId(routineId, this));
            selectedPosition = -1;

            adapterRoutineList.notifyDataSetChanged();
        }
    }

    /*********************************************************************/
    /********************* MENU/ACTION BAR LISTENERS *********************/
    /*********************************************************************/

    /**
     * Menu inflater listener.
     * Choose which menu layout to be inflated on menu create options.
     * @param menu
     * @return creationStatus
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.routine_list_activity_menu, menu);
        return true;
    }

    /**
     * Listener to menu options preparation.
     * If routine is in course, hide new routine option;
     * @param menu
     * @return preparationStatus
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(routineInCourseId != 0) menu.findItem(R.id.itemMenuRoutineListActivityNew).setVisible(false);
        return true;
    }

    /**
     * Listener to menu options item selected.
     * 1. Android Home Button: finish activity;
     * 2. New Routine: redirect to new routine activity;
     * 3. Default: call super method to check for updates;
     * @param item
     * @return selectedStatus
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Step 1
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            // Step 2
            case R.id.itemMenuRoutineListActivityNew:
                addNewRoutine();
                return true;
            // Step 3
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * MultiChoiceModeListener for multiple choice selected items action bar.
     * Listen to all multiChoiceMode life cycles:
     * 1. onItemCheckedStateChanged
     * 2. onCreateActionMode
     * 3. onPrepareActionMode
     * 4. onActionItemClicked
     * 5. onDestroyActionMode
     * @return AbsListView.MultiChoiceModeListener
     */
    public AbsListView.MultiChoiceModeListener onMultiChoiceModeListener() {
        return new AbsListView.MultiChoiceModeListener() {

            /**
             * Event listener for listViewRoutines item checked.
             * 1. Get checked item and view from position of listView;
             * 2. If an item is checked, change its background color to light gray;
             * 3. Get total checked items in order to set title with plural quantity or not;
             * 4. Invalidate the action mode and refresh menu content.
             * @param mode
             * @param position
             * @param id
             * @param checked
             */
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position,
                                                  long id, boolean checked) {

                // Step 1
                boolean selected = listViewRoutines.isItemChecked(position);
                View view = listViewRoutines.getChildAt(position);

                // Step 2
                if (selected) view.setBackgroundColor(Color.LTGRAY);
                else view.setBackgroundColor(Color.TRANSPARENT);

                // Step 3
                int totalSelected = listViewRoutines.getCheckedItemCount();
                if (totalSelected > 0) {
                    mode.setTitle(getResources().getQuantityString(R.plurals.selected,
                            totalSelected,
                            totalSelected));
                }

                // Step 4
                mode.invalidate();

            }

            /**
             * Event listener for multiChoiceActionMode creation.
             * When entered creation life cycle, it inflates a menu with multiple selection layout.
             * @param mode
             * @param menu
             * @return creationStatus
             */
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.main_multiple_selected_menu, menu);
                return true;
            }

            /**
             * Event listener for multiChoiceActionMode preparation.
             * When preparing actionMode construction, hide edit button for two or more selected items.
             * @param mode
             * @param menu
             * @return preparationStatus
             */
            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                if (listViewRoutines.getCheckedItemCount() > 1) menu.getItem(0).setVisible(false);
                else menu.getItem(0).setVisible(true);
                return true;
            }

            /**
             * Event listener for multiChoiceActionMode clicked item.
             * 1. Edit Option: Check if routine list item is checked and redirect to
             *    change routine activity - then finish multiChoice mode;
             * 2. Remove Option: Check if routine list items are checked and asks
             *    if the delete command must proceed;
             * 3. Return false for any other non-existing options.
             * @param mode
             * @param item
             * @return clickedStatus
             */
            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                switch(item.getItemId()) {
                    // Step 1
                    case R.id.itemMenuMainSelectedActivityEdit:
                        for (int position = listViewRoutines.getChildCount(); position >= 0;
                             position--) {
                            if (listViewRoutines.isItemChecked(position)) {
                                changeRoutine(position);
                            }
                        }
                        mode.finish();
                        return true;
                    // Step 2
                    case R.id.itemMenuMainSelectedActivityRemove:
                        removeRoutines(mode);
                        return true;
                    // Step 3
                    default:
                        return false;
                }
            }

            /**
             * Event listener for multiChoiceActionMode destruction.
             * Uncheck each Routine ListView items, changing their colors to transparent.
             * @param mode
             */
            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                for (int position = 0; position < listViewRoutines.getChildCount(); position++) {
                    View view = listViewRoutines.getChildAt(position);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
    }

    /*********************************************************************/
    /********************* ACTIVITY REDIRECT METHODS *********************/
    /*********************************************************************/

    /**
     * Generate activity intent to new routine page.
     * Bundle: MODE as NEW
     * RequestCode: REQUEST_NEW_ROUTINE
     */
    public void addNewRoutine() {
        Intent intent = new Intent(this, RegisterRoutineActivity.class);
        intent.putExtra(RegisterRoutineActivity.MODE, RegisterRoutineActivity.NEW);
        startActivityForResult(intent, REQUEST_NEW_ROUTINE);
    }

    /**
     * Generate activity intent to change routine page.
     * Get routine from selectedPosition and put its ID in Bundle.
     * Bundle: MODE as CHANGE / ID as routineID
     * RequestCode: REQUEST_CHANGE_ROUTINE
     * @param position
     */
    private void changeRoutine(int position) {
        selectedPosition = position;
        Routine routine = routineList.get(selectedPosition);

        Intent intent = new Intent(this, RegisterRoutineActivity.class);
        intent.putExtra(RegisterRoutineActivity.MODE, RegisterRoutineActivity.CHANGE);
        intent.putExtra(RegisterRoutineActivity.ID, routine.getId());

        startActivityForResult(intent, REQUEST_CHANGE_ROUTINE);
    }

    /*********************************************************************/
    /***************** ROUTINE LIST MANIPULATION METHODS *****************/
    /*********************************************************************/

    /**
     * Remove selected routines if user accepts dialog.
     * 1. Dialog interface onClickListener;
     * 2. Override onClick from dialog interface listener, with acceptance and reject cases;
     * 3. Iterate listViewRoutines to get selected items to be removed and add to a RoutineList;
     * 4. Run database request to remove the routineList from last step;
     * 5. Iterate removedRoutineList from fullRoutineList to remove each item;
     * 6. Insert updated routineList into adapterRoutineList to update listView from removal;
     * 7. Nothing is done in reject case;
     * 8. Finally finish multiChoiceMode;
     * 9. Dialog call with step 1 listener.
     * @param mode
     * @see DialogInterface
     * @see ServicesRoutine
     */
    private void removeRoutines(final ActionMode mode) {

        // Step 1
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            // Step 2
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        // Step 3
                        List<Routine> removeRoutineList = new ArrayList<>();
                        for (int position = listViewRoutines.getChildCount();
                             position >= 0; position--) {
                            if (listViewRoutines.isItemChecked(position)) {
                                removeRoutineList.add(routineList.get(position));
                            }
                        }

                        // Step 4
                        ServicesRoutine.removeRoutines(removeRoutineList,
                                RoutineListActivity.this);

                        // Step 5
                        routineList = ServicesRoutine.iterateAndRemoveFromList(
                                routineList, removeRoutineList);

                        // Step 6
                        adapterRoutineList.notifyDataSetChanged();
                        adapterRoutineList = new ArrayAdapter<Routine>(
                                RoutineListActivity.this,
                                android.R.layout.simple_list_item_1,
                                routineList);
                        listViewRoutines.setAdapter(adapterRoutineList);

                        break;

                    // Step 7
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }

                // Step 8
                mode.finish();
            }
        };

        // Step 9
        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_delete_routine_modal), listener);

    }

    /**
     * Get routineList from database and fill listViewRoutines with records.
     * 1. Get routine list records from database;
     * 2. If an error is emitted from database, start empty ArrayList and emit a Toast error;
     * 3. Insert routine list into adapterRoutineList to update listView;
     * @see ServicesRoutine
     */
    private void fillRoutineList() {

        // Step 1
        routineList = null;
        routineList = ServicesRoutine.getRoutineList(this);

        // Step 2
        if(routineList == null) {
            routineList = new ArrayList<Routine>();
            Toast.makeText(this,
                    R.string.txt_error_ormlite, Toast.LENGTH_SHORT).show();
        }

        // Step 3
        adapterRoutineList = new ArrayAdapter<Routine>(this,
                android.R.layout.simple_list_item_1,
                routineList);
        listViewRoutines.setAdapter(adapterRoutineList);
    }

    /*********************************************************************/
    /***************** ROUTINE IN COURSE LAYOUT METHODS ******************/
    /*********************************************************************/

    /**
     * Get routine in course from shared preferences.
     * 1. Get routineInCourseId from shared preferences ROUTINE_IN_COURSE ID;
     * 2. Disabling add routine with optionsMenu invalidation.
     */
    private void checkRoutineInCourse() {
        // Step 1
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        routineInCourseId = sharedPref.getInt(getString(
                R.string.register_routine_txt_in_course), routineInCourseId);

        // Step 2
        if(routineInCourseId != 0) invalidateOptionsMenu();
    }

}
