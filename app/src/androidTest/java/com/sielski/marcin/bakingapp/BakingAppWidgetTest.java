package com.sielski.marcin.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.sielski.marcin.bakingapp.activity.RecipesActivity;
import com.sielski.marcin.bakingapp.data.Recipe;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class BakingAppWidgetTest {
    private Recipe mRecipe;

    @Rule
    public ActivityTestRule<RecipesActivity> mActivityTestRule =
            new ActivityTestRule<RecipesActivity>(RecipesActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context context =
                            InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent intent = new Intent(context, RecipesActivity.class);
                    mRecipe = BakingAppUtils.loadRecipe(context);
                    intent.putExtra(BakingAppUtils.KEY.RECIPE, mRecipe);
                    return intent;
                }
            };

    @Test
    public void clickWidget_openRecipeStepsDetailActivity() {
        onView(withId(R.id.recipe_steps)).check(matches(isCompletelyDisplayed()));
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(mRecipe.name)));
    }
}
