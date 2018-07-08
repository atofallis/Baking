package com.tofallis.baking.ui.recipe_list;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.tofallis.baking.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class RecipeListActivityTest {

    @Rule
    public ActivityTestRule<RecipeListActivity> mRule = new ActivityTestRule<>(RecipeListActivity.class);

    @Before
    public void setUp() throws Exception {
        mRule.launchActivity(new Intent());
    }

    @Test
    public void recyclerViewIsPresent() {
        RecipeListActivity activity = mRule.getActivity();
        assertNotNull(activity.findViewById(R.id.rvRecipes));
    }
}