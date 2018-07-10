package com.tofallis.baking.ui.recipe_step;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.tofallis.baking.R;
import com.tofallis.baking.ui.RecipeConstants;

import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_NAME;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_POS;

public class RecipeStepActivity extends AppCompatActivity {

    private static final String TAG = RecipeStepActivity.class.getName();

    private int mRecipeId;
    private String mRecipeName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        setContentView(R.layout.recipe_step);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_STEP_POS) && intent.hasExtra(EXTRA_RECIPE_ID)) {
            mRecipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1);
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && actionBar.isShowing() && intent.hasExtra(EXTRA_RECIPE_NAME)) {
                mRecipeName = intent.getStringExtra(EXTRA_RECIPE_NAME);
                actionBar.setTitle(mRecipeName);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            // Prevent fragment from being unnecessarily replaced on rotation, allowing us to
            // properly retain video playback position
            if (savedInstanceState == null) {
                RecipeStepFragment stepFragment = RecipeStepFragment.newPhoneInstance(
                        mRecipeId,
                        mRecipeName,
                        intent.getIntExtra(EXTRA_STEP_POS, -1));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_step_fragment, stepFragment, RecipeStepFragment.class.getSimpleName())
                        .commit();
            } else {
                getSupportFragmentManager().findFragmentByTag(RecipeStepFragment.class.getSimpleName());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra(RecipeConstants.EXTRA_RECIPE_ID, mRecipeId);
                intent.putExtra(RecipeConstants.EXTRA_RECIPE_NAME, mRecipeName);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Required for 'up' navigation from Phone (back to RecipeDetailActivity)
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // hide the action and status bar on phone in landscape mode
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    public static void startStepActivity(Context context, int recipeId, String recipeName, int nextStepPos) {
        Intent i = new Intent(context, RecipeStepActivity.class);
        i.putExtra(EXTRA_STEP_POS, nextStepPos);
        i.putExtra(EXTRA_RECIPE_ID, recipeId);
        i.putExtra(EXTRA_RECIPE_NAME, recipeName);
        context.startActivity(i);
    }
}
