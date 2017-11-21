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
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;

public class PlaceTypeListActivity extends AppCompatActivity {

    // Static request variables
    private static final int REQUEST_NEW_TYPE    = 1;
    private static final int REQUEST_CHANGE_TYPE = 2;

    // Layout Components
    private ListView                listViewPlaceTypes;
    // Place Type Array Adapter List
    private ArrayAdapter<PlaceType> adapterPlaceTypeList;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_NEW_TYPE || requestCode == REQUEST_CHANGE_TYPE)
                && resultCode == Activity.RESULT_OK){

            fillPlaceTypeList();
        }
    }

    public void addNewPlaceType() {
        Intent intent = new Intent(this, RegisterPlaceTypeActivity.class);
        intent.putExtra(RegisterPlaceTypeActivity.MODE, RegisterPlaceTypeActivity.NEW);
        startActivityForResult(intent, REQUEST_NEW_TYPE);
    }

    private void fillPlaceTypeList() {
        List<PlaceType> list = null;

        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            list = connection.getPlaceTypeDAO()
                    .queryBuilder()
                    .orderBy(PlaceType.NAME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        adapterPlaceTypeList = new ArrayAdapter<PlaceType>(this,
                                    android.R.layout.simple_list_item_1,
                                    list);

        listViewPlaceTypes.setAdapter(adapterPlaceTypeList);
    }
}
