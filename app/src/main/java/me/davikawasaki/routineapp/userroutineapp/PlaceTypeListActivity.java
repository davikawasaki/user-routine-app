package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesPlaceType;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

public class PlaceTypeListActivity extends AppCompatActivity {

    /*********************************************************************/
    /***************************** VARIABLES *****************************/
    /*********************************************************************/

    // Static request variables
    private static final int REQUEST_NEW_TYPE    = 1;
    private static final int REQUEST_CHANGE_TYPE = 2;

    // Layout Components
    private ListView                listViewPlaceTypes;

    // Place Type Array Adapter List
    private ArrayAdapter<PlaceType> adapterPlaceTypeList;
    private List<PlaceType>        placeTypeList;

    // Routine position on listView
    private int selectedPosition = -1;

    /*********************************************************************/
    /************************* ACTIVITY LISTENERS ************************/
    /*********************************************************************/

    /**
     * On PlaceTypeListActivity instantiation implements sequence of scripts.
     * 1. Set content view placeTypeList layout;
     * 2. Get actionBar and set its title and a back button with ic_back_white drawable;
     * 3. Get listViewPlaceTypes and insert a itemClickListener for each option;
     * 4. Set multiple choice action bar for listViewPlaces selected item(s);
     * 5. Fill PlaceType List.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Step 1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_type_list);

        // Step 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.list_place_types_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        // Step 3
        listViewPlaceTypes = (ListView) findViewById(R.id.listViewMainPlaceTypesList);
        listViewPlaceTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changePlaceType(position);
            }
        });

        // Step 4
        listViewPlaceTypes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewPlaceTypes.setMultiChoiceModeListener(onMultiChoiceModeListener());

        // Step 5
        fillPlaceTypeList();
    }

    /**
     * Listener for other activity result incomes.
     * 1. If result is from a success new place type, update place type list;
     * 2. If result is from a success changed place type, recover updated ID and change it in listView.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Step 1
        if (requestCode == REQUEST_NEW_TYPE && resultCode == Activity.RESULT_OK) {
            fillPlaceTypeList();
        }
        // Step 2
        else if (requestCode == REQUEST_CHANGE_TYPE && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            int placeTypeId = bundle.getInt(RegisterPlaceTypeActivity.ID);

            placeTypeList.remove(selectedPosition);
            placeTypeList.add(selectedPosition, ServicesPlaceType.getPlaceTypeFromId(
                    placeTypeId, this));
            selectedPosition = -1;

            adapterPlaceTypeList.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.place_type_list_activity_menu, menu);
        return true;
    }

    /**
     * Listener to menu options item selected.
     * 1. Android Home Button: finish activity;
     * 2. New Place Type: redirect to new place type activity;
     * 3. Default: call super method to check for updates;
     * @param item
     * @return selectedStatus
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Step 1
            case android.R.id.home:
                finish();
                return true;
            // Step 2
            case R.id.itemMenuPlaceTypeListActivityNew:
                addNewPlaceType();
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
             * Event listener for listViewPlaceTypes item checked.
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
                boolean selected = listViewPlaceTypes.isItemChecked(position);
                View view = listViewPlaceTypes.getChildAt(position);

                // Step 2
                if (selected) view.setBackgroundColor(Color.LTGRAY);
                else view.setBackgroundColor(Color.TRANSPARENT);

                // Step 3
                int totalSelected = listViewPlaceTypes.getCheckedItemCount();
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
                if (listViewPlaceTypes.getCheckedItemCount() > 1) menu.getItem(0).setVisible(false);
                else menu.getItem(0).setVisible(true);
                return true;
            }

            /**
             * Event listener for multiChoiceActionMode clicked item.
             * 1. Edit Option: Check if place type list item is checked and redirect to
             *    change place type activity - then finish multiChoice mode;
             * 2. Remove Option: Check if place type list items are checked and asks
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
                        for (int position = listViewPlaceTypes.getChildCount(); position >= 0; position--){
                            if (listViewPlaceTypes.isItemChecked(position)) {
                                changePlaceType(position);
                            }
                        }
                        mode.finish();
                        return true;

                    // Step 2
                    case R.id.itemMenuMainSelectedActivityRemove:
                        removePlaceTypes(mode);
                        return true;

                    default:
                        return false;
                }
            }

            /**
             * Event listener for multiChoiceActionMode destruction.
             * Uncheck each Place Type ListView items, changing their colors to transparent.
             * @param mode
             */
            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                for (int position = 0; position < listViewPlaceTypes.getChildCount(); position++) {
                    View view = listViewPlaceTypes.getChildAt(position);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
    }

    /*********************************************************************/
    /********************* ACTIVITY REDIRECT METHODS *********************/
    /*********************************************************************/

    /**
     * Generate activity intent to new place type page.
     * Bundle: MODE as NEW
     * RequestCode: REQUEST_NEW_PLACE_TYPE
     */
    public void addNewPlaceType() {
        Intent intent = new Intent(this, RegisterPlaceTypeActivity.class);
        intent.putExtra(RegisterPlaceTypeActivity.MODE, RegisterPlaceTypeActivity.NEW);
        startActivityForResult(intent, REQUEST_NEW_TYPE);
    }

    /**
     * Generate activity intent to change place type page.
     * Get place type from selectedPosition and put its ID in Bundle.
     * Bundle: MODE as CHANGE / ID as placeID
     * RequestCode: REQUEST_CHANGE_PLACE_TYPE
     * @param position
     */
    private void changePlaceType(int position){

        selectedPosition = position;
        PlaceType placeType = placeTypeList.get(selectedPosition);

        Intent intent = new Intent(this, RegisterPlaceTypeActivity.class);
        intent.putExtra(RegisterPlaceTypeActivity.MODE, RegisterPlaceTypeActivity.CHANGE);
        intent.putExtra(RegisterPlaceTypeActivity.ID, placeType.getId());

        startActivityForResult(intent, REQUEST_CHANGE_TYPE);

    }

    /*********************************************************************/
    /**************** PLACE TYPE LIST MANIPULATION METHODS ***************/
    /*********************************************************************/

    /**
     * Remove selected place types if user accepts dialog.
     * 1. Dialog interface onClickListener;
     * 2. Override onClick from dialog interface listener, with acceptance and reject cases;
     * 3. Iterate listViewPlaceTypes to get selected items to be removed and add to a PlaceTypeList;
     * 4. Run database request to remove the placeTypeList from last step;
     * 5. Iterate removedPlaceTypeList from fullPlaceTypeList to remove each item;
     * 6. Insert updated placeTypeList into adapterPlaceTypeList to update listView from removal;
     * 7. If any place type is in use in registered places an error is emitted to uncheck used places;
     * 8. Nothing is done in reject case;
     * 9. Finally finish multiChoiceMode;
     * 10. Dialog call with step 1 listener.
     * @param mode
     * @see DialogInterface
     * @see ServicesPlaceType
     */
    private void removePlaceTypes(final ActionMode mode) {

        // Step 1
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            // Step 2
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        // Step 3
                        List<PlaceType> removePlaceTypeList = new ArrayList<>();
                        for (int position = listViewPlaceTypes.getChildCount();
                             position >= 0; position--) {
                            if (listViewPlaceTypes.isItemChecked(position)) {
                                removePlaceTypeList.add(placeTypeList.get(position));
                            }
                        }

                        // Step 4
                        if(ServicesPlaceType.removePlaceTypes(removePlaceTypeList,
                                PlaceTypeListActivity.this)) {
                            // Step 5
                            placeTypeList = new ArrayList<>(ServicesPlaceType.
                                    iterateAndRemoveFromList(placeTypeList,removePlaceTypeList));

                            // Step 6
                            adapterPlaceTypeList = new ArrayAdapter<PlaceType>(
                                    PlaceTypeListActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            placeTypeList);
                                    listViewPlaceTypes.setAdapter(adapterPlaceTypeList);
                            adapterPlaceTypeList.notifyDataSetChanged();
                        }
                        // Step 7
                        else UtilsGUI.modalError(PlaceTypeListActivity.this,
                                R.string.update_place_type_txt_names_used);

                        break;

                    // Step 8
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }

                // Step 9
                mode.finish();
            }
        };

        // Step 10
        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_delete_place_type_modal), listener);

    }

    /**
     * Get placeTypeList from database and fill listViewPlaceTypes with records.
     * 1. Get place type list records from database;
     * 2. If an error is emitted from database, start empty ArrayList and emit a Toast error;
     * 3. Insert place type list into adapterPlaceTypeList to update listView;
     * @see ServicesPlaceType
     */
    private void fillPlaceTypeList() {

        // Step 1
        placeTypeList = null;
        placeTypeList = ServicesPlaceType.getPlaceTypeList(this);

        // Step 2
        if(placeTypeList == null) {
            placeTypeList = new ArrayList<PlaceType>();
            Toast.makeText(this,
                    R.string.txt_error_ormlite, Toast.LENGTH_SHORT).show();
        }

        // Step 3
        adapterPlaceTypeList = new ArrayAdapter<PlaceType>(this,
                                    android.R.layout.simple_list_item_1,
                                    placeTypeList);
        listViewPlaceTypes.setAdapter(adapterPlaceTypeList);

    }

}
