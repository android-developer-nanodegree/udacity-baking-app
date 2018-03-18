package com.sielski.marcin.bakingapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.adapter.RecipesAdapter;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesActivity extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipes)
    public RecyclerView mRecycleView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.progress_baking_app)
    public ProgressBar mProgressBar;

    private RequestQueue mRequestQueue;

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepies);
        ButterKnife.bind(this);
        if (BakingAppUtils.getCSEApiKey(this).length() !=
                getResources().getInteger(R.integer.length_cse_api_key)) {
            Intent intent = new Intent(this, BakingAppSettingsActivity.class);
            intent.putExtra(BakingAppUtils.KEY.SHOW_WARNING, true);
            startActivity(intent);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_recipes));
        }
        mRecycleView.setLayoutManager(new GridLayoutManager(this,
                BakingAppUtils.spanCount(this)));
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        Recipe recipe = getIntent().getParcelableExtra(BakingAppUtils.KEY.RECIPE);
        if (recipe != null) {
            getIntent().removeExtra(BakingAppUtils.KEY.RECIPE);
            Intent intent = new Intent(this, RecipeStepsDetailActivity.class);
            intent.putExtra(BakingAppUtils.KEY.RECIPE, recipe);
            startActivity(intent);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (BakingAppUtils.isNetworkAvailable(context)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mRequestQueue.add(new StringRequest(Request.Method.GET,
                            BakingAppUtils.BAKING_APP_URL,
                            (response) -> {
                                mRecycleView.setAdapter(new RecipesAdapter(RecipesActivity.this,
                                        new Gson().fromJson(response,
                                                new TypeToken<List<Recipe>>() {
                                                }.getType())));
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }, (error) ->
                            mProgressBar.setVisibility(View.INVISIBLE)
                    ));
                } else {
                    BakingAppUtils.showSnackBar(context, mRecycleView,
                            getString(R.string.snackbar_network_unavailable));
                }
            }
    };

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver,
                new IntentFilter(BakingAppUtils.ACTION_NETWORK_CHANGE));
    }

    @Override
    public void onStop() {
        mRequestQueue.cancelAll(this);
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.baking_app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, BakingAppSettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
