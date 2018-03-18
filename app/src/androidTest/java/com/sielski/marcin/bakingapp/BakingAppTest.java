package com.sielski.marcin.bakingapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sielski.marcin.bakingapp.util.BakingAppTestUtils;
import com.sielski.marcin.bakingapp.util.BakingAppUtils;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BakingAppTest {
    @Rule public final ActivityTestRule<RecipesActivity> mActivityTestRule =
            new ActivityTestRule<>(RecipesActivity.class);

   @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.sielski.marcin.bakingapp", appContext.getPackageName());
    }

    @Test
    public void clickRecipe_opensRecipeStepsDetailActivity() {
        for (int position = 0; position < 4; position ++) {
            Intents.init();
            BakingAppTestUtils.selectRecipe(position);
            intended(hasComponent(RecipeStepsDetailActivity.class.getName()));
            intended(hasExtraWithKey(BakingAppUtils.KEY.RECIPE));
            Intents.release();
            onView(withId(R.id.recipe_steps)).check(matches(isCompletelyDisplayed()));
            BakingAppTestUtils.pressBack();
        }
    }

    @Test
    public void clickStep_opensRecipeDetailActivity_orRecipeDetailFragment() {
        BakingAppTestUtils.selectRecipe(0);
        for (int position = 0; position < 8; position++) {
            Intents.init();
            BakingAppTestUtils.selectStep(position);
            if (BakingAppTestUtils.isTwoPane(mActivityTestRule.getActivity())) {
                onView(isRoot()).perform(BakingAppTestUtils.waitFor(2000));
                if (position == 1) {
                    onView(withId(R.id.recipe_step_video)).check(matches(isCompletelyDisplayed()));
                } else {
                    onView(withId(R.id.recipe_step_description))
                            .check(matches(isDisplayed()));
                }
                if (position == 6) {
                    onView(withId(R.id.recipe_step_image)).check(matches(isCompletelyDisplayed()));
                }
            } else {
                intended(hasComponent(RecipeDetailActivity.class.getName()));
                intended(hasExtraWithKey(BakingAppUtils.KEY.RECIPE));
                intended(hasExtra(BakingAppUtils.KEY.POSITION, position));
                onView(isRoot()).perform(BakingAppTestUtils.waitFor(2000));
                if (BakingAppTestUtils.isHorizontal(mActivityTestRule.getActivity()) &&
                        (position != 0) && (position != 2) && (position != 6)) {
                    onView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                            withId(R.id.recipe_step_video)))
                            .check(matches(isCompletelyDisplayed()));
                    BakingAppTestUtils.pressBack();
                }
                onView(withId(R.id.detail_viewpager)).check(matches(isCompletelyDisplayed()));
                for (int i = position; i < 7; i++) {
                    onView(withId(R.id.detail_viewpager)).perform(swipeLeft());
                    onView(isRoot()).perform(BakingAppTestUtils.waitFor(2000));
                    if (i == 0) {
                        onView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                                withId(R.id.recipe_step_video)))
                                .check(matches(isCompletelyDisplayed()));
                    }
                    if (i == 5) {
                        onView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                                withId(R.id.recipe_step_image)))
                                .check(matches(isCompletelyDisplayed()));
                    }
                }
                for (int i = 5; i >= position; i--) {
                    onView(withId(R.id.detail_viewpager)).perform(swipeRight());
                    onView(isRoot()).perform(BakingAppTestUtils.waitFor(2000));
                    if (i == 0) {
                        onView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                                withId(R.id.recipe_step_video)))
                                .check(matches(isCompletelyDisplayed()));
                    }
                    if (i == 5) {
                        onView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                                withId(R.id.recipe_step_image)))
                                .check(matches(isCompletelyDisplayed()));
                    }
                }
                BakingAppTestUtils.pressBack();
            }
            Intents.release();
        }
    }

    @After
    public void after() {
       try {
           Intents.release();
       } catch (Exception e) {
            e.printStackTrace();
       }
    }

    @Test
    public void clickRecipe_savesRecipeForWidget() {
        mActivityTestRule.getActivity().getSharedPreferences(BakingAppUtils.KEY.RECIPE,
                Context.MODE_PRIVATE).edit().clear().commit();
        BakingAppTestUtils.selectRecipe(1);
        assertNotNull(BakingAppUtils.loadRecipe(mActivityTestRule.getActivity()));
    }


}
