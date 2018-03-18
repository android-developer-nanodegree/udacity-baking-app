package com.sielski.marcin.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.RecipesActivity;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

public class BakingAppWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Recipe recipe = BakingAppUtils.loadRecipe(context);

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_baking_app);

        if (recipe == null) {
            remoteViews.setTextViewText(R.id.widget_title, context.getString(R.string.text_data));
        } else {
            remoteViews.setTextViewText(R.id.widget_title, recipe.name);

            Intent intent = new Intent(context, RecipesActivity.class);
            intent.putExtra(BakingAppUtils.KEY.RECIPE, recipe);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            remoteViews.setRemoteAdapter(R.id.widget_ingredients,
                    new Intent(context, BakingAppWidgetService.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_ingredients);
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void update(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int [] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, BakingAppWidget.class));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

