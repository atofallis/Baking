package com.tofallis.baking.ui.recipe_detail;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_NAME;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

public class RecipeDetailActivityTest {

    @Rule
    public ActivityTestRule<RecipeDetailActivity> mRule = new ActivityTestRule<>(RecipeDetailActivity.class);

    private static final String RECIPE_NAME = "Secret Sauce";

    @Before
    public void setUp() {
        Intent i = new Intent();
        i.putExtra(EXTRA_RECIPE_ID, 1);
        i.putExtra(EXTRA_RECIPE_NAME, RECIPE_NAME);
        mRule.launchActivity(i);
    }

    @Test
    public void startActivitySetsActionBarTitle() {
        onView(allOf(instanceOf(TextView.class), withParent(withResourceName("action_bar"))))
                .check(matches(withText(RECIPE_NAME)));
    }
}