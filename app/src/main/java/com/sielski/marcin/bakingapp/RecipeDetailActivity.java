package com.sielski.marcin.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity {

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment) {
            if (fragment != null) {
                mFragments.add(fragment);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @NonNull
        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position);
        }
    }

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_viewpager)
    ViewPager mViewPager;

    private Recipe mRecipe;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mRecipe = intent.getParcelableExtra(BakingAppUtils.KEY.RECIPE);
        if (savedInstanceState == null) {
            mPosition = intent.getIntExtra(BakingAppUtils.KEY.POSITION, 0);
        } else {
            mPosition = savedInstanceState.getInt(BakingAppUtils.KEY.POSITION);
        }

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(String.format("%s %s", mRecipe.name,
                    getString(R.string.text_ingredients)));
        }

        Adapter adapter = new Adapter(getSupportFragmentManager());
        for (int i = 0; i < mRecipe.steps.size() + 1; i++) {
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(BakingAppUtils.KEY.RECIPE, mRecipe);
            bundle.putInt(BakingAppUtils.KEY.POSITION, i);
            if (i == mPosition) {
                bundle.putBoolean(BakingAppUtils.KEY.PLAY_WHEN_READY, true);
            }
            recipeDetailFragment.setArguments(bundle);
            adapter.addFragment(recipeDetailFragment);
        }
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    if (position == 0) {
                        actionBar.setTitle(String.format("%s %s", mRecipe.name,
                                getString(R.string.text_ingredients)));
                    } else {
                        actionBar.setTitle(mRecipe.steps.get(position - 1).shortDescription);
                    }
                }
                mPosition = position;
                Pause(mPosition - 1);
                Pause(mPosition + 1);
                Play(mPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(mPosition);
        BakingAppUtils.showSnackBar(this, mViewPager,
                getString(R.string.text_swipe));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Play(int position) {
        FragmentStatePagerAdapter fragmentPagerAdapter =
                (FragmentStatePagerAdapter) mViewPager.getAdapter();
        if (fragmentPagerAdapter != null && position > 0 &&
                position < fragmentPagerAdapter.getCount()) {
            ((RecipeDetailFragment) fragmentPagerAdapter.getItem(position)).Play();
        }
    }

    private void Pause(int position) {
        FragmentStatePagerAdapter fragmentPagerAdapter =
                (FragmentStatePagerAdapter) mViewPager.getAdapter();
        if (fragmentPagerAdapter != null && position > 0 &&
                position < fragmentPagerAdapter.getCount()) {
            ((RecipeDetailFragment) fragmentPagerAdapter.getItem(position)).Pause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BakingAppUtils.KEY.POSITION, mPosition);
    }

    public int getPosition() {
        return mViewPager.getCurrentItem();
    }


}
