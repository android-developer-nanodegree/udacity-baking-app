package com.sielski.marcin.bakingapp.activity;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

public class BakingAppSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baking_app_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_baking_app_settings);
        }
        if (getIntent().getBooleanExtra(BakingAppUtils.KEY.SHOW_WARNING, false)) {
            BakingAppUtils.showSnackBar(this,
                    findViewById(R.id.fragment_baking_app_settings),
                    getString(R.string.snackbar_cse_api_key));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
