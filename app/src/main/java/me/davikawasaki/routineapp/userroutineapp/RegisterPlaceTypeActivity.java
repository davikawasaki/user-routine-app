package me.davikawasaki.routineapp.userroutineapp;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsGUI;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsString;

public class RegisterPlaceTypeActivity extends AppCompatActivity {

    // Static request variables
    public static final String MODE   = "MODO";
    public static final String ID     = "ID";
    public static final int    NEW    = 1;
    public static final int    CHANGE = 2;

    // Layout components
    private EditText editPlaceTypeName;
    // Place type instance
    private PlaceType placeType;

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

        editPlaceTypeName = (EditText) findViewById(R.id.textRegisterPlaceTypeNameInput);

        placeType = new PlaceType();
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

        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(this);

            List<PlaceType> list = connection.getPlaceTypeDAO()
                                    .queryBuilder()
                                    .where().eq(PlaceType.NAME, name)
                                    .query();

            if (list.size() > 0){
                UtilsGUI.modalError(this, R.string.register_place_type_txt_name_used);
                return;
            }

            placeType.setName(name);
            connection.getPlaceTypeDAO().create(placeType);

            setResult(Activity.RESULT_OK);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
