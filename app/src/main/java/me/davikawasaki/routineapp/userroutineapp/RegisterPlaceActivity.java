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

/**
 * Register/Update Place Activity.
 * Place activity with registration and update.
 * @see android.support.v7.app.AppCompatActivity
 */
public class RegisterPlaceActivity extends AppCompatActivity {

    /*********************************************************************/
    /***************************** VARIABLES *****************************/
    /*********************************************************************/

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

    /*********************************************************************/
    /************************* ACTIVITY LISTENERS ************************/
    /*********************************************************************/

    /**
     * On RegisterPlaceActivity instantiation implements sequence of scripts.
     * 1. Set content view with registerPlace layout;
     * 2. Get actionBar and set its title and a back button with ic_back_white drawable;
     * 3. Get textView, editText, spinner and buttons elements from id;
     * 4. Fill Place Type Spinner List;
     * 5. Get activity mode from incoming intent;
     * 6. Edit mode: get place by ID from database inside intent bundle. If place isn't returned,
     *               close activity;
     * 7. Edit mode: with place returned from database set editText fields;
     * 8. Edit mode: select properly spinner option from place type name position;
     * 9. Edit mode: Update action bar title, button text and main title;
     * 10. New mode: only instantiate a new place.
     * @param savedInstanceState
     * @see ServicesPlace
     * @see ServicesPlaceType
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Step 1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_place);

        // Step 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.register_place_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        // Step 3
        textViewPlaceTitle = (TextView) findViewById(R.id.textRegisterPlaceTitle);
        editPlaceName      = (EditText) findViewById(R.id.textRegisterPlaceNameInput);
        spinnerPlaceType   = (Spinner)  findViewById(R.id.spinnerRegisterPlaceType);
        editPlaceAddress   = (EditText) findViewById(R.id.textRegisterPlaceAddressInput);
        editPlaceCity      = (EditText) findViewById(R.id.textRegisterPlaceCityInput);
        editPlaceState     = (EditText) findViewById(R.id.textRegisterPlaceStateInput);
        editPlaceCountry   = (EditText) findViewById(R.id.textRegisterPlaceCountryInput);
        buttonSavePlace    = (Button)   findViewById(R.id.buttonRegisterPlaceSave);

        // Step 4
        fillPlaceTypesSpinner();

        // Step 5
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mode = bundle.getInt(MODE);

        if (mode == CHANGE) {

            // Step 6
            place = ServicesPlace.getPlaceFromId(bundle.getInt(ID), this);
            if(place == null) {
                finish();
                return;
            }

            // Step 7
            editPlaceName.setText(place.getName());
            editPlaceAddress.setText(place.getAddress());
            editPlaceCity.setText(place.getCity());
            editPlaceState.setText(place.getState());
            editPlaceCountry.setText(place.getCountry());

            // Step 8
            ArrayAdapter<PlaceType> arraySpinnerPlaceType = (ArrayAdapter<PlaceType>)
                    spinnerPlaceType.getAdapter();
            int typePos = ServicesPlaceType.getPositionFromArrayAdapter(
                    arraySpinnerPlaceType, place.getPlaceType().toString());
            spinnerPlaceType.setSelection(typePos);

            // Step 9
            actionBar.setTitle(R.string.update_place_txt_bar_title);
            textViewPlaceTitle.setText(R.string.update_place_txt_title);
            buttonSavePlace.setText(R.string.update_place_txt_save_button);

        }
        // Step 10
        else place = new Place();
    }

    /*********************************************************************/
    /********************* MENU/ACTION BAR LISTENERS *********************/
    /*********************************************************************/

    /**
     * Listener to menu options item selected.
     * Android Home Button: finish activity.
     * @param item
     * @return selectedStatus
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) finish();
        return true;
    }

    /*********************************************************************/
    /************************ BUTTON CLICK METHODS ***********************/
    /*********************************************************************/

    /**
     * Button call to clear fields and emit a Toast.
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
     * Save/update place.
     * 1. Check name emptiness, returning a modal error and canceling method;
     * 2. Get other fields (String and PlaceType);
     * 3. Edit mode: set place object with collected updated fields;
     * 4. Edit mode: if updated place is saved into database, insert ID into intent bundle
     *               and finish activity with success;
     * 5. Edit mode: if updated place isn't saved into database, emit a modal error;
     * 6. New mode: if new place isn't repeated, finish activity with success;
     * 7. New mode: if new place is repeated, emit a modal error.
     * @param view
     * @see ServicesPlace
     */
    public void save(View view) {
        // Step 1
        String name = UtilsGUI.checkTextField(this,
                editPlaceName,
                R.string.register_place_txt_name_empty);
        if(name == null) return;

        // Step 2
        PlaceType placeType = (PlaceType) spinnerPlaceType.getSelectedItem();
        String address = editPlaceAddress.getText().toString();
        String city = editPlaceCity.getText().toString();
        String state = editPlaceState.getText().toString();
        String country = editPlaceCountry.getText().toString();

        if (mode == CHANGE) {
            // Step 3
            place.setName(name);
            place.setType(placeType);
            place.setAddress(address);
            place.setCity(city);
            place.setState(state);
            place.setCountry(country);

            // Step 4
            if(ServicesPlace.updatePlace(place, this)) {
                Intent intent = new Intent();
                intent.putExtra(ID, place.getId());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            // Step 5
            else UtilsGUI.modalError(this, R.string.update_place_txt_name_used);
        } else {
            // Step 6
            if(ServicesPlace.registerPlace(name, placeType, address, city, state, country, this)) {
                setResult(Activity.RESULT_OK);
                finish();
            }
            // Step 7
            else UtilsGUI.modalError(this, R.string.register_place_txt_name_used);
        }
    }

    /*********************************************************************/
    /************** PLACE TYPE SPINNER MANIPULATION METHODS **************/
    /*********************************************************************/

    /**
     * Fill spinner with place type options collected from database records.
     * 1. Get place type list records from database;
     * 2. If an error is emitted from database, finish activity;
     * 3. Insert place type list into spinnerPlaceTypeAdapter to update spinnerPlaceType;
     * @see ServicesPlaceType
     */
    private void fillPlaceTypesSpinner() {

        // Step 1
        placeTypeList = null;
        placeTypeList = ServicesPlaceType.getPlaceTypeList(this);

        // Step 2
        if(placeTypeList == null) {
            finish();
            return;
        }

        // Step 3
        ArrayAdapter<PlaceType> spinnerPlaceTypeAdapter = new ArrayAdapter<PlaceType>(this,
                android.R.layout.simple_list_item_1,
                placeTypeList);
        spinnerPlaceType.setAdapter(spinnerPlaceTypeAdapter);

    }

}
