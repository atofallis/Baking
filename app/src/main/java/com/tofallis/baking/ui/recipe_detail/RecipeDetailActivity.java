package com.tofallis.baking.ui.recipe_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.tofallis.baking.R;
import com.tofallis.baking.ui.recipe_step.RecipeStepFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_NAME;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final String TAG = RecipeDetailActivity.class.getName();

    @Nullable
    @BindView(R.id.recipe_step_fragment)
    ViewGroup mRecipeStepFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipe_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_RECIPE_ID) && intent.hasExtra(EXTRA_RECIPE_NAME)) {
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.setTitle(intent.getStringExtra(EXTRA_RECIPE_NAME));
            }
            RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(
                    intent.getIntExtra(EXTRA_RECIPE_ID, -1));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_fragment, fragment)
                    .commit();

            if (mRecipeStepFragment != null) {
                RecipeStepFragment stepFragment = new RecipeStepFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_step_fragment, stepFragment)
                        .commit();
            }
        }
    }
}
