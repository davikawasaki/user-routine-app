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

public class RegisterPlaceTypeActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_place_type);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.register_place_type_txt_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }

        textViewPlaceTypeTitle = (TextView) findViewById(R.id.textRegisterPlaceTypeTitle);
        editPlaceTypeName      = (EditText) findViewById(R.id.textRegisterPlaceTypeNameInput);
        buttonSavePlaceType    = (Button)   findViewById(R.id.buttonRegisterPlaceTypeSave);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mode = bundle.getInt(MODE);

        if (mode == CHANGE) {

            placeType = ServicesPlaceType.getPlaceTypeFromId(bundle.getInt(ID), this);

            if(placeType == null) {
                finish();
                return;
            }

            editPlaceTypeName.setText(placeType.getName());

            actionBar.setTitle(R.string.update_place_type_txt_bar_title);
            textViewPlaceTypeTitle.setText(R.string.update_place_type_txt_title);
            buttonSavePlaceType.setText(R.string.update_place_type_txt_save_button);

        } else placeType = new PlaceType();

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
        editPlaceTypeName.setText(null);
        Toast.makeText(this, R.string.txt_clear_field_toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Save place type
     * @param view
     */
    public void save(View view) {
        String name = UtilsGUI.checkTextField(this,
                                              editPlaceTypeName,
                                              R.string.register_place_type_txt_name_empty);

        if(name == null) return;

        if (mode == CHANGE) {
            placeType.setName(name);

            if(ServicesPlaceType.updatePlaceType(placeType, this)) {
                Intent intent = new Intent();
                intent.putExtra(ID, placeType.getId());

                setResult(Activity.RESULT_OK, intent);
                finish();
            } else UtilsGUI.modalError(this, R.string.update_place_type_txt_name_used);
        } else {
            if(ServicesPlaceType.registerPlaceType(name, this)) {
                setResult(Activity.RESULT_OK);
                finish();
            } else UtilsGUI.modalError(this, R.string.update_place_type_txt_name_used);
        }
    }
}
