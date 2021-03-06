package com.tofallis.baking.ui.recipe_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tofallis.baking.R;
import com.tofallis.baking.ui.recipe_step.RecipeStepActivity;
import com.tofallis.baking.ui.recipe_step.RecipeStepFragment;

import butterknife.ButterKnife;

import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_NAME;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_STEP_POS;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener {

    private static final String TAG = RecipeDetailActivity.class.getName();

    private int mRecipeId;
    private boolean mTwoPane;
    private String mRecipeName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipe_list);
        ButterKnife.bind(this);
        mTwoPane = (findViewById(R.id.recipe_step_fragment) != null);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_RECIPE_ID) && intent.hasExtra(EXTRA_RECIPE_NAME)) {
            setupViewFromIntent(intent, savedInstanceState);
        }
    }

    // Required for 'up' navigation on Phone (from RecipeStepActivity)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        setupViewFromIntent(intent, null);
        RecipeDetailFragment fragment = (RecipeDetailFragment) getSupportFragmentManager().findFragmentByTag(RecipeDetailFragment.class.getSimpleName());
        fragment.refreshViews();
    }

    private void setupViewFromIntent(Intent intent, Bundle savedInstanceState) {
        mRecipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1);
        mRecipeName = intent.getStringExtra(EXTRA_RECIPE_NAME);
        int stepPos = intent.getIntExtra(EXTRA_STEP_POS, 0);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.isShowing()) {
            actionBar.setTitle(mRecipeName);
        }
        if (savedInstanceState == null) {
            RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(mRecipeId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_fragment, fragment, RecipeDetailFragment.class.getSimpleName())
                    .commit();
        } else {
            getSupportFragmentManager().findFragmentByTag(RecipeDetailFragment.class.getSimpleName());
        }

        if (mTwoPane) {
            // Prevent fragment from being unnecessarily replaced on rotation, allowing us to
            // properly retain video playback position
            if (savedInstanceState == null) {
                RecipeStepFragment stepFragment = RecipeStepFragment.newTabletInstance(mRecipeId, mRecipeName, stepPos);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_step_fragment, stepFragment, RecipeStepFragment.class.getSimpleName())
                        .commit();
            } else {
                getSupportFragmentManager().findFragmentByTag(RecipeStepFragment.class.getSimpleName());
            }
        }
    }

    @Override
    public void onRecipeStepClicked(int position) {
        if (mTwoPane) {
            RecipeStepFragment stepFragment = RecipeStepFragment.newTabletInstance(mRecipeId, mRecipeName, position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_fragment, stepFragment)
                    .commit();
        } else {
            RecipeStepActivity.startStepActivity(this, mRecipeId, mRecipeName, position);
        }
    }
}
