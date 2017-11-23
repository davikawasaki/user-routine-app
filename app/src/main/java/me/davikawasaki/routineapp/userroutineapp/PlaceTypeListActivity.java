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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesPlaceType;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

public class PlaceTypeListActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_type_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.list_place_types_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        listViewPlaceTypes = (ListView) findViewById(R.id.listViewMainPlaceTypesList);

        listViewPlaceTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changePlaceType(position);
            }
        });

        listViewPlaceTypes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewPlaceTypes.setMultiChoiceModeListener(onMultiChoiceModeListener());

        fillPlaceTypeList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_type_list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.itemMenuPlaceTypeListActivityNew:
                addNewPlaceType();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AbsListView.MultiChoiceModeListener onMultiChoiceModeListener() {
        return new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {

                boolean selected = listViewPlaceTypes.isItemChecked(position);

                View view = listViewPlaceTypes.getChildAt(position);

                if (selected) view.setBackgroundColor(Color.LTGRAY);
                else view.setBackgroundColor(Color.TRANSPARENT);

                int totalSelected = listViewPlaceTypes.getCheckedItemCount();

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
                if (listViewPlaceTypes.getCheckedItemCount() > 1) menu.getItem(0).setVisible(false);
                else menu.getItem(0).setVisible(true);
                return true;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {

                switch(item.getItemId()){
                    case R.id.itemMenuMainSelectedActivityEdit:
                        for (int position = listViewPlaceTypes.getChildCount(); position >= 0; position--){
                            if (listViewPlaceTypes.isItemChecked(position)){
                                changePlaceType(position);
                            }
                        }
                        mode.finish();
                        return true;

                    case R.id.itemMenuMainSelectedActivityRemove:
                        removePlaceTypes(mode);
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                for (int position = 0; position < listViewPlaceTypes.getChildCount(); position++){
                    View view = listViewPlaceTypes.getChildAt(position);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_TYPE && resultCode == Activity.RESULT_OK) {
            fillPlaceTypeList();
        } else if (requestCode == REQUEST_CHANGE_TYPE && resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            int placeTypeId = bundle.getInt(RegisterPlaceTypeActivity.ID);

            placeTypeList.remove(selectedPosition);
            placeTypeList.add(selectedPosition, ServicesPlaceType.getPlaceTypeFromId(
                    placeTypeId, this));
            selectedPosition = -1;

            adapterPlaceTypeList.notifyDataSetChanged();
        }
    }

    public void addNewPlaceType() {
        Intent intent = new Intent(this, RegisterPlaceTypeActivity.class);
        intent.putExtra(RegisterPlaceTypeActivity.MODE, RegisterPlaceTypeActivity.NEW);
        startActivityForResult(intent, REQUEST_NEW_TYPE);
    }

    private void changePlaceType(int position){

        selectedPosition = position;

        PlaceType placeType = placeTypeList.get(selectedPosition);

        Intent intent = new Intent(this, RegisterPlaceTypeActivity.class);

        intent.putExtra(RegisterPlaceTypeActivity.MODE, RegisterPlaceTypeActivity.CHANGE);
        intent.putExtra(RegisterPlaceTypeActivity.ID, placeType.getId());

        startActivityForResult(intent, REQUEST_CHANGE_TYPE);
    }

    private void removePlaceTypes(final ActionMode mode) {

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                List<PlaceType> removePlaceTypeList = new ArrayList<>();
                                for (int position = listViewPlaceTypes.getChildCount();
                                     position >= 0; position--){
                                    System.out.println(listViewPlaceTypes.isItemChecked(position));
                                    if (listViewPlaceTypes.isItemChecked(position)){
                                        System.out.println(placeTypeList.get(position));
                                        removePlaceTypeList.add(placeTypeList.get(position));
                                        System.out.println(removePlaceTypeList);
                                    }
                                }

                                System.out.println(removePlaceTypeList);

                                // Database request to remove.
                                // If any of places are in use in registered routines
                                // an error is emitted to uncheck used routines
                                if(ServicesPlaceType.removePlaceTypes(removePlaceTypeList,
                                        PlaceTypeListActivity.this)) {
                                    // Remove items from routineList,
                                    // setting a new adapter according to new instance of Array List
                                    PlaceTypeListActivity.this.placeTypeList = new ArrayList<>(ServicesPlaceType.
                                            iterateAndRemoveFromList(PlaceTypeListActivity.this.placeTypeList,removePlaceTypeList));
                                    PlaceTypeListActivity.this.adapterPlaceTypeList = new ArrayAdapter<PlaceType>(
                                            PlaceTypeListActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            PlaceTypeListActivity.this.placeTypeList);
                                    PlaceTypeListActivity.this.listViewPlaceTypes.setAdapter(PlaceTypeListActivity.this.adapterPlaceTypeList);

                                    PlaceTypeListActivity.this.adapterPlaceTypeList.notifyDataSetChanged();
                                } else UtilsGUI.modalError(PlaceTypeListActivity.this,
                                        R.string.update_place_type_txt_names_used);

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }

                        mode.finish();
                    }
                };

        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_delete_place_type_modal), listener);

    }

    private void fillPlaceTypeList() {
        placeTypeList = null;

        placeTypeList = ServicesPlaceType.getPlaceTypeList(this);

        if(placeTypeList == null) placeTypeList = new ArrayList<PlaceType>();

        adapterPlaceTypeList = new ArrayAdapter<PlaceType>(this,
                                    android.R.layout.simple_list_item_1,
                                    placeTypeList);

        listViewPlaceTypes.setAdapter(adapterPlaceTypeList);
    }
}
