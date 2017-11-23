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
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesPlace;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;

public class PlaceListActivity extends AppCompatActivity {

    // Static request variables
    private static final int REQUEST_NEW_PLACE    = 1;
    private static final int REQUEST_CHANGE_PLACE = 2;

    // Layout Components
    private ListView listViewPlaces;
    // Place Array Adapter List
    private ArrayAdapter<Place> adapterPlaceList;
    private List<Place> placeList;

    // Routine position on listView
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.list_place_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        listViewPlaces = (ListView) findViewById(R.id.listViewMainPlacesList);

        listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changePlace(position);
            }
        });

        listViewPlaces.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewPlaces.setMultiChoiceModeListener(onMultiChoiceModeListener());

        fillPlaceList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.itemMenuPlaceListActivityNew:
                addNewPlace();
                return true;
            case R.id.itemMenuPlaceListActivityPlaceTypeList:
                seePlaceTypeList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AbsListView.MultiChoiceModeListener onMultiChoiceModeListener() {
        return new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {

                boolean selected = listViewPlaces.isItemChecked(position);

                View view = listViewPlaces.getChildAt(position);

                if (selected) view.setBackgroundColor(Color.LTGRAY);
                else view.setBackgroundColor(Color.TRANSPARENT);

                int totalSelected = listViewPlaces.getCheckedItemCount();

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
                if (listViewPlaces.getCheckedItemCount() > 1) menu.getItem(0).setVisible(false);
                else menu.getItem(0).setVisible(true);
                return true;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {

                switch(item.getItemId()){
                    case R.id.itemMenuMainSelectedActivityEdit:
                        for (int position = listViewPlaces.getChildCount(); position >= 0; position--){
                            if (listViewPlaces.isItemChecked(position)){
                                changePlace(position);
                            }
                        }
                        mode.finish();
                        return true;

                    case R.id.itemMenuMainSelectedActivityRemove:
                        removePlaces(mode);
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                for (int position = 0; position < listViewPlaces.getChildCount(); position++){
                    View view = listViewPlaces.getChildAt(position);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_PLACE && resultCode == Activity.RESULT_OK){
            fillPlaceList();
        } else if (requestCode == REQUEST_CHANGE_PLACE && resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            int placeId = bundle.getInt(RegisterPlaceActivity.ID);

            placeList.remove(selectedPosition);
            placeList.add(selectedPosition, ServicesPlace.getPlaceFromId(placeId, this));
            selectedPosition = -1;

            adapterPlaceList.notifyDataSetChanged();
        }
    }

    public void addNewPlace() {
        Intent intent = new Intent(this, RegisterPlaceActivity.class);
        intent.putExtra(RegisterPlaceActivity.MODE, RegisterPlaceActivity.NEW);
        startActivityForResult(intent, REQUEST_NEW_PLACE);
    }

    public void seePlaceTypeList() {
        Intent intent = new Intent(this, PlaceTypeListActivity.class);
        startActivity(intent);
    }

    private void changePlace(int position){

        selectedPosition = position;

        Place place = placeList.get(selectedPosition);

        Intent intent = new Intent(this, RegisterPlaceActivity.class);

        intent.putExtra(RegisterPlaceActivity.MODE, RegisterPlaceActivity.CHANGE);
        intent.putExtra(RegisterPlaceActivity.ID, place.getId());

        startActivityForResult(intent, REQUEST_CHANGE_PLACE);
    }

    private void removePlaces(final ActionMode mode) {

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                List<Place> removePlaceList = new ArrayList<>();
                                for (int position = listViewPlaces.getChildCount();
                                     position >= 0; position--){
                                    if (listViewPlaces.isItemChecked(position)){
                                        removePlaceList.add(placeList.get(position));
                                    }
                                }
                                // Database request to remove.
                                // If any of places are in use in registered routines
                                // an error is emitted to uncheck used routines
                                if(ServicesPlace.removePlaces(removePlaceList,
                                        PlaceListActivity.this)) {
                                    // Remove items from routineList,
                                    // setting a new adapter according to new instance of Array List
                                    placeList = new ArrayList<>(ServicesPlace.
                                            iterateAndRemoveFromList(placeList,removePlaceList));
                                    adapterPlaceList = new ArrayAdapter<Place>(
                                            PlaceListActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            placeList);
                                    listViewPlaces.setAdapter(adapterPlaceList);

                                    adapterPlaceList.notifyDataSetChanged();
                                } else UtilsGUI.modalError(PlaceListActivity.this,
                                        R.string.update_place_txt_names_used);

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }

                        mode.finish();
                    }
                };

        UtilsGUI.modalConfirm(this, getString(
                R.string.txt_confirmation_msg_delete_place_modal), listener);

    }

    private void fillPlaceList() {
        placeList = null;

        placeList = ServicesPlace.getPlaceList(this);

        if(placeList == null) placeList = new ArrayList<Place>();

        adapterPlaceList = new ArrayAdapter<Place>(this,
                android.R.layout.simple_list_item_1,
                placeList);

        listViewPlaces.setAdapter(adapterPlaceList);
    }
}
