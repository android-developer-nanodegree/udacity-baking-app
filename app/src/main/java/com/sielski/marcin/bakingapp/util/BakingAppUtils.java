package com.sielski.marcin.bakingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sielski.marcin.bakingapp.R;
import com.sielski.marcin.bakingapp.data.Ingredient;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.data.Step;
import com.sielski.marcin.bakingapp.widget.BakingAppWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
//import java.util.HashSet;
//import java.util.Set;

public final class BakingAppUtils {
    public final static String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    public final static String BAKING_APP_URL = "https://go.udacity.com/android-baking-app-json";

    public final static String REGEX = "[a-z0-9]+";

    public static final class KEY {
        public final static String RECIPE = "recipe";
        public final static String POSITION = "position";
        public final static String PLAYBACK_POSITION = "playback_position";
        public final static String PLAY_WHEN_READY = "play_when_ready";
        public final static String SHOW_WARNING = "show_warning";
    }

    public static final class EOL {
        public final static String SINGLE = "\n";
        public final static String DOUBLE = "\n\n";
    }

    private static final class CSE {
        private final static String URL = "https://www.googleapis.com/customsearch/v1";
        private final static String QUERY = "q";
        private final static String CX = "cx";
        private final static String ID = "011030891348742473955:y0zvqrkwm0y";
        private final static String NUMBER = "num";
        private final static String KEY = "key";
        private final static String SEARCH_TYPE = "searchType";
        private final static String IMAGE = "image";
        private final static String IMG_SIZE = "imgSize";
        private final static String LARGE = "large";
        private final static String ITEMS = "items";
        private final static String LINK = "link";
    }

    private static final class EMAIL {
        private final static String SCHEME = "mailto";
        private final static String SUBJECT = "subject";
        private final static String BODY = "body";
    }

    public static Uri buildEmailUri(String subject, String body) {
        return new Uri.Builder().scheme(EMAIL.SCHEME).
                appendQueryParameter(EMAIL.SUBJECT,
                        subject).appendQueryParameter(EMAIL.BODY, body).build();
    }


    public static int spanCount(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dp = (float)(displayMetrics.widthPixels) / displayMetrics.density;
        return (int)(dp / 360);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String buildCSEQuery(Context context, String query) {
        Uri.Builder builder = Uri.parse(CSE.URL).buildUpon();
        Uri uri = builder.appendQueryParameter(CSE.QUERY, query)
                .appendQueryParameter(CSE.NUMBER, "1")
                .appendQueryParameter(CSE.CX, CSE.ID)
                .appendQueryParameter(CSE.SEARCH_TYPE, CSE.IMAGE)
                .appendQueryParameter(CSE.IMG_SIZE, CSE.LARGE)
                .appendQueryParameter(CSE.KEY, getCSEApiKey(context)).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) return url.toString();
        return "";
    }

    public static String getCSEImage(JSONObject jsonObject) {

        String result = "";
        try {
            if (!jsonObject.has(CSE.ITEMS)) throw new JSONException(CSE.ITEMS);
            JSONArray jsonArray = jsonObject.getJSONArray(CSE.ITEMS);
            if (jsonArray.length() <= 0) throw new JSONException(CSE.ITEMS);
            jsonObject = jsonArray.getJSONObject(0);
            if (!jsonObject.has(CSE.LINK))  throw new JSONException(CSE.LINK);
            result = jsonObject.getString(CSE.LINK);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getCSEApiKey(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.key_cse_api_key),
                "");
    }

    public static Uri getVideoUri(Step step) {
        String videoURL;
        if (step.videoURL != null && step.videoURL.length() > 0) {
            videoURL = step.videoURL;
        } else {
            return null;
        }
        return Uri.parse(videoURL);
    }

    public static void showSnackBar(Context context, View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbar.show();
    }

    public static StringBuilder buildIngredient(Context context, Ingredient ingredient, String eol,
                                                int index, int size, StringBuilder stringBuilder) {
        String quantity;
        if (Math.round(ingredient.quantity) == ingredient.quantity) {
            quantity = String.valueOf((int) ingredient.quantity);
        } else {
            quantity = String.valueOf(ingredient.quantity);
        }
        stringBuilder.append(String.format("%s.   %s %s %s %s", String.valueOf(index), quantity,
                ingredient.measure, context.getString(R.string.text_of), ingredient.ingredient));
        if (index == size) {
            stringBuilder.append(".");
        } else {
            stringBuilder.append(",");
            stringBuilder.append(eol);
        }
        return stringBuilder;
    }

    public static StringBuilder buildIngredients(Context context, Recipe recipe, String eol,
                                                 StringBuilder stringBuilder) {
        if (recipe == null) return stringBuilder;
        int index = 0;
        for (Ingredient ingredient : recipe.ingredients) {
            stringBuilder = buildIngredient(context, ingredient, eol, ++index,
                    recipe.ingredients.size(), stringBuilder);
        }
        return stringBuilder;
    }

    @SuppressWarnings("SameParameterValue")
    public static StringBuilder buildRecipe(Recipe recipe, String eol,
                                            StringBuilder stringBuilder) {
        boolean first = true;
        for (Step step : recipe.steps) {
            stringBuilder.append(step.description);
            if (first) {
                stringBuilder.append(":\n");
                first = false;
            }
            stringBuilder.append(eol);
        }
        return stringBuilder;
    }

    public static void saveRecipe(Context context, Recipe mRecipe) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KEY.RECIPE, new Gson().toJson(mRecipe)).apply();
        BakingAppWidget.update(context);
    }

    public static Recipe loadRecipe(Context context) {
        String recipe = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY.RECIPE, "");
        if (recipe.isEmpty()) return null;
        return new Gson().fromJson(recipe, new TypeToken<Recipe>() {}.getType());
    }
}