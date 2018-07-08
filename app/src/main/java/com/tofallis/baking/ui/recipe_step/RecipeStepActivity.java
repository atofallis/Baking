package com.tofallis.baking.ui.recipe_step;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tofallis.baking.R;

import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_NAME;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_POS;

public class RecipeStepActivity extends AppCompatActivity {

    private static final String TAG = RecipeStepActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        setContentView(R.layout.recipe_step);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_STEP_POS) && intent.hasExtra(EXTRA_RECIPE_ID)) {
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && actionBar.isShowing() && intent.hasExtra(EXTRA_RECIPE_NAME)) {
                actionBar.setTitle(intent.getStringExtra(EXTRA_RECIPE_NAME));
            }
            RecipeStepFragment stepFragment = RecipeStepFragment.newPhoneInstance(
                    intent.getIntExtra(EXTRA_RECIPE_ID, -1),
                    intent.getIntExtra(EXTRA_STEP_POS, -1));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_fragment, stepFragment)
                    .commit();
        }
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

    public static void startStepActivity(Context context, int recipeId, int nextStepPos) {
        Intent i = new Intent(context, RecipeStepActivity.class);
        i.putExtra(EXTRA_STEP_POS, nextStepPos);
        i.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startActivity(i);
    }
}
