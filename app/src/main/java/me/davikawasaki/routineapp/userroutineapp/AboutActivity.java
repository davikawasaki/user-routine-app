package me.davikawasaki.routineapp.userroutineapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * About Application Activity.
 * Basic page with only information and return button.
 * @see android.support.v7.app.AppCompatActivity
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * On AboutActivity instantiation implements sequence of scripts.
     * 1. Set content view with layout;
     * 2. Display back button as drawable chevron left in actionBar.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_back_white);
        }
    }

    /**
     * Listener to menu options item selected.
     * 1. Android Home Button: finish activity;
     * 2. Default: call super method to check for updates;
     * @param item
     * @return selectedStatus
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
