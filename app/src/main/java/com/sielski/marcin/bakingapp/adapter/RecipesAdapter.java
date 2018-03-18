package com.sielski.marcin.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.RecipeStepsDetailActivity;
import com.sielski.marcin.bakingapp.RecipesActivity;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;
import com.sielski.marcin.bakingapp.util.GlideApp;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private final List<Recipe> mRecipes;
    private final RecipesActivity mRecipesActivity;

    public RecipesAdapter(RecipesActivity recipesActivity, List<Recipe> recipes) {
        mRecipesActivity = recipesActivity;
        mRecipes = recipes;
    }

    @NonNull
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {
        int layoutPosition = holder.getLayoutPosition();
        Recipe recipe = mRecipes.get(layoutPosition);
        Context context = holder.itemView.getContext();
        if (layoutPosition == 0 && BakingAppUtils.loadRecipe(context) == null) {
            BakingAppUtils.saveRecipe(context, recipe);
        }
        ((TextView)holder.itemView.findViewById(R.id.recipe_name)).setText(recipe.name);
        ((TextView)holder.itemView.findViewById(R.id.recipe_servings))
                .setText(String.format("%s %s", recipe.servings,
                        holder.itemView.getContext().getString(R.string.text_servings)));
        if (recipe.image == null || recipe.image.length() == 0) {

            mRecipesActivity.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                    BakingAppUtils.buildCSEQuery(context, recipe.name),
                    null, (response) -> {
                recipe.image = BakingAppUtils.getCSEImage(response);
                GlideApp.with(context.getApplicationContext())
                        .load(recipe.image)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into((ImageView) holder.itemView.findViewById(R.id.recipe_image));
            }, (error) ->
                GlideApp.with(context.getApplicationContext())
                        .load(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into((ImageView) holder.itemView.findViewById(R.id.recipe_image))
            ));
        } else {
            GlideApp.with(context.getApplicationContext())
                    .load(recipe.image)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into((ImageView) holder.itemView.findViewById(R.id.recipe_image));
        }
        holder.itemView.setOnClickListener((v) -> {
            // TODO: Remove line below if the the option is required
            BakingAppUtils.saveRecipe(context, recipe);
            Intent intent = new Intent(context, RecipeStepsDetailActivity.class);
            intent.putExtra(BakingAppUtils.KEY.RECIPE, recipe);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}
