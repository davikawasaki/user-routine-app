package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsString;

public class RegisterPlaceActivity extends AppCompatActivity {

    // Static request variables
    public static final String MODE   = "MODO";
    public static final String ID     = "ID";
    public static final int    NEW    = 1;
    public static final int    CHANGE = 2;

    // Layout components
    private EditText editPlaceName;
    private Spinner spinnerPlaceType;
    private EditText editPlaceAddress;
    private EditText editPlaceCity;
    private EditText editPlaceState;
    private EditText editPlaceCountry;

    // Place instance and place type list
    private Place place;
    private List<PlaceType> placeTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_place);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.register_place_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        editPlaceName    = (EditText) findViewById(R.id.textRegisterPlaceNameInput);
        spinnerPlaceType = (Spinner) findViewById(R.id.spinnerRegisterPlaceType);
        editPlaceAddress = (EditText) findViewById(R.id.textRegisterPlaceAddressInput);
        editPlaceCity    = (EditText) findViewById(R.id.textRegisterPlaceCityInput);
        editPlaceState   = (EditText) findViewById(R.id.textRegisterPlaceStateInput);
        editPlaceCountry = (EditText) findViewById(R.id.textRegisterPlaceCountryInput);

        place = new Place();

        fillPlaceTypesSpinner();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home) finish();
        return true;
    }

    /**
     * Button call to clear fields
     * @param view
     */
    public void clearFields(View view) {
        editPlaceName.setText(null);
        editPlaceAddress.setText(null);
        editPlaceCity.setText(null);
        editPlaceCountry.setText(null);

        Toast.makeText(this, R.string.txt_clear_fields_toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Fill spinner with place type options.
     */
    private void fillPlaceTypesSpinner() {

        placeTypeList = null;

        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            placeTypeList = connection.getPlaceTypeDAO()
                    .queryBuilder()
                    .orderBy(PlaceType.NAME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayAdapter<PlaceType> spinnerPlaceTypeAdapter = new ArrayAdapter<PlaceType>(this,
                android.R.layout.simple_list_item_1,
                placeTypeList);

        spinnerPlaceType.setAdapter(spinnerPlaceTypeAdapter);
    }

    /**
     * Save place
     * @param view
     */
    public void save(View view) {
        String name = UtilsGUI.checkTextField(this,
                editPlaceName,
                R.string.register_place_txt_name_empty);
        PlaceType placeType = (PlaceType) spinnerPlaceType.getSelectedItem();
        String address = editPlaceAddress.getText().toString();
        String city = editPlaceCity.getText().toString();
        String state = editPlaceState.getText().toString();
        String country = editPlaceCountry.getText().toString();

        if(name == null) return;

        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            List<Place> list = connection.getPlaceDAO()
                    .queryBuilder()
                    .where().eq(Place.NAME, name)
                    .query();

            if (list.size() > 0){
                UtilsGUI.modalError(this, R.string.register_place_txt_name_used);
                return;
            }

            place.setName(name);
            if(placeType != null) place.setType(placeType);
            if(!UtilsString.stringEmpty(address)) place.setAddress(address);
            if(!UtilsString.stringEmpty(city)) place.setCity(city);
            if(!UtilsString.stringEmpty(state)) place.setState(state);
            if(!UtilsString.stringEmpty(country)) place.setCountry(country);
            connection.getPlaceDAO().create(place);

            setResult(Activity.RESULT_OK);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
