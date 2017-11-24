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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.services.ServicesPlaceType;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsString;

/**
 * Register/Update Place Type Activity.
 * Place type activity with registration and update.
 * @see android.support.v7.app.AppCompatActivity
 */
public class RegisterPlaceTypeActivity extends AppCompatActivity {

    /*********************************************************************/
    /***************************** VARIABLES *****************************/
    /*********************************************************************/

    // Static request variables
    public static final String MODE   = "MODO";
    public static final String ID     = "ID";
    public static final int    NEW    = 1;
    public static final int    CHANGE = 2;

    // Layout components
    private TextView textViewPlaceTypeTitle;
    private EditText editPlaceTypeName;
    private Button   buttonSavePlaceType;

    // Place type instance
    private PlaceType placeType;

    // Intent mode flag
    private int mode;

    /*********************************************************************/
    /************************* ACTIVITY LISTENERS ************************/
    /*********************************************************************/

    /**
     * On RegisterPlaceTypeActivity instantiation implements sequence of scripts.
     * 1. Set content view with registerPlaceType layout;
     * 2. Get actionBar and set its title and a back button with ic_back_white drawable;
     * 3. Get textView, editText and buttons elements from id;
     * 4. Get activity mode from incoming intent;
     * 5. Edit mode: get place type by ID from database inside intent bundle. If place type
     *               isn't returned, close activity;
     * 6. Edit mode: with place returned from database set editText fields;
     * 7. Edit mode: Update action bar title, button text and main title;
     * 8. New mode: only instantiate a new place type.
     * @param savedInstanceState
     * @see ServicesPlaceType
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Step 1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_place_type);

        // Step 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.register_place_type_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        // Step 3
        textViewPlaceTypeTitle = (TextView) findViewById(R.id.textRegisterPlaceTypeTitle);
        editPlaceTypeName      = (EditText) findViewById(R.id.textRegisterPlaceTypeNameInput);
        buttonSavePlaceType    = (Button)   findViewById(R.id.buttonRegisterPlaceTypeSave);

        // Step 4
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mode = bundle.getInt(MODE);

        if (mode == CHANGE) {
            // Step 5
            placeType = ServicesPlaceType.getPlaceTypeFromId(bundle.getInt(ID), this);
            if(placeType == null) {
                finish();
                return;
            }

            // Step 6
            editPlaceTypeName.setText(placeType.getName());

            // Step 7
            actionBar.setTitle(R.string.update_place_type_txt_bar_title);
            textViewPlaceTypeTitle.setText(R.string.update_place_type_txt_title);
            buttonSavePlaceType.setText(R.string.update_place_type_txt_save_button);

        }
        // Step 8
        else placeType = new PlaceType();
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
        editPlaceTypeName.setText(null);
        Toast.makeText(this, R.string.txt_clear_field_toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Save/update place type.
     * 1. Check name emptiness, returning a modal error and canceling method;
     * 2. Edit mode: set place type object with collected updated fields;
     * 3. Edit mode: if updated place type is saved into database, insert ID into intent bundle
     *               and finish activity with success;
     * 4. Edit mode: if updated place type isn't saved into database, emit a modal error;
     * 5. New mode: if new place type isn't repeated, finish activity with success;
     * 6. New mode: if new place type is repeated, emit a modal error.
     * @param view
     * @see ServicesPlaceType
     */
    public void save(View view) {
        // Step 1
        String name = UtilsGUI.checkTextField(this,
                                              editPlaceTypeName,
                                              R.string.register_place_type_txt_name_empty);
        if(name == null) return;

        if (mode == CHANGE) {
            // Step 2
            placeType.setName(name);

            // Step 3
            if(ServicesPlaceType.updatePlaceType(placeType, this)) {
                Intent intent = new Intent();
                intent.putExtra(ID, placeType.getId());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            // Step 4
            else UtilsGUI.modalError(this, R.string.update_place_type_txt_name_used);
        } else {
            // Step 5
            if(ServicesPlaceType.registerPlaceType(name, this)) {
                setResult(Activity.RESULT_OK);
                finish();
            }
            // Step 6
            else UtilsGUI.modalError(this, R.string.register_place_type_txt_name_used);
        }
    }

}
