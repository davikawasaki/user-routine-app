package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesPlace;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesPlaceType;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsString;

public class RegisterPlaceActivity extends AppCompatActivity {

    // Static request variables
    public static final String MODE   = "MODO";
    public static final String ID     = "ID";
    public static final int    NEW    = 1;
    public static final int    CHANGE = 2;

    // Layout components
    private TextView textViewPlaceTitle;
    private EditText editPlaceName;
    private Spinner  spinnerPlaceType;
    private EditText editPlaceAddress;
    private EditText editPlaceCity;
    private EditText editPlaceState;
    private EditText editPlaceCountry;
    private Button   buttonSavePlace;

    // Place instance and place type list
    private Place place;
    private List<PlaceType> placeTypeList;

    // Intent mode flag
    private int mode;

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

        textViewPlaceTitle = (TextView) findViewById(R.id.textRegisterPlaceTitle);
        editPlaceName      = (EditText) findViewById(R.id.textRegisterPlaceNameInput);
        spinnerPlaceType   = (Spinner)  findViewById(R.id.spinnerRegisterPlaceType);
        editPlaceAddress   = (EditText) findViewById(R.id.textRegisterPlaceAddressInput);
        editPlaceCity      = (EditText) findViewById(R.id.textRegisterPlaceCityInput);
        editPlaceState     = (EditText) findViewById(R.id.textRegisterPlaceStateInput);
        editPlaceCountry   = (EditText) findViewById(R.id.textRegisterPlaceCountryInput);
        buttonSavePlace    = (Button)   findViewById(R.id.buttonRegisterPlaceSave);

        fillPlaceTypesSpinner();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mode = bundle.getInt(MODE);

        if (mode == CHANGE) {

            place = ServicesPlace.getPlaceFromId(bundle.getInt(ID), this);

            if(place == null) {
                finish();
                return;
            }

            // Set other editText fields
            editPlaceName.setText(place.getName());
            editPlaceAddress.setText(place.getAddress());
            editPlaceCity.setText(place.getCity());
            editPlaceState.setText(place.getState());
            editPlaceCountry.setText(place.getCountry());

            // Select properly spinner option from place type name
            ArrayAdapter<PlaceType> arraySpinnerPlaceType = (ArrayAdapter<PlaceType>)
                    spinnerPlaceType.getAdapter();
            int typePos = ServicesPlaceType.getPositionFromArrayAdapter(
                    arraySpinnerPlaceType, place.getPlaceType().toString());
            spinnerPlaceType.setSelection(typePos);

            actionBar.setTitle(R.string.update_place_txt_bar_title);
            textViewPlaceTitle.setText(R.string.update_place_txt_title);
            buttonSavePlace.setText(R.string.update_place_txt_save_button);

        } else place = new Place();
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

        placeTypeList = ServicesPlaceType.getPlaceTypeList(this);

        if(placeTypeList == null) {
            finish();
            return;
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

        if (mode == CHANGE) {
            place.setName(name);
            place.setType(placeType);
            place.setAddress(address);
            place.setCity(city);
            place.setState(state);
            place.setCountry(country);

            if(ServicesPlace.updatePlace(place, this)) {
                Intent intent = new Intent();
                intent.putExtra(ID, place.getId());

                setResult(Activity.RESULT_OK, intent);
                finish();
            } else UtilsGUI.modalError(this, R.string.update_place_txt_name_used);
        } else {
            if(ServicesPlace.registerPlace(name, placeType, address, city, state, country, this)) {
                setResult(Activity.RESULT_OK);
                finish();
            } else UtilsGUI.modalError(this, R.string.update_place_txt_name_used);
        }
    }
}
