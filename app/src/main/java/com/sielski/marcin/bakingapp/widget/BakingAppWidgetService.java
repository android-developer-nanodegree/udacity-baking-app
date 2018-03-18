package com.sielski.marcin.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

public class BakingAppWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            Recipe mRecipe;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                mRecipe = BakingAppUtils.loadRecipe(getApplicationContext());

            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return mRecipe.ingredients.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.widget_baking_app_ingredients);
                remoteViews.setTextViewText(R.id.widget_ingredient,
                        BakingAppUtils.buildIngredient(getApplicationContext(),
                                mRecipe.ingredients.get(position), BakingAppUtils.EOL.SINGLE,
                                position + 1, getCount(), new StringBuilder()).toString());
                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

