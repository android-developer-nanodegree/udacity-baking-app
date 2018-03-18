package com.sielski.marcin.bakingapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
// TODO: Uncomment or remove if this option is required
// import android.view.Menu;
import android.view.MenuItem;

import com.sielski.marcin.bakingapp.adapter.RecipeStepsAdapter;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepsDetailActivity extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipe_steps)
    RecyclerView mRecyclerView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        ButterKnife.bind(this);
        if (mRecipe == null) {
            mRecipe = getIntent().getParcelableExtra(BakingAppUtils.KEY.RECIPE);
        }

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        floatingActionButton.setOnClickListener((view) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getString(R.string.text_ingredients));
            stringBuilder.append(":");
            stringBuilder.append(BakingAppUtils.EOL.DOUBLE);
            stringBuilder = BakingAppUtils.buildIngredients(this,
                    mRecipe, BakingAppUtils.EOL.SINGLE, stringBuilder);
            stringBuilder.append(BakingAppUtils.EOL.DOUBLE);

            Intent intent = new Intent(Intent.ACTION_SENDTO,
                    BakingAppUtils.buildEmailUri(mRecipe.name,
                            BakingAppUtils.buildRecipe(mRecipe, BakingAppUtils.EOL.SINGLE,
                                    stringBuilder).toString()));
            startActivity(intent);
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mRecipe.name);
        }

        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
        }

        mRecyclerView.setAdapter(new RecipeStepsAdapter(this, mRecipe, mTwoPane));
    }
/* TODO: Uncomment or remove if this option is required
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
/* TODO: Uncomment or remove if this option is required
            case R.id.action_widget:
                BakingAppUtils.saveRecipe(this, mRecipe);
                return true;
                */
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        RecipeDetailFragment recipeDetailFragment =
                ((RecipeStepsAdapter)mRecyclerView.getAdapter()).getCurrentFragment();
        if (mTwoPane && recipeDetailFragment != null) {
            recipeDetailFragment.Pause();
        }
        super.onPause();
    }

}
