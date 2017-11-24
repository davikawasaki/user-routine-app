package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesRoutine;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsDateTime;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

/**
 * Main Application Activity.
 * Launcher activity with last routines and in course routine layouts.
 * @see android.support.v7.app.AppCompatActivity
 */
public class MainActivity extends AppCompatActivity {

    /*********************************************************************/
    /***************************** VARIABLES *****************************/
    /*********************************************************************/

    // Routine in Course Id from Shared Preferences
    private int routineInCourseId = 0;

    // Routine in Course Layout components
    private TextView originPlaceText;
    private TextView originDateTimeText;
    private TextView destinationPlaceText;

    // Last Routines Layout components
    private ListView listViewRoutines;

    // Last Routines Array Adapter List
    private ArrayAdapter<Routine> adapterRoutineList;
    private List<Routine> routineList;

    // Routine in Course instance
    private Routine routine;

    // Routine position on Last Routines listView
    private int selectedPosition = -1;

    /*********************************************************************/
    /************************* ACTIVITY LISTENERS ************************/
    /*********************************************************************/

    /**
     * On MainActivity instantiation implements sequence of scripts.
     * 1. Set content view with last routines layout;
     * 2. Get listViewRoutines and insert a itemClickListener for each option;
     * 3. Set multiple choice action bar for listViewRoutines selected item(s);
     * 4. Fill Last Routines List;
     * 5. If a routine is in course, get it from sharedPreferences and change to its layout.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Step 1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step 2
        listViewRoutines = (ListView) findViewById(R.id.listViewMainRoutines);
        listViewRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRoutine(position);
            }
        });

        // Step 3
        listViewRoutines.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewRoutines.setMultiChoiceModeListener(onMultiChoiceModeListener());

        // Step 4
        fillRoutineList();

        // Step 5
        getRoutineInCourse();
    }

    /**
     * Listener for other activity result incomes.
     * 1. If result is from a success new routine or routine list, try to recover routine in course;
     * 2. If result is from a success changed routine, recover updated ID and change it in listView;
     * 3. Just fill routine list.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Step 1
        if((requestCode == RoutineListActivity.REQUEST_NEW_ROUTINE ||
                requestCode == RoutineListActivity.REQUEST_ROUTINE_LIST)
                && resultCode == Activity.RESULT_OK) {
            getRoutineInCourse();
        }
        // Step 2
        else if(requestCode == RoutineListActivity.REQUEST_CHANGE_ROUTINE &&
                resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            int routineId = bundle.getInt(RegisterRoutineActivity.ID);

            routineList.remove(selectedPosition);
            routineList.add(selectedPosition, ServicesRoutine.getRoutineFromId(routineId, this));
            selectedPosition = -1;

            adapterRoutineList.notifyDataSetChanged();
        }
        // Step 3
        else {
            fillRoutineList();
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
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    /**
     * Listener to menu options preparation.
     * 1. If routine is in course, hide new routine option and disable routines option from menu;
     * 2. In no routine is in course, show new routine option and enable routines option from menu.
     * @param menu
     * @return preparationStatus
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Step 1
        if(routineInCourseId != 0) {
            menu.findItem(R.id.itemMenuMainActivityNew).setVisible(false);
            menu.findItem(R.id.itemMenuMainActivityRoutines).setEnabled(false);
        }
        // Step 2
        else {
            menu.findItem(R.id.itemMenuMainActivityNew).setVisible(true);
            menu.findItem(R.id.itemMenuMainActivityRoutines).setEnabled(true);
        }
        return true;
    }

    /**
     * Listener to menu options item selected.
     * 1. New Routine: redirect to new routine activity;
     * 2. Places List: redirect to place list activity;
     * 3. Routines List: redirect to routine list activity;
     * 4. About: redirect to about activity;
     * 5. Default: call super method to check for updates;
     * @param item
     * @return selectedStatus
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Step 1
            case R.id.itemMenuMainActivityNew:
                addNewRoutine();
                return true;
            // Step 2
            case R.id.itemMenuMainActivityPlaces:
                seePlacesList();
                return true;
            // Step 3
            case R.id.itemMenuMainActivityRoutines:
                seeRoutinesList();
                return true;
            // Step 4
            case R.id.itemMenuMainActivityAbout:
                seeAboutPage();
                return true;
            // Step 5
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
                    case R.id.itemMenuMainSelectedActivityEdit:
                        for (int position = listViewRoutines.getChildCount(); position >= 0; position--){
                            if (listViewRoutines.isItemChecked(position)){
                                changeRoutine(position);
                            }
                        }
                        mode.finish();
                        return true;

                    case R.id.itemMenuMainSelectedActivityRemove:
                        removeRoutines(mode);
                        return true;

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
                for (int position = 0; position < listViewRoutines.getChildCount(); position++){
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
        startActivityForResult(intent, RoutineListActivity.REQUEST_NEW_ROUTINE);
    }

    /**
     * Generate activity intent to places list page.
     * Bundle: -
     * RequestCode: -
     */
    public void seePlacesList() {
        Intent intent = new Intent(this, PlaceListActivity.class);
        startActivity(intent);
    }

    /**
     * Generate activity intent to routines list page.
     * Bundle: -
     * RequestCode: -
     */
    public void seeRoutinesList() {
        Intent intent = new Intent(this, RoutineListActivity.class);
        startActivityForResult(intent, RoutineListActivity.REQUEST_ROUTINE_LIST);
    }

    /**
     * Generate activity intent to about page.
     * Bundle: -
     * RequestCode: -
     */
    public void seeAboutPage() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
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

        startActivityForResult(intent, RoutineListActivity.REQUEST_CHANGE_ROUTINE);
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
                                MainActivity.this);

                        // Step 5
                        routineList = ServicesRoutine.iterateAndRemoveFromList(
                                routineList, removeRoutineList);

                        // Step 6
                        adapterRoutineList.notifyDataSetChanged();
                        adapterRoutineList = new ArrayAdapter<Routine>(
                                MainActivity.this,
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
     * 2. If routineInCourseId isn't setted (equals to zero) return and doesn't change layout activity;
     * 3. Get routine object from routineInCourseId, if doesn't exists break method;
     * 4. If routine object exists, change to routine in course layout in mainActivity;
     * 5. Get text views from routine in course layout;
     * 6. Fill routine text views with routineInCourse object;
     * 7. Disable list-add routine buttons.
     */
    private void getRoutineInCourse() {

        // Step 1
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        routineInCourseId = sharedPref.getInt(getString(
                R.string.register_routine_txt_in_course), routineInCourseId);

        // Step 2
        if(routineInCourseId == 0) return;

        // Step 3
        routine = ServicesRoutine.getRoutineFromId(routineInCourseId, this);
        if(routine == null) return;

        // Step 4
        setContentView(R.layout.activity_main_transit);

        // Step 5
        originPlaceText      = (TextView) findViewById(R.id.textMainTransitFromInput);
        originDateTimeText   = (TextView) findViewById(R.id.textMainTransitFromDateTimeInput);
        destinationPlaceText = (TextView) findViewById(R.id.textMainTransitToInput);

        // Step 6
        originPlaceText.setText(routine.getOriginPlace().toString());
        destinationPlaceText.setText(routine.getDestinationPlace().toString());
        originDateTimeText.setText(UtilsDateTime.convertDateTimeToString(routine.getStartDateTime()));

        // Step 7
        invalidateOptionsMenu();
    }

    /**
     * Cancel current routine in course if user accepts dialog.
     * 1. Dialog interface onClickListener;
     * 2. Override onClick from dialog interface listener, with acceptance and reject cases;
     * 3. Cancel routine from database and if request is succeeded, reset MainActivity with other layout;
     * 4. If cancel routine database request isn't succeeded, emit an error;
     * 5. Dialog call with step 1 listener.
     * @param view
     * @see DialogInterface
     * @see ServicesRoutine
     */
    public void cancelRoutine(View view) {

        // Step 1
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            // Step 2
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        // Step 3
                        if(ServicesRoutine.cancelRoutine(routineInCourseId,
                                MainActivity.this)) {
                            resetActivityMain();
                        }
                        // Step 4
                        else Toast.makeText(MainActivity.this,
                                R.string.txt_error_ormlite, Toast.LENGTH_SHORT)
                                .show();

                        break;

                    // Step 5
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        // Step 6
        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_cancel_routine_modal), listener);

    }

    /**
     * Finish current routine in course if user accepts dialog.
     * 1. Dialog interface onClickListener;
     * 2. Override onClick from dialog interface listener, with acceptance and reject cases;
     * 3. If routine isn't setted, get from database by ID;
     * 4. If routine isn't returned from database request, emit an error;
     * 5. Set endDateTime routineInCourse from current emitted Date;
     * 6. Update routine in database with endDateTime;
     * 7. If routine update database request doesn't succeed, emit an error;
     * 8. Nothing is done in reject case;
     * 9. Dialog call with step 1 listener.
     * @param view
     * @see DialogInterface
     * @see ServicesRoutine
     */
    public void finishRoutine(View view) {

        // Step 1
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            // Step 2
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        // Step 3
                        if(routine == null) {
                            routine = ServicesRoutine.getRoutineFromId(routineInCourseId,
                                    MainActivity.this);

                            // Step 4
                            if(routine == null) {
                                Toast.makeText(MainActivity.this,
                                        R.string.txt_error_ormlite, Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            }
                        }

                        // Step 5
                        routine.setEndDateTime(Calendar.getInstance().getTime());

                        // Step 6
                        if(ServicesRoutine.updateRoutine(routine,
                                MainActivity.this)) {
                            resetActivityMain();
                        }

                        // Step 7
                        else Toast.makeText(MainActivity.this,
                                R.string.txt_error_ormlite, Toast.LENGTH_SHORT).show();

                        break;

                    // Step 8
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        // Step 9
        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_finish_routine_modal), listener);

    }

    /**
     * Reset MainActivity after routineInCourse finished.
     * 1. Reset routineInCourseId and shared preferences;
     * 2. Reset menu and layout;
     * 3. Set up and Load updated routine list;
     * 4. Reset click listener and choice modes;
     * 5. Notifies attached adapterRoutineList observer that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    private void resetActivityMain() {

        // Step 1
        routineInCourseId = 0;
        SharedPreferences sharedPref = getSharedPreferences(getString(
                R.string.preferences_routine_in_course), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.register_routine_txt_in_course), routineInCourseId);
        editor.commit();

        // Step 2
        invalidateOptionsMenu();
        setContentView(R.layout.activity_main);

        // Step 3
        listViewRoutines = (ListView) findViewById(R.id.listViewMainRoutines);
        fillRoutineList();

        // Step 4
        listViewRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeRoutine(position);
            }
        });
        listViewRoutines.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewRoutines.setMultiChoiceModeListener(onMultiChoiceModeListener());

        // Step 5
        adapterRoutineList.notifyDataSetChanged();

    }

}
