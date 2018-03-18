package com.sielski.marcin.bakingapp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

import com.sielski.marcin.bakingapp.R;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class BakingAppTestUtils {

    public static void selectRecipe(int position) {
        onView(isRoot()).perform(BakingAppTestUtils.waitFor(3000));
        onView(withId(R.id.recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    public static void pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    public static void selectStep(int position) {
        onView(isRoot()).perform(BakingAppTestUtils.waitFor(3000));
        onView(withId(R.id.recipe_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    public static boolean isTwoPane(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static boolean isHorizontal(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    // https://code.i-harness.com/en/q/146cfe2 to avoid using idling resources
    @SuppressWarnings("SameParameterValue")
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
