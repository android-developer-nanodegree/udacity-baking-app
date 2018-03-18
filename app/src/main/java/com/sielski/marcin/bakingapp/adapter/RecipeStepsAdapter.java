package com.sielski.marcin.bakingapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.activity.RecipeDetailActivity;
import com.sielski.marcin.bakingapp.fragment.RecipeDetailFragment;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder>{

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private final Recipe mRecipe;
    private final boolean mTwoPane;
    private final FragmentActivity mActivity;

    private TextView mTextView;
    private RecipeDetailFragment mRecipeDetailFragment;
    private boolean mShowFragment = true;
    private int mLayoutPosition = 0;
    private long mPlaybackPosition = 0;

    public RecipeStepsAdapter(FragmentActivity activity, Recipe recipe, boolean twoPane,
                              int position, long playbackPosition) {
        mActivity = activity;
        mRecipe = recipe;
        mTwoPane = twoPane;
        mLayoutPosition = position;
        mPlaybackPosition = playbackPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_card, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.step_short_description);
        Context context = holder.itemView.getContext();
        int layoutPosition = holder.getLayoutPosition();

        // Show always ingredients first
        if (layoutPosition == 0) {
            textView.setText(context.getString(R.string.text_ingredients));
        } else {
            textView.setText(mRecipe.steps.get(layoutPosition-1).shortDescription);
        }

        // If this is two pane view
        if (mTwoPane) {
            // Show fragment only once
            if (mShowFragment) {
                showDetailFragment();
                mShowFragment = false;
            }
            // If current view position is selected position change the color of the text
            if (layoutPosition == mLayoutPosition) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                mTextView = textView;
            }
        }

        // Setup view click listener
        textView.setOnClickListener((view) -> {
            if (BakingAppUtils.isNetworkAvailable(context)) {
                if (mTwoPane) {
                    // Clear the last selection
                    mTextView.setTextColor(ContextCompat.getColor(context,
                            android.R.color.secondary_text_dark));
                    // Setup new selection
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    mTextView = textView;

                    // Setup new position
                    mLayoutPosition = layoutPosition;
                    mPlaybackPosition = 0;

                    // Show selected fragment
                    showDetailFragment();
                } else {
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra(BakingAppUtils.KEY.POSITION, layoutPosition);
                    intent.putExtra(BakingAppUtils.KEY.RECIPE, mRecipe);
                    context.startActivity(intent);
                }
            } else {
                BakingAppUtils.showSnackBar(context, view,
                        context.getString(R.string.snackbar_network_unavailable));
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        // If selected view has been recycled change its text color
        if (holder.getLayoutPosition() == mLayoutPosition) {
            TextView textView = holder.itemView.findViewById(R.id.step_short_description);
            textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    android.R.color.secondary_text_dark));
        }
        super.onViewRecycled(holder);
    }

    private void showDetailFragment() {
        Fragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BakingAppUtils.KEY.RECIPE, mRecipe);
        bundle.putInt(BakingAppUtils.KEY.POSITION, mLayoutPosition);
        bundle.putLong(BakingAppUtils.KEY.PLAYBACK_POSITION, mPlaybackPosition);
        bundle.putBoolean(BakingAppUtils.KEY.PLAY_WHEN_READY, true);
        recipeDetailFragment.setArguments(bundle);
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().replace(R.id.recipe_detail_container,
                    recipeDetailFragment).commit();
            mRecipeDetailFragment = (RecipeDetailFragment) recipeDetailFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 1+mRecipe.steps.size();
    }

    public RecipeDetailFragment getCurrentFragment() {
        return mRecipeDetailFragment;
    }

    public int getPosition() {
        return mLayoutPosition;
    }

}
