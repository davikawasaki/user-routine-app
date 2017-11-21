package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;

public class PlaceListActivity extends AppCompatActivity {

    // Static request variables
    private static final int REQUEST_NEW_PLACE    = 1;
    private static final int REQUEST_CHANGE_PLACE = 2;

    // Layout Components
    private ListView listViewPlaces;
    // Place Array Adapter List
    private ArrayAdapter<Place> adapterPlaceList;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_NEW_PLACE || requestCode == REQUEST_CHANGE_PLACE)
                && resultCode == Activity.RESULT_OK){

            fillPlaceList();
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

    private void fillPlaceList() {
        List<Place> list = null;

        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            list = connection.getPlaceDAO()
                    .queryBuilder()
                    .orderBy(Place.NAME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        adapterPlaceList = new ArrayAdapter<Place>(this,
                android.R.layout.simple_list_item_1,
                list);

        listViewPlaces.setAdapter(adapterPlaceList);
    }
}
